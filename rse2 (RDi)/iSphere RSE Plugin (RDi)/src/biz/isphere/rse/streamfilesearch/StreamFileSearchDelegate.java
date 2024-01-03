/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.streamfilesearch;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.rse.core.model.SystemMessageObject;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFileSubSystemConfiguration;
import org.eclipse.swt.widgets.Shell;

import com.ibm.etools.iseries.subsystems.ifs.files.IFSFileFilterString;
import com.ibm.etools.iseries.subsystems.ifs.files.IFSFileServiceSubSystem;
import com.ibm.etools.iseries.subsystems.ifs.files.IFSRemoteFile;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

import biz.isphere.core.internal.exception.InvalidFilterException;
import biz.isphere.core.streamfilesearch.AbstractStreamFileSearchDelegate;
import biz.isphere.rse.internal.IFSRemoteFileHelper;

/**
 * This class adds individual objects or resolves filter strings in order to add
 * the matching objects to the list of searched objects.
 */
public class StreamFileSearchDelegate extends AbstractStreamFileSearchDelegate {

    private IBMiConnection connection;

    public StreamFileSearchDelegate(Shell shell, IBMiConnection connection) {
        this(shell, connection, null);
    }

    public StreamFileSearchDelegate(Shell shell, IBMiConnection connection, IProgressMonitor monitor) {
        super(shell, monitor);

        this.connection = connection;
    }

    @Override
    protected String produceStreamFileFilterString(String path, String file) {

        IFSFileFilterString streamFileFilterString = new IFSFileFilterString();
        streamFileFilterString.setPath(path);
        streamFileFilterString.setFile(file);

        return streamFileFilterString.toString();
    }

    protected IFSFileFilterString produceStreamFileFilter(String filterString) {

        IRemoteFileSubSystemConfiguration configuration = IFSRemoteFileHelper.getIFSFileServiceSubsystem(connection)
            .getParentRemoteFileSubSystemConfiguration();
        IFSFileFilterString streamFileFilter = new IFSFileFilterString(configuration, filterString);

        return streamFileFilter;
    }

    @Override
    protected Object[] resolveFilterString(String filterString) throws Exception {

        IFSFileServiceSubSystem fileServiceSubSystem = IFSRemoteFileHelper.getIFSFileServiceSubsystem(connection);
        return fileServiceSubSystem.resolveFilterString(filterString, new NullProgressMonitor());
    }

    protected void throwSystemErrorMessage(final Object message) throws InvalidFilterException {
        throw new InvalidFilterException(((SystemMessageObject)message).getMessage());
    }

    protected boolean isSystemMessageObject(Object object) {
        return (object instanceof SystemMessageObject);
    }

    protected boolean isDirectory(Object object) {
        if (object instanceof IFSRemoteFile) {
            return ((IFSRemoteFile)object).isDirectory();
        }
        return false;
    }

    protected boolean isStreamFile(Object object) {
        if (object instanceof IFSRemoteFile) {
            return ((IFSRemoteFile)object).isFile();
        }
        return false;
    }

    protected String getFileOrTypesFromFilterString(String filterString) {

        IFSFileFilterString streamFileFilter = produceStreamFileFilter(filterString);
        return streamFileFilter.getFileOrTypes();
    }

    protected String getResourcePath(Object resource) {
        IFSFileServiceSubSystem fileServiceSubSystem = IFSRemoteFileHelper.getIFSFileServiceSubsystem(connection);
        return getResourceDirectory(resource) + fileServiceSubSystem.getSeparator() + getResourceName(resource);
    }

    protected String getResourceDirectory(Object resource) {
        return ((IFSRemoteFile)resource).getParentPath();
    }

    protected String getResourceName(Object resource) {
        return ((IFSRemoteFile)resource).getName();
    }

    protected String getResourceType(Object resource) {
        return ((IFSRemoteFile)resource).getExtension();
    }
}
