/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.ibm.etools.iseries.subsystems.ifs.files.IFSRemoteFile;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;
import biz.isphere.rse.compareeditor.handler.CompareStreamFilesHandler;
import biz.isphere.rse.internal.RSEStreamFile;

public class CompareStreamFileEditorAction implements IObjectActionDelegate {

    private Shell shell;

    private RSEStreamFile[] selectedMembers;
    private List<RSEStreamFile> selectedMembersList;

    public CompareStreamFileEditorAction() {
        selectedMembersList = new ArrayList<RSEStreamFile>();
    }

    public void setActivePart(IAction action, IWorkbenchPart workbenchPart) {
        shell = workbenchPart.getSite().getShell();
    }

    public void run(IAction arg0) {

        try {

            if (selectedMembers.length > 0) {
                CompareStreamFilesHandler handler = new CompareStreamFilesHandler();
                handler.handleSourceCompare(selectedMembers);
            }

        } catch (Exception e) {
            ISphereRSEPlugin.logError(biz.isphere.core.Messages.Unexpected_Error, e);
            MessageDialog.openError(shell, Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {

        if (selection instanceof IStructuredSelection) {
            selectedMembers = getMembersFromSelection((IStructuredSelection)selection);
            if (selectedMembers.length >= 1) {
                action.setEnabled(true);
            } else {
                action.setEnabled(false);
            }
        } else {
            action.setEnabled(false);
        }
    }

    private RSEStreamFile[] getMembersFromSelection(IStructuredSelection structuredSelection) {

        selectedMembersList.clear();

        try {
            if (structuredSelection != null && structuredSelection.size() > 0) {
                Object[] objects = structuredSelection.toArray();
                for (Object object : objects) {
                    if (object instanceof IFSRemoteFile) {
                        selectedMembersList.add(new RSEStreamFile((IFSRemoteFile)object));
                    }
                }
            }
        } catch (Exception e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
        }

        return selectedMembersList.toArray(new RSEStreamFile[selectedMembersList.size()]);
    }
}
