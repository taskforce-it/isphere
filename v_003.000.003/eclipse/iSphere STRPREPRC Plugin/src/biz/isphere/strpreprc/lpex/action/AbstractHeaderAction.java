/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.lpex.action;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.clcommands.ICLPrompter;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;

import com.ibm.as400.ui.util.CommandPrompter;
import com.ibm.lpex.core.LpexAction;
import com.ibm.lpex.core.LpexView;

public abstract class AbstractHeaderAction implements LpexAction {

    public boolean available(LpexView view) {
        return (view.currentElement() != 0) && (!view.queryOn("readonly"));
    }

    protected Shell getShell() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    }

    protected IEditorPart getActiveEditor() {

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            IWorkbenchPage activePage = window.getActivePage();
            if (activePage != null) {
                return activePage.getActiveEditor();
            }
        }

        return null;
    }

    protected String getConnectionName(IEditorPart editor) {
        String connectionName = IBMiHostContributionsHandler.getConnectionName(editor);
        if (connectionName == null) {
            return null;
        }
        return connectionName;
    }

    protected String performPromptCommand(String connectionName, String createCommand) {

        ICLPrompter prompter = IBMiHostContributionsHandler.getCLPrompter(connectionName);

        prompter.setCommandString(createCommand);
        prompter.setMode(ICLPrompter.EDIT_MODE);
        prompter.setConnection(connectionName);
        prompter.setParent(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

        if (prompter.showDialog() == CommandPrompter.OK) {
            return prompter.getCommandString();
        }

        return null;
    }
}
