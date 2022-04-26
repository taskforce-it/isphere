/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.joblogexplorer.exceptions.InvalidJobLogFormatException;
import biz.isphere.joblogexplorer.exceptions.JobLogNotLoadedException;
import biz.isphere.joblogexplorer.model.JobLog;
import biz.isphere.joblogexplorer.model.JobLogParser;

public class JobLogExplorerFileInput extends AbstractJobLogExplorerInput {

    private static final String INPUT_TYPE = "file://"; //$NON-NLS-1$

    private File file;
    private JobLog jobLog;

    public JobLogExplorerFileInput(String path) {
        this.file = new File(path);
    }

    public JobLogExplorerFileInput(File file) {
        this.file = file;
    }

    public String getPath() {

        if (file == null) {
            return ""; //$NON-NLS-1$
        }

        return file.getPath();
    }

    public JobLog load(IProgressMonitor monitor) throws JobLogNotLoadedException, InvalidJobLogFormatException {

        JobLogParser reader = new JobLogParser(monitor);
        jobLog = reader.loadFromStmf(getPath());

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
        return getPath();
    }

    @Override
    public String getContentId() {
        return INPUT_TYPE + getPath();
    }
}
