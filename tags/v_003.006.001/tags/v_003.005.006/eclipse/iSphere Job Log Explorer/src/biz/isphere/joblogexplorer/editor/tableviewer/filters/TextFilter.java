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

public class TextFilter extends AbstractStringFilter {

    private static final String NEGATED_MARKER = AbstractMessagePropertyFilter.NEGATED_MARKER;

    protected boolean negated;

    public TextFilter(String text) {
        super(JobLogMessage.Fields.TEXT.fieldName());

        if (text.startsWith(NEGATED_MARKER)) {
            setValue(text.substring(1).toLowerCase());
            this.negated = true;
        } else {
            setValue(text.toLowerCase());
            this.negated = false;
        }
    }

    protected boolean doSelect(Viewer tableViewer, Object parentElement, JobLogMessage element) {

        JobLogMessage jobLogMessage = (JobLogMessage)element;
        if (jobLogMessage == null || getCurrentValue(jobLogMessage) == null) {
            return true;
        }

        String currentValue = getCurrentValue(jobLogMessage);

        return currentValue.indexOf(value) >= 0;
    }

    @Override
    protected String getCurrentValue(JobLogMessage jobLogMessage) {
        return jobLogMessage.getLowerCaseText();
    }

    public String getWhereClause() {

        if (negated) {
            return "Lower(" + fieldName + ") NOT LIKE '%" + value + "%'";
        } else {
            return "Lower(" + fieldName + ") LIKE '%" + value + "%'";
        }
    }
}
