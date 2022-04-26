/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.FileHelper;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;
import biz.isphere.jobtraceexplorer.core.ISphereJobTraceExplorerCorePlugin;
import biz.isphere.jobtraceexplorer.core.Messages;
import biz.isphere.jobtraceexplorer.core.preferences.Preferences;
import biz.isphere.jobtraceexplorer.core.ui.views.JobTraceExplorerView;

public class LoadJobTraceEntriesAction extends Action {

    private static final String IMAGE = ISphereJobTraceExplorerCorePlugin.IMAGE_JSON;

    private Shell shell;
    private JobTraceExplorerView view;

    public LoadJobTraceEntriesAction(Shell shell, JobTraceExplorerView view) {
        super(Messages.JobTraceExplorerView_Import_from_Json);

        this.shell = shell;
        this.view = view;
    }

    public Image getImage() {
        return ISphereJobTraceExplorerCorePlugin.getDefault().getImage(IMAGE);
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
        dialog.setOverwrite(false);
        final String importPath = dialog.open();
        if (importPath == null) {
            return;
        }

        Preferences.getInstance().setExportPath(dialog.getFilterPath());
        Preferences.getInstance().setExportFileJson(FileHelper.getFileName(importPath));

        view.createJobTraceTab(importPath);
    }
}
