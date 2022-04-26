/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor.tableviewer;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;

import biz.isphere.joblogexplorer.model.JobLogMessage;

/**
 * This class implements an ICellModifier. An ICellModifier is called when the
 * user modifes a cell in the tableViewer
 */

public class JobLogExplorerCellModifier implements ICellModifier {

    private List<Object> columnNames;
    private TableViewer tableViewer;

    /**
     * Constructor
     * 
     * @param TableViewerExample an instance of a TableViewerExample
     */
    public JobLogExplorerCellModifier(TableViewer tableViewer) {
        super();

        this.tableViewer = tableViewer;
        this.columnNames = Arrays.asList(tableViewer.getColumnProperties());
    }

    /**
     * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object,
     *      java.lang.String)
     */
    public boolean canModify(Object element, String property) {

        // Find the index of the column
        int columnIndex = columnNames.indexOf(property);

        switch (columnIndex) {
        case Columns.Index.SELECTED:
            return true;
        default:
            return false;
        }
    }

    /**
     * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object,
     *      java.lang.String)
     */
    public Object getValue(Object element, String property) {

        // Find the index of the column
        int columnIndex = columnNames.indexOf(property);

        JobLogMessage jobLogMessage = (JobLogMessage)element;

        switch (columnIndex) {
        case Columns.Index.SELECTED:
            return new Boolean(jobLogMessage.isSelected());
        default:
            return ""; //$NON-NLS-1$
        }
    }

    /**
     * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object,
     *      java.lang.String, java.lang.Object)
     */
    public void modify(Object element, String property, Object value) {

        // Find the index of the column
        int columnIndex = columnNames.indexOf(property);

        TableItem item = (TableItem)element;
        JobLogMessage jobLogMessages = (JobLogMessage)item.getData();

        switch (columnIndex) {
        case Columns.Index.SELECTED:
            jobLogMessages.setSelected(((Boolean)value).booleanValue());
            break;
        default:
        }

        tableViewer.update(jobLogMessages, new String[] { property });
    }
}
