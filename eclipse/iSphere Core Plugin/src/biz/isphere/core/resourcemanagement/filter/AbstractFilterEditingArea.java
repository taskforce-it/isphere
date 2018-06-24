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
import biz.isphere.core.resourcemanagement.AbstractEditingArea;
import biz.isphere.core.resourcemanagement.AbstractResource;

public abstract class AbstractFilterEditingArea extends AbstractEditingArea {

    private boolean singleFilterPool;

    public AbstractFilterEditingArea(Composite parent, AbstractResource[] resources, boolean both, boolean singleFilterPool) {
        super(parent, resources, both, new FilterQualifier(singleFilterPool));
        this.singleFilterPool = singleFilterPool;
    }

    @Override
    public void addTableColumns(Table tableResources) {

        FilterQualifier qualifier = (FilterQualifier)tableResources.getData("Qualifier");

        if (!qualifier.isSingleFilterPool()) {
            TableColumn columnName = new TableColumn(tableResources, SWT.NONE);
            columnName.setWidth(Size.getSize(150));
            columnName.setText(Messages.Pool);
        }

        TableColumn columnName = new TableColumn(tableResources, SWT.NONE);
        columnName.setWidth(Size.getSize(150));
        columnName.setText(Messages.Filter);

        TableColumn columnType = new TableColumn(tableResources, SWT.NONE);
        columnType.setWidth(Size.getSize(100));
        columnType.setText(Messages.Type);

        TableColumn columnFilterStrings = new TableColumn(tableResources, SWT.NONE);
        columnFilterStrings.setWidth(Size.getSize(400));
        columnFilterStrings.setText(Messages.Strings);

    }

    @Override
    public String getTableColumnText(Object resource, int columnIndex) {

        int counter = 0;
        if (!singleFilterPool) {
            counter++;
            if (columnIndex == 0) {
                return (((RSEFilter)resource).getFilterPool().getName());
            }
        }

        if (columnIndex == 0 + counter) {
            return (((RSEFilter)resource).getName());
        } else if (columnIndex == 1 + counter) {
            return RSEFilter.getTypeText((((RSEFilter)resource).getType()));
        } else if (columnIndex == 2 + counter) {
            return (((RSEFilter)resource).getDisplayFilterString());
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

        if (!singleFilterPool) {
            int result = (((RSEFilter)resource1).getFilterPool().getName()).compareTo((((RSEFilter)resource2).getFilterPool().getName()));
            if (result != 0) {
                return result;
            }
        }

        return (((RSEFilter)resource1).getName()).compareTo((((RSEFilter)resource2).getName()));

    }

}
