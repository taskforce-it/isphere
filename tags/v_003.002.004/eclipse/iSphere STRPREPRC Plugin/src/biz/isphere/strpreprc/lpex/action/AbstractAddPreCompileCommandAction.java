/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.lpex.action;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.clcommands.CLFormatter;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.strpreprc.Messages;
import biz.isphere.strpreprc.gui.EditHeaderDialog;
import biz.isphere.strpreprc.model.StrPrePrcParser;

import com.ibm.as400.access.AS400;
import com.ibm.lpex.core.LpexView;

/**
 * This action adds a pre-compile command to an existing STRPREPRC header.
 */
public abstract class AbstractAddPreCompileCommandAction extends AbstractHeaderAction {

    public void doAction(LpexView view) {

        try {

            IEditorPart editor = getActiveEditor();
            if (editor == null) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Could_not_get_the_active_editor);
                return;
            }

            /*
             * Load STRPREPRC header from the view.
             */
            StrPrePrcParser header = new StrPrePrcParser(null);
            if (!header.loadFromLpexView(view)) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.STRPREPRC_header_not_found_or_incomplete);
                return;
            }

            /*
             * Prompt for a connection name pre-/post-compile command.
             */
            String connectionName = getConnectionName(editor);
            String prePostCommand = null;

            EditHeaderDialog dialog = createEditDialog();
            dialog.setMemberType(null);
            dialog.setConnectionName(connectionName);
            dialog.setCommand(prePostCommand);
            int action = dialog.open();
            if (action == EditHeaderDialog.CANCEL) {
                return;
            }

            prePostCommand = dialog.getCommand() + " " + dialog.getParameters();
            connectionName = dialog.getConnectionName();

            /*
             * Get IBM i system for creating the CLFormatter.
             */
            AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
            if (system == null) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                    Messages.bind(Messages.Could_not_get_AS400_object_for_connection_A, connectionName));
                return;
            }

            /*
             * Let the user edit the command with the CL command prompter.
             */
            if (action == EditHeaderDialog.PROMPT) {
                prePostCommand = performPromptCommand(connectionName, prePostCommand);
                if (prePostCommand == null) {
                    return;
                }
            }

            /*
             * Update the STRPREPRC header with the changed command.
             */
            addCompileCommand(header, prePostCommand);
            header.updateLpexView(view, new CLFormatter(system));

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Unexpected error when attempting to add a pre-compile command ***", e); //$NON-NLS-1$
        }
    }

    protected abstract void addCompileCommand(StrPrePrcParser header, String commandString);

    protected abstract EditHeaderDialog createEditDialog();
}
