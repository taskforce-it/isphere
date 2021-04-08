/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.externalapi.Access;

public class LoadJournalEntriesFromOutputFileAction extends Action {

    private static final String IMAGE = ISphereJournalExplorerCorePlugin.IMAGE_OPEN_JOURNAL_OUTFILE;

    private Shell shell;

    public LoadJournalEntriesFromOutputFileAction(Shell shell) {

        this.shell = shell;

        setText(Messages.JournalExplorerView_OpenJournal);
        setImageDescriptor(ISphereJournalExplorerCorePlugin.getDefault().getImageDescriptor(IMAGE));
    }

    @Override
    public void run() {
        performOpenJournalOutputFile();
    }

    private void performOpenJournalOutputFile() {

        try {
            Access.openJournalEntriesFromOutputFile(shell, true);
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not open journal exploer view ***", e);
        }
    }
}
