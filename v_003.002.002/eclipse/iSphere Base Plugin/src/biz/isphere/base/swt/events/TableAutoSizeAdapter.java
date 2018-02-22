/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.swt.events;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This class automatically resizes the columns of a {@link TableViewer} object
 * according to the specified column weights. The columns that shall be resized
 * must be register to the TableAutoSizeAdapter by calling
 * {@link #addResizableColumn(TableColumn, Integer)}.
 * 
 * @author Thomas Raddatz
 */
public class TableAutoSizeAdapter extends ControlAdapter {

    private final String WEIGHT = TableAutoSizeAdapter.class.getName() + "_WEIGHT";
    private TableViewer tableViewer;
    private int vBarMode = 1;

    private List<TableColumn> resizableTableColumns;
    private int totalColumnsWeight;

    public TableAutoSizeAdapter(TableViewer tableParent) {
        this(tableParent, 0);
    }

    public TableAutoSizeAdapter(TableViewer tableParent, int vBarMode) {

        this.tableViewer = tableParent;
        this.vBarMode = vBarMode;
        this.resizableTableColumns = new ArrayList<TableColumn>();
        this.totalColumnsWeight = 0;
    }

    /**
     * Registers a column that is automatically resized.
     * 
     * @param tableColumn - column that is resized
     * @param weight - weight of the column width
     */
    public void addResizableColumn(TableColumn tableColumn, Integer weight) {

        if (!tableColumn.getResizable()) {
            return;
        }

        resizableTableColumns.add(tableColumn);
        tableColumn.setData(WEIGHT, weight);
        totalColumnsWeight += weight;
    }

    @Override
    public void controlResized(ControlEvent e) {

        Rectangle area = getClientArea();
        Point oldSize = tableViewer.getTable().getSize();
        if (oldSize.x > area.width) {
            // table is getting smaller so make the columns
            // smaller first and then resize the table to
            // match the client area width
            resizeTableColumns();
            tableViewer.getTable().setSize(area.width, area.height);
        } else {
            // table is getting bigger so make the table
            // bigger first and then make the columns wider
            // to match the client area width
            tableViewer.getTable().setSize(area.width, area.height);
            resizeTableColumns();
        }
    }

    /**
     * Resizes a table column when the size of the parent composite changes.
     */
    private void resizeTableColumns() {

        int tableWidth = getTableWidth();
        int fixedColumnsWidth = getTotalWidthOfRemainingColumns();

        int i = 0;
        for (TableColumn tableColumn : resizableTableColumns) {
            if (tableColumn.getResizable()) {
                tableColumn.setWidth((tableWidth - fixedColumnsWidth) / totalColumnsWeight * getColumnWeight(tableColumn));
            }
            i++;
        }
    }

    /**
     * Returns the weight of a given table column.
     * 
     * @param tableColumn - table column whose weight is returned
     * @return column weight
     */
    private int getColumnWeight(TableColumn tableColumn) {

        if (tableColumn.getData(WEIGHT) == null) {
            return 0;
        }

        return (Integer)tableColumn.getData(WEIGHT);
    }

    /**
     * Returns the total width of all columns that are not automatically
     * resized.
     * 
     * @return total width
     */
    private int getTotalWidthOfRemainingColumns() {

        int fixedWidth = 0;
        for (TableColumn tableColumn : tableViewer.getTable().getColumns()) {
            if (tableColumn.getData(WEIGHT) == null) {
                fixedWidth += tableColumn.getWidth();
            }
        }

        return fixedWidth;
    }

    /**
     * Returns the width of the table viewer.
     * 
     * @return width
     */
    private int getTableWidth() {

        Rectangle area = getClientArea();
        ScrollBar vBar = tableViewer.getTable().getVerticalBar();

        int width;
        if (vBarMode == 0) {
            width = area.width - tableViewer.getTable().computeTrim(0, 0, 0, 0).width - vBar.getSize().x;
        } else {
            width = area.width - tableViewer.getTable().computeTrim(0, 0, 0, 0).width;
            if (vBar.isVisible()) {
                // Subtract the scrollbar width from the total column
                // width
                // if a vertical scrollbar will be required
                Point vBarSize = vBar.getSize();
                width -= vBarSize.x;
            }
        }

        return width;
    }

    /**
     * Returns the client area of the parent composite of the table viewer.
     * 
     * @return client area
     */
    private Rectangle getClientArea() {
        return tableViewer.getTable().getParent().getClientArea();
    }
}
