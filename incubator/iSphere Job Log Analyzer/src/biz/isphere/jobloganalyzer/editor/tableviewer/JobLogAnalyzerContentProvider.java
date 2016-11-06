/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobloganalyzer.editor.tableviewer;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import biz.isphere.jobloganalyzer.model.JobLog;
import biz.isphere.jobloganalyzer.model.JobLogMessage;

public class JobLogAnalyzerContentProvider implements IStructuredContentProvider {

    private JobLog jobLog;

    public void inputChanged(Viewer v, Object oldInput, Object newInput) {

        // if (newInput != null) ((JobLog)newInput).addChangeListener(this);
        // if (oldInput != null) ((JobLog)oldInput).removeChangeListener(this);

        jobLog = (JobLog)newInput;
    }

    public void dispose() {
    }

    // Return the tasks as an array of Objects
    public JobLogMessage[] getElements(Object parent) {
        JobLogMessage[] messages = jobLog.getMessages().toArray(new JobLogMessage[jobLog.getMessages().size()]);
        return messages;
    }

}
