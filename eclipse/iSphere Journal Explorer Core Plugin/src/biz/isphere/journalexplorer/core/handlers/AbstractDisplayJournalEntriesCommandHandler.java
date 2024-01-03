/*******************************************************************************
 * Copyright (c) 2012-2023 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.handlers;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.handler.AbstractCommandHandler;
import biz.isphere.journalexplorer.core.model.shared.Journal;
import biz.isphere.journalexplorer.core.model.shared.JournaledFile;
import biz.isphere.journalexplorer.core.model.shared.JournaledObject;

public abstract class AbstractDisplayJournalEntriesCommandHandler extends AbstractCommandHandler {

    protected void execute(String qualifiedConnectionName, String libraryName, String objectName, String memberName, String objectType) {

        DisplayJournalEntriesHandler handler = new DisplayJournalEntriesHandler();

        try {

            if (ISeries.JRN.equals(objectType)) {
                Journal journal = new Journal(qualifiedConnectionName, libraryName, objectName);
                handler.handleDisplayJournalEntries(journal);
            } else {
                JournaledObject object;
                if (ISeries.DTAARA.equals(objectType)) {
                    object = new JournaledObject(qualifiedConnectionName, libraryName, objectName, objectType);
                } else if (ISeries.DTAQ.equals(objectType)) {
                    object = new JournaledObject(qualifiedConnectionName, libraryName, objectName, objectType);
                } else if (ISeries.FILE.equals(objectType)) {
                    object = new JournaledFile(qualifiedConnectionName, libraryName, objectName, memberName);
                } else {
                    ISpherePlugin.logError("*** Invalid object type: " + objectType + " ***", null); //$NON-NLS-1$
                    object = null;
                }

                if (object != null) {
                    handler.handleDisplayJournalEntries(object);
                }
            }
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not handle object ***", e);
        }
    }
}
