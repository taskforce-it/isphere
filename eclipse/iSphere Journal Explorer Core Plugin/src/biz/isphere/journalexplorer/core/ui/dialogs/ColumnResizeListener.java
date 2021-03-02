/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.dialogs;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.TreeColumn;

import biz.isphere.journalexplorer.core.ui.widgets.JournalEntryDetailsViewer;

public class ColumnResizeListener extends ControlAdapter {

    private JournalEntryDetailsViewer viewer;
    private JournalEntryDetailsViewer connectedViewer;

    private boolean isActive;

    public ColumnResizeListener(JournalEntryDetailsViewer viewer, JournalEntryDetailsViewer connectedViewer) {
        this.viewer = viewer;
        this.connectedViewer = connectedViewer;
        this.isActive = false;
    }

    @Override
    public void controlResized(ControlEvent e) {

        if (isActive) {
            return;
        }

        try {
            isActive = true;
            if (e.widget instanceof TreeColumn) {
                TreeColumn column = (TreeColumn)e.widget;
                connectedViewer.setConnectedColumnWidth(column);
            }

        } finally {
            isActive = false;
        }
    }
}
