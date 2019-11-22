/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.jobs.rse;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import biz.isphere.joblogexplorer.editor.AbstractJobLogExplorerInput;
import biz.isphere.joblogexplorer.views.JobLogExplorerView;

public abstract class AbstractLoadInputJob {

    protected void openJobLogExplorerView(AbstractJobLogExplorerInput input) throws PartInitException {

        IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(JobLogExplorerView.ID);
        if (view == null) {
            view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(JobLogExplorerView.ID);
        }

        if (view instanceof JobLogExplorerView) {
            JobLogExplorerView jobLogExplorerView = (JobLogExplorerView)view;
            jobLogExplorerView.openExplorerTab(input);
        }
    }

}
