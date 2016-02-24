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
import biz.isphere.core.clcommands.CLFormatter;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.strpreprc.Messages;
import biz.isphere.strpreprc.model.StrPrePrcParser;

import com.ibm.as400.access.AS400;
import com.ibm.lpex.core.LpexView;

public class EditCommandAction extends AbstractHeaderAction {

    public static final String ID = "SprPrePrc.EditCommand";

    public void doAction(LpexView view) {

        try {

            IEditorPart editor = getActiveEditor();
            if (editor == null) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Could_not_get_the_active_editor);
                return;
            }

            StrPrePrcParser header = new StrPrePrcParser(null);
            if (!header.loadFromLpexView(view)) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.STRPREPRC_header_not_found_or_incomplete);
                return;
            }

            String connectionName = getConnectionName(editor);
            if (StringHelper.isNullOrEmpty(connectionName)) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Missing_connection_name);
                return;
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

            int cursorLine = IntHelper.tryParseInt(view.query("element"), 0);
            if (cursorLine <= 0) {
                return;
            }

            String commandString = header.getCommandAtLine(cursorLine);

            System.out.println("Pre/Post command: " + commandString + ", line#: " + cursorLine);

            /*
             * Update the STRPREPRC header with the changed command.
             */
            if (header.changeCommandAtLine(cursorLine, commandString + " MSGTYPE(*ALL)")) {
                header.updateLpexView(view, new CLFormatter(system));
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static String getLPEXMenuAction() {
        return "\"" + Messages.Menu_Edit_Pre_Post_Command + "\" " + EditCommandAction.ID; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
