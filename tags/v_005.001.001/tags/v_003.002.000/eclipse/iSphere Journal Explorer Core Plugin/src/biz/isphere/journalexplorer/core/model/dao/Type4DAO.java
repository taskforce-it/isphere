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

public class Type4DAO extends Type3DAO {

    // @formatter:off
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
        "           result.JONVI,   " +   
                    SQL_JOESD_RESULT  +
        "      FROM %s.%s as result";
    // @formatter:on

    public Type4DAO(OutputFile outputFile) throws Exception {
        super(outputFile);
    }

    @Override
    public String getSqlStatement() {
        return GET_JOURNAL_DATA_4;
    }

    @Override
    protected JournalEntry populateJournalEntry(ResultSet resultSet, JournalEntry journalEntry) throws Exception {

        journalEntry = super.populateJournalEntry(resultSet, journalEntry);

        journalEntry.setJournalID(resultSet.getString(JOJID));
        journalEntry.setReferentialConstraint(resultSet.getString(JORCST));
        journalEntry.setTrigger(resultSet.getString(JOTGR));
        journalEntry.setIgnoredByApyRmvJrnChg(resultSet.getString(JOIGNAPY));

        return journalEntry;
    }

}
