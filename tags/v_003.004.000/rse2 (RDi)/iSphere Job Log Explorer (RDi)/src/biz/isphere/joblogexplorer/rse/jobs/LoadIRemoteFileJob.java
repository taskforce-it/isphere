/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.rse.jobs;

import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;

import biz.isphere.joblogexplorer.jobs.rse.AbstractLoadIRemoteFileJob;

public class LoadIRemoteFileJob extends AbstractLoadIRemoteFileJob {

    IRemoteFile remoteFile;

    public LoadIRemoteFileJob(IRemoteFile remoteFile) {
        this.remoteFile = remoteFile;
    }

    @Override
    protected String getRemoteFileName() {
        return remoteFile.getName();
    }

    @Override
    protected String getRemoteFileAbsolutePath() {
        return remoteFile.getAbsolutePath();
    }

}
