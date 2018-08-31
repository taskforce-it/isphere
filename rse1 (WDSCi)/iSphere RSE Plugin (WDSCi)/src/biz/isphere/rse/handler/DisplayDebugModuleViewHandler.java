/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.handler.DisplayDebugModuleViewHandlerDelegate;
import biz.isphere.rse.Messages;

public class DisplayDebugModuleViewHandler extends AbstractHandler implements IHandler {

    public static final String PARAMETER_CONNECTION_NAME = "connectionName"; //$NON-NLS-1$
    public static final String PARAMETER_PROGRAM_NAME = "programName"; //$NON-NLS-1$
    public static final String PARAMETER_LIBRARY_NAME = "libraryName"; //$NON-NLS-1$
    public static final String PARAMETER_OBJECT_TYPE = "objectType"; //$NON-NLS-1$
    public static final String PARAMETER_MODULE_NAME = "moduleName"; //$NON-NLS-1$

    public Object execute(ExecutionEvent event) throws ExecutionException {

        try {

            String connectionName = event.getParameter(PARAMETER_CONNECTION_NAME);
            DisplayDebugModuleViewHandlerDelegate handler = new DisplayDebugModuleViewHandlerDelegate(connectionName);
            String program = event.getParameter(PARAMETER_PROGRAM_NAME);
            String library = event.getParameter(PARAMETER_LIBRARY_NAME);
            String objectType = event.getParameter(PARAMETER_OBJECT_TYPE);
            String module = event.getParameter(PARAMETER_MODULE_NAME);

            handler.execute(program, library, objectType, module);

        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not retrieve module source ***", e); //$NON-NLS-1$
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
        }

        return null;
    }
}
