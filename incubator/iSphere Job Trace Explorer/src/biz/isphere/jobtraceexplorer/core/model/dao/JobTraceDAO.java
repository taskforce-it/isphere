/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.jobtraceexplorer.core.ISphereJobTraceExplorerCorePlugin;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntries;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;
import biz.isphere.jobtraceexplorer.core.model.JobTraceSession;
import biz.isphere.jobtraceexplorer.core.model.api.IBMiMessage;

import com.ibm.as400.access.AS400;

/**
 * This class retrieves journal entries from the journal a given object is
 * associated to.
 */
public class JobTraceDAO {

    /**
     * SQL statement for querying the job trace session data. Tables and their
     * descriptions:
     * <p>
     * QAYPETIDX - PEX TRACE INDEX DATA<br>
     * Main table, that stores the recorded program steps.
     * <p>
     * QAYPETBRKT - PEX TRACE JOB STYLE BRACKETING EVENT<br>
     * Table, that stores the statement and caller statement numbers and other
     * statistic data. Also contains the procedure traceback table addresses of
     * the caller and callee. The traceback addresses are used to join QAYPETIDX
     * with QAYPEPROCI.
     * <p>
     * QAYPEPROCI - PEX PROC RESOLUTION DATA<br>
     * Table, that stores the program and procedure names.
     * <p>
     * QAYPEEVENT - PEX EVENT MAPPING DATA<br>
     * Table, that stores the event types. This table is used to retrieve the
     * relation between the caller and the callee.
     * <p>
     * 
     * @see biz.isphere.jobtraceexplorer.core.model.dao.JobTraceDAO
     */

    // @formatter:off
    private static final String SQL_STATEMENT = 
        "SELECT x.QTITIMN      as \"NANOS_SINE_STARTED\"  , " +
               "x.QTITSP       as \"TIMESTAMP\"           , " +
               "i.QPRPGN       as \"PGM_NAME\"            , " +
               "i.QPRPQL       as \"PGM_LIB\"             , " +
               "i.QPRMNM       as \"MODULE_NAME\"         , " +
               "i.QPRMQL       as \"MODULE_LIBRARY\"      , " +
               "t.QTBHLL       as \"HLL_STMT_NBR\"        , " +
               "i.QPRPNM       as \"PROC_NAME\"           , " +
               "t.QTBCLL       as \"CALL_LEVEL\"          , " +
               "v.QEVSSN       as \"EVENT_SUB_TYPE\"      , " +
               "t.QTBCHL       as \"CALLER_HLL_STMT_NBR\" , " +
               "ci.QPRPNM      as \"CALLER_PROC_NAME\"      " + 
        "FROM QAYPETIDX x "                                   +
        "LEFT JOIN QAYPETBRKT t on x.QRECN = t.QRECN "        +
        "LEFT JOIN QAYPEPROCI i on i.QPRKEY = t.QTBTBT "      +
        "LEFT JOIN QAYPEEVENT v on x.QTITY  =  v.QEVTY AND "  +
                                  "x.QTISTY  =  v.QEVSTY "    +
        "LEFT JOIN QAYPEPROCI ci on ci.QPRKEY = t.QTBCTB ";

    private static final String SQL_WHERE_NO_IBM_DATA = 
        "WHERE i.QPRPQL not in ('QSYS', 'QTCP', 'QPDA') "     +
        "AND i.QPRPQL not like 'QXMLLIB%' "                 +
        "AND i.QPRPNM not in ('_CL_PEP') "                  +
        "AND i.QPRPNM not like '_QRN%' ";

    private static final String SQL_ORDER_BY =
        "ORDER BY x.QTITIMN";

    private static final String[] OVRDBF_CMD =
      { "OVRDBF FILE(%S/QAYPETIDX) TOFILE(*FILE) MBR(%S) SECURE(*YES) OVRSCOPE(*JOB)" ,
        "OVRDBF FILE(%S/QAYPETBRKT) TOFILE(*FILE) MBR(%S) SECURE(*YES) OVRSCOPE(*JOB)" ,
        "OVRDBF FILE(%S/QAYPEPROCI) TOFILE(*FILE) MBR(%S) SECURE(*YES) OVRSCOPE(*JOB)" ,
        "OVRDBF FILE(%S/QAYPEEVENT) TOFILE(*FILE) MBR(%S) SECURE(*YES) OVRSCOPE(*JOB)" };

    private static final String[] DLTOVR_CMD =
      { "DLTOVR FILE(QAYPETIDX) LVL(*JOB)" ,
        "DLTOVR FILE(QAYPETBRKT) LVL(*JOB)" ,
        "DLTOVR FILE(QAYPEPROCI) LVL(*JOB)" ,
        "DLTOVR FILE(QAYPEEVENT) LVL(*JOB)" };
         
    // @formatter:on

    private JobTraceSession jobTraceSession;

    public JobTraceDAO(JobTraceSession jobTraceSession) throws Exception {

        this.jobTraceSession = jobTraceSession;
    }

