/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.lpex.action;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.strpreprc.Messages;

import com.ibm.lpex.core.LpexView;

/**
 * This action removes the STRPREPRC header from a source member.
 */
public class AddPreCompileCommandAction extends AbstractHeaderAction {

    public static final String ID = "SprPrePrc.AddPreCompileCommand";

    public void doAction(LpexView view) {

        try {

            System.out.println("Adding pre-compile command ...");

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Unexpected error when attempting to add a pre-compile command ***", e); //$NON-NLS-1$
        }
    }

    public static String getLPEXMenuAction() {
        return "\"" + Messages.Menu_Add_pre_compile_command + "\" " + AddPreCompileCommandAction.ID; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
