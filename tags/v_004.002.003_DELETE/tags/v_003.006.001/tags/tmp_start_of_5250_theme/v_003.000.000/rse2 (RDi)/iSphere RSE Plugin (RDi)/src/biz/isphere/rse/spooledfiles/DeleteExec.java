/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;

/**
 * This class asynchronously deletes spooled files on the System i.
 * 
 * @author Thomas Raddatz
 */
public class DeleteExec {

    /**
     * This class covers the actual "delete spooled files" thread.
     */
    private class Delete extends Job {

        private ArrayList<SpooledFileResource> spooledFileResources;

        private IDeletePostRun postRun;

        public Delete(ArrayList<SpooledFileResource> aSpooledFileResources, IDeletePostRun aPostRun) {

            super(Messages.Deleting_spooled_files);

            spooledFileResources = aSpooledFileResources;
            postRun = aPostRun;

        }

        /**
         * Deletes spooled files while displaying a progress monitor to keep the
         * user up to date.
         */
        @Override
        public IStatus run(IProgressMonitor aMonitor) {

            IStatus status = Status.OK_STATUS;

            aMonitor.beginTask(Messages.Deleting, spooledFileResources.size());

            String message = null;
            for (SpooledFileResource spooledFileResource : spooledFileResources) {

                if (aMonitor.isCanceled()) {
                    status = Status.CANCEL_STATUS;
                    break;
                }

                message = spooledFileResource.getSpooledFile().delete();
                deleteResults.add(spooledFileResource, message);

                // Ignore CPF3344:
                // "File QPSUPRTF number 80 no longer in the system."
                if (message != null && !message.startsWith("CPF3344")) {
                    ISphereRSEPlugin.logError(message, null);
                }

                aMonitor.worked(1);
            }

            aMonitor.done();

            /*
             * Optionally update GUI.
             */
            if (postRun != null) {
                postRun.run(deleteResults);
            }

            return status;

        }

    }

    private DeleteResult deleteResults = new DeleteResult();

    /**
     * Interactively deletes a given list of spooled files.
     * 
     * @param aSpooledFiles - list of spooled files to delete
     * @return array of deleted spooled files
     */
    public DeleteResult execute(ArrayList<SpooledFileResource> aSpooledFiles) {

        Delete delete = new Delete(aSpooledFiles, null);
        delete.setUser(true);
        delete.schedule();

        try {
            delete.join();
        } catch (InterruptedException e) {
        }

        return deleteResults;

    }

    /**
     * Spans a new thread to delete a given list of spooled files. Optionally
     * updates the GUI.
     * 
     * @param aSpooledFiles - list of spooled files to delete
     * @param aDeletePostRun - post run object to update the GUI
     */
    public void execute(ArrayList<SpooledFileResource> aSpooledFiles, IDeletePostRun aDeletePostRun) {

        Delete delete = new Delete(aSpooledFiles, aDeletePostRun);
        delete.setUser(true);
        delete.schedule();

    }

}
