/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.IEditor;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.rse.Messages;
import biz.isphere.rse.dataareaeditor.DataAreaEditor;
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

public class UserSpaceEditorAction extends ISeriesSystemBaseAction implements ISystemDynamicPopupMenuExtension {

    protected ArrayList arrayListSelection;

    public UserSpaceEditorAction() {
        super(Messages.iSphere_User_Space_Editor, "", null);
        arrayListSelection = new ArrayList();
        setContextMenuGroup("additions");
        allowOnMultipleSelection(true);
        setHelp("");
        setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_USER_SPACE));
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
            if (ISeriesDataElementHelper.isUserSpace(objSelection)) {
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

                ISeriesObject object = new ISeriesObject(dataElement);

                if (object != null) {

                    String dataArea = object.getName();
                    String library = object.getLibrary();
                    String objectType = object.getType();
                    String description = object.getDescription();

                    ISeriesConnection iseriesConnection = object.getISeriesConnection();

                    if (iseriesConnection != null) {

                        String connectionName = iseriesConnection.getSystemConnection().getAliasName();

                        AS400 as400 = null;
                        try {
                            as400 = iseriesConnection.getAS400ToolboxObject(getShell());
                        } catch (SystemMessageException e) {
                        }

                        Connection jdbcConnection = null;
                        try {
                            jdbcConnection = iseriesConnection.getJDBCConnection(null, false);
                        } catch (SQLException e1) {
                        }

                        if (as400 != null && jdbcConnection != null) {
                            RemoteObject remoteObject = new RemoteObject(connectionName, dataArea, library, objectType, description);
                            DataAreaEditor.openEditor(as400, remoteObject, IEditor.EDIT);
                        }
                    }
                }
            }
        }
    }
}
