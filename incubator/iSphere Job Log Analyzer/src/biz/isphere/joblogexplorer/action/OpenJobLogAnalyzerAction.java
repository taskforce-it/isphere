/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.action;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import biz.isphere.joblogexplorer.handler.OpenJobLogAnalyzerHandler;

public class OpenJobLogAnalyzerAction implements IObjectActionDelegate {

    public static final String ID = "biz.isphere.joblogexplorer.action.OpenJobLogAnalyzerAction"; //$NON-NLS-1$

    protected Shell shell;

    public void run(IAction action) {
        try {
            OpenJobLogAnalyzerHandler handler = new OpenJobLogAnalyzerHandler();
            ExecutionEvent event = new ExecutionEvent();
            handler.execute(event);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
        System.out.println("Selection change ***");
        return;
    }

    public void dispose() {
        return;
    }

    public void init(IWorkbenchWindow window) {
        return;
    }

    public void setActivePart(IAction action, IWorkbenchPart workbenchPart) {
        shell = workbenchPart.getSite().getShell();
    }
}
