/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.widgets.menues;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import biz.isphere.journalexplorer.core.ui.widgets.actions.CopyJournalPropertyToClipboardAction;
import biz.isphere.journalexplorer.core.ui.widgets.actions.ToggleTrimPropertyValuesAction;

public class JournalEntryMenuAdapter implements IMenuListener {

    private TreeViewer viewer;

    public JournalEntryMenuAdapter(TreeViewer viewer) {
        this.viewer = viewer;
    }

    public TreeViewer getViewer() {
        return viewer;
    }

    public void setViewer(TreeViewer viewer) {
        this.viewer = viewer;
    }

    public void menuAboutToShow(IMenuManager manager) {

        if (viewer.getSelection().isEmpty()) {
            return;
        }

        if (viewer.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
            manager.add(new CopyJournalPropertyToClipboardAction(selection, true));
            manager.add(new CopyJournalPropertyToClipboardAction(selection, false));
            manager.add(new ToggleTrimPropertyValuesAction());
        }
    }
}
