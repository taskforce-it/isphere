/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.rse.jobs;

import biz.isphere.joblogexplorer.jobs.rse.AbstractLoadIRemoteFileJob;

import com.ibm.etools.systems.subsystems.IRemoteFile;

public class LoadIRemoteFileJob extends AbstractLoadIRemoteFileJob {

    IRemoteFile remoteFile;

    public LoadIRemoteFileJob(IRemoteFile remoteFile) {
        super();

        this.remoteFile = remoteFile;
    }

    @Override
    public String getRemoteFileAbsolutePath() {
        return remoteFile.getAbsolutePath();
    }

    @Override
    public String getRemoteFileName() {
        return remoteFile.getName();
    }
}
