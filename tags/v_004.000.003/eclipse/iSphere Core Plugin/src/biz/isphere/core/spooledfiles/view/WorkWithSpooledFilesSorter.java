/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
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
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.spooledfiles.BasicSpooledFileSortComparator;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.SpooledFileAttributes;

public class WorkWithSpooledFilesSorter extends ViewerSorter {

    public static final String SORT_UP = "Up"; //$NON-NLS-1$
    public static final String SORT_DOWN = "Down"; //$NON-NLS-1$
    public static final String SORT_NONE = "None"; //$NON-NLS-1$

    private static final String SORT_COLUMN_INDEX_NULL = ""; //$NON-NLS-1$

    private TableViewer tableViewer;
    private Table table;
    private DialogSettingsManager dialogSettingsManager;

    private BasicSpooledFileSortComparator comparator;

    public WorkWithSpooledFilesSorter(TableViewer tableViewer, DialogSettingsManager dialogSettingsManager) {
        this.tableViewer = tableViewer;
        this.table = tableViewer.getTable();
        this.dialogSettingsManager = dialogSettingsManager;
        this.comparator = new BasicSpooledFileSortComparator();

        setDefaultSortOrder();
    }

    public String getSortColumnName() {

        SpooledFileAttributes sortColumn = comparator.getSortAttribute();
        if (sortColumn == null) {
            return SORT_COLUMN_INDEX_NULL;
        }

        TableColumn tableColumn = dialogSettingsManager.getColumn(table, sortColumn.attributeName);
        if (tableColumn == null) {
            return SORT_COLUMN_INDEX_NULL;
        }

        return sortColumn.attributeName;
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
    }

    public void setSortColumn(TableColumn column, String direction) {

        if (SORT_UP.equals(direction) || SORT_DOWN.equals(direction) || SORT_NONE.equals(direction)) {
            setSortColumnInternally(column, direction);
        } else {
            setSortColumnInternally(column, SORT_NONE);
        }

    }

    private void setDefaultSortOrder() {

        if (dialogSettingsManager == null) {
            setSortColumnInternally(null, SORT_NONE);
            return;
        }

        if (Preferences.getInstance().isMergeSpooledFileFilters()) {
            TableColumn tableColumn = dialogSettingsManager.getColumn(table, SpooledFileAttributes.CREATION_TIMESTAMP.attributeName);
            setSortColumnInternally(tableColumn, SORT_UP);
        } else {
            setSortColumnInternally(null, SORT_NONE);
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
            this.comparator.setSortAttribute(null);
        } else {
            this.table.setSortColumn(column);
            this.comparator.setSortAttribute(getSortAttribute(column));
        }

        if (table.getSortDirection() == SWT.DOWN) {
            this.comparator.setReverseOrder(true);
        } else {
            this.comparator.setReverseOrder(false);
        }
    }

    private SpooledFileAttributes getSortAttribute(TableColumn column) {
        return SpooledFileAttributes.getByName(dialogSettingsManager.getColumnName(column));
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        return comparator.compare((SpooledFile)e1, (SpooledFile)e2);
    }
}
