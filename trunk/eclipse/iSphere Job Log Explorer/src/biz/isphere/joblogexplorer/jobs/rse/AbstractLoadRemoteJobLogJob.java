/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.jobs.rse;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.joblogexplorer.editor.JobLogExplorerJobInput;

public abstract class AbstractLoadRemoteJobLogJob extends AbstractLoadInputJob {

    public void run() {

        try {

            String connectionName = getConnectionName();
            String jobName = getJobName();
            String userName = getUserName();
            String jobNumber = getJobNumber();

            JobLogExplorerJobInput editorInput = new JobLogExplorerJobInput(connectionName, jobName, userName, jobNumber);
            openJobLogExplorerView(editorInput);

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Failed to open job log explorer with active job input ***", e); //$NON-NLS-1$
        }
    }

    protected abstract String getConnectionName();

    protected abstract String getJobName();

    protected abstract String getUserName();

    protected abstract String getJobNumber();

}
