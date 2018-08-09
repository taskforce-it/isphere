/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteObject;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteProgramModule;

import biz.isphere.rse.handler.DisplayDebugModuleViewHandler;

public class DisplayDebugModuleViewAction implements IObjectActionDelegate {

    protected IStructuredSelection structuredSelection;
    protected Shell shell;

    public void run(IAction arg0) {

        if (structuredSelection != null && !structuredSelection.isEmpty()) {

            Object object = structuredSelection.getFirstElement();

            if (object instanceof QSYSRemoteObject) {

                QSYSRemoteObject qsysRemoteObject = (QSYSRemoteObject)object;

                if (qsysRemoteObject instanceof QSYSRemoteProgramModule) {

                    QSYSRemoteProgramModule qsysRemoteProgramModule = (QSYSRemoteProgramModule)qsysRemoteObject;

                    try {

                        String connectionName = qsysRemoteProgramModule.getRemoteObjectContext().getObjectSubsystem().getObjectSubSystem()
                            .getHostAliasName();

                        String programName = qsysRemoteProgramModule.getProgram().getName();
                        String libraryName = qsysRemoteProgramModule.getProgram().getLibrary();
                        String objectType = qsysRemoteProgramModule.getProgram().getType();
                        String moduleName = qsysRemoteProgramModule.getName();

                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put(DisplayDebugModuleViewHandler.PARAMETER_CONNECTION_NAME, connectionName);
                        parameters.put(DisplayDebugModuleViewHandler.PARAMETER_PROGRAM_NAME, programName);
                        parameters.put(DisplayDebugModuleViewHandler.PARAMETER_LIBRARY_NAME, libraryName);
                        parameters.put(DisplayDebugModuleViewHandler.PARAMETER_OBJECT_TYPE, objectType);
                        parameters.put(DisplayDebugModuleViewHandler.PARAMETER_MODULE_NAME, moduleName);

                        ExecutionEvent event = new ExecutionEvent(null, parameters, null, null);

                        DisplayDebugModuleViewHandler handler = new DisplayDebugModuleViewHandler();
                        handler.execute(event);

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Shell getShell() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    }

    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            structuredSelection = ((IStructuredSelection)selection);
        } else {
            structuredSelection = null;
        }
    }

    public void setActivePart(IAction action, IWorkbenchPart workbenchPart) {
        shell = workbenchPart.getSite().getShell();
    }

}
