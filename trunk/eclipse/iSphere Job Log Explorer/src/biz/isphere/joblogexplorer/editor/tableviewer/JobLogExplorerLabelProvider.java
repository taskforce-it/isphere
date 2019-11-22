/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
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
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;
import biz.isphere.joblogexplorer.model.JobLogMessage;
import biz.isphere.joblogexplorer.preferences.Preferences;
import biz.isphere.joblogexplorer.preferences.SeverityColor;

public class JobLogExplorerLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {

    private TableViewer tableViewer;

    private Preferences preferences;
    private boolean isColoring;
    private Color severityColorBL;
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
                severityColorBL = preferences.getColorSeverity(SeverityColor.SEVERITY_BL);
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
            public void propertyChange(PropertyChangeEvent event) {
                String propertyName = event.getProperty();
                if (propertyName.startsWith("biz.isphere.joblogexplorer.COLORS.")) { //$NON-NLS-1$
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
        case Columns.Index.SELECTED:
            break;
        case Columns.Index.DATE:
            result = jobLogMessage.getDate();
            break;
        case Columns.Index.TIME:
            result = jobLogMessage.getTime();
            break;
        case Columns.Index.ID:
            result = jobLogMessage.getId();
            break;
        case Columns.Index.TYPE:
            result = jobLogMessage.getType();
            break;
        case Columns.Index.SEVERITY:
            result = jobLogMessage.getSeverity();
            break;
        case Columns.Index.TEXT:
            result = jobLogMessage.getText();
            break;
        case Columns.Index.FROM_LIBRARY:
            result = jobLogMessage.getFromLibrary();
            break;
        case Columns.Index.FROM_PROGRAM:
            result = jobLogMessage.getFromProgram();
            break;
        case Columns.Index.FROM_STATEMENT:
            result = jobLogMessage.getFromStatement();
            break;
        case Columns.Index.TO_LIBRARY:
            result = jobLogMessage.getToLibrary();
            break;
        case Columns.Index.TO_PROGRAM:
            result = jobLogMessage.getToProgram();
            break;
        case Columns.Index.TO_STATEMENT:
            result = jobLogMessage.getToStatement();
            break;
        case Columns.Index.FROM_MODULE:
            result = jobLogMessage.getFromModule();
            break;
        case Columns.Index.TO_MODULE:
            result = jobLogMessage.getToModule();
            break;
        case Columns.Index.FROM_PROCEDURE:
            result = jobLogMessage.getFromProcedure();
            break;
        case Columns.Index.TO_PROCEDURE:
            result = jobLogMessage.getToProcedure();
            break;
        default:
            break;
        }

        return result;
    }

    public Image getColumnImage(Object element, int columnIndex) {

        if (columnIndex != Columns.Index.SELECTED) {
            return null;
        }

        return getImage(((JobLogMessage)element).isSelected());
    }

    private Image getImage(boolean isSelected) {
        String key = isSelected ? ISphereJobLogExplorerPlugin.IMAGE_CHECKED : ISphereJobLogExplorerPlugin.IMAGE_UNCHECKED;
        return ISphereJobLogExplorerPlugin.getDefault().getImage(key);
    }

    public Color getBackground(Object element, int columnIndex) {

        if (isColoring && element instanceof JobLogMessage) {

            JobLogMessage jobLogMessage = (JobLogMessage)element;
            int severity = jobLogMessage.getSeverityInt();
            if (severity == JobLogMessage.SEVERITY_BLANK) {
                return severityColorBL;
            } else if (severity >= 40) {
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

        return null;
    }

    public Color getForeground(Object arg0, int arg1) {
        return null;
    }

    private class UpdateTableViewerJob extends UIJob {

        public UpdateTableViewerJob() {
            super(""); //$NON-NLS-1$
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor arg0) {
            initializeColors();
            tableViewer.refresh(true);
            tableViewer.getTable().redraw();
            return Status.OK_STATUS;
        }
    }
}
