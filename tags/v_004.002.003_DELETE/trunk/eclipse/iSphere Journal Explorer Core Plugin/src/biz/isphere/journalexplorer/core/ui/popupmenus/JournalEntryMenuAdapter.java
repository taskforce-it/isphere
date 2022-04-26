/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.popupmenus;

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
import org.eclipse.wb.swt.ResourceManager;

import biz.isphere.journalexplorer.core.ui.actions.CompareSideBySideAction;
import biz.isphere.journalexplorer.core.ui.actions.ExportToExcelAction;
import biz.isphere.journalexplorer.core.ui.labelproviders.JournalEntryLabelProvider;
import biz.isphere.journalexplorer.core.ui.widgets.actions.CopyJournalPropertyToClipboardAction;
import biz.isphere.journalexplorer.core.ui.widgets.actions.ToggleTrimPropertyValuesAction;

public class JournalEntryMenuAdapter extends MenuAdapter {

    private TableViewer tableViewer;
    private Menu menuTableMembers;
    private Shell shell;
    private MenuItem compareSideBySideMenuItem;
    private MenuItem exportToExcelMenuItem;
    private MenuItem separator;
    private MenuItem copyAllToClipboardMenuItem;
    private MenuItem copyValuesToClipboardMenuItem;
    private MenuItem toggleTrimPropertyValuesMenuItem;

    public JournalEntryMenuAdapter(Menu menuTableMembers, TableViewer tableViewer) {
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
        dispose(separator);
        dispose(copyAllToClipboardMenuItem);
        dispose(copyValuesToClipboardMenuItem);
        dispose(toggleTrimPropertyValuesMenuItem);
    }

    private int selectedItemsCount() {
        return getSelection().size();
    }

    private StructuredSelection getSelection() {

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

        if (selectedItemsCount() == 1) {
            copyAllToClipboardMenuItem = new MenuItem(menuTableMembers, SWT.NONE);
            final CopyJournalPropertyToClipboardAction copyAllToClipboardAction = new CopyJournalPropertyToClipboardAction(true);
            copyAllToClipboardMenuItem.setText(copyAllToClipboardAction.getText());
            copyAllToClipboardMenuItem.setImage(ResourceManager.getImage(copyAllToClipboardAction.getImageDescriptor()));
            copyAllToClipboardMenuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    copyAllToClipboardAction.setSelectedItems(getSelection());
                    copyAllToClipboardAction.run();
                }
            });

            copyValuesToClipboardMenuItem = new MenuItem(menuTableMembers, SWT.NONE);
            final CopyJournalPropertyToClipboardAction copyValueToClipboardAction = new CopyJournalPropertyToClipboardAction(false);
            copyValuesToClipboardMenuItem.setText(copyValueToClipboardAction.getText());
            copyValuesToClipboardMenuItem.setImage(ResourceManager.getImage(copyValueToClipboardAction.getImageDescriptor()));
            copyValuesToClipboardMenuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    copyValueToClipboardAction.setSelectedItems(getSelection());
                    copyValueToClipboardAction.run();
                }
            });

            toggleTrimPropertyValuesMenuItem = new MenuItem(menuTableMembers, SWT.CHECK);
            final ToggleTrimPropertyValuesAction toggleTrimPropertyValuesAction = new ToggleTrimPropertyValuesAction();
            toggleTrimPropertyValuesMenuItem.setText(toggleTrimPropertyValuesAction.getText());
            toggleTrimPropertyValuesMenuItem.setImage(ResourceManager.getImage(toggleTrimPropertyValuesAction.getImageDescriptor()));
            toggleTrimPropertyValuesMenuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    toggleTrimPropertyValuesAction.setChecked(!toggleTrimPropertyValuesAction.isChecked());
                    toggleTrimPropertyValuesAction.run();
                }
            });
        }

        if (selectedItemsCount() > 0) {
            separator = new MenuItem(menuTableMembers, SWT.SEPARATOR);

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
