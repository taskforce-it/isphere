/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobloganalyzer.editor.tableviewer;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import biz.isphere.jobloganalyzer.ISphereJobLogAnalyzerPlugin;
import biz.isphere.jobloganalyzer.model.JobLogMessage;

public class JobLogAnalyzerLabelProvider extends LabelProvider implements ITableLabelProvider, JobLogAnalyzerTableColumns {

    public String getColumnText(Object element, int columnIndex) {

        String result = "";
        JobLogMessage jobLogMessage = (JobLogMessage)element;

        switch (columnIndex) {
        case JobLogAnalyzerTableColumns.COLUMN_SELECTED:
            break;
        case JobLogAnalyzerTableColumns.COLUMN_DATE:
            result = jobLogMessage.getDate();
            break;
        case JobLogAnalyzerTableColumns.COLUMN_TIME:
            result = jobLogMessage.getTime();
            break;
        case JobLogAnalyzerTableColumns.COLUMN_ID:
            result = jobLogMessage.getId();
            break;
        case JobLogAnalyzerTableColumns.COLUMN_TYPE:
            result = jobLogMessage.getType();
            break;
        case JobLogAnalyzerTableColumns.COLUMN_SEVERITY:
            result = jobLogMessage.getSeverity();
            break;
        case JobLogAnalyzerTableColumns.COLUMN_TEXT:
            result = jobLogMessage.getText();
            break;
        case JobLogAnalyzerTableColumns.COLUMN_FROM_LIBRARY:
            result = jobLogMessage.getFromLibrary();
            break;
        case JobLogAnalyzerTableColumns.COLUMN_FROM_PROGRAM:
            result = jobLogMessage.getFromProgram();
            break;
        case JobLogAnalyzerTableColumns.COLUMN_FROM_STATEMENT:
            result = jobLogMessage.getFromStatement();
            break;
        case JobLogAnalyzerTableColumns.COLUMN_TO_LIBRARY:
            result = jobLogMessage.getToLibrary();
            break;
        case JobLogAnalyzerTableColumns.COLUMN_TO_PROGRAM:
            result = jobLogMessage.getToProgram();
            break;
        case JobLogAnalyzerTableColumns.COLUMN_TO_STATEMENT:
            result = jobLogMessage.getToStatement();
            break;
        case JobLogAnalyzerTableColumns.COLUMN_FROM_MODULE:
            result = jobLogMessage.getFromModule();
            break;
        case JobLogAnalyzerTableColumns.COLUMN_TO_MODULE:
            result = jobLogMessage.getToModule();
            break;
        case JobLogAnalyzerTableColumns.COLUMN_FROM_PROCEDURE:
            result = jobLogMessage.getFromProcedure();
            break;
        case JobLogAnalyzerTableColumns.COLUMN_TO_PROCEDURE:
            result = jobLogMessage.getToProcedure();
            break;
        default:
            break;
        }

        return result;
    }

    public Image getColumnImage(Object element, int columnIndex) {

        if (columnIndex != JobLogAnalyzerTableColumns.COLUMN_SELECTED) {
            return null;
        }

        return getImage(((JobLogMessage)element).isSelected());
    }

    private Image getImage(boolean isSelected) {
        String key = isSelected ? ISphereJobLogAnalyzerPlugin.IMAGE_CHECKED : ISphereJobLogAnalyzerPlugin.IMAGE_UNCHECKED;
        return ISphereJobLogAnalyzerPlugin.getDefault().getImage(key);
    }
}
