/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import biz.isphere.core.Messages;

@SuppressWarnings("serial")
public class SearchOptions implements Serializable {

    private static String SPACE = " "; //$NON-NLS-1$
    private static final String NEW_LINE = "\n";

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
    private Map<String, GenericSearchOption> genericOptions;

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

    public void setShowAllItems(boolean showAllItems) {
        this.showAllItems = showAllItems;
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

    public void setGenericOption(String anOption, Boolean aValue) {
        getOrCreateGenericOptions().put(anOption, new GenericSearchOption(anOption, aValue));
    }

    public boolean isGenericOption(String anOption) {
        if (!getOrCreateGenericOptions().containsKey(anOption)) {
            return false;
        }
        GenericSearchOption tValue = getOrCreateGenericOptions().get(anOption);
        if (!(tValue.getValue() instanceof Boolean)) {
            throw new IllegalArgumentException("Invalid object type assigned to boolean option: " + tValue);
        }
        return ((Boolean)tValue.getValue()).booleanValue();
    }

    public boolean hasGenericOptions() {

        if (getOrCreateGenericOptions().size() > 0) {
            return true;
        }

        return false;
    }

    public GenericSearchOption[] getGenericOptions() {

        List<GenericSearchOption> list = new LinkedList<GenericSearchOption>();

        Collection<GenericSearchOption> values = genericOptions.values();
        for (GenericSearchOption genericSearchOption : values) {
            list.add(genericSearchOption);
        }

        return list.toArray(new GenericSearchOption[list.size()]);
    }

    private Map<String, GenericSearchOption> getOrCreateGenericOptions() {
        if (genericOptions == null) {
            genericOptions = new HashMap<String, GenericSearchOption>();
        }
        return genericOptions;
    }

    public String toText() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(Messages.Conditions_to_match_colon + SPACE + getMatchOption());
        buffer.append(NEW_LINE);
        buffer.append(Messages.Show_all_matches_colon + SPACE + isShowAllItems());
        buffer.append(NEW_LINE);
        buffer.append(NEW_LINE);
        buffer.append(Messages.Search_arguments_colon);

        int c = 0;
        for (SearchArgument searchArgument : searchArguments) {
            c++;
            buffer.append(NEW_LINE);
            buffer.append("#" + c + ": " + searchArgument.toText()); //$NON-NLS-#1$ //$NON-NLS-2$
        }

        if (hasGenericOptions()) {
            buffer.append(NEW_LINE);
            buffer.append(NEW_LINE);
            buffer.append(Messages.Additional_Options_colon);
            for (GenericSearchOption genericOption : genericOptions.values()) {
                c++;
                buffer.append(NEW_LINE);
                buffer.append(genericOption.toText());
            }
        }

        return buffer.toString();
    }
}
