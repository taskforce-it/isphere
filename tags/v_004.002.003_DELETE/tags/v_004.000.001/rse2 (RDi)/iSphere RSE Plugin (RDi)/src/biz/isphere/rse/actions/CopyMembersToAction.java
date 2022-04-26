/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import biz.isphere.rse.handler.CopyMembersToHandler;

public class CopyMembersToAction implements IObjectActionDelegate {

    protected IStructuredSelection structuredSelection;
    protected Shell shell;

    public void run(IAction action) {

        CopyMembersToHandler handler = new CopyMembersToHandler();
        handler.execute(shell, structuredSelection);
    }

    public void selectionChanged(IAction action, ISelection selection) {

        if (selection instanceof IStructuredSelection) {
            structuredSelection = ((IStructuredSelection)selection);
        } else {
            structuredSelection = null;
        }

    }

    public void setActivePart(IAction action, IWorkbenchPart workbenchPart) {

        shell = workbenchPart.getSite().getShell();

    }

}
