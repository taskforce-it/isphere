/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rcp.handlers.journalexplorer;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import biz.isphere.core.internal.handler.AbstractCommandHandler;
import biz.isphere.journalexplorer.core.externalapi.Access;

/**
 * This class is the action handler of the "Open Journal Output File" action.
 */
public class OpenJournalOutputFileHandler extends AbstractCommandHandler {

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
     * ExecutionEvent)
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {

        try {
            Access.exploreJournalEntriesFromOutputFile(getShell(event), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
