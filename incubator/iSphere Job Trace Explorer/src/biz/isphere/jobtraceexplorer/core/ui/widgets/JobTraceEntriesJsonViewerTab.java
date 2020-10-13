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

import biz.isphere.core.json.JsonImporter;
import biz.isphere.jobtraceexplorer.core.Messages;
import biz.isphere.jobtraceexplorer.core.model.AbstractJobTraceSession;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;
import biz.isphere.jobtraceexplorer.core.model.JobTraceSessionJson;
import biz.isphere.jobtraceexplorer.core.ui.views.IDataLoadPostRun;

/**
 * This widget is a viewer for the job trace entries loaded from a job trace
 * session. It is used by the "Job Trace Explorer" view when creating a tab for
 * retrieved job trace entries.
 * 
 * @see JobTraceEntry
 * @see JobTraceEntryViewerView
 */
public class JobTraceEntriesJsonViewerTab extends AbstractJobTraceEntriesViewerTab {

    public JobTraceEntriesJsonViewerTab(CTabFolder parent, AbstractJobTraceSession jobTraceSession,
        SelectionListener loadJobTraceEntriesSelectionListener) {
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
    public JobTraceSessionJson getJobTraceSession() {
        return (JobTraceSessionJson)super.getJobTraceSession();
    }

    private class OpenJobTraceSessionJob extends Job {

        private IDataLoadPostRun postRun;
        private JobTraceSessionJson jobTraceSession;
        private JobTraceEntry selectedItem;

        public OpenJobTraceSessionJob(IDataLoadPostRun postRun, JobTraceSessionJson jobTraceSession) {
            this(postRun, jobTraceSession, null);
        }

        public OpenJobTraceSessionJob(IDataLoadPostRun postRun, JobTraceSessionJson jobTraceSession, JobTraceEntry selectedItem) {
            super(Messages.bind(Messages.Status_Loading_job_trace_entries_of_session_A, jobTraceSession.getQualifiedName()));

            this.postRun = postRun;
            this.jobTraceSession = jobTraceSession;
            this.selectedItem = selectedItem;
        }

        public IStatus run(IProgressMonitor monitor) {

            try {

                jobTraceSession.getJobTraceEntries().reset();

                JsonImporter<JobTraceSessionJson> importer = new JsonImporter<JobTraceSessionJson>(JobTraceSessionJson.class);
                jobTraceSession = importer.execute(postRun.getShell(), jobTraceSession.getFileName());
                for (JobTraceEntry jobTraceEntry : jobTraceSession.getJobTraceEntries().getItems()) {
                    jobTraceEntry.setParent(jobTraceSession.getJobTraceEntries());
                }

                jobTraceSession.getJobTraceEntries().applyFilter();

                if (!isDisposed()) {
                    getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            setJobTraceSession(jobTraceSession);
                            setSelectedItem(selectedItem);
                            postRun.finishDataLoading(JobTraceEntriesJsonViewerTab.this, false);
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
                            postRun.handleDataLoadException(JobTraceEntriesJsonViewerTab.this, e1);
                        }
                    });
                }

            }

            return Status.OK_STATUS;
        }
    }
}
