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

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * This class automatically resizes the columns of a {@link Tree} object
 * according to the specified column weights. The columns that shall be resized
 * must be register to the TreeAutoSizeControlListener by calling
 * {@link #addResizableColumn(TreeColumn, int)}. The TreeAutoSizeControlListener
 * must added as a {@link ControlListener} to the table of a {@link Tree}
 * object.
 * 
 * @author Thomas Raddatz
 */
public class TreeAutoSizeControlListener extends ControlAdapter {

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

    private static final int MIN_COLUMN_WIDTH = 20;
    private final String WEIGHT = getClass().getName() + "_WEIGHT";
    private final String MIN_WIDTH = getClass().getName() + "_MIN_WIDTH";
    private final String MAX_WIDTH = getClass().getName() + "_MAX_WIDTH";

    private boolean isResizing;
    private Point oldSize;

    private Tree tableParent;
    private int vBarMode;

    private List<TreeColumn> resizableTableColumns;
    private int initialTotalColumnsWeight;

    public TreeAutoSizeControlListener(Tree tableParent) {
        this(tableParent, RESERVE_VBAR_SPACE);
    }

    public TreeAutoSizeControlListener(Tree tableParent, int vBarMode) {

        this.isResizing = false;
        this.oldSize = new Point(0, 0);
        this.vBarMode = vBarMode;
        this.initialTotalColumnsWeight = 0;

        this.tableParent = tableParent;
        this.resizableTableColumns = new ArrayList<TreeColumn>();
    }

    /**
     * Registers a column that is automatically resized.
     * 
     * @param column - column that is resized
     * @param weight - weight of the column width
     */
    public void addResizableColumn(TreeColumn column, int weight) {
        addResizableColumn(column, weight, MIN_COLUMN_WIDTH);
    }

    /**
     * Registers a column that is automatically resized.
     * 
     * @param column - column that is resized
     * @param weight - weight of the column width
     * @param minWidth - minimum width of the column. Defaults to 20.
     */
    public void addResizableColumn(TreeColumn column, int weight, int minWidth) {
        addResizableColumn(column, weight, minWidth, -1);
    }

    /**
     * Registers a column that is automatically resized.
     * 
     * @param column - column that is resized
     * @param weight - weight of the column width
     * @param minWidth - minimum width of the column. Defaults to 20.
     */
    public void addResizableColumn(TreeColumn column, int weight, int minWidth, int maxWidth) {

        if (!column.getResizable()) {
            return;
        }

        resizableTableColumns.add(column);
        column.setData(WEIGHT, new Integer(weight));
        column.setData(MIN_WIDTH, new Integer(minWidth));
        if (maxWidth > 0) {
            column.setData(MAX_WIDTH, new Integer(maxWidth));
        }
        initialTotalColumnsWeight += weight;
    }

    /**
     * Callback method that is called by the framework on 'resize' events
     * triggered by the parent control.
     */
    @Override
    public void controlResized(ControlEvent e) {

        if (isResizing) {
            return;
        }

        try {

            isResizing = true;

            Point area = getClientArea();
            if (oldSize.x > area.x) {
                // table is getting smaller so make the columns
                // smaller first and then resize the table to
                // match the client area width
                resizeTableColumns();
                tableParent.setSize(area.x, area.y);
            } else if (oldSize.x < area.x) {
                // table is getting bigger so make the table
                // bigger first and then make the columns wider
                // to match the client area width
                tableParent.setSize(area.x, area.y);
                resizeTableColumns();
            }

            oldSize.x = area.x;
            oldSize.y = area.y;

        } finally {
            isResizing = false;
        }
    }

    /**
     * Resizes a column when the size of the parent composite changes.
     */
    private void resizeTableColumns() {

        int clientWidth = getClientWidth();
        int fixedColumnsWidth = getTotalWidthOfRemainingColumns();
        int totalColumnsWeight = initialTotalColumnsWeight;

        // Resize columns with a maximum size restriction
        int i = 0;
        for (TreeColumn column : resizableTableColumns) {
            if (column.getResizable() && hasMaxSizeRestriction(column)) {
                int minColumnWidth = getMinColumnWidth(column);
                int maxColumnWidth = getMaxColumnWidth(column);
                int newColumnWidth = (clientWidth - fixedColumnsWidth) / initialTotalColumnsWeight * getColumnWeight(column);
                if (newColumnWidth > maxColumnWidth) {
                    // Transform column to a fixed-length column
                    totalColumnsWeight -= getColumnWeight(column);
                    fixedColumnsWidth += maxColumnWidth;
                    newColumnWidth = maxColumnWidth;
                } else if (newColumnWidth < minColumnWidth) {
                    newColumnWidth = minColumnWidth;
                }

                column.setWidth(newColumnWidth);
            }
            i++;
        }

        // Resize columns without a maximum size restriction
        i = 0;
        for (TreeColumn column : resizableTableColumns) {
            if (column.getResizable() && !hasMaxSizeRestriction(column)) {
                int minColumnWidth = getMinColumnWidth(column);
                int newColumnWidth = (clientWidth - fixedColumnsWidth) / totalColumnsWeight * getColumnWeight(column);
                if (newColumnWidth < minColumnWidth) {
                    newColumnWidth = minColumnWidth;
                }
                column.setWidth(newColumnWidth);
            }
            i++;
        }
    }

    private boolean hasMaxSizeRestriction(TreeColumn column) {

        int maxColumnWidth = getMaxColumnWidth(column);
        if (maxColumnWidth > 0) {
            return true;
        }

        return false;
    }

    /**
     * Returns the weight of a given column.
     * 
     * @param column - table column whose weight is returned
     * @return column weight
     */
    private int getColumnWeight(TreeColumn column) {

        if (column.getData(WEIGHT) == null) {
            return 0;
        }

        return ((Integer)column.getData(WEIGHT)).intValue();
    }

    /**
     * Returns the minimum width of a given column.
     * 
     * @param column - table column whose minimum width is returned
     * @return column weight
     */
    private int getMinColumnWidth(TreeColumn column) {

        if (column.getData(MIN_WIDTH) == null) {
            return MIN_COLUMN_WIDTH;
        }

        return ((Integer)column.getData(MIN_WIDTH)).intValue();
    }

    /**
     * Returns the maximum width of a given column.
     * 
     * @param column - table column whose maximum width is returned
     * @return column weight
     */
    private int getMaxColumnWidth(TreeColumn column) {

        if (column.getData(MAX_WIDTH) == null) {
            return -1;
        }

        return ((Integer)column.getData(MAX_WIDTH)).intValue();
    }

    /**
     * Returns the total width of all columns that are not automatically
     * resized.
     * 
     * @return total width
     */
    private int getTotalWidthOfRemainingColumns() {

        int fixedWidth = 0;
        for (TreeColumn column : tableParent.getColumns()) {
            if (column.getData(WEIGHT) == null) {
                fixedWidth += column.getWidth();
            }
        }

        return fixedWidth;
    }

    /**
     * Returns the width of the parent control.
     * 
     * @return width
     */
    private int getClientWidth() {

        Point area = getClientArea();

        ScrollBar vBar = tableParent.getVerticalBar();

        int vBarSize;
        boolean isVisible;
        if (vBar == null) {
            vBarSize = 0;
            isVisible = false;
        } else {
            vBarSize = vBar.getSize().x;
            isVisible = vBar.isVisible();
        }

        int width = tableParent.getClientArea().width;
        if (vBarMode == RESERVE_VBAR_SPACE) {
            width = area.x - tableParent.computeTrim(0, 0, 0, 0).width;
        } else {
            width = area.x - tableParent.computeTrim(0, 0, 0, 0).width;
            if (!isVisible) {
                width += vBarSize;
            }
        }

        return width;
    }

    /**
     * Returns the client area of the parent control.
     * 
     * @return client area
     */
    private Point getClientArea() {
        return tableParent.getSize();
    }
}
