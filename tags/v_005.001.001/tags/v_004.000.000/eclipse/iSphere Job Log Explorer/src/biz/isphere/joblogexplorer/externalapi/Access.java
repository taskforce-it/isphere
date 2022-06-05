/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.externalapi;

import java.io.File;
import java.util.Date;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.externalapi.AbstractAccess;
import biz.isphere.core.spooledfiles.ISpooledFileBrief;
import biz.isphere.joblogexplorer.editor.JobLogExplorerFileInput;
import biz.isphere.joblogexplorer.editor.JobLogExplorerJobInput;
import biz.isphere.joblogexplorer.editor.JobLogExplorerSpooledFileInput;
import biz.isphere.joblogexplorer.views.JobLogExplorerView;

/**
 * This class is the public API of the iSphere Job Log Explorer.
 */
public class Access extends AbstractAccess {

    /**
     * Opens the job log explorer for exploring the job log of an active job.
     * 
     * @param shell - the parent shell.
     * @param connectionName - connection name.
     * @param jobName - name of the job whose log is shown.
     * @param userName - name of the user profile under which the job is run.
     * @param jobNumber - job number assigned by the system.
     * @throws Exception
     */
    public static void openJobLogExplorer(Shell shell, String connectionName, String jobName, String userName, String jobNumber) throws Exception {

        JobLogExplorerJobInput input = new JobLogExplorerJobInput(connectionName, jobName, userName, jobNumber);

        JobLogExplorerView.openJobLog(ensureShell(shell), input);
    }

    /**
     * Opens the job log explorer for exploring the job log printed to a
     * QPJOBLOG spooled file.
     * 
     * @param shell - the parent shell.
     * @param connectionName - connection name.
     * @param splfName - the name of the spooled file.
     * @param splfNumber - number of the spooled file.
     * @param jobName - the name of the job that created the spooled file.
     * @param userName - the user who created the spooled file.
     * @param jobNumber - the number of the job that created the spooled file.
     * @throws Exception
     */
    public static void openJobLogExplorer(Shell shell, String connectionName, String splfName, int splfNumber, String jobName, String userName,
        String jobNumber) throws Exception {

        JobLogExplorerSpooledFileInput input = new JobLogExplorerSpooledFileInput(connectionName, splfName, splfNumber, jobName, userName, jobNumber);

        JobLogExplorerView.openJobLog(ensureShell(shell), input);
    }

    /**
     * Opens the job log explorer for exploring the job log printed to a
     * QPJOBLOG spooled file.
     * 
     * @param shell - the parent shell.
     * @param connectionName - connection name.
     * @param splfName - the name of the spooled file.
     * @param splfNumber - number of the spooled file.
     * @param jobName - the name of the job that created the spooled file.
     * @param userName - the user who created the spooled file.
     * @param jobNumber - the number of the job that created the spooled file.
     * @param jobSystemName - the name of the system where the spooled file was
     *        created.
     * @param creationTimestamp - the timestamp the spooled file was created on
     *        the system.
     * @throws Exception
     */
    public static void openJobLogExplorer(Shell shell, String connectionName, String splfName, int splfNumber, String jobName, String userName,
        String jobNumber, String jobSystemName, Date creationTimestamp) throws Exception {

        JobLogExplorerSpooledFileInput input = new JobLogExplorerSpooledFileInput(connectionName, splfName, splfNumber, jobName, userName, jobNumber,
            jobSystemName, creationTimestamp);

        JobLogExplorerView.openJobLog(ensureShell(shell), input);
    }

    /**
     * Opens the job log explorer for exploring the job log printed to a
     * QPJOBLOG spooled file.<br>
     * This method has been intentionally created for iSphere but may also be
     * used by other applications.
     * 
     * @param shell - the parent shell.
     * @param connectionName - connection name.
     * @param spooledFile - iSphere spooled file identifier.
     * @throws Exception
     */
    public static void openJobLogExplorer(Shell shell, ISpooledFileBrief spooledFile) throws Exception {

        JobLogExplorerSpooledFileInput input = new JobLogExplorerSpooledFileInput(spooledFile);
        JobLogExplorerView.openJobLog(ensureShell(shell), input);
    }

    /**
     * Open the job log explorer for exploring the job log printed to a QPJOBLOG
     * spooled file and exported as a PC text file.<br>
     * The txt file must have been created by the iSphere "Save as Text" option
     * or the "Download" option of ACS.
     * 
     * @param shell - the parent shell.
     * @param jobLog - stream file that contains the job log.
     * @throws Exception
     */
    public static void openJobLogExplorer(Shell shell, File jobLog) throws Exception {

        JobLogExplorerFileInput input = new JobLogExplorerFileInput(jobLog);

        JobLogExplorerView.openJobLog(ensureShell(shell), input);
    }
}
