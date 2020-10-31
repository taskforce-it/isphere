/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.filter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.core.Messages;
import biz.isphere.core.internal.Size;
import biz.isphere.core.resourcemanagement.AbstractResource;

public class FilterEditingAreaBothDifferent extends AbstractFilterEditingArea {

    private boolean singleFilterPool;

    public FilterEditingAreaBothDifferent(Composite parent, AbstractResource[] resources, boolean both, boolean singleFilterPool) {
        super(parent, resources, both, singleFilterPool);
        this.singleFilterPool = singleFilterPool;
    }

    @Override
    public void addTableColumns(Table tableResources) {

        FilterQualifier qualifier = (FilterQualifier)tableResources.getData(TABLE_RESOURCE_QUALIFIER);

        if (!qualifier.isSingleFilterPool()) {
            TableColumn columnName = new TableColumn(tableResources, SWT.NONE);
            columnName.setWidth(Size.getSize(150));
            columnName.setText(Messages.Pool);
        }

        TableColumn columnName = new TableColumn(tableResources, SWT.NONE);
        columnName.setWidth(Size.getSize(150));
        columnName.setText(Messages.Filter);

        TableColumn columnWorkspaceType = new TableColumn(tableResources, SWT.NONE);
        columnWorkspaceType.setWidth(Size.getSize(100));
        columnWorkspaceType.setText("Wrk.-" + Messages.Type);

        TableColumn columnRepositoryType = new TableColumn(tableResources, SWT.NONE);
        columnRepositoryType.setWidth(Size.getSize(100));
        columnRepositoryType.setText("Rep.-" + Messages.Type);

        TableColumn columnWorkspaceFilterStrings = new TableColumn(tableResources, SWT.NONE);
        columnWorkspaceFilterStrings.setWidth(Size.getSize(200));
        columnWorkspaceFilterStrings.setText("Wrk.-" + Messages.Strings);

        TableColumn columnRepositoryFilterStrings = new TableColumn(tableResources, SWT.NONE);
        columnRepositoryFilterStrings.setWidth(Size.getSize(200));
        columnRepositoryFilterStrings.setText("Rep.-" + Messages.Strings);

    }

    @Override
    public String getTableColumnText(Object resource, int columnIndex) {

        RSEFilterBoth filterBoth = (RSEFilterBoth)resource;

        int counter = 0;
        if (!singleFilterPool) {
            counter++;
            if (columnIndex == 0) {
                return ((RSEFilter)filterBoth.getResourceWorkspace()).getFilterPool().getName();
            }
        }

        if (columnIndex == 0 + counter) {
            return filterBoth.getName();
        } else if (columnIndex == 1 + counter) {
            return RSEFilter.getTypeText(((RSEFilter)filterBoth.getResourceWorkspace()).getType());
        } else if (columnIndex == 2 + counter) {
            return RSEFilter.getTypeText(((RSEFilter)filterBoth.getResourceRepository()).getType());
        } else if (columnIndex == 3 + counter) {
            return (((RSEFilter)filterBoth.getResourceWorkspace()).getDisplayFilterString());
        } else if (columnIndex == 4 + counter) {
            return (((RSEFilter)filterBoth.getResourceRepository()).getDisplayFilterString());
        } else {
            return "";
        }

    }

    @Override
    protected Image getTableColumnImage(Object resource, int columnIndex) {
        return null;
    }

    @Override
    public int compareResources(Object resource1, Object resource2) {

        RSEFilterBoth filterBoth1 = (RSEFilterBoth)resource1;
        RSEFilterBoth filterBoth2 = (RSEFilterBoth)resource2;

        if (!singleFilterPool) {

            RSEFilter filter1 = (RSEFilter)filterBoth1.getResourceWorkspace();
            RSEFilter filter2 = (RSEFilter)filterBoth2.getResourceWorkspace();

            int result = filter1.getFilterPool().getName().compareTo(filter2.getFilterPool().getName());
            if (result != 0) {
                return result;
            }

        }

        return filterBoth1.getName().compareTo(filterBoth2.getName());

    }

    @Override
    protected String[] getActions(boolean both) {
        return getActionsBothDifferent();
    }

    public String getTitle() {
        return Messages.Filters + " " + getTitleBothDifferent() + " " + Messages.type_or_string + " (" + getNumberOfItems() + ")";
    }

}
