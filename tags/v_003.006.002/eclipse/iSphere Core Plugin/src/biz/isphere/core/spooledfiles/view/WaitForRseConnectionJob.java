/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;

public class WaitForRseConnectionJob extends Job {

    private Shell shell;
    private String connectionName;
    private IWaitForRseConnectionPostRun postRun;

    public WaitForRseConnectionJob(Shell shell, String connectionName, IWaitForRseConnectionPostRun postRun) {
        super(Messages.Waiting_for_RSE_connection);

        this.shell = shell;
        this.connectionName = connectionName;
        this.postRun = postRun;
    }

    @Override
    protected IStatus run(IProgressMonitor arg0) {

        if (IBMiHostContributionsHandler.isRseSubsystemInitialized(connectionName)) {
            postRun.setWaitForRseConnectionPostRunData(shell, connectionName, true);
        } else {
            postRun.setWaitForRseConnectionPostRunData(shell, connectionName, false);
        }

        return Status.OK_STATUS;
    }
}
