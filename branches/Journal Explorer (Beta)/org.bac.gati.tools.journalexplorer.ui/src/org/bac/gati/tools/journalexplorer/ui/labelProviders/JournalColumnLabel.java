package org.bac.gati.tools.journalexplorer.ui.labelProviders;

import java.text.SimpleDateFormat;

import org.bac.gati.tools.journalexplorer.model.Journal;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class JournalColumnLabel extends ColumnLabelProvider {

    private static boolean highlightUserEntries;

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    public JournalColumnLabel() {

    }

    @Override
    public Color getBackground(Object element) {
        if (element instanceof Journal) {
            Journal journalObject = (Journal)element;

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

    public SimpleDateFormat getDateFormatter() {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        }
        return dateFormat;
    }

    public SimpleDateFormat getTimeFormatter() {
        if (timeFormat == null) {
            timeFormat = new SimpleDateFormat("HH:mm:ss");
        }
        return timeFormat;
    }
    
    @Override
    public String getText(Object element) {
        return super.getText(element);
    }
}
