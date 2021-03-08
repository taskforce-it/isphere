/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.JournalEntries;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.OutputFile;
import biz.isphere.journalexplorer.core.model.SQLWhereClause;
import biz.isphere.journalexplorer.core.preferences.Preferences;

public abstract class AbstractTypeDAO extends DAOBase {

    /*
     * Restrict the number of bytes returned. JOENTL is more than the record
     * length of the journal file but usually less the actual size of JOESD.
     * That is close enough for now.
     */
    protected static final String SQL_JOESD_RESULT = "SUBSTR(JOESD, 1, CAST(JOENTL AS INTEGER)) AS JOESD"; //$NON-NLS-1$

    private OutputFile outputFile;

    public AbstractTypeDAO(OutputFile outputFile) throws Exception {
        super(outputFile.getConnectionName());

        this.outputFile = outputFile;
    }

    public JournalEntries load(SQLWhereClause whereClause, IProgressMonitor monitor) throws Exception {

        int maxNumRows = Preferences.getInstance().getMaximumNumberOfRowsToFetch();

        JournalEntries journalEntries = new JournalEntries(maxNumRows);

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Statement statement = null;

        try {

            Date startTime = new Date();

            statement = createStatement();
            overwriteDatabaseFile(statement, outputFile.getFileName(), outputFile.getLibraryName(), outputFile.getFileName(),
                outputFile.getMemberName());

            String sqlStatement = String.format(getSqlStatement(), outputFile.getLibraryName(), outputFile.getFileName());
            if (whereClause.hasClause()) {
                sqlStatement = sqlStatement + " WHERE " + whereClause.getClause(); //$NON-NLS-1$
            }

            int numRowsAvailable = getNumRowsAvailable(whereClause);

            monitor.beginTask(Messages.Status_Loading_journal_entries, numRowsAvailable);

            preparedStatement = prepareStatement(sqlStatement);
            resultSet = preparedStatement.executeQuery();
            resultSet.setFetchSize(50);

            if (resultSet != null) {

                JournalEntry journalEntry = null;

                while (resultSet.next() && !isCanceled(monitor, journalEntries)) {

                    monitor.worked(1);

                    if (journalEntries.getNumberOfRowsDownloaded() >= maxNumRows) {
                        handleOverflowError(journalEntries, numRowsAvailable);
                        break;
                    } else {
                        journalEntry = new JournalEntry(outputFile);
                        journalEntries.add(populateJournalEntry(resultSet, journalEntry));

                        if (journalEntry.isRecordEntryType()) {
                            MetaDataCache.getInstance().prepareMetaData(journalEntry);
                        }
                    }
                }
            }

            // System.out.println("mSecs total: " + timeElapsed(startTime) +
            // ", WHERE-CLAUSE: " + whereClause);

        } finally {

            try {
                deleteDatabaseOverwrite(statement, outputFile.getFileName());
            } catch (Throwable e) {
                // Ignore error. It has already been logged.
            }

            super.destroy(preparedStatement);
            super.destroy(resultSet);

            monitor.done();
        }

        return journalEntries;
    }

    private boolean isCanceled(IProgressMonitor monitor, JournalEntries journalEntries) {
        if (monitor.isCanceled()) {
            journalEntries.setCanceled(true);
            return true;
        }
        return false;
    }

    /**
     * Executes a OVRDBF statement likes this:
     * 
     * <pre>
     * OVRDBF FILE(OVRFILE) TOFILE(LIBRARY/FILE) MBR(MEMBER) OVRSCOPE(*JOB)
     * </pre>
     * 
     * @param statement - SQL statement for executing the CL command
     * @param toFile - file that is overwritten
     * @param library - library the contains the file that is actually used
     * @param file - file that is actually used
     * @param member - member that is actually used
     * @throws Exception
     */
    private boolean overwriteDatabaseFile(Statement statement, String toFile, String library, String file, String member) throws Exception {

        String command = null;

        try {

            command = String.format("OVRDBF FILE(%s) TOFILE(%s/%s) MBR(%s) OVRSCOPE(*JOB)", outputFile.getFileName(), outputFile.getLibraryName(),
                outputFile.getFileName(), outputFile.getMemberName());
            command = "CALL QSYS.QCMDEXC('" + command + "', CAST(" + command.length() + " AS DECIMAL(15, 5)))";
            statement.execute(command);

        } catch (Exception e) {
            String message = String.format("*** Could not overwrite database file %s ***", command);
            ISpherePlugin.logErrorOnce(message, e);
            throw new Exception(message, e);
        }

        return true;
    }

