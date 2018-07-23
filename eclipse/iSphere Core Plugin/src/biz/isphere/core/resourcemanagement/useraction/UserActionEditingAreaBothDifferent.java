/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.useraction;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.Size;
import biz.isphere.core.resourcemanagement.AbstractResource;

public class UserActionEditingAreaBothDifferent extends AbstractUserActionEditingArea {

    private boolean singleDomain;

    public UserActionEditingAreaBothDifferent(Composite parent, AbstractResource[] resources, boolean both, boolean singleDomain) {
        super(parent, resources, both, singleDomain);
        this.singleDomain = singleDomain;
    }

    @Override
    public void addTableColumns(Table tableResources) {

        UserActionQualifier qualifier = (UserActionQualifier)tableResources.getData(TABLE_RESOURCE_QUALIFIER);

        if (!qualifier.isSingleDomain()) {
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
    protected void addTablePopupMenu(Table table) {

        Menu menu = new Menu(table);
        menu.addMenuListener(new UserActionPopupMenu(menu, table) {

            @Override
            protected boolean isEnabled(MenuItem menuItem, Table table) {

                if (table.getSelectionCount() == 1) {
                    return true;
                }

                return false;
            }

            @Override
            protected void performAction(MenuItem menuItem, Table table) {
                performDisplayDifferences(table);
            }
        });
        table.setMenu(menu);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                Table table = (Table)e.getSource();
                performDisplayDifferences(table);
            }
        });
    }

    private void performDisplayDifferences(Table table) {

        TableItem[] tableItems = table.getSelection();
        RSEUserActionBoth data = (RSEUserActionBoth)tableItems[0].getData();

        UserActionDifferencesDialog dialog = new UserActionDifferencesDialog(getShell());
        dialog.setInput(data);
        dialog.open();
    }

    @Override
    public String getTableColumnText(Object resource, int columnIndex) {

        RSEUserActionBoth userActionBoth = (RSEUserActionBoth)resource;

        int counter = 0;
        if (!singleDomain) {
            counter++;
            if (columnIndex == 0) {
                return ((RSEUserAction)userActionBoth.getResourceWorkspace()).getDomain().getName();
            }
        }

        if (columnIndex == 0 + counter) {
            return userActionBoth.getLabel();
        } else if (columnIndex == 1 + counter) {
            return ((RSEUserAction)userActionBoth.getResourceWorkspace()).getCommandString();
        } else if (columnIndex == 2 + counter) {
            return ((RSEUserAction)userActionBoth.getResourceRepository()).getCommandString();
        } else {
            return "";
        }

    }

    @Override
    protected Image getTableColumnImage(Object resource, int columnIndex) {

        RSEUserActionBoth commandBoth = (RSEUserActionBoth)resource;

        int counter = 0;
        if (!singleDomain) {
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

        RSEUserActionBoth userActionBoth1 = (RSEUserActionBoth)resource1;
        RSEUserActionBoth userActionBoth2 = (RSEUserActionBoth)resource2;

        if (!singleDomain) {

            RSEUserAction userAction1 = (RSEUserAction)userActionBoth1.getResourceWorkspace();
            RSEUserAction userAction2 = (RSEUserAction)userActionBoth2.getResourceWorkspace();

            RSEDomain domain1 = userAction1.getDomain();
            RSEDomain domain2 = userAction2.getDomain();

            int result = domain1.compareTo(domain2);
            if (result != 0) {
                return result;
            }

        }

        return userActionBoth1.getLabel().compareTo(userActionBoth2.getLabel());

    }

    @Override
    protected String[] getActions(boolean both) {
        return getActionsBothDifferent();
    }

    public String getTitle() {
        return Messages.UserActions + " " + getTitleBothDifferent() + " " + Messages.userAction_parameters + " (" + getNumberOfItems() + ")";
    }

}
