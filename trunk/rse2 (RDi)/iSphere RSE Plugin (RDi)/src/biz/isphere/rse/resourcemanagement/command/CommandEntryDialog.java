/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.resourcemanagement.command;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.resourcemanagement.command.AbstractCommandEditingDialog;
import biz.isphere.core.resourcemanagement.command.AbstractCommandEntryDialog;
import biz.isphere.core.resourcemanagement.command.RSECommand;
import biz.isphere.core.resourcemanagement.command.RSECommandBoth;
import biz.isphere.core.resourcemanagement.command.RSECompileType;
import biz.isphere.core.resourcemanagement.filter.RSEProfile;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;

public class CommandEntryDialog extends AbstractCommandEntryDialog {

    public CommandEntryDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected RSEProfile[] getProfiles() {
        return RSECommandHelper.getProfiles();
    }

    @Override
    protected RSECompileType[] getCompileTypes(RSEProfile profile) {
        return RSECommandHelper.getCompileTypes(profile);
    }

    @Override
    protected RSECommand[] getCommands(RSEProfile profile) {
        return RSECommandHelper.getCommands(profile);
    }

    @Override
    protected RSECommand[] getCommands(RSECompileType compileType) {
        return RSECommandHelper.getCommands(compileType);
    }

    @Override
    protected int openEditingDialog(Shell parentShell, boolean editWorkspace, boolean editRepository, boolean editBoth, boolean singleCommandType,
        String workspace, String repository, RSECommand[] resourcesWorkspace, RSECommand[] resourcesRepository,
        RSECommandBoth[] resourcesBothDifferent, RSECommand[] resourcesBothEqual) {
        AbstractCommandEditingDialog dialog = new CommandEditingDialog(getShell(), editWorkspace, editRepository, editBoth, singleCommandType,
            workspace, repository, resourcesWorkspace, resourcesRepository, resourcesBothDifferent, resourcesBothEqual);
        return dialog.open();
    }

    @Override
    protected boolean saveCommandsToXML(File toFile, boolean singleCommandType, RSECommand[] commands) {

        try {
            XMLCommandHelper.saveCommandsToXML(toFile, singleCommandType, commands);
            return true;
        } catch (Exception e) {
            ISpherePlugin.logError("Failed to save data to file: " + toFile, e); //$NON-NLS-1$
            ErrorDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Failed_to_save_data_to_file_colon_A, toFile), new Status(
                IStatus.ERROR, ISphereRSEPlugin.PLUGIN_ID, ExceptionHelper.getLocalizedMessage(e), e));
            return false;
        }
    }

    @Override
    protected RSECommand[] restoreCommandsFromXML(File fromFile, boolean singleCommandType, RSEProfile profile, RSECompileType compileType) {

        try {
            return XMLCommandHelper.restoreCommandsFromXML(fromFile, singleCommandType, profile, compileType);
        } catch (Exception e) {
            ISpherePlugin.logError("Failed to load data from file: " + fromFile, e); //$NON-NLS-1$
            ErrorDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Failed_to_load_data_from_file_colon_A, fromFile),
                new Status(IStatus.ERROR, ISphereRSEPlugin.PLUGIN_ID, ExceptionHelper.getLocalizedMessage(e), e));
            return null;
        }
    }

}
