/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.dao;

import java.sql.ResultSet;

import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.OutputFile;

public class Type2DAO extends Type1DAO {

    // @formatter:off
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
                    SQL_JOESD_RESULT  +
        "      FROM %s.%s as result";
    // @formatter:on

    public Type2DAO(OutputFile outputFile) throws Exception {
        super(outputFile);
    }

    @Override
    public String getSqlStatement() {
        return GET_JOURNAL_DATA_2;
    }

    @Override
    protected JournalEntry populateJournalEntry(ResultSet resultSet, JournalEntry journalEntry) throws Exception {

        journalEntry = super.populateJournalEntry(resultSet, journalEntry);

        journalEntry.setUserProfile(resultSet.getString("JOUSPF"));
        journalEntry.setSystemName(resultSet.getString("JOSYNM"));

        return journalEntry;
    }

}
