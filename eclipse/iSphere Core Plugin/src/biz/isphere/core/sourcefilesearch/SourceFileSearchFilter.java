/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcefilesearch;

import java.util.ArrayList;
import java.util.Collection;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.search.GenericSearchOption;
import biz.isphere.core.search.SearchOptions;

public class SourceFileSearchFilter {

    public SourceFileSearchFilter() {
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

    public boolean isItemSelected(SearchElement item, SearchOptions searchOptions) {

        if (searchOptions == null) {
            return true;
        }

        if (isSourceTypeSelected(item, searchOptions)) {
            return true;
        }

        return false;
    }

    private boolean isSourceTypeSelected(SearchElement item, SearchOptions searchOptions) {

        String srcType = searchOptions.getGenericStringOption(GenericSearchOption.SRCMBR_SRC_TYPE, "*"); //$NON-NLS-1$

        if (StringHelper.isNullOrEmpty(srcType)) {
            return true;
        }

        if ("*".equals(srcType)) {
            return true;
        }

        String itemSrcType = item.getType();
        if (StringHelper.isNullOrEmpty(itemSrcType)) {
            return true;
        }

        try {
            if (StringHelper.matchesGeneric(itemSrcType, srcType)) {
                return true;
            }
        } catch (Throwable e) {
            // Ignore pattern syntax errors
        }

        return false;
    }

}
