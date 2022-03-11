/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.streamfilesearch;

import java.util.ArrayList;
import java.util.Collection;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.search.GenericSearchOption;
import biz.isphere.core.search.SearchOptions;

public class StreamFileSearchFilter {

    public StreamFileSearchFilter() {
    }

    public ArrayList<SearchElement> applyFilter(Collection<SearchElement> elements, SearchOptions searchOptions) {

        ArrayList<SearchElement> selectedSearchElements = new ArrayList<SearchElement>();

        Collection<SearchElement> allSearchElements = elements;
        for (SearchElement searchElement : allSearchElements) {
            if (isItemSelected(searchElement, searchOptions)) {
                selectedSearchElements.add(searchElement);
            }
        }

        return selectedSearchElements;
    }

    private boolean isItemSelected(SearchElement item, SearchOptions searchOptions) {

        if (searchOptions == null) {
            return true;
        }

        if (isTypeSelected(item, searchOptions)) {
            return true;
        }

        return false;
    }

    private boolean isTypeSelected(SearchElement item, SearchOptions searchOptions) {

        String type = searchOptions.getGenericStringOption(GenericSearchOption.STMF_TYPE, "*"); //$NON-NLS-1$

        if ("*".equals(type)) {
            return true;
        }

        String itemType = item.getType();
        if ("*BLANK".equals(type) || StringHelper.isNullOrEmpty(type)) {
            if (StringHelper.isNullOrEmpty(itemType)) {
                return true;
            } else {
                return false;
            }
        }

        // ISeriesPDMPatternMatch matcher = new
        // ISeriesPDMPatternMatch(type.toUpperCase(), true);
        // if (matcher.matches(itemType.toUpperCase())){
        // return true;
        // }

        try {
            if (StringHelper.matchesGeneric(itemType, type)) {
                return true;
            }
        } catch (Throwable e) {
            // Ignore pattern syntax errors
            return true;
        }

        return false;
    }
}
