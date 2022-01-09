/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.exceptions;

import biz.isphere.journalexplorer.core.Messages;

public class NoJournalEntriesLoadedException extends Exception {

    private static final long serialVersionUID = -7609562990537223356L;

    public static final String ID = "CPF7062";

    private String libraryName;
    private String journalName;

    public NoJournalEntriesLoadedException(String libraryName, String journalName) {

        this.libraryName = libraryName;
        this.journalName = journalName;
    }

    @Override
    public String getMessage() {
        return Messages.bind(Messages.Exception_No_entries_converted_or_received_from_journal_A_B, libraryName, journalName);
    }
}
