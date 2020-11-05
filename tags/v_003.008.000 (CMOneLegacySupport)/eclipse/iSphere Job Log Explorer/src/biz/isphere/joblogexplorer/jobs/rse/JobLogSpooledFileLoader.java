/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.jobs.rse;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.joblogexplorer.editor.JobLogExplorerSpooledFileInput;

public class JobLogSpooledFileLoader extends AbstractJobLogLoader {

    private SpooledFile spooledFile;

    public JobLogSpooledFileLoader(SpooledFile spooledFile) {
        this.spooledFile = spooledFile;
    }

    public void run() {

        try {

            JobLogExplorerSpooledFileInput editorInput = new JobLogExplorerSpooledFileInput(spooledFile);
            openJobLogExplorerView(editorInput);

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Failed to open job log explorer with spooled file input ***", e); //$NON-NLS-1$
        }
    }
}
