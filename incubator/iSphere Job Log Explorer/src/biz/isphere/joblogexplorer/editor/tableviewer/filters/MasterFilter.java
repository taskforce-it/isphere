/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor.tableviewer.filters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class MasterFilter extends ViewerFilter {

    List<ViewerFilter> filters;

    public MasterFilter() {
        this.filters = new ArrayList<ViewerFilter>();
    }

    public void addFilter(ViewerFilter filter) {
        filters.add(filter);
    }

    public void removeAllFilters() {
        filters.clear();
    }

    public int countFilters() {
        return filters.size();
    }

    @Override
    public boolean select(Viewer tableViewer, Object parentElement, Object element) {

        for (ViewerFilter filter : filters) {
            if (!filter.select(tableViewer, parentElement, element)) {
                return false;
            }
        }

        return true;
    }
}
