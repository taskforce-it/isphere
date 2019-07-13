/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.taskdef;

import java.util.Vector;

import org.apache.tools.ant.Task;

import biz.isphere.antcontrib.sf.SFClient;
import biz.isphere.antcontrib.utils.FileUtil;

public class SF extends Task {

    private String user;
    private String password;
    private String host;
    private String remoteDir;
    private int port;
    private boolean trust;
    private boolean dryRun;

    private Vector<Object> actions;

    public SF() {

        // required attributes
        this.user = null;
        this.password = null;
        this.host = null;

        // optional attributes
        this.remoteDir = ".";
        this.port = 22;
        this.trust = false;
        this.dryRun = true;

        this.actions = new Vector<Object>();
    }

    public void execute() {

        SFClient client = null;

        try {

            System.out.println("User:  " + user);
            System.out.println("Host:  " + host);
            System.out.println("Port:  " + port);
            System.out.println("Path:  " + remoteDir);
            System.out.println("Trust: " + trust);

            client = new SFClient(host, user);
            client.setPassword(password);
            client.setStrictHostKeyChecking(!trust);
            client.setDryRun(isDryRun());

            if (client.isDryRun()) {
                System.out.println("*** dry-run ***");
            }

            client.connect();

            if (".".equals(remoteDir)) {
            	remoteDir = client.getRemoteDir();
            } else {
            	client.cd(remoteDir);
            }

            for (Object action : actions) {
                if (action instanceof Rmdir) {
                    Rmdir rmDir = (Rmdir)action;
                    rmDir.execute(client);
                } else if (action instanceof Copydir) {
                    Copydir copyDir = (Copydir)action;
                    copyDir.execute(client);
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

            if (client != null) {
                client.disconnect();
            }
        }
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setRemoteDir(String remoteDir) {
        this.remoteDir = FileUtil.trimDirectory(remoteDir);
    }

    public void setTrust(boolean trust) {
        this.trust = trust;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public Rmdir createRmdir() {

        Rmdir rmDir = new Rmdir(this);
        actions.add(rmDir);

        return rmDir;
    }

    public Copydir createCopydir() {

        Copydir copyDir = new Copydir(this);
        actions.add(copyDir);

        return copyDir;
    }

    private boolean isDryRun() {
        return dryRun;
    }

}
