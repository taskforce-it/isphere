/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilesearch;

import java.io.Serializable;
import java.util.LinkedList;

@SuppressWarnings("serial")
public class SearchResultTabFolder implements Serializable {

    private LinkedList<SearchResultTab> tabFolder;

    public SearchResultTabFolder() {
        this.tabFolder = new LinkedList<SearchResultTab>();
    }

    public void addTab(SearchResultTab searchResultTab) {
        if (tabFolder.add(searchResultTab)) {
        }
    }

    public void removeTab(SearchResultTab searchResultTab) {
        if (tabFolder.remove(searchResultTab)) {
        }
    }

    public int getNumTabs() {
        return tabFolder.size();
    }

    public SearchResultTab[] getTabs() {

        if (tabFolder == null || tabFolder.size() <= 0) {
            return new SearchResultTab[0];
        }

        return tabFolder.toArray(new SearchResultTab[tabFolder.size()]);
    }
}
