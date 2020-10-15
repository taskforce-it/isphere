/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.FileHelper;
import biz.isphere.core.json.JsonExporter;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;
import biz.isphere.jobtraceexplorer.core.ISphereJobTraceExplorerCorePlugin;
import biz.isphere.jobtraceexplorer.core.Messages;
import biz.isphere.jobtraceexplorer.core.model.JobTraceSession;
import biz.isphere.jobtraceexplorer.core.preferences.Preferences;

public class SaveJobTraceEntriesAction extends Action {

    private static final String IMAGE = ISphereJobTraceExplorerCorePlugin.IMAGE_JSON;

    private Shell shell;
    private JobTraceSession selectedItems;

    public SaveJobTraceEntriesAction(Shell shell) {
        super(Messages.JobTraceExplorerView_Export_to_Json);

        this.shell = shell;

        setImageDescriptor(ISphereJobTraceExplorerCorePlugin.getDefault().getImageDescriptor(IMAGE));
    }

    public Image getImage() {
        return ISphereJobTraceExplorerCorePlugin.getDefault().getImage(IMAGE);
    }

    public void setSelectedItems(JobTraceSession selectedItems) {
        this.selectedItems = selectedItems;
    }

    @Override
    public void run() {
        performExportToJson();
    }

    private void performExportToJson() {

        IFileDialog dialog = WidgetFactory.getFileDialog(shell, SWT.SAVE);
        dialog.setFilterNames(new String[] { "Json Files", FileHelper.getAllFilesText() }); //$NON-NLS-1$ 
        dialog.setFilterExtensions(new String[] { "*.json", FileHelper.getAllFilesFilter() }); //$NON-NLS-1$ 
        dialog.setFilterPath(Preferences.getInstance().getExportPath());
        dialog.setFileName(Preferences.getInstance().getExportFileJson());
        dialog.setOverwrite(true);
        final String exportPath = dialog.open();
        if (exportPath == null) {
            return;
        }

        Preferences.getInstance().setExportPath(dialog.getFilterPath());
        Preferences.getInstance().setExportFileJson(FileHelper.getFileName(exportPath));

        Job exportJob = new Job(Messages.Status_Exporting_to_Json) {

            @Override
            protected IStatus run(IProgressMonitor arg0) {

                UIJob displayResultJob = null;

                try {

                    JsonExporter exporter = new JsonExporter();
                    exporter.execute(shell, selectedItems, exportPath);

                    displayResultJob = new UIJob(Messages.Display_Export_Result) {
                        @Override
                        public IStatus runInUIThread(IProgressMonitor arg0) {
                            MessageDialog.openInformation(shell, Messages.MessageDialog_Information,
                                Messages.bind(Messages.Finished_exporting_data_to_file_A, exportPath));
                            return Status.OK_STATUS;
                        }
                    };

                } catch (Exception e) {
                    final String message = ExceptionHelper.getLocalizedMessage(e);
                    displayResultJob = new UIJob(Messages.Display_Export_Result) {
                        @Override
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            MessageDialog.openError(shell, Messages.E_R_R_O_R, message);
                            return Status.OK_STATUS;
                        }
                    };
                }

                if (displayResultJob != null) {
                    displayResultJob.schedule();
                }

                return Status.OK_STATUS;
            }
        };

        exportJob.schedule();
    }
}
