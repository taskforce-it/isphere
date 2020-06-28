/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor;

import java.io.File;

import biz.isphere.base.internal.FileHelper;

public class JobLogExplorerFileInput extends AbstractJobLogExplorerInput {

    private static final String INPUT_TYPE = "file://"; //$NON-NLS-1$

    private String pathName;
    private String originalFileName;

    private File file;

    public JobLogExplorerFileInput(String pathName, String originalFileName) {

        this.pathName = pathName;
        this.originalFileName = originalFileName;

        if (pathName == null) {
            this.file = null;
        } else {
            this.file = new File(pathName);
        }
    }

    public String getPath() {
        return pathName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    public String getName() {

        if (file == null) {
            return ""; //$NON-NLS-1$
        }

        return FileHelper.getBaseName(file);
    }

    public String getToolTipText() {
        return getPath();
    }

    @Override
    public String getContentId() {
        return INPUT_TYPE + getPath();
    }
}
