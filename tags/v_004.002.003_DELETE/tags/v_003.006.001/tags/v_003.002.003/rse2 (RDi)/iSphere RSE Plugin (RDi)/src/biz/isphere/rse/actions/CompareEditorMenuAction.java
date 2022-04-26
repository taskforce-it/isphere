/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;
import biz.isphere.rse.compareeditor.handler.CompareSourceMembersHandler;
import biz.isphere.rse.internal.RSEMember;

public class CompareEditorMenuAction implements IWorkbenchWindowActionDelegate {

    public static final String ID = "biz.isphere.rse.actions.CompareEditorMenuAction";

    public void run(IAction arg0) {

        try {

            CompareSourceMembersHandler handler = new CompareSourceMembersHandler();
            handler.handleSourceCompare(new RSEMember[0]);

        } catch (Exception e) {
            ISphereRSEPlugin.logError(biz.isphere.core.Messages.Unexpected_Error, e);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }

    public void dispose() {
    }

    public void init(IWorkbenchWindow window) {
    }

    private Shell getShell() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    }
}
