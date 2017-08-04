/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.actions;

import org.eclipse.jface.action.Action;

public class CloseJournalAction extends Action {

    private String library;

    private String fileName;

    private String connectionName;

    public CloseJournalAction(String library, String fileName, String connectionName) {

        super();
        this.library = library;
        this.fileName = fileName;
        this.connectionName = connectionName;
    }

    @Override
    public String getText() {
        return this.connectionName.trim() + ":" + this.library.trim() + "/" + this.fileName.trim();
    }

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

}
