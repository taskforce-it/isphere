/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.rse.jobs;

import biz.isphere.joblogexplorer.jobs.rse.AbstractLoadRemoteJobLogJob;

import com.ibm.etools.iseries.comm.interfaces.ISeriesJobName;

public class LoadRemoteJobLogJob extends AbstractLoadRemoteJobLogJob {

    String connectionName;
    ISeriesJobName jobName;

    public LoadRemoteJobLogJob(String connectionName, ISeriesJobName jobName) {
        this.connectionName = connectionName;
        this.jobName = jobName;
    }

    protected String getConnectionName() {
        return connectionName;
    }

    protected String getJobName() {
        return jobName.getName();
    }

    protected String getUserName() {
        return jobName.getUser();
    }

    protected String getJobNumber() {
        return jobName.getNumber();
    }

}
