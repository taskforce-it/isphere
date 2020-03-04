/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.taskdef;

import biz.isphere.antcontrib.sf.SFAbstractCmd;
import biz.isphere.antcontrib.sf.SFClient;
import biz.isphere.antcontrib.sf.SFException;
import biz.isphere.antcontrib.sf.SFFileListener;
import biz.isphere.antcontrib.utils.FileUtil;

import com.jcraft.jsch.SftpException;

public class Copydir extends SFAbstractCmd implements SFFileListener {

    private String dir;
    private String toDir;
    private boolean subDirs;

    public Copydir(SF sf) {
        super(sf);
        
        // optional attributes
        this.dir = ".";
        this.toDir = ".";
        this.subDirs = false;
    }

    public void setDir(String dir) throws SFException {

        if ("..".equals(dir)) {
            throw new SFException("Invalid directory name: '" + dir + "'");
        }

        this.dir = FileUtil.trimDirectory(dir);
    }

    public void setToDir(String toDir) throws SFException {

        if ("..".equals(toDir)) {
            throw new SFException("Invalid directory name: '" + toDir + "'");
        }

        this.toDir = FileUtil.trimDirectory(toDir);
    }

    public void setSubDirs(boolean subDirs) {
        this.subDirs = subDirs;
    }

    protected void executeCmd(SFClient client) throws SFException {

        client.pushFileListener(this);

        try {

            client.copyDir(client, getDir(), getToDir(), subDirs);

        } catch (SftpException e) {
            throw new SFException("Failed to copy directory.", e);
        } finally {
            client.popFileListener(this);
        }

    }

    public void executingFileCommand(String command, String filename, String info) {
        System.out.println(command + ": " + filename + " " + info);
    }

    private String getDir() throws SFException {

        if (dir == null) {
            throw new SFException("Attribute 'dir' not set.");
        }

        return dir;
    }

    private String getToDir() throws SFException {

        if (toDir == null) {
            return getDir();
        }

        return toDir;
    }
}
