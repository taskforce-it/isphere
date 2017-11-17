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

import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;

import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.messagesubsystem.rse.SendMessageDelegate;
import biz.isphere.messagesubsystem.rse.SendMessageDialog;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.rse.ui.actions.popupmenu.ISeriesAbstractQSYSPopupMenuAction;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

/**
 * This class adds a popup menu extension to queued message resources in order
 * to display the message details in a message dialog.
 */
public class SendMessageAction extends ISeriesAbstractQSYSPopupMenuAction {

    public static final String ID = "biz.isphere.messagesubsystem.rse.internal.SendMessageAction"; //$NON-NLS-1$

    public SendMessageAction() {
        super();
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

    private AS400 getAS400Toolbox(ISubSystem subSystem) throws SystemMessageException {

        String connectionName = subSystem.getHostAliasName();
        return IBMiConnection.getConnection(connectionName).getAS400ToolboxObject();
    }

}