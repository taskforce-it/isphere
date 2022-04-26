/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.contentproviders;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import biz.isphere.journalexplorer.core.model.JournalEntries;

public class JournalViewerContentProvider implements ILazyContentProvider {

    private JournalEntries inputData;
    private TableViewer viewer;

    public JournalViewerContentProvider(TableViewer viewer) {
        this.viewer = viewer;
    }

    public void dispose() {
    }

    @SuppressWarnings("unchecked")
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

        if (newInput != null) {
            inputData = (JournalEntries)newInput;
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

    public JournalEntries getInput() {
        return inputData;
    }
}
