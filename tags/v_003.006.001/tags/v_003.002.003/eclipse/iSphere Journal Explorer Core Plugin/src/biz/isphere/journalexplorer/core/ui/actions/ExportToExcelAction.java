/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.FileHelper;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.export.ExcelExporter;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumn;

public class ExportToExcelAction extends Action {

    private static final String IMAGE = ISphereJournalExplorerCorePlugin.IMAGE_EXCEL;

    private Shell shell;
    private JournalEntry[] selectedItems;
    private JournalEntryColumn[] columns;

    public ExportToExcelAction(Shell shell) {
        super(Messages.JournalExplorerView_Export_to_Excel);

        this.shell = shell;

        setToolTipText(Messages.JournalExplorerView_Export_to_Excel_Tooltip);
        setImageDescriptor(ISphereJournalExplorerCorePlugin.getDefault().getImageDescriptor(IMAGE));
    }

    public Image getImage() {
        return ISphereJournalExplorerCorePlugin.getDefault().getImage(IMAGE);
    }

    public void setSelectedItems(StructuredSelection selection) {

        List<JournalEntry> selectedItems = new ArrayList<JournalEntry>();
        for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
            Object object = iterator.next();
            if (object instanceof JournalEntry) {
                JournalEntry journalEntry = (JournalEntry)object;
                selectedItems.add(journalEntry);
            } else if (object instanceof JournalProperties) {
                JournalEntry journalEntry = ((JournalProperties)object).getJournalEntry();
                selectedItems.add(journalEntry);
            }
        }

        this.selectedItems = selectedItems.toArray(new JournalEntry[selectedItems.size()]);
    }

    public void setSelectedItems(JournalEntry[] selectedItems) {
        this.selectedItems = selectedItems;
    }

    public void setColumns(JournalEntryColumn[] columns) {
        this.columns = columns;
    }

    @Override
    public void run() {
        performExportToExcel();
    }

    private void performExportToExcel() {

        IFileDialog dialog = WidgetFactory.getFileDialog(shell, SWT.SAVE);
        dialog.setFilterNames(new String[] { "Excel Files", FileHelper.getAllFilesText() }); //$NON-NLS-1$ //$NON-NLS-2$
        dialog.setFilterExtensions(new String[] { "*.xls", FileHelper.getAllFilesFilter() }); //$NON-NLS-1$ //$NON-NLS-2$
        dialog.setFilterPath(Preferences.getInstance().getExportPath());
        dialog.setFileName(Preferences.getInstance().getExportFile()); //$NON-NLS-1$
        dialog.setOverwrite(true);
        final String exportPath = dialog.open();
        if (exportPath == null) {
            return;
        }

        Preferences.getInstance().setExportPath(dialog.getFilterPath());
        Preferences.getInstance().setExportFile(FileHelper.getFileName(exportPath));

        if (columns == null) {
            return;
        }

        Job exportJob = new Job(Messages.Exporting_to_Excel) {

            @Override
            protected IStatus run(IProgressMonitor arg0) {

                UIJob displayResultJob = null;

                try {

                    ExcelExporter exporter = new ExcelExporter();
                    exporter.export(shell, columns, selectedItems, exportPath);

                    displayResultJob = new UIJob(Messages.Display_Export_Result) {
                        @Override
                        public IStatus runInUIThread(IProgressMonitor arg0) {
                            MessageDialog.openInformation(shell, Messages.Information,
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
