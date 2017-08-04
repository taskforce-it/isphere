package biz.isphere.journalexplorer.core.internals;

import java.util.Comparator;

import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;

public class JournalEntryComparator implements Comparator<JournalProperties> {

    public int compare(JournalProperties left, JournalProperties right) {

        if (left.getJOESDProperty().compareTo(right.getJOESDProperty()) == 0) {
            return 0;
        } else {
            return -1;
        }

    }
}
