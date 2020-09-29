/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.FileHelper;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.views.JournalExplorerView;

public class LoadJournalEntriesAction extends Action {

    private static final String IMAGE = ISphereJournalExplorerCorePlugin.IMAGE_JSON;

    private Shell shell;
    private JournalExplorerView view;

    public LoadJournalEntriesAction(Shell shell, JournalExplorerView view) {
        super(Messages.JournalExplorerView_Import_from_Json);

        this.shell = shell;
        this.view = view;

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

        IFileDialog dialog = WidgetFactory.getFileDialog(shell, SWT.OPEN);
        dialog.setFilterNames(new String[] { "Json Files", FileHelper.getAllFilesText() }); //$NON-NLS-1$ 
        dialog.setFilterExtensions(new String[] { "*.json", FileHelper.getAllFilesFilter() }); //$NON-NLS-1$ 
        dialog.setFilterPath(Preferences.getInstance().getExportPath());
        dialog.setFileName(Preferences.getInstance().getExportFileJson());
        dialog.setOverwrite(true);
        final String importPath = dialog.open();
        if (importPath == null) {
            return;
        }

        Preferences.getInstance().setExportPath(dialog.getFilterPath());
        Preferences.getInstance().setExportFileJson(FileHelper.getFileName(importPath));

        view.createJournalTab(importPath);
    }
}
