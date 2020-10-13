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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionListener;

import biz.isphere.jobtraceexplorer.core.Messages;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;
import biz.isphere.jobtraceexplorer.core.model.JobTraceSessionSQL;
import biz.isphere.jobtraceexplorer.core.model.api.IBMiMessage;
import biz.isphere.jobtraceexplorer.core.model.dao.JobTraceSQLDAO;
import biz.isphere.jobtraceexplorer.core.ui.views.IDataLoadPostRun;

/**
 * This widget is a viewer for the job trace entries loaded from a job trace
 * session. It is used by the "Job Trace Explorer" view when creating a tab for
 * retrieved job trace entries.
 * 
 * @see JobTraceEntry
 * @see JobTraceEntryViewerView
 */
public class JobTraceEntriesSQLViewerTab extends AbstractJobTraceEntriesViewerTab {

    public JobTraceEntriesSQLViewerTab(CTabFolder parent, JobTraceSessionSQL jobTraceSession, SelectionListener loadJobTraceEntriesSelectionListener) {
        super(parent, jobTraceSession, loadJobTraceEntriesSelectionListener);
    }

    public void reloadJobTraceSession(final IDataLoadPostRun postRun) throws Exception {

        JobTraceEntry selectedItem = getSelectedItem();

        setInputData(null);

        setTableViewerEnabled(false);
        setSqlEditorEnabled(false);

        Job loadJobTraceDataJob = new OpenJobTraceSessionJob(postRun, getJobTraceSession(), selectedItem);
        loadJobTraceDataJob.schedule();
    }

    public void openJobTraceSession(final IDataLoadPostRun postRun) throws Exception {

        setInputData(null);

        setTableViewerEnabled(false);
        setSqlEditorEnabled(false);

        Job loadJobTraceDataJob = new OpenJobTraceSessionJob(postRun, getJobTraceSession());
        loadJobTraceDataJob.schedule();
    }

    public boolean hasSqlEditor() {
        return true;
    }

    @Override
    public JobTraceSessionSQL getJobTraceSession() {
        return (JobTraceSessionSQL)super.getJobTraceSession();
    }

    private class OpenJobTraceSessionJob extends Job {

        private IDataLoadPostRun postRun;
        private JobTraceSessionSQL jobTraceSession;
        private JobTraceEntry selectedItem;

        public OpenJobTraceSessionJob(IDataLoadPostRun postRun, JobTraceSessionSQL jobTraceSession) {
            this(postRun, jobTraceSession, null);
        }

        public OpenJobTraceSessionJob(IDataLoadPostRun postRun, JobTraceSessionSQL jobTraceSession, JobTraceEntry selectedItem) {
            super(Messages.bind(Messages.Status_Loading_job_trace_entries_of_session_A, jobTraceSession.getQualifiedName()));

            this.postRun = postRun;
            this.jobTraceSession = jobTraceSession;
            this.selectedItem = selectedItem;
        }

        public IStatus run(IProgressMonitor monitor) {

            try {

                jobTraceSession.getJobTraceEntries().fullReset();

                JobTraceSQLDAO jobTraceDAO = new JobTraceSQLDAO(jobTraceSession);
                jobTraceSession = jobTraceDAO.load(monitor);

                jobTraceSession.getJobTraceEntries().applyFilter();

                IBMiMessage[] messages = jobTraceSession.getJobTraceEntries().getMessages();
                if (messages.length != 0) {
                    throw new Exception("*** Error loading job trace entries. *** \n" + messages[0].getID() + ": " + messages[0].getText()); //$NON-NLS-1$ //$NON-NLS-2$
                }

                if (!isDisposed()) {
                    getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            setJobTraceSession(jobTraceSession);
                            setSelectedItem(selectedItem);
                            postRun.finishDataLoading(JobTraceEntriesSQLViewerTab.this, false);
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
                            postRun.handleDataLoadException(JobTraceEntriesSQLViewerTab.this, e1);
                        }
                    });
                }

            }

            return Status.OK_STATUS;
        }

    }
}
