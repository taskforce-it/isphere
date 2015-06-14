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
package biz.isphere.messagesubsystem.internal;

import org.eclipse.swt.widgets.Display;

import com.ibm.as400.access.QueuedMessage;
import com.ibm.etools.iseries.core.ui.actions.isv.ISeriesAbstractQSYSPopupMenuExtensionAction;

public class QueuedMessageDetailsPopupMenuExtensionAction extends ISeriesAbstractQSYSPopupMenuExtensionAction {

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
                QueuedMessageDialog dialog = new QueuedMessageDialog(Display.getCurrent().getActiveShell(), queuedMessage);
                if (dialog.open() == QueuedMessageDialog.CANCEL) break;
            }
        }
    }

}