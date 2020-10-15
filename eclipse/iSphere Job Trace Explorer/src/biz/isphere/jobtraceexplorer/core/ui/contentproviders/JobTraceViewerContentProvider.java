/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.contentproviders;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import biz.isphere.jobtraceexplorer.core.model.JobTraceEntries;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;

public class JobTraceViewerContentProvider implements ILazyContentProvider {

    private JobTraceEntries inputData;
    private TableViewer viewer;

    public JobTraceViewerContentProvider(TableViewer viewer) {
        this.viewer = viewer;
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

        if (newInput != null) {
            inputData = (JobTraceEntries)newInput;
        } else {
            inputData = null;
        }
    }

    public void updateElement(int index) {

        if (getInput() == null || getInput().size() < index + 1) {
            return;
        }

        viewer.replace(inputData.getItem(index), index);
    }

    public JobTraceEntries getInput() {
        return inputData;
    }

    public JobTraceEntry getElementAt(int index) {

        if (index >= 0 && index < inputData.size()) {
            return inputData.getItem(index);
        }

        return null;
    }
}
