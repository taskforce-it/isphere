/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.TransferISphereLibrary;
import biz.isphere.core.preferences.Preferences;

/**
 * This class is the action handler of the "TransferLibraryAction".
 */
public class TransferLibraryHandler extends AbstractShellHandler {

    /**
     * Default constructor, used by the Eclipse framework.
     */
    public TransferLibraryHandler() {
        super();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
     * ExecutionEvent)
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {

        String iSphereLibrary = Preferences.getInstance().getISphereLibrary(); // CHECKED

        if (StringHelper.isNullOrEmpty(iSphereLibrary)) {
            MessageDialog.openError(getShell(event), Messages.Error, Messages.iSphere_library_not_set_in_preferences);
            return null;
        }

        String aspGroup = Preferences.getInstance().getASPGroup();
        String connectionName = Preferences.getInstance().getConnectionName();
        int ftpPort = Preferences.getInstance().getFtpPortNumber();

        return execute(getShell(event), connectionName, ftpPort, iSphereLibrary, aspGroup, true);
    }

    /**
     * Called from various iSphere classes, such as <i>preferences page</i>,
     * <i>transfer library</i> menu item and <i>RSE properties page</i>.
     * 
     * @param shell - the parent shell.
     * @param connectionName - the connection name.
     * @param ftpPort - the ftp port number.
     * @param iSphereLibrary - the name of the iSphere library.
     * @param aspGroup - the asp group the library is put into.
     * @param selectConnectionEnabled - whether the user can change the
     *        connection name.
     * @return null
     */
    public Object execute(Shell shell, String connectionName, int ftpPort, String iSphereLibrary, String aspGroup, boolean selectConnectionEnabled) {

        TransferISphereLibrary statusDialog = new TransferISphereLibrary(shell, SWT.APPLICATION_MODAL | SWT.SHELL_TRIM, iSphereLibrary, aspGroup,
            connectionName, ftpPort);

        statusDialog.setConnectionsEnabled(selectConnectionEnabled);
        statusDialog.open();

        return null;
    }
}