    public JobTraceEntries load(String whereClause, IProgressMonitor monitor) {

        int maxNumRows = 17; // Preferences.getInstance().getMaximumNumberOfRowsToFetch();

        JobTraceEntries jobTraceEntries = new JobTraceEntries(maxNumRows);

        List<IBMiMessage> messages = null;
        int id = 0;

        AS400 system = null;
        Connection jdbcConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        boolean isTableOverWrite = false;
        boolean isDataOverflow = false;

        try {

            system = IBMiHostContributionsHandler.getSystem(jobTraceSession.getConnectionName());

            isTableOverWrite = overWriteTables(system, jobTraceSession);
            if (isTableOverWrite) {

                jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(jobTraceSession.getConnectionName());
                preparedStatement = jdbcConnection.prepareStatement(getSQLStatement(whereClause));
                resultSet = preparedStatement.executeQuery();

                Date startTime = new Date();

                while (resultSet.next() && !isDataOverflow && !isCanceled(monitor, jobTraceEntries)) {

                    if (jobTraceEntries.getNumberOfRowsDownloaded() < maxNumRows) {

                        id++;

                        JobTraceEntry jobTraceEntry = new JobTraceEntry(jobTraceSession);

                        JobTraceEntry populatedJobTraceEntry = populateJobTraceEntry(resultSet, jobTraceEntry);
                        jobTraceEntries.add(populatedJobTraceEntry);

                    } else {
                        isDataOverflow = true;
                    }
                }
            }

        } catch (Throwable e) {
            ISphereJobTraceExplorerCorePlugin.logError("*** Could not load trace data " + jobTraceSession.toString() + " ***", e);
        } finally {
            if (isTableOverWrite) {
                deleteTableOverWrites(system);
            }
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                }
            }
        }

        // System.out.println("mSecs total: " + timeElapsed(startTime) +
        // ", WHERE-CLAUSE: " + whereClause);

        if (isDataOverflow) {
            jobTraceEntries.setOverflow(true, -1);
        }

        jobTraceEntries.setMessages(messages);

        return jobTraceEntries;
    }

    private boolean overWriteTables(AS400 system, JobTraceSession jobTraceSession) {

        for (String ovrDbfCmd : OVRDBF_CMD) {
            String cmd = String.format(ovrDbfCmd, jobTraceSession.getLibraryName(), jobTraceSession.getSessionID());
            try {
                ISphereHelper.executeCommand(system, cmd);
            } catch (Exception e) {
                ISphereJobTraceExplorerCorePlugin.logError("*** Could not overwrite job trace tables " + jobTraceSession.toString() + " ***", e);
                return false;
            }
        }

        return true;
    }

    private void deleteTableOverWrites(AS400 system) {

        for (String dltOvrCmd : DLTOVR_CMD) {
            try {
                ISphereHelper.executeCommand(system, dltOvrCmd);
            } catch (Exception e) {
                // Ignore error messages
            }
        }
    }

    private String getSQLStatement(String whereClause) {

        StringBuilder buffer = new StringBuilder();

        buffer.append(SQL_STATEMENT);
        buffer.append(SQL_WHERE_NO_IBM_DATA);
        buffer.append(SQL_ORDER_BY);

        return buffer.toString();
    }

    private long timeElapsed(Date startTime) {
        return (new Date().getTime() - startTime.getTime());
    }

    private boolean isCanceled(IProgressMonitor monitor, JobTraceEntries jobTraceEntries) {
        if (monitor.isCanceled()) {
            jobTraceEntries.setCanceled(true);
            return true;
        }
        return false;
    }

    private JobTraceEntry populateJobTraceEntry(ResultSet resultSet, JobTraceEntry jobTraceEntry) throws Exception {

        // AbstractTypeDAO
        // journalEntry.setConnectionName(connectionName);
        jobTraceEntry.setNanosSinceStarted(resultSet.getBigDecimal(ColumnsDAO.NANOS_SINE_STARTED.index()).toBigIntegerExact());
        jobTraceEntry.setTimestamp(resultSet.getTimestamp(ColumnsDAO.TIMESTAMP.index()));
        jobTraceEntry.setProgramName(resultSet.getString(ColumnsDAO.PGM_NAME.index()));
        jobTraceEntry.setProgramLibrary(resultSet.getString(ColumnsDAO.PGM_LIB.index()));
        jobTraceEntry.setModuleName(resultSet.getString(ColumnsDAO.MODULE_NAME.index()));
        jobTraceEntry.setHLLStmtNbr(resultSet.getInt(ColumnsDAO.HLL_STMT_NBR.index()));
        jobTraceEntry.setProcedureName(resultSet.getString(ColumnsDAO.PROC_NAME.index()));
        jobTraceEntry.setCallLevel(resultSet.getInt(ColumnsDAO.CALL_LEVEL.index()));
        jobTraceEntry.setEventSubType(resultSet.getString(ColumnsDAO.EVENT_SUB_TYPE.index()));
        jobTraceEntry.setCallerHLLStmtNbr(resultSet.getInt(ColumnsDAO.CALLER_HLL_STMT_NBR.index()));
        jobTraceEntry.setCallerProcedureName(resultSet.getString(ColumnsDAO.CALLER_PROC_NAME.index()));

        return jobTraceEntry;
    }
}
