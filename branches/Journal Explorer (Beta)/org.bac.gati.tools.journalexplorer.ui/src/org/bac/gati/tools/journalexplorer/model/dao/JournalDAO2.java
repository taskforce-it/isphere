package org.bac.gati.tools.journalexplorer.model.dao;

import java.sql.Time;
import java.util.ArrayList;

import org.bac.gati.tools.journalexplorer.model.Journal;
import org.bac.gati.tools.journalexplorer.model.access.DAOBase;

import biz.isphere.journaling.retrievejournalentries.JournalEntries;
import biz.isphere.journaling.retrievejournalentries.JrneToRtv;
import biz.isphere.journaling.retrievejournalentries.QjoRetrieveJournalEntries;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class JournalDAO2 extends DAOBase {

    private String library;
    private String file;
    private IBMiConnection connection;

    private static int MAX_NUM_ENTRIES = 70;

    public JournalDAO2(IBMiConnection aConnection, String aLibrary, String aFile) throws Exception {
        super(aConnection);
        library = aLibrary;
        file = aFile;
        connection = aConnection;
    }

    public ArrayList<Journal> getJournalData() throws Exception {

        ArrayList<Journal> tList = new ArrayList<Journal>();

        JrneToRtv tJrneToRtv = getSelectionCriteria("JRN", "RADDATZ");
        QjoRetrieveJournalEntries tRetriever = new QjoRetrieveJournalEntries(connection.getAS400ToolboxObject(), tJrneToRtv);
        JournalEntries tJournalEntries = null;

        do {
            tJournalEntries = tRetriever.execute();
            if (tJournalEntries != null) {
                while (tJournalEntries.nextEntry()) {
                    if (tJournalEntries.isFileObject()) {

                        Journal tJournal = new Journal();

                        tJournal.connection = connection;
                        tJournal.setCommitmentCycle(tJournalEntries.getCommitCycleId().intValue());
                        tJournal.setDate(tJournalEntries.getTimestamp());
                        tJournal.setEntryLength(tJournalEntries.getEntrySpecificDataLength());
                        tJournal.setEntryType(tJournalEntries.getEntryType());
                        tJournal.setIncompleteData(tJournalEntries.isIncompleteData().toString());
                        tJournal.setJobName(tJournalEntries.getJobName());
                        tJournal.setJobNumber(Integer.parseInt(tJournalEntries.getJobNumber()));
                        tJournal.setJobUserName(tJournalEntries.getUserName());
                        tJournal.setJoCtrr(tJournalEntries.getRelativeRecordNumber().intValue());
                        tJournal.setJoFlag("-flag-");
                        tJournal.setJournalCode(tJournalEntries.getJournalCode());
                        tJournal.setJournalID(tJournalEntries.getJournalIdentifier());
                        tJournal.setMemberName(tJournalEntries.getFileMember());
                        tJournal.setMinimizedSpecificData(tJournalEntries.isMinimizedEntrySpecificData().toString());
                        tJournal.setObjectLibrary(tJournalEntries.getObjectLibrary());
                        tJournal.setObjectName(tJournalEntries.getObjectName());
                        tJournal.setOutFileLibrary("*N");
                        tJournal.setOutFileName("*N");
                        tJournal.setProgramName(tJournalEntries.getProgramName());
                        tJournal.setReferentialConstraint(tJournalEntries.isReferentialConstraint().toString());
                        tJournal.setRrn(tJournalEntries.getRelativeRecordNumber().intValue());
                        tJournal.setSequenceNumber(tJournalEntries.getSequenceNumber());
                        tJournal.setSpecificData(tJournalEntries.getEntrySpecificDataRaw());
                        tJournal.setStringSpecificData("");
                        tJournal.setSystemName(tJournalEntries.getSystemName());
                        tJournal.setTime(new Time(tJournalEntries.getTimestamp().getTime()));
                        tJournal.setTrigger(tJournalEntries.isTrigger().toString());
                        tJournal.setUserProfile(tJournalEntries.getUserProfile());

                        tList.add(tJournal);
                    }
                }
            } else {
                // displayMessages(tRetriever.getMessages());
            }
        } while (tJournalEntries != null && tJournalEntries.moreEntriesAvailable());

        return tList;
    }

    private JrneToRtv getSelectionCriteria(String aJournal, String aLibrary) throws Exception {
        //
        // JrneToRtv tJrneToRtv = new JrneToRtv(aJournal, aLibrary);
        //
        // tJrneToRtv.setFromTime("2014-05-03-11.21.09");
        // tJrneToRtv.setToTime("2014-05-03-11.22.10");
        // tJrneToRtv.setEntTyp("*RCD");
        // tJrneToRtv.setRcvRng("*CURCHAIN");
        // tJrneToRtv.setNbrEnt(70);
        //
        // tJrneToRtv.setFormatMinimzedData("*YES");
        //
        // return tJrneToRtv;

        JrneToRtv tJrneToRtv = new JrneToRtv(aJournal, aLibrary);

        tJrneToRtv.setFromTime("2017-07-31-00.00.00");
        tJrneToRtv.setToTime("2017-07-31-23.59.00");
        tJrneToRtv.setEntTyp("*RCD");
        tJrneToRtv.setRcvRng("*CURCHAIN");
        tJrneToRtv.setNbrEnt(MAX_NUM_ENTRIES);

        tJrneToRtv.setFormatMinimzedData("*YES");

        return tJrneToRtv;
    }

}
