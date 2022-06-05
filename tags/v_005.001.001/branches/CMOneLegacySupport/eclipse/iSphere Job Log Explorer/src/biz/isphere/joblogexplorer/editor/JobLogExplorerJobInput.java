/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor;

import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.joblogexplorer.exceptions.JobLogNotLoadedException;
import biz.isphere.joblogexplorer.exceptions.JobNotFoundException;
import biz.isphere.joblogexplorer.model.JobLog;
import biz.isphere.joblogexplorer.model.JobLogReader;

import com.ibm.as400.access.AS400;

public class JobLogExplorerJobInput extends AbstractJobLogExplorerInput {

    private static final String INPUT_TYPE = "job://"; //$NON-NLS-1$

    private String connectionName;
    private String jobName;
    private String userName;
    private String jobNumber;

    private String qualifiedJobName;

    public JobLogExplorerJobInput(String connectionName, String jobName, String userName, String jobNumber) {

        this.connectionName = connectionName;
        this.jobName = jobName;
        this.userName = userName;
        this.jobNumber = jobNumber;

        StringBuilder buffer = new StringBuilder();

        buffer.append(connectionName);
        buffer.append(":"); //$NON-NLS-1$
        buffer.append(jobNumber);
        buffer.append("/"); //$NON-NLS-1$
        buffer.append(userName);
        buffer.append("/"); //$NON-NLS-1$
        buffer.append(jobName);

        qualifiedJobName = buffer.toString();
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getJobName() {
        return jobName;
    }

    public String getUserName() {
        return userName;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public JobLog load(IProgressMonitor monitor) throws JobNotFoundException, JobLogNotLoadedException {

        AS400 as400 = IBMiHostContributionsHandler.getSystem(getConnectionName());

        JobLogReader reader = new JobLogReader();
        final JobLog jobLog = reader.loadFromJob(as400, getJobName(), getUserName(), getJobNumber());

        return jobLog;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    public String getName() {

        if (qualifiedJobName == null) {
            return ""; //$NON-NLS-1$
        }

        return qualifiedJobName;
    }

    public String getToolTipText() {
        return qualifiedJobName;
    }

    @Override
    public String getContentId() {
        return INPUT_TYPE + qualifiedJobName; // $NON-NLS-1$
    }
}
