/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.sf;

import java.io.File;
import java.util.Stack;
import java.util.Vector;

import biz.isphere.antcontrib.taskdef.IgnoreFile;
import biz.isphere.antcontrib.utils.FileUtil;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

public class SFClient {

    private String host;
    private String password;
    private String user;

    private int port;
    private boolean strictHostKeyChecking;
    private boolean dryRun;

    private Session session;
    private ChannelSftp channel;

    private Stack<SFFileListener> fileListener;

    public SFClient(String host, String user) {
        this(host, user, null);
    }

    public SFClient(String host, String user, String password) {
        this.host = host;
        this.user = user;
        this.password = password;

        this.port = 22;
        this.strictHostKeyChecking = true;
        this.dryRun = true;

        this.fileListener = new Stack<SFFileListener>();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStrictHostKeyChecking(boolean enabled) {
        this.strictHostKeyChecking = enabled;
    }

    public void setDryRun(boolean enabled) {
        this.dryRun = enabled;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void connect() throws JSchException {

        JSch jsch = new JSch();
        session = jsch.getSession(user, host, port);

        if (password != null) {
            session.setPassword(password);
        }

        // this setting will cause JSCH to automatically add all target
        // servers' entry to the known_hosts file
        if (!strictHostKeyChecking) {
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
        }

        session.setUserInfo(new SFUserInfo());
        session.connect();

        Channel c = session.openChannel("sftp");
        c.connect();
        channel = (ChannelSftp)c;
    }

    public void cd(String remoteDir) throws SftpException {

        channel.cd(remoteDir);
    }

    public String getRemoteDir() throws SftpException {

        return channel.pwd();
    }

    public void rmDir(SFClient c, String directory, boolean subDirs, IgnoreFile[] ignoredFiles) throws SftpException, SFException {

        directory = getRemoteDirectory(directory);

        try {
            SftpATTRS attrs = channel.lstat(directory);
        } catch (SftpException e) {
            if (e.id == 2) {
                return; // No such file
            }
            throw e;
        }

        Vector vv = channel.ls(directory);
        if (vv != null) {
            for (int ii = 0; ii < vv.size(); ii++) {
                Object obj = vv.elementAt(ii);
                if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {

                    LsEntry lsEntry = (com.jcraft.jsch.ChannelSftp.LsEntry)obj;
                    SftpATTRS attrs = lsEntry.getAttrs();

                    if (attrs.isDir() && (".".equals(lsEntry.getFilename()) || "..".equals(lsEntry.getFilename()))) {
                        // ignore
                    } else {

                        if (attrs.isDir()) {

                            if (subDirs) {
                                rmDir(c, directory + "/" + lsEntry.getFilename(), subDirs, ignoredFiles);
                            }

                        } else {

                            if (attrs.isBlk()) {
                                // fireDeleteFileListener(lsEntry.getFilename(),
                                // "(Blk)");
                            } else if (attrs.isChr()) {
                                // fireDeleteFileListener(lsEntry.getFilename(),
                                // "(Chr)");
                            } else if (attrs.isFifo()) {
                                // fireDeleteFileListener(lsEntry.getFilename(),
                                // "(Fifo)");
                            } else if (attrs.isLink()) {
                                // fireDeleteFileListener(lsEntry.getFilename(),
                                // "(Link)");
                            } else if (attrs.isReg()) {
                                deleteRemoteFile(directory + "/" + lsEntry.getFilename(), "(Reg)", ignoredFiles);
                            } else if (attrs.isSock()) {
                                // fireDeleteFileListener(lsEntry.getFilename(),
                                // "(Sock)");
                            }

                        }
                    }
                }
            }
        }

        deleteRemoteDirectory(directory);
    }

    public void copyDir(SFClient c, String directory, String toDirectory, boolean subDirs) throws SftpException, SFException {

        File localDir = new File(directory);
        if (!localDir.exists()) {
            throw new SFException("Local directory " + directory + " does not exist.");
        }

        if (!localDir.isDirectory()) {
            throw new SFException("Local directory " + directory + " does not exist.");
        }

        toDirectory = getRemoteDirectory(toDirectory);

        ensureRemoteDirectory(toDirectory);

        String[] entries = localDir.list();
        for (String entry : entries) {
            String localEntry = directory + File.separator + entry;
            String remoteEntry = toDirectory + "/" + entry;
            File localFile = new File(localEntry);
            if (localFile.isDirectory()) {
                if (subDirs && !isIgnoredDirectory(entry)) {
                    copyDir(c, localEntry, remoteEntry, subDirs);
                }
            } else {
                fireCreateFileListener(remoteEntry, "(File)");
                if (!isDryRun()) {
                    channel.put(localEntry, remoteEntry, ChannelSftp.OVERWRITE);
                }
            }
        }

    }

    private String getRemoteDirectory(String directory) throws SftpException {

        if (directory.startsWith("/")) {
            return directory;
        }

        if (directory.startsWith(".")) {
            return FileUtil.trimDirectory(getRemoteDir() + directory.substring(1));
        }

        return FileUtil.trimDirectory(getRemoteDir() + "/" + directory);
    }

    private boolean isIgnoredDirectory(String entry) {

        if (".svn".equalsIgnoreCase(entry)) {
            return true;
        }

        return false;
    }

    private boolean isIgnoredFile(IgnoreFile[] ignoredFiles, String filename) throws SFException {

        for (IgnoreFile ignoreFile : ignoredFiles) {
            if (ignoreFile.matches(filename)) {
                return true;
            }
        }

        return false;
    }

    private void ensureRemoteDirectory(String toDirectory) throws SftpException, SFException {

        try {

            SftpATTRS attrs = channel.lstat(toDirectory);
            if (attrs.isDir()) {
                return;
            }

            throw new SFException("Cannot create directory. A file with the same name exists: " + toDirectory);

        } catch (SftpException e) {

            if (e.id == 2) {
                fireCreateFileListener(toDirectory, "(Dir)");
                if (!isDryRun()) {
                    channel.mkdir(toDirectory);
                }
                return;
            }

            throw e;
        }

    }

    private void deleteRemoteDirectory(String directory) throws SftpException {

        if (directory == null) {
            return;
        }

        if (directory.equals(channel.pwd())) {
            return;
        }
        
        if (!isDirectoryEmpty(directory)) {
            return;
        }

        if (!isDryRun()) {
            channel.rmdir(directory);
        }

        fireDeleteFileListener(directory, "(Dir)");
    }

    private void deleteRemoteFile(String filename, String info, IgnoreFile[] ignoredFiles) throws SftpException, SFException {

        if (isIgnoredFile(ignoredFiles, filename)) {
            fireIgnoreFileListener(filename, info);
            return;
        }

        if (!isDryRun()) {
            channel.rm(filename);
        }

        fireDeleteFileListener(filename, info);
    }

    public void pushFileListener(SFFileListener listener) {
        fileListener.push(listener);
    }

    public void popFileListener(SFFileListener listener) throws SFException {
        if (fileListener.peek() == listener) {
            fileListener.pop();
        } else {
            throw new SFException("Listener not found on stack: " + listener);
        }
    }

    public void disconnect() {

        if (channel != null) {
            channel.disconnect();
        }

        if (session != null) {
            session.disconnect();
        }
    }

    private void fireCreateFileListener(String filename, String info) {
        fireFileListener(SFFileListener.CREATE, filename, info);
    }

    private void fireDeleteFileListener(String filename, String info) {
        fireFileListener(SFFileListener.DELETE, filename, info);
    }

    private void fireFileListener(String command, String filename, String info) {
        if (fileListener != null && fileListener.size() > 0) {
            SFFileListener listener = fileListener.peek();
            listener.executingFileCommand(command, filename, info);
        }
    }

    private void fireIgnoreFileListener(String filename, String info) {
        fireFileListener(SFFileListener.IGNORED, filename, info);
    }
    
    private boolean isDirectoryEmpty(String directory) throws SftpException {
        
        Vector vv = channel.ls(directory);
        if (vv == null || vv.size() == 0) {
            return true;
        }
        
        return false;
        
    }
}
