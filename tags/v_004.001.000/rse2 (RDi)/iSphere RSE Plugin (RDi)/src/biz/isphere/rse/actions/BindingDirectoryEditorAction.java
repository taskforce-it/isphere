/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
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

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.externalapi.Access;
import biz.isphere.core.internal.ISeries;
import biz.isphere.rse.connection.ConnectionManager;

import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteObject;

public class BindingDirectoryEditorAction implements IObjectActionDelegate {

    protected IStructuredSelection structuredSelection;
    protected Shell shell;

    public void run(IAction arg0) {

        if (structuredSelection != null && !structuredSelection.isEmpty()) {

            Object object = structuredSelection.getFirstElement();

            if (object instanceof QSYSRemoteObject) {

                QSYSRemoteObject qsysRemoteObject = (QSYSRemoteObject)object;

                if (qsysRemoteObject.getType().equals(ISeries.BNDDIR)) {

                    String qualifiedConnectionName = ConnectionManager.getConnectionName(qsysRemoteObject);
                    String bindingDirectory = qsysRemoteObject.getName();
                    String library = qsysRemoteObject.getLibrary();

                    try {
                        Access.openBindingDirectoryEditor(shell, qualifiedConnectionName, library, bindingDirectory, false);
                    } catch (Exception e) {
                        ISpherePlugin.logError("*** Could not open binding directory editor ***", e); //$NON-NLS-1$
                    }
                }
            }
        }
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
