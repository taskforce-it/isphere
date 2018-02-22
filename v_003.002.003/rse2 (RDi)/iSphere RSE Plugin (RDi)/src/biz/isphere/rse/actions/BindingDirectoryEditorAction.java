/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.sql.Connection;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteObject;

import biz.isphere.core.bindingdirectoryeditor.BindingDirectoryEditor;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.IEditor;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.RemoteObject;

public class BindingDirectoryEditorAction implements IObjectActionDelegate {

    protected IStructuredSelection structuredSelection;
    protected Shell shell;

    public void run(IAction arg0) {

        if (structuredSelection != null && !structuredSelection.isEmpty()) {

            Object object = structuredSelection.getFirstElement();

            if (object instanceof QSYSRemoteObject) {

                QSYSRemoteObject qsysRemoteObject = (QSYSRemoteObject)object;

                if (qsysRemoteObject.getType().equals(ISeries.BNDDIR)) {

                    String profile = qsysRemoteObject.getRemoteObjectContext().getObjectSubsystem().getObjectSubSystem().getSystemProfileName();
                    String connectionName = qsysRemoteObject.getRemoteObjectContext().getObjectSubsystem().getObjectSubSystem().getHostAliasName();

                    String bindingDirectory = qsysRemoteObject.getName();
                    String library = qsysRemoteObject.getLibrary();
                    String objectType = qsysRemoteObject.getType();
                    String description = qsysRemoteObject.getDescription();

                    AS400 as400 = IBMiHostContributionsHandler.getSystem(profile, connectionName);
                    Connection jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(profile, connectionName);
                    if (as400 != null && jdbcConnection != null) {

                        RemoteObject remoteObject = new RemoteObject(connectionName, bindingDirectory, library, objectType, description);
                        BindingDirectoryEditor.openEditor(as400, jdbcConnection, remoteObject, IEditor.EDIT);
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
