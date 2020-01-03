/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view.menus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.clcommands.ICLPrompter;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.preferencepages.IPreferences;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.spooledfiles.ConfirmDeletionSpooledFiles;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.view.events.ITableItemChangeListener;
import biz.isphere.core.spooledfiles.view.events.TableItemChangedEvent;
import biz.isphere.core.spooledfiles.view.events.TableItemChangedEvent.EventType;

import com.ibm.as400.ui.util.CommandPrompter;

public class WorkWithSpooledFilesMenuAdapter extends MenuAdapter implements IDoubleClickListener {

    private Menu parentMenu;
    private String connectionName;
    private TableViewer tableViewer;
    private Table table;

    private List<ITableItemChangeListener> changedListeners;

    private MenuItem menuItemChange;
    private MenuItem menuItemDelete;
    private MenuItem menuItemHold;
    private MenuItem menuItemRelease;
    private MenuItem menuItemMessages;
    private MenuItem menuItemOpenAs;
    private MenuItem menuItemSaveAs;

    public WorkWithSpooledFilesMenuAdapter(Menu parentMenu, String connectionName, TableViewer tableViewer) {
        this.parentMenu = parentMenu;
        this.connectionName = connectionName;
        this.tableViewer = tableViewer;
        this.table = tableViewer.getTable();
        this.changedListeners = new LinkedList<ITableItemChangeListener>();
    }

    public void addChangedListener(ITableItemChangeListener modifyListener) {
        changedListeners.add(modifyListener);
    }

    public void removeChangedListener(ITableItemChangeListener modifyListener) {
        changedListeners.remove(modifyListener);
    }

