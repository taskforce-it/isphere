/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.externalapi;

import java.io.File;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.externalapi.AbstractAccess;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.SpooledFileFactory;
import biz.isphere.joblogexplorer.views.JobLogExplorerView;

/**
 * This class is the public API of the iSphere Job Log Explorer. <br>
 * The following interfaces are part of it.
 */
public class Access extends AbstractAccess {

    /**
     * Opens the job log explorer for a given job, identified by job name, user
     * name and job number.
     * 
     * @param shell - the parent shell.
     * @param connectionName - connection name.
     * @param jobName - name of the job whose log is shown.
     * @param userName - name of the user profile under which the job is run.
     * @param jobNumber - job number assigned by the system.
     * @throws Exception
     */
    public static void openJobLogExplorer(Shell shell, String connectionName, String jobName, String userName, String jobNumber) throws Exception {

        JobLogExplorerView.openActiveJobJobLog(ensureShell(shell), connectionName, jobName, userName, jobNumber);
    }

    /**
     * /** Opens the job log explorer for a spooled file identified by its name.
     * 
     * @param shell - the parent shell.
     * @param connectionName - connection name.
     * @param jobName - name of the job that created the spooled file.
     * @param userName - name of the user profile under which the job is run.
     * @param jobNumber - job number assigned by the system.
     * @param splfName - name of the spooled file
     * @param splfNumber - number of the spooled file.
     * @throws Exception
     */
    public static void openJobLogExplorer(Shell shell, String connectionName, String jobName, String userName, String jobNumber, String splfName,
        int splfNumber) throws Exception {

        SpooledFile spooledFile = new SpooledFileFactory().getSpooledFile(shell, connectionName, jobName, userName, jobNumber, splfName, splfNumber);
        if (spooledFile == null) {
            throw new SpooledFileNotFoundException(connectionName, jobName, userName, jobNumber, splfName, splfNumber);
        }

        openJobLogExplorer(shell, spooledFile);
    }

    /**
     * Opens the job log explorer for a given iSphere spooled file.
     * <p>
     * This method is intended to be <b>exclusively</b> used <b>by iSphere</b>.
     * 
     * @param shell - the parent shell.
     * @param spooledFile - spooled file that contains the job log.
     * @throws Exception
     */
    public static void openJobLogExplorer(Shell shell, SpooledFile spooledFile) throws Exception {

        JobLogExplorerView.openSpooledFileJobLog(ensureShell(shell), spooledFile);
    }

    /**
     * Opens the job log explorer for a given stream file. The stream file must
     * have been created by the iSphere "Save as Text" option or
     * 
     * @param shell - the parent shell.
     * @param jobLog - stream file that contains the job log.
     * @throws Exception
     */
    public static void openJobLogExplorer(Shell shell, File jobLog) throws Exception {

        JobLogExplorerView.openStreamFileJobLog(ensureShell(shell), jobLog);
    }
}
