/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor.tableviewer.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import biz.isphere.joblogexplorer.model.JobLogMessage;

public class StringFilter extends ViewerFilter {

    public static final String SPCVAL_ALL = "*ALL"; //$NON-NLS-1$

    private String value;

    public void setType(String value) {
        this.value = value;
    }

    @Override
    public boolean select(Viewer tableViewer, Object parentElement, Object element) {

        if (SPCVAL_ALL.equals(value)) {
            return true;
        }

        if (element instanceof JobLogMessage) {
            JobLogMessage jobLogMessage = (JobLogMessage)element;
            return value.equalsIgnoreCase(jobLogMessage.getType());
        }

        return true;
    }

}
