/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;
import biz.isphere.rse.handler.DisplayDebugModuleViewHandler;

import com.ibm.etools.iseries.core.api.ISeriesAbstractProgramObject;
import com.ibm.etools.iseries.core.api.ISeriesProgramModule;
import com.ibm.etools.iseries.core.descriptors.ISeriesDataElementDescriptorType;
import com.ibm.etools.iseries.core.dstore.common.ISeriesDataElementHelpers;
import com.ibm.etools.iseries.core.ui.actions.ISeriesSystemBaseAction;
import com.ibm.etools.systems.core.ui.SystemMenuManager;
import com.ibm.etools.systems.core.ui.actions.ISystemDynamicPopupMenuExtension;
import com.ibm.etools.systems.dstore.core.model.DataElement;

public class DisplayDebugModuleViewAction extends ISeriesSystemBaseAction implements ISystemDynamicPopupMenuExtension {

    protected ArrayList arrayListSelection;

    public DisplayDebugModuleViewAction() {
        super(Messages.iSphere_Display_Debug_Module_View, "", null);
        arrayListSelection = new ArrayList();
        setContextMenuGroup("group.generate");
        allowOnMultipleSelection(true);
        setHelp("");
        setImageDescriptor(ISphereRSEPlugin.getDefault().getImageRegistry().getDescriptor(ISphereRSEPlugin.IMAGE_DISPLAY_MODULE_VIEW));
    }

    public void populateMenu(Shell shell, SystemMenuManager menu, IStructuredSelection selection, String menuGroup) {
        setShell(shell);
        menu.add("group.generate", this);
    }

    public boolean supportsSelection(IStructuredSelection selection) {

        this.arrayListSelection.clear();

        ArrayList<DataElement> arrayListSelection = new ArrayList<DataElement>();

        for (Iterator iterSelection = selection.iterator(); iterSelection.hasNext();) {
            Object objSelection = iterSelection.next();
            if (objSelection instanceof DataElement) {
                DataElement dataElement = (DataElement)objSelection;
                ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
                if (type.isProgramModule()) {
                    String strType = ISeriesDataElementHelpers.getType(dataElement);
                    if (strType.equalsIgnoreCase("*MODULE_INTERNAL")) { //$NON-NLS-1$
                        arrayListSelection.add(dataElement);
                    }
                }
            }
        }

        if (arrayListSelection.isEmpty()) {
            return false;
        }

        this.arrayListSelection = arrayListSelection;

        return true;
    }

    public void run() {

        if (arrayListSelection.size() > 0) {

            for (Iterator iterObjects = arrayListSelection.iterator(); iterObjects.hasNext();) {

                DataElement dataElement = (DataElement)iterObjects.next();
                String strType = ISeriesDataElementHelpers.getType(dataElement);

                if (strType.equalsIgnoreCase("*MODULE_INTERNAL")) { //$NON-NLS-1$

                    ISeriesProgramModule module = new ISeriesProgramModule(dataElement);

                    try {

                        String connectionName = module.getISeriesConnection().getSystemConnection().getAliasName();

                        ISeriesAbstractProgramObject program = (ISeriesAbstractProgramObject)module.getParentObject();

                        String programName = program.getName();
                        String libraryName = program.getLibrary();
                        String objectType = program.getType();
                        String moduleName = module.getModuleName();

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
                        ISpherePlugin.logError("*** Could not retrieve module source ***", e); //$NON-NLS-1$
                        MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.E_R_R_O_R, e
                            .getLocalizedMessage());
                    }
                }
            }
        }
    }

}
