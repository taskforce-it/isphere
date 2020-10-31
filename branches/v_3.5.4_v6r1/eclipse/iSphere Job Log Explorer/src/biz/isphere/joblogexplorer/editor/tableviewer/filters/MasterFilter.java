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

import biz.isphere.joblogexplorer.model.JobLogMessage;

public class MasterFilter extends ViewerFilter {

    List<IMessagePropertyFilter> filters;

    public MasterFilter() {
        this.filters = new ArrayList<IMessagePropertyFilter>();
    }

    public void addFilter(IMessagePropertyFilter filter) {
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

        if (element instanceof JobLogMessage) {

            JobLogMessage jobLogMessage = (JobLogMessage)element;
            if (jobLogMessage.isSelected()) {
                return true;
            }

            if (filters.isEmpty()) {
                return false;
            }

            for (IMessagePropertyFilter filter : filters) {
                if (!filter.xselect(tableViewer, parentElement, jobLogMessage)) {
                    return false;
                }
            }
        }

        return true;
    }
}
