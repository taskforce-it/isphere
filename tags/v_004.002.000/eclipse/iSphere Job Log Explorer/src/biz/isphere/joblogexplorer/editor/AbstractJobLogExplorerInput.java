/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor;

import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.joblogexplorer.exceptions.BasicJobLogLoaderException;
import biz.isphere.joblogexplorer.model.JobLog;

public abstract class AbstractJobLogExplorerInput {

    public abstract String getName();

    public abstract String getToolTipText();

    public abstract String getContentId();

    public boolean isSameInput(AbstractJobLogExplorerInput otherInput) {

        if (otherInput == null) {
            return false;
        }

        String otherContentId = otherInput.getContentId();
        String contentId = getContentId();

        if (otherContentId == null && contentId == null) {
            return true;
        }

        if (contentId == null) {
            return false;
        }

        return contentId.equals(otherContentId);
    }

    public abstract JobLog load(IProgressMonitor arg0) throws BasicJobLogLoaderException;
}
