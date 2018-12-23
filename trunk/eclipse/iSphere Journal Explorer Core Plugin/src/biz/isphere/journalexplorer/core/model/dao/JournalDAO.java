/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.dao;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.medfoster.sqljep.RowJEP;

import biz.isphere.base.internal.Buffer;
import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.exceptions.BufferTooSmallException;
import biz.isphere.journalexplorer.core.model.JournalEntries;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.OutputFile;
import biz.isphere.journalexplorer.core.model.api.IBMiMessage;
import biz.isphere.journalexplorer.core.model.api.JrneToRtv;
import biz.isphere.journalexplorer.core.model.api.QjoRetrieveJournalEntries;
import biz.isphere.journalexplorer.core.model.api.RJNE0200;
import biz.isphere.journalexplorer.core.preferences.Preferences;

/**
 * This class retrieves journal entries from the journal a given object is
 * associated to.
 */
public class JournalDAO {

    private static final int BUFFER_INCREMENT_SIZE = Buffer.size("64k");
    private static final int BUFFER_MAXIMUM_SIZE = Buffer.size("16MB");

    private JrneToRtv jrneToRtv;
    private OutputFile outputFile;

    public JournalDAO(JrneToRtv jrneToRtv) throws Exception {

        this.jrneToRtv = jrneToRtv;
        this.jrneToRtv.setNbrEnt(this.jrneToRtv.getNbrEnt() + 1);
        this.outputFile = getOutputFile(jrneToRtv.getConnectionName());
    }

    public static OutputFile getOutputFile(String connectionName) {
        return new OutputFile(connectionName, "QSYS", "QADSPJR5");
    }

    public JournalEntries getJournalData(String whereClause) throws Exception {

        int maxNumRows = Preferences.getInstance().getMaximumNumberOfRowsToFetch();

        JournalEntries journalEntries = new JournalEntries(maxNumRows);

        QjoRetrieveJournalEntries tRetriever = new QjoRetrieveJournalEntries(jrneToRtv);

        List<IBMiMessage> messages = null;
        RJNE0200 rjne0200 = null;
        int id = 0;

        RowJEP sqljep;
        if (!StringHelper.isNullOrEmpty(whereClause)) {
            HashMap<String, Integer> columnMapping = JournalEntry.getColumnMapping();
            sqljep = new RowJEP(whereClause);
            sqljep.parseExpression(columnMapping);
        } else {
            sqljep = null;
        }

        Date startTime = new Date();

        do {

            boolean isDynamicBufferSize = Preferences.getInstance().isRetrieveJournalEntriesDynamicBufferSize();
            int bufferSize = IntHelper.align16Bytes(Preferences.getInstance().getRetrieveJournalEntriesBufferSize());

            do {
                rjne0200 = tRetriever.execute(bufferSize);
                if (isBufferTooSmall(rjne0200)) {
                    bufferSize = BUFFER_INCREMENT_SIZE;
                }
            } while (isDynamicBufferSize && isBufferTooSmall(rjne0200) && !isBufferTooBig(rjne0200));

            if (rjne0200 != null) {
                if (rjne0200.moreEntriesAvailable() && rjne0200.getNbrOfEntriesRetrieved() == 0) {
                    messages = new LinkedList<IBMiMessage>();
                    messages.add(new IBMiMessage(BufferTooSmallException.ID,
                        Messages.Exception_Buffer_too_small_to_retrieve_next_journal_entry_Check_preferences));
                } else {
                    while (journalEntries.size() < maxNumRows && rjne0200.nextEntry()) {

                        id++;

                        JournalEntry journalEntry = new JournalEntry(outputFile);

                        JournalEntry populatedJournalEntry = populateJournalEntry(jrneToRtv.getConnectionName(), id, rjne0200, journalEntry);

                        boolean isSelected;
                        if (sqljep != null) {
                            Comparable<?>[] row = journalEntry.getRow();
                            isSelected = (Boolean)sqljep.getValue(row);
                        } else {
                            isSelected = true;
                        }

                        if (isSelected) {
                            journalEntries.add(populatedJournalEntry);
                        }

                        if (journalEntry.isRecordEntryType()) {
                            MetaDataCache.getInstance().prepareMetaData(journalEntry);
                        }

                    }
                }
            } else {
                messages = tRetriever.getMessages();
            }

        } while (rjne0200 != null && rjne0200.moreEntriesAvailable() && messages == null && journalEntries.size() < maxNumRows);

        // System.out.println("mSecs total: " + timeElapsed(startTime));

        if (rjne0200 != null && (rjne0200.hasNext() || rjne0200.moreEntriesAvailable())) {
            journalEntries.setOverflow(true, -1);
        }

        journalEntries.setMessages(messages);

        return journalEntries;
    }

    private long timeElapsed(Date startTime) {
        return (new Date().getTime() - startTime.getTime());
    }

    private boolean isBufferTooSmall(RJNE0200 rjne0200) {

        if (rjne0200 != null && rjne0200.moreEntriesAvailable() && rjne0200.getNbrOfEntriesRetrieved() == 0) {
            return true;
        }

        return false;
    }

