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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;
import biz.isphere.joblogexplorer.model.JobLogMessage;
import biz.isphere.joblogexplorer.preferences.Preferences;
import biz.isphere.joblogexplorer.preferences.SeverityColor;

public class JobLogExplorerBackgroundProvider implements Listener {

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

    public JobLogExplorerBackgroundProvider(TableViewer tableViewer) {

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

    public void handleEvent(Event event) {

        if ((event.detail & SWT.SELECTED) != 0) {
            return;
        }

        if (event.index != 0) {
            return;
        }

        TableItem item = (TableItem)event.item;
        Object element = item.getData();

        Color background = getBackground(element);
        item.setBackground(background);
    }

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

        return null;
    }

    private class UpdateTableViewerJob extends UIJob {

        public UpdateTableViewerJob() {
            super("");
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor arg0) {
            initializeColors();
            tableViewer.getTable().redraw();
            return Status.OK_STATUS;
        }
    }
}
