/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.bindingdirectoryeditor;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.DialogActionTypes;
import biz.isphere.core.internal.IEditor;
import biz.isphere.core.internal.Size;
import biz.isphere.core.swt.widgets.WidgetFactory;

import com.ibm.as400.access.AS400;

public class BindingDirectoryEntryViewer {

    private String level;
    private AS400 as400;
    private Connection jdbcConnection;
    private String connectionName;
    private String library;
    private String bindingDirectory;
    private String mode;
    private int ccsid;
    private TableViewer _tableViewer;
    private Table _table;
    private Shell shell;
    private ArrayList<BindingDirectoryEntry> _bindingDirectoryEntries = new ArrayList<BindingDirectoryEntry>();
    private Button buttonUp;
    private Button buttonDown;
    private Label statusLine;

    private class LabelProviderTableViewer extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            BindingDirectoryEntry bindingDirectoryEntry = (BindingDirectoryEntry)element;
            if (columnIndex == 0) {
                return bindingDirectoryEntry.getLibrary();
            } else if (columnIndex == 1) {
                return bindingDirectoryEntry.getObject();
            } else if (columnIndex == 2) {
                return bindingDirectoryEntry.getObjectType();
            } else if (level.compareTo("V6R1M0") >= 0 && columnIndex == 3) {
                return bindingDirectoryEntry.getActivation();
            }
            return "*UNKNOWN";
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }

    private class ContentProviderTableViewer implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            BindingDirectoryEntry[] bindingDirectoryEntries = new BindingDirectoryEntry[_bindingDirectoryEntries.size()];
            _bindingDirectoryEntries.toArray(bindingDirectoryEntries);
            return bindingDirectoryEntries;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    public BindingDirectoryEntryViewer(String level, AS400 as400, Connection jdbcConnection, String connectionName, String library,
        String bindingDirectory, String mode) {

        this.level = level;
        this.as400 = as400;
        this.jdbcConnection = jdbcConnection;
        this.connectionName = connectionName;
        this.library = library;
        this.bindingDirectory = bindingDirectory;
        this.mode = mode;
        this.ccsid = IBMiHostContributionsHandler.getSystemCcsid(connectionName);

        _bindingDirectoryEntries = BindingDirectory.getEntries(level, as400, jdbcConnection, connectionName, library, bindingDirectory);

    }

    /**
     * @wbp.parser.entryPoint
     */
    public void createContents(Composite parent) {

        shell = parent.getShell();

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(2, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        _tableViewer = new TableViewer(container, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        _tableViewer.setLabelProvider(new LabelProviderTableViewer());
        _tableViewer.setContentProvider(new ContentProviderTableViewer());
        _tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                if (_tableViewer.getSelection() instanceof IStructuredSelection) {

                    IStructuredSelection structuredSelection = (IStructuredSelection)_tableViewer.getSelection();
                    if (structuredSelection.getFirstElement() instanceof BindingDirectoryEntry) {
                        BindingDirectoryEntry bindingDirectoryEntry = (BindingDirectoryEntry)structuredSelection.getFirstElement();
                        doChangeEntry(bindingDirectoryEntry);
                    }
                }
            }
        });
        _table = _tableViewer.getTable();
        _table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                refreshUpDown();
            }
        });
        _table.setLinesVisible(true);
        _table.setHeaderVisible(true);
        _table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TableColumn columnLibrary = new TableColumn(_table, SWT.NONE);
        columnLibrary.setWidth(Size.getSize(100));
        columnLibrary.setText(Messages.Library);

        final TableColumn columnObject = new TableColumn(_table, SWT.NONE);
        columnObject.setWidth(Size.getSize(100));
        columnObject.setText(Messages.Object);

        final TableColumn columnObjectType = new TableColumn(_table, SWT.NONE);
        columnObjectType.setWidth(Size.getSize(100));
        columnObjectType.setText(Messages.Object_type);

        if (level.compareTo("V6R1M0") >= 0) {
            final TableColumn columnActivation = new TableColumn(_table, SWT.NONE);
            columnActivation.setWidth(Size.getSize(100));
            columnActivation.setText(Messages.Activation);
        }

        Composite compositeUpDown = new Composite(container, SWT.NONE);
        compositeUpDown.setLayout(new GridLayout(1, false));
        compositeUpDown.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));

        buttonUp = WidgetFactory.createPushButton(compositeUpDown);
        buttonUp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                doMoveUpDown(-1);
            }
        });
        buttonUp.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, true, 1, 1));
        buttonUp.setText(Messages.Up);
        buttonUp.setEnabled(false);

        buttonDown = WidgetFactory.createPushButton(compositeUpDown);
        buttonDown.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                doMoveUpDown(+1);
            }
        });
        buttonDown.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, true, 1, 1));
        buttonDown.setText(Messages.Down);
        buttonDown.setEnabled(false);

        statusLine = new Label(container, SWT.NONE);
        GridData gridDataStatusLine = new GridData(GridData.FILL_HORIZONTAL);
        gridDataStatusLine.horizontalSpan = 2;
        statusLine.setLayoutData(gridDataStatusLine);

        final Menu menuTableBindingDirectoryEntries = new Menu(_table);
        menuTableBindingDirectoryEntries.addMenuListener(new MenuAdapter() {

            private MenuItem menuItemNew;
            private MenuItem menuItemChange;
            private MenuItem menuItemCopy;
            private MenuItem menuItemDelete;
            private MenuItem menuItemDisplay;
            private MenuItem menuSeparator;
            private MenuItem menuItemRefresh;

            @Override
            public void menuShown(MenuEvent event) {
                destroyMenuItems();
                createMenuItems();
            }

            public void destroyMenuItems() {
                if (!((menuItemNew == null) || (menuItemNew.isDisposed()))) {
                    menuItemNew.dispose();
                }
                if (!((menuItemChange == null) || (menuItemChange.isDisposed()))) {
                    menuItemChange.dispose();
                }
                if (!((menuItemCopy == null) || (menuItemCopy.isDisposed()))) {
                    menuItemCopy.dispose();
                }
                if (!((menuItemDelete == null) || (menuItemDelete.isDisposed()))) {
                    menuItemDelete.dispose();
                }
                if (!((menuItemDisplay == null) || (menuItemDisplay.isDisposed()))) {
                    menuItemDisplay.dispose();
                }
                if (!((menuSeparator == null) || (menuSeparator.isDisposed()))) {
                    menuSeparator.dispose();
                }
                if (!((menuItemRefresh == null) || (menuItemRefresh.isDisposed()))) {
                    menuItemRefresh.dispose();
                }
            }

            public void createMenuItems() {
                boolean isChange = false;
                boolean isCopy = false;
                boolean isDelete = false;
                boolean isDisplay = false;
                boolean isRefresh = true;
                Object[] selectedItems = retrieveSelectedTableItems();
                for (int idx = 0; idx < selectedItems.length; idx++) {
                    if (selectedItems[idx] instanceof BindingDirectoryEntry) {
                        if (mode.equals(IEditor.EDIT)) {
                            isChange = true;
                            isCopy = true;
                            isDelete = true;
                        }
                        isDisplay = true;
                    }
                }
                if (mode.equals(IEditor.EDIT)) {
                    createMenuItemNew();
                }
                if (isChange) createMenuItemChange();
                if (isCopy) createMenuItemCopy();
                if (isDelete) createMenuItemDelete();
                if (isDisplay) createMenuItemDisplay();

                if (isRefresh) {
                    menuSeparator = new MenuItem(menuTableBindingDirectoryEntries, SWT.SEPARATOR);
                    createMenuItemRefresh();
                }
            }

            public void createMenuItemNew() {
                menuItemNew = new MenuItem(menuTableBindingDirectoryEntries, SWT.NONE);
                menuItemNew.setText(Messages.New);
                menuItemNew.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_NEW));
                menuItemNew.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {

                        BindingDirectoryEntry _bindingDirectoryEntry = new BindingDirectoryEntry();
                        _bindingDirectoryEntry.setConnection(connectionName);
                        doAddEntry(_bindingDirectoryEntry);

                        refreshTableViewer();
                    }
                });
            }

            public void createMenuItemChange() {
                menuItemChange = new MenuItem(menuTableBindingDirectoryEntries, SWT.NONE);
                menuItemChange.setText(Messages.Change);
                menuItemChange.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_CHANGE));
                menuItemChange.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        Object[] tempSelection = retrieveSelectedTableItems();
                        for (Object selectedItem : tempSelection) {
                            if (selectedItem instanceof BindingDirectoryEntry) {

                                BindingDirectoryEntry bindingDirectoryEntry = (BindingDirectoryEntry)selectedItem;
                                if (!doChangeEntry(bindingDirectoryEntry)) {
                                    return;
                                }
                            }
                        }
                        refreshTableViewer();
                    }
                });
            }

            public void createMenuItemCopy() {
                menuItemCopy = new MenuItem(menuTableBindingDirectoryEntries, SWT.NONE);
                menuItemCopy.setText(Messages.Duplicate);
                menuItemCopy.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_COPY));
                menuItemCopy.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        Object[] tempSelection = retrieveSelectedTableItems();
                        for (Object selectedItem : tempSelection) {
                            if (selectedItem instanceof BindingDirectoryEntry) {

                                BindingDirectoryEntry fromBindingDirectoryEntry = ((BindingDirectoryEntry)selectedItem);

                                BindingDirectoryEntry toBindingDirectoryEntry = new BindingDirectoryEntry();
                                toBindingDirectoryEntry.setConnection(fromBindingDirectoryEntry.getConnection());
                                toBindingDirectoryEntry.setLibrary(fromBindingDirectoryEntry.getLibrary());
                                toBindingDirectoryEntry.setObject(fromBindingDirectoryEntry.getObject());
                                toBindingDirectoryEntry.setObjectType(fromBindingDirectoryEntry.getObjectType());
                                if (level.compareTo("V6R1M0") >= 0) {
                                    toBindingDirectoryEntry.setActivation(fromBindingDirectoryEntry.getActivation());
                                }

                                if (!doCopyEntry(fromBindingDirectoryEntry, toBindingDirectoryEntry)) {
                                    return;
                                }
                            }
                        }
                        refreshTableViewer();
                    }
                });
            }

            public void createMenuItemDelete() {
                menuItemDelete = new MenuItem(menuTableBindingDirectoryEntries, SWT.NONE);
                menuItemDelete.setText(Messages.Delete);
                menuItemDelete.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_DELETE));
                menuItemDelete.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        Object[] tempSelection = retrieveSelectedTableItems();
                        for (Object selectedItem : tempSelection) {
                            if (selectedItem instanceof BindingDirectoryEntry) {

                                BindingDirectoryEntry bindingDirectoryEntry = (BindingDirectoryEntry)selectedItem;
                                if (!doRemoveEntry(bindingDirectoryEntry)) {
                                    return;
                                }
                            }
                        }
                    }
                });
            }

            public void createMenuItemDisplay() {
                menuItemDisplay = new MenuItem(menuTableBindingDirectoryEntries, SWT.NONE);
                menuItemDisplay.setText(Messages.Display);
                menuItemDisplay.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_DISPLAY));
                menuItemDisplay.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        Object[] tempSelection = retrieveSelectedTableItems();
                        for (Object selectedItem : tempSelection) {
                            if (selectedItem instanceof BindingDirectoryEntry) {

                                BindingDirectoryEntry bindingDirectoryEntry = (BindingDirectoryEntry)selectedItem;
                                if (!doDisplayEntry(bindingDirectoryEntry)) {
                                    break;
                                }
                            }
                        }
                    }
                });
            }

            public void createMenuItemRefresh() {
                menuItemRefresh = new MenuItem(menuTableBindingDirectoryEntries, SWT.NONE);
                menuItemRefresh.setText(Messages.Refresh);
                menuItemRefresh.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_REFRESH));
                menuItemRefresh.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {

                        doRefreshItems();
                    }
                });
            }
        });
        _table.setMenu(menuTableBindingDirectoryEntries);

        _tableViewer.setInput(new Object());
        updateStatusLine();
    }

    private Object[] retrieveSelectedTableItems() {
        if (_tableViewer.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection)_tableViewer.getSelection();
            return structuredSelection.toArray();
        } else {
            return new Object[0];
        }
    }

    private void refreshUpDown() {

        buttonUp.setEnabled(false);
        buttonDown.setEnabled(false);

        if (mode.equals(IEditor.EDIT)) {

            Object[] selectedItems = retrieveSelectedTableItems();

            if (selectedItems.length == 1) {
                if (selectedItems[0] instanceof BindingDirectoryEntry) {
                    BindingDirectoryEntry bindingDirectoryEntry = (BindingDirectoryEntry)selectedItems[0];
                    int position = _bindingDirectoryEntries.indexOf(bindingDirectoryEntry) + 1;
                    if (position > 1) {
                        buttonUp.setEnabled(true);
                    }
                    if (position < _bindingDirectoryEntries.size()) {
                        buttonDown.setEnabled(true);
                    }
                }
            }

        }

    }

    private boolean doAddEntry(BindingDirectoryEntry newBindingDirectoryEntry) {

        ArrayList<BindingDirectoryEntry> newBindingDirectoryEntries = new ArrayList<BindingDirectoryEntry>(_bindingDirectoryEntries);
        BindingDirectoryEntryDetailDialog _bindingDirectoryEntryDetailDialog = new BindingDirectoryEntryDetailDialog(shell, level,
            DialogActionTypes.CREATE, newBindingDirectoryEntry, newBindingDirectoryEntries, ccsid);
        if (_bindingDirectoryEntryDetailDialog.open() == Dialog.OK) {
            newBindingDirectoryEntries.add(newBindingDirectoryEntry);
            if (uploadEntries(newBindingDirectoryEntries)) {
                _bindingDirectoryEntries = newBindingDirectoryEntries;
                deSelectAllItems();
            } else {
                return false;
            }
        }

        return true;
    }

    private boolean doChangeEntry(BindingDirectoryEntry bindingDirectoryEntry) {

        ArrayList<BindingDirectoryEntry> newBindingDirectoryEntries = new ArrayList<BindingDirectoryEntry>(_bindingDirectoryEntries);
        BindingDirectoryEntryDetailDialog _bindingDirectoryEntryDetailDialog = new BindingDirectoryEntryDetailDialog(shell, level,
            DialogActionTypes.getSubEditorActionType(mode), bindingDirectoryEntry, newBindingDirectoryEntries, ccsid);
        if (_bindingDirectoryEntryDetailDialog.open() == Dialog.OK) {
            if (uploadEntries(newBindingDirectoryEntries)) {
                _bindingDirectoryEntries = newBindingDirectoryEntries;
                deSelectItem(bindingDirectoryEntry);
                refreshTableViewerItem(bindingDirectoryEntry);
                return true;
            }
        }

        return false;
    }

    private boolean doCopyEntry(BindingDirectoryEntry fromBindingDirectoryEntry, BindingDirectoryEntry toBindingDirectoryEntry) {

        ArrayList<BindingDirectoryEntry> newBindingDirectoryEntries = new ArrayList<BindingDirectoryEntry>(_bindingDirectoryEntries);
        BindingDirectoryEntryDetailDialog _bindingDirectoryEntryDetailDialog = new BindingDirectoryEntryDetailDialog(shell, level,
            DialogActionTypes.CREATE, toBindingDirectoryEntry, newBindingDirectoryEntries, ccsid);
        if (_bindingDirectoryEntryDetailDialog.open() == Dialog.OK) {
            newBindingDirectoryEntries.add(toBindingDirectoryEntry);
            if (uploadEntries(newBindingDirectoryEntries)) {
                _bindingDirectoryEntries = newBindingDirectoryEntries;
                Object[] selectedItems = deSelectItem(fromBindingDirectoryEntry);
                refreshTableViewer();
                selectItems(selectedItems);
                return true;
            }
        }

        return false;
    }

    private boolean doRemoveEntry(BindingDirectoryEntry bindingDirectoryEntry) {

        ArrayList<BindingDirectoryEntry> newBindingDirectoryEntries = new ArrayList<BindingDirectoryEntry>(_bindingDirectoryEntries);
        BindingDirectoryEntryDetailDialog _bindingDirectoryEntryDetailDialog = new BindingDirectoryEntryDetailDialog(shell, level,
            DialogActionTypes.DELETE, bindingDirectoryEntry, newBindingDirectoryEntries, ccsid);
        if (_bindingDirectoryEntryDetailDialog.open() == Dialog.OK) {
            newBindingDirectoryEntries.remove(bindingDirectoryEntry);
            if (uploadEntries(newBindingDirectoryEntries)) {
                _bindingDirectoryEntries = newBindingDirectoryEntries;
                Object[] selectedItems = deSelectItem(bindingDirectoryEntry);
                refreshTableViewer();
                selectItems(selectedItems);
                return true;
            }
        }

        return false;
    }

    private boolean doDisplayEntry(BindingDirectoryEntry bindingDirectoryEntry) {

        BindingDirectoryEntryDetailDialog _bindingDirectoryEntryDetailDialog = new BindingDirectoryEntryDetailDialog(shell, level,
            DialogActionTypes.DISPLAY, bindingDirectoryEntry, _bindingDirectoryEntries, ccsid);
        if (_bindingDirectoryEntryDetailDialog.open() == Dialog.OK) {
            deSelectItem(bindingDirectoryEntry);
            return true;
        }

        return false;
    }

    public boolean doRefreshItems() {

        _bindingDirectoryEntries = BindingDirectory.getEntries(level, as400, jdbcConnection, connectionName, library, bindingDirectory);

        refreshTableViewer();
        deSelectAllItems();

        return true;
    }

    private void doMoveUpDown(int offset) {

        Object[] selectedItems = retrieveSelectedTableItems();

        if (selectedItems.length == 1) {

            if (selectedItems[0] instanceof BindingDirectoryEntry) {

                BindingDirectoryEntry bindingDirectoryEntry = (BindingDirectoryEntry)selectedItems[0];

                ArrayList<BindingDirectoryEntry> newBindingDirectoryEntries = new ArrayList<BindingDirectoryEntry>(_bindingDirectoryEntries);
                int position = newBindingDirectoryEntries.indexOf(bindingDirectoryEntry);
                newBindingDirectoryEntries.remove(position);
                newBindingDirectoryEntries.add(position + offset, bindingDirectoryEntry);
                if (uploadEntries(newBindingDirectoryEntries)) {
                    _bindingDirectoryEntries = newBindingDirectoryEntries;
                }

                refreshTableViewer();

                refreshUpDown();

                _table.setFocus();

            }

        }

    }

    private void selectItems(Object[] bindingDirectoryEntries) {

        _tableViewer.setSelection(new StructuredSelection(bindingDirectoryEntries), true);

        refreshUpDown();
    }

    private Object[] deSelectItem(Object item) {

        Set<Object> items = new HashSet<Object>(Arrays.asList(retrieveSelectedTableItems()));
        items.remove(item);
        _tableViewer.setSelection(new StructuredSelection(items.toArray()), true);

        refreshUpDown();

        return retrieveSelectedTableItems();
    }

    private void deSelectAllItems() {

        _tableViewer.setSelection(new StructuredSelection(), true);

        refreshUpDown();
    }

    private void refreshTableViewer() {
        _tableViewer.refresh();
        updateStatusLine();
    }

    private void refreshTableViewerItem(Object item) {
        _tableViewer.refresh(item);
    }

    private void updateStatusLine() {
        statusLine.setText(Messages.bind(Messages.Number_of_entries_colon, _table.getItemCount()));
    }

    private boolean uploadEntries(ArrayList<BindingDirectoryEntry> newBindingDirectoryEntries) {

        return BindingDirectory.saveChanges(level, as400, jdbcConnection, connectionName, library, bindingDirectory, newBindingDirectoryEntries);

    }

}
