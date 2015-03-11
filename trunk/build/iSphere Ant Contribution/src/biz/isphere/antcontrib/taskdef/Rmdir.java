package biz.isphere.antcontrib.taskdef;

import biz.isphere.antcontrib.sf.SFAbstractCmd;
import biz.isphere.antcontrib.sf.SFClient;
import biz.isphere.antcontrib.sf.SFException;
import biz.isphere.antcontrib.sf.SFFileListener;
import biz.isphere.antcontrib.utils.FileUtil;

import com.jcraft.jsch.SftpException;

public class Rmdir extends SFAbstractCmd implements SFFileListener {

    private String dir;
    private boolean subDirs;

    public Rmdir(SF sf) {
        super(sf);
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

    protected void executeCmd(SFClient client) throws SFException {

        client.pushFileListener(this);

        try {

            client.rmDir(client, dir, subDirs);

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
