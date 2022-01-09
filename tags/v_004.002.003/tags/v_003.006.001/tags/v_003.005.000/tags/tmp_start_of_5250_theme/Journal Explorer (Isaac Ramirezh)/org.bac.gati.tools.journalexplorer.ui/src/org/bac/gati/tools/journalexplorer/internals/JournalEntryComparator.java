package org.bac.gati.tools.journalexplorer.internals;

import java.util.Comparator;

import org.bac.gati.tools.journalexplorer.model.adapters.JournalProperties;

public class JournalEntryComparator implements Comparator<JournalProperties> {

	@Override
	public int compare(JournalProperties left, JournalProperties right) {
		
		if (left.getJOESDProperty().compareTo(right.getJOESDProperty()) == 0) {
			return 0;
		} else {
			return -1;
		}
			
	}
}
