/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
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
     * Tests is a remote IFS directory exists.
     * 
     * @param connection - Connection where the remote IFS file is stored.
     * @param directoryPath - Path of the remote IFS file.
     * @return A remote IFS directory.
     */
    public static boolean checkRemoteDirectory(IBMiConnection connection, String directoryPath) {
        return checkRemoteDirectory(connection.getHost(), directoryPath);
    }

    /**
     * Tests is a remote IFS directory exists.
     * 
     * @param host - Host where the remote IFS file is stored.
     * @param directoryPath - Path of the remote IFS file.
     * @return A remote IFS directory.
     */
    public static boolean checkRemoteDirectory(IHost host, String directoryPath) {

        IFSRemoteFile remoteDirectory = getRemoteDirectory(host, directoryPath);
        if (remoteDirectory != null) {
            return true;
        }

        return false;
    }

    /**
     * Tests if a remote IFS file exists.
     * 
     * @param connection - Connection where the remote IFS file is stored.
     * @param filePath - Path of the remote IFS file.
     * @return A remote IFS file.
     */
    public static boolean checkRemoteStreamFile(IBMiConnection connection, String filePath) {
        return checkRemoteStreamFile(connection.getHost(), filePath);
    }

    /**
     * Tests if a remote IFS file exists.
     * 
     * @param host - Host where the remote IFS file is stored.
     * @param filePath - Path of the remote IFS file.
     * @return A remote IFS file.
     */
    public static boolean checkRemoteStreamFile(IHost host, String filePath) {
        return checkRemoteStreamFile(host, null, filePath);
    }

    /**
     * Tests if a remote IFS file exists.
     * 
     * @param connection - Connection where the remote IFS file is stored.
     * @param directoryName - Parent directory that contains the requested
     *        remote IFS file. Can be set to <code>null</code>.
     * @param fileName
     * @return A remote IFS file.
     */
    public static boolean checkRemoteStreamFile(IBMiConnection connection, String directoryName, String fileName) {
        return checkRemoteStreamFile(connection.getHost(), directoryName, fileName);
    }

    /**
     * Tests if a remote IFS file exists.
     * 
     * @param host - Host where the remote IFS file is stored.
     * @param directoryName - Parent directory that contains the requested
     *        remote IFS file. Can be set to <code>null</code>.
     * @param fileName
     * @return A remote IFS file.
     */
    public static boolean checkRemoteStreamFile(IHost host, String directoryName, String fileName) {

        IFSRemoteFile remoteFile = getRemoteStreamFile(host, directoryName, fileName);
        if (remoteFile != null) {
            return true;
        }

        return false;
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
     * Returns a remote IFS file.
     * 
     * @param connection - Connection where the remote IFS file is stored.
     * @param directoryName - Parent directory that contains the requested
     *        remote IFS file. Can be set to <code>null</code>.
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
     * @param directoryName - Parent directory that contains the requested
     *        remote IFS file. Can be set to <code>null</code>.
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
     * Lists the files and sub-directories of a given directory.
     * 
     * @param parent - A directory.
     * @param fileType - Files types that are listed. One of
     *        <code>IFileService.FILE_TYPE_FILES</code>,
     *        <code>IFileService.FILE_TYPE_FILES_AND_FOLDERS</code> or
     *        <code>IFileService.FILE_TYPE_FOLDERS</code>.
     * @param isResolveSubDirectories - Indicates whether sub-directories are
     *        resolved.
     * @return list of remote files
     */
    public static List<IRemoteFile> listFiles(IRemoteFile parent, int fileType, boolean isResolveSubDirectories) {
        return listFiles(parent, fileType, isResolveSubDirectories, new NullProgressMonitor());
    }

    /**
     * Lists the files and sub-directories of a given directory.
     * 
     * @param parent - A directory.
     * @param fileType - Files types that are listed. One of
     *        <code>IFileService.FILE_TYPE_FILES</code>,
     *        <code>IFileService.FILE_TYPE_FILES_AND_FOLDERS</code> or
     *        <code>IFileService.FILE_TYPE_FOLDERS</code>.
     * @param isResolveSubDirectories - Indicates whether sub-directories are
     *        resolved.
     * @param IProgressMonitor - Progress monitor.
     * @return list of remote files
     */
    public static List<IRemoteFile> listFiles(IRemoteFile parent, int fileType, boolean isResolveSubDirectories, IProgressMonitor monitor) {

        List<IRemoteFile> files = new LinkedList<IRemoteFile>();

        try {

            IRemoteFile[] remoteFiles = IFSRemoteFileHelper.getIFSFileServiceSubsystem(parent.getHost()).list(parent, fileType,
                new NullProgressMonitor());
            for (IRemoteFile iRemoteFile : remoteFiles) {
                if (iRemoteFile.isFile()) {
                    files.add(iRemoteFile);
                } else if (iRemoteFile.isDirectory()) {
                    List<IRemoteFile> iRemoteFiles = listFiles(iRemoteFile, fileType, isResolveSubDirectories, monitor);
                    files.addAll(iRemoteFiles);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return files;
    }

    /**
     * Returns a remote IFS file or directory.
     * 
     * @param host - Host where the remote IFS file or directory is stored.
     * @param directory - Parent directory that contains the requested remote
     *        IFS file or directory. Can be set to <code>null</code>.
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
