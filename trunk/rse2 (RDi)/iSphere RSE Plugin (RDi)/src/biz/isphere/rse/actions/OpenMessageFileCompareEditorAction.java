/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.externalapi.Access;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.messagefilecompare.rse.MessageFileCompareEditorConfiguration;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteObject;

public class OpenMessageFileCompareEditorAction implements IObjectActionDelegate {

    public static final String ID = "biz.isphere.rse.actions.OpenMessageFileCompareEditorAction"; //$NON-NLS-1$

    protected IStructuredSelection structuredSelection;
    protected Shell shell;

    public void run(IAction arg0) {

        if (structuredSelection != null && !structuredSelection.isEmpty()) {

            if (isValidSelection(structuredSelection)) {

                MessageFileCompareEditorConfiguration configuration = MessageFileCompareEditorConfiguration.getDefaultConfiguration();
                RemoteObject[] remoteObjects = getSelectedMessageFiles(structuredSelection);

                try {
                    if (remoteObjects.length == 2) {
                        Access.openMessageFileCompareEditor(shell, remoteObjects[0], remoteObjects[1], configuration);
                    } else {
                        Access.openMessageFileCompareEditor(shell, remoteObjects[0], null, configuration);
                    }
                } catch (Exception e) {
                    ISpherePlugin.logError("*** Could not open message file compare editor ***", e); //$NON-NLS-1$
                }
            }
        }
    }

    private RemoteObject[] getSelectedMessageFiles(IStructuredSelection selectedObject) {

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

        String profil = qsysRemoteObject.getRemoteObjectContext().getObjectSubsystem().getObjectSubSystem().getSystemProfileName();
        String connectionName = qsysRemoteObject.getRemoteObjectContext().getObjectSubsystem().getObjectSubSystem().getHostAliasName();

        String messageFile = qsysRemoteObject.getName();
        String library = qsysRemoteObject.getLibrary();
        String objectType = qsysRemoteObject.getType();
        String description = qsysRemoteObject.getDescription();
        IBMiConnection ibmiConnection = IBMiConnection.getConnection(profil, connectionName);

        if (ibmiConnection != null) {

            AS400 as400 = null;
            try {
                as400 = ibmiConnection.getAS400ToolboxObject();
            } catch (SystemMessageException e) {
            }

            if (as400 != null) {
                return new RemoteObject(connectionName, messageFile, library, objectType, description);
            }
        }

        return null;
    }

    private boolean isValidSelection(IStructuredSelection selectedObject) {

        for (Iterator<?> iterator = selectedObject.iterator(); iterator.hasNext();) {

            Object object = iterator.next();
            if (!(object instanceof QSYSRemoteObject)) {
                return false;
            }

            QSYSRemoteObject qsysRemoteObject = (QSYSRemoteObject)object;
            if (!qsysRemoteObject.getType().equals(ISeries.MSGF)) {
                return false;
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
