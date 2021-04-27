/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.dao;

import java.sql.ResultSet;

import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.OutputFile;

public class Type5DAO extends Type4DAO {

    // @formatter:off
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
        "           hex(result.JOJID) AS " + ColumnsDAO.JOJID.name() + ", " +
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
        "           result.JONVI,   " +
                    SQL_JOESD_RESULT  +
        "      FROM %s.%s as result";
    // @formatter:on

    public Type5DAO(OutputFile outputFile) throws Exception {
        super(outputFile);
    }

    @Override
    public String getSqlStatement() {
        return GET_JOURNAL_DATA_5;
    }

    @Override
    protected JournalEntry populateJournalEntry(ResultSet resultSet, JournalEntry journalEntry) throws Exception {
        super.populateJournalEntry(resultSet, journalEntry);

        journalEntry = super.populateJournalEntry(resultSet, journalEntry);
        journalEntry.setProgramLibrary(resultSet.getString(ColumnsDAO.JOPGMLIB.name()));
        journalEntry.setProgramLibraryAspDeviceName(resultSet.getString(ColumnsDAO.JOPGMDEV.name()));
        journalEntry.setProgramLibraryAspNumber(resultSet.getInt(ColumnsDAO.JOPGMASP.name()));
        journalEntry.setObjectNameIndicator(resultSet.getString(ColumnsDAO.JOOBJIND.name()));
        journalEntry.setSystemSequenceNumber(resultSet.getString(ColumnsDAO.JOSYSSEQ.name()));
        journalEntry.setReceiverName(resultSet.getString(ColumnsDAO.JORCV.name()));
        journalEntry.setReceiverLibraryName(resultSet.getString(ColumnsDAO.JORCVLIB.name()));
        journalEntry.setReceiverLibraryASPDeviceName(resultSet.getString(ColumnsDAO.JORCVDEV.name()));
        journalEntry.setReceiverLibraryASPNumber(resultSet.getInt(ColumnsDAO.JORCVASP.name()));
        journalEntry.setArmNumber(resultSet.getInt(ColumnsDAO.JOARM.name()));
        journalEntry.setThreadId(resultSet.getString(ColumnsDAO.JOTHDX.name()));
        journalEntry.setAddressFamily(resultSet.getString(ColumnsDAO.JOADF.name()));
        journalEntry.setRemotePort(resultSet.getInt(ColumnsDAO.JORPORT.name()));
        journalEntry.setRemoteAddress(new String(resultSet.getBytes(ColumnsDAO.JORADR.name()), getJournalEntryCcsid()));
        journalEntry.setLogicalUnitOfWork(resultSet.getString(ColumnsDAO.JOLUW.name()));
        journalEntry.setTransactionIdentifier(resultSet.getString(ColumnsDAO.JOXID.name()));
        journalEntry.setObjectType(resultSet.getString(ColumnsDAO.JOOBJTYP.name()));
        journalEntry.setFileTypeIndicator(resultSet.getString(ColumnsDAO.JOFILTYP.name()));
        journalEntry.setNestedCommitLevel(Long.parseLong(resultSet.getString(ColumnsDAO.JOCMTLVL.name())));

        return journalEntry;
    }

}
