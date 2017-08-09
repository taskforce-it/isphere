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
    
    public Type5DAO(File outputFile) throws Exception {
        super(outputFile);
    }

    protected String getSqlStatement() {
        return GET_JOURNAL_DATA_5;
    }

    @Override
    protected JournalEntry populateJournalEntry(ResultSet resultSet, JournalEntry journalEntry) throws Exception {
        
        journalEntry = super.populateJournalEntry(resultSet, journalEntry);
        journalEntry.setProgramLibrary(resultSet.getString("JOPGMLIB"));
        journalEntry.setProgramAspDevice(resultSet.getString("JOPGMDEV"));
        journalEntry.setProgramAsp(resultSet.getInt("JOPGMASP"));
        journalEntry.setObjectIndicator(resultSet.getString("JOOBJIND")); 
        journalEntry.setSystemSequenceNumber(resultSet.getString("JOSYSSEQ")); 
        journalEntry.setReceiver(resultSet.getString("JORCV")); 
        journalEntry.setReceiverLibrary(resultSet.getString("JORCVLIB")); 
        journalEntry.setReceiverAspDevice(resultSet.getString("JORCVDEV")); 
        journalEntry.setReceiverAsp(resultSet.getInt("JORCVASP")); 
        journalEntry.setArmNumber(resultSet.getInt("JOARM")); 
//        journalEntry.setTrigger(resultSet.getString("JOTHD")); // Thread ID, HEX(8) 
        journalEntry.setThreadId(resultSet.getString("JOTHDX")); // Thread ID Hex, CHAR(16) 
        journalEntry.setAddressFamily(resultSet.getString("JOADF")); // Address Family
        journalEntry.setRemotePort(resultSet.getInt("JORPORT")); // Remote Port 
        journalEntry.setRemoteAddress(resultSet.getString("JORADR")); // Remote Address
        journalEntry.setLogicalUnitOfWork(resultSet.getString("JOLUW")); // Logical Unit of Work 
        journalEntry.setTransactionIdentifier(resultSet.getString("JOXID")); // XID
        journalEntry.setObjectType(resultSet.getString("JOOBJTYP")); // Object Type
        journalEntry.setFileTypeIndicator(resultSet.getString("JOFILTYP")); // File Type Indicator 
        journalEntry.setNestedCommitLevel(resultSet.getString("JOCMTLVL")); // Nested Commit Level

        return journalEntry;
    }

}
