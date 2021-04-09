/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.internal.handler.TransferLibraryHandler;
import biz.isphere.core.preferences.Preferences;

/**
 * This action is assigned to menu option "Transfer iSphere Library".
 */
public class TransferLibraryAction implements IWorkbenchWindowActionDelegate {

    public static final String ID = "biz.isphere.rse.actions.TransferLibraryAction";

    public TransferLibraryAction() {
        return;
    }

    public void run(IAction action) {

        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        String connectionName = Preferences.getInstance().getConnectionName();
        int ftpPort = Preferences.getInstance().getFtpPortNumber();
        String iSphereLibrary = Preferences.getInstance().getISphereLibrary(); // CHECKED
        String aspGroup = Preferences.getInstance().getASPGroup();

        TransferLibraryHandler handler = new TransferLibraryHandler();
        handler.execute(shell, connectionName, ftpPort, iSphereLibrary, aspGroup, true);
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }

    public void dispose() {
    }

    public void init(IWorkbenchWindow window) {
    }
}
