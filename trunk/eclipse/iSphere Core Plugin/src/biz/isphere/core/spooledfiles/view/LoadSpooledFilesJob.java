/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import biz.isphere.core.Messages;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesInputData;

public class LoadSpooledFilesJob extends Job {

    private AbstractWorkWithSpooledFilesInputData inputData;
    private ILoadSpooledFilesPostRun postRun;

    public LoadSpooledFilesJob(AbstractWorkWithSpooledFilesInputData inputData, ILoadSpooledFilesPostRun postRun) {
        super(Messages.Loading_spooled_file);

        this.inputData = inputData;
        this.postRun = postRun;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {

        SpooledFile[] spooledFiles = inputData.load(monitor);
        postRun.setLoadSpooledFilesPostRunData(inputData, spooledFiles);

        return Status.OK_STATUS;
    }
}
