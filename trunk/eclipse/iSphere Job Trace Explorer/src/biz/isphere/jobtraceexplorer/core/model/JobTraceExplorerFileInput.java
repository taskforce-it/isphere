/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.model;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.jobtraceexplorer.core.model.dao.JobTraceJsonDAO;

public class JobTraceExplorerFileInput extends AbstractJobTraceExplorerInput {

    private static final String INPUT_TYPE = "file://"; //$NON-NLS-1$

    private File file;

    public JobTraceExplorerFileInput(String path) {
        this.file = new File(path);
    }

    public JobTraceExplorerFileInput(File file) {
        this.file = file;
    }

    public String getPath() {

        if (file == null) {
            return ""; //$NON-NLS-1$
        }

        return file.getPath();
    }

    @Override
    public String getName() {
        return getPath();
    }

    @Override
    public String getToolTipText() {
        return getPath();
    }

    @Override
    public String getContentId() {
        return INPUT_TYPE + getPath();
    }

    @Override
    public JobTraceSession load(IProgressMonitor monitor) throws IOException {

        JobTraceJsonDAO loader = new JobTraceJsonDAO(file.getAbsolutePath());
        JobTraceSession traceData;
        traceData = loader.load(monitor);

        return traceData;
    }

}
