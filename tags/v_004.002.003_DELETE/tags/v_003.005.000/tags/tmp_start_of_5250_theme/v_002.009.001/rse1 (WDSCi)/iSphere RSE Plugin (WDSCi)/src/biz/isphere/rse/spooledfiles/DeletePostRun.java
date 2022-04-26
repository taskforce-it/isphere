/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.progress.UIJob;

import com.ibm.etools.systems.core.SystemPlugin;
import com.ibm.etools.systems.model.ISystemRemoteChangeEvents;
import com.ibm.etools.systems.model.SystemRegistry;

/**
 * Post run object that updates the GUI after spooled files have been deleted.
 * 
 * @author Thomas Raddatz
 * @see DeleteExec#execute(java.util.ArrayList, IDeletePostRun)
 */
public class DeletePostRun implements IDeletePostRun {

    /**
     * Class that runs on the UI thread to update the delete spooled files
     * result.
     * 
     * @author Thomas Raddatz
     */
    private class UpdateView extends UIJob {

        private DeleteResult deleteResult;

        public UpdateView(IWorkbenchWindow aWorkbenchWindow, DeleteResult aDeleteResult) {
            super("Updating view");
            deleteResult = aDeleteResult;
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor aMonitor) {
            SystemRegistry sr = SystemPlugin.getDefault().getSystemRegistry();
            Vector<SpooledFileResource> spooledFileVector = deleteResult.getDeletedSpooledFiles();
            if (!spooledFileVector.isEmpty()) {
                sr.fireRemoteResourceChangeEvent(ISystemRemoteChangeEvents.SYSTEM_REMOTE_RESOURCE_DELETED, spooledFileVector, null, null, null, null);
            }
            return Status.OK_STATUS;
        }

    }

    private IWorkbenchWindow workbenchWindow;

    public void setWorkbenchWindow(IWorkbenchWindow aWorkbenchWindow) {
        workbenchWindow = aWorkbenchWindow;
    }

    public void run(final DeleteResult aDeleteResult) {

        UpdateView showView = new UpdateView(workbenchWindow, aDeleteResult);
        showView.schedule();

    }

}