    /**
     * Executes a DLTOVR statement likes this:
     * 
     * <pre>
     * DLTOVR FILE(OVRFILE) LVL(*JOB)
     * </pre>
     * 
     * @param statement - SQL statement for executing the CL command
     * @param toFile - file, whose overwrite is removed
     * @throws Exception
     */
    private boolean deleteDatabaseOverwrite(Statement statement, String toFile) throws Exception {

        String command = null;

        try {

            command = String.format("DLTOVR FILE(%s) LVL(*JOB)", toFile);
            command = "CALL QSYS.QCMDEXC('" + command + "', CAST(" + command.length() + " AS DECIMAL(15, 5)))";
            statement.execute(command);

        } catch (Exception e) {
            String message = String.format("*** Could not delete database overwrite %s ***", command);
            ISpherePlugin.logErrorOnce(message, e);
            throw new Exception(message, e);
        }

        return true;
    }

    private int getNumRowsAvailable(SQLWhereClause whereClause) {

        int numRowsAvailable = Integer.MAX_VALUE;

        String sqlCountStatement = getSqlCountStatement(outputFile, whereClause);
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            preparedStatement = prepareStatement(sqlCountStatement);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                numRowsAvailable = resultSet.getInt(1);
            } else {
                numRowsAvailable = Integer.MAX_VALUE;
            }

        } catch (SQLException e) {
            ISpherePlugin.logError("*** Could not execute SQL statement: '" + sqlCountStatement + "' ***", e);
        } finally {
            try {
                super.destroy(preparedStatement);
                super.destroy(resultSet);
            } catch (Throwable e) {
            }
        }

        return numRowsAvailable;
    }

    private void handleOverflowError(JournalEntries journalEntries, int numRowsAvailable) {

        journalEntries.setOverflow(true, numRowsAvailable);
    }

    private String getSqlCountStatement(OutputFile outputFile, SQLWhereClause whereClause) {

        String sqlStatement = String.format("SELECT COUNT(JOENTT) FROM %s.%s", outputFile.getLibraryName(), outputFile.getFileName());
        if (whereClause.hasClause()) {
            sqlStatement = sqlStatement + " WHERE " + whereClause.getClause(); //$NON-NLS-1$
        }

        return sqlStatement;
    }

    public abstract String getSqlStatement();

    protected JournalEntry populateJournalEntry(ResultSet resultSet, JournalEntry journalEntry) throws Exception {

        // journalEntry.setConnectionName(getConnectionName());
        journalEntry.setId(resultSet.getInt("ID")); //$NON-NLS-1$
        journalEntry.setCommitmentCycle(resultSet.getBigDecimal(ColumnsDAO.JOCCID.name()).toBigIntegerExact());
        journalEntry.setEntryLength(resultSet.getInt(ColumnsDAO.JOENTL.name()));
        journalEntry.setEntryType(resultSet.getString(ColumnsDAO.JOENTT.name()));
        journalEntry.setIncompleteData(resultSet.getString(ColumnsDAO.JOINCDAT.name()));
        journalEntry.setJobName(resultSet.getString(ColumnsDAO.JOJOB.name()));
        journalEntry.setJobNumber(resultSet.getInt(ColumnsDAO.JONBR.name()));
        journalEntry.setJobUserName(resultSet.getString(ColumnsDAO.JOUSER.name()));
        journalEntry.setCountRrn(resultSet.getBigDecimal(ColumnsDAO.JOCTRR.name()).toBigIntegerExact());
        journalEntry.setFlag(resultSet.getString(ColumnsDAO.JOFLAG.name()));
        journalEntry.setJournalCode(resultSet.getString(ColumnsDAO.JOCODE.name()));
        journalEntry.setMemberName(resultSet.getString(ColumnsDAO.JOMBR.name()));
        journalEntry.setMinimizedSpecificData(resultSet.getString(ColumnsDAO.JOMINESD.name()));
        journalEntry.setObjectLibrary(resultSet.getString(ColumnsDAO.JOLIB.name()));
        journalEntry.setObjectName(resultSet.getString(ColumnsDAO.JOOBJ.name()));
        journalEntry.setProgramName(resultSet.getString(ColumnsDAO.JOPGM.name()));
        journalEntry.setSequenceNumber(resultSet.getBigDecimal(ColumnsDAO.JOSEQN.name()).toBigIntegerExact());
        journalEntry.setSpecificData(resultSet.getBytes(ColumnsDAO.JOESD.name()));
        journalEntry.setStringSpecificData(resultSet.getString(ColumnsDAO.JOESD.name()));

        return journalEntry;
    }

    protected String getJournalEntryCcsid() {
        return Preferences.getInstance().getJournalEntryCcsid();
    }

    private long timeElapsed(Date startTime) {
        return (new Date().getTime() - startTime.getTime());
    }

}
