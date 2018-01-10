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
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumn;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumnUI;
import biz.isphere.journalexplorer.core.ui.widgets.JournalEntriesViewer;

/**
 * This class is the label provider for a "Journal Entry" column.
 * 
 * @see JournalEntryColumn
 * @see JournalEntriesViewer
 */
public class JournalEntryLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    private Preferences preferences;
    private JournalEntryColumnUI[] fieldIdMapping;
    private JournalEntryColumn[] columns;

    public JournalEntryLabelProvider(JournalEntryColumnUI[] fieldIdMapping, JournalEntryColumn[] columns) {
        this.preferences = Preferences.getInstance();
        this.fieldIdMapping = fieldIdMapping;
        this.columns = columns;
    }

    public Color getBackground(Object element, int index) {

        if (index == 0) {
            if (preferences.isColoringEnabled()) {
                return columns[index].getColor();
            } else {
                return Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
            }
        }

        if (element instanceof JournalEntry) {
            JournalEntry journalEntry = (JournalEntry)element;

            if (preferences.isHighlightUserEntries() && journalEntry.getJournalCode().equals(JournalEntry.USER_GENERATED)) {
                return Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
            } else {
                if (preferences.isColoringEnabled()) {
                    return columns[index].getColor();
                } else {
                    return null;
                }
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

        switch (fieldIdMapping[index]) {
        case ID:
            return Integer.toString(journal.getId()).trim();
        case JOENTL:
            return Integer.toString(journal.getEntryLength());
        case JOSEQN:
            return Long.toString(journal.getSequenceNumber());
        case JOCODE:
            return journal.getJournalCode();
        case JOENTT:
            return journal.getEntryType();
        case JODATE:
            Date date = journal.getDate();
            if (date == null) {
                return "";
            }
            return getDateFormatter().format(date);
        case JOTIME:
            Time time = journal.getTime();
            if (time == null) {
                return "";
            }
            return getTimeFormatter().format(time);
        case JOJOB:
            return journal.getJobName();
        case JOUSER:
            return journal.getJobUserName();
        case JONBR:
            return Integer.toString(journal.getJobNumber());
        case JOPGM:
            return journal.getProgramName();
        case JOPGMLIB:
            return journal.getProgramLibrary();
        case JOPGMDEV:
            return journal.getProgramAspDevice();
        case JOPGMASP:
            return Integer.toString(journal.getProgramAsp());
        case JOOBJ:
            return journal.getObjectName();
        case JOLIB:
            return journal.getObjectLibrary();
        case JOMBR:
            return journal.getMemberName();
        case JOCTRR:
            return Integer.toString(journal.getCountRrn());
        case JOFLAG:
            return journal.getFlag();
        case JOCCID:
            return Integer.toString(journal.getCommitmentCycle());
        case JOUSPF:
            return journal.getUserProfile();
        case JOSYNM:
            return journal.getSystemName();
        case JOJID:
            return journal.getJournalID();
        case JORCST:
            return journal.getReferentialConstraintText();
        case JOTGR:
            return journal.getTriggerText();
        case JOINCDAT:
            return journal.getIncompleteDataText();
        case JOIGNAPY:
            return journal.getIgnoredByApyRmvJrnChgText();
        case JOMINESD:
            return journal.getMinimizedSpecificDataText();
        case JOOBJIND:
            return journal.getObjectIndicatorText();
        case JOSYSSEQ:
            return journal.getSystemSequenceNumber();
        case JORCV:
            return journal.getReceiver();
        case JORCVLIB:
            return journal.getReceiverLibrary();
        case JORCVDEV:
            return journal.getReceiverAspDevice();
        case JORCVASP:
            return Integer.toString(journal.getReceiverAsp());
        case JOARM:
            return Integer.toString(journal.getArmNumber());
        case JOTHDX:
            return journal.getThreadId();
        case JOADF:
            return journal.getAddressFamilyText();
        case JORPORT:
            return Integer.toString(journal.getRemotePort());
        case JORADR:
            return journal.getRemoteAddress();
        case JOLUW:
            return journal.getLogicalUnitOfWork();
        case JOXID:
            return journal.getTransactionIdentifier();
        case JOOBJTYP:
            return journal.getObjectType();
        case JOFILTYP:
            return journal.getFileTypeIndicatorText();
        case JOCMTLVL:
            return Integer.toString(journal.getCommitmentCycle());
        case JOESD:
            // For displaying purposes, replace 0x00 with blanks.
            // Otherwise, the string was truncate by JFace
            String stringSpecificData = journal.getStringSpecificData();
            if (stringSpecificData.lastIndexOf('\0') >= 0) {
                return stringSpecificData.replace('\0', ' ').substring(1, Math.min(200, stringSpecificData.length()));
            } else {
                return stringSpecificData;
            }
        default:
            break;
        }

        return null;
    }

    public void setColumnColor(String columnName, Color color) {

        if (columns == null || columnName == null) {
            return;
        }

        for (JournalEntryColumn column : columns) {
            if (columnName.equals(column.getName())) {
                column.setColor(color);
                return;
            }
        }

    }
}
