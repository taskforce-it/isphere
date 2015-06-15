/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        setImageDescriptor(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_COMPARE));
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

                RSECompareDialog dialog;
                if (selectedMembers.length > 2) {
                    dialog = new RSECompareDialog(shell, true, selectedMembers);
                } else if (selectedMembers.length == 2) {
                    dialog = new RSECompareDialog(shell, true, selectedMembers[0], selectedMembers[1]);
                } else {
                    dialog = new RSECompareDialog(shell, true, selectedMembers[0]);
                }

                if (dialog.open() == Dialog.OK) {

                    boolean editable = dialog.isEditable();
                    boolean considerDate = dialog.isConsiderDate();
                    boolean ignoreCase = dialog.isIgnoreCase();
                    boolean threeWay = dialog.isThreeWay();

                    RSEMember rseAncestorMember = null;
                    if (threeWay) {
                        rseAncestorMember = dialog.getAncestorRSEMember();
                    }

                    CompareEditorConfiguration cc = new CompareEditorConfiguration();
                    cc.setLeftEditable(editable);
                    cc.setConsiderDate(considerDate);
                    cc.setIgnoreCase(ignoreCase);
                    cc.setThreeWay(threeWay);

                    if (selectedMembers.length > 2) {
                        for (RSEMember rseSelectedMember : selectedMembers) {
                            RSEMember rseRightMember = getRightRSEMember(dialog.getRightConnection(), dialog.getRightLibrary(),
                                dialog.getRightFile(), rseSelectedMember.getMember());
                            if (!rseRightMember.exists()) {
                                String message = biz.isphere.core.Messages
                                    .bind(biz.isphere.core.Messages.Member_2_file_1_in_library_0_not_found, new Object[] {
                                        rseSelectedMember.getLibrary(), rseSelectedMember.getSourceFile(), rseSelectedMember.getMember() });
                                MessageDialog.openError(shell, biz.isphere.core.Messages.Error, message);

                            } else {
                                CompareAction action = new CompareAction(cc, rseAncestorMember, rseSelectedMember, rseRightMember, null);
                                action.run();
                            }
                        }
                    } else {
                        RSEMember rseLeftMember = dialog.getLeftRSEMember();
                        RSEMember rseRightMember = dialog.getRightRSEMember();
                        CompareAction action = new CompareAction(cc, rseAncestorMember, rseLeftMember, rseRightMember, null);
                        action.run();
                    }
                }
            }

        } catch (Exception e) {
            ISphereRSEPlugin.logError(biz.isphere.core.Messages.Unexpected_Error, e);
            if (e.getLocalizedMessage() == null) {
                MessageDialog.openError(shell, biz.isphere.core.Messages.Unexpected_Error, e.getClass().getName() + " - " + getClass().getName());
            } else {
                MessageDialog.openError(shell, biz.isphere.core.Messages.Unexpected_Error, e.getLocalizedMessage());
            }
        }
    }

    private RSEMember getRightRSEMember(ISeriesConnection connection, String libraryName, String sourceFileName, String memberName) {
        try {
            return new RSEMember((ISeriesMember)connection.getISeriesMember(libraryName, sourceFileName, memberName));
        } catch (Exception e) {
            MessageDialog.openError(shell, biz.isphere.core.Messages.Error, e.getMessage());
            return null;
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
}