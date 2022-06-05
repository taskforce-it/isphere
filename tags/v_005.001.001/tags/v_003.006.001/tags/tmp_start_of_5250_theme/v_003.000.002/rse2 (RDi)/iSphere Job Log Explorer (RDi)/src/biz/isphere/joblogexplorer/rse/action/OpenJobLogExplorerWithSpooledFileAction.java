/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.rse.action;

import biz.isphere.joblogexplorer.action.rse.AbstractOpenJobLogExplorerWithSpooledFileAction;
import biz.isphere.joblogexplorer.rse.jobs.LoadRemoteSpooledFileJob;
import biz.isphere.rse.spooledfiles.SpooledFileResource;

public class OpenJobLogExplorerWithSpooledFileAction extends AbstractOpenJobLogExplorerWithSpooledFileAction {

    public static final String ID = "biz.isphere.joblogexplorer.rse.action.OpenJobLogExplorerWithSpooledFileAction"; //$NON-NLS-1$

    @Override
    protected void execute(Object object) {
        if (object instanceof SpooledFileResource) {
            SpooledFileResource spooledFileResource = (SpooledFileResource)object;
            LoadRemoteSpooledFileJob job = new LoadRemoteSpooledFileJob(spooledFileResource);
            job.schedule();
        }
    }

}
