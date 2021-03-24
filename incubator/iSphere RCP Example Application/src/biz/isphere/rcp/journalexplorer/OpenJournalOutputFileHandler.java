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
import org.eclipse.swt.widgets.Shell;

import biz.isphere.journalexplorer.core.externalapi.Access;
import biz.isphere.rcp.handlers.AbstractShellHandler;

/**
 * This class is the action handler of the "Open Output File Action".
 */
public class OpenJournalOutputFileHandler extends AbstractShellHandler {

    /**
     * Default constructor, used by the Eclipse framework.
     */
    public OpenJournalOutputFileHandler(Shell shell) {
        super(shell);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
     * ExecutionEvent)
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {

        try {
            Access.openJournalExplorerView(getShell());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
