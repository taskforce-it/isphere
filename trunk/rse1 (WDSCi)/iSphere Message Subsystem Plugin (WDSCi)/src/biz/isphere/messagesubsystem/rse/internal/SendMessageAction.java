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

import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.messagesubsystem.rse.SendMessageDelegate;
import biz.isphere.messagesubsystem.rse.SendMessageDialog;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.ui.actions.isv.ISeriesAbstractQSYSPopupMenuExtensionAction;
import com.ibm.etools.systems.core.messages.SystemMessageException;

/**
 * This class adds a popup menu extension to queued message resources in order
 * to display the message details in a message dialog.
 */
public class SendMessageAction extends ISeriesAbstractQSYSPopupMenuExtensionAction {

    public SendMessageAction() {
        super();
    }

    @Override
    public void run() {
        Object[] selection = getSelectedRemoteObjects();
        if (selection != null && selection.length >= 1 && selection[0] instanceof QueuedMessageSubSystem) {
            SendMessageDialog dialog = new SendMessageDialog(getShell());
            if (dialog.open() == SendMessageDialog.OK) {
                try {
                    SendMessageDelegate delegate = new SendMessageDelegate();
                    QueuedMessageSubSystem subSystem = (QueuedMessageSubSystem)selection[0];
                    delegate.sendMessage(getAS400Toolbox(subSystem), dialog.getInput());
                } catch (SystemMessageException e) {
                    MessageDialogAsync.displayError(getShell(), e.getLocalizedMessage());
                }
            }
        }
    }

    private AS400 getAS400Toolbox(QueuedMessageSubSystem subSystem) throws SystemMessageException {

        String connectionName = subSystem.getHostName();
        return ISeriesConnection.getConnection(connectionName).getAS400ToolboxObject(null);
    }

}