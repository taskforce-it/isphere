/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.model;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;

import biz.isphere.core.json.JsonImporter;
import biz.isphere.core.json.JsonSerializable;

import com.google.gson.annotations.Expose;

public class JobTraceSession implements JsonSerializable, IAdaptable {

    @Expose(serialize = true, deserialize = true)
    private String connectionName;
    @Expose(serialize = true, deserialize = true)
    private String libraryName;
    @Expose(serialize = true, deserialize = true)
    private String sessionID;

    @Expose(serialize = true, deserialize = true)
    private String whereClause;
    @Expose(serialize = true, deserialize = true)
    private Boolean isIBMDataExcluded;

    @Expose(serialize = true, deserialize = true)
    private JobTraceEntries jobTraceEntries;
    @Expose(serialize = true, deserialize = true)
    private String fileName;

    private transient boolean isFileSession;
    private transient JobTraceSessionPropertySource propertySource;

    /**
     * Produces a new JobTraceSession object. This constructor is exclusively
     * used by the {@link JsonImporter}.
     */
    public JobTraceSession() {
        this.isFileSession = true;
        initialize();
    }

    /**
     * Produces a new JobTraceSession object. This constructor is used when
     * loading job trace entries from a Json file.
     * 
     * @param fileName - Json file that stores a job trace session
     */
    public JobTraceSession(String fileName) {
        this.fileName = fileName;
        this.isFileSession = true;
        initialize();
    }

    /**
     * Produces a new JobTraceSession object. This constructor is used when
     * loading job trace entries from a job trace database.
     * 
     * @param connectionName - connection of the remote system
     * @param libraryName - name of the library, where the job trace session is
     *        stored
     * @param sessionID - session ID of the job trace session
     */
    public JobTraceSession(String connectionName, String libraryName, String sessionID) {
        this.connectionName = connectionName;
        this.libraryName = libraryName;
        this.sessionID = sessionID;
        this.isFileSession = false;

        initialize();
    }

    private void initialize() {

        this.whereClause = null;
        this.isIBMDataExcluded = true;
        this.jobTraceEntries = null;
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
        if (isFileSession) {
            return fileName;
        } else {
            return libraryName + ":" + sessionID; //$NON-NLS-1$
        }
    }

    public String getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    public boolean isIBMDataExcluded() {
        return isIBMDataExcluded;
    }

    public void setExcludeIBMData(boolean isExcluded) {
        this.isIBMDataExcluded = isExcluded;
    }

    public String getFileName() {
        return fileName;
    }

    public void updateFileName(String fileName) {

        if (!isFileSession) {
            throw new IllegalAccessError("Method not allowed, when session is not a file session."); //$NON-NLS-1$
        }

        this.fileName = fileName;
    }

    public JobTraceEntries getJobTraceEntries() {

        if (jobTraceEntries == null) {
            jobTraceEntries = new JobTraceEntries();
        }

        return jobTraceEntries;
    }

    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySource.class) {
            if (propertySource == null) {
                propertySource = new JobTraceSessionPropertySource(this);
            }
            return propertySource;
        }
        return null;
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }

    /**
     * Produces a hash code with attributes 'whereClause' and
     * 'isIBMDataExcluded'.
     */
    @Override
    public int hashCode() {
        final int prime = 67;
        int result = 1;
        result = prime * result + ((connectionName == null) ? 0 : connectionName.hashCode());
        result = prime * result + ((libraryName == null) ? 0 : libraryName.hashCode());
        result = prime * result + ((sessionID == null) ? 0 : sessionID.hashCode());
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        return result;
    }

    /**
     * Compares the object without attribute 'whereClause' and
     * 'isIBMDataExcluded'.
     * 
     * @param obj - the other object
     */
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
        if (fileName == null) {
            if (other.fileName != null) return false;
        } else if (!fileName.equals(other.fileName)) return false;
        return true;
    }
}
