/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
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

import biz.isphere.base.internal.SqlHelper;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.jobtraceexplorer.core.ISphereJobTraceExplorerCorePlugin;
import biz.isphere.jobtraceexplorer.core.Messages;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntries;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;
import biz.isphere.jobtraceexplorer.core.model.JobTraceSession;
import biz.isphere.jobtraceexplorer.core.model.api.IBMiMessage;
import biz.isphere.jobtraceexplorer.core.preferences.Preferences;

/**
 * This class retrieves journal entries from the journal a given object is
 * associated to.
 */
public class JobTraceSQLDAO {

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
     * @see biz.isphere.jobtraceexplorer.core.model.dao.JobTraceSQLDAO
     */

    // @formatter:off
    
    
    private static final String SQL_FROM_CLAUSE = 
        "FROM QAYPETIDX x "                                   +
        "LEFT JOIN QAYPETBRKT t on x.QRECN = t.QRECN "        +
        "LEFT JOIN QAYPEPROCI i on i.QPRKEY = t.QTBTBT "      +
        "LEFT JOIN QAYPEEVENT v on x.QTITY  =  v.QEVTY AND "  +
                                  "x.QTISTY  =  v.QEVSTY "    +
        "LEFT JOIN QAYPEPROCI ci on ci.QPRKEY = t.QTBCTB ";

    
    private static final String SQL_COUNT_STATEMENT = 
        "SELECT COUNT(x.QTITIMN) " +
         SQL_FROM_CLAUSE;      
        
    private static final String SQL_STATEMENT = 
        "SELECT 0              as \"ID\"                  , " +
               "x.QTITIMN      as \"NANOS_SINE_STARTED\"  , " +
               "x.QTITSP       as \"TIMESTAMP\"           , " +
               "i.QPRPGN       as \"PGM_NAME\"            , " +
               "i.QPRPQL       as \"PGM_LIB\"             , " +
               "i.QPRMNM       as \"MOD_NAME\"            , " +
               "i.QPRMQL       as \"MOD_LIB\"             , " +
               "t.QTBHLL       as \"HLL_STMT_NBR\"        , " +
               "i.QPRPNM       as \"PROC_NAME\"           , " +
               "t.QTBCLL       as \"CALL_LEVEL\"          , " +
               "v.QEVSSN       as \"EVENT_SUB_TYPE\"      , " +
               "t.QTBCHL       as \"CALLER_HLL_STMT_NBR\" , " +
               "ci.QPRPNM      as \"CALLER_PROC_NAME\"      " +
         SQL_FROM_CLAUSE;      

    public static final String SQL_WHERE_NO_IBM_DATA = 
        "WHERE i.QPRPQL not in ('QSYS', 'QTCP', 'QPDA') "     +                     /* Exclude IBM Libraries */
        "AND i.QPRPQL  not like 'QXMLLIB%' "                  +                     /* Exclude IBM Xerces Parser */
        "AND (t.QTBCHL > 0 or ( t.QTBCHL = 0 and i.QPRPNM not like '_QRNI_%' )) " + /* Exclude RPG Entry Point Procedures */ 
        "AND i.QPRPNM not in ('*ccsidConvProc') ";                                  /* Exclude Special RPG Procedures */ 

    private static final String SQL_ORDER_BY =
        "ORDER BY x.QTITIMN";

    private static final String[] OVRDBF_CMD =
      { "OVRDBF FILE(QAYPETIDX)  TOFILE(%S/QAYPETIDX ) MBR(%S) SECURE(*YES) OVRSCOPE(*JOB)" ,
        "OVRDBF FILE(QAYPETBRKT) TOFILE(%S/QAYPETBRKT) MBR(%S) SECURE(*YES) OVRSCOPE(*JOB)" ,
        "OVRDBF FILE(QAYPEPROCI) TOFILE(%S/QAYPEPROCI) MBR(%S) SECURE(*YES) OVRSCOPE(*JOB)" ,
        "OVRDBF FILE(QAYPEEVENT) TOFILE(%S/QAYPEEVENT) MBR(%S) SECURE(*YES) OVRSCOPE(*JOB)" };

