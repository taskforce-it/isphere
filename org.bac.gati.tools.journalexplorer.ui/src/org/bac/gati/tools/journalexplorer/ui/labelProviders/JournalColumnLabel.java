package org.bac.gati.tools.journalexplorer.ui.labelProviders;

import org.bac.gati.tools.journalexplorer.model.Journal;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class JournalColumnLabel extends ColumnLabelProvider {

	private static boolean highlightUserEntries;

	public JournalColumnLabel() {
		
	}
	
	@Override
	public Color getBackground(Object element) {
		if (element instanceof Journal) {
			Journal journalObject = (Journal) element;
			
			if (JournalColumnLabel.highlightUserEntries && journalObject.getJournalCode().equals(Journal.USER_GENERATED)) { 
				return Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static boolean isHighlightUserEntries() {
		return JournalColumnLabel.highlightUserEntries;
	}

	public static void setHighlightUserEntries(boolean highlightUserEntries) {
		JournalColumnLabel.highlightUserEntries = highlightUserEntries;
	}
}
