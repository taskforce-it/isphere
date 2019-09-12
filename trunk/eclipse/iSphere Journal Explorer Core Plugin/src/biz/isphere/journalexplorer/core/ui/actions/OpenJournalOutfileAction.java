/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.OutputFile;
import biz.isphere.journalexplorer.core.ui.dialogs.OpenJournalOutputFileDialog;

public abstract class OpenJournalOutfileAction extends Action {

    private static final String IMAGE = ISphereJournalExplorerCorePlugin.IMAGE_OPEN_JOURNAL_OUTFILE;

    private Shell shell;
    private OutputFile outputFile;
    private String whereClause;

    public OpenJournalOutfileAction(Shell shell) {
        super(Messages.JournalExplorerView_OpenJournal);

        this.shell = shell;

        setImageDescriptor(ISphereJournalExplorerCorePlugin.getDefault().getImageDescriptor(IMAGE));
    }

    public Image getImage() {
        return ISphereJournalExplorerCorePlugin.getDefault().getImage(IMAGE);
    }

    @Override
    public void run() {
        performOpenJournalOutputFile();
        postRunAction();
    }

    public OutputFile getOutputFile() {
        return outputFile;
    }

    public String getWhereClause() {
        return whereClause;
    }

    private void performOpenJournalOutputFile() {

        OpenJournalOutputFileDialog openJournalOutputFileDialog = new OpenJournalOutputFileDialog(shell);
        openJournalOutputFileDialog.create();
        int result = openJournalOutputFileDialog.open();

        outputFile = null;
        whereClause = null;

        if (result == Window.OK) {
            if (ISphereHelper.checkISphereLibrary(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                openJournalOutputFileDialog.getConnectionName())) {
                outputFile = new OutputFile(openJournalOutputFileDialog.getConnectionName(), openJournalOutputFileDialog.getLibrary(),
                    openJournalOutputFileDialog.getFileName(), openJournalOutputFileDialog.getMemberName());
                whereClause = openJournalOutputFileDialog.getSqlWhere();
            }
        }
    }

    protected abstract void postRunAction();
}
