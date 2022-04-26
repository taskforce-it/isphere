/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
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

public class DisplayChangedFieldsOnlyAction extends Action {

    private static final String IMAGE = ISphereJournalExplorerCorePlugin.IMAGE_CHANGES_ONLY;

    private Shell shell;

    public DisplayChangedFieldsOnlyAction(Shell shell) {
        super(Messages.JournalEntryView_DisplayChangedFieldsOnly);

        this.shell = shell;

        setChecked(false);
        setImageDescriptor(ISphereJournalExplorerCorePlugin.getDefault().getImageDescriptor(IMAGE));
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    public Image getImage() {
        return ISphereJournalExplorerCorePlugin.getDefault().getImage(IMAGE);
    }
}
