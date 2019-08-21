/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.jobs.rse;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.preferencepages.IPreferences;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.editor.JobLogExplorerEditor;
import biz.isphere.joblogexplorer.editor.JobLogExplorerEditorFileInput;

public abstract class AbstractLoadRemoteSpooledFileJob extends Job {

    public AbstractLoadRemoteSpooledFileJob() {
        super(Messages.Loading_remote_job_log_dots);
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {

        try {

            SpooledFile spooledFile = getSpooledFile();

            String format = IPreferences.OUTPUT_FORMAT_TEXT;
            // CHANGED: Rz, 29.05.2018
            // String target =
            // ISpherePlugin.getDefault().getSpooledFilesDirectory() +
            // File.separator + spooledFile.getTemporaryName(format);
            IFile target = ISpherePlugin.getDefault().getSpooledFilesProject().getFile(spooledFile.getTemporaryName(format)); // .getLocation().toOSString();

            IFile localSpooledFilePath = spooledFile.downloadSpooledFile(format, target);
            final String filePath = localSpooledFilePath.getLocation().toOSString();
            final String originalFileName = spooledFile.getQualifiedName();

            UIJob job = new UIJob(getName()) {

                @Override
                public IStatus runInUIThread(IProgressMonitor paramIProgressMonitor) {

                    try {

                        JobLogExplorerEditorFileInput editorInput = new JobLogExplorerEditorFileInput(filePath, originalFileName);
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, JobLogExplorerEditor.ID);

                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    return Status.OK_STATUS;
                }
            };
            job.schedule();

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return Status.OK_STATUS;
    }

    protected abstract SpooledFile getSpooledFile();
}
