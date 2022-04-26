/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchOptions {

    public static int ARGUMENTS_SIZE = 16;
    public static int MAX_STRING_SIZE = 40;

    public static final int CONTAINS = 1;
    public static final int CONTAINS_NOT = -1;

    public static final String MATCH_ALL = "*ALL";
    public static final String MATCH_ANY = "*ANY";
    public static final String MATCH_MESSAGE_ID = "*MSGID";
    
    public static final String CASE_MATCH = "*MATCH";
    public static final String CASE_IGNORE = "*IGNORE";

    public static final String SEARCH_ARG_STRING = "*STRING";
    public static final String SEARCH_ARG_REGEX = "*REGEX";

    private String matchOption;
    private boolean showAllItems;
    private List<SearchArgument> searchArguments;
    private Map<String, Object> genericOptions;

    public SearchOptions() {
        this(MATCH_ALL, true);
    }

    public SearchOptions(String aMatchOption, boolean aShowAllItems) {
        matchOption = aMatchOption;
        showAllItems = aShowAllItems;
        searchArguments = null;
    }

    public boolean isShowAllItems() {
        return showAllItems;
    }

    public String getMatchOption() {
        return matchOption;
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

    public void setOption(String anOption, Boolean aValue) {
        getOptions().put(anOption, aValue);
    }

    public boolean isOption(String anOption) {
        if (!getOptions().containsKey(anOption)) {
            return false;
        }
        Object tValue = getOptions().get(anOption);
        if (!(tValue instanceof Boolean)) {
            throw new IllegalArgumentException("Invalid object type assigned to boolean option: " + tValue);
        }
        return ((Boolean)tValue).booleanValue();
    }

    private Map<String, Object> getOptions() {
        if (genericOptions == null) {
            genericOptions = new HashMap<String, Object>();
        }
        return genericOptions;
    }
}
