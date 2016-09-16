/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilecompare;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import biz.isphere.core.messagefilecompare.rse.MessageFileCompareItem;

/**
 * Class to filter the content of the table according to the selection settings
 * that can be changed with the buttons above the table.
 */
public class TableFilter extends ViewerFilter {

    private TableFilterData filterData;
    private TableStatistics tableStatistics;

    public TableFilter(TableStatistics tableStatistics) {
        this.tableStatistics = tableStatistics;
    }

    public void setFilterData(TableFilterData filterData) {
        this.filterData = filterData;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {

        if (filterData == null) {
            return true;
        }

        MessageFileCompareItem compareItem = (MessageFileCompareItem)element;

        tableStatistics.addElement(compareItem, filterData);

        if (compareItem.isSelected(filterData)) {
            return true;
        }

        return false;
    }
}
