/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.jobs.rse;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.joblogexplorer.editor.JobLogExplorerFileInput;

public abstract class AbstractLoadIRemoteFileJob extends AbstractLoadInputJob {

    public void run() {

        try {

            String filePath = getRemoteFileAbsolutePath();
            String originalFileName = getRemoteFileName();

            JobLogExplorerFileInput editorInput = new JobLogExplorerFileInput(filePath, originalFileName);
            openJobLogExplorerView(editorInput);

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Failed to open job log explorer with file input ***", e); //$NON-NLS-1$
        }
    }

    protected abstract String getRemoteFileName();

    protected abstract String getRemoteFileAbsolutePath();
}
