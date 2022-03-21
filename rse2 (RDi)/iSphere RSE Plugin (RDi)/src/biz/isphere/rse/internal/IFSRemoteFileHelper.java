/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;

import com.ibm.etools.iseries.subsystems.ifs.files.IFSFileServiceSubSystem;
import com.ibm.etools.iseries.subsystems.ifs.files.IFSRemoteFile;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public final class IFSRemoteFileHelper {

    private IFSRemoteFileHelper() {
    }

    /**
     * Returns a remote IFS directory.
     * 
     * @param connection - Connection where the remote IFS file is stored.
     * @param directoryPath - Path of the remote IFS file.
     * @return A remote IFS directory.
     */
    public static IFSRemoteFile getRemoteDirectory(IBMiConnection connection, String directoryPath) {
        return getRemoteDirectory(connection.getHost(), directoryPath);
    }

    /**
     * Returns a remote IFS directory.
     * 
     * @param host - Host where the remote IFS file is stored.
     * @param directoryPath - Path of the remote IFS file.
     * @return A remote IFS directory.
     */
    public static IFSRemoteFile getRemoteDirectory(IHost host, String directoryPath) {

        IFSRemoteFile remoteDirectory = getRemoteStreamFileOrDirectory(host, null, directoryPath);
        if (remoteDirectory.isDirectory()) {
            return remoteDirectory;
        }

        return null;
    }

    /**
     * Returns a remote IFS file.
     * 
     * @param connection - Connection where the remote IFS file is stored.
     * @param filePath - Path of the remote IFS file.
     * @return A remote IFS file.
     */
    public static IFSRemoteFile getRemoteStreamFile(IBMiConnection connection, String filePath) {
        return getRemoteStreamFile(connection.getHost(), filePath);
    }

    /**
     * Returns a remote IFS file.
     * 
     * @param host - Host where the remote IFS file is stored.
     * @param filePath - Path of the remote IFS file.
     * @return A remote IFS file.
     */
    public static IFSRemoteFile getRemoteStreamFile(IHost host, String filePath) {
        return getRemoteStreamFile(host, null, filePath);
    }

    /**
     * @param connection - Connection where the remote IFS file is stored.
     * @param directoryName - Parent directory that contains the requested remote IFS
     *        file. Can be set to <code>null</code>.
     * @param fileName
     * @return A remote IFS file.
     */
    public static IFSRemoteFile getRemoteStreamFile(IBMiConnection connection, String directoryName, String fileName) {
        return getRemoteStreamFile(connection.getHost(), directoryName, fileName);
    }

    /**
     * Returns a remote IFS file.
     * 
     * @param host - Host where the remote IFS file is stored.
     * @param directoryName - Parent directory that contains the requested remote IFS
     *        file. Can be set to <code>null</code>.
     * @param fileName
     * @return A remote IFS file.
     */
    public static IFSRemoteFile getRemoteStreamFile(IHost host, String directoryName, String fileName) {

        IFSRemoteFile remoteFile = null;

        if (directoryName == null) {
            remoteFile = getRemoteStreamFileOrDirectory(host, null, fileName);
        } else {
            IFSRemoteFile remoteDirectory = getRemoteDirectory(host, directoryName);
            if (remoteDirectory == null) {
                return null;
            }
            remoteFile = getRemoteStreamFileOrDirectory(host, remoteDirectory, fileName);
        }

        if (!remoteFile.isDirectory()) {
            return remoteFile;
        }

        return null;
    }

    /**
     * Returns a remote IFS file or directory.
     * 
     * @param host - Host where the remote IFS file or directory is stored.
     * @param directory - Parent directory that contains the requested remote IFS file
     *        or directory. Can be set to <code>null</code>.
     * @param fileOrDirectoryName - Requested IFS remote file or directory.
     * @return A remote IFS file or directory.
     */
    private static IFSRemoteFile getRemoteStreamFileOrDirectory(IHost host, IRemoteFile directory, String fileOrDirectoryName) {

        try {

            IFSFileServiceSubSystem subSystem = getIFSFileServiceSubsystem(host);
            IRemoteFile remoteObject;
            if (directory == null) {
                remoteObject = subSystem.getRemoteFileObject(fileOrDirectoryName, new NullProgressMonitor());
            } else {
                remoteObject = subSystem.getRemoteFileObject(directory, fileOrDirectoryName, new NullProgressMonitor());
            }
            if (remoteObject instanceof IFSRemoteFile) {
                return (IFSRemoteFile)remoteObject;
            }

        } catch (SystemMessageException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns the IFS file service subsystem of a given connection.
     * 
     * @param connection - Connection whose IFS file service subsystem is
     *        returned.
     * @return IFS file service subsystem of the connection.
     */
    public static IFSFileServiceSubSystem getIFSFileServiceSubsystem(IBMiConnection connection) {
        return getIFSFileServiceSubsystem(connection.getHost());
    }

    /**
     * Returns the IFS file service subsystem of a given host.
     * 
     * @param host - Host whose IFS file service subsystem is returned.
     * @return IFS file service subsystem of the host.
     */
    public static IFSFileServiceSubSystem getIFSFileServiceSubsystem(IHost host) {

        ISubSystem[] subSystems = host.getSubSystems();
        for (ISubSystem iSubSystem : subSystems) {
            if (iSubSystem instanceof IFSFileServiceSubSystem) {
                return (IFSFileServiceSubSystem)iSubSystem;
            }
        }

        return null;
    }
}