    public void notifyChangedListener(TableItemChangedEvent event) {
        for (ITableItemChangeListener listener : changedListeners) {
            listener.itemChanged(event);
        }
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
                performChangeSpooledFile(e);
            }
        });

        menuItemDelete = new MenuItem(parentMenu, SWT.PUSH);
        menuItemDelete.setText(Messages.Delete);
        menuItemDelete.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performDeleteSpooledFile(e);
            }
        });

        menuItemHold = new MenuItem(parentMenu, SWT.PUSH);
        menuItemHold.setText(Messages.Hold);
        menuItemHold.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performHoldSpooledFile(e);
            }
        });

        menuItemRelease = new MenuItem(parentMenu, SWT.PUSH);
        menuItemRelease.setText(Messages.Release);
        menuItemRelease.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performReleaseSpooledFile(e);
            }
        });

        menuItemMessages = new MenuItem(parentMenu, SWT.PUSH);
        menuItemMessages.setText(Messages.Messages);
        menuItemMessages.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performShowMessages(e);
            }
        });

        menuItemOpenAs = new MenuItem(parentMenu, SWT.CASCADE);
        menuItemOpenAs.setText(Messages.OpenAs);
        menuItemOpenAs.setMenu(createOpenAsSubMenu());

        menuItemSaveAs = new MenuItem(parentMenu, SWT.CASCADE);
        menuItemSaveAs.setText(Messages.SaveAs);
        menuItemSaveAs.setMenu(createSaveAsSubMenu());
    }

    private Menu createOpenAsSubMenu() {

        Menu subMenuOpenAs = new Menu(getShell(), SWT.DROP_DOWN);

        MenuItem menuItemOpenAsText = new MenuItem(subMenuOpenAs, SWT.PUSH);
        menuItemOpenAsText.setText(Messages.OpenAsText);
        menuItemOpenAsText.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performOpenAsText(e);
            }
        });

        MenuItem menuItemOpenAsHtml = new MenuItem(subMenuOpenAs, SWT.PUSH);
        menuItemOpenAsHtml.setText(Messages.OpenAsHTML);
        menuItemOpenAsHtml.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performOpenAsHtml(e);
            }
        });

        MenuItem menuItemOpenAsPdf = new MenuItem(subMenuOpenAs, SWT.PUSH);
        menuItemOpenAsPdf.setText(Messages.OpenAsPDF);
        menuItemOpenAsPdf.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performOpenAsPdf(e);
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
                performSaveAsText(e);
            }
        });

        MenuItem menuItemSaveAsHtml = new MenuItem(subMenuSaveAs, SWT.PUSH);
        menuItemSaveAsHtml.setText(Messages.SaveAsHTML);
        menuItemSaveAsHtml.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performSaveAsHtml(e);
            }
        });

        MenuItem menuItemSaveAsPdf = new MenuItem(subMenuSaveAs, SWT.PUSH);
        menuItemSaveAsPdf.setText(Messages.SaveAsPDF);
        menuItemSaveAsPdf.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performSaveAsPdf(e);
            }
        });

        return subMenuSaveAs;
    }

    private void performChangeSpooledFile(SelectionEvent event) {

        TableItem[] tableItems = table.getSelection();
        for (TableItem tableItem : tableItems) {
            if (tableItem.getData() instanceof SpooledFile) {
                SpooledFile spooledFile = (SpooledFile)tableItem.getData();

                try {

                    ICLPrompter command = IBMiHostContributionsHandler.getCLPrompter(connectionName);
                    command.setCommandString(spooledFile.getCommandChangeAttribute());
                    command.setParent(Display.getCurrent().getActiveShell());
                    if (command.showDialog() == CommandPrompter.OK) {

                        String message = spooledFile.changeAttribute(command.getCommandString());
                        if (handleErrorMessage(message)) {
                            break;
                        } else {
                            notifyChangedListener(new TableItemChangedEvent(spooledFile, EventType.CHANGED));
                        }

                    }
                } catch (Exception e) {
                    if (handleException(e)) {
                        break;
                    }
                }

            }
        }
    }

    private void performDeleteSpooledFile(SelectionEvent event) {

        if (!isConfirmed(table.getSelection())) {
            return;
        }

        int[] selectionIndices = table.getSelectionIndices();
        for (int index : selectionIndices) {
            TableItem tableItem = table.getItem(index);
            if (tableItem.getData() instanceof SpooledFile) {
                SpooledFile spooledFile = (SpooledFile)tableItem.getData();
                spooledFile.delete();
                notifyChangedListener(new TableItemChangedEvent(spooledFile, EventType.DELETED, index));
            }
        }
    }

    private boolean isConfirmed(TableItem[] items) {

        List<SpooledFile> spooledFiles = new ArrayList<SpooledFile>();
        for (TableItem item : items) {
            SpooledFile spooledFile = (SpooledFile)item.getData();
            spooledFiles.add(spooledFile);
        }

        ConfirmDeletionSpooledFiles dialog = new ConfirmDeletionSpooledFiles(getShell(), spooledFiles.toArray(new SpooledFile[spooledFiles.size()]));
        if (dialog.open() == Dialog.OK) {
            return true;
        }

        return false;
    }

    private void performHoldSpooledFile(SelectionEvent event) {

        TableItem[] tableItems = table.getSelection();
        for (TableItem tableItem : tableItems) {
            if (tableItem.getData() instanceof SpooledFile) {
                SpooledFile spooledFile = (SpooledFile)tableItem.getData();
                if (handleErrorMessage(spooledFile.hold())) {
                    break;
                }
                notifyChangedListener(new TableItemChangedEvent(spooledFile, EventType.HOLD));
            }
        }
    }

    private void performReleaseSpooledFile(SelectionEvent event) {

        TableItem[] tableItems = table.getSelection();
        for (TableItem tableItem : tableItems) {
            if (tableItem.getData() instanceof SpooledFile) {
                SpooledFile spooledFile = (SpooledFile)tableItem.getData();
                if (handleErrorMessage(spooledFile.release())) {
                    break;
                }
                notifyChangedListener(new TableItemChangedEvent(spooledFile, EventType.RELEASED));
            }
        }
    }

    private void performShowMessages(SelectionEvent event) {

        TableItem[] tableItems = table.getSelection();
        for (TableItem tableItem : tableItems) {
            if (tableItem.getData() instanceof SpooledFile) {
                SpooledFile spooledFile = (SpooledFile)tableItem.getData();
                if (handleErrorMessage(spooledFile.replyMessage())) {
                    break;
                }
                notifyChangedListener(new TableItemChangedEvent(spooledFile, EventType.MESSAGE));
            }
        }
    }

    private void performOpenAsText(SelectionEvent e) {

        TableItem[] tableItems = table.getSelection();
        for (TableItem tableItem : tableItems) {
            if (tableItem.getData() instanceof SpooledFile) {
                SpooledFile spooledFile = (SpooledFile)tableItem.getData();
                if (handleErrorMessage(spooledFile.open(IPreferences.OUTPUT_FORMAT_TEXT))) {
                    break;
                }
            }
        }
    }

    private void performOpenAsHtml(SelectionEvent e) {

        TableItem[] tableItems = table.getSelection();
        for (TableItem tableItem : tableItems) {
            if (tableItem.getData() instanceof SpooledFile) {
                SpooledFile spooledFile = (SpooledFile)tableItem.getData();
                if (handleErrorMessage(spooledFile.open(IPreferences.OUTPUT_FORMAT_HTML))) {
                    break;
                }
            }
        }
    }

    private void performOpenAsPdf(SelectionEvent e) {

        TableItem[] tableItems = table.getSelection();
        for (TableItem tableItem : tableItems) {
            if (tableItem.getData() instanceof SpooledFile) {
                SpooledFile spooledFile = (SpooledFile)tableItem.getData();
                if (handleErrorMessage(spooledFile.open(IPreferences.OUTPUT_FORMAT_PDF))) {
                    break;
                }
            }
        }
    }

    private void performSaveAsText(SelectionEvent e) {

        TableItem[] tableItems = table.getSelection();
        for (TableItem tableItem : tableItems) {
            if (tableItem.getData() instanceof SpooledFile) {
                SpooledFile spooledFile = (SpooledFile)tableItem.getData();
                if (handleErrorMessage(spooledFile.save(getShell(), IPreferences.OUTPUT_FORMAT_TEXT))) {
                    break;
                }
            }
        }
    }

    private void performSaveAsHtml(SelectionEvent e) {

        TableItem[] tableItems = table.getSelection();
        for (TableItem tableItem : tableItems) {
            if (tableItem.getData() instanceof SpooledFile) {
                SpooledFile spooledFile = (SpooledFile)tableItem.getData();
                if (handleErrorMessage(spooledFile.save(getShell(), IPreferences.OUTPUT_FORMAT_HTML))) {
                    break;
                }
            }
        }
    }

    private void performSaveAsPdf(SelectionEvent e) {

        TableItem[] tableItems = table.getSelection();
        for (TableItem tableItem : tableItems) {
            if (tableItem.getData() instanceof SpooledFile) {
                SpooledFile spooledFile = (SpooledFile)tableItem.getData();
                if (handleErrorMessage(spooledFile.save(getShell(), IPreferences.OUTPUT_FORMAT_PDF))) {
                    break;
                }
            }
        }
    }

    private Shell getShell() {
        return parentMenu.getShell();
    }

    private boolean handleErrorMessage(String message) {

        if (message != null) {
            MessageDialog.openError(getShell(), Messages.Error, message);
            return true;
        }

        return false;
    }

    private boolean handleException(Exception e) {
        return handleErrorMessage(ExceptionHelper.getLocalizedMessage(e));
    }

    public void doubleClick(DoubleClickEvent e) {
        int index = table.getSelectionIndex();
        if (index != -1) {
            TableItem tableItem = table.getItem(index);
            if (tableItem.getData() instanceof SpooledFile) {
                SpooledFile spooledFile = (SpooledFile)tableItem.getData();
                String openFormat = Preferences.getInstance().getSpooledFileConversionDefaultFormat();
                handleErrorMessage(spooledFile.open(openFormat));
            }
        }
    }
}
