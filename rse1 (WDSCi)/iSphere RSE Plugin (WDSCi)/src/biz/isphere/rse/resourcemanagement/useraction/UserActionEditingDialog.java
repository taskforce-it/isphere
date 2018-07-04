/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.resourcemanagement.useraction;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.resourcemanagement.AbstractResource;
import biz.isphere.core.resourcemanagement.useraction.AbstractUserActionEditingDialog;
import biz.isphere.core.resourcemanagement.useraction.RSEUserAction;
import biz.isphere.core.resourcemanagement.useraction.RSEUserActionBoth;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;

public class UserActionEditingDialog extends AbstractUserActionEditingDialog {

    public UserActionEditingDialog(Shell parentShell, boolean editWorkspace, boolean editRepository, boolean editBoth, boolean singleDomain,
        String workspace, String repository, RSEUserAction[] resourceWorkspace, RSEUserAction[] resourceRepository,
        RSEUserActionBoth[] resourceBothDifferent, RSEUserAction[] resourceBothEqual) {
        super(parentShell, editWorkspace, editRepository, editBoth, singleDomain, workspace, repository, resourceWorkspace, resourceRepository,
            resourceBothDifferent, resourceBothEqual);
    }

    @Override
    protected void pushToWorkspace(AbstractResource resource) {

        RSEUserAction rseUserAction = (RSEUserAction)resource;
        RSEUserAction workspaceUserAction = RSEUserActionHelper.getUserAction(rseUserAction.getDomain(), rseUserAction.getLabel());

        // Update existing user actions.
        if (workspaceUserAction != null) {
            workspaceUserAction.setCommandString(workspaceUserAction.getCommandString());
            workspaceUserAction.setPromptFirst(workspaceUserAction.isPromptFirst());
            workspaceUserAction.setRefreshAfter(workspaceUserAction.isRefreshAfter());
            workspaceUserAction.setShowAction(workspaceUserAction.isShowAction());
            workspaceUserAction.setSingleSelection(workspaceUserAction.isSingleSelection());
            workspaceUserAction.setInvokeOnce(workspaceUserAction.isInvokeOnce());
            workspaceUserAction.setComment(workspaceUserAction.getComment());
            workspaceUserAction.setFileTypes(workspaceUserAction.getFileTypes());
            // must be called before setVendor()
            workspaceUserAction.setIBM(workspaceUserAction.isIBM());
            workspaceUserAction.setVendor(workspaceUserAction.getVendor());
            return;
        }

        RSEUserActionHelper.createUserAction(rseUserAction.getDomain(), rseUserAction.getLabel(), rseUserAction.getCommandString(), rseUserAction
            .isPromptFirst(), rseUserAction.isRefreshAfter(), rseUserAction.isShowAction(), rseUserAction.isSingleSelection(), rseUserAction
            .isInvokeOnce(), rseUserAction.getComment(), rseUserAction.getFileTypes(), rseUserAction.isIBM(), rseUserAction.getVendor());
    }

    @Override
    protected void deleteFromWorkspace(AbstractResource resource) {

        RSEUserAction rseUserAction = (RSEUserAction)resource;
        RSEUserAction workspaceUserAction = RSEUserActionHelper.getUserAction(rseUserAction.getDomain(), rseUserAction.getLabel());

        // Ensure that commands that are not editable are not deleted.
        // Usually that are IBM supplied commands.
        if (workspaceUserAction != null) {
            return;
        }

        RSEUserActionHelper.deleteUserAction(rseUserAction.getDomain(), rseUserAction.getLabel());
    }

    @Override
    protected boolean saveUserActionsToXML(File toFile, boolean singleDomain, RSEUserAction[] userActions) {

        try {
            XMLUserActionHelper.saveUserActionsToXML(toFile, singleDomain, userActions);
            return true;
        } catch (Exception e) {
            ISpherePlugin.logError("Failed to save data to file: " + toFile, e); //$NON-NLS-1$
            ErrorDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Failed_to_save_data_to_file_colon_A, toFile), new Status(
                IStatus.ERROR, ISphereRSEPlugin.PLUGIN_ID, -1, ExceptionHelper.getLocalizedMessage(e), e));
            return false;
        }
    }

}
