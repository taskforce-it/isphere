/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model;

import biz.isphere.base.internal.StringHelper;

public class SQLWhereClause {

    private String fileName;
    private String libraryName;
    private String whereClause;

    public SQLWhereClause() {
        this("");
    }

    public SQLWhereClause(String whereClause) {
        this("", "", whereClause);
    }

    public SQLWhereClause(String fileName, String libraryName) {
        this(fileName, libraryName, "");
    }

    public SQLWhereClause(String fileName, String libraryName, String whereClause) {
        this.fileName = fileName.trim();
        this.libraryName = libraryName.trim();
        this.whereClause = whereClause.trim();
    }

    public boolean isEmpty() {
        return !hasTable() && StringHelper.isNullOrEmpty(whereClause);
    }

    public boolean hasTable() {

        if (!StringHelper.isNullOrEmpty(fileName) && !StringHelper.isNullOrEmpty(libraryName)) {
            return true;
        }

        return false;
    }

    public boolean hasClause() {
        return !StringHelper.isNullOrEmpty(whereClause);
    }

    public String getFile() {
        return fileName;
    }

    public String getLibrary() {
        return libraryName;
    }

    public String getClause() {
        return whereClause;
    }
}
