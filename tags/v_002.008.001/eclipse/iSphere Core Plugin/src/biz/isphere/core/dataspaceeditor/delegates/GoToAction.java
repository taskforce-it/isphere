/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.delegates;

import java.util.Collections;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.services.IServiceLocator;

import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditor.AbstractDataSpaceEditor;
import biz.isphere.core.dataspaceeditor.GoToHandler;

public class GoToAction extends AbstractEditorAction {

    public GoToAction() {
        super(GoToHandler.ID);
    }

    private AbstractDataSpaceEditor activeEditor;

    @Override
    public void run() {
        Command command = getGoToCommand();
        try {
            command.executeWithChecks(new ExecutionEvent(command, Collections.singletonMap("active.editor", activeEditor), null, null));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Called by the menu manager.
     */
    @Override
    public boolean isEnabled() {
        return getGoToCommand().isEnabled();
    }

    @Override
    public String getText() {
        return Messages.GoTo_Location_dots;
    }

    public void setActiveEditor(AbstractDataSpaceEditor activeEditor) {
        this.activeEditor = activeEditor;
    }

    private Command getGoToCommand() {
        ICommandService service = (ICommandService)((IServiceLocator)PlatformUI.getWorkbench()).getService(ICommandService.class);
        Command command = service.getCommand(GoToHandler.ID);
        return command;
    }

}
