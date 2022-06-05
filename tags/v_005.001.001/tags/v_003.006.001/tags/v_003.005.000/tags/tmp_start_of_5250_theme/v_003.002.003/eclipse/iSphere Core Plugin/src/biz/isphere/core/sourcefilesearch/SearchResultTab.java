/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcefilesearch;

import java.io.Serializable;

import biz.isphere.core.search.ISearchResultTab;
import biz.isphere.core.search.SearchOptions;

@SuppressWarnings("serial")
public class SearchResultTab implements ISearchResultTab, Serializable {

    private String connectionName;
    private String searchString;
    private SearchResult[] searchResult;
    private SearchOptions searchOptions;

    public SearchResultTab(String connectionName, String searchString, SearchResult[] searchResult, SearchOptions searchOptions) {

        this.connectionName = connectionName;
        this.searchString = searchString;
        this.searchResult = searchResult;
        this.searchOptions = searchOptions;
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

    public SearchOptions getSearchOptions() {
        return searchOptions;
    }

    public boolean hasSearchOptions() {
        
        if ( searchOptions != null){
            return true;
        }
        
        return false;
    }

    public String toText() {

        StringBuilder buffer = new StringBuilder();

        buffer.append("Connection: " + connectionName);
        buffer.append("\n");
        if (searchOptions != null) {
            buffer.append(searchOptions.toText());
        } else {
            buffer.append(searchString);
            buffer.append("\n");
        }

        return buffer.toString();
    }
}
