/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor.tableviewer.filters;

import org.eclipse.jface.viewers.Viewer;

import biz.isphere.joblogexplorer.model.JobLogMessage;

public class NativeSQLFilter implements IMessagePropertyFilter {

    private String whereClause;

    public NativeSQLFilter(String whereClause) {
        this.whereClause = whereClause;
    }

    public void setValue(String whereClause) {
        this.whereClause = whereClause;
    }

    public boolean xselect(Viewer tableViewer, Object parentElement, JobLogMessage element) {
        throw new IllegalAccessError("Method xselect() should not be called."); //$NON-NLS-1$
    }

    public String getWhereClause() {
        return whereClause;
    }

}
