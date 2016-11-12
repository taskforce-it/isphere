/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor.tableviewer;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;
import biz.isphere.joblogexplorer.model.JobLogMessage;

public class JobLogExplorerLabelProvider extends LabelProvider implements ITableLabelProvider, JobLogExplorerTableColumns {

    public String getColumnText(Object element, int columnIndex) {

        String result = ""; //$NON-NLS-1$
        JobLogMessage jobLogMessage = (JobLogMessage)element;

        switch (columnIndex) {
        case JobLogExplorerTableColumns.COLUMN_SELECTED:
            break;
        case JobLogExplorerTableColumns.COLUMN_DATE:
            result = jobLogMessage.getDate();
            break;
        case JobLogExplorerTableColumns.COLUMN_TIME:
            result = jobLogMessage.getTime();
            break;
        case JobLogExplorerTableColumns.COLUMN_ID:
            result = jobLogMessage.getId();
            break;
        case JobLogExplorerTableColumns.COLUMN_TYPE:
            result = jobLogMessage.getType();
            break;
        case JobLogExplorerTableColumns.COLUMN_SEVERITY:
            result = jobLogMessage.getSeverity();
            break;
        case JobLogExplorerTableColumns.COLUMN_TEXT:
            result = jobLogMessage.getText();
            break;
        case JobLogExplorerTableColumns.COLUMN_FROM_LIBRARY:
            result = jobLogMessage.getFromLibrary();
            break;
        case JobLogExplorerTableColumns.COLUMN_FROM_PROGRAM:
            result = jobLogMessage.getFromProgram();
            break;
        case JobLogExplorerTableColumns.COLUMN_FROM_STATEMENT:
            result = jobLogMessage.getFromStatement();
            break;
        case JobLogExplorerTableColumns.COLUMN_TO_LIBRARY:
            result = jobLogMessage.getToLibrary();
            break;
        case JobLogExplorerTableColumns.COLUMN_TO_PROGRAM:
            result = jobLogMessage.getToProgram();
            break;
        case JobLogExplorerTableColumns.COLUMN_TO_STATEMENT:
            result = jobLogMessage.getToStatement();
            break;
        case JobLogExplorerTableColumns.COLUMN_FROM_MODULE:
            result = jobLogMessage.getFromModule();
            break;
        case JobLogExplorerTableColumns.COLUMN_TO_MODULE:
            result = jobLogMessage.getToModule();
            break;
        case JobLogExplorerTableColumns.COLUMN_FROM_PROCEDURE:
            result = jobLogMessage.getFromProcedure();
            break;
        case JobLogExplorerTableColumns.COLUMN_TO_PROCEDURE:
            result = jobLogMessage.getToProcedure();
            break;
        default:
            break;
        }

        return result;
    }

    public Image getColumnImage(Object element, int columnIndex) {

        if (columnIndex != JobLogExplorerTableColumns.COLUMN_SELECTED) {
            return null;
        }

        return getImage(((JobLogMessage)element).isSelected());
    }

    private Image getImage(boolean isSelected) {
        String key = isSelected ? ISphereJobLogExplorerPlugin.IMAGE_CHECKED : ISphereJobLogExplorerPlugin.IMAGE_UNCHECKED;
        return ISphereJobLogExplorerPlugin.getDefault().getImage(key);
    }
}
