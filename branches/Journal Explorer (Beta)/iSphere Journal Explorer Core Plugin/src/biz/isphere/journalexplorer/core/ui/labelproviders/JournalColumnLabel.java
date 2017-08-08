/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.labelproviders;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.preferences.Preferences;

public class JournalColumnLabel extends LabelProvider implements ITableLabelProvider, ITableColorProvider {

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    private Preferences preferences;

    public JournalColumnLabel() {
        this.preferences = Preferences.getInstance();
    }

    public Color getBackground(Object element, int index) {
        if (element instanceof JournalEntry) {
            JournalEntry journalEntry = (JournalEntry)element;

            if (preferences.isHighlightUserEntries() && journalEntry.getJournalCode().equals(JournalEntry.USER_GENERATED)) {
                return Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public Color getForeground(Object element, int index) {
        return null;
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

    public Image getColumnImage(Object object, int index) {
        return null;
    }

    public String getColumnText(Object object, int index) {

        JournalEntry journal = (JournalEntry)object;

        switch (index) {
        case 0: // RRN
            return Integer.toString(journal.getRrn()).trim();
        case 1: // JOENTT
            return journal.getEntryType();
        case 2: // JOSEQN
            return Long.toString(journal.getSequenceNumber());
        case 3: // JOCODE
            return journal.getJournalCode();
        case 4: // JOENTL
            return Integer.toString(journal.getEntryLength());
        case 5: // JODATE
            Date date = journal.getDate();
            if (date == null) {
                return "";
            }
            return getDateFormatter().format(date);
        case 6: // JOTIME
            Time time = journal.getTime();
            if (time == null) {
                return "";
            }
            return getTimeFormatter().format(time);
        case 7: // JOJOB
            return journal.getJobName();
        case 8: // JOUSER
            return journal.getJobUserName();
        case 9: // JONBR
            return Integer.toString(journal.getJobNumber());
        case 10: // JOPGM
            return journal.getProgramName();
        case 11: // JOLIB
            return journal.getObjectLibrary();
        case 12: // JOMBR
            return journal.getMemberName();
        case 13: // JOOBJ
            return journal.getObjectName();
        case 14: // JOMINESD
            return journal.getMinimizedSpecificData();
        case 15: // JOESD
            // For displaying purposes, replace the null ending character
            // for a blank.
            // Otherwise, the string was truncate by JFace
            String stringSpecificData = journal.getStringSpecificData();
            if (stringSpecificData.indexOf('\0') >= 0) {
                return stringSpecificData.replace('\0', ' ').substring(1, 200);
            } else {
                return stringSpecificData;
            }
        default:
            break;
        }

        return null;
    }
}
