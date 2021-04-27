/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.model;

import java.sql.SQLException;

import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.jobtraceexplorer.core.model.dao.JobTraceSQLDAO;

public class JobTraceExplorerSessionInput extends AbstractJobTraceExplorerInput {

    private String connectionName;
    private String libraryName;
    private String sessionID;

    private Boolean isIBMDataExcluded;

    public JobTraceExplorerSessionInput(String connectionName, String libraryName, String sessionID) {
        this.connectionName = connectionName;
        this.libraryName = libraryName;
        this.sessionID = sessionID;
        this.isIBMDataExcluded = true;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public String getSessionID() {
        return sessionID;
    }

    public boolean isIBMDataExcluded() {
        return isIBMDataExcluded;
    }

    public void setExcludeIBMData(boolean excluded) {
        this.isIBMDataExcluded = excluded;
    }

    @Override
    public String getName() {
        return String.format("%s:%s", libraryName, sessionID);
    }

    @Override
    public String getToolTipText() {
        return String.format("%s::%s:%s", connectionName, libraryName, sessionID);
    }

    @Override
    public String getContentId() {
        return "remote_session:/" + connectionName + ":" + libraryName + ":" + sessionID;
    }

    @Override
    public JobTraceSession load(IProgressMonitor monitor) throws SQLException {

        JobTraceSQLDAO loader = new JobTraceSQLDAO(connectionName, libraryName, sessionID, isIBMDataExcluded);
        JobTraceSession traceData;
        traceData = loader.load(monitor);

        return traceData;
    }

}
