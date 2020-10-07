/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.model;

public class JobTraceSession {

    private String connectionName;
    private String libraryName;
    private String sessionID;

    public JobTraceSession(String connectionName, String libraryName, String sessionID) {
        this.connectionName = connectionName;
        this.libraryName = libraryName;
        this.sessionID = sessionID;
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

    public String getQualifiedName() {
        return libraryName + ":" + sessionID; //$NON-NLS-1$
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }
}
