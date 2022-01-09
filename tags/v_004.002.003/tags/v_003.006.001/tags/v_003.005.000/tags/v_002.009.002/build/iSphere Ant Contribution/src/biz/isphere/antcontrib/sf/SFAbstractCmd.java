package biz.isphere.antcontrib.sf;

import biz.isphere.antcontrib.taskdef.SF;

public abstract class SFAbstractCmd {

    private SF sf;

    public SFAbstractCmd(SF sf) {

        this.sf = sf;
    }

    public void execute(SFClient client) throws SFException {
        executeCmd(client);
    }

    protected abstract void executeCmd(SFClient client) throws SFException;
}
