/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.action.rse;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public abstract class AbstractOpenJobLogExplorerAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate {

    protected Shell shell;
    private IStructuredSelection structuredSelection;

    public void run(IAction action) {

        if (structuredSelection == null || structuredSelection.isEmpty()) {
            return;
        }

        Iterator<?> iterator = structuredSelection.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            execute(object);
        }
    }

    protected abstract void execute(Object object);

    public void selectionChanged(IAction action, ISelection selection) {

        if (selection instanceof IStructuredSelection) {
            structuredSelection = ((IStructuredSelection)selection);
        } else {
            structuredSelection = null;
        }
    }

    public void dispose() {
    }

    public void init(IWorkbenchWindow window) {
        this.shell = window.getShell();
    }

    public void setActivePart(IAction action, IWorkbenchPart workbenchPart) {
        this.shell = workbenchPart.getSite().getShell();
    }
}
