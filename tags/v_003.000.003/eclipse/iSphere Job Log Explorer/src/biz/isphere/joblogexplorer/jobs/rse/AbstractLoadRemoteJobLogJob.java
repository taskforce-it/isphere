/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.jobs.rse;

import org.eclipse.ui.PlatformUI;

import biz.isphere.joblogexplorer.editor.JobLogExplorerEditor;
import biz.isphere.joblogexplorer.editor.JobLogExplorerEditorJobInput;

public abstract class AbstractLoadRemoteJobLogJob {

    public void run() {

        try {

            String connectionName = getConnectionName();
            String jobName = getJobName();
            String userName = getUserName();
            String jobNumber = getJobNumber();

            JobLogExplorerEditorJobInput editorInput = new JobLogExplorerEditorJobInput(connectionName, jobName, userName, jobNumber);
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, JobLogExplorerEditor.ID);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected abstract String getConnectionName();

    protected abstract String getJobName();

    protected abstract String getUserName();

    protected abstract String getJobNumber();

}
