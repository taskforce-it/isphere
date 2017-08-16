/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.widgets;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.ui.labelproviders.JournalEntryAppearanceLabelProvider;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumn;

public class JournalEntryAppearanceEditor extends Composite {

    // private Composite container;
    private TableViewer tableViewer;
    private List<JournalEntry> data;

    public JournalEntryAppearanceEditor(Composite parent) {
        super(parent, SWT.NONE);

        this.initializeComponents();
    }

    private void initializeComponents() {

        this.setLayout(new GridLayout(1, true));
        createTableViewer(this);
    }

    private void createTableViewer(Composite container) {

        tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.heightHint = 0;
        tableViewer.getControl().setLayoutData(layoutData);

        Table table = tableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        TableColumn newColumn;

        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setText("Column");
        newColumn.setWidth(100);

        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setText("Description");
        newColumn.setWidth(100);

        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setText("Color");
        newColumn.setWidth(100);

        tableViewer.setLabelProvider(new JournalEntryAppearanceLabelProvider());
        tableViewer.setContentProvider(new ArrayContentProvider());

        String[] columnNames = new String[] { "NAME", "DESCRIPTION", "SELECT_COLOR" };
        tableViewer.setColumnProperties(columnNames);

        table.getVerticalBar().setEnabled(true);
        table.getHorizontalBar().setEnabled(true);

        configureEditors(tableViewer, columnNames);
    }

    private void configureEditors(final TableViewer tableViewer, final String[] columnNames) {

        Table table = tableViewer.getTable();

        // Create the cell editors
        final CellEditor[] editors = new CellEditor[columnNames.length];

        // Column 1 : Column Name
        editors[0] = null;

        // Column 2 : Column Description
        editors[1] = null;

        // Column 3 : Change Color Button
        editors[2] = new ColorCellEditor(table);

        // Assign the cell editors to the viewer
        tableViewer.setCellEditors(editors);

        // Set the cell modifier for the viewer
        tableViewer.setCellModifier(new ICellModifier() {

            public void modify(Object element, String property, Object value) {

                if ("SELECT_COLOR".equals(property)) {
                    TableItem tableItem = (TableItem)element;
                    JournalEntryColumn columnColorEntry = (JournalEntryColumn)tableItem.getData();
                    Color color = ISphereJournalExplorerCorePlugin.getDefault().getColor((RGB)value);
                    columnColorEntry.setColor(color);
                    tableViewer.refresh(columnColorEntry);
                }
            }

            public Object getValue(Object element, String property) {

                if ("SELECT_COLOR".equals(property)) {
                    JournalEntryColumn columnColorEntry = (JournalEntryColumn)element;
                    return columnColorEntry.getColor().getRGB();
                }

                return null;
            }

            public boolean canModify(Object element, String property) {

                int index = getColumnIndex(property);
                if (index < 0 || index > editors.length - 1) {
                    return false;
                }

                return editors[index] != null;
            }

            private int getColumnIndex(String property) {

                for (int i = 0; i < columnNames.length; i++) {
                    if (property.equals(columnNames[i])) {
                        return i;
                    }

                }

                return -1;
            }
        });
    }

    public void setInput(JournalEntryColumn[] columns) {
        tableViewer.setInput(columns);
    }

    public JournalEntryColumn[] getInput() {
        return (JournalEntryColumn[])tableViewer.getInput();
    }

    @Override
    public void dispose() {

        if (data != null) {
            data.clear();
            data = null;
        }

        if (tableViewer != null) {
            tableViewer.getTable().dispose();
            tableViewer = null;
        }

        super.dispose();
    }

    public TableViewer getTableViewer() {
        return tableViewer;
    }

    public void refreshTable() {
        if (tableViewer != null) {
            tableViewer.refresh(true);
        }

    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        tableViewer.getControl().setEnabled(enabled);
    }

    @Override
    public boolean getEnabled() {
        return super.getEnabled();
    }
}
