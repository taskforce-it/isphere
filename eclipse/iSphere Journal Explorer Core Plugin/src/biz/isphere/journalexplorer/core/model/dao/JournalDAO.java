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
import java.util.LinkedList;
import java.util.List;

import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.DateTimeHelper;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.JournalEntries;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.OutputFile;
import biz.isphere.journalexplorer.core.model.api.IBMiMessage;
import biz.isphere.journalexplorer.core.model.api.JrneToRtv;
import biz.isphere.journalexplorer.core.model.api.QjoRetrieveJournalEntries;
import biz.isphere.journalexplorer.core.model.api.RJNE0200;
import biz.isphere.journalexplorer.core.model.shared.JournaledObject;
import biz.isphere.journalexplorer.core.preferences.Preferences;

import com.ibm.as400.access.AS400;

/**
 * This class retrieves journal entries from the journal a given object is
 * associated to.
 */
public class JournalDAO {

    private JournaledObject journaledObject;

    public JournalDAO(JournaledObject journaledObject) throws Exception {

        this.journaledObject = journaledObject;
    }

    public JournalEntries getJournalData() throws Exception {
        return load();
    }

    public JournalEntries load() throws Exception {

        JournalEntries journalEntries = new JournalEntries();

        int maxNumRows = Preferences.getInstance().getMaximumNumberOfRowsToFetch();

        JrneToRtv tJrneToRtv = new JrneToRtv(journaledObject.getJournalLibraryName(), journaledObject.getJournalName());

        String startingDate = DateTimeHelper.getTimestampFormattedISO(journaledObject.getStartingDate());
        String endingDate = DateTimeHelper.getTimestampFormattedISO(journaledObject.getEndingDate());

        tJrneToRtv.setFromTime(startingDate);
        tJrneToRtv.setToTime(endingDate);

        if (journaledObject.isRecordsOnly()) {
            tJrneToRtv.setEntTyp(JrneToRtv.ENTTYP_RCD);
        } else {
            tJrneToRtv.setEntTyp(JrneToRtv.ENTTYP_ALL);
        }

        tJrneToRtv.setNullIndLen(JrneToRtv.NULLINDLEN_VARLEN);
        tJrneToRtv.setNbrEnt(maxNumRows);
        tJrneToRtv.setFile(journaledObject.getLibraryName(), journaledObject.getObjectName(), "*FIRST"); //$NON-NLS-1$

        AS400 system = IBMiHostContributionsHandler.getSystem(journaledObject.getConnectionName());
        QjoRetrieveJournalEntries tRetriever = new QjoRetrieveJournalEntries(system, tJrneToRtv);

        OutputFile outputFile = new OutputFile(journaledObject.getConnectionName(), "QSYS", "QADSPJR5");
        List<IBMiMessage> messages = null;
        RJNE0200 rjne0200 = null;
        int id = 0;

        do {

            rjne0200 = tRetriever.execute();
            if (rjne0200 != null) {
                if (rjne0200.moreEntriesAvailable() && rjne0200.getNbrOfEntriesRetrieved() == 0) {
                    messages = new LinkedList<IBMiMessage>();
                    messages.add(new IBMiMessage("RJE0001",
                        Messages.RJE0001_Retrieve_journal_entry_buffer_is_to_small_to_return_at_least_one_journal_entry));
                } else {
                    while (rjne0200.nextEntry()) {

                        id++;

                        JournalEntry journalEntry = new JournalEntry(outputFile); //$NON-NLS-1$ //$NON-NLS-2$

                        journalEntries.add(populateJournalEntry(journaledObject.getConnectionName(), id, rjne0200, journalEntry));

                        if (journalEntry.isRecordEntryType()) {
                            MetaDataCache.INSTANCE.prepareMetaData(journalEntry);
                        }

                    }
                }
            } else {
                messages = tRetriever.getMessages();
            }

        } while (rjne0200 != null && rjne0200.moreEntriesAvailable() && messages == null && journalEntries.size() < maxNumRows);

        if (rjne0200 != null && (rjne0200.hasNext() || rjne0200.moreEntriesAvailable())) {
            journalEntries.setOverflow(true, -1);
        }

        journalEntries.setMessages(messages);

        return journalEntries;
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
