/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.lpex.action;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.clcommands.CLFormatter;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.preferences.DoNotAskMeAgain;
import biz.isphere.core.preferences.DoNotAskMeAgainDialog;
import biz.isphere.strpreprc.Messages;
import biz.isphere.strpreprc.gui.EditHeaderDialog;
import biz.isphere.strpreprc.model.StrPrePrcParser;
import biz.isphere.strpreprc.preferences.Preferences;

import com.ibm.as400.access.AS400;
import com.ibm.lpex.core.LpexView;

/**
 * This action adds or changes the creation command of an existing (change) or
 * non-existing (add) STRPREPRC header.
 */
public class EditHeaderAction extends AbstractHeaderAction {

    public static final String ID = "SprPrePrc.EditHeader";

    public void doAction(LpexView view) {

        try {

            boolean displayDialog = false;

            IEditorPart editor = getActiveEditor();
            if (editor == null) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Could_not_get_the_active_editor);
                return;
            }

            String memberType = getMemberType(editor);

            /*
             * Load STRPREPRC header either from the view or from a default
             * template.
             */
            StrPrePrcParser header = new StrPrePrcParser(memberType);
            if (!header.loadFromLpexView(view)) {
                header.loadDefaultTemplate();
                displayDialog = true;
            } else {
                if (header.hasSections() && !Preferences.getInstance().useParameterSections()) {
                    DoNotAskMeAgainDialog.openWarning(getShell(), DoNotAskMeAgain.WARNING_REMOVE_STRPREPRC_SECTIONS,
                        Messages.Sections_IMPORTANT_COMPILE_and_LINK_are_removed_when_updating_the_STRPREPRC_header_Change_preferences);
                }
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
                EditHeaderDialog dialog = new EditHeaderDialog(getShell(), Messages.Menu_Edit_header, EditHeaderDialog.HEADER);
                dialog.setMemberType(memberType);
                dialog.setConnectionName(connectionName);
                dialog.setCommand(header.getFullCommand());
                action = dialog.open();
                if (action == EditHeaderDialog.CANCEL) {
                    return;
                }

                if (header.getCommand() == null || !header.getCommand().equals(dialog.getCommand())) {
                    header.setFullCommand(dialog.getCommand() + " " + dialog.getParameters()); //$NON-NLS-1$
                } else {
                    header.updateFullCommand(dialog.getCommand() + " " + dialog.getParameters()); //$NON-NLS-1$
                }
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
            String fullCommandString;
            if (action == EditHeaderDialog.PROMPT) {
                fullCommandString = performPromptCommand(connectionName, header.getFullCommand());
                if (fullCommandString == null) {
                    return;
                }
            } else {
                fullCommandString = header.getFullCommand();
            }

            /*
             * Update the STRPREPRC header with the changed command.
             */
            header.updateFullCommand(fullCommandString);
            header.updateLpexView(view, new CLFormatter(system));

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Unexpected error when attempting to edit a SPRPREPRC header ***", e); //$NON-NLS-1$
        }
    }

    private String getMemberType(IEditorPart editor) {

        IEditorInput editorInput = getActiveEditor().getEditorInput();
        if (editorInput instanceof FileEditorInput) {
            FileEditorInput fileEditorInput = (FileEditorInput)editorInput;
            return fileEditorInput.getFile().getFileExtension();
        }

        return null;
    }

    public static String getLPEXMenuAction() {
        return "\"" + Messages.Menu_Edit_header + "\" " + EditHeaderAction.ID; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
