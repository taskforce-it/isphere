/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.model.dao;

import java.io.IOException;
import java.util.Date;

import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.core.json.JsonImporter;
import biz.isphere.jobtraceexplorer.core.ISphereJobTraceExplorerCorePlugin;
import biz.isphere.jobtraceexplorer.core.Messages;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;
import biz.isphere.jobtraceexplorer.core.model.JobTraceSession;

/**
 * This class retrieves journal entries from the journal a given object is
 * associated to.
 */
public class JobTraceJsonDAO {

    private String fileName;

    public JobTraceJsonDAO(String fileName) {

        this.fileName = fileName;
    }

    public JobTraceSession load(IProgressMonitor monitor) throws IOException {

        JobTraceSession jobTraceSession = new JobTraceSession(fileName);

        Date startTime = new Date();

        try {

            monitor.setTaskName(Messages.Status_Preparing_to_load_job_trace_entries);

            JsonImporter<JobTraceSession> importer = new JsonImporter<JobTraceSession>(JobTraceSession.class);

            jobTraceSession = importer.execute(null, fileName);
            jobTraceSession.updateFileName(fileName);

            for (JobTraceEntry jobTraceEntry : jobTraceSession.getJobTraceEntries().getItems()) {
                jobTraceEntry.setParent(jobTraceSession.getJobTraceEntries());
            }

        } finally {
            monitor.done();
        }

        ISphereJobTraceExplorerCorePlugin.debug("mSecs total: " + timeElapsed(startTime)); //$NON-NLS-1$

        return jobTraceSession;
    }

    private long timeElapsed(Date startTime) {
        return (new Date().getTime() - startTime.getTime());
    }
}
