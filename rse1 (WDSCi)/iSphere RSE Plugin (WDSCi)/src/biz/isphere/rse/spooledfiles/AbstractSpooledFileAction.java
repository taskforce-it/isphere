/*******************************************************************************
 * Copyright (c) 2012-2020 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import biz.isphere.core.Messages;
import biz.isphere.core.internal.exception.CanceledByUserException;

import com.ibm.etools.iseries.core.ui.actions.isv.ISeriesAbstractQSYSPopupMenuExtensionAction;

public abstract class AbstractSpooledFileAction extends ISeriesAbstractQSYSPopupMenuExtensionAction {

    public AbstractSpooledFileAction() {
        super();
    }

    @Override
    public void run() {

        init();

        String message = null;

        try {

            Object[] selection = getSelectedRemoteObjects();
            for (int i = 0; i < selection.length; i++) {
                if (selection[i] instanceof SpooledFileResource) {
                    SpooledFileResource spooledFileResource = (SpooledFileResource)selection[i];
                    message = execute(spooledFileResource);
                    if (message != null) {
                        MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.Error, message);
                        break;
                    }
                }
            }

            if (message == null) {
                message = finish();
                if (message != null) {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.Error, message);
                }
            }

        } catch (CanceledByUserException e) {
            // nothing to do here
        }

    }

    public void init() {
    }

    public abstract String execute(SpooledFileResource spooledFileResource) throws CanceledByUserException;

    public String finish() {
        return null;
    }

}