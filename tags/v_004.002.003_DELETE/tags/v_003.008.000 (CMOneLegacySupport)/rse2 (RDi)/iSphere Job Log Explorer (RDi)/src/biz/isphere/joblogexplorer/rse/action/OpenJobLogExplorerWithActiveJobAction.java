/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.rse.action;

import biz.isphere.joblogexplorer.action.rse.AbstractOpenJobLogExplorerAction;
import biz.isphere.joblogexplorer.jobs.rse.JobLogActiveJobLoader;

import com.ibm.etools.iseries.comm.interfaces.ISeriesJobName;
import com.ibm.etools.iseries.subsystems.qsys.jobs.QSYSRemoteJob;

public class OpenJobLogExplorerWithActiveJobAction extends AbstractOpenJobLogExplorerAction {

    public static final String ID = "biz.isphere.joblogexplorer.rse.action.OpenJobLogExplorerWithActiveJobAction"; //$NON-NLS-1$

    @Override
    protected void execute(Object object) {
        if (object instanceof QSYSRemoteJob) {
            QSYSRemoteJob remoteJob = (QSYSRemoteJob)object;
            String connectionName = remoteJob.getRemoteJobContext().getJobSubsystem().getCmdSubSystem().getHostAliasName();
            ISeriesJobName jobName = new ISeriesJobName(remoteJob.getFullJobName());
            JobLogActiveJobLoader job = new JobLogActiveJobLoader(connectionName, jobName.getName(), jobName.getUser(), jobName.getNumber());
            job.run();
        }
    }

}
