/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.sourcemembercopy.rse.CopyMemberDialog;
import biz.isphere.core.sourcemembercopy.rse.CopyMemberService;
import biz.isphere.rse.Messages;

import com.ibm.etools.iseries.comm.filters.ISeriesMemberFilterString;
import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.api.ISeriesObject;
import com.ibm.etools.iseries.core.descriptors.ISeriesDataElementDescriptorType;
import com.ibm.etools.iseries.core.dstore.common.ISeriesDataElementHelpers;
import com.ibm.etools.iseries.core.ui.actions.ISeriesSystemBaseAction;
import com.ibm.etools.iseries.core.util.ISeriesDataElementUtil;
import com.ibm.etools.systems.as400filesubsys.impl.FileSubSystemImpl;
import com.ibm.etools.systems.core.ui.SystemMenuManager;
import com.ibm.etools.systems.core.ui.actions.ISystemDynamicPopupMenuExtension;
import com.ibm.etools.systems.core.ui.messages.SystemMessageDialog;
import com.ibm.etools.systems.dstore.core.model.DataElement;
import com.ibm.etools.systems.filters.SystemFilterReference;
import com.ibm.etools.systems.model.impl.SystemMessageObject;
import com.ibm.etools.systems.subsystems.SubSystem;

public class CopyToAction extends ISeriesSystemBaseAction implements ISystemDynamicPopupMenuExtension {

    protected ArrayList arrayListSelection;

    private CopyMemberService jobDescription;

    public CopyToAction() {
        super(Messages.iSphere_CopyMembersTo, "", null);
        arrayListSelection = new ArrayList();
        setContextMenuGroup("additions");
        allowOnMultipleSelection(true);
        setHelp("");
        setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_COPY_MEMBERS_TO));
    }

    public void populateMenu(Shell shell, SystemMenuManager menu, IStructuredSelection selection, String menuGroup) {
        setShell(shell);
        menu.add("additions", this);
    }

    public boolean supportsSelection(IStructuredSelection selection) {

        this.arrayListSelection.clear();

        ArrayList<Object> arrayListSelection = new ArrayList<Object>();

        for (Iterator iterSelection = selection.iterator(); iterSelection.hasNext();) {
            Object objSelection = iterSelection.next();
            if (objSelection instanceof DataElement) {
                DataElement dataElement = (DataElement)objSelection;
                ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
                if (type.isSourceMember() || type.isSourceFile()) {
                    arrayListSelection.add(dataElement);
                }
            } else if (objSelection instanceof SystemFilterReference) {
                SystemFilterReference systemFilterReference = (SystemFilterReference)objSelection;
                if (systemFilterReference.getReferencedFilter() != null) {
                    if ("Member".equalsIgnoreCase(systemFilterReference.getReferencedFilter().getType())) {
                        arrayListSelection.add(systemFilterReference);
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

    @Override
    public void run() {

        jobDescription = null;

        if (arrayListSelection.size() > 0) {

            for (Iterator iterObjects = arrayListSelection.iterator(); iterObjects.hasNext();) {

                Object selectedObject = iterObjects.next();

                if (selectedObject instanceof DataElement) {
                    DataElement dataElement = (DataElement)selectedObject;
                    ISeriesObject object = new ISeriesObject(dataElement);
                    if (ISeriesDataElementUtil.getDescriptorTypeObject(dataElement).isMember()) {
                        if (!addElement(object)) {
                            return;
                        }
                    } else if (ISeriesDataElementUtil.getDescriptorTypeObject(dataElement).isSourceFile()) {
                        String connectionName = object.getISeriesConnection().getSystemConnection().getAliasName();
                        if (!addElementsFromSourceFile(connectionName, ISeriesDataElementHelpers.getLibrary(dataElement), ISeriesDataElementHelpers
                            .getName(dataElement))) {
                            return;
                        }
                    }
                } else if (selectedObject instanceof SystemFilterReference) {
                    SystemFilterReference filterReference = (SystemFilterReference)selectedObject;
                    String[] filterStrings = filterReference.getReferencedFilter().getFilterStrings();
                    String connectionName = ((SubSystem)filterReference.getFilterPoolReferenceManager().getProvider()).getSystemConnection()
                        .getAliasName();
                    if (!addElementsFromFilterString(connectionName, filterStrings)) {
                        return;
                    }

                }
            }

            if (jobDescription != null && jobDescription.getItems().length > 0) {
                CopyMemberDialog dialog = new CopyMemberDialog(getShell());
                dialog.setContent(jobDescription);
                dialog.open();
            }
        }

        jobDescription = null;
    }

    private boolean addElement(ISeriesObject object) {

        if (object == null) {
            ISpherePlugin.logError("*** CopyToAction.addElement(): 'object' must not be null ***", null);
            return false;
        }

        ISeriesConnection iseriesConnection = object.getISeriesConnection();
        if (iseriesConnection != null) {
            String connectionName = iseriesConnection.getSystemConnection().getAliasName();
            if (jobDescription == null) {
                jobDescription = new CopyMemberService(getShell(), connectionName);
            } else {
                if (!jobDescription.getFromConnectionName().equals(connectionName)) {
                    MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Cannot_copy_source_members_from_different_connections);
                    return false;
                }
            }
            jobDescription.addItem(object.getFile(), object.getLibrary(), object.getName(), object.getType());
        }

        return true;
    }

    private boolean addElementsFromFilterString(String connectionName, String[] filterStrings) {

        Object[] children = null;

        for (int idx = 0; idx < filterStrings.length; idx++) {

            String _filterString = filterStrings[idx];
            ISeriesConnection _connection = ISeriesConnection.getConnection(connectionName);
            FileSubSystemImpl _fileSubSystemImpl = _connection.getISeriesFileSubSystem();

            try {
                children = _fileSubSystemImpl.resolveFilterString(_filterString, null);
            } catch (InterruptedException localInterruptedException) {
                return false;
            } catch (Exception e) {
                SystemMessageDialog.displayExceptionMessage(shell, e);
                return false;
            }

            if ((children != null) && (children.length != 0)) {
                Object firstObject = children[0];
                if ((firstObject instanceof SystemMessageObject)) {
                    SystemMessageDialog.displayErrorMessage(shell, ((SystemMessageObject)firstObject).getMessage());
                    return false;
                } else {
                    for (int idx2 = 0; idx2 < children.length; idx2++) {
                        DataElement dataElement = (DataElement)children[idx2];
                        if (ISeriesDataElementUtil.getDescriptorTypeObject(dataElement).isSourceFile()) {
                            // not yet used.
                            if (!addElementsFromSourceFile(connectionName, ISeriesDataElementHelpers.getLibrary(dataElement),
                                ISeriesDataElementHelpers.getName(dataElement))) {
                                return false;
                            }
                        } else if (ISeriesDataElementUtil.getDescriptorTypeObject(dataElement).isMember()) {
                            ISeriesObject object = new ISeriesObject(dataElement);
                            if (!addElement(object)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;

    }

    private boolean addElementsFromSourceFile(String connectionName, String library, String sourceFile) {

        ISeriesMemberFilterString _memberFilterString = new ISeriesMemberFilterString();
        _memberFilterString.setLibrary(library);
        _memberFilterString.setFile(sourceFile);
        _memberFilterString.setMember("*");
        _memberFilterString.setMemberType("*");

        String[] _filterStrings = new String[1];
        _filterStrings[0] = _memberFilterString.toString();
        return addElementsFromFilterString(connectionName, _filterStrings);
    }
}
