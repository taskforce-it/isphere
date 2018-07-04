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
import biz.isphere.core.resourcemanagement.filter.RSEProfile;
import biz.isphere.core.resourcemanagement.useraction.AbstractUserActionEditingDialog;
import biz.isphere.core.resourcemanagement.useraction.AbstractUserActionEntryDialog;
import biz.isphere.core.resourcemanagement.useraction.RSEDomain;
import biz.isphere.core.resourcemanagement.useraction.RSEUserAction;
import biz.isphere.core.resourcemanagement.useraction.RSEUserActionBoth;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;

public class UserActionEntryDialog extends AbstractUserActionEntryDialog {

    public UserActionEntryDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected RSEProfile[] getProfiles() {
        return RSEUserActionHelper.getProfiles();
    }

    @Override
    protected RSEDomain[] getDomains(RSEProfile profile) {
        return RSEUserActionHelper.getDomains(profile);
    }

    @Override
    protected RSEUserAction[] getUserActions(RSEProfile profile) {
        return RSEUserActionHelper.getUserActions(profile);
    }

    @Override
    protected RSEUserAction[] getUserActions(RSEDomain domain) {
        return RSEUserActionHelper.getUserActions(domain);
    }

    @Override
    protected int openEditingDialog(Shell parentShell, boolean editWorkspace, boolean editRepository, boolean editBoth, boolean singleDomain,
        String workspace, String repository, RSEUserAction[] resourcesWorkspace, RSEUserAction[] resourcesRepository,
        RSEUserActionBoth[] resourcesBothDifferent, RSEUserAction[] resourcesBothEqual) {
        AbstractUserActionEditingDialog dialog = new UserActionEditingDialog(getShell(), editWorkspace, editRepository, editBoth, singleDomain,
            workspace, repository, resourcesWorkspace, resourcesRepository, resourcesBothDifferent, resourcesBothEqual);
        return dialog.open();
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

    @Override
    protected RSEUserAction[] restoreUserActionsFromXML(File fromFile, boolean singleDomain, RSEProfile profile, RSEDomain domain) {

        try {
            return XMLUserActionHelper.restoreUserActionsFromXML(fromFile, singleDomain, profile, domain);
        } catch (Exception e) {
            ISpherePlugin.logError("Failed to load data from file: " + fromFile, e); //$NON-NLS-1$
            ErrorDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Failed_to_load_data_from_file_colon_A, fromFile),
                new Status(IStatus.ERROR, ISphereRSEPlugin.PLUGIN_ID, -1, ExceptionHelper.getLocalizedMessage(e), e));
            return null;
        }
    }

}
