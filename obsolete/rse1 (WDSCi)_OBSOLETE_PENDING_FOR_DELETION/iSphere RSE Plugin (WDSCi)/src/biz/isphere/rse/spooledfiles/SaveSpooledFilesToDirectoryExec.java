/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.rse.Messages;

/**
 * This class asynchronously saves spooled files to a directory on the PC.
 * 
 * @author Thomas Raddatz
 */
public class SaveSpooledFilesToDirectoryExec {

    /**
     * This class covers the actual "save spooled files" thread.
     */
    private class Save extends Job {

        private SpooledFile[] spooledFiles;
        private String format;
        private String directory;

        public Save(SpooledFile[] aSpooledFiles, String aFormat, String aDirectory) {

            super(Messages.Saving_spooled_files);

            this.spooledFiles = aSpooledFiles;
            this.format = aFormat;
            this.directory = aDirectory;

        }

        /**
         * Deletes spooled files while displaying a progress monitor to keep the
         * user up to date.
         */
        @Override
        public IStatus run(IProgressMonitor aMonitor) {

            IStatus status = Status.OK_STATUS;

            aMonitor.beginTask("", spooledFiles.length); //$NON-NLS-1$

            for (SpooledFile spooledFile : spooledFiles) {

                if (aMonitor.isCanceled()) {
                    status = Status.CANCEL_STATUS;
                    break;
                }

                spooledFile.saveToDirectory(format, directory);

                aMonitor.worked(1);
            }

            aMonitor.done();

            return status;

        }

    }

    /**
     * Spans a new thread to delete a given list of spooled files. Optionally
     * updates the GUI.
     * 
     * @param aSpooledFiles - list of spooled files to delete
     * @param aFormat - format of the PC file (IPreferences.OUTPUT_FORMAT_TEXT,
     *        IPreferences.OUTPUT_FORMAT_HTML, IPreferences.OUTPUT_FORMAT_PDF)
     * @param aDirectory - directory where to store the spooled files
     */
    public void execute(SpooledFile[] aSpooledFiles, String aFormat, String aDirectory) {

        Save save = new Save(aSpooledFiles, aFormat, aDirectory);
        save.setUser(true);
        save.schedule();

    }

}
