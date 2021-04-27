/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.labelproviders;

import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.dao.ColumnsDAO;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumn;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumnUI;

/**
 * This class is the label provider for a "Journal Entry" column.
 * 
 * @see JournalEntryColumn
 * @see JournalEntriesViewerForOutputFilesTab
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

    public JournalEntryColumn[] getColumns() {
        return columns;
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
            dateFormat = new SimpleDateFormat("dd.MM.yyyy"); //$NON-NLS-1$
        }
        return dateFormat;
    }

    public SimpleDateFormat getTimeFormatter() {
        if (timeFormat == null) {
            timeFormat = new SimpleDateFormat("HH:mm:ss"); //$NON-NLS-1$
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

        JournalEntry journalEntry = (JournalEntry)object;

        switch (fieldIdMapping[index]) {
        case ID:
            return journalEntry.getValueForUi(ColumnsDAO.ID.name());
        case JOENTL:
            return journalEntry.getValueForUi(ColumnsDAO.JOENTL.name());
        case JOSEQN:
            return journalEntry.getValueForUi(ColumnsDAO.JOSEQN.name());
        case JOCODE:
            return journalEntry.getValueForUi(ColumnsDAO.JOCODE.name());
        case JOENTT:
            return journalEntry.getValueForUi(ColumnsDAO.JOENTT.name());
        case JODATE:
            return journalEntry.getValueForUi(ColumnsDAO.JODATE.name());
        case JOTIME:
            return journalEntry.getValueForUi(ColumnsDAO.JOTIME.name());
        case JOTSTP:
            return journalEntry.getValueForUi(ColumnsDAO.JOTSTP.name());
        case JOJOB:
            return journalEntry.getValueForUi(ColumnsDAO.JOJOB.name());
        case JOUSER:
            return journalEntry.getValueForUi(ColumnsDAO.JOUSER.name());
        case JONBR:
            return journalEntry.getValueForUi(ColumnsDAO.JONBR.name());
        case JOPGM:
            return journalEntry.getValueForUi(ColumnsDAO.JOPGM.name());
        case JOPGMLIB:
            return journalEntry.getValueForUi(ColumnsDAO.JOPGMLIB.name());
        case JOPGMDEV:
            return journalEntry.getValueForUi(ColumnsDAO.JOPGMDEV.name());
        case JOPGMASP:
            return journalEntry.getValueForUi(ColumnsDAO.JOPGMASP.name());
        case JOOBJ:
            return journalEntry.getValueForUi(ColumnsDAO.JOOBJ.name());
        case JOLIB:
            return journalEntry.getValueForUi(ColumnsDAO.JOLIB.name());
        case JOMBR:
            return journalEntry.getValueForUi(ColumnsDAO.JOMBR.name());
        case JOCTRR:
            return journalEntry.getValueForUi(ColumnsDAO.JOCTRR.name());
        case JOFLAG:
            return journalEntry.getValueForUi(ColumnsDAO.JOFLAG.name());
        case JOCCID:
            return journalEntry.getValueForUi(ColumnsDAO.JOCCID.name());
        case JOUSPF:
            return journalEntry.getValueForUi(ColumnsDAO.JOUSPF.name());
        case JOSYNM:
            return journalEntry.getValueForUi(ColumnsDAO.JOSYNM.name());
        case JOJID:
            return journalEntry.getValueForUi(ColumnsDAO.JOJID.name());
        case JORCST:
            return journalEntry.getValueForUi(ColumnsDAO.JORCST.name());
        case JOTGR:
            return journalEntry.getValueForUi(ColumnsDAO.JOTGR.name());
        case JOINCDAT:
            return journalEntry.getValueForUi(ColumnsDAO.JOINCDAT.name());
        case JOIGNAPY:
            return journalEntry.getValueForUi(ColumnsDAO.JOIGNAPY.name());
        case JOMINESD:
            return journalEntry.getValueForUi(ColumnsDAO.JOMINESD.name());
        case JOOBJIND:
            return journalEntry.getValueForUi(ColumnsDAO.JOOBJIND.name());
        case JOSYSSEQ:
            return journalEntry.getValueForUi(ColumnsDAO.JOSYSSEQ.name());
        case JORCV:
            return journalEntry.getValueForUi(ColumnsDAO.JORCV.name());
        case JORCVLIB:
            return journalEntry.getValueForUi(ColumnsDAO.JORCVLIB.name());
        case JORCVDEV:
            return journalEntry.getValueForUi(ColumnsDAO.JORCVDEV.name());
        case JORCVASP:
            return journalEntry.getValueForUi(ColumnsDAO.JORCVASP.name());
        case JOARM:
            return journalEntry.getValueForUi(ColumnsDAO.JOARM.name());
        case JOTHDX:
            return journalEntry.getValueForUi(ColumnsDAO.JOTHDX.name());
        case JOADF:
            return journalEntry.getValueForUi(ColumnsDAO.JOADF.name());
        case JORPORT:
            return journalEntry.getValueForUi(ColumnsDAO.JORPORT.name());
        case JORADR:
            return journalEntry.getValueForUi(ColumnsDAO.JORADR.name());
        case JOLUW:
            return journalEntry.getValueForUi(ColumnsDAO.JOLUW.name());
        case JOXID:
            return journalEntry.getValueForUi(ColumnsDAO.JOXID.name());
        case JOOBJTYP:
            return journalEntry.getValueForUi(ColumnsDAO.JOOBJTYP.name());
        case JOFILTYP:
            return journalEntry.getValueForUi(ColumnsDAO.JOFILTYP.name());
        case JOCMTLVL:
            return journalEntry.getValueForUi(ColumnsDAO.JOCMTLVL.name());
        case JONVI:
            return journalEntry.getValueForUi(ColumnsDAO.JONVI.name());
        case JOESD:
            return journalEntry.getValueForUi(ColumnsDAO.JOESD.name());
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
