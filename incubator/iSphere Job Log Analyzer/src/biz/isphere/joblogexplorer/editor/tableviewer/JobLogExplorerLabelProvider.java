/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor.tableviewer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;
import biz.isphere.joblogexplorer.model.JobLogMessage;
import biz.isphere.joblogexplorer.preferences.Preferences;
import biz.isphere.joblogexplorer.preferences.SeverityColor;

public class JobLogExplorerLabelProvider extends ColumnLabelProvider implements ITableLabelProvider, JobLogExplorerTableColumns {

    private TableViewer tableViewer;

    private Preferences preferences;
    private boolean isColoring;
    private Color severityColor00;
    private Color severityColor10;
    private Color severityColor20;
    private Color severityColor30;
    private Color severityColor40;

    private UIJob updateTableViewerJob;

    private Object lock1 = new Object();

    public JobLogExplorerLabelProvider(TableViewer tableViewer) {

        this.tableViewer = tableViewer;
        this.preferences = Preferences.getInstance();

        initializeColors();
        registerPropertyChangeListener();
    }

    private void initializeColors() {

        synchronized (lock1) {
            isColoring = preferences.isColoringEnabled();
            
            if (isColoring) {
                severityColor00 = preferences.getColorSeverity(SeverityColor.SEVERITY_00);
                severityColor10 = preferences.getColorSeverity(SeverityColor.SEVERITY_10);
                severityColor20 = preferences.getColorSeverity(SeverityColor.SEVERITY_20);
                severityColor30 = preferences.getColorSeverity(SeverityColor.SEVERITY_30);
                severityColor40 = preferences.getColorSeverity(SeverityColor.SEVERITY_40);
            }
        }
    }

    private void registerPropertyChangeListener() {

        ISphereJobLogExplorerPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                String propertyName = event.getProperty();
                if (propertyName.startsWith("biz.isphere.joblogexplorer.COLORS.")) {
                    if (updateTableViewerJob != null) {
                        updateTableViewerJob.cancel();
                        updateTableViewerJob = null;
                    }
                    updateTableViewerJob = new UpdateTableViewerJob();
                    updateTableViewerJob.schedule(100);
                    /*
                     * Delay update for 100 mSecs to cancel updating the table
                     * viewer, when multiple colors have changed.
                     */
                }
            }
        });
    }

    public String getColumnText(Object element, int columnIndex) {

        String result = ""; //$NON-NLS-1$
        JobLogMessage jobLogMessage = (JobLogMessage)element;

        switch (columnIndex) {
        case JobLogExplorerTableColumns.COLUMN_SELECTED:
            break;
        case JobLogExplorerTableColumns.COLUMN_DATE:
            result = jobLogMessage.getDate();
            break;
        case JobLogExplorerTableColumns.COLUMN_TIME:
            result = jobLogMessage.getTime();
            break;
        case JobLogExplorerTableColumns.COLUMN_ID:
            result = jobLogMessage.getId();
            break;
        case JobLogExplorerTableColumns.COLUMN_TYPE:
            result = jobLogMessage.getType();
            break;
        case JobLogExplorerTableColumns.COLUMN_SEVERITY:
            result = jobLogMessage.getSeverity();
            break;
        case JobLogExplorerTableColumns.COLUMN_TEXT:
            result = jobLogMessage.getText();
            break;
        case JobLogExplorerTableColumns.COLUMN_FROM_LIBRARY:
            result = jobLogMessage.getFromLibrary();
            break;
        case JobLogExplorerTableColumns.COLUMN_FROM_PROGRAM:
            result = jobLogMessage.getFromProgram();
            break;
        case JobLogExplorerTableColumns.COLUMN_FROM_STATEMENT:
            result = jobLogMessage.getFromStatement();
            break;
        case JobLogExplorerTableColumns.COLUMN_TO_LIBRARY:
            result = jobLogMessage.getToLibrary();
            break;
        case JobLogExplorerTableColumns.COLUMN_TO_PROGRAM:
            result = jobLogMessage.getToProgram();
            break;
        case JobLogExplorerTableColumns.COLUMN_TO_STATEMENT:
            result = jobLogMessage.getToStatement();
            break;
        case JobLogExplorerTableColumns.COLUMN_FROM_MODULE:
            result = jobLogMessage.getFromModule();
            break;
        case JobLogExplorerTableColumns.COLUMN_TO_MODULE:
            result = jobLogMessage.getToModule();
            break;
        case JobLogExplorerTableColumns.COLUMN_FROM_PROCEDURE:
            result = jobLogMessage.getFromProcedure();
            break;
        case JobLogExplorerTableColumns.COLUMN_TO_PROCEDURE:
            result = jobLogMessage.getToProcedure();
            break;
        default:
            break;
        }

        return result;
    }

    public Image getColumnImage(Object element, int columnIndex) {

        if (columnIndex != JobLogExplorerTableColumns.COLUMN_SELECTED) {
            return null;
        }

        return getImage(((JobLogMessage)element).isSelected());
    }

    private Image getImage(boolean isSelected) {
        String key = isSelected ? ISphereJobLogExplorerPlugin.IMAGE_CHECKED : ISphereJobLogExplorerPlugin.IMAGE_UNCHECKED;
        return ISphereJobLogExplorerPlugin.getDefault().getImage(key);
    }

    @Override
    public Color getBackground(Object element) {

        if (isColoring && element instanceof JobLogMessage) {

            JobLogMessage jobLogMessage = (JobLogMessage)element;
            int severity = jobLogMessage.getSeverityInt();
            if (severity >= 40) {
                return severityColor40;
            } else if (severity >= 30) {
                return severityColor30;
            } else if (severity >= 20) {
                return severityColor20;
            } else if (severity >= 10) {
                return severityColor10;
            } else {
                return severityColor00;
            }
        }

        return super.getBackground(element);
    }

    private class UpdateTableViewerJob extends UIJob {

        public UpdateTableViewerJob() {
            super("");
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor arg0) {
            initializeColors();
            tableViewer.refresh(true);
            return Status.OK_STATUS;
        }
    }
}
