/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.TransferISphereLibrary;

/**
 * This class is the action handler of the "TransferLibraryAction".
 */
public class TransferLibraryHandler extends AbstractHandler implements IHandler {

    private String connectionName;
    private int ftpPort;
    private String iSphereLibrary;
    private String aspGroup;
    private boolean connectionsEnabled;

    /**
     * Default constructor, used by the Eclipse framework.
     */
    public TransferLibraryHandler() {
        super();
    }

    public TransferLibraryHandler(String connectionName, int ftpPort, String iSphereLibrary, String aspGroup) {
        this.connectionName = connectionName;
        this.ftpPort = ftpPort;
        this.iSphereLibrary = iSphereLibrary;
        this.aspGroup = aspGroup;

        setConnectionsEnabled(true);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
     * ExecutionEvent)
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {

        try {

            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

            if (StringHelper.isNullOrEmpty(iSphereLibrary)) {
                MessageDialog.openError(shell, Messages.Error, Messages.iSphere_library_not_set_in_preferences);
                return null;
            }

            TransferISphereLibrary statusDialog = new TransferISphereLibrary(shell.getDisplay(), SWT.APPLICATION_MODAL | SWT.SHELL_TRIM,
                iSphereLibrary, aspGroup, connectionName, ftpPort);

            statusDialog.setConnectionsEnabled(connectionsEnabled);
            statusDialog.open();

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Failed to invoke the 'Transfer Library' handler ***", e); //$NON-NLS-1$
        }
        return null;
    }

    public void setConnectionsEnabled(boolean enabled) {
        this.connectionsEnabled = enabled;
    }
}
