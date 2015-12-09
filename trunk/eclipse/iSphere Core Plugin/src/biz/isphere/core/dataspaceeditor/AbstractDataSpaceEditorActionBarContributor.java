/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.progress.UIJob;

public abstract class AbstractDataSpaceEditorActionBarContributor extends EditorActionBarContributor {

    AbstractDataSpaceEditor activeEditor;

    @Override
    public void setActiveEditor(IEditorPart targetEditor) {

        activeEditor = (AbstractDataSpaceEditor)targetEditor;
        
        UIJob job = new UIJob("") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                activeEditor.updateActionsStatusAndStatusLine();
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    @Override
    public void contributeToStatusLine(IStatusLineManager statusLineManager) {
        statusLineManager.add(new StatusLineContributionItem(StatusLine.STATUS_LINE_ID));
    }
}
