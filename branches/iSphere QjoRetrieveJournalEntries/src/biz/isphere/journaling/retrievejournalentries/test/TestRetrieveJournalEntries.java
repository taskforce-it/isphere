package biz.isphere.journaling.retrievejournalentries.test;

import java.util.List;

import biz.isphere.journaling.retrievejournalentries.JournalEntries;
import biz.isphere.journaling.retrievejournalentries.JrneToRtv;
import biz.isphere.journaling.retrievejournalentries.QjoRetrieveJournalEntries;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;

public class TestRetrieveJournalEntries {

    private static int MAX_NUM_ENTRIES = 70;

    public static void main(String[] args) {
        TestRetrieveJournalEntries main = new TestRetrieveJournalEntries();
        main.run(args);
    }

    private void run(String[] args) {

        AS400 tAS400 = null;

        try {

            tAS400 = new AS400("ghentw.gfd.de", "webuser", "webuser");
            JrneToRtv tJrneToRtv = getSelectionCriteria("GH282DTL", "GH282DTL");
            QjoRetrieveJournalEntries tRetriever = new QjoRetrieveJournalEntries(tAS400, tJrneToRtv);
            JournalEntries tJournalEntries = null;

            int tTotal = 0;

            do {

                tJournalEntries = tRetriever.execute();
                if (tJournalEntries != null) {

                    System.out.println("Bytes returned: " + tJournalEntries.getBytesReturned());
                    System.out.println("#Entries returned: " + tJournalEntries.getNbrOfEntriesRetrieved());
                    System.out.println("Entries:");

                    while (tJournalEntries.nextEntry()) {
                        System.out.println(tJournalEntries.getEntrySpecificDataLength() + ": " + tJournalEntries.getSequenceNumber().toString()
                            + " / " + tJournalEntries.getEntryType() + " / " + tJournalEntries.getTimestamp() + " / "
                            + tJournalEntries.getObjectLibrary() + "/" + tJournalEntries.getObjectName() + "(" + tJournalEntries.getFileMember()
                            + ")");
                        if (tJournalEntries.isFileObject()) {
                            System.out.print("==> ");
                            Object[] tDataItems = tJournalEntries.getEntrySpecificData();
                            for (Object tData : tDataItems) {
                                System.out.print(tData.toString() + "; ");
                            }
                            System.out.println();
                        }
                        tTotal++;
                    }

                } else {
                    displayMessages(tRetriever.getMessages());
                }

            } while (tJournalEntries != null && tJournalEntries.moreEntriesAvailable());

            System.out.println("Total #entries returned: " + tTotal);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tAS400 != null) {
                tAS400.disconnectAllServices();
            }
        }

    }

    private void displayMessages(List<AS400Message> messages) {
        for (AS400Message as400Message : messages) {
            System.out.println(as400Message.getText());
        }
    }

    private JrneToRtv getSelectionCriteria(String aJournal, String aLibrary) throws Exception {

        JrneToRtv tJrneToRtv = new JrneToRtv(aJournal, aLibrary);

        tJrneToRtv.setFromTime("2014-05-05-06.20.03");
        tJrneToRtv.setToTime("2014-05-05-06.20.04");
        tJrneToRtv.setEntTyp("*RCD");
        tJrneToRtv.setRcvRng("*CURCHAIN");
        tJrneToRtv.setNbrEnt(MAX_NUM_ENTRIES);

        tJrneToRtv.setFormatMinimzedData("*YES");

        return tJrneToRtv;
    }
}