/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.IEditor;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.messagefilecompare.rse.AbstractMessageFileCompareEditor;
import biz.isphere.rse.Messages;
import biz.isphere.rse.ibm.helper.ISeriesDataElementHelper;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.api.ISeriesObject;
import com.ibm.etools.iseries.core.ui.actions.ISeriesSystemBaseAction;
import com.ibm.etools.systems.core.ui.SystemMenuManager;
import com.ibm.etools.systems.core.ui.actions.ISystemDynamicPopupMenuExtension;
import com.ibm.etools.systems.dstore.core.model.DataElement;

public class OpenMessageFileCompareEditorAction extends ISeriesSystemBaseAction implements ISystemDynamicPopupMenuExtension {

    public static final String ID = "biz.isphere.rse.actions.OpenMessageFileCompareEditorAction"; //$NON-NLS-1$

    protected Shell shell;
    protected ArrayList<DataElement> arrayListSelection;

    public OpenMessageFileCompareEditorAction() {
        super(Messages.iSphere_Compare_Message_File_Editor, "", null);
        arrayListSelection = new ArrayList<DataElement>();
        setContextMenuGroup("additions");
        allowOnMultipleSelection(true);
        setHelp("");
        setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_COMPARE_MESSAGE_FILES));
    }

    public void populateMenu(Shell shell, SystemMenuManager menu, IStructuredSelection selection, String menuGroup) {
        setShell(shell);
        menu.add("additions", this);
    }

    public boolean supportsSelection(IStructuredSelection selection) {

        this.arrayListSelection.clear();

        ArrayList<DataElement> arrayListSelection = new ArrayList<DataElement>();

        for (Iterator iterSelection = selection.iterator(); iterSelection.hasNext();) {
            Object objSelection = iterSelection.next();
            if (ISeriesDataElementHelper.isMessageFile(objSelection)) {
                DataElement dataElement = (DataElement)objSelection;
                arrayListSelection.add(dataElement);
            }
        }

        if (arrayListSelection.isEmpty()) {
            return false;
        }

        if (arrayListSelection.size() > 2) {
            return false;
        }

        this.arrayListSelection = arrayListSelection;
        return true;

    }

    @Override
    public void runWithEvent(Event arg0) {

        if (arrayListSelection != null && !arrayListSelection.isEmpty()) {

            if (isValidSelection(arrayListSelection)) {
                RemoteObject[] remoteObjects = getSelectedMessageFiles(arrayListSelection);
                if (remoteObjects.length == 2) {
                    AbstractMessageFileCompareEditor.openEditor(remoteObjects[0], remoteObjects[1], IEditor.EDIT);
                } else {
                    AbstractMessageFileCompareEditor.openEditor(remoteObjects[0], null, IEditor.EDIT);
                }
            }
        }
    }

    private RemoteObject[] getSelectedMessageFiles(List<DataElement> selectedObject) {

        List<?> objects = selectedObject;
        RemoteObject[] remoteObjects = new RemoteObject[objects.size()];

        int i = 0;
        for (Object object : objects) {
            ISeriesObject qsysRemoteObject = new ISeriesObject((DataElement)object);
            remoteObjects[i] = createRemoteObject(qsysRemoteObject);
            i++;
        }

        return remoteObjects;
    }

    private RemoteObject createRemoteObject(ISeriesObject qsysRemoteObject) {

        String messageFile = qsysRemoteObject.getName();
        String library = qsysRemoteObject.getLibrary();
        String objectType = qsysRemoteObject.getType();
        String description = qsysRemoteObject.getDescription();
        ISeriesConnection ibmiConnection = qsysRemoteObject.getISeriesConnection();

        if (ibmiConnection != null) {

            String connectionName = ibmiConnection.getSystemConnection().getAliasName();

            AS400 as400 = null;
            try {
                as400 = ibmiConnection.getAS400ToolboxObject(shell);
            } catch (Exception e) {
            }

            if (as400 != null) {
                return new RemoteObject(connectionName, messageFile, library, objectType, description);
            }
        }

        return null;
    }

    private boolean isValidSelection(ArrayList<DataElement> selectedObject) {

        if (arrayListSelection.size() <= 0 || arrayListSelection.size() > 2) {
            return false;
        }

        for (DataElement dataElement : arrayListSelection) {
            if (!ISeriesDataElementHelper.isMessageFile(dataElement)) {
                return false;
            }
        }

        return true;
    }

    public void setActivePart(IAction action, IWorkbenchPart workbenchPart) {
        shell = workbenchPart.getSite().getShell();
    }

}
