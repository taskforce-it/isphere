/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.tasktags.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import biz.isphere.lpex.tasktags.ISphereLpexTasksPlugin;
import biz.isphere.lpex.tasktags.model.LPEXTaskManager;

/**
 * This class represents a background job, that scans a given document for LPEX
 * task tags.
 * 
 * @author Thomas Raddatz
 */
public class DocumentScannerJob extends Job {

    private LPEXTaskManager manager;
    private boolean isRemoveOnly;

    public DocumentScannerJob(LPEXTaskManager aManager) {
        this(aManager, false);
    }

    public DocumentScannerJob(LPEXTaskManager aManager, boolean isRemoveOnly) {
        super(""); //$NON-NLS-1$
        this.manager = aManager;
        this.isRemoveOnly = isRemoveOnly;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        runInternally(monitor);
        return Status.OK_STATUS;
    }

    protected void runInternally(IProgressMonitor monitor) {

        // Calendar start = Calendar.getInstance();

        try {
            manager.removeMarkers();
            if (!isRemoveOnly && manager.markerAreEnabled()) {
                manager.createMarkers();
            }

        } catch (Exception e) {
            ISphereLpexTasksPlugin.logError("Failed to process document: " + manager.getDocumentName(), e);
        }

        // Calendar end = Calendar.getInstance();
        // System.out.println("ms: " + (end.getTimeInMillis() -
        // start.getTimeInMillis()));
    }
}
