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
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import biz.isphere.core.internal.handler.TransferLibraryHandler;
import biz.isphere.core.preferences.Preferences;

/**
 * This action is assigned to menu option "Transfer iSphere Library".
 */
public class TransferLibraryAction implements IWorkbenchWindowActionDelegate {

    public static final String ID = "biz.isphere.rse.actions.TransferLibraryAction";

    public void run(IAction action) {
        try {

            String hostName = Preferences.getInstance().getHostName();
            int port = Preferences.getInstance().getFtpPortNumber();
            String iSphereLibrary = Preferences.getInstance().getISphereLibrary(); // CHECKED
            String aspGroup = Preferences.getInstance().getASPGroup();

            TransferLibraryHandler handler = new TransferLibraryHandler(hostName, port, iSphereLibrary, aspGroup);
            ExecutionEvent event = new ExecutionEvent();
            handler.execute(event);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }

    public void dispose() {
    }

    public void init(IWorkbenchWindow window) {
    }
}
