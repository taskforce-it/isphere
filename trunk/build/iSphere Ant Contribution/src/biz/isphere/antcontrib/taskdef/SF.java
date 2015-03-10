package biz.isphere.antcontrib.taskdef;

import java.util.Vector;

import biz.isphere.antcontrib.sf.SFClient;

public class SF {

    private String user;
    private String password;
    private String host;
    private String remoteDir;
    private int port;
    private boolean trust;
    private boolean dryRun;

    private Vector<Object> actions;

    public SF() {

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

            if (remoteDir != null) {
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
        this.remoteDir = remoteDir;
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
