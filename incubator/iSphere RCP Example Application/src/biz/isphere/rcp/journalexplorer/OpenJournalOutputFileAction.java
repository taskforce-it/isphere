/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rcp.journalexplorer;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.rcp.AbstractMenuAction;

/**
 * This action is assigned to menu option "Transfer iSphere Library".
 */
public class OpenJournalOutputFileAction extends AbstractMenuAction {

    public static final String ID = "biz.isphere.rcp.menu.openjournaloutputfile";
    private Shell shell;

    public void run(IAction action) {
        try {

            OpenJournalOutputFileHandler handler = new OpenJournalOutputFileHandler(getShell());
            ExecutionEvent event = new ExecutionEvent();
            handler.execute(event);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
