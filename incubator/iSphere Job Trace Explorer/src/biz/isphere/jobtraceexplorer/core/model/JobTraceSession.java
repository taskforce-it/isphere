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

    private String whereClause;

    public JobTraceSession(String connectionName, String libraryName, String sessionID) {
        this.connectionName = connectionName;
        this.libraryName = libraryName;
        this.sessionID = sessionID;

        this.whereClause = null;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public void setWhereClause() {
        this.whereClause = whereClause;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((connectionName == null) ? 0 : connectionName.hashCode());
        result = prime * result + ((libraryName == null) ? 0 : libraryName.hashCode());
        result = prime * result + ((sessionID == null) ? 0 : sessionID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        JobTraceSession other = (JobTraceSession)obj;
        if (connectionName == null) {
            if (other.connectionName != null) return false;
        } else if (!connectionName.equals(other.connectionName)) return false;
        if (libraryName == null) {
            if (other.libraryName != null) return false;
        } else if (!libraryName.equals(other.libraryName)) return false;
        if (sessionID == null) {
            if (other.sessionID != null) return false;
        } else if (!sessionID.equals(other.sessionID)) return false;
        return true;
    }

}
