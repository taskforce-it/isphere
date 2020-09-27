/*******************************************************************************
 * Copyright (c) 2012-2013 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.IEditor;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.messagefileeditor.MessageFileEditor;
import biz.isphere.rse.Messages;
import biz.isphere.rse.ibm.helper.ISeriesDataElementHelper;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.api.ISeriesObject;
import com.ibm.etools.iseries.core.descriptors.ISeriesDataElementDescriptorType;
import com.ibm.etools.iseries.core.dstore.common.ISeriesDataElementHelpers;
import com.ibm.etools.iseries.core.ui.actions.ISeriesSystemBaseAction;
import com.ibm.etools.systems.core.messages.SystemMessageException;
import com.ibm.etools.systems.core.ui.SystemMenuManager;
import com.ibm.etools.systems.core.ui.actions.ISystemDynamicPopupMenuExtension;
import com.ibm.etools.systems.dstore.core.model.DataElement;

public class MessageFileEditorAction extends ISeriesSystemBaseAction implements ISystemDynamicPopupMenuExtension {

    protected ArrayList arrayListSelection;

    public MessageFileEditorAction() {
        super(Messages.iSphere_Message_File_Editor, "", null);
        arrayListSelection = new ArrayList();
        setContextMenuGroup("additions");
        allowOnMultipleSelection(true);
        setHelp("");
        setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_MESSAGE_FILE));
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

        this.arrayListSelection = arrayListSelection;
        return true;

    }

    @Override
    public void run() {

        if (arrayListSelection.size() > 0) {

            for (Iterator iterObjects = arrayListSelection.iterator(); iterObjects.hasNext();) {

                DataElement dataElement = (DataElement)iterObjects.next();

                ISeriesObject qsysRemoteObject = new ISeriesObject(dataElement);

                if (qsysRemoteObject != null) {

                    String connectionName = qsysRemoteObject.getISeriesConnection().getSystemConnection().getAliasName();
                    String messageFile = qsysRemoteObject.getName();
                    String library = qsysRemoteObject.getLibrary();
                    String objectType = qsysRemoteObject.getType();
                    String description = qsysRemoteObject.getDescription();
                    ISeriesConnection iseriesConnection = qsysRemoteObject.getISeriesConnection();

                    if (iseriesConnection != null) {

                        AS400 as400 = null;
                        try {
                            as400 = iseriesConnection.getAS400ToolboxObject(getShell());
                        } catch (SystemMessageException e) {
                        }

                        if (as400 != null) {

                            RemoteObject remoteObject = new RemoteObject(connectionName, messageFile, library, objectType, description);
                            MessageFileEditor.openEditor(connectionName, remoteObject, IEditor.EDIT);

                        }
                    }
                }
            }
        }
    }
}