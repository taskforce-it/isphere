/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view;

import java.util.Map;

import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
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
import biz.isphere.core.Messages;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.view.events.ITableItemChangeListener;
import biz.isphere.core.spooledfiles.view.menus.WorkWithSpooledFilesMenuAdapter;

public class WorkWithSpooledFilesPanel extends Composite implements IResizableTableColumnsViewer, ControlListener, IPostSelectionProvider {

    /*
     * View pin properties
     */
    private static final String TABLE_COLUMN = "tableColumn_"; //$NON-NLS-1$
    private static final String SORT_COLUMN_INDEX = "sortColumnIndex"; //$NON-NLS-1$
    private static final String SORT_ORDER = "sortOrder"; //$NON-NLS-1$

    private static final String SORT_UP = "Up";
    private static final String SORT_DOWN = "Down";
    private static final String SORT_NONE = "None";

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

    public void setChangedListener(ITableItemChangeListener listener) {
        this.listener = listener;
    }

    public void setInput(String connectionName, SpooledFile[] spooledFiles) {

        this.tableViewer.setInput(spooledFiles);

        Menu menu = new Menu(table);
        WorkWithSpooledFilesMenuAdapter menuAdapter = new WorkWithSpooledFilesMenuAdapter(menu, connectionName, tableViewer);
        menuAdapter.addChangedListener(listener);
        menu.addMenuListener(menuAdapter);

        table.setMenu(menu);
        setDoubleClickListener(menuAdapter);
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

    @Override
    public void setLayout(Layout layout) {
    }

    public void setMenu(Menu menu) {
        throw new IllegalAccessError("Do not use setMenu()."); //$NON-NLS-1$
    }

    public void setDoubleClickListener(IDoubleClickListener listener) {

        if (doubleClickListener != null) {
            tableViewer.removeDoubleClickListener(doubleClickListener);
        }

        doubleClickListener = listener;
        tableViewer.addDoubleClickListener(doubleClickListener);
    }

    public void resetColumnSizes() {
        getDialogSettingsManager().resetColumnWidths(table);
    }

    public void setPinProperties(Map<String, String> pinProperties) {

        this.pinProperties = pinProperties;
        initializePinProperties();
    }

    private void createContentArea() {
        super.setLayout(new FillLayout());

        createTableViewer();
    }

    private void createTableViewer() {

        tableViewer = new TableViewer(this, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);

        tableSorter = new WorkWithSpooledFilesSorter(tableViewer);
        tableViewer.setSorter(tableSorter);
        tableViewer.setUseHashlookup(true);

        Listener sortListener = new Listener() {
            public void handleEvent(Event e) {
                TableColumn column = (TableColumn)e.widget;
                tableSorter.setSortColumn(column);
                updateSortPinProperties();
                tableSorter.refresh();
            }
        };

        table = tableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        createColumn(table, Columns.STATUS, Messages.Status, sortListener);
        createColumn(table, Columns.FILE, Messages.File, sortListener);
        createColumn(table, Columns.FILE_NUMBER, Messages.File_number, sortListener);
        createColumn(table, Columns.JOB_NAME, Messages.Job_name, sortListener);
        createColumn(table, Columns.JOB_USER, Messages.Job_user, sortListener);
        createColumn(table, Columns.JOB_NUMBER, Messages.Job_number, sortListener);
        createColumn(table, Columns.JOB_SYSTEM, Messages.Job_system, sortListener);
        createColumn(table, Columns.CREATION_DATE, Messages.Creation_date, sortListener);
        createColumn(table, Columns.CREATION_TIME, Messages.Creation_time, sortListener);
        createColumn(table, Columns.OUTPUT_QUEUE, Messages.Output_queue, sortListener);
        createColumn(table, Columns.OUTPUT_PRIORITY, Messages.Output_priority, sortListener);
        createColumn(table, Columns.USER_DATE, Messages.User_data, sortListener);
        createColumn(table, Columns.FORM_TYPE, Messages.Form_type, sortListener);
        createColumn(table, Columns.COPIES, Messages.Copies, sortListener);
        createColumn(table, Columns.PAGES, Messages.Pages, sortListener);
        createColumn(table, Columns.CURRENT_PAGE, Messages.Current_page, sortListener);
        createColumn(table, Columns.CREATION_TIMESTAMP, Messages.Creation_timestamp, sortListener);

        tableViewer.setLabelProvider(new WorkWithSpooledFilesLabelProvider());
        tableViewer.setContentProvider(new WorkWithSpooledFilesContentProvider());
    }

    private TableColumn createColumn(Table table, Columns column, String text, Listener sortListener) {
        return createColumn(table, column, text, sortListener, SWT.LEFT);
    }

    private TableColumn createColumn(Table table, Columns column, String text, Listener sortListener, int style) {
        TableColumn tableColumn = getDialogSettingsManager().createResizableTableColumn(table, style, column.index, column.name, column.width);
        tableColumn.setText(text);

        tableColumn.addListener(SWT.Selection, sortListener);
        tableColumn.addControlListener(this);

        return tableColumn;
    }

    private void initializePinProperties() {

        if (pinProperties == null) {
            return;
        }

        for (TableColumn column : table.getColumns()) {
            pinProperties.put(getPinPropertiesKey(column), Integer.toString(column.getWidth()));
        }

        updateSortPinProperties();
    }

    private void updateColumnPinProperties(TableColumn column) {

        if (pinProperties == null) {
            return;
        }

        pinProperties.put(getPinPropertiesKey(column), Integer.toString(column.getWidth()));
    }

    private void updateSortPinProperties() {

        if (pinProperties == null) {
            return;
        }

        if (tableSorter.getSortDirection() == SWT.UP) {
            pinProperties.put(SORT_ORDER, SORT_UP);
        } else if (tableSorter.getSortDirection() == SWT.DOWN) {
            pinProperties.put(SORT_ORDER, SORT_DOWN);
        } else {
            pinProperties.put(SORT_ORDER, SORT_NONE);
        }

        pinProperties.put(SORT_COLUMN_INDEX, Integer.toString(tableSorter.getSortColumnIndex()));
    }

    public void restorePinProperties(Map<String, String> pinProperties) {

        this.pinProperties = pinProperties;

        for (TableColumn column : table.getColumns()) {
            String key = getPinPropertiesKey(column);
            Object width = pinProperties.get(key);
            int columnWidth = -1;
            if (width instanceof String) {
                columnWidth = IntHelper.tryParseInt((String)width, -1);
                if (columnWidth != -1) {
                    column.setWidth(columnWidth);
                }
            }
            // Fix incorrect column width
            if (columnWidth == -1) {
                pinProperties.put(key, Integer.toString(column.getWidth()));
            }
        }

        String sortColumnIndex = pinProperties.get(SORT_COLUMN_INDEX);
        int columnIndex = IntHelper.tryParseInt(sortColumnIndex, -1);
        if (columnIndex >= 0 && columnIndex < table.getColumnCount()) {
            String sortOrder = pinProperties.get(SORT_ORDER);
            if (SORT_UP.equals(sortOrder)) {
                tableSorter.setSortColumn(table.getColumn(columnIndex), SWT.UP);
            } else if (SORT_DOWN.equals(sortOrder)) {
                tableSorter.setSortColumn(table.getColumn(columnIndex), SWT.DOWN);
            } else {
                tableSorter.setSortColumn(null, SWT.NONE);
            }
        }
    }

    private String getPinPropertiesKey(TableColumn column) {
        String columnName = getDialogSettingsManager().getColumnName(column);
        return TABLE_COLUMN + columnName;
    }

    private DialogSettingsManager getDialogSettingsManager() {

        if (dialogSettingsManager == null) {
            dialogSettingsManager = new DialogSettingsManager(ISpherePlugin.getDefault().getDialogSettings(), getClass());
        }
        return dialogSettingsManager;
    }

    /*
     * ControlListener methods
     */

    public void controlMoved(ControlEvent arg0) {
    }

    public void controlResized(ControlEvent event) {
        TableColumn column = (TableColumn)event.getSource();
        updateColumnPinProperties(column);
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