    private static final String[] DLTOVR_CMD =
      { "DLTOVR FILE(QAYPETIDX) LVL(*JOB)" ,
        "DLTOVR FILE(QAYPETBRKT) LVL(*JOB)" ,
        "DLTOVR FILE(QAYPEPROCI) LVL(*JOB)" ,
        "DLTOVR FILE(QAYPEEVENT) LVL(*JOB)" };
         
    // @formatter:on

    private JobTraceSession jobTraceSession;
    private String sqlWhereNoIBMData;

    public JobTraceSQLDAO(JobTraceSession jobTraceSession) throws Exception {

        this.jobTraceSession = jobTraceSession;

        this.sqlWhereNoIBMData = Preferences.getInstance().getExcludeIBMDataSQLWhereClause();
    }

    public JobTraceSession load(IProgressMonitor monitor) throws Exception {

        List<IBMiMessage> messages = null;

        Connection jdbcConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        SqlHelper sqlHelper = null;

        boolean isTableOverWrite = false;
        boolean isDataOverflow = false;

        Date startTime = new Date();

        try {

            monitor.setTaskName(Messages.Status_Preparing_to_load_job_trace_entries);

            jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(jobTraceSession.getConnectionName());

            sqlHelper = new SqlHelper(jdbcConnection);
            isTableOverWrite = overWriteTables(sqlHelper, jobTraceSession);

            int maxNumRows = Preferences.getInstance().getMaximumNumberOfRowsToFetch();

            int numRowsAvailable = getNumRowsAvailable(sqlHelper, jobTraceSession.getWhereClause());

            preparedStatement = jdbcConnection.prepareStatement(getSQLStatement(jobTraceSession.getWhereClause()));

            monitor.setTaskName(Messages.Status_Executing_query);

            resultSet = preparedStatement.executeQuery();

            monitor.beginTask(Messages.Status_Receiving_job_trace_entries, numRowsAvailable);

            JobTraceEntries jobTraceEntries = jobTraceSession.getJobTraceEntries();

            while (resultSet.next() && !isDataOverflow && !isCanceled(monitor, jobTraceEntries)) {

                monitor.worked(1);

                if (jobTraceEntries.getNumberOfRowsDownloaded() < maxNumRows) {

                    JobTraceEntry jobTraceEntry = new JobTraceEntry();

                    JobTraceEntry populatedJobTraceEntry = populateJobTraceEntry(resultSet, jobTraceEntry);
                    jobTraceEntries.add(populatedJobTraceEntry);

                } else {
                    isDataOverflow = true;
                }
            }

        } finally {

            monitor.done();

            if (isTableOverWrite) {
                deleteTableOverWrites(sqlHelper);
            }

            sqlHelper.close(resultSet);
            sqlHelper.close(preparedStatement);
        }

        ISphereJobTraceExplorerCorePlugin.debug("mSecs total: " + timeElapsed(startTime) + ", WHERE-CLAUSE: " + jobTraceSession.getWhereClause()); //$NON-NLS-1$ //$NON-NLS-2$

        if (isDataOverflow) {
            jobTraceSession.getJobTraceEntries().setOverflow(true, -1);
        }

        jobTraceSession.getJobTraceEntries().setMessages(messages);

        return jobTraceSession;
    }

    private boolean overWriteTables(SqlHelper sqlHelper, JobTraceSession jobTraceSession) {

        for (String ovrDbfCmd : OVRDBF_CMD) {
            ovrDbfCmd = String.format(ovrDbfCmd, jobTraceSession.getLibraryName(), jobTraceSession.getSessionID());
            try {
                sqlHelper.executeSystemCommand(ovrDbfCmd);
            } catch (Exception e) {
                ISphereJobTraceExplorerCorePlugin.logError("*** Could not overwrite job trace tables " + jobTraceSession.toString() + " ***", e);
                return false;
            }
        }

        return true;
    }

