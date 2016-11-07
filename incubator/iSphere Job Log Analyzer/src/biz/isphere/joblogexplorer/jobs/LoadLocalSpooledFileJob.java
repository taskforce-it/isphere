/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import biz.isphere.joblogexplorer.editor.DroppedLocalFile;

public class LoadLocalSpooledFileJob extends Job {

    DroppedLocalFile[] droppedLocalFiles;
    ILocalFileReceiver receiver;

    public LoadLocalSpooledFileJob(String name, DroppedLocalFile[] droppedLocalFiles, ILocalFileReceiver receiver) {
        super(name);
        this.droppedLocalFiles = droppedLocalFiles;
        this.receiver = receiver;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {

        receiver.setRemoteObjects(droppedLocalFiles);

        return Status.OK_STATUS;
    }
}
