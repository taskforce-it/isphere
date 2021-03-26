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

import biz.isphere.rcp.AbstractMenuAction;

/**
 * This action is assigned to menu option "Open Journal Json File".
 */
public class OpenJournalJsonFileAction extends AbstractMenuAction {

    public static final String ID = "biz.isphere.rcp.menu.openjournaljsonfile";

    public void run(IAction action) {
        try {

            OpenJournalJsonFileHandler handler = new OpenJournalJsonFileHandler(getShell());
            ExecutionEvent event = new ExecutionEvent();
            handler.execute(event);

        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
