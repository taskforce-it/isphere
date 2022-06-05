/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.contentproviders;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;
import biz.isphere.jobtraceexplorer.core.model.JobTraceSession;

public class JobTraceViewerContentProvider implements ILazyContentProvider {

    private JobTraceSession inputData;
    private TableViewer viewer;

    public JobTraceViewerContentProvider(TableViewer viewer) {
        this.viewer = viewer;
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

        if (newInput != null) {
            inputData = (JobTraceSession)newInput;
        } else {
            inputData = null;
        }
    }

    public void updateElement(int index) {

        if (getInput() == null || getInput().getJobTraceEntries().size() < index + 1) {
            return;
        }

        viewer.replace(inputData.getJobTraceEntries().getItem(index), index);
    }

    public JobTraceSession getInput() {
        return inputData;
    }

    public JobTraceEntry getElementAt(int index) {

        if (index >= 0 && index < inputData.getJobTraceEntries().size()) {
            return inputData.getJobTraceEntries().getItem(index);
        }

        return null;
    }
}
