/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import biz.isphere.core.compareeditor.CompareAction;
import biz.isphere.core.compareeditor.CompareEditorConfiguration;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.compareeditor.RSECompareDialog;
import biz.isphere.rse.internal.RSEMember;

import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteSourceMember;

public class CompareEditorAction implements IObjectActionDelegate {

    protected IStructuredSelection structuredSelection;
    protected Shell shell;

    public void run(IAction arg0) {

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

                        IQSYSMember ancestorMember = dialog.getAncestorConnection().getMember(dialog.getAncestorLibrary(), dialog.getAncestorFile(),
                            dialog.getAncestorMember(), null);

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
                MessageDialog.openError(shell, biz.isphere.core.Messages.Unexpected_Error, e.getClass().getName() + " - " + getClass().getName());
            } else {
                MessageDialog.openError(shell, biz.isphere.core.Messages.Unexpected_Error, e.getLocalizedMessage());
            }
        }

    }

    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            structuredSelection = ((IStructuredSelection)selection);
            if (structuredSelection.size() >= 1 && structuredSelection.size() <= 2) {
                action.setEnabled(true);
            } else {
                action.setEnabled(false);
            }
        } else {
            structuredSelection = null;
            action.setEnabled(false);
        }
    }

    public void setActivePart(IAction action, IWorkbenchPart workbenchPart) {
        shell = workbenchPart.getSite().getShell();
    }

    private RSEMember getLeftMemberFromSelection() throws Exception {
        if (structuredSelection != null && structuredSelection.size() >= 1) {
            Object[] objects = structuredSelection.toArray();
            if (objects[0] instanceof QSYSRemoteSourceMember) {
                return new RSEMember((QSYSRemoteSourceMember)objects[0]);
            }
        }
        return null;
    }

    private RSEMember getRightMemberFromSelection() throws Exception {
        if (structuredSelection != null && structuredSelection.size() >= 2) {
            Object[] objects = structuredSelection.toArray();
            if (objects[1] instanceof QSYSRemoteSourceMember) {
                return new RSEMember((QSYSRemoteSourceMember)objects[1]);
            }
        }
        return null;
    }

}
