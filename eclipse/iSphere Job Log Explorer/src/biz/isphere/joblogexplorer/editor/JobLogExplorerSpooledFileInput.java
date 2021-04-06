/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.preferencepages.IPreferences;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.joblogexplorer.exceptions.DownloadSpooledFileException;
import biz.isphere.joblogexplorer.exceptions.InvalidJobLogFormatException;
import biz.isphere.joblogexplorer.exceptions.JobLogNotLoadedException;
import biz.isphere.joblogexplorer.model.JobLog;
import biz.isphere.joblogexplorer.model.JobLogParser;

public class JobLogExplorerSpooledFileInput extends AbstractJobLogExplorerInput {

    private static final String INPUT_TYPE = "splf://"; //$NON-NLS-1$

    private SpooledFile spooledFile;

    private IFile localSpooledFilePath;
    private JobLog jobLog;

    public JobLogExplorerSpooledFileInput(SpooledFile spooledFile) {

        this.spooledFile = spooledFile;
    }

    public JobLog load(IProgressMonitor monitor) throws DownloadSpooledFileException, JobLogNotLoadedException, InvalidJobLogFormatException {

        try {

            String format = IPreferences.OUTPUT_FORMAT_TEXT;
            IFile target = ISpherePlugin.getDefault().getSpooledFilesProject().getFile(spooledFile.getTemporaryName(format));

            localSpooledFilePath = spooledFile.downloadSpooledFile(format, target);

        } catch (Exception e) {
            ISpherePlugin.logError("*** Failed downloading spooled file ***", e);
            throw new DownloadSpooledFileException(ExceptionHelper.getLocalizedMessage(e));
        }

        final String filePath = localSpooledFilePath.getLocation().toOSString();
        JobLogExplorerFileInput editorInput = new JobLogExplorerFileInput(filePath);

        JobLogParser reader = new JobLogParser(monitor);
        jobLog = reader.loadFromStmf(editorInput.getPath());

        return jobLog;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    public String getName() {
        return jobLog.getQualifiedJobName();
    }

    public String getToolTipText() {
        return spooledFile.getToolTip(IPreferences.OUTPUT_FORMAT_TEXT);
    }

    @Override
    public String getContentId() {
        return INPUT_TYPE + spooledFile.getQualifiedName(); // $NON-NLS-1$
    }
}
