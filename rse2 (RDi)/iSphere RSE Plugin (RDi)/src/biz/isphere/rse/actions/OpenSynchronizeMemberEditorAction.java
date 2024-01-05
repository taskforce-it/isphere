/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteObject;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.externalapi.Access;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.objectsynchronization.rse.SynchronizeMembersEditorConfiguration;
import biz.isphere.rse.Messages;
import biz.isphere.rse.connection.ConnectionManager;

public class OpenSynchronizeMemberEditorAction implements IObjectActionDelegate {

    public static final String ID = "biz.isphere.rse.actions.OpenMessageFileCompareEditorAction"; //$NON-NLS-1$

    protected IStructuredSelection structuredSelection;
    protected Shell shell;
    protected Object firstSelectedObject;

    public void run(IAction arg0) {

        if (structuredSelection != null && !structuredSelection.isEmpty()) {

            if (isValidSelection(structuredSelection)) {

                SynchronizeMembersEditorConfiguration configuration = SynchronizeMembersEditorConfiguration.getDefaultConfiguration();
                RemoteObject[] remoteObjects = getSelectedObjects(structuredSelection);

                try {
                    if (remoteObjects.length == 1) {
                        Access.openSynchronizeMembersEditor(shell, remoteObjects[0], null, configuration);
                    } else if (remoteObjects.length == 2) {
                        Access.openSynchronizeMembersEditor(shell, remoteObjects[0], remoteObjects[1], configuration);
                    }
                } catch (Exception e) {
                    ISpherePlugin.logError("*** Could not open synchronize members editor ***", e); //$NON-NLS-1$
                }
            }
        }
    }

    private RemoteObject[] getSelectedObjects(IStructuredSelection selectedObject) {

        List<?> objects = selectedObject.toList();
        RemoteObject[] remoteObjects = new RemoteObject[objects.size()];

        int i = 0;
        for (Object object : objects) {
            QSYSRemoteObject qsysRemoteObject = (QSYSRemoteObject)object;
            remoteObjects[i] = createRemoteObject(qsysRemoteObject);
            i++;
        }

        return remoteObjects;
    }

    private RemoteObject createRemoteObject(QSYSRemoteObject qsysRemoteObject) {

        IHost host = qsysRemoteObject.getRemoteObjectContext().getObjectSubsystem().getObjectSubSystem().getHost();
        String qualifiedConnectionName = ConnectionManager.getConnectionName(host);

        String name = qsysRemoteObject.getName();
        String library = qsysRemoteObject.getLibrary();
        String objectType = qsysRemoteObject.getType();
        String description = qsysRemoteObject.getDescription();

        return new RemoteObject(qualifiedConnectionName, name, library, objectType, description);
    }

    private boolean isValidSelection(IStructuredSelection selectedObject) {

        firstSelectedObject = null;

        for (Iterator<?> iterator = selectedObject.iterator(); iterator.hasNext();) {

            Object object = iterator.next();
            if (object instanceof QSYSRemoteObject) {
                if (firstSelectedObject == null) {
                    firstSelectedObject = object;
                } else {
                    if (!firstSelectedObject.getClass().equals(object.getClass())) {
                        MessageDialog.openError(shell, Messages.E_R_R_O_R, "Invalid selection. Objects must be of the same type.");
                        return false;
                    }
                }
                // QSYSRemoteObject qsysRemoteObject = (QSYSRemoteObject)object;
                // System.out.println("isValidSelection(): " +
                // qsysRemoteObject.getClass().getSimpleName());
            }
        }

        return true;
    }

    public void selectionChanged(IAction action, ISelection selection) {

        if (selection instanceof IStructuredSelection) {
            structuredSelection = ((IStructuredSelection)selection);
        } else {
            structuredSelection = null;
        }

        if (structuredSelection.size() >= 1 && structuredSelection.size() <= 2) {
            action.setEnabled(true);
        } else {
            action.setEnabled(false);
        }
    }

    public void setActivePart(IAction action, IWorkbenchPart workbenchPart) {
        shell = workbenchPart.getSite().getShell();
    }

}
