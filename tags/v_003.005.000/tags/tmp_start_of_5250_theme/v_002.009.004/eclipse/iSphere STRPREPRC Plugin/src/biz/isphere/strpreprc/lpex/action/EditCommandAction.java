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

import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.clcommands.CLFormatter;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.strpreprc.Messages;
import biz.isphere.strpreprc.gui.EditHeaderDialog;
import biz.isphere.strpreprc.model.StrPrePrcParser;
import biz.isphere.strpreprc.preferences.Preferences;

import com.ibm.as400.access.AS400;
import com.ibm.lpex.core.LpexView;

/**
 * This action changes a selected pre- or post-compile command of an existing
 * STRPREPRC header.
 */
public class EditCommandAction extends AbstractHeaderAction {

    public static final String ID = "SprPrePrc.EditCommand";

    public void doAction(LpexView view) {

        try {

            boolean displayDialog = false;

            IEditorPart editor = getActiveEditor();
            if (editor == null) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Could_not_get_the_active_editor);
                return;
            }

            /*
             * Load pre-/post-command from the view.
             */
            StrPrePrcParser header = new StrPrePrcParser(null);
            if (!header.loadFromLpexView(view)) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.STRPREPRC_header_not_found_or_incomplete);
                return;
            }

            int cursorLine = IntHelper.tryParseInt(view.query("element"), 0);
            if (cursorLine <= 0) {
                return;
            }

            String prePostCommand = header.getCommandAtLine(cursorLine);
            if (prePostCommand == null) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                    Messages.bind(Messages.Pre_or_post_command_not_found_at_line_A, new Integer(cursorLine)));
                return;
            }

            String connectionName = getConnectionName(editor);
            if (StringHelper.isNullOrEmpty(connectionName)) {
                displayDialog = true;
            }

            if (!Preferences.getInstance().skipEditDialog()) {
                displayDialog = true;
            }

            /*
             * Prompt for a connection name or when using a header template.
             */
            int action;
            if (displayDialog) {
                EditHeaderDialog dialog = new EditHeaderDialog(getShell(), Messages.Menu_Edit_Pre_Post_Command, EditHeaderDialog.PRE_POST_COMMAND);
                dialog.setMemberType(null);
                dialog.setConnectionName(connectionName);
                dialog.setCommand(prePostCommand);
                action = dialog.open();
                if (action == EditHeaderDialog.CANCEL) {
                    return;
                }

                prePostCommand = dialog.getCommand() + " " + dialog.getParameters();
                connectionName = dialog.getConnectionName();
            } else {
                action = EditHeaderDialog.PROMPT;
            }

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
            if (header.changeCommandAtLine(cursorLine, prePostCommand)) {
                header.updateLpexView(view, new CLFormatter(system));
            }

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Unexpected error when attempting to edit a pre-/post-compile command ***", e); //$NON-NLS-1$
        }
    }

    public static String getLPEXMenuAction() {
        return "\"" + Messages.Menu_Edit_Pre_Post_Command + "\" " + EditCommandAction.ID; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
