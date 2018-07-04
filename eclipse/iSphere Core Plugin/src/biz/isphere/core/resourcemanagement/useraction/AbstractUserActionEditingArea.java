/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.useraction;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.core.Messages;
import biz.isphere.core.internal.Size;
import biz.isphere.core.resourcemanagement.AbstractEditingArea;
import biz.isphere.core.resourcemanagement.AbstractResource;

public abstract class AbstractUserActionEditingArea extends AbstractEditingArea {

    private boolean singleDomain;

    public AbstractUserActionEditingArea(Composite parent, AbstractResource[] resources, boolean both, boolean singleDomain) {
        super(parent, resources, both, new UserActionQualifier(singleDomain));
        this.singleDomain = singleDomain;
    }

    @Override
    protected boolean isActionEnabled(String action, AbstractResource[] selectedItems) {

        if (AbstractResource.PUSH_TO_WORKSPACE.equals(action)) {
            return true;
        } else if (AbstractResource.DELETE_FROM_WORKSPACE.equals(action)) {
            return allResourcesEditable(selectedItems);
        } else if (AbstractResource.DELETE_FROM_BOTH.equals(action)) {
            return allResourcesEditable(selectedItems);
        }

        return true;
    }

    @Override
    public void addTableColumns(Table tableResources) {

        UserActionQualifier qualifier = (UserActionQualifier)tableResources.getData(TABLE_RESOURCE_QUALIFIER);

        if (!qualifier.isSingleDomain()) {
            TableColumn columnName = new TableColumn(tableResources, SWT.NONE);
            columnName.setWidth(Size.getSize(100));
            columnName.setText(Messages.Domain);
        }

        TableColumn columnName = new TableColumn(tableResources, SWT.NONE);
        columnName.setWidth(Size.getSize(100));
        columnName.setText(Messages.Label);

        TableColumn columnType = new TableColumn(tableResources, SWT.NONE);
        columnType.setWidth(Size.getSize(600));
        columnType.setText(Messages.Command);

    }

    @Override
    public String getTableColumnText(Object resource, int columnIndex) {

        int counter = 0;
        if (!singleDomain) {
            counter++;
            if (columnIndex == 0) {
                return (((RSEUserAction)resource).getDomain().getName());
            }
        }

        if (columnIndex == 0 + counter) {
            return (((RSEUserAction)resource).getLabel());
        } else if (columnIndex == 1 + counter) {
            return (((RSEUserAction)resource).getCommandString());
        } else if (columnIndex == 2 + counter) {
            return "???";
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

        if (!singleDomain) {

            RSEDomain domain1 = ((RSEUserAction)resource1).getDomain();
            RSEDomain domain2 = ((RSEUserAction)resource2).getDomain();

            int result = domain1.compareTo(domain2);
            if (result != 0) {
                return result;
            }
        }

        return (((RSEUserAction)resource1).getLabel()).compareTo((((RSEUserAction)resource2).getLabel()));

    }

}
