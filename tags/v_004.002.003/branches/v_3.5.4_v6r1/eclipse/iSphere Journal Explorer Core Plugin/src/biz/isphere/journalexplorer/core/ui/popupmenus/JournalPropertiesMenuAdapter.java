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

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
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

import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;
import biz.isphere.journalexplorer.core.ui.actions.CompareJournalPropertiesAction;
import biz.isphere.journalexplorer.core.ui.actions.CompareSideBySideAction;

public class JournalPropertiesMenuAdapter extends MenuAdapter {

    private TreeViewer treeViewer;
    private Menu menuTableMembers;
    private Shell shell;
    private MenuItem compareJournalPropertiesMenuItem;
    private MenuItem compareSideBySideMenuItem;

    public JournalPropertiesMenuAdapter(Menu menuTableMembers, TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
        this.shell = treeViewer.getControl().getShell();
        this.menuTableMembers = menuTableMembers;
    }

    @Override
    public void menuShown(MenuEvent event) {
        destroyMenuItems();
        createMenuItems();
    }

    public void destroyMenuItems() {
        dispose(compareJournalPropertiesMenuItem);
        dispose(compareSideBySideMenuItem);
    }

    private int selectedItemsCount() {
        return getSelectedJournalEntries().size();
    }

    private StructuredSelection getSelectedJournalProperties() {

        List<JournalProperties> journalProperties = new LinkedList<JournalProperties>();
        if (treeViewer != null && (treeViewer.getControl() instanceof Tree)) {
            Tree tree = (Tree)treeViewer.getControl();
            for (TreeItem treeItem : tree.getSelection()) {
                Object data = treeItem.getData();
                if (data instanceof JournalProperties) {
                    journalProperties.add((JournalProperties)data);
                }
            }
        }

        return new StructuredSelection(journalProperties.toArray(new JournalProperties[journalProperties.size()]));
    }

    private StructuredSelection getSelectedJournalEntries() {

        if (treeViewer != null && (treeViewer.getControl() instanceof Tree)) {
            Tree tree = (Tree)treeViewer.getControl();
            List<JournalEntry> journalEntries = new LinkedList<JournalEntry>();
            for (TreeItem treeItem : tree.getSelection()) {
                Object data = treeItem.getData();
                if (data instanceof JournalProperties) {
                    JournalProperties journalProperties = (JournalProperties)treeItem.getData();
                    journalEntries.add(journalProperties.getJournalEntry());
                }
            }

            return new StructuredSelection(journalEntries.toArray(new JournalEntry[journalEntries.size()]));
        }

        return new StructuredSelection(new Object[0]);
    }

    private void dispose(MenuItem menuItem) {

        if (!((menuItem == null) || (menuItem.isDisposed()))) {
            menuItem.dispose();
        }
    }

    public void createMenuItems() {

        if (selectedItemsCount() == 2) {
            compareJournalPropertiesMenuItem = new MenuItem(menuTableMembers, SWT.NONE);
            final CompareJournalPropertiesAction compareJournalPropertiesAction = new CompareJournalPropertiesAction(treeViewer);
            compareJournalPropertiesMenuItem.setText(compareJournalPropertiesAction.getText());
            compareJournalPropertiesMenuItem.setImage(compareJournalPropertiesAction.getImage());
            compareJournalPropertiesMenuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    compareJournalPropertiesAction.setSelectedItems(getSelectedJournalProperties());
                    compareJournalPropertiesAction.run();
                }
            });

            compareSideBySideMenuItem = new MenuItem(menuTableMembers, SWT.NONE);
            final CompareSideBySideAction compareSideBySideAction = new CompareSideBySideAction(shell);
            compareSideBySideMenuItem.setText(compareSideBySideAction.getText());
            compareSideBySideMenuItem.setImage(compareSideBySideAction.getImage());
            compareSideBySideMenuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    compareSideBySideAction.setSelectedItems(getSelectedJournalEntries());
                    compareSideBySideAction.run();
                }
            });
        }
    }
}
