/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.internal.IntHelper;
import biz.isphere.core.spooledfiles.BasicSpooledFileSortComparator;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.view.rse.Columns;

/**
 * This table sorter automatically stores the sort properties in
 * "dialog_settings.xml". Previously stored properties can be re-applied by
 * calling {@link #setPreviousSortOrder()}.
 */
public class WorkWithSpooledFilesSorter extends ViewerSorter {

    private static final String SORT_COLUMN_INDEX = "sortColumnIndex"; //$NON-NLS-1$
    private static final String SORT_DIRECTION = "sortDirection"; //$NON-NLS-1$

    private static final String SORT_UP = "Up"; //$NON-NLS-1$
    private static final String SORT_DOWN = "Down"; //$NON-NLS-1$
    private static final String SORT_NONE = "None"; //$NON-NLS-1$
    private static final int SORT_INDEX_NULL = -1;

    private TableViewer tableViewer;
    private Table table;
    private DialogSettingsManager dialogSettingsManager;

    private BasicSpooledFileSortComparator comparator;

    public WorkWithSpooledFilesSorter(TableViewer tableViewer, DialogSettingsManager dialogSettingsManager) {
        this.tableViewer = tableViewer;
        this.table = tableViewer.getTable();
        this.dialogSettingsManager = dialogSettingsManager;
        this.comparator = new BasicSpooledFileSortComparator();

        setSortColumnInternally(null, SORT_NONE);
    }

    private int getSortColumnIndex() {

        Columns sortColumn = comparator.getSortColumn();
        if (sortColumn == null) {
            return SORT_INDEX_NULL;
        }

        return sortColumn.ordinal();
    }

    public String getSortDirection() {

        int sortDirection = table.getSortDirection();
        if (sortDirection == SWT.UP) {
            return SORT_UP;
        } else if (sortDirection == SWT.DOWN) {
            return SORT_DOWN;
        } else {
            return SORT_NONE;
        }
    }

    public void setSortColumn(TableColumn column) {
        setSortProperties(column, changeSortDirection(column));
        storeSortProperties();
    }

    public void setPreviousSortOrder() {

        if (dialogSettingsManager == null) {
            return;
        }

        String sortColumnIndex = dialogSettingsManager.loadValue(SORT_COLUMN_INDEX, null);
        if (sortColumnIndex == null) {
            return;
        }

        String sortOrder = dialogSettingsManager.loadValue(SORT_DIRECTION, null);
        if (sortOrder == null) {
            return;
        }

        int columnIndex = IntHelper.tryParseInt(sortColumnIndex, SORT_INDEX_NULL);
        if (columnIndex >= 0 && columnIndex < table.getColumnCount()) {
            setSortColumnInternally(table.getColumn(columnIndex), sortOrder);
        }
    }

    public void refresh() {
        tableViewer.refresh();
    }

    private int changeSortDirection(TableColumn column) {

        if (column == table.getSortColumn()) {
            if (table.getSortDirection() == SWT.NONE) {
                return SWT.UP;
            } else if (table.getSortDirection() == SWT.UP) {
                return SWT.DOWN;
            } else {
                return SWT.NONE;
            }
        } else {
            return SWT.UP;
        }
    }

    private void setSortColumnInternally(TableColumn column, String direction) {

        if (column == null) {
            setSortProperties(null, SWT.NONE);
        } else {
            if (SORT_UP.equals(direction)) {
                setSortProperties(column, SWT.UP);
            } else if (SORT_DOWN.equals(direction)) {
                setSortProperties(column, SWT.DOWN);
            } else {
                setSortProperties(column, SWT.NONE);
            }
        }
    }

    private void setSortProperties(TableColumn column, int direction) {

        table.setSortDirection(direction);

        if (direction == SWT.NONE) {
            this.table.setSortColumn(null);
            this.comparator.setColumnIndex(null);
        } else {
            this.table.setSortColumn(column);
            this.comparator.setColumnIndex(getColumnIndex(column));
        }

        if (table.getSortDirection() == SWT.DOWN) {
            this.comparator.setReverseOrder(true);
        } else {
            this.comparator.setReverseOrder(false);
        }
    }

    private void storeSortProperties() {

        if (dialogSettingsManager != null) {
            dialogSettingsManager.storeValue(SORT_COLUMN_INDEX, Integer.toString(getSortColumnIndex()));
            dialogSettingsManager.storeValue(SORT_DIRECTION, getSortDirection());
        }
    }

    private Columns getColumnIndex(TableColumn column) {
        return Columns.getByName(dialogSettingsManager.getColumnName(column));
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        return comparator.compare((SpooledFile)e1, (SpooledFile)e2);
    }
}
