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
import biz.isphere.jobtraceexplorer.core.ui.views.IDataLoadPostRun;

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

    public JobTraceEntriesViewerTab(CTabFolder parent, JobTraceSession jobTraceSession, SelectionListener loadJobTraceEntriesSelectionListener) {
        super(parent, jobTraceSession, loadJobTraceEntriesSelectionListener);

        setSqlEditorVisibility(false);

        initializeComponents();
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

    public void openJobTraceSession(final IDataLoadPostRun postRun) throws Exception {

        tableViewer.getTable().setEnabled(false);

        setSqlEditorEnabled(false);

        Job loadJobTraceDataJob = new OpenJobTraceSessionJob(postRun, getJobTraceSession());
        loadJobTraceDataJob.schedule();
    }

    public void filterJobTraceSession(final IDataLoadPostRun postRun) throws Exception {

        setSqlEditorEnabled(false);

        Job filterJobTraceDataJob = new FilterJobTraceSessionDataJob(postRun);
        filterJobTraceDataJob.schedule();
    }

    public boolean hasSqlEditor() {
        return true;
    }

    private class OpenJobTraceSessionJob extends Job {

        private IDataLoadPostRun postRun;
        private JobTraceSession jobTraceSession;

        public OpenJobTraceSessionJob(IDataLoadPostRun postRun, JobTraceSession jobTraceSession) {
            super(Messages.bind(Messages.Status_Loading_job_trace_entries_of_session_A, jobTraceSession.getQualifiedName()));

            this.postRun = postRun;
            this.jobTraceSession = jobTraceSession;
        }

        public IStatus run(IProgressMonitor monitor) {

            try {

                JobTraceDAO jobTraceDAO = new JobTraceDAO(jobTraceSession);
                final JobTraceEntries data = jobTraceDAO.load(monitor);
                data.applyFilter();

                IBMiMessage[] messages = data.getMessages();
                if (messages.length != 0) {
                    throw new Exception("*** Error loading job trace entries. *** \n" + messages[0].getID() + ": " + messages[0].getText()); //$NON-NLS-1$ //$NON-NLS-2$
                }

                if (!isDisposed()) {
                    getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            setInputData(data);
                            postRun.finishDataLoading(JobTraceEntriesViewerTab.this, false);
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
                            postRun.handleDataLoadException(JobTraceEntriesViewerTab.this, e1);
                        }
                    });
                }

            }

            return Status.OK_STATUS;
        }

    };

    private class FilterJobTraceSessionDataJob extends Job {

        private IDataLoadPostRun postRun;

        public FilterJobTraceSessionDataJob(IDataLoadPostRun postRun) {
            super(Messages.Status_Loading_job_trace_entries_of_session_A);

            this.postRun = postRun;
        }

        public IStatus run(IProgressMonitor monitor) {

            final JobTraceEntries data = getInput();

            try {

                data.applyFilter();

                if (!isDisposed()) {
                    getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            setInputData(data);
                            setSqlEditorEnabled(true);
                            setFocusOnSqlEditor();
                            postRun.finishDataLoading(JobTraceEntriesViewerTab.this, true);
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
                            postRun.handleDataLoadException(JobTraceEntriesViewerTab.this, e1);
                        }
                    });
                }

            }

            return Status.OK_STATUS;
        }

    };
}
