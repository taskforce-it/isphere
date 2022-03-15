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
     * Returns a remote IFS folder.
     * 
     * @param connection - Connection where the remote IFS file is stored.
     * @param folderPath - Path of the remote IFS file.
     * @return A remote IFS folder.
     */
    public static IFSRemoteFile getRemoteFolder(IBMiConnection connection, String folderPath) {
        return getRemoteFolder(connection.getHost(), folderPath);
    }

    /**
     * Returns a remote IFS folder.
     * 
     * @param host - Host where the remote IFS file is stored.
     * @param folderPath - Path of the remote IFS file.
     * @return A remote IFS folder.
     */
    public static IFSRemoteFile getRemoteFolder(IHost host, String folderPath) {

        IFSRemoteFile remoteFolder = getRemoteFileOrFolder(host, null, folderPath);
        if (remoteFolder.isDirectory()) {
            return remoteFolder;
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
    public static IFSRemoteFile getRemoteFile(IBMiConnection connection, String filePath) {
        return getRemoteFile(connection.getHost(), filePath);
    }

    /**
     * Returns a remote IFS file.
     * 
     * @param host - Host where the remote IFS file is stored.
     * @param filePath - Path of the remote IFS file.
     * @return A remote IFS file.
     */
    public static IFSRemoteFile getRemoteFile(IHost host, String filePath) {
        return getRemoteFile(host, null, filePath);
    }

    /**
     * @param connection - Connection where the remote IFS file is stored.
     * @param folderName - Parent folder that contains the requested remote IFS
     *        file. Can be set to <code>null</code>.
     * @param fileName
     * @return A remote IFS file.
     */
    public static IFSRemoteFile getRemoteFile(IBMiConnection connection, String folderName, String fileName) {
        return getRemoteFile(connection.getHost(), folderName, fileName);
    }

    /**
     * Returns a remote IFS file.
     * 
     * @param host - Host where the remote IFS file is stored.
     * @param folderName - Parent folder that contains the requested remote IFS
     *        file. Can be set to <code>null</code>.
     * @param fileName
     * @return A remote IFS file.
     */
    public static IFSRemoteFile getRemoteFile(IHost host, String folderName, String fileName) {

        IFSRemoteFile remoteFile = null;

        if (folderName == null) {
            remoteFile = getRemoteFileOrFolder(host, null, fileName);
        } else {
            IFSRemoteFile remoteFolder = getRemoteFolder(host, folderName);
            if (remoteFolder == null) {
                return null;
            }
            remoteFile = getRemoteFileOrFolder(host, remoteFolder, fileName);
        }

        if (!remoteFile.isDirectory()) {
            return remoteFile;
        }

        return null;
    }

    /**
     * Returns a remote IFS file or folder.
     * 
     * @param host - Host where the remote IFS file or folder is stored.
     * @param folder - Parent folder that contains the requested remote IFS file
     *        or folder. Can be set to <code>null</code>.
     * @param fileOrFolderName - Requested IFS remote file or folder.
     * @return A remote IFS file or folder.
     */
    private static IFSRemoteFile getRemoteFileOrFolder(IHost host, IRemoteFile folder, String fileOrFolderName) {

        try {

            IFSFileServiceSubSystem subSystem = getIFSFileServiceSubsystem(host);
            IRemoteFile remoteObject;
            if (folder == null) {
                remoteObject = subSystem.getRemoteFileObject(fileOrFolderName, new NullProgressMonitor());
            } else {
                remoteObject = subSystem.getRemoteFileObject(folder, fileOrFolderName, new NullProgressMonitor());
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
