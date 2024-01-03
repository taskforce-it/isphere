/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.messagefilesearch;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.rse.core.model.SystemMessageObject;
import org.eclipse.swt.widgets.Shell;

import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;
import com.ibm.etools.iseries.rse.ui.ResourceTypeUtil;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMessageFile;
import com.ibm.etools.iseries.services.qsys.api.IQSYSResource;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSObjectSubSystem;

import biz.isphere.core.internal.exception.InvalidFilterException;
import biz.isphere.core.messagefilesearch.AbstractMessageFileSearchDelegate;

/**
 * This class adds individual objects or resolves filter strings in order to add
 * the matching objects to the list of searched objects.
 */
public class MessageFileSearchDelegate extends AbstractMessageFileSearchDelegate {

    private IBMiConnection connection;

    public MessageFileSearchDelegate(Shell shell, IBMiConnection connection) {
        this(shell, connection, new NullProgressMonitor());
    }

    public MessageFileSearchDelegate(Shell shell, IBMiConnection connection, IProgressMonitor monitor) {
        super(shell, monitor);

        this.connection = connection;
    }

    protected String produceFilterString(String library, String messageFile, String objectType) {

        ISeriesObjectFilterString objectFilterString = new ISeriesObjectFilterString();
        objectFilterString.setObject(messageFile);
        objectFilterString.setObjectType(objectType); // $NON-NLS-1$
        objectFilterString.setLibrary(library);

        return objectFilterString.toString();
    }

    protected Object[] resolveFilterString(String filterString) throws Exception {

        QSYSObjectSubSystem objectSubSystem = connection.getQSYSObjectSubSystem();
        return objectSubSystem.resolveFilterString(filterString, null);
    }

    protected void throwSystemErrorMessage(final Object message) throws InvalidFilterException {
        throw new InvalidFilterException(((SystemMessageObject)message).getMessage());
    }

    protected boolean isSystemMessageObject(Object object) {
        return (object instanceof SystemMessageObject);
    }

    protected boolean isLibrary(Object object) {
        return ResourceTypeUtil.isLibrary(object);
    }

    protected boolean isMessageFile(Object object) {
        return ResourceTypeUtil.isMessageFile(object);
    }

    protected String getResourceLibrary(Object resource) {
        return ((IQSYSResource)resource).getLibrary();
    }

    protected String getResourceName(Object resource) {
        return ((IQSYSResource)resource).getName();
    }

    protected String getResourceDescription(Object resource) {
        return ((IQSYSMessageFile)resource).getDescription();
    }
}
