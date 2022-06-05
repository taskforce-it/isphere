/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.lpex.action;

import biz.isphere.strpreprc.Messages;
import biz.isphere.strpreprc.gui.EditHeaderDialog;
import biz.isphere.strpreprc.model.StrPrePrcParser;

/**
 * This action adds a pre-compile command to an existing STRPREPRC header.
 */
public class AddPreCompileCommandAction extends AbstractAddPreCompileCommandAction {

    public static final String ID = "SprPrePrc.AddPreCompileCommand";

    protected EditHeaderDialog createEditDialog() {
        return new EditHeaderDialog(getShell(), Messages.Menu_Add_pre_compile_command, EditHeaderDialog.PRE_POST_COMMAND);
    }

    @Override
    protected void addCompileCommand(StrPrePrcParser header, String commandString) {
        header.addPreCompileCommand(commandString);
    }

    public static String getLPEXMenuAction() {
        return "\"" + Messages.Menu_Add_pre_compile_command + "\" " + AddPreCompileCommandAction.ID; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
