/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.dao;

import java.sql.ResultSet;

import biz.isphere.journalexplorer.core.model.OutputFile;
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
        "           result.JONVI,   " +   //  added with TYPE3
                    SQL_JOESD_RESULT  +
        "      FROM %s.%s as result";
    // @formatter:on

    public Type3DAO(OutputFile outputFile) throws Exception {
        super(outputFile);
    }

    @Override
    public String getSqlStatement() {
        return GET_JOURNAL_DATA_3;
    }

    @Override
    protected JournalEntry populateJournalEntry(ResultSet resultSet, JournalEntry journalEntry) throws Exception {

        super.populateJournalEntry(resultSet, journalEntry);

        journalEntry.setUserProfile(resultSet.getString(JOUSPF));
        journalEntry.setSystemName(resultSet.getString(JOSYNM));

        // Depending of the journal out type, the timestamp can be a
        // single field or splitted in JODATE and JOTYPE.
        // For TYPE3+ output files it is returned as a timestamp value.
        journalEntry.setDate(resultSet.getDate(JOTSTP));
        journalEntry.setTime(resultSet.getTime(JOTSTP));
        journalEntry.setNullIndicators(new String(resultSet.getBytes(JONVI), getJournalEntryCcsid()).getBytes());

        return journalEntry;
    }

}
