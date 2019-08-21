/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

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
