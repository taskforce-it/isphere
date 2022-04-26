/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor;

import biz.isphere.core.spooledfiles.SpooledFile;

public class JobLogExplorerSpooledFileInput extends AbstractJobLogExplorerInput {

    private static final String INPUT_TYPE = "splf://"; //$NON-NLS-1$

    private SpooledFile spooledFile;

    public JobLogExplorerSpooledFileInput(SpooledFile spooledFile) {

        this.spooledFile = spooledFile;
    }

    public SpooledFile getSpooledFile() {
        return spooledFile;
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
