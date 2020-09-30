/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.base.internal.Buffer;
import biz.isphere.base.internal.IntHelper;
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

    /*
     * com.ibm.as400.access.ErrorCompletingRequestException: Length is not
     * valid. ==> Reducing length to 15.5 MB.
     */
    private static final int BUFFER_MAXIMUM_SIZE = IntHelper.align16Bytes((int)(1024 * 1024 * 15.5)); // 15.5MB;
    private static final int BUFFER_INCREMENT_SIZE = IntHelper.align16Bytes(Buffer.size("64k"));

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

    public JournalEntries getJournalData(String whereClause, IProgressMonitor monitor) throws Exception {

        int maxNumRows = Preferences.getInstance().getMaximumNumberOfRowsToFetch();

        JournalEntries journalEntries = new JournalEntries(maxNumRows);

        QjoRetrieveJournalEntries tRetriever = new QjoRetrieveJournalEntries(jrneToRtv);

        List<IBMiMessage> messages = null;
        RJNE0200 rjne0200 = null;
        int id = 0;

        Date startTime = new Date();

        do {

            boolean isDynamicBufferSize = Preferences.getInstance().isRetrieveJournalEntriesDynamicBufferSize();
            int bufferSize = Math.min(Preferences.getInstance().getRetrieveJournalEntriesBufferSize(), BUFFER_MAXIMUM_SIZE);
            bufferSize = IntHelper.align16Bytes(bufferSize);

            do {
                monitor.setTaskName(Messages.Calling_API);
                rjne0200 = tRetriever.execute(bufferSize);
                if (isBufferTooSmall(rjne0200) && isDynamicBufferSize) {
                    bufferSize = bufferSize + BUFFER_INCREMENT_SIZE;
                }
            } while (isDynamicBufferSize && isBufferTooSmall(rjne0200) && !isBufferTooBig(bufferSize) && !isCanceled(monitor, journalEntries));

            if (rjne0200 != null) {
                monitor.setTaskName(Messages.Loading_entries);
                if (rjne0200.moreEntriesAvailable() && rjne0200.getNbrOfEntriesRetrieved() == 0) {
                    messages = new LinkedList<IBMiMessage>();
                    messages.add(new IBMiMessage(BufferTooSmallException.ID,
                        Messages.Exception_Buffer_too_small_to_retrieve_next_journal_entry_Check_preferences));
                } else {
                    while (journalEntries.getNumberOfRowsDownloaded() < maxNumRows && rjne0200.nextEntry() && !isCanceled(monitor, journalEntries)) {

                        id++;

                        JournalEntry journalEntry = new JournalEntry(outputFile);

                        JournalEntry populatedJournalEntry = populateJournalEntry(jrneToRtv.getConnectionName(), id, rjne0200, journalEntry);
                        journalEntries.add(populatedJournalEntry);

                        if (journalEntry.isRecordEntryType()) {
                            MetaDataCache.getInstance().prepareMetaData(journalEntry);
                        }

                    }
                }
            } else {
                messages = tRetriever.getMessages();
            }

        } while (rjne0200 != null && rjne0200.moreEntriesAvailable() && messages == null && journalEntries.getNumberOfRowsDownloaded() < maxNumRows
            && !isCanceled(monitor, journalEntries));

        // System.out.println("mSecs total: " + timeElapsed(startTime) +
        // ", WHERE-CLAUSE: " + whereClause);

        if (rjne0200 != null && (rjne0200.hasNext() || rjne0200.moreEntriesAvailable())) {
            journalEntries.setOverflow(true, -1);
        }

        journalEntries.setMessages(messages);

        return journalEntries;
    }

    private long timeElapsed(Date startTime) {
        return (new Date().getTime() - startTime.getTime());
    }

    private boolean isCanceled(IProgressMonitor monitor, JournalEntries journalEntries) {
        if (monitor.isCanceled()) {
            journalEntries.setCanceled(true);
            return true;
        }
        return false;
    }

    private boolean isBufferTooSmall(RJNE0200 rjne0200) {

        if (rjne0200 != null && rjne0200.moreEntriesAvailable() && rjne0200.getNbrOfEntriesRetrieved() == 0) {
            return true;
        }

        return false;
    }

    private boolean isBufferTooBig(int bufferSize) {

        if (bufferSize >= BUFFER_MAXIMUM_SIZE) {
            return true;
        }

        return false;
    }

    private JournalEntry populateJournalEntry(String connectionName, int id, RJNE0200 journalEntryData, JournalEntry journalEntry) throws Exception {

        // AbstractTypeDAO
        // journalEntry.setConnectionName(connectionName);
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
            Timestamp timestamp = journalEntryData.getTimestamp();
            journalEntry.setTimestamp(timestamp);
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
