/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.journalexplorer.core.model.JournalEntries;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.OutputFile;
import biz.isphere.journalexplorer.core.preferences.Preferences;

public abstract class AbstractTypeDAO extends DAOBase implements ColumnsDAO {

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

    public JournalEntries load(String whereClause) throws Exception {

        JournalEntries journalEntries = new JournalEntries();

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            String sqlStatement = String.format(getSqlStatement(), outputFile.getOutFileLibrary(), outputFile.getOutFileName());
            if (!StringHelper.isNullOrEmpty(whereClause)) {
                sqlStatement = sqlStatement + " WHERE " + whereClause; //$NON-NLS-1$
            }

            int maxNumRows = Preferences.getInstance().getMaximumNumberOfRowsToFetch();

            preparedStatement = prepareStatement(sqlStatement);
            resultSet = preparedStatement.executeQuery();
            resultSet.setFetchSize(50);

            if (resultSet != null) {

                JournalEntry journalEntry = null;

                while (resultSet.next()) {

                    if (journalEntries.size() >= maxNumRows) {
                        handleOverflowError(journalEntries);
                        break;
                    } else {
                        journalEntry = new JournalEntry(outputFile);
                        journalEntries.add(populateJournalEntry(resultSet, journalEntry));

                        if (journalEntry.isRecordEntryType()) {
                            MetaDataCache.INSTANCE.prepareMetaData(journalEntry);
                        }
                    }
                }
            }

        } finally {
            super.destroy(preparedStatement);
            super.destroy(resultSet);
        }

        return journalEntries;
    }

    private void handleOverflowError(JournalEntries journalEntries) {

        String sqlCountStatement = getSqlCountStatement(outputFile);

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            preparedStatement = prepareStatement(sqlCountStatement);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                journalEntries.setOverflow(true, resultSet.getInt(1));
            } else {
                journalEntries.setOverflow(true, Integer.MAX_VALUE);
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
    }

    private String getSqlCountStatement(OutputFile outputFile) {

        String sqlStatement = String.format("SELECT COUNT(JOENTT) FROM %s.%s", outputFile.getOutFileLibrary(), outputFile.getOutFileName());

        return sqlStatement;
    }

    public abstract String getSqlStatement();

    protected JournalEntry populateJournalEntry(ResultSet resultSet, JournalEntry journalEntry) throws Exception {

        journalEntry.setConnectionName(getConnectionName());
        journalEntry.setId(resultSet.getInt(RRN_OUTPUT_FILE));
        journalEntry.setCommitmentCycle(resultSet.getInt(JOCCID));
        journalEntry.setEntryLength(resultSet.getInt(JOENTL));
        journalEntry.setEntryType(resultSet.getString(JOENTT));
        journalEntry.setIncompleteData(resultSet.getString(JOINCDAT));
        journalEntry.setJobName(resultSet.getString(JOJOB));
        journalEntry.setJobNumber(resultSet.getInt(JONBR));
        journalEntry.setJobUserName(resultSet.getString(JOUSER));
        journalEntry.setCountRrn(resultSet.getInt(JOCTRR));
        journalEntry.setFlag(resultSet.getString(JOFLAG));
        journalEntry.setJournalCode(resultSet.getString(JOCODE));
        journalEntry.setMemberName(resultSet.getString(JOMBR));
        journalEntry.setMinimizedSpecificData(resultSet.getString(JOMINESD));
        journalEntry.setObjectLibrary(resultSet.getString(JOLIB));
        journalEntry.setObjectName(resultSet.getString(JOOBJ));
        journalEntry.setProgramName(resultSet.getString(JOPGM));
        journalEntry.setSequenceNumber(resultSet.getLong(JOSEQN));
        journalEntry.setSpecificData(resultSet.getBytes(JOESD));
        journalEntry.setStringSpecificData(resultSet.getString(JOESD));

        return journalEntry;
    }

    protected String getJournalEntryCcsid() {
        return Preferences.getInstance().getJournalEntryCcsid();
    }

}
