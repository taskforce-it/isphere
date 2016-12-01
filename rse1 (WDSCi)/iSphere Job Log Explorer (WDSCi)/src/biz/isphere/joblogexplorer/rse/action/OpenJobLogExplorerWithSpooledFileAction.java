/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.rse.action;

import biz.isphere.joblogexplorer.rse.jobs.LoadRemoteSpooledFileJob;
import biz.isphere.rse.spooledfiles.SpooledFileResource;

import com.ibm.etools.iseries.core.ui.actions.isv.ISeriesAbstractQSYSPopupMenuExtensionAction;

public class OpenJobLogExplorerWithSpooledFileAction extends ISeriesAbstractQSYSPopupMenuExtensionAction {

    public OpenJobLogExplorerWithSpooledFileAction() {
        super();
    }

    @Override
    public void run() {

        Object[] selection = getSelectedRemoteObjects();
        for (int i = 0; i < selection.length; i++) {
            if (selection[i] instanceof SpooledFileResource) {
                SpooledFileResource spooledFileResource = (SpooledFileResource)selection[i];
                LoadRemoteSpooledFileJob job = new LoadRemoteSpooledFileJob(spooledFileResource);
                job.schedule();
            }
        }
    }
}
