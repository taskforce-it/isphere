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

import biz.isphere.core.handler.DisplayDebugModuleViewHandlerDelegate;

public class DisplayDebugModuleViewHandler extends AbstractHandler implements IHandler {

    public static final String PARAMETER_CONNECTION_NAME = "connectionName";
    public static final String PARAMETER_PROGRAM_NAME = "programName";
    public static final String PARAMETER_LIBRARY_NAME = "libraryName";
    public static final String PARAMETER_OBJECT_TYPE = "objectType";
    public static final String PARAMETER_MODULE_NAME = "moduleName";

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
            e.printStackTrace();
        }

        return null;
    }
}
