/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor.tableviewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.editor.filter.FilterData;
import biz.isphere.joblogexplorer.editor.filter.JobLogExplorerFilterPanelEvents;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.AbstractMessagePropertyFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.IdFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.MasterFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.SeverityFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.TypeFilter;
import biz.isphere.joblogexplorer.model.JobLog;
import biz.isphere.joblogexplorer.model.JobLogMessage;

public class JobLogExplorerTableViewer implements JobLogExplorerTableColumns, SelectionListener {

    public enum Columns {
        SELECTED ("selected", COLUMN_SELECTED), //$NON-NLS-1$
        DATE ("date", COLUMN_DATE), //$NON-NLS-1$
        TIME ("time", COLUMN_TIME), //$NON-NLS-1$
        ID ("id", COLUMN_ID), //$NON-NLS-1$
        TYPE ("type", COLUMN_TYPE), //$NON-NLS-1$
        SEVERITY ("severity", COLUMN_SEVERITY), //$NON-NLS-1$
        TEXT ("text", COLUMN_TEXT), //$NON-NLS-1$
        FROM_LIBRARY ("fromLibrary", COLUMN_FROM_LIBRARY), //$NON-NLS-1$
        FROM_PROGRAM ("fromProgram", COLUMN_FROM_PROGRAM), //$NON-NLS-1$
        FROM_STATEMENT ("fromStatement", COLUMN_FROM_STATEMENT), //$NON-NLS-1$
        TO_LIBRARY ("toLibrary", COLUMN_TO_LIBRARY), //$NON-NLS-1$
        TO_PROGRAM ("toProgram", COLUMN_TO_PROGRAM), //$NON-NLS-1$
        TO_STATEMENT ("toStatement", COLUMN_TO_STATEMENT), //$NON-NLS-1$
        FROM_MODULE ("fromModule", COLUMN_FROM_MODULE), //$NON-NLS-1$
        TO_MODULE ("toModule", COLUMN_TO_MODULE), //$NON-NLS-1$
        FROM_PROCEDURE ("fromProcedure", COLUMN_FROM_PROCEDURE), //$NON-NLS-1$
        TO_PROCEDURE ("toProcedure", COLUMN_TO_PROCEDURE); //$NON-NLS-1$

        public final String name;
        public final int columnNumber;

        private Columns(String name, int columnNumber) {
            this.name = name;
            this.columnNumber = columnNumber;
        }

        public static String[] names() {

            List<String> names = new ArrayList<String>();
            for (Columns column : Columns.values()) {
                names.add(column.name);
            }

            return names.toArray(new String[names.size()]);
        }

    }

    private Table table;
    private TableViewer tableViewer;
    private Composite viewerArea;

    private MasterFilter masterFilter;

    // private List<AbstractFilter> filters;

    public JobLogExplorerTableViewer() {

        this.masterFilter = new MasterFilter();

        // this.filters = new ArrayList<AbstractFilter>();
        // this.filters.add(idFilter);
        // this.filters.add(typeFilter);
        // this.filters.add(severityFilter);
    }

    public boolean isDisposed() {
        return tableViewer.getControl().isDisposed();
    }

    public void setEnabled(boolean enabled) {
        viewerArea.setEnabled(enabled);
    }

    public void setInputData(JobLog jobLog) {

        tableViewer.setInput(jobLog);
    }

    public void setFocus() {
        tableViewer.getTable().setFocus();
    }

    public void setSelection(int index) {

        if (tableViewer.getTable().getItemCount() <= 0) {
            return;
        }

        tableViewer.getTable().setSelection(index);

        /*
         * Ugly hack to enforce a selection changed event
         */
        tableViewer.setSelection(tableViewer.getSelection());
    }

    public void createViewer(Composite parent) {

        // Create main panel
        viewerArea = new Composite(parent, SWT.NONE);
        viewerArea.setLayout(new GridLayout(1, false));
        viewerArea.setLayoutData(new GridData(GridData.FILL_BOTH));

        // Create the table
        createTable(viewerArea);

        // Create and setup the TableViewer
        createTableViewer();
        tableViewer.setContentProvider(new JobLogExplorerContentProvider());
        tableViewer.setLabelProvider(new JobLogExplorerLabelProvider(tableViewer));
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        tableViewer.addSelectionChangedListener(listener);
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        tableViewer.removeSelectionChangedListener(listener);
    }

