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

import biz.isphere.core.ISpherePlugin;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.externalapi.Access;

public class LoadJournalEntriesFromJsonFileAction extends Action {

    private static final String IMAGE = ISphereJournalExplorerCorePlugin.IMAGE_JSON;

    private Shell shell;

    public LoadJournalEntriesFromJsonFileAction(Shell shell) {
        super(Messages.JournalExplorerView_Import_from_Json);

        this.shell = shell;

        setToolTipText(Messages.JournalExplorerView_Import_from_Json_Tooltip);
    }

    public Image getImage() {
        return ISphereJournalExplorerCorePlugin.getDefault().getImage(IMAGE);
    }

    @Override
    public void run() {
        performImportFromJson();
    }

    private void performImportFromJson() {

        try {
            Access.loadJournalEntriesFromJsonFile(shell);
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not open journal exploer view ***", e);
        }
    }
}
