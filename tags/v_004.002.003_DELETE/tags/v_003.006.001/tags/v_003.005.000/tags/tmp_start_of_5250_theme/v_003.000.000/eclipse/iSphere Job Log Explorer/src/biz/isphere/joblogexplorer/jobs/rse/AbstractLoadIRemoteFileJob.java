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
import biz.isphere.joblogexplorer.editor.JobLogExplorerEditorFileInput;

public abstract class AbstractLoadIRemoteFileJob {

    public void run() {

        try {

            try {

                String filePath = getRemoteFileAbsolutePath();
                String originalFileName = getRemoteFileName();

                JobLogExplorerEditorFileInput editorInput = new JobLogExplorerEditorFileInput(filePath, originalFileName);
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, JobLogExplorerEditor.ID);

            } catch (Throwable e) {
                e.printStackTrace();
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected abstract String getRemoteFileName();

    protected abstract String getRemoteFileAbsolutePath();
}
