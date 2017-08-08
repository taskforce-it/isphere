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
import java.util.ArrayList;
import java.util.List;

import biz.isphere.journalexplorer.core.model.File;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.MetaTable;

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

    private File file;

    public JournalDAO(File outputFile) throws Exception {
        super(outputFile.getConnectionName());
        this.file = outputFile;
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
                    // JORES - reserved
        "           SUBSTR(result.JOESD,1,5000) AS JOESD" +
        "      FROM %s.%s as result";
    
    private static final String GET_JOURNAL_DATA_2 =
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
        "           result.JOUSPF,  " + //  added with TYPE2
        "           result.JOSYNM,  " + //  added with TYPE2
        "           result.JOINCDAT," +
        "           result.JOMINESD," +
                    // JORES - reserved
        "           SUBSTR(result.JOESD,1,5000) AS JOESD" + 
        "      FROM %s.%s as result";
    
    private static final String GET_JOURNAL_DATA_3 =
        "    SELECT rrn(result) as ID, " +
        "           result.JOENTL,  " +
        "           result.JOSEQN,  " +
        "           result.JOCODE,  " +
        "           result.JOENTT,  " +
        "           result.JOTSTP,  " + //  changed with TYPE3
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
        "           result.JOUSPF,  " +
        "           result.JOSYNM,  " +
        "           result.JOINCDAT," +
        "           result.JOMINESD," +
                    // JORES - reserved
                    // JONVI - null value indicators
        "           SUBSTR(result.JOESD,1,5000) AS JOESD" + 
        "      FROM %s.%s as result";
    
    private static final String GET_JOURNAL_DATA_4 = 
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
        "           result.JOUSPF,  " +
        "           result.JOSYNM,  " +
        "           result.JOJID,   " +   //  added with TYPE4
        "           result.JORCST,  " +   //  added with TYPE4
        "           result.JOTGR,   " +   //  added with TYPE4
        "           result.JOINCDAT," +
        "           result.JOIGNAPY," +   //  added with TYPE4
        "           result.JOMINESD," +
                    // JORES - reserved
                    // JONVI - null value indicators
        "           SUBSTR(result.JOESD,1,5000) AS JOESD" + 
        "      FROM %s.%s as result";
    
    private static final String GET_JOURNAL_DATA_5 = 
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
        "           result.JOPGMLIB," +   //  added with TYPE5
        "           result.JOPGMDEV," +   //  added with TYPE5
        "           result.JOPGMASP," +   //  added with TYPE5
        "           result.JOOBJ,   " +
        "           result.JOLIB,   " +
        "           result.JOMBR,   " +
        "           result.JOCTRR,  " +
        "           result.JOFLAG,  " +
        "           result.JOCCID,  " +
        "           result.JOUSPF,  " +
        "           result.JOSYNM,  " +
        "           result.JOJID,   " +
        "           result.JORCST,  " +
        "           result.JOTGR,   " +
        "           result.JOINCDAT," +
        "           result.JOIGNAPY," +
        "           result.JOMINESD," +
        "           result.JOOBJIND," +   //  added with TYPE5
        "           result.JOSYSSEQ," +   //  added with TYPE5
        "           result.JORCV   ," +   //  added with TYPE5
        "           result.JORCVLIB," +   //  added with TYPE5
        "           result.JORCVDEV," +   //  added with TYPE5
        "           result.JORCVASP," +   //  added with TYPE5
        "           result.JOARM   ," +   //  added with TYPE5
        "           result.JOTHD   ," +   //  added with TYPE5
        "           result.JOTHDX  ," +   //  added with TYPE5
        "           result.JOADF   ," +   //  added with TYPE5
        "           result.JORPORT ," +   //  added with TYPE5
        "           result.JORADR  ," +   //  added with TYPE5
        "           result.JOLUW   ," +   //  added with TYPE5
        "           result.JOXID   ," +   //  added with TYPE5
        "           result.JOOBJTYP," +   //  added with TYPE5
        "           result.JOFILTYP," +   //  added with TYPE5
        "           result.JOCMTLVL," +   //  added with TYPE5
                    // JORES - reserved
                    // JONVI - null value indicators
        "           SUBSTR(result.JOESD,1,5000) AS JOESD" + 
        "      FROM %s.%s as result";
    // @formatter:on

    public List<JournalEntry> getJournalData() throws Exception {

        JournalEntry journalEntry = null;
        String statementSQL = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        List<JournalEntry> journalData = new ArrayList<JournalEntry>();

        try {

            int type = getOutfileType(file);
            switch (type) {
            case JournalOutputType.TYPE5:
                statementSQL = String.format(GET_JOURNAL_DATA_5, file.getOutFileLibrary(), file.getOutFileName());
                break;
            case JournalOutputType.TYPE4:
                statementSQL = String.format(GET_JOURNAL_DATA_4, file.getOutFileLibrary(), file.getOutFileName());
                break;
            case JournalOutputType.TYPE3:
                statementSQL = String.format(GET_JOURNAL_DATA_3, file.getOutFileLibrary(), file.getOutFileName());
                break;
            case JournalOutputType.TYPE2:
                statementSQL = String.format(GET_JOURNAL_DATA_2, file.getOutFileLibrary(), file.getOutFileName());
                break;
            default:
                statementSQL = String.format(GET_JOURNAL_DATA_1, file.getOutFileLibrary(), file.getOutFileName());
                break;
            }

            preparedStatement = prepareStatement(statementSQL);
            resultSet = preparedStatement.executeQuery();
            resultSet.setFetchSize(50);

            if (resultSet != null) {

                while (resultSet.next()) {

                    journalEntry = new JournalEntry();

                    journalEntry.setConnectionName(getConnectionName());
                    
                    journalEntry.setRrn(resultSet.getInt("ID"));
                    journalEntry.setCommitmentCycle(resultSet.getInt("JOCCID"));

                    // Depending of the journal out type, the timestamp can be a
                    // single field or splitted in JODATE and JOTYPE
                    if (type >= JournalOutputType.TYPE3) {
                        journalEntry.setDate(resultSet.getDate("JOTSTP"));
                        journalEntry.setTime(resultSet.getTime("JOTSTP"));
                    } else {
                        String date = resultSet.getString("JODATE");
                        int time = resultSet.getInt("JOTIME");
                        journalEntry.setDate(date, time, getDateFormat(), null, null);
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
                    journalEntry.setSequenceNumber(resultSet.getLong("JOSEQN"));
                    journalEntry.setSpecificData(resultSet.getBytes("JOESD"));
                    journalEntry.setStringSpecificData(resultSet.getString("JOESD"));
                    // setSystemName
                    // setTime
                    // setTrigger
                    // setUserProfile

                    journalEntry.setOutFileLibrary(file.getOutFileLibrary());
                    journalEntry.setOutFileName(file.getOutFileName());
                    journalData.add(journalEntry);

                    MetaDataCache.INSTANCE.prepareMetaData(journalEntry);

                    if (type >= JournalOutputType.TYPE5) {
                        journalEntry.setProgramLibrary(resultSet.getString("JOPGMLIB"));
                    }
                }
            }
        } catch (Exception exception) {
            throw exception;
        } finally {
            super.destroy(preparedStatement);
            super.destroy(resultSet);
        }
        return journalData;
    }

    private int getOutfileType(File outputFile) throws Exception {

        MetaTable metaTable = MetaDataCache.INSTANCE.retrieveMetaData(outputFile);

        return metaTable.getOutfileType();
    }
}
