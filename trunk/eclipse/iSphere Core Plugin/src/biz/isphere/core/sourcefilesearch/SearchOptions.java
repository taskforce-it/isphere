/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcefilesearch;

import java.util.ArrayList;
import java.util.List;

import biz.isphere.core.search.SearchArgument;

public class SearchOptions {

    private boolean matchAll;
    private boolean showRecords;
    private List<SearchArgument> searchArguments;

    public SearchOptions() {
        this(true, true);
    }

    public SearchOptions(boolean aMatchAll, boolean aShowRecords) {
        matchAll = aMatchAll;
        showRecords = aShowRecords;
        searchArguments = null;
    }

    public boolean isShowRecords() {
        return showRecords;
    }

    public boolean isMatchAll() {
        return matchAll;
    }

    public List<SearchArgument> getSearchArguments() {
        if (searchArguments == null) {
            searchArguments = new ArrayList<SearchArgument>();
        }
        return searchArguments;
    }

    public void addSearchArgument(SearchArgument aSearchArgument) {
        getSearchArguments().add(aSearchArgument);
    }

    public void setSearchArguments(List<SearchArgument> aSearchArguments) {
        searchArguments = aSearchArguments;
    }

}
