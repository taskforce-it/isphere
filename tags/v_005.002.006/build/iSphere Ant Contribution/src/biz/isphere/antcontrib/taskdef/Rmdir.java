/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.taskdef;

import java.util.Vector;

import biz.isphere.antcontrib.sf.SFAbstractCmd;
import biz.isphere.antcontrib.sf.SFClient;
import biz.isphere.antcontrib.sf.SFException;
import biz.isphere.antcontrib.sf.SFFileListener;
import biz.isphere.antcontrib.utils.FileUtil;

import com.jcraft.jsch.SftpException;

public class Rmdir extends SFAbstractCmd implements SFFileListener {

    private String dir;
    private boolean subDirs;
    private Vector<IgnoreFile> ignoreFiles;

    public Rmdir(SF sf) {
        super(sf);

        // optional attributes
        this.dir = ".";
        this.subDirs = false;
        this.ignoreFiles = new Vector<IgnoreFile>();
    }

    public void setDir(String dir) throws SFException {

        if ("..".equals(dir)) {
            throw new SFException("Invalid directory name: '" + dir + "'");
        }

        this.dir = FileUtil.trimDirectory(dir);
    }

    public void setSubDirs(boolean subDirs) {
        this.subDirs = subDirs;
    }

    public IgnoreFile createIgnoreFile() {

        IgnoreFile ignoreFile = new IgnoreFile(this);
        ignoreFiles.add(ignoreFile);

        return ignoreFile;
    }
    
    public IgnoreFile[] getIgnoredFiles() {
        return ignoreFiles.toArray(new IgnoreFile[ignoreFiles.size()]);
    }

    protected void executeCmd(SFClient client) throws SFException {

        client.pushFileListener(this);

        try {

            client.rmDir(client, dir, subDirs, getIgnoredFiles());

        } catch (SftpException e) {
            throw new SFException("Failed to remove directory.", e);
        } finally {
            client.popFileListener(this);
        }

    }

    public void executingFileCommand(String command, String filename, String info) {
        System.out.println(command + ": " + filename + " " + info);
    }
}
