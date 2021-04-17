/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import biz.isphere.core.externalapi.Access;
import biz.isphere.core.internal.ISeries;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteObject;

public class DataAreaEditorAction implements IObjectActionDelegate {

    protected IStructuredSelection structuredSelection;
    protected Shell shell;

    public void run(IAction arg0) {
        if (structuredSelection != null) {
            Iterator<?> iter = structuredSelection.iterator();
            while (iter.hasNext()) {
                Object object = iter.next();
                if (object instanceof QSYSRemoteObject) {
                    run((QSYSRemoteObject)object);
                }
            }
        }
    }

    private void run(QSYSRemoteObject qsysRemoteObject) {

        if (qsysRemoteObject.getType().equals(ISeries.DTAARA)) {

            String profil = qsysRemoteObject.getRemoteObjectContext().getObjectSubsystem().getObjectSubSystem().getSystemProfileName();
            String connectionName = qsysRemoteObject.getRemoteObjectContext().getObjectSubsystem().getObjectSubSystem().getHostAliasName();

            String dataArea = qsysRemoteObject.getName();
            String library = qsysRemoteObject.getLibrary();
            IBMiConnection ibmiConnection = IBMiConnection.getConnection(profil, connectionName);

            if (ibmiConnection != null) {

                AS400 as400 = null;
                try {
                    as400 = ibmiConnection.getAS400ToolboxObject();
                } catch (SystemMessageException e) {
                }

                if (as400 != null) {
                    Access.openDataAreaEditor(shell, connectionName, library, dataArea, false);
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
