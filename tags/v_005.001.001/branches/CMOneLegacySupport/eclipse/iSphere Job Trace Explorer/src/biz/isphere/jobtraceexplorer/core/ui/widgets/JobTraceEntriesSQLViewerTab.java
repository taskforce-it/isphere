/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.widgets;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionListener;

import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;
import biz.isphere.jobtraceexplorer.core.model.JobTraceSession;
import biz.isphere.jobtraceexplorer.core.ui.views.IDataLoadPostRun;
import biz.isphere.jobtraceexplorer.core.ui.widgets.jobs.OpenJobTraceSessionSQLJob;

/**
 * This widget is a viewer for the job trace entries loaded from a job trace
 * session. It is used by the "Job Trace Explorer" view when creating a tab for
 * retrieved job trace entries.
 * 
 * @see JobTraceEntry
 * @see JobTraceEntryViewerView
 */
public class JobTraceEntriesSQLViewerTab extends AbstractJobTraceEntriesViewerTab {

    public JobTraceEntriesSQLViewerTab(CTabFolder parent, JobTraceSession jobTraceSession, SelectionListener loadJobTraceEntriesSelectionListener) {
        super(parent, jobTraceSession, loadJobTraceEntriesSelectionListener);
    }

    public void reloadJobTraceSession(final IDataLoadPostRun postRun) throws Exception {

        JobTraceEntry selectedItem = getSelectedItem();

        setInputData(null);

        setEnabled(false);
        setSqlEditorEnabled(false);

        Job loadJobTraceDataJob = new OpenJobTraceSessionSQLJob(this, postRun, getJobTraceSession(), selectedItem);
        loadJobTraceDataJob.schedule();
    }

    public void openJobTraceSession(final IDataLoadPostRun postRun) throws Exception {

        setInputData(null);

        setEnabled(false);
        setSqlEditorEnabled(false);

        Job loadJobTraceDataJob = new OpenJobTraceSessionSQLJob(this, postRun, getJobTraceSession());
        loadJobTraceDataJob.schedule();
    }

    @Override
    public JobTraceSession getJobTraceSession() {
        return (JobTraceSession)super.getJobTraceSession();
    }
}
