package biz.isphere.journaling.retrievejournalentries.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;

import biz.isphere.journaling.retrievejournalentries.JrneToRtv;
import biz.isphere.journaling.retrievejournalentries.QjoRetrieveJournalEntries;
import biz.isphere.journaling.retrievejournalentries.RJNE0200;

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
            JrneToRtv tJrneToRtv = getSelectionCriteria("GH362DTL", "GH362DTL");
            QjoRetrieveJournalEntries tRetriever = new QjoRetrieveJournalEntries(tAS400, tJrneToRtv);
            RJNE0200 tJournalEntries = null;

            int tTotal = 0;

            do {

                tJournalEntries = tRetriever.execute();
                if (tJournalEntries != null) {

                    System.out.println("Bytes returned: " + tJournalEntries.getBytesReturned());
                    System.out.println("#Entries returned: " + tJournalEntries.getNbrOfEntriesRetrieved());
                    System.out.println("Entries:");

                    while (tJournalEntries.nextEntry()) {
                        System.out
                            .println(tJournalEntries.getEntrySpecificDataLength() + ": " + tJournalEntries.getSequenceNumber().toString() + " / "
                                + tJournalEntries.getEntryType() + " / " + tJournalEntries.getTimestamp() + " / " + tJournalEntries.getObjectLibrary()
                                + "/" + tJournalEntries.getObjectName() + "(" + tJournalEntries.getFileMember() + ")");
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

        tJrneToRtv.setFromTime(today(-3));
        tJrneToRtv.setToTime(today());
        tJrneToRtv.setEntTyp("*RCD");
        tJrneToRtv.setRcvRng("*CURCHAIN");
        tJrneToRtv.setNbrEnt(MAX_NUM_ENTRIES);

        tJrneToRtv.setFormatMinimzedData("*YES");

        tJrneToRtv.setFile("SCSTWT", "GH362DTL", "*FIRST");
        tJrneToRtv.addFile("WRKDR", "GH362DTL", "*FIRST");

        return tJrneToRtv;
    }

    private String today() {
        return today(0);
    }

    private String today(int minutes) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, minutes);

        return formatter.format(calendar.getTime());
    }
}