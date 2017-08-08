/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import biz.isphere.journalexplorer.core.model.File;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.MetaTable;
import biz.isphere.journalexplorer.rse.shared.model.dao.AbstractDAOBase;

/**
 * This class loads the exported journal *TYPE1 to *TYPE5 data that has been
 * exported by DSPJRN to an output file. For example:
 * 
 * <pre>
 * DSPJRN JRN(library/journal) FILE((library/file)) RCVRNG(*CURCHAIN) 
 *   FROMTIME(060417 140000) TOTIME(060417 160000) ENTTYP(*RCD)    
 *   OUTPUT(*OUTFILE) OUTFILFMT(*TYPE3) OUTFILE(library/file)    
 *   ENTDTALEN(1024)
 * </pre>
 */
public class JournalDAO extends DAOBase {

    private String library;

    private String file;

    public JournalDAO(String connectionName, String library, String file) throws Exception {
        super(connectionName);
        this.library = library;
        this.file = file;
    }

    // @formatter:off
    private static final String GET_JOURNAL_DATA_1 = 
        "    SELECT rrn(result) as ID, " + 
        "           result.JOENTL,  " + 
        "           result.JOSEQN,  " + 
        "           result.JOCODE,  " + 
        "           result.JOENTT,  " + 
        "           result.JODATE,  " +
        "           result.JOTIME,  " +
        "           result.JOJOB,   " + 
        "           result.JOUSER,  " + 
        "           result.JONBR,   " + 
        "           result.JOPGM,   " +
        "           result.JOOBJ,   " +
        "           result.JOLIB,   " + 
        "           result.JOMBR,   " + 
        "           result.JOCTRR,  " + 
        "           result.JOFLAG,  " + 
        "           result.JOCCID,  " + 
        "           result.JOINCDAT," + 
        "           result.JOMINESD," + 
        "           SUBSTR(result.JOESD,1,5000) AS JOESD" + 
        "      FROM %s.%s as result";
    
    
    private static final String GET_JOURNAL_DATA_3 = 
        "    SELECT rrn(result) as ID, " + 
        "           result.JOENTL,  " + 
        "           result.JOSEQN,  " + 
        "           result.JOCODE,  " + 
        "           result.JOENTT,  " + 
        "           result.JOTSTP,  " +
        "           result.JOJOB,   " + 
        "           result.JOUSER,  " + 
        "           result.JONBR,   " + 
        "           result.JOPGM,   " +
        "           result.JOOBJ,   " +
        "           result.JOLIB,   " + 
        "           result.JOMBR,   " + 
        "           result.JOCTRR,  " + 
        "           result.JOFLAG,  " + 
        "           result.JOCCID,  " + 
        "           result.JOINCDAT," + 
        "           result.JOMINESD," + 
        "           SUBSTR(result.JOESD,1,5000) AS JOESD" + 
        "      FROM %s.%s as result";
    // @formatter:on

    public ArrayList<JournalEntry> getJournalData() throws Exception {

        JournalEntry journalEntry = null;
        String statementSQL = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        ArrayList<JournalEntry> journalData = new ArrayList<JournalEntry>();

        try {

            boolean isType3OutputFile = isType3OutputFile(this.library, this.file);
            if (isType3OutputFile) {
                statementSQL = String.format(GET_JOURNAL_DATA_3, this.library, this.file);
            } else {
                statementSQL = String.format(GET_JOURNAL_DATA_1, this.library, this.file);
            }

            preparedStatement = prepareStatement(statementSQL);
            resultSet = preparedStatement.executeQuery();
            resultSet.setFetchSize(50);

            if (resultSet != null) {

                // TODO: remove it later
                Date inicio = Calendar.getInstance().getTime();

                while (resultSet.next()) {

                    journalEntry = new JournalEntry();

                    journalEntry.setConnectionName(getConnectionName());
                    journalEntry.setCommitmentCycle(resultSet.getInt("JOCCID"));

                    // Depending of the journal out type, the timestamp can be a
                    // single field or splitted in JODATE and JOTYPE
                    if (hasColumn(resultSet, "JOTSTP")) {
                        journalEntry.setDate(resultSet.getDate("JOTSTP"));
                        journalEntry.setTime(resultSet.getTime("JOTSTP"));
                    } else {
                        String date = resultSet.getString("JODATE");
                        int time = resultSet.getInt("JOTIME");
                        journalEntry.setDate(date, time, getDateFormat(), getDateSeparator(), getTimeSeparator());
                    }

                    journalEntry.setEntryLength(resultSet.getInt("JOENTL"));
                    journalEntry.setEntryType(resultSet.getString("JOENTT"));
                    journalEntry.setIncompleteData(resultSet.getString("JOINCDAT"));
                    journalEntry.setJobName(resultSet.getString("JOJOB"));
                    journalEntry.setJobNumber(resultSet.getInt("JONBR"));
                    journalEntry.setJobUserName(resultSet.getString("JOUSER"));
                    journalEntry.setJoCtrr(resultSet.getInt("JOCTRR"));
                    journalEntry.setJoFlag(resultSet.getString("JOFLAG"));
                    journalEntry.setJournalCode(resultSet.getString("JOCODE"));
                    // setJournalID
                    journalEntry.setMemberName(resultSet.getString("JOMBR"));
                    journalEntry.setMinimizedSpecificData(resultSet.getString("JOMINESD"));
                    journalEntry.setObjectLibrary(resultSet.getString("JOLIB"));
                    journalEntry.setObjectName(resultSet.getString("JOOBJ"));
                    // setOutFileLibrary
                    // setOutFileName
                    journalEntry.setProgramName(resultSet.getString("JOPGM"));
                    // setReferentialConstraint
                    journalEntry.setRrn(resultSet.getInt("ID"));
                    journalEntry.setSequenceNumber(resultSet.getLong("JOSEQN"));
                    journalEntry.setSpecificData(resultSet.getBytes("JOESD"));
                    journalEntry.setStringSpecificData(resultSet.getString("JOESD"));
                    // setSystemName
                    // setTime
                    // setTrigger
                    // setUserProfile

                    journalEntry.setOutFileLibrary(library);
                    journalEntry.setOutFileName(file);
                    journalData.add(journalEntry);

                    MetaDataCache.INSTANCE.prepareMetaData(journalEntry);
                }
                // TODO: remove it later
                Date Fin = Calendar.getInstance().getTime();
            }
        } catch (Exception exception) {
            throw exception;
        } finally {
            super.destroy(preparedStatement);
            super.destroy(resultSet);
        }
        return journalData;
    }

    private boolean isType3OutputFile(String library2, String file2) throws Exception {

        File outputFile = new File();
        outputFile.setOutFileLibrary(library2);
        outputFile.setOutFileName(file2);
        outputFile.setConnetionName(getConnectionName());

        MetaTable metaTable = MetaDataCache.INSTANCE.retrieveMetaData(outputFile);
        metaTable.setHidden(true); // Hide table for ConfigureParserDialog

        return metaTable.hasColumn("JOTSTP");
    }

    private boolean hasColumn(ResultSet resultSet, String columnName) throws SQLException {

        ResultSetMetaData metaData = resultSet.getMetaData();
        int count = metaData.getColumnCount();
        for (int i = 1; i <= count; i++) {
            if (metaData.getColumnName(i).equals(columnName)) {
                return true;
            }
        }

        return false;
    }

    /*
     * private boolean hasColumn(ResultSet resultSet, String columnName) { int
     * columnIndex; try { columnIndex = resultSet.findColumn(columnName); return
     * columnIndex >= 0; } catch (Exception exception) { return false; } }
     */
}
