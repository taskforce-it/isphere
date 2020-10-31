/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcefilesearch;

import java.io.Serializable;
import java.sql.Timestamp;

@SuppressWarnings("serial")
public class SearchResult implements Serializable {

    private String library;
    private String file;
    private String member;
    private String description;
    private Timestamp lastChangedDate;
    private SearchResultStatement[] statements;

    public SearchResult() {
        library = ""; //$NON-NLS-1$
        file = ""; //$NON-NLS-1$
        member = ""; //$NON-NLS-1$
        description = ""; //$NON-NLS-1$
        statements = null;
    }

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SearchResultStatement[] getStatements() {
        return statements;
    }

    public void setStatements(SearchResultStatement[] statements) {
        this.statements = statements;
    }

    public int getStatementsCount() {
        return statements.length;
    }

    public Timestamp getLastChangedDate() {
        return lastChangedDate;
    }

    public void setLastChangedDate(Timestamp lastChangedDate) {
        this.lastChangedDate = lastChangedDate;
    }
}
