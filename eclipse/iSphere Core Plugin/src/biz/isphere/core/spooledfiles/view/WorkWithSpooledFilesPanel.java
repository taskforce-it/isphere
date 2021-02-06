/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.internal.IResizableTableColumnsViewer;
import biz.isphere.base.internal.IntHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.WorkWithSpooledFilesHelper;
import biz.isphere.core.spooledfiles.view.events.ITableItemChangeListener;
import biz.isphere.core.spooledfiles.view.menus.WorkWithSpooledFilesMenuAdapter;

public class WorkWithSpooledFilesPanel extends Composite implements IResizableTableColumnsViewer, IPostSelectionProvider {

    /*
     * View pin properties
     */
    private static final String SORT_COLUMN_INDEX = "sortColumnIndex"; //$NON-NLS-1$
    private static final String SORT_DIRECTION = "sortDirection"; //$NON-NLS-1$
    private static final String COLUMN_ORDER = "columnOrder"; //$NON-NLS-1$
    private static final String COLUMN_WIDTH = "columnWidth_"; //$NON-NLS-1$

    private WorkWithSpooledFilesHelper workWithSpooledFilesHelper;

    private DialogSettingsManager dialogSettingsManager;

    private TableViewer tableViewer;
    private Table table;
    private WorkWithSpooledFilesSorter tableSorter;

    private Map<String, String> pinProperties;

    private IDoubleClickListener doubleClickListener;
    private ITableItemChangeListener listener;

    public WorkWithSpooledFilesPanel(Composite parent, int style) {
        super(parent, style);

        createContentArea();
    }

    @Override
    public boolean setFocus() {
        boolean hasFocus = tableViewer.getTable().setFocus();
        if (tableViewer.getSelection().isEmpty() && tableViewer.getTable().getItemCount() > 0) {
            tableViewer.getTable().setSelection(0);

        }
        return hasFocus;
    }

    public void setChangedListener(ITableItemChangeListener listener) {
        this.listener = listener;
    }

    public void setInput(String connectionName, SpooledFile[] spooledFiles) {

        SpooledFile[] oldInput = (SpooledFile[])this.tableViewer.getInput();

        this.tableViewer.setInput(spooledFiles);

        if (connectionName == null || spooledFiles == null) {
            setMenuAndDoubleClickListener(null, null);
            return;
        }

        /*
         * Select new spooled files.
         */
        if (oldInput != null) {
            Set<SpooledFile> oldSet = new HashSet<SpooledFile>(Arrays.asList(oldInput));
            List<SpooledFile> newSpooledFiles = new ArrayList<SpooledFile>();
            for (SpooledFile spooledFile : spooledFiles) {
                if (!oldSet.contains(spooledFile)) {
                    newSpooledFiles.add(spooledFile);
                }
            }
            if (!newSpooledFiles.isEmpty()) {
                ISelection selection = new StructuredSelection(newSpooledFiles);
                tableViewer.setSelection(selection);
            } else {
                tableViewer.setSelection(null);
            }
        }

        Menu menu = new Menu(table);
        WorkWithSpooledFilesMenuAdapter menuAdapter = new WorkWithSpooledFilesMenuAdapter(menu, connectionName, tableViewer);
        menuAdapter.addChangedListener(listener);
        menu.addMenuListener(menuAdapter);

        setMenuAndDoubleClickListener(menu, menuAdapter);

        workWithSpooledFilesHelper = new WorkWithSpooledFilesHelper(getShell(), connectionName);
        workWithSpooledFilesHelper.addChangedListener(listener);
    }

    public int getItemCount() {
        return tableViewer.getTable().getItemCount();
    }

    public SpooledFile[] getItems() {

        SpooledFile[] spooledFiles = new SpooledFile[tableViewer.getTable().getItemCount()];
        TableItem[] tableItems = tableViewer.getTable().getItems();
        for (int i = 0; i < tableItems.length; i++) {
            spooledFiles[i] = (SpooledFile)tableItems[i].getData();
        }

        return spooledFiles;
    }

