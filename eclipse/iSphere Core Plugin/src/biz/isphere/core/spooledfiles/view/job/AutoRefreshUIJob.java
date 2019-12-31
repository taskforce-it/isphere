/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.core.Messages;
import biz.isphere.core.internal.IJobFinishedListener;
import biz.isphere.core.spooledfiles.view.IAutoRefreshView;

public class AutoRefreshUIJob extends UIJob {

    private IAutoRefreshView view;

    public AutoRefreshUIJob(IAutoRefreshView view) {
        super(Messages.EMPTY);

        this.view = view;
    }

    private IJobFinishedListener listener;

    @Override
    public IStatus runInUIThread(IProgressMonitor arg0) {
        System.out.println("Updating UI ...");
        view.refreshData();
        listener.jobFinished(this);
        return Status.OK_STATUS;
    }

    public void setJobFinishedListener(IJobFinishedListener listener) {
        this.listener = listener;
    }
}
