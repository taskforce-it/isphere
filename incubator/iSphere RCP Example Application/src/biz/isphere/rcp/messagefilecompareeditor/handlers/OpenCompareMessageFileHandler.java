/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rcp.messagefilecompareeditor.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.externalapi.Access;
import biz.isphere.core.internal.handler.AbstractCommandHandler;
import biz.isphere.rcp.Messages;
import biz.isphere.rcp.messagefilecompareeditor.CompareConfiguration;
import biz.isphere.rcp.messagefilecompareeditor.dialogs.SelectMessageFilesDialog;

/**
 * This class is the action handler of the "Open Journal Json File" action.
 */
public class OpenCompareMessageFileHandler extends AbstractCommandHandler {

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
     * ExecutionEvent)
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {

        try {

            SelectMessageFilesDialog dialog = new SelectMessageFilesDialog(getShell());
            int rc = dialog.open();
            if (rc != Dialog.OK) {
                return null;
            }

            String leftConnection = dialog.getLeftConnectionName();
            String leftLibrary = dialog.getLeftLibraryName();
            String leftMessageFile = dialog.getLeftMessageFIleName();

            String rightConnection = dialog.getRightConnectionName();
            String rightLibrary = dialog.getRightLibraryName();
            String rightMessageFile = dialog.getRightMessageFIleName();

            CompareConfiguration configuration = new CompareConfiguration();

            Access.openMessageFileCompareEditor(getShell(event), leftConnection, leftLibrary, leftMessageFile, rightConnection, rightLibrary,
                rightMessageFile, configuration);

        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not open message file compare editor ***", e); //$NON-NLS-1$
            MessageDialog.openError(getShell(), Messages.Title_E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }

        return null;
    }
}
