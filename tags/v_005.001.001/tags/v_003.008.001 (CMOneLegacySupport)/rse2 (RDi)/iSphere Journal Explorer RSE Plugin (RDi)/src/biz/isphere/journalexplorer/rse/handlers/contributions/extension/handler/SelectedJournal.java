/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.rse.handlers.contributions.extension.handler;

import biz.isphere.journalexplorer.rse.handlers.contributions.extension.point.ISelectedJournal;

public class SelectedJournal implements ISelectedJournal {

    private String connectionName;
    private String libraryName;
    private String journalName;

    public SelectedJournal(String connectionName, String libraryName, String journalName) {
        this.connectionName = connectionName;
        this.libraryName = libraryName;
        this.journalName = journalName;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnetionName(String connetionName) {
        this.connectionName = connetionName;
    }

    public String getLibrary() {
        return libraryName;
    }

    public void setLibrary(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getName() {
        return journalName;
    }

    public void setName(String journalName) {
        this.journalName = journalName;
    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(getLibrary());
        buffer.append("/"); //$NON-NLS-1$
        buffer.append(getName());

        return buffer.toString();
    }
}
