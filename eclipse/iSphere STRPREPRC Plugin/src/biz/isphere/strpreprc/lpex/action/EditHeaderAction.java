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
import org.eclipse.ui.PlatformUI;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.strpreprc.model.StrPrePrcCommand;
import biz.isphere.strpreprc.model.StrPrePrcHeader;

import com.ibm.etools.systems.editor.SystemTextEditor;
import com.ibm.lpex.core.LpexView;

public class EditHeaderAction extends AbstractHeaderAction {

    public static final String ID = "SprPrePrc.EditHeader";

    public void doAction(LpexView view) {

        try {

            StrPrePrcHeader header = new StrPrePrcHeader();

            SystemTextEditor editor = (SystemTextEditor)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
            IEditorInput editorInput = editor.getEditorInput();

            if (!header.loadFromLpexView(view)) {
                MessageDialog.openError(getShell(), "E R R O R", "No STRPREPRC header found.");
                return;
            }

            StrPrePrcCommand createCommand = header.getCreateCommand();
            if (createCommand == null) {
                MessageDialog.openError(getShell(), "E R R O R", "No CREATE command found.");
                return;
            }

            // MessageDialog.openInformation(getShell(), "STRPREPRC",
            // "Editieren STRPREPRC header.");

            // StrPrePrcHeaderDialog dialog = new
            // StrPrePrcHeaderDialog(getShell());
            // dialog.setInput(header);
            // if (dialog.open() == Dialog.OK) {
            // header.updateLpexView(view);
            // }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private boolean performPromptCommand(String createCommand) {

        if (createCommand == null) {
            MessageDialog.openError(getShell(), "E R R O R", "Command not specified.");
            return false;
        }

        // IClCommandPrompter prompter;

        try {

            // CLFormatter formatter = new
            // CLFormatter(connection.getAS400ToolboxObject());
            // String formattedCommand = formatter.format(createCommand);
            // if (formattedCommand == null) {
            // return false;
            // }
            //
            // prompter = new ClCommandPromper();
            // prompter.setCommandString(formattedCommand);
            // prompter.setMode(CLPrompter.EDIT_MODE);
            // prompter.setConnection(connection);
            // prompter.setParent(Display.getCurrent().getActiveShell());
            //
            // if (prompter.showDialog() != CommandPrompter.OK) {
            // return false;
            // }
            //
            // header.update(prompter.getCommandString());
            //
            // updatePreview(null);

            return true;

        } catch (Throwable e) {
            MessageDialog.openError(getShell(), "E R R O R", ExceptionHelper.getLocalizedMessage(e));
            return false;
        }
    }

    public static String getLPEXMenuAction() {
        return "\"Edit header\" " + EditHeaderAction.ID;
    }
}
