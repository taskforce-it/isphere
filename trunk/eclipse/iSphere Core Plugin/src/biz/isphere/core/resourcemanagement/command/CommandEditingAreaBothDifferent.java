/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.command;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.Size;
import biz.isphere.core.resourcemanagement.AbstractEditingArea;
import biz.isphere.core.resourcemanagement.AbstractResource;

public class CommandEditingAreaBothDifferent extends AbstractEditingArea {

    private boolean singleCompileType;

    public CommandEditingAreaBothDifferent(Composite parent, AbstractResource[] resources, boolean both, boolean singleCompileType) {
        super(parent, resources, both, new CommandQualifier(singleCompileType));
        this.singleCompileType = singleCompileType;
    }

    @Override
    public void addTableColumns(Table tableResources) {

        CommandQualifier qualifier = (CommandQualifier)tableResources.getData("Qualifier");

        if (!qualifier.isSingleCompileType()) {
            TableColumn columnName = new TableColumn(tableResources, SWT.NONE);
            columnName.setWidth(Size.getSize(100));
            columnName.setText(Messages.Compile_type);
        }

        TableColumn columnName = new TableColumn(tableResources, SWT.NONE);
        columnName.setWidth(Size.getSize(100));
        columnName.setText(Messages.Label);

        TableColumn columnWorkspaceType = new TableColumn(tableResources, SWT.NONE);
        columnWorkspaceType.setWidth(Size.getSize(300));
        columnWorkspaceType.setText("Wrk.-" + Messages.Command);

        TableColumn columnRepositoryType = new TableColumn(tableResources, SWT.NONE);
        columnRepositoryType.setWidth(Size.getSize(300));
        columnRepositoryType.setText("Rep.-" + Messages.Command);

    }

    @Override
    public String getTableColumnText(Object resource, int columnIndex) {

        RSECommandBoth commandBoth = (RSECommandBoth)resource;

        int counter = 0;
        if (!singleCompileType) {
            counter++;
            if (columnIndex == 0) {
                return ((RSECommand)commandBoth.getResourceWorkspace()).getCompileType().getType();
            }
        }

        if (columnIndex == 0 + counter) {
            return commandBoth.getLabel();
        } else if (columnIndex == 1 + counter) {
            return ((RSECommand)commandBoth.getResourceWorkspace()).getCommandString();
        } else if (columnIndex == 2 + counter) {
            return ((RSECommand)commandBoth.getResourceRepository()).getCommandString();
        } else {
            return "";
        }

    }

    @Override
    protected Image getTableColumnImage(Object resource, int columnIndex) {

        RSECommandBoth commandBoth = (RSECommandBoth)resource;

        int counter = 0;
        if (!singleCompileType) {
            counter++;
        }

        if (columnIndex == 0 + counter) {
            if (commandBoth.isEditable()) {
                return ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_UNLOCKED);
            } else {
                return ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_LOCKED);
            }
        } else {
            return null;
        }
    }

    @Override
    public int compareResources(Object resource1, Object resource2) {

        RSECommandBoth commandBoth1 = (RSECommandBoth)resource1;
        RSECommandBoth commandBoth2 = (RSECommandBoth)resource2;

        if (!singleCompileType) {

            RSECommand command1 = (RSECommand)commandBoth1.getResourceWorkspace();
            RSECommand command2 = (RSECommand)commandBoth2.getResourceWorkspace();

            int result = command1.getCompileType().getType().compareTo(command2.getCompileType().getType());
            if (result != 0) {
                return result;
            }

        }

        return commandBoth1.getLabel().compareTo(commandBoth2.getLabel());

    }

    @Override
    protected String[] getActions(boolean both) {
        return getActionsBothDifferent();
    }

    public String getTitle() {
        return Messages.Commands + " " + getTitleBothDifferent() + " " + Messages.command_parameters + " (" + getNumberOfItems() + ")";
    }

}
