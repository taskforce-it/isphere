/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.labelproviders;

import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;
import biz.isphere.jobtraceexplorer.core.model.dao.ColumnsDAO;
import biz.isphere.jobtraceexplorer.core.preferences.Preferences;
import biz.isphere.jobtraceexplorer.core.ui.model.JobTraceEntryColumn;
import biz.isphere.jobtraceexplorer.core.ui.model.JobTraceEntryColumnUI;
import biz.isphere.jobtraceexplorer.core.ui.preferencepages.HighlightColor;
import biz.isphere.jobtraceexplorer.core.ui.widgets.JobTraceEntriesSQLViewerTab;

/**
 * This class is the label provider for a "Job Trace Entry" column.
 * 
 * @see JobTraceEntryColumn
 * @see JobTraceEntriesSQLViewerTab
 */
public class JobTraceEntryLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    private JobTraceEntryColumnUI[] fieldIdMapping;
    private JobTraceEntryColumn[] columns;

    public JobTraceEntryLabelProvider(JobTraceEntryColumnUI[] fieldIdMapping, JobTraceEntryColumn[] columns) {
        this.fieldIdMapping = fieldIdMapping;
        this.columns = columns;
    }

    public JobTraceEntryColumn[] getColumns() {
        return columns;
    }

    public Color getBackground(Object element, int index) {

        if (!(element instanceof JobTraceEntry)) {
            return null;
        }

        JobTraceEntry jobTraceEntry = (JobTraceEntry)element;

        if (jobTraceEntry.isExcluded() && fieldIdMapping[index] == JobTraceEntryColumnUI.PROC_NAME) {
            return Preferences.getInstance().getColorSeverity(HighlightColor.HIDDEN_PROCEDURES);
        }

        if (jobTraceEntry.getParent().isHighlighted(index, jobTraceEntry.getValueForUi(index))) {
            return Preferences.getInstance().getColorSeverity(HighlightColor.ATTRIBUTES);
        } else if (jobTraceEntry.isHighlighted()) {
            return Preferences.getInstance().getColorSeverity(HighlightColor.PROCEDURES);
        }

        return null;
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

        JobTraceEntry jobTraceEntry = (JobTraceEntry)object;

        switch (fieldIdMapping[index]) {
        case ID:
            return jobTraceEntry.getValueForUi(ColumnsDAO.ID);
        case NANOS_SINE_STARTED:
            return jobTraceEntry.getValueForUi(ColumnsDAO.NANOS_SINE_STARTED);
        case TIMESTAMP:
            return jobTraceEntry.getValueForUi(ColumnsDAO.TIMESTAMP);
        case PGM_NAME:
            return jobTraceEntry.getValueForUi(ColumnsDAO.PGM_NAME);
        case PGM_LIB:
            return jobTraceEntry.getValueForUi(ColumnsDAO.PGM_LIB);
        case MOD_NAME:
            return jobTraceEntry.getValueForUi(ColumnsDAO.MOD_NAME);
        case MOD_LIB:
            return jobTraceEntry.getValueForUi(ColumnsDAO.MOD_LIB);
        case HLL_STMT_NBR:
            return jobTraceEntry.getValueForUi(ColumnsDAO.HLL_STMT_NBR);
        case PROC_NAME:
            return jobTraceEntry.getValueForUi(ColumnsDAO.PROC_NAME);
        case CALL_LEVEL:
            return jobTraceEntry.getValueForUi(ColumnsDAO.CALL_LEVEL);
        case EVENT_SUB_TYPE:
            return jobTraceEntry.getValueForUi(ColumnsDAO.EVENT_SUB_TYPE);
        case CALLER_HLL_STMT_NBR:
            return jobTraceEntry.getValueForUi(ColumnsDAO.CALLER_HLL_STMT_NBR);
        case CALLER_PROC_NAME:
            return jobTraceEntry.getValueForUi(ColumnsDAO.CALLER_PROC_NAME);
        case CALLER_CALL_LEVEL:
            return jobTraceEntry.getValueForUi(ColumnsDAO.CALLER_CALL_LEVEL);
        default:
            break;
        }

        return null;
    }

    public void setColumnColor(String columnName, Color color) {

        if (columns == null || columnName == null) {
            return;
        }

        for (JobTraceEntryColumn column : columns) {
            if (columnName.equals(column.getName())) {
                column.setColor(color);
                return;
            }
        }

    }
}
