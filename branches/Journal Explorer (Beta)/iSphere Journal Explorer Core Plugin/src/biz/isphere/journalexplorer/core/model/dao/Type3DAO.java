/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.dao;

import java.sql.ResultSet;

import biz.isphere.journalexplorer.core.model.File;
import biz.isphere.journalexplorer.core.model.JournalEntry;

public class Type3DAO extends AbstractTypeDAO {

    // @formatter:off
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
    // @formatter:on
    
    public Type3DAO(File outputFile) throws Exception {
        super(outputFile);
    }

    protected String getSqlStatement() {
        return GET_JOURNAL_DATA_3;
    }

    @Override
    protected JournalEntry populateJournalEntry(ResultSet resultSet, JournalEntry journalEntry) throws Exception {
        
        journalEntry.setConnectionName(getConnectionName());
        
        journalEntry.setRrn(resultSet.getInt("ID"));
        journalEntry.setCommitmentCycle(resultSet.getInt("JOCCID"));

        // Depending of the journal out type, the timestamp can be a
        // single field or splitted in JODATE and JOTYPE.
        // For TYPE3+ output files it is returned as a timestamp value.
        journalEntry.setDate(resultSet.getDate("JOTSTP"));
        journalEntry.setTime(resultSet.getTime("JOTSTP"));
        
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

        return journalEntry;
    }

}
