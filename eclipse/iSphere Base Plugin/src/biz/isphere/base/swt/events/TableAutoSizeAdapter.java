/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
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
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This class automatically resizes the columns of a {@link TableViewer} object
 * according to the specified column weights. The columns that shall be resized
 * must be register to the TableAutoSizeAdapter by calling
 * {@link #addResizableColumn(TableColumn, Integer)}. The TableAutoSizeAdapter
 * must added as a <i>Control Listener</i> to the table of a <i>TableViewer</i>.
 * 
 * @author Thomas Raddatz
 */
public class TableAutoSizeAdapter extends ControlAdapter {

    /**
     * Reserves the space for a vertical scroll bar, keeping the column sizes
     * stable, when the scroll bar is displayed.
     */
    public static final int RESERVE_VBAR_SPACE = 0;

    /**
     * Always uses all available space for the columns. The columns get smaller,
     * when the scroll bar is displayed.
     */
    public static final int USE_FULL_WIDTH = 1;

    private boolean isResizing;

    private final String WEIGHT = TableAutoSizeAdapter.class.getName() + "_WEIGHT";
    private TableViewer tableViewer;
    private int vBarMode;

    private List<TableColumn> resizableTableColumns;
    private int totalColumnsWeight;

    public TableAutoSizeAdapter(TableViewer tableParent) {
        this(tableParent, RESERVE_VBAR_SPACE);
    }

    public TableAutoSizeAdapter(TableViewer tableParent, int vBarMode) {

        this.isResizing = false;

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

        if (isResizing) {
            return;
        }

        try {

            isResizing = true;

            Point area = getClientArea();
            Point oldSize = tableViewer.getTable().getSize();
            if (oldSize.x > area.x) {
                // table is getting smaller so make the columns
                // smaller first and then resize the table to
                // match the client area width
                resizeTableColumns();
                tableViewer.getTable().setSize(area.x, area.y);
            } else {
                // table is getting bigger so make the table
                // bigger first and then make the columns wider
                // to match the client area width
                tableViewer.getTable().setSize(area.x, area.y);
                resizeTableColumns();
            }

        } finally {
            isResizing = false;
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

        Point area = getClientArea();

        ScrollBar vBar = tableViewer.getTable().getVerticalBar();

        int vBarSize;
        boolean isVisible;
        if (vBar == null) {
            vBarSize = 0;
            isVisible = false;
        } else {
            vBarSize = vBar.getSize().x;
            isVisible = vBar.isVisible();
        }

        int width = tableViewer.getTable().getClientArea().width;
        if (vBarMode == RESERVE_VBAR_SPACE) {
            width = area.x - tableViewer.getTable().computeTrim(0, 0, 0, 0).width;
        } else {
            width = area.x - tableViewer.getTable().computeTrim(0, 0, 0, 0).width;
            if (!isVisible) {
                width += vBarSize;
            }
        }

        return width;
    }

    /**
     * Returns the client area of the parent composite of the table viewer.
     * 
     * @return client area
     */
    private Point getClientArea() {
        return tableViewer.getTable().getSize();
    }
}