    public int getSelectionCount() {
        return tableViewer.getTable().getSelectionCount();
    }

    public SpooledFile[] getSelectedItems() {

        SpooledFile[] spooledFiles = new SpooledFile[tableViewer.getTable().getSelectionCount()];
        TableItem[] tableItems = tableViewer.getTable().getSelection();
        for (int i = 0; i < tableItems.length; i++) {
            spooledFiles[i] = (SpooledFile)tableItems[i].getData();
        }

        return spooledFiles;
    }

    public void update(SpooledFile spooledFile) {
        tableViewer.update(spooledFile, null);
    }

    public void remove(SpooledFile[] spooledFiles) {
        tableViewer.remove(spooledFiles);
        table.update();
    }

    @Override
    public void setLayout(Layout layout) {
    }

    public void setMenu(Menu menu) {
        throw new IllegalAccessError("Do not use setMenu()."); //$NON-NLS-1$
    }

    public void setMenuAndDoubleClickListener(Menu menu, IDoubleClickListener listener) {

        table.setMenu(menu);

        if (doubleClickListener != null) {
            tableViewer.removeDoubleClickListener(doubleClickListener);
        }

        doubleClickListener = listener;

        if (doubleClickListener != null) {
            tableViewer.addDoubleClickListener(doubleClickListener);
        }
    }

    public void resetColumnSizes() {
        getDialogSettingsManager().resetColumnWidths(table);
    }

    private void createContentArea() {
        super.setLayout(new FillLayout());

        createTableViewer();

        this.pinProperties = new HashMap<String, String>();
    }

    private void createTableViewer() {

        tableViewer = new TableViewer(this, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        table = tableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        table.addKeyListener(new KeyListener() {
            public void keyReleased(KeyEvent event) {
            }

            public void keyPressed(KeyEvent event) {
                if ((event.stateMask & SWT.CTRL) == SWT.CTRL) {
                    if (event.keyCode == 'a') {
                        table.selectAll();
                    }
                } else if (event.stateMask == 0) {
                    if (event.keyCode == SWT.DEL) {
                        workWithSpooledFilesHelper.performDeleteSpooledFile(getSelectedItems());
                    }
                }
            }
        });

        tableViewer.setLabelProvider(new WorkWithSpooledFilesLabelProvider());
        tableViewer.setContentProvider(new WorkWithSpooledFilesContentProvider());

        Listener sortListener = new Listener() {
            public void handleEvent(Event e) {
                TableColumn column = (TableColumn)e.widget;
                tableSorter.setSortColumn(column);
                tableSorter.refresh();
                updateSortPinProperties();
            }
        };

        WorkWithSpooledFilesTableColumns[] columns = WorkWithSpooledFilesTableColumns.getDefaultColumns();
        for (WorkWithSpooledFilesTableColumns column : columns) {
            createColumn(table, column, sortListener);
        }

        tableSorter = new WorkWithSpooledFilesSorter(tableViewer, getDialogSettingsManager());
        tableViewer.setSorter(tableSorter);
        tableViewer.setUseHashlookup(true);

        ControlAdapter columnResizeListener = new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent event) {
                TableColumn column = (TableColumn)event.getSource();
                updateColumnResizeProperties(column);
            }
        };

        Listener columnMoveListener = new Listener() {
            public void handleEvent(Event event) {
                TableColumn column = (TableColumn)event.widget;
                Table table = column.getParent();
                updateColumnMoveProperties(table);
            }
        };

