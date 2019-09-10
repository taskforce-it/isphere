/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.popupmenus;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;
import biz.isphere.journalexplorer.core.ui.actions.CompareSideBySideAction;
import biz.isphere.journalexplorer.core.ui.actions.ExportToExcelAction;
import biz.isphere.journalexplorer.core.ui.labelproviders.JournalEntryLabelProvider;

public class JournalEntryMenuAdapter extends MenuAdapter {

    private Tree tree;
    private TableViewer tableViewer;
    private Menu menuTableMembers;
    private Shell shell;
    private MenuItem compareSideBySideMenuItem;
    private MenuItem exportToExcelMenuItem;

    public JournalEntryMenuAdapter(Menu menuTableMembers, TableViewer tableViewer) {
        this.tree = null;
        this.tableViewer = tableViewer;
        this.shell = tableViewer.getControl().getShell();
        this.menuTableMembers = menuTableMembers;
    }

    @Override
    public void menuShown(MenuEvent event) {
        destroyMenuItems();
        createMenuItems();
    }

    public void destroyMenuItems() {
        dispose(exportToExcelMenuItem);
        dispose(compareSideBySideMenuItem);
    }

    private int selectedItemsCount() {
        return getSelection().size();
    }

    private StructuredSelection getSelection() {

        if (tree != null) {
            List<JournalProperties> journalProperties = new LinkedList<JournalProperties>();
            for (TreeItem treeItem : tree.getSelection()) {
                Object data = treeItem.getData();
                if (data instanceof JournalProperties) {
                    journalProperties.add((JournalProperties)treeItem.getData());
                }
            }
            return new StructuredSelection(journalProperties.toArray(new JournalProperties[journalProperties.size()]));
        }

        ISelection selection = tableViewer.getSelection();
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
            final ExportToExcelAction exportToExcelAction = new ExportToExcelAction(shell);
            exportToExcelMenuItem.setText(exportToExcelAction.getText());
            exportToExcelMenuItem.setImage(exportToExcelAction.getImage());
            exportToExcelMenuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    JournalEntryLabelProvider labelProvider = (JournalEntryLabelProvider)tableViewer.getLabelProvider();
                    exportToExcelAction.setSelectedItems(getSelection());
                    exportToExcelAction.setColumns(labelProvider.getColumns());
                    exportToExcelAction.run();
                }
            });
        }

        if (selectedItemsCount() == 2) {
            compareSideBySideMenuItem = new MenuItem(menuTableMembers, SWT.NONE);
            final CompareSideBySideAction compareSideBySideAction = new CompareSideBySideAction(shell);
            compareSideBySideMenuItem.setText(compareSideBySideAction.getText());
            compareSideBySideMenuItem.setImage(compareSideBySideAction.getImage());
            compareSideBySideMenuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    compareSideBySideAction.setSelectedItems(getSelection());
                    compareSideBySideAction.run();
                }
            });
        }
    }
}
