/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import biz.isphere.core.objectsynchronization.rse.MemberCompareItem;

/**
 * Class to filter the content of the table according to the selection settings
 * that can be changed with the buttons above the table.
 */
public class TableFilter extends ViewerFilter {

    private TableFilterData filterData;
    private TableStatistics tableStatistics;

    private CompareOptions compareOptions;

    public TableFilter(TableStatistics tableStatistics) {
        this.tableStatistics = tableStatistics;
        this.compareOptions = null;
    }

    public void setCompareOptions(CompareOptions compareOptions) {
        this.compareOptions = compareOptions;
        tableStatistics.setCompareOptions(compareOptions);
    }

    public void setFilterData(TableFilterData filterData) {
        this.filterData = filterData;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {

        if (filterData == null) {
            return true;
        }

        MemberCompareItem compareItem = (MemberCompareItem)element;

        tableStatistics.addElement(compareItem, filterData);

        if (compareItem.isSelected(filterData, compareOptions)) {
            return true;
        }

        return false;
    }
}