        for (TableColumn column : table.getColumns()) {
            column.addControlListener(columnResizeListener);
            column.addListener(SWT.Move, columnMoveListener);
        }
    }

    private TableColumn createColumn(Table table, WorkWithSpooledFilesTableColumns column, Listener sortListener) {
        return createColumn(table, column, sortListener, SWT.LEFT);
    }

    private TableColumn createColumn(Table table, WorkWithSpooledFilesTableColumns column, Listener sortListener, int style) {

        TableColumn tableColumn = getDialogSettingsManager().createResizableTableColumn(table, style, column.name, column.width, column.index);
        tableColumn.setText(column.label);

        tableColumn.addListener(SWT.Selection, sortListener);

        return tableColumn;
    }

    public void setPinned(boolean pinned) {

        if (pinned) {
            dialogSettingsManager.setSaveTableStatusEnabled(table, false);
        } else {
            dialogSettingsManager.setSaveTableStatusEnabled(table, true);
        }
    }

    public Set<String> getPinKeys() {

        Set<String> keySet = new HashSet<String>();
        keySet.add(SORT_COLUMN_INDEX);
        keySet.add(SORT_DIRECTION);
        keySet.add(COLUMN_ORDER);

        for (TableColumn column : table.getColumns()) {
            String columnName = dialogSettingsManager.getColumnName(column);
            keySet.add(COLUMN_WIDTH + columnName);
        }

        return keySet;
    }

    public Map<String, String> getPinProperties() {
        return pinProperties;
    }

    private void updateSortPinProperties() {

        if (pinProperties == null) {
            return;
        }

        pinProperties.put(SORT_DIRECTION, tableSorter.getSortDirection());
        pinProperties.put(SORT_COLUMN_INDEX, tableSorter.getSortColumnName());
    }

    private void updateColumnResizeProperties(TableColumn column) {

        String columnName = dialogSettingsManager.getColumnName(column);
        pinProperties.put(COLUMN_WIDTH + columnName, Integer.toString(column.getWidth()));
    }

    private void updateColumnMoveProperties(Table table) {

        int[] columnOrder = table.getColumnOrder();
        pinProperties.put(COLUMN_ORDER, IntHelper.toString(columnOrder));
    }

    public void restoreData(Map<String, String> pinProperties) {

        try {

            String sortAttributeName = pinProperties.get(SORT_COLUMN_INDEX);
            String sortOrder = pinProperties.get(SORT_DIRECTION);

            TableColumn tableColumn = dialogSettingsManager.getColumn(table, sortAttributeName);
            tableSorter.setSortColumn(tableColumn, sortOrder);

            String columnOrder = pinProperties.get(COLUMN_ORDER);
            int[] columnOrderArray = IntHelper.toIntArray(columnOrder);
            if (columnOrderArray.length == table.getColumnCount()) {
                table.setColumnOrder(columnOrderArray);
            }

            String prefix = COLUMN_WIDTH;
            for (Map.Entry<String, String> entry : pinProperties.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith(prefix)) {
                    String columnName = key.substring(prefix.length());
                    TableColumn column = dialogSettingsManager.getColumn(table, columnName);
                    int width = IntHelper.tryParseInt(entry.getValue(), -1);
                    if (width > 0) {
                        column.setWidth(width);
                    }
                }
            }

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Error restoring pinned view 'Work With Spooled Files' ***", e); //$NON-NLS-1$
        }
    }

    private DialogSettingsManager getDialogSettingsManager() {

        if (dialogSettingsManager == null) {
            dialogSettingsManager = new DialogSettingsManager(ISpherePlugin.getDefault().getDialogSettings(), getClass());
        }
        return dialogSettingsManager;
    }

    /*
     * ISelectionProvider methods
     */

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        tableViewer.addPostSelectionChangedListener(listener);
    }

    public ISelection getSelection() {
        return tableViewer.getSelection();
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        tableViewer.removeSelectionChangedListener(listener);
    }

    public void setSelection(ISelection selection) {
        tableViewer.setSelection(selection);
    }

    public void addPostSelectionChangedListener(ISelectionChangedListener listener) {
        tableViewer.addPostSelectionChangedListener(listener);
    }

    public void removePostSelectionChangedListener(ISelectionChangedListener listener) {
        tableViewer.removePostSelectionChangedListener(listener);
    }
}
