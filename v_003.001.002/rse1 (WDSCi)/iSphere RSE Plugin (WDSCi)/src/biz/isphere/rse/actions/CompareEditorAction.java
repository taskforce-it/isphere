/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;
import biz.isphere.rse.compareeditor.handler.CompareSourceMembersHandler;
import biz.isphere.rse.internal.RSEMember;

import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.api.ISeriesMember;
import com.ibm.etools.iseries.core.descriptors.ISeriesDataElementDescriptorType;
import com.ibm.etools.iseries.core.ui.actions.ISeriesSystemBaseAction;
import com.ibm.etools.systems.core.ui.SystemMenuManager;
import com.ibm.etools.systems.core.ui.actions.ISystemDynamicPopupMenuExtension;
import com.ibm.etools.systems.dstore.core.model.DataElement;

public class CompareEditorAction extends ISeriesSystemBaseAction implements ISystemDynamicPopupMenuExtension {

    private RSEMember[] selectedMembers;
    private List<RSEMember> selectedMembersList;

    public CompareEditorAction() {
        super(Messages.iSphere_Compare_Editor, "", null);
        setContextMenuGroup("additions");
        allowOnMultipleSelection(true);
        setHelp("");
        setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_COMPARE));
        selectedMembersList = new ArrayList<RSEMember>();
    }

    public void populateMenu(Shell shell, SystemMenuManager menu, IStructuredSelection selection, String menuGroup) {
        setShell(shell);
        menu.add("additions", this);
    }

    @Override
    public void run() {

        try {

            if (selectedMembers.length > 0) {
                CompareSourceMembersHandler handler = new CompareSourceMembersHandler();
                handler.handleSourceCompare(selectedMembers);
            }

        } catch (Exception e) {
            ISphereRSEPlugin.logError(biz.isphere.core.Messages.Unexpected_Error, e);
            MessageDialog.openError(shell, Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }
    }

    public boolean supportsSelection(IStructuredSelection structuredSelection) {

        selectedMembers = getMembersFromSelection(structuredSelection);

        if (selectedMembersList.size() >= 1) {
            return true;
        } else {
            return false;
        }
    }

    private RSEMember[] getMembersFromSelection(IStructuredSelection structuredSelection) {

        selectedMembersList.clear();

        try {
            if (structuredSelection != null && structuredSelection.size() > 0) {
                Object[] objects = structuredSelection.toArray();
                for (Object object : objects) {
                    if (object instanceof DataElement) {
                        DataElement dataElement = (DataElement)object;
                        ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
                        if (type.isSourceMember()) {
                            selectedMembersList.add(new RSEMember(new ISeriesMember(dataElement)));
                        }
                    }
                }
            }
        } catch (Exception e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
        }

        return selectedMembersList.toArray(new RSEMember[selectedMembersList.size()]);
    }

    private RSEMember getMember(String connectionName, String libraryName, String sourceFileName, String memberName) {
        try {
            ISeriesConnection connection = ISeriesConnection.getConnection(connectionName);
            return new RSEMember((ISeriesMember)connection.getISeriesMember(libraryName, sourceFileName, memberName));
        } catch (Exception e) {
            MessageDialog.openError(shell, biz.isphere.core.Messages.Error, e.getMessage());
            return null;
        }
    }
}