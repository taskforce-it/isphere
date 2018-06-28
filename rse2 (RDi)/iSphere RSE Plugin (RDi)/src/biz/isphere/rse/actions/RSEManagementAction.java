/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.rse.handler.OpenRSEFilterManagementHandler;

public class RSEManagementAction implements IViewActionDelegate {
    
	public void init(IViewPart viewPart) {
	}

	public void run(IAction action) {

        try {

            OpenRSEFilterManagementHandler handler = new OpenRSEFilterManagementHandler();
            ExecutionEvent event = new ExecutionEvent();
            handler.execute(event);

        } catch (ExecutionException e) {
            ISpherePlugin.logError("Failed to open the RSE filter management.", e);
        }
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}
