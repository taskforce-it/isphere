/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view.menus;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import biz.isphere.core.Messages;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.WorkWithSpooledFilesHelper;
import biz.isphere.core.spooledfiles.view.events.ITableItemChangeListener;

public class WorkWithSpooledFilesMenuAdapter extends MenuAdapter implements IDoubleClickListener {

    private Menu parentMenu;
    private TableViewer tableViewer;
    private Table table;

    private WorkWithSpooledFilesHelper workWithSpooledFilesHelper;

    private MenuItem menuItemChange;
    private MenuItem menuItemDelete;
    private MenuItem menuItemHold;
    private MenuItem menuItemRelease;
    private MenuItem menuItemMessages;
    private MenuItem menuItemOpenAs;
    private MenuItem menuItemSaveAs;
    private MenuItem menuItemSeparator;
    private MenuItem menuItemProperties;

    public WorkWithSpooledFilesMenuAdapter(Menu parentMenu, String connectionName, TableViewer tableViewer) {
        this.parentMenu = parentMenu;
        this.tableViewer = tableViewer;
        this.table = tableViewer.getTable();

        this.workWithSpooledFilesHelper = new WorkWithSpooledFilesHelper(parentMenu.getShell(), connectionName);
    }

    public void addChangedListener(ITableItemChangeListener modifyListener) {
        workWithSpooledFilesHelper.addChangedListener(modifyListener);
    }

    public void removeChangedListener(ITableItemChangeListener modifyListener) {
        workWithSpooledFilesHelper.removeChangedListener(modifyListener);
    }

    @Override
    public void menuShown(MenuEvent event) {
        destroyMenuItems();
        createMenuItems();
    }

    public void destroyMenuItems() {
        dispose(menuItemChange);
        dispose(menuItemDelete);
        dispose(menuItemHold);
        dispose(menuItemRelease);
        dispose(menuItemMessages);
        dispose(menuItemOpenAs);
        dispose(menuItemSaveAs);
        dispose(menuItemSeparator);
        dispose(menuItemProperties);
    }

    private void dispose(MenuItem menuItem) {
        if (!((menuItem == null) || (menuItem.isDisposed()))) {
            menuItem.dispose();
        }
    }

    public void createMenuItems() {

        menuItemChange = new MenuItem(parentMenu, SWT.PUSH);
        menuItemChange.setText(Messages.Change);
        menuItemChange.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                workWithSpooledFilesHelper.performChangeSpooledFile(getSelectedItems());
            }
        });

        menuItemDelete = new MenuItem(parentMenu, SWT.PUSH);
        menuItemDelete.setText(Messages.Delete);
        menuItemDelete.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                workWithSpooledFilesHelper.performDeleteSpooledFile(getSelectedItems());
            }
        });

        menuItemHold = new MenuItem(parentMenu, SWT.PUSH);
        menuItemHold.setText(Messages.Hold);
        menuItemHold.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                workWithSpooledFilesHelper.performHoldSpooledFile(getSelectedItems());
            }
        });

        menuItemRelease = new MenuItem(parentMenu, SWT.PUSH);
        menuItemRelease.setText(Messages.Release);
        menuItemRelease.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                workWithSpooledFilesHelper.performReleaseSpooledFile(getSelectedItems());
            }
        });

        menuItemMessages = new MenuItem(parentMenu, SWT.PUSH);
        menuItemMessages.setText(Messages.Messages);
        menuItemMessages.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                workWithSpooledFilesHelper.performShowMessages(getSelectedItems());
            }
        });

        menuItemOpenAs = new MenuItem(parentMenu, SWT.CASCADE);
        menuItemOpenAs.setText(Messages.OpenAs);
        menuItemOpenAs.setMenu(createOpenAsSubMenu());

        menuItemSaveAs = new MenuItem(parentMenu, SWT.CASCADE);
        menuItemSaveAs.setText(Messages.SaveAs);
        menuItemSaveAs.setMenu(createSaveAsSubMenu());

        if (table.getSelectionCount() == 1) {

            menuItemSeparator = new MenuItem(parentMenu, SWT.SEPARATOR);

            menuItemProperties = new MenuItem(parentMenu, SWT.PUSH);
            menuItemProperties.setText(Messages.Properties);
            menuItemProperties.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    workWithSpooledFilesHelper.performDisplaySpooledFileProperties(getSelectedItems());
                }
            });
        }
    }

    private Menu createOpenAsSubMenu() {

        Menu subMenuOpenAs = new Menu(getShell(), SWT.DROP_DOWN);

        MenuItem menuItemOpenAsText = new MenuItem(subMenuOpenAs, SWT.PUSH);
        menuItemOpenAsText.setText(Messages.OpenAsText);
        menuItemOpenAsText.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                workWithSpooledFilesHelper.performOpenAsText(getSelectedItems());
            }
        });

        MenuItem menuItemOpenAsHtml = new MenuItem(subMenuOpenAs, SWT.PUSH);
        menuItemOpenAsHtml.setText(Messages.OpenAsHTML);
        menuItemOpenAsHtml.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                workWithSpooledFilesHelper.performOpenAsHtml(getSelectedItems());
            }
        });

        MenuItem menuItemOpenAsPdf = new MenuItem(subMenuOpenAs, SWT.PUSH);
        menuItemOpenAsPdf.setText(Messages.OpenAsPDF);
        menuItemOpenAsPdf.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                workWithSpooledFilesHelper.performOpenAsPdf(getSelectedItems());
            }
        });

        return subMenuOpenAs;
    }

    private Menu createSaveAsSubMenu() {

        Menu subMenuSaveAs = new Menu(getShell(), SWT.DROP_DOWN);

        MenuItem menuItemSaveAsText = new MenuItem(subMenuSaveAs, SWT.PUSH);
        menuItemSaveAsText.setText(Messages.SaveAsText);
        menuItemSaveAsText.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                workWithSpooledFilesHelper.performSaveAsText(getSelectedItems());
            }
        });

        MenuItem menuItemSaveAsHtml = new MenuItem(subMenuSaveAs, SWT.PUSH);
        menuItemSaveAsHtml.setText(Messages.SaveAsHTML);
        menuItemSaveAsHtml.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                workWithSpooledFilesHelper.performSaveAsHtml(getSelectedItems());
            }
        });

        MenuItem menuItemSaveAsPdf = new MenuItem(subMenuSaveAs, SWT.PUSH);
        menuItemSaveAsPdf.setText(Messages.SaveAsPDF);
        menuItemSaveAsPdf.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                workWithSpooledFilesHelper.performSaveAsPdf(getSelectedItems());
            }
        });

        return subMenuSaveAs;
    }

    private Shell getShell() {
        return parentMenu.getShell();
    }

    private SpooledFile[] getSelectedItems() {

        SpooledFile[] spooledFiles = new SpooledFile[tableViewer.getTable().getSelectionCount()];
        TableItem[] tableItems = table.getSelection();
        for (int i = 0; i < tableItems.length; i++) {
            spooledFiles[i] = (SpooledFile)tableItems[i].getData();
        }

        return spooledFiles;
    }

    public void doubleClick(DoubleClickEvent e) {
        int index = table.getSelectionIndex();
        if (index != -1) {
            TableItem tableItem = table.getItem(index);
            if (tableItem.getData() instanceof SpooledFile) {
                SpooledFile spooledFile = (SpooledFile)tableItem.getData();
                workWithSpooledFilesHelper.performOpen(new SpooledFile[] { spooledFile });
            }
        }
    }
}