    /**
     * Create the Table
     */
    private void createTable(Composite parent) {

        int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

        table = new Table(parent, style);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        // 1. column with image/checkboxes - NOTE: The SWT.CENTER has no
        // effect!!
        TableColumn column = new TableColumn(table, SWT.CENTER, Columns.SELECTED.columnNumber);
        column.setText(""); //$NON-NLS-1$
        column.setWidth(WIDTH_SELECTED);

        // 2. column with date sent
        column = new TableColumn(table, SWT.LEFT, Columns.DATE.columnNumber);
        column.setText(Messages.Column_Date_sent);
        column.setWidth(WIDTH_DATE);
        // Add listener to column so tasks are sorted by description when
        // clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.DESCRIPTION));
            }
        });

        // 3. column with time sent
        column = new TableColumn(table, SWT.LEFT, Columns.TIME.columnNumber);
        column.setText(Messages.Column_Time_sent);
        column.setWidth(WIDTH_TIME);
        // Add listener to column so tasks are sorted by owner when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.OWNER));
            }
        });

        // 4. column with message id
        column = new TableColumn(table, SWT.LEFT, Columns.ID.columnNumber);
        column.setText(Messages.Column_ID);
        column.setWidth(WIDTH_ID);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 5. column with message type
        column = new TableColumn(table, SWT.LEFT, Columns.TYPE.columnNumber);
        column.setText(Messages.Column_Type);
        column.setWidth(WIDTH_TYPE);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 6. column with message severity
        column = new TableColumn(table, SWT.CENTER, Columns.SEVERITY.columnNumber);
        column.setText(Messages.Column_Severity);
        column.setWidth(WIDTH_SEVERITY);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 7. column with message text
        column = new TableColumn(table, SWT.LEFT, Columns.TEXT.columnNumber);
        column.setText(Messages.Column_Text);
        column.setWidth(WIDTH_TEXT);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 8. column with from library
        column = new TableColumn(table, SWT.LEFT, Columns.FROM_LIBRARY.columnNumber);
        column.setText(Messages.Column_From_Library);
        column.setWidth(WIDTH_FROM_LIBRARY);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 9. column with from program
        column = new TableColumn(table, SWT.LEFT, Columns.FROM_PROGRAM.columnNumber);
        column.setText(Messages.Column_From_Program);
        column.setWidth(WIDTH_FROM_PROGRAM);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 10. column with from statement
        column = new TableColumn(table, SWT.LEFT, Columns.FROM_STATEMENT.columnNumber);
        column.setText(Messages.Column_From_Stmt);
        column.setWidth(WIDTH_FROM_STATEMENT);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 11. column with to library
        column = new TableColumn(table, SWT.LEFT, Columns.TO_LIBRARY.columnNumber);
        column.setText(Messages.Column_To_Library);
        column.setWidth(WIDTH_TO_LIBRARY);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 12. column with to program
        column = new TableColumn(table, SWT.LEFT, Columns.TO_PROGRAM.columnNumber);
        column.setText(Messages.Column_To_Program);
        column.setWidth(WIDTH_TO_PROGRAM);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 13. column with to statement
        column = new TableColumn(table, SWT.LEFT, Columns.TO_STATEMENT.columnNumber);
        column.setText(Messages.Column_To_Stmt);
        column.setWidth(WIDTH_TO_STATEMENT);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 14. column with from module
        column = new TableColumn(table, SWT.LEFT, Columns.FROM_MODULE.columnNumber);
        column.setText(Messages.Column_From_Module);
        column.setWidth(WIDTH_FROM_MODULE);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 15. column with to module
        column = new TableColumn(table, SWT.LEFT, Columns.TO_MODULE.columnNumber);
        column.setText(Messages.Column_To_Module);
        column.setWidth(WIDTH_TO_MODULE);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 16. column with from module
        column = new TableColumn(table, SWT.LEFT, Columns.FROM_PROCEDURE.columnNumber);
        column.setText(Messages.Column_From_Procedure);
        column.setWidth(WIDTH_FROM_PROCEDURE);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 17. column with to module
        column = new TableColumn(table, SWT.LEFT, Columns.TO_PROCEDURE.columnNumber);
        column.setText(Messages.Column_To_Procedure);
        column.setWidth(WIDTH_TO_PROCEDURE);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });
    }

    /**
     * Create the TableViewer
     */
    private void createTableViewer() {

        tableViewer = new TableViewer(table);
        tableViewer.setUseHashlookup(true);
        tableViewer.setColumnProperties(Columns.names());

        enableEditing(tableViewer);

        // Set the default sorter for the viewer
        // tableViewer.setSorter(new
        // ExampleTaskSorter(ExampleTaskSorter.DESCRIPTION));
    }

    private void enableEditing(TableViewer tableViewer) {

        Table table = tableViewer.getTable();

        // Create the cell editors
        CellEditor[] editors = new CellEditor[Columns.values().length];

        // Column 1 : Completed (Checkbox)
        editors[0] = new CheckboxCellEditor(table);

        // Column 2 : Description (Free text)
        // TextCellEditor textEditor = new TextCellEditor(table);
        // ((Text)textEditor.getControl()).setTextLimit(60);
        // editors[1] = textEditor;

        // Column 3 : Owner (Combo Box)
        // editors[2] = new ComboBoxCellEditor(table, taskList.getOwners(),
        // SWT.READ_ONLY);

        // Column 4 : Percent complete (Text with digits only)
        // textEditor = new TextCellEditor(table);
        // ((Text)textEditor.getControl()).addVerifyListener(
        //
        // new VerifyListener() {
        // public void verifyText(VerifyEvent e) {
        // // Here, we could use a RegExp such as the following
        // // if using JRE1.4 such as e.doit = e.text.matches("[\\-0-9]*");
        // e.doit = "0123456789".indexOf(e.text) >= 0;
        // }
        // });
        // editors[3] = textEditor;

        // Assign the cell editors to the viewer
        tableViewer.setCellEditors(editors);
        // Set the cell modifier for the viewer
        tableViewer.setCellModifier(new JobLogExplorerCellModifier(tableViewer));
    }

    public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {
    }

    public void widgetSelected(SelectionEvent event) {

        switch (event.detail) {
        case JobLogExplorerFilterPanelEvents.APPLY_FILTERS:
            doApplyFilters(event.data);
            break;

        case JobLogExplorerFilterPanelEvents.REMOVE_FILTERS:
            doApplyFilters(null);
            break;

        case JobLogExplorerFilterPanelEvents.SELECT_ALL:
            doSetSelection(true);
            break;

        case JobLogExplorerFilterPanelEvents.DESELECT_ALL:
            doSetSelection(false);
            break;

        default:
            break;
        }

    }

    private void doApplyFilters(Object object) {

        masterFilter.removeAllFilters();

        if (object instanceof FilterData) {
            FilterData filterData = (FilterData)object;
            if (isFilterValue(filterData.id)) {
                masterFilter.addFilter(new IdFilter(filterData.id));
            }
            if (isFilterValue(filterData.type)) {
                masterFilter.addFilter(new TypeFilter(filterData.type));
            }
            if (isFilterValue(filterData.severity)) {
                masterFilter.addFilter(new SeverityFilter(filterData.severity));
            }
        }

        if (masterFilter.countFilters() == 0) {
            tableViewer.removeFilter(masterFilter);
        } else {
            tableViewer.addFilter(masterFilter);
        }
    }

    private void doSetSelection(boolean selected) {

        TableItem[] tableItems = tableViewer.getTable().getItems();
        for (TableItem tableItem : tableItems) {
            JobLogMessage jobLogMessage = (JobLogMessage)tableItem.getData();
            jobLogMessage.setSelected(selected);
        }

        tableViewer.refresh();
    }

    private boolean isFilterValue(String value) {

        if (AbstractMessagePropertyFilter.SPCVAL_ALL.equals(value)) {
            return false;
        }

        return !StringHelper.isNullOrEmpty(value);
    }
}
