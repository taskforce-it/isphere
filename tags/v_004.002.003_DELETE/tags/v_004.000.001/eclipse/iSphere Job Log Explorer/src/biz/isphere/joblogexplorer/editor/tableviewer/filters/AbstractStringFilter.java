/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor.tableviewer.filters;

import org.eclipse.jface.viewers.Viewer;

import biz.isphere.joblogexplorer.model.JobLogMessage;

public abstract class AbstractStringFilter extends AbstractMessagePropertyFilter {

    public AbstractStringFilter(String fieldName) {
        super(fieldName);
    }

    public void setValue(String value) {
        super.setValue(value);
    }

    @Override
    protected boolean doSelect(Viewer tableViewer, Object parentElement, JobLogMessage element) {

        if (super.doSelect(tableViewer, parentElement, element)) {
            return true;
        }

        JobLogMessage jobLogMessage = (JobLogMessage)element;
        String currentValue = getCurrentValue(jobLogMessage);
        if (currentValue == null || currentValue.length() == 0) {
            currentValue = UI_SPCVAL_BLANK;
        }
        return value.equalsIgnoreCase(currentValue);
    }

    public String getWhereClause() {

        if (negated) {
            return fieldName + " <> '" + value + "'";
        } else {
            return fieldName + " = '" + value + "'";
        }
    }

    protected abstract String getCurrentValue(JobLogMessage jobLogMessage);
}
