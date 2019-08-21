/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.common.ui.action.WorkbenchWindowActionDelegate;
import org.eclipse.jface.action.IAction;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.rse.handler.OpenRSEUserActionManagementHandler;

public class OpenRSEUserActionManagementAction extends WorkbenchWindowActionDelegate {

    public static final String ID = "biz.isphere.rse.actions.OpenRSEUserActionManagementAction";

    public void run(IAction action) {

        try {

            OpenRSEUserActionManagementHandler handler = new OpenRSEUserActionManagementHandler();
            ExecutionEvent event = new ExecutionEvent();
            handler.execute(event);

        } catch (ExecutionException e) {
            ISpherePlugin.logError("Failed to open the RSE user action management.", e);
        }
    }
}