    private boolean isBufferTooBig(RJNE0200 rjne0200) {

        if (rjne0200 != null && rjne0200.getBufferSize() >= BUFFER_MAXIMUM_SIZE) {
            return true;
        }

        return false;
    }

    private JournalEntry populateJournalEntry(String connectionName, int id, RJNE0200 journalEntryData, JournalEntry journalEntry) throws Exception {

        // AbstractTypeDAO
        journalEntry.setConnectionName(connectionName);
        journalEntry.setId(id);
        journalEntry.setCommitmentCycle(journalEntryData.getCommitCycleId());
        journalEntry.setEntryLength(journalEntryData.getEntrySpecificDataLength());
        journalEntry.setEntryType(journalEntryData.getEntryType());
        journalEntry.setIncompleteData(journalEntryData.isIncompleteData());
        journalEntry.setJobName(journalEntryData.getJobName());
        journalEntry.setJobNumber(journalEntryData.getJobNumber());
        journalEntry.setJobUserName(journalEntryData.getUserName());
        journalEntry.setCountRrn(journalEntryData.getRelativeRecordNumber());
        journalEntry.setFlag(journalEntryData.getIndicatorFlag());
        journalEntry.setJournalCode(journalEntryData.getJournalCode());
        journalEntry.setMemberName(journalEntryData.getFileMember());
        journalEntry.setMinimizedSpecificData(journalEntryData.isMinimizedEntrySpecificData());
        journalEntry.setObjectLibrary(journalEntryData.getObjectLibrary());
        journalEntry.setObjectName(journalEntryData.getObjectName());
        journalEntry.setProgramName(journalEntryData.getProgramName());
        journalEntry.setSequenceNumber(journalEntryData.getSequenceNumber());
        journalEntry.setSpecificData(journalEntryData.getEntrySpecificDataRaw());
        journalEntry.setStringSpecificData(journalEntryData.getEntrySpecificDataRaw());

        try {
            Date timestamp = journalEntryData.getTimestamp();
            journalEntry.setDate(timestamp);
            journalEntry.setTime(new Time(timestamp.getTime()));
        } catch (Exception e) {
        }

        // Type1DAO (extends the AbstractTypeDAO)
        // Depending of the journal out type, the timestamp can be a
        // single field or splitted in JODATE and JOTYPE.
        // For TYPE1 output files it is splitted into Date and Time.
        // journalEntry.setDate(timestamp);
        // journalEntry.setTime(new Time(timestamp.getTime()));

        // Type2DAO (extends the Type1DAO)
        journalEntry.setUserProfile(journalEntryData.getUserProfile());
        journalEntry.setSystemName(journalEntryData.getSystemName());

        // Type3DAO (extends the AbstractTypeDAO)
        journalEntry.setNullIndicators(new String(journalEntryData.getNullValueIndicators(), getJournalEntryCcsid()).getBytes());

        // Type4DAO (extends the Type3DAO)
        journalEntry.setJournalID(journalEntryData.getJournalIdentifier());
        journalEntry.setReferentialConstraint(journalEntryData.isReferentialConstraint());
        journalEntry.setTrigger(journalEntryData.isTrigger());
        journalEntry.setIgnoredByApyRmvJrnChg(journalEntryData.isIgnoreApyRmvJrnChg());

        // Type5DAO (extends the Type4DAO)
        journalEntry.setProgramLibrary(journalEntryData.getProgramLibraryName());
        journalEntry.setProgramLibraryAspDeviceName(journalEntryData.getProgramLibraryASPDeviceName());
        journalEntry.setProgramLibraryAspNumber(journalEntryData.getProgramLibraryASPNumber());
        journalEntry.setObjectNameIndicator(journalEntryData.getObjectNameIndicator());
        journalEntry.setSystemSequenceNumber(journalEntryData.getSystemSequenceNumber());
        journalEntry.setReceiverName(journalEntryData.getReceiverName());
        journalEntry.setReceiverLibraryName(journalEntryData.getReceiverLibraryName());
        journalEntry.setReceiverLibraryASPDeviceName(journalEntryData.getReceiverLibraryASPDeviceName());
        journalEntry.setReceiverLibraryASPNumber(journalEntryData.getReceiverLibraryASPNumber());
        journalEntry.setArmNumber(journalEntryData.getArmNumber());
        journalEntry.setThreadId(journalEntryData.getThreadIdentifier());
        journalEntry.setAddressFamily(journalEntryData.getAddressFamily());
        journalEntry.setRemotePort(journalEntryData.getRemotePort());
        journalEntry.setRemoteAddress(journalEntryData.getRemoteAddress());
        journalEntry.setLogicalUnitOfWork(journalEntryData.getLogicalUnitOfWork());
        journalEntry.setTransactionIdentifier(journalEntryData.getTransactionIdentifier());
        journalEntry.setObjectType(journalEntryData.getObjectType());
        journalEntry.setFileTypeIndicator(journalEntryData.getFileTypeIndicator());
        journalEntry.setNestedCommitLevel(journalEntryData.getNestedCommitLevel());

        return journalEntry;
    }

    protected String getJournalEntryCcsid() {
        return Preferences.getInstance().getJournalEntryCcsid();
    }
}
