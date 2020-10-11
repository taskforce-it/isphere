/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.widgets;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.jobtraceexplorer.core.Messages;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntries;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;
import biz.isphere.jobtraceexplorer.core.model.JobTraceSession;
import biz.isphere.jobtraceexplorer.core.model.api.IBMiMessage;
import biz.isphere.jobtraceexplorer.core.model.dao.JobTraceDAO;
import biz.isphere.jobtraceexplorer.core.ui.model.JobTraceViewerFactory;
import biz.isphere.jobtraceexplorer.core.ui.views.JobTraceExplorerView;

/**
 * This widget is a viewer for the job trace entries loaded from a job trace
 * session. It is used by the "Job Trace Explorer" view when creating a tab for
 * retrieved job trace entries.
 * 
 * @see JobTraceEntry
 * @see JobTraceEntryViewerView
 */
public class JobTraceEntriesViewerTab extends AbstractJobTraceEntriesViewerTab {

    private TableViewer tableViewer;
    private JobTraceSession jobTraceSession;

    public JobTraceEntriesViewerTab(CTabFolder parent, JobTraceSession jobTraceSession, String whereClause,
        SelectionListener loadJobTraceEntriesSelectionListener) {
        super(parent, null, loadJobTraceEntriesSelectionListener);

        this.jobTraceSession = jobTraceSession;

        setSelectClause(null);
        setSqlEditorVisibility(false);

        initializeComponents();
    }

    protected String getLabel() {
        return jobTraceSession.toString();
    }

    protected String getTooltip() {
        return jobTraceSession.toString(); //$NON-NLS-1$
    }

    protected TableViewer createTableViewer(Composite container) {

        try {

            tableViewer = new JobTraceViewerFactory().createTableViewer(container, getDialogSettingsManager());
            tableViewer.addSelectionChangedListener(this);
            tableViewer.getTable().setEnabled(false);

            return tableViewer;

        } catch (Exception e) {
            ISpherePlugin.logError("*** Error in method JobTraceEntriesViewerTab.createTableViewer() ***", e);
            MessageDialog.openError(getParent().getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            return null;
        }
    }

    public boolean isLoading() {

        if (tableViewer.getTable().isEnabled()) {
            return false;
        }

        return true;
    }

    public JobTraceEntry getSelectedItem() {

        int index = tableViewer.getTable().getSelectionIndex();
        if (index < 0) {
            return null;
        }

        TableItem tableItem = tableViewer.getTable().getItem(index);
        JobTraceEntry jobTraceEntry = (JobTraceEntry)tableItem.getData();

        return jobTraceEntry;
    }

    public void setSelectedItem(JobTraceEntry jobTraceEntry) {

        if (jobTraceEntry == null) {
            return;
        }

        tableViewer.setSelection(new StructuredSelection(jobTraceEntry));
        int index = tableViewer.getTable().getSelectionIndex();
        if (index >= 0) {
            tableViewer.getTable().setTopIndex(index);
        }
    }

    public void closeJobTraceSession() {
        setInputData(null);
    }

    public void openJobTraceSession(final JobTraceExplorerView view, String whereClause, String filterWhereClause) throws Exception {

        tableViewer.getTable().setEnabled(false);

        setSqlEditorEnabled(false);

        Job loadJobTraceDataJob = new OpenJobTraceSessionJob(view, jobTraceSession, whereClause);
        loadJobTraceDataJob.schedule();
    }

    public void filterJobTraceSession(final JobTraceExplorerView view, String whereClause) throws Exception {

        setSqlEditorEnabled(false);

        Job filterJobTraceDataJob = new FilterJobTraceSessionDataJob(view, whereClause);
        filterJobTraceDataJob.schedule();
    }

    public boolean hasSqlEditor() {
        return true;
    }

    private class OpenJobTraceSessionJob extends Job {

        private JobTraceExplorerView view;
        private JobTraceSession jobTraceSession;
        private String whereClause;
        private String filterWhereClause;

        public OpenJobTraceSessionJob(JobTraceExplorerView view, JobTraceSession jobTraceSession, String whereClause) {
            super(Messages.bind(Messages.Status_Loading_job_trace_entries_of_session_A, jobTraceSession.getQualifiedName()));

            this.view = view;
            this.jobTraceSession = jobTraceSession;
            this.whereClause = whereClause;
            this.filterWhereClause = ""; // filterWhereClause;
        }

        public IStatus run(IProgressMonitor monitor) {

            try {

                JobTraceDAO jobTraceDAO = new JobTraceDAO(jobTraceSession);
                final JobTraceEntries data = jobTraceDAO.load(whereClause, monitor);
                data.applyFilter(filterWhereClause);

                IBMiMessage[] messages = data.getMessages();
                if (messages.length != 0) {
                    throw new Exception("*** Error loading job trace entries. *** \n" + messages[0].getID() + ": " + messages[0].getText()); //$NON-NLS-1$ //$NON-NLS-2$
                }

                if (!isDisposed()) {
                    getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            setInputData(data);
                            view.finishDataLoading(JobTraceEntriesViewerTab.this, false);
                        }
                    });
                }

            } catch (Throwable e) {

                if (!isDisposed()) {
                    final Throwable e1 = e;
                    getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            setSqlEditorEnabled(true);
                            setFocusOnSqlEditor();
                            view.handleDataLoadException(JobTraceEntriesViewerTab.this, e1);
                        }
                    });
                }

            }

            return Status.OK_STATUS;
        }

    };

    private class FilterJobTraceSessionDataJob extends Job {

        private JobTraceExplorerView view;
        private String whereClause;

        public FilterJobTraceSessionDataJob(JobTraceExplorerView view, String whereClause) {
            super(Messages.Status_Loading_job_trace_entries_of_session_A);

            this.view = view;
            this.whereClause = whereClause;
        }

        public IStatus run(IProgressMonitor monitor) {

            final JobTraceEntries data = getInput();

            try {

                data.applyFilter(whereClause);

                if (!isDisposed()) {
                    getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            setInputData(data);
                            setSqlEditorEnabled(true);
                            setFocusOnSqlEditor();
                            view.finishDataLoading(JobTraceEntriesViewerTab.this, true);
                        }
                    });
                }

            } catch (Throwable e) {

                if (!isDisposed()) {
                    final Throwable e1 = e;
                    getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            setSqlEditorEnabled(true);
                            setFocusOnSqlEditor();
                            view.handleDataLoadException(JobTraceEntriesViewerTab.this, e1);
                        }
                    });
                }

            }

            return Status.OK_STATUS;
        }

    };
}
