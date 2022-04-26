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

public class TextFilter implements IMessagePropertyFilter {

    private static final String NEGATED_MARKER = AbstractMessagePropertyFilter.NEGATED_MARKER;

    private String text;
    protected boolean negated;

    public TextFilter(String text) {

        if (text.startsWith(NEGATED_MARKER)) {
            this.text = text.substring(1);
            this.negated = true;
        } else {
            this.text = text;
            this.negated = false;
        }
    }

    public boolean xselect(Viewer tableViewer, Object parentElement, JobLogMessage element) {
        return applyNegatedAttribute(doSelect(tableViewer, parentElement, element));
    }

    protected boolean doSelect(Viewer tableViewer, Object parentElement, JobLogMessage element) {

        if (element == null || element.getText() == null) {
            return true;
        }

        return element.getLowerCaseText().indexOf(text) >= 0;
    }

    protected boolean applyNegatedAttribute(boolean isSelected) {

        if (negated) {
            return !isSelected;
        } else {
            return isSelected;
        }
    }
}
