/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.IJobFinishedListener;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.spooledfiles.view.IAutoRefreshView;

/**
 * Job, that periodically refreshes the content of the view.
 */
public class AutoRefreshJob extends Job implements IJobFinishedListener {

    final int MILLI_SECONDS = 1000;

    private AutoRefreshUIJob autoRefreshUIJob;
    private IAutoRefreshView view;
    private int interval;

    private int waitTime;

    public AutoRefreshJob(IAutoRefreshView view, int seconds) {
        super(Messages.EMPTY);

        this.view = view;
        setInterval(seconds);
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {

        final int SLEEP_INTERVAL = 50;

        while (!monitor.isCanceled()) {

            try {

                /*
                 * Create a UI job to update the view with the new data.
                 */
                autoRefreshUIJob = new AutoRefreshUIJob(view);
                autoRefreshUIJob.setJobFinishedListener(this);
                autoRefreshUIJob.schedule();

                waitTime = interval;
                while ((!monitor.isCanceled() && waitTime > 0) || autoRefreshUIJob != null) {
                    Thread.sleep(SLEEP_INTERVAL);
                    if (waitTime > interval) {
                        waitTime = interval;
                    }
                    if (waitTime > 0) {
                        waitTime = waitTime - SLEEP_INTERVAL;
                    }
                }

            } catch (InterruptedException e) {
                // exit the thread
                break;
            } catch (Throwable e) {
                ISpherePlugin.logError(e.getMessage(), e);
                MessageDialogAsync.displayError(view.getShell(), e.getLocalizedMessage());
                break;
            }
        }

        view.jobFinished(this);

        return Status.OK_STATUS;
    }

    public int getInterval() {

        return interval / MILLI_SECONDS;
    }

    public void setInterval(int seconds) {

        interval = seconds * MILLI_SECONDS;
    }

    public void jobFinished(Job job) {

        if (job == autoRefreshUIJob) {
            autoRefreshUIJob = null;
        }
    }
}
