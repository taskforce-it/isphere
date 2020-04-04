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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import biz.isphere.base.swt.events.TableAutoSizeControlListener;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.ui.labelproviders.JournalEntryAppearanceAttributesLabelProvider;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryAppearanceAttributes;

/**
 * This widget is an editor for editing the persisted appearance attributes of a
 * journal entry column.
 * 
 * @see JournalEntryAppearanceAttributes
 * @see JournalEntryAppearanceAttributesLabelProvider
 */
public class JournalEntryAppearanceAttributesEditor extends Composite {

    private static final String COLUMN_NAME = "NAME";
    private static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    private static final String COLUMN_COLOR = "COLOR";
    private static final String[] COLUMN_NAMES = new String[] { COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_COLOR };

    private TableViewer tableViewer;
    private List<JournalEntry> data;
    private Button btnUp;
    private Button btnDown;
    private Button btnClearColors;

    public JournalEntryAppearanceAttributesEditor(Composite parent) {
        super(parent, SWT.NONE);

        this.initializeComponents();
    }

    private void initializeComponents() {

        this.setLayout(new GridLayout(2, false));
        createTableViewer(this);

        setButtonsEnablement(tableViewer.getSelection());
    }

    private void createTableViewer(Composite container) {

        tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.heightHint = 0;
        tableViewer.getControl().setLayoutData(layoutData);

        Table table = tableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        TableColumn columnName = new TableColumn(table, SWT.NONE);
        columnName.setText(Messages.ColumnHeading_ColumnName);
        columnName.setWidth(100);

        TableColumn columnDescription = new TableColumn(table, SWT.BORDER);
        columnDescription.setText(Messages.ColumnHeading_Description);
        columnDescription.setWidth(100);

        TableColumn columnColor = new TableColumn(table, SWT.NONE);
        columnColor.setText(Messages.ColumnHeading_Color);
        columnColor.setWidth(100);

        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                setButtonsEnablement(event.getSelection());
            }
        });

        tableViewer.setColumnProperties(COLUMN_NAMES);
        tableViewer.setLabelProvider(new JournalEntryAppearanceAttributesLabelProvider());
        tableViewer.setContentProvider(new ArrayContentProvider());

        table.getVerticalBar().setEnabled(true);
        table.getHorizontalBar().setEnabled(true);

        TableAutoSizeControlListener tableAutoSizeAdapter = new TableAutoSizeControlListener(table);
        tableAutoSizeAdapter.addResizableColumn(columnDescription, 1);
        tableViewer.getTable().addControlListener(tableAutoSizeAdapter);

        Composite groupButtons = new Composite(container, SWT.NONE);
        groupButtons.setLayout(new GridLayout());
        groupButtons.setLayoutData(new GridData(GridData.FILL, SWT.CENTER, true, true));

        btnUp = WidgetFactory.createPushButton(groupButtons, Messages.Move_up);
        btnUp.setToolTipText(Messages.Move_up_tooltip);
        btnUp.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        btnUp.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                performMoveUp();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        btnDown = WidgetFactory.createPushButton(groupButtons, Messages.Move_down);
        btnDown.setToolTipText(Messages.Move_down_tooltip);
        btnDown.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        btnDown.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                performMoveDown();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        btnClearColors = WidgetFactory.createPushButton(this, Messages.Clear_Colors);
        btnClearColors.setToolTipText(Messages.Clear_Colors_tooltip);
        btnClearColors.setLayoutData(new GridData());
        btnClearColors.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                performClearColors();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        configureEditors(tableViewer, COLUMN_NAMES);
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

                if (COLUMN_COLOR.equals(property)) {
                    TableItem tableItem = (TableItem)element;
                    JournalEntryAppearanceAttributes columnColorEntry = (JournalEntryAppearanceAttributes)tableItem.getData();
                    Color color = ISphereJournalExplorerCorePlugin.getDefault().getColor((RGB)value);
                    columnColorEntry.setColor(color);
                    tableViewer.refresh(columnColorEntry);
                }
            }

            public Object getValue(Object element, String property) {

                if (COLUMN_COLOR.equals(property)) {
                    JournalEntryAppearanceAttributes columnColorEntry = (JournalEntryAppearanceAttributes)element;
                    return columnColorEntry.getColor().getRGB();
                }

                return null;
            }

            public boolean canModify(Object element, String property) {
                return COLUMN_COLOR.equals(property);
            }
        });
    }

    public void setInput(JournalEntryAppearanceAttributes[] columns) {
        tableViewer.setInput(columns);
    }

    public JournalEntryAppearanceAttributes[] getInput() {
        return (JournalEntryAppearanceAttributes[])tableViewer.getInput();
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

    private void performMoveUp() {

        JournalEntryAppearanceAttributes[] items = getInput();

        int index = tableViewer.getTable().getSelectionIndex();
        if (index <= 0) {
            return;
        }

        moveItem(items, index, -1);
    }

    private void performMoveDown() {

        JournalEntryAppearanceAttributes[] items = getInput();

        int index = tableViewer.getTable().getSelectionIndex();
        if (index >= items.length - 1) {
            return;
        }

        moveItem(items, index, 1);
    }

    private void moveItem(JournalEntryAppearanceAttributes[] items, int index, int positions) {

        List<JournalEntryAppearanceAttributes> columns = new LinkedList<JournalEntryAppearanceAttributes>();
        columns.addAll(Arrays.asList(items));
        JournalEntryAppearanceAttributes removedItem = columns.remove(index);
        index = index + positions;
        columns.add(index, removedItem);

        items = columns.toArray(new JournalEntryAppearanceAttributes[columns.size()]);

        setInput(items);
        setButtonsEnablement(tableViewer.getSelection());
    }

    private void setButtonsEnablement(ISelection selection) {

        if (selection == null || selection.isEmpty()) {
            btnUp.setEnabled(false);
            btnDown.setEnabled(false);
        } else {
            btnUp.setEnabled(!isFirstEntryOfList());
            btnDown.setEnabled(!isLastEntryOfList());
        }
    }

    private boolean isFirstEntryOfList() {
        return tableViewer.getTable().getSelectionIndex() <= 0;
    }

    private boolean isLastEntryOfList() {
        return tableViewer.getTable().getSelectionIndex() >= tableViewer.getTable().getItemCount() - 1;
    }

    private void performClearColors() {

        JournalEntryAppearanceAttributes[] columns = getInput();

        for (JournalEntryAppearanceAttributes column : columns) {
            column.setColor(null);
        }

        setInput(columns);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        tableViewer.getControl().setEnabled(enabled);

        if (enabled) {
            setButtonsEnablement(tableViewer.getSelection());
        } else {
            btnUp.setEnabled(false);
            btnDown.setEnabled(false);
        }

        btnClearColors.setEnabled(enabled);
    }

    @Override
    public boolean getEnabled() {
        return super.getEnabled();
    }
}
