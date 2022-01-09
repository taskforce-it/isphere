/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

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

    public JobLogExplorerSpooledFileInput(SpooledFile spooledFile) {

        this.spooledFile = spooledFile;
    }

    public SpooledFile getSpooledFile() {
        return spooledFile;
    }

    public JobLog load(IProgressMonitor monitor) throws DownloadSpooledFileException, JobLogNotLoadedException, InvalidJobLogFormatException {

        SpooledFile spooledFile = getSpooledFile();

        String format = IPreferences.OUTPUT_FORMAT_TEXT;
        IFile target = ISpherePlugin.getDefault().getSpooledFilesProject().getFile(spooledFile.getTemporaryName(format));

        IFile localSpooledFilePath;
        try {
            localSpooledFilePath = spooledFile.downloadSpooledFile(format, target);
        } catch (Exception e) {
            throw new DownloadSpooledFileException(e.getLocalizedMessage());
        }

        final String filePath = localSpooledFilePath.getLocation().toOSString();
        final String originalFileName = spooledFile.getQualifiedName();

        JobLogExplorerFileInput editorInput = new JobLogExplorerFileInput(filePath, originalFileName);

        JobLogParser reader = new JobLogParser(monitor);
        final JobLog jobLog = reader.loadFromStmf(editorInput.getPath());

        return jobLog;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    public String getName() {
        return spooledFile.getQualifiedName();
    }

    public String getToolTipText() {
        return spooledFile.getAbsoluteName();
    }

    @Override
    public String getContentId() {
        return INPUT_TYPE + spooledFile.getQualifiedName(); // $NON-NLS-1$
    }
}