    private void deleteTableOverWrites(SqlHelper sqlHelper) {

        for (String dltOvrCmd : DLTOVR_CMD) {
            sqlHelper.executeSystemCommandChecked(dltOvrCmd);
        }
    }

    private String getSQLStatement(String whereClause) {

        StringBuilder buffer = new StringBuilder();

        buffer.append(SQL_STATEMENT);

        if (jobTraceSession.isIBMDataExcluded()) {
            buffer.append(sqlWhereNoIBMData);
        }

        buffer.append(SQL_ORDER_BY);

        return buffer.toString();
    }

    private String getSQLCountStatement(String whereClause) {

        StringBuilder buffer = new StringBuilder();

        buffer.append(SQL_COUNT_STATEMENT);

        if (jobTraceSession.isIBMDataExcluded()) {
            buffer.append(sqlWhereNoIBMData);
        }

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

    private int getNumRowsAvailable(SqlHelper sqlHelper, String whereClause) throws SQLException {

        int numRowsAvailable = 0;

        String sqlCountStatement = getSQLCountStatement(whereClause);
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            Connection jdbcConnection = sqlHelper.getConnection();
            preparedStatement = jdbcConnection.prepareStatement(sqlCountStatement);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                numRowsAvailable = resultSet.getInt(1);
            } else {
                numRowsAvailable = Integer.MAX_VALUE;
            }

        } finally {
            try {
                sqlHelper.close(preparedStatement);
                sqlHelper.close(resultSet);
            } catch (Throwable e) {
            }
        }

        return numRowsAvailable;
    }

    private JobTraceEntry populateJobTraceEntry(ResultSet resultSet, JobTraceEntry jobTraceEntry) throws Exception {

        // AbstractTypeDAO
        // journalEntry.setConnectionName(connectionName);
        jobTraceEntry.setNanosSinceStarted(resultSet.getBigDecimal(ColumnsDAO.NANOS_SINE_STARTED.index()).toBigIntegerExact());
        jobTraceEntry.setTimestamp(resultSet.getTimestamp(ColumnsDAO.TIMESTAMP.index()));
        jobTraceEntry.setProgramName(resultSet.getString(ColumnsDAO.PGM_NAME.index()));
        jobTraceEntry.setProgramLibrary(resultSet.getString(ColumnsDAO.PGM_LIB.index()));
        jobTraceEntry.setModuleName(resultSet.getString(ColumnsDAO.MOD_NAME.index()));
        jobTraceEntry.setModuleLibrary(resultSet.getString(ColumnsDAO.MOD_LIB.index()));
        jobTraceEntry.setHLLStmtNbr(resultSet.getInt(ColumnsDAO.HLL_STMT_NBR.index()));
        jobTraceEntry.setProcedureName(resultSet.getString(ColumnsDAO.PROC_NAME.index()));
        jobTraceEntry.setCallLevel(resultSet.getInt(ColumnsDAO.CALL_LEVEL.index()));
        jobTraceEntry.setEventSubType(resultSet.getString(ColumnsDAO.EVENT_SUB_TYPE.index()));
        jobTraceEntry.setCallerHLLStmtNbr(resultSet.getInt(ColumnsDAO.CALLER_HLL_STMT_NBR.index()));
        jobTraceEntry.setCallerProcedureName(resultSet.getString(ColumnsDAO.CALLER_PROC_NAME.index()));

        if (ColumnsDAO.EVENT_SUB_TYPE_PRCENTRY.equals(jobTraceEntry.getEventSubType())) {
            jobTraceEntry.setCallLevel(resultSet.getInt(ColumnsDAO.CALL_LEVEL.index()));
            jobTraceEntry.setCallerCallLevel(jobTraceEntry.getCallLevel() - 1);
        } else {
            jobTraceEntry.setCallerCallLevel(resultSet.getInt(ColumnsDAO.CALL_LEVEL.index()));
            jobTraceEntry.setCallLevel(jobTraceEntry.getCallLevel() + 1);
        }

        return jobTraceEntry;
    }
}
