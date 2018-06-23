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
import biz.isphere.core.resourcemanagement.AbstractResource;
import biz.isphere.core.resourcemanagement.command.AbstractCommandEditingDialog;
import biz.isphere.core.resourcemanagement.command.RSECommand;
import biz.isphere.core.resourcemanagement.command.RSECommandBoth;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;

public class CommandEditingDialog extends AbstractCommandEditingDialog {

    public CommandEditingDialog(Shell parentShell, boolean editWorkspace, boolean editRepository, boolean editBoth, boolean singleCompileType,
        String workspace, String repository, RSECommand[] resourceWorkspace, RSECommand[] resourceRepository, RSECommandBoth[] resourceBothDifferent,
        RSECommand[] resourceBothEqual) {
        super(parentShell, editWorkspace, editRepository, editBoth, singleCompileType, workspace, repository, resourceWorkspace, resourceRepository,
            resourceBothDifferent, resourceBothEqual);
    }

    @Override
    protected void pushToWorkspace(AbstractResource resource) {
        RSECommand command = (RSECommand)resource;
        RSECommandHelper.createCommand(command.getCompileType(), command.getLabel(), command.isLabelEditable(), command.getCommandString(), command
            .isCommandStringEditable(), command.getId(), command.getNature(), command.getMenuOption());
    }

    @Override
    protected void deleteFromWorkspace(AbstractResource resource) {
        RSECommand command = (RSECommand)resource;
        RSECommandHelper.deleteCommand(command.getCompileType(), command.getLabel());
    }

    @Override
    protected boolean saveCommandsToXML(File toFile, boolean singleCompileType, RSECommand[] commands) {

        try {
            XMLCommandHelper.saveCommandsToXML(toFile, singleCompileType, commands);
            return true;
        } catch (Exception e) {
            ISpherePlugin.logError("Failed to save data to file: " + toFile, e); //$NON-NLS-1$
            ErrorDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Failed_to_save_data_to_file_colon_A, toFile), new Status(
                IStatus.ERROR, ISphereRSEPlugin.PLUGIN_ID, -1, ExceptionHelper.getLocalizedMessage(e), e));
            return false;
        }
    }

}
