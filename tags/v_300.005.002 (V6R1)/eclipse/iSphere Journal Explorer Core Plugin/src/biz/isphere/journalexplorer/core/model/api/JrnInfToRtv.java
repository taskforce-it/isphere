/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.api;

/**
 * Class representing 'Journal information to retrieve' section of the
 * QjoRetrieveJournalInformation API.
 */
public class JrnInfToRtv {

    private String journal;
    private String library;

    public JrnInfToRtv(String aLibrary, String aJournal) {
        journal = aJournal;
        library = aLibrary;
    }

    public String getJournal() {
        return journal;
    }

    public String getLibrary() {
        return library;
    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(journal);
        buffer.append("/"); //$NON-NLS-1$
        buffer.append(library);

        return buffer.toString();
    }
}
