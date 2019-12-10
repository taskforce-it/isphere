/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.joblogexplorer.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import biz.isphere.joblogexplorer.action.ExportToExcelAction;

public class JobLogMessageMenuAdapter extends MenuAdapter implements ISelectionChangedListener {

    private Menu menuTableMembers;
    private JobLogExplorerTab tabItem;
    private ISelection selection;
    private MenuItem exportToExcelMenuItem;

    public JobLogMessageMenuAdapter(Menu menuTableMembers, JobLogExplorerTab tabItem) {
        this.menuTableMembers = menuTableMembers;
        this.tabItem = tabItem;
    }

    @Override
    public void menuShown(MenuEvent event) {
        destroyMenuItems();
        createMenuItems();
    }

    public void destroyMenuItems() {
        dispose(exportToExcelMenuItem);
    }

    private int selectedItemsCount() {
        return getSelection().size();
    }

    private StructuredSelection getSelection() {

        if (selection instanceof StructuredSelection) {
            return (StructuredSelection)selection;
        }

        return new StructuredSelection(new Object[0]);
    }

    private void dispose(MenuItem menuItem) {

        if (!((menuItem == null) || (menuItem.isDisposed()))) {
            menuItem.dispose();
        }
    }

    public void createMenuItems() {

        if (selectedItemsCount() > 0) {
            exportToExcelMenuItem = new MenuItem(menuTableMembers, SWT.NONE);
            final ExportToExcelAction exportToExcelAction = new ExportToExcelAction();
            exportToExcelMenuItem.setText(exportToExcelAction.getText());
            exportToExcelMenuItem.setImage(exportToExcelAction.getImage());
            exportToExcelMenuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    exportToExcelAction.setTabTitle(tabItem.getText());
                    exportToExcelAction.setSelectedItems(getSelection());
                    exportToExcelAction.run();
                }
            });
        }
    }

    public void selectionChanged(SelectionChangedEvent event) {
        selection = event.getSelection();
    }
}
