/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;

import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;

public class CollapseAllAction extends Action {

    private static final String IMAGE = ISphereJournalExplorerCorePlugin.IMAGE_COLLAPSE_ALL;

    private TreeViewer treeViewer;

    public CollapseAllAction(TreeViewer treeViewer) {
        super(Messages.JournalEntryView_CollapseAll);

        this.treeViewer = treeViewer;

        setImageDescriptor(ISphereJournalExplorerCorePlugin.getDefault().getImageDescriptor(IMAGE));
    }

    public Image getImage() {
        return ISphereJournalExplorerCorePlugin.getDefault().getImage(IMAGE);
    }

    @Override
    public void run() {
        performCollapseAll(treeViewer);
    }

    protected void performCollapseAll(TreeViewer treeViewer) {

        treeViewer.collapseAll();
    }
}
