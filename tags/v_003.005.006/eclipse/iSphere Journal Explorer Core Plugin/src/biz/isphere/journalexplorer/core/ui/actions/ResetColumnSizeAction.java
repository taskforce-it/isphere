/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.ui.widgets.AbstractJournalEntriesViewer;

public class ResetColumnSizeAction extends Action {

    private static final String IMAGE = ISphereJournalExplorerCorePlugin.IMAGE_RESET_COLUMN_SIZE;

    private Shell shell;
    private AbstractJournalEntriesViewer viewer;

    public ResetColumnSizeAction(Shell shell) {
        super(Messages.JournalExplorerView_ResetColumnSize);

        this.shell = shell;

        setToolTipText(Messages.JournalExplorerView_ResetColumnSize_Tooltip);
        setImageDescriptor(ISphereJournalExplorerCorePlugin.getDefault().getImageDescriptor(IMAGE));
    }

    public Image getImage() {
        return ISphereJournalExplorerCorePlugin.getDefault().getImage(IMAGE);
    }

    public void setViewer(AbstractJournalEntriesViewer viewer) {
        this.viewer = viewer;
    }

    @Override
    public void run() {

        if (viewer == null) {
            return;
        }

        viewer.resetColumnSizes();
    }
}
