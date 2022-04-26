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

public abstract class AbstractMessagePropertyFilter implements IMessagePropertyFilter {

    public static final String UI_SPCVAL_ALL = "*ALL"; //$NON-NLS-1$
    public static final String UI_SPCVAL_BLANK = "*BLANK"; //$NON-NLS-1$

    public static final String NEGATED_MARKER = "!"; //$NON-NLS-1$

    protected String fieldName;
    protected String value;
    protected boolean negated;

    public AbstractMessagePropertyFilter(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setValue(String value) {

        if (value.startsWith(NEGATED_MARKER)) {
            this.value = value.substring(1);
            this.negated = true;
        } else {
            this.value = value;
            this.negated = false;
        }
    }

    public boolean xselect(Viewer tableViewer, Object parentElement, JobLogMessage element) {
        return applyNegatedAttribute(doSelect(tableViewer, parentElement, element));
    }

    protected boolean doSelect(Viewer tableViewer, Object parentElement, JobLogMessage element) {

        if (UI_SPCVAL_ALL.equals(value)) {
            return true;
        }

        return false;
    }

    protected boolean applyNegatedAttribute(boolean isSelected) {

        if (negated) {
            return !isSelected;
        } else {
            return isSelected;
        }
    }
}
