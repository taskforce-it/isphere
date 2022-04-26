/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilesearch;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SearchResultTab implements Serializable {

    private String connectionName;
    private String searchString;
    private SearchResult[] searchResult;

    public SearchResultTab(String connectionName, String searchString, SearchResult[] searchResult) {

        this.connectionName = connectionName;
        this.searchString = searchString; 
        this.searchResult = searchResult;
    }

    public String getConnectionName() {
        return connectionName;
    }
    
    public String getSearchString() {
        return searchString;
    }

    public SearchResult[] getSearchResult() {
        return searchResult;
    }
}
