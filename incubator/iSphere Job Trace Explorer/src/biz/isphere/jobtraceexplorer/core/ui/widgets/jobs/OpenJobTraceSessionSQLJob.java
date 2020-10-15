/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.widgets.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import biz.isphere.jobtraceexplorer.core.Messages;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;
import biz.isphere.jobtraceexplorer.core.model.JobTraceSession;
import biz.isphere.jobtraceexplorer.core.model.api.IBMiMessage;
import biz.isphere.jobtraceexplorer.core.model.dao.JobTraceSQLDAO;
import biz.isphere.jobtraceexplorer.core.ui.views.IDataLoadPostRun;
import biz.isphere.jobtraceexplorer.core.ui.widgets.AbstractJobTraceEntriesViewerTab;

public class OpenJobTraceSessionSQLJob extends Job {

    private AbstractJobTraceEntriesViewerTab tabItem;
    private IDataLoadPostRun postRun;
    private JobTraceSession jobTraceSession;
    private JobTraceEntry selectedItem;

    public OpenJobTraceSessionSQLJob(AbstractJobTraceEntriesViewerTab tabItem, IDataLoadPostRun postRun, JobTraceSession jobTraceSession) {
        this(tabItem, postRun, jobTraceSession, null);
    }

    public OpenJobTraceSessionSQLJob(AbstractJobTraceEntriesViewerTab tabItem, IDataLoadPostRun postRun, JobTraceSession jobTraceSession,
        JobTraceEntry selectedItem) {
        super(Messages.bind(Messages.Status_Loading_job_trace_entries_of_session_A, jobTraceSession.getQualifiedName()));

        this.tabItem = tabItem;
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

            if (!tabItem.isDisposed()) {
                postRun.getShell().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        tabItem.setJobTraceSession(jobTraceSession);
                        tabItem.setSelectedItem(selectedItem);
                        postRun.finishDataLoading(tabItem, false);
                    }
                });
            }

        } catch (Throwable e) {

            if (!tabItem.isDisposed()) {
                final Throwable e1 = e;
                postRun.getShell().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        tabItem.setSqlEditorEnabled(true);
                        tabItem.setFocusOnSqlEditor();
                        postRun.handleDataLoadException(tabItem, e1);
                    }
                });
            }

        }

        return Status.OK_STATUS;
    }
}
