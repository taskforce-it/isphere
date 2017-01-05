/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/

package biz.isphere.messagesubsystem.rse.internal;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;

import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.messagesubsystem.rse.ISphereMessageSubsystemRSEPlugin;
import biz.isphere.messagesubsystem.rse.Messages;
import biz.isphere.messagesubsystem.rse.SendMessageDelegate;
import biz.isphere.messagesubsystem.rse.SendMessageDialog;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.ui.actions.ISeriesSystemBaseAction;
import com.ibm.etools.systems.core.messages.SystemMessageException;
import com.ibm.etools.systems.subsystems.SubSystem;

/**
 * This class adds a popup menu extension to queued message resources in order
 * to display the message details in a message dialog.
 */
public class SendMessageAction extends ISeriesSystemBaseAction {

    public static final String ID = "biz.isphere.messagesubsystem.rse.internal.SendMessageAction"; //$NON-NLS-1$

    public SendMessageAction() {
        super(Messages.MessageSubsystem_Send_Message, null);

        setId(ID);
        setImageDescriptor(ISphereMessageSubsystemRSEPlugin.getDefault().getImageDescriptor(ISphereMessageSubsystemRSEPlugin.SEND_MESSAGE));
    }

    @Override
    public void run() {
        try {
            Object[] selection = getSelectedRemoteObjects();
            if (selection != null && selection.length >= 1 && selection[0] instanceof QueuedMessageSubSystem) {
                QueuedMessageSubSystem subSystem = (QueuedMessageSubSystem)selection[0];
                SendMessageDialog dialog = SendMessageDialog.createSendDialog(getShell(), getAS400Toolbox(subSystem));
                if (dialog.open() == SendMessageDialog.OK) {
                    SendMessageDelegate delegate = new SendMessageDelegate();
                    delegate.sendMessage(getAS400Toolbox(subSystem), dialog.getInput());
                }
            }
        } catch (SystemMessageException e) {
            MessageDialogAsync.displayError(getShell(), e.getLocalizedMessage());
        }
    }

    private AS400 getAS400Toolbox(SubSystem subSystem) throws SystemMessageException {

        String connectionName = subSystem.getSystem().getHostName();
        return ISeriesConnection.getConnection(connectionName).getAS400ToolboxObject(null);
    }

    private Object[] getSelectedRemoteObjects() {

        Object[] selectedObjects;
        IStructuredSelection structuredSelection = getSelection();
        if (structuredSelection != null) {
            selectedObjects = new Object[structuredSelection.size()];
        } else {
            selectedObjects = new Object[0];
        }

        if (structuredSelection == null) {
            return selectedObjects;
        }

        Iterator<?> i = structuredSelection.iterator();
        int idx = 0;
        while (i.hasNext()) {
            Object object = i.next();
            if (object instanceof QueuedMessageSubSystem) {
                selectedObjects[(idx++)] = object;
            }
        }

        return selectedObjects;
    }

}