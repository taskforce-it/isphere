/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import biz.isphere.rse.handler.CompareSourceMembersHandler;

public class CompareEditorMenuAction implements IWorkbenchWindowActionDelegate {

    public static final String ID = "biz.isphere.rse.actions.CompareEditorMenuAction";

    public void run(IAction arg0) {
        
        try {

            CompareSourceMembersHandler handler = new CompareSourceMembersHandler();
            ExecutionEvent event = new ExecutionEvent();
            handler.execute(event);

        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }

    public void dispose() {
    }

    public void init(IWorkbenchWindow window) {
    }
}
