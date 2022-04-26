/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.clcommands;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.clcommands.ICLPrompter;

import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.util.clprompter.CLPrompter;

public class ICLPrompterImpl implements ICLPrompter {

    private CLPrompter prompter;

    public ICLPrompterImpl(CLPrompter prompter) {
        this.prompter = prompter;
    }

    public void setCommandString(String commandString) {
        prompter.setCommandString(commandString);
    }

    public void setMode(int mode) {
        prompter.setMode(mode);
    }

    public void setConnection(String connectionName) {
        prompter.setConnection(ISeriesConnection.getConnection(connectionName));
    }

    public void setParent(Shell parent) {
        prompter.setParent(parent);
    }

    public int showDialog() {
        return prompter.showDialog();
    }

    public String getCommandString() {
        return prompter.getCommandString();
    }

    public String testSyntax() {
        return prompter.testSyntax().getLevelOneText();
    }

}
