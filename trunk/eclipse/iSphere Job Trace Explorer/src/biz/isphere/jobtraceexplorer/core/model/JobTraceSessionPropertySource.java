/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.model;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import biz.isphere.jobtraceexplorer.core.Messages;

public class JobTraceSessionPropertySource implements IPropertySource {

    private static final String PROPERTY_CONNECTION_NAME = "biz.isphere.jobtraceexplorer.core.model.JobTraceSession.connectionName";//$NON-NLS-1$
    private static final String PROPERTY_LIBRARY_NAME = "biz.isphere.jobtraceexplorer.core.model.JobTraceSession.libraryName";//$NON-NLS-1$
    private static final String PROPERTY_SESSION_ID = "biz.isphere.jobtraceexplorer.core.model.JobTraceSession.sessionID";//$NON-NLS-1$
    private static final String PROPERTY_WHERE_CLAUSE = "biz.isphere.jobtraceexplorer.core.model.JobTraceSession.whereClause";//$NON-NLS-1$
    private static final String PROPERTY_IS_IBM_DATA_EXCLUDED = "biz.isphere.jobtraceexplorer.core.model.JobTraceSession.isIBMDataExcluded";//$NON-NLS-1$
    private static final String PROPERTY_FILE_NAME = "biz.isphere.jobtraceexplorer.core.model.JobTraceSession.fileName";//$NON-NLS-1$

    private JobTraceSession jobTraceSession;
    private IPropertyDescriptor[] propertyDescriptors;

    public JobTraceSessionPropertySource(JobTraceSession jobTraceSession) {
        this.jobTraceSession = jobTraceSession;
    }

    public Object getEditableValue() {
        return null;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {

        if (propertyDescriptors == null) {

            PropertyDescriptor connectionName = createPropertyDescriptor(PROPERTY_CONNECTION_NAME, Messages.Property_connection_name, null);

            // "Job" descriptors
            PropertyDescriptor libraryName = createPropertyDescriptor(PROPERTY_LIBRARY_NAME, Messages.Property_library_name, null);

            PropertyDescriptor sessionID = createPropertyDescriptor(PROPERTY_SESSION_ID, Messages.Property_session_ID, null);

            PropertyDescriptor whereClause = createPropertyDescriptor(PROPERTY_WHERE_CLAUSE, Messages.Property_where_clause, null);

            PropertyDescriptor isIBMDataExcluded = createPropertyDescriptor(PROPERTY_IS_IBM_DATA_EXCLUDED, Messages.Property_IBM_data_excluded, null);

            PropertyDescriptor fileName = createPropertyDescriptor(PROPERTY_FILE_NAME, Messages.Property_file_name, null);

            // Read-only (instance of PropertyDescriptor)
            propertyDescriptors = new IPropertyDescriptor[] { connectionName, libraryName, sessionID, whereClause, isIBMDataExcluded, fileName };
        }

        return propertyDescriptors;
    }

    private PropertyDescriptor createPropertyDescriptor(Object id, String displayName, String category) {

        PropertyDescriptor descriptor = new PropertyDescriptor(id, displayName);
        descriptor.setCategory(null);

        return descriptor;
    }

    public Object getPropertyValue(Object name) {
        if (name.equals(PROPERTY_CONNECTION_NAME)) {
            return jobTraceSession.getConnectionName();
        } else if (name.equals(PROPERTY_LIBRARY_NAME)) {
            return jobTraceSession.getLibraryName();
        } else if (name.equals(PROPERTY_SESSION_ID)) {
            return jobTraceSession.getSessionID();
        } else if (name.equals(PROPERTY_WHERE_CLAUSE)) {
            return jobTraceSession.getWhereClause();
        } else if (name.equals(PROPERTY_IS_IBM_DATA_EXCLUDED)) {
            return !jobTraceSession.isIBMDataExcluded();
        } else if (name.equals(PROPERTY_FILE_NAME)) {
            return jobTraceSession.getFileName();
        } else {
            return null;
        }
    }

    public boolean isPropertySet(Object arg0) {
        return false;
    }

    public void resetPropertyValue(Object arg0) {
    }

    public void setPropertyValue(Object arg0, Object arg1) {
    }

}
