/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.streamfilesearch;

import java.io.Serializable;
import java.sql.Timestamp;

@SuppressWarnings("serial")
public class SearchResult implements Serializable {

    private String directory;
    private String streamFile;
    private String type;
    private Timestamp lastChangedDate;
    private SearchResultStatement[] statements;

    public SearchResult() {
        directory = ""; //$NON-NLS-1$
        streamFile = ""; //$NON-NLS-1$
        type = ""; //$NON-NLS-1$
        statements = null;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getStreamFile() {
        return streamFile;
    }

    public void setStreamFile(String streamFile) {
        this.streamFile = streamFile;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(directory);
        buffer.append("/");
        buffer.append(streamFile);
        return buffer.toString();
    }
}
