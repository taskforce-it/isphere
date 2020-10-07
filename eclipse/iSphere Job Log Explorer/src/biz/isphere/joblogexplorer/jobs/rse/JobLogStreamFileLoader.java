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

public class JobLogStreamFileLoader extends AbstractJobLogLoader {

    private String originalFileName;
    private String filePath;

    public JobLogStreamFileLoader(String fileName, String absolutePath) {
        this.originalFileName = fileName;
        this.filePath = absolutePath;
    }

    public void run() {

        try {

            JobLogExplorerFileInput editorInput = new JobLogExplorerFileInput(filePath, originalFileName);
            openJobLogExplorerView(editorInput);

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Failed to open job log explorer with file input ***", e); //$NON-NLS-1$
        }
    }
}
