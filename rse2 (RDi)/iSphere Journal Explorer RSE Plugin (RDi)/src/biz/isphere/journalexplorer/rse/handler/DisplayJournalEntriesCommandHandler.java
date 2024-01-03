/*******************************************************************************
 * Copyright (c) 2012-2023 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.rse.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import biz.isphere.journalexplorer.core.handlers.AbstractDisplayJournalEntriesCommandHandler;
import biz.isphere.journalexplorer.rse.ui.dialogs.DisplayJournalEntriesDialog;

public class DisplayJournalEntriesCommandHandler extends AbstractDisplayJournalEntriesCommandHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {

        DisplayJournalEntriesDialog dialog = new DisplayJournalEntriesDialog(getShell());
        if (dialog.open() == DisplayJournalEntriesDialog.OK) {
            String qualifiedConnectionName = dialog.getQualifiedConnectionName();
            String objectType = dialog.getObjectType();
            String libraryName = dialog.getLibraryName();
            String objectName = dialog.getObjectName();
            String memberName = dialog.getMemberName();
            execute(qualifiedConnectionName, libraryName, objectName, memberName, objectType);
        }

        return null;
    }
}