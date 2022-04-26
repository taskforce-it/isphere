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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.compareeditor.CompareAction;
import biz.isphere.core.compareeditor.CompareEditorConfiguration;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;
import biz.isphere.rse.compareeditor.RSECompareDialog;
import biz.isphere.rse.internal.RSEMember;

import com.ibm.etools.iseries.core.api.ISeriesMember;
import com.ibm.etools.iseries.core.descriptors.ISeriesDataElementDescriptorType;
import com.ibm.etools.iseries.core.ui.actions.ISeriesSystemBaseAction;
import com.ibm.etools.systems.core.ui.SystemMenuManager;
import com.ibm.etools.systems.core.ui.actions.ISystemDynamicPopupMenuExtension;
import com.ibm.etools.systems.dstore.core.model.DataElement;

public class CompareEditorAction extends ISeriesSystemBaseAction implements ISystemDynamicPopupMenuExtension {

    protected ArrayList arrayListSelection;

    public CompareEditorAction() {
        super(Messages.iSphere_Compare_Editor, "", null);
        arrayListSelection = new ArrayList();
        setContextMenuGroup("additions");
        allowOnMultipleSelection(true);
        setHelp("");
        setImageDescriptor(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_COMPARE));
    }

    public void populateMenu(Shell shell, SystemMenuManager menu, IStructuredSelection selection, String menuGroup) {
        setShell(shell);
        menu.add("additions", this);
    }

    @Override
    public void run() {
        if (arrayListSelection.size() > 0) {

            try {

                RSEMember rseLeftMember = getLeftMemberFromSelection();
                RSEMember rseRightMember = getRightMemberFromSelection();

                if (rseLeftMember != null) {

                    RSECompareDialog dialog;
                    if (rseRightMember == null) {
                        dialog = new RSECompareDialog(shell, true, rseLeftMember);
                    } else {
                        dialog = new RSECompareDialog(shell, true, rseLeftMember, rseRightMember);
                    }

                    if (dialog.open() == Dialog.OK) {

                        boolean editable = dialog.isEditable();
                        boolean considerDate = dialog.isConsiderDate();
                        boolean ignoreCase = dialog.isIgnoreCase();
                        boolean threeWay = dialog.isThreeWay();

                        RSEMember rseAncestorMember = null;

                        if (threeWay) {

                            ISeriesMember ancestorMember = dialog.getAncestorConnection().getISeriesMember(getShell(), dialog.getAncestorLibrary(),
                                dialog.getAncestorFile(), dialog.getAncestorMember());

                            if (ancestorMember != null) {
                                rseAncestorMember = new RSEMember(ancestorMember);
                            }

                        }

                        rseRightMember = dialog.getRightRSEMember();
                        rseLeftMember = dialog.getLeftRSEMember();

                        CompareEditorConfiguration cc = new CompareEditorConfiguration();
                        cc.setLeftEditable(editable);
                        cc.setRightEditable(false);
                        cc.setConsiderDate(considerDate);
                        cc.setIgnoreCase(ignoreCase);
                        cc.setThreeWay(threeWay);

                        CompareAction action = new CompareAction(cc, rseAncestorMember, rseLeftMember, rseRightMember, null);
                        action.run();

                    }
                }

            } catch (Exception e) {
                ISphereRSEPlugin.logError(biz.isphere.core.Messages.Unexpected_Error, e);
                if (e.getLocalizedMessage() == null) {
                    MessageDialog.openError(getShell(), biz.isphere.core.Messages.Unexpected_Error, e.getClass().getName() + " - "
                        + getClass().getName());
                } else {
                    MessageDialog.openError(getShell(), biz.isphere.core.Messages.Unexpected_Error, e.getLocalizedMessage());
                }
            }

        }
    }

    public boolean supportsSelection(IStructuredSelection selection) {

        this.arrayListSelection.clear();

        ArrayList<DataElement> arrayListSelection = new ArrayList<DataElement>();

        for (Iterator iterSelection = selection.iterator(); iterSelection.hasNext();) {
            Object objSelection = iterSelection.next();
            if (objSelection instanceof DataElement) {
                DataElement dataElement = (DataElement)objSelection;
                ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
                if (type.isSourceMember()) {
                    arrayListSelection.add(dataElement);
                }
            }
        }

        if (arrayListSelection.size() >= 1 && arrayListSelection.size() <= 2) {
            this.arrayListSelection = arrayListSelection;
            return true;
        } else {
            return false;
        }

    }

    private RSEMember getLeftMemberFromSelection() throws Exception {
        if (this.arrayListSelection != null && this.arrayListSelection.size() >= 1) {
            Object[] objects = this.arrayListSelection.toArray();
            if (objects[0] instanceof DataElement) {
                return new RSEMember(new ISeriesMember((DataElement)objects[0]));
            }
        }
        return null;
    }

    private RSEMember getRightMemberFromSelection() throws Exception {
        if (this.arrayListSelection != null && this.arrayListSelection.size() >= 2) {
            Object[] objects = this.arrayListSelection.toArray();
            if (objects[1] instanceof DataElement) {
                return new RSEMember(new ISeriesMember((DataElement)objects[1]));
            }
        }
        return null;
    }

}