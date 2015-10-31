/*******************************************************************************
 * Copyright (c) 2005 SoftLanding Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     SoftLanding - initial API and implementation
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/
package biz.isphere.messagesubsystem.rse.internal;

import org.eclipse.swt.widgets.Display;

import biz.isphere.messagesubsystem.rse.QueuedMessageDialog;
import biz.isphere.messagesubsystem.rse.ReceivedMessage;

import com.ibm.as400.access.QueuedMessage;
import com.ibm.etools.iseries.rse.ui.actions.popupmenu.ISeriesAbstractQSYSPopupMenuAction;

/**
 * This class adds a popup menu extension to queued message resources in order
 * to display the message details in a message dialog.
 */
public class QueuedMessageDetailsPopupMenuExtensionAction extends ISeriesAbstractQSYSPopupMenuAction {

    public QueuedMessageDetailsPopupMenuExtensionAction() {
        super();
    }

    @Override
    public void run() {
        Object[] selection = getSelectedRemoteObjects();
        for (int i = 0; i < selection.length; i++) {
            if (selection[i] instanceof QueuedMessageResource) {
                QueuedMessageResource queuedMessageResource = (QueuedMessageResource)selection[i];
                QueuedMessage queuedMessage = queuedMessageResource.getQueuedMessage();
                QueuedMessageDialog dialog = new QueuedMessageDialog(Display.getCurrent().getActiveShell(), new ReceivedMessage(queuedMessage));
                if (dialog.open() == QueuedMessageDialog.CANCEL) {
                    break;
                }
            }
        }
    }

}