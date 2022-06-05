/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.model;

import org.eclipse.swt.graphics.Color;

import biz.isphere.jobtraceexplorer.core.ui.labelproviders.JobTraceEntryLabelProvider;
import biz.isphere.jobtraceexplorer.core.ui.widgets.JobTraceEntriesSQLViewerTab;

/**
 * This class represents a column of the "Job Trace Entries Viewer" widget. It
 * defines the properties of the column, such as column heading, color, width.
 * 
 * @see JobTraceEntriesSQLViewerTab
 * @see JobTraceEntryLabelProvider
 */
public class JobTraceEntryColumn {

    private JobTraceEntryColumnUI columnDef;

    private boolean resizable;
    private boolean moveable;
    private Color color;

    public JobTraceEntryColumn(JobTraceEntryColumnUI columnDef) {

        this.columnDef = columnDef;

        this.resizable = true;
        this.moveable = true;
        this.color = null;

    }

    public JobTraceEntryColumnUI getColumnDef() {
        return columnDef;
    }

    public String getName() {
        return this.columnDef.columnName();
    }

    public int getStyle() {
        return columnDef.style();
    }

    public int getColumnIndex() {
        return columnDef.columnIndex();
    }

    public String getColumnHeading() {
        return notNull(columnDef.columnText());
    }

    public String getTooltipText() {
        return notNull(columnDef.columnTooltip());
    }

    public int getWidth() {
        return columnDef.width();
    }

    public boolean isResizable() {
        return resizable;
    }

    public boolean isMovebale() {
        return moveable;
    }

    private String notNull(String text) {

        if (text == null) {
            return ""; //$NON-NLS-1$
        }

        return text;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return columnDef.name() + " (" + super.toString() + ")";
    }
}
