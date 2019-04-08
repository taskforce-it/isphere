/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.rse.jobs;

import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.joblogexplorer.jobs.rse.AbstractLoadRemoteSpooledFileJob;
import biz.isphere.rse.spooledfiles.SpooledFileResource;

public class LoadRemoteSpooledFileJob extends AbstractLoadRemoteSpooledFileJob {

    SpooledFileResource spooledFileResource;

    public LoadRemoteSpooledFileJob(SpooledFileResource spooledFileResource) {
        super();
        this.spooledFileResource = spooledFileResource;
    }

    protected SpooledFile getSpooledFile() {
        return spooledFileResource.getSpooledFile();
    }
}
