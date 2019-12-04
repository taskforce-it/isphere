/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.medfoster.sqljep.ParseException;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.editor.IJobLogExplorerStatusChangedListener;
import biz.isphere.joblogexplorer.editor.JobLogExplorerStatusChangedEvent;
import biz.isphere.joblogexplorer.editor.filter.FilterData;
import biz.isphere.joblogexplorer.editor.filter.JobLogExplorerFilterPanelEvents;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.AbstractMessagePropertyFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.FromLibraryFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.FromProgramFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.FromStatementFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.IdFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.MasterFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.NativeSQLFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.SeverityFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.TextFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.ToLibraryFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.ToProgramFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.ToStatementFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.TypeFilter;
import biz.isphere.joblogexplorer.model.JobLog;
import biz.isphere.joblogexplorer.model.JobLogMessage;

public class JobLogExplorerTableViewer implements SelectionListener, ISelectionProvider {

    private static final String NEGATED_MARKER = AbstractMessagePropertyFilter.NEGATED_MARKER;

    private Table table;
    private TableViewer tableViewer;
    private Composite viewerArea;

    private MasterFilter masterFilter;

    private List<IJobLogExplorerStatusChangedListener> statusChangedListeners;
    private DialogSettingsManager dialogSettingsManager;

    public JobLogExplorerTableViewer(DialogSettingsManager dialogSettingsManager) {

        this.dialogSettingsManager = dialogSettingsManager;
        this.statusChangedListeners = new ArrayList<IJobLogExplorerStatusChangedListener>();
    }

    public boolean isDisposed() {
        return tableViewer.getControl().isDisposed();
    }

    public void setEnabled(boolean enabled) {
        viewerArea.setEnabled(enabled);
    }

    public boolean hasInputData() {
        if (tableViewer != null && tableViewer.getInput() != null) {
            return true;
        }
        return false;
    }

    public void setInputData(JobLog jobLog) {
        tableViewer.setInput(jobLog);
    }

    public JobLog getInputData() {
        return (JobLog)tableViewer.getInput();
    }

    public JobLogMessage[] getItems() {

        List<JobLogMessage> messages = new ArrayList<JobLogMessage>();

        TableItem[] items = tableViewer.getTable().getItems();
        for (TableItem item : items) {
            JobLogMessage message = (JobLogMessage)item.getData();
            messages.add(message);
        }

        return messages.toArray(new JobLogMessage[messages.size()]);
    }

    public int getItemCount() {
        return tableViewer.getTable().getItemCount();
    }

    private JobLog getInput() {
        return (JobLog)tableViewer.getInput();
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

    public void resetColumnSize() {
        dialogSettingsManager.resetColumnWidths(tableViewer.getTable());
    }

    public void createViewer(Composite parent) {

        // Create main panel
        viewerArea = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        viewerArea.setLayout(gridLayout);
        viewerArea.setLayoutData(new GridData(GridData.FILL_BOTH));

        // Create the table
        createTable(viewerArea);

        // Create and setup the TableViewer
        createTableViewer();
        tableViewer.setContentProvider(new JobLogExplorerContentProvider());
        tableViewer.setLabelProvider(new JobLogExplorerLabelProvider(tableViewer));
    }

    public void addMessageSelectionChangedListener(ISelectionChangedListener listener) {
        tableViewer.addSelectionChangedListener(listener);
    }

    public void removeMessageSelectionChangedListener(ISelectionChangedListener listener) {
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

        TableColumn column;

        // 1. column with image/checkboxes - NOTE: The SWT.CENTER has no
        // effect!!
        createColumn(table, Columns.SELECTED, "", SWT.CENTER); //$NON-NLS-1$

        // 2. column with date sent
        column = createColumn(table, Columns.DATE, Messages.Column_Date_sent);
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
        column = createColumn(table, Columns.TIME, Messages.Column_Time_sent);
        // Add listener to column so tasks are sorted by owner when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.OWNER));
            }
        });

        // 4. column with message id
        column = createColumn(table, Columns.ID, Messages.Column_ID);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 5. column with message type
        column = createColumn(table, Columns.TYPE, Messages.Column_Type);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 6. column with message severity
        column = createColumn(table, Columns.SEVERITY, Messages.Column_Severity, SWT.CENTER);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 7. column with message text
        column = createColumn(table, Columns.TEXT, Messages.Column_Text);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 8. column with from library
        column = createColumn(table, Columns.FROM_LIBRARY, Messages.Column_From_Library);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 9. column with from program
        column = createColumn(table, Columns.FROM_PROGRAM, Messages.Column_From_Program);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 10. column with from statement
        column = createColumn(table, Columns.FROM_STATEMENT, Messages.Column_From_Stmt);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 11. column with to library
        column = createColumn(table, Columns.TO_LIBRARY, Messages.Column_To_Library);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 12. column with to program
        column = createColumn(table, Columns.TO_PROGRAM, Messages.Column_To_Program);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 13. column with to statement
        column = createColumn(table, Columns.TO_STATEMENT, Messages.Column_To_Stmt);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 14. column with from module
        column = createColumn(table, Columns.FROM_MODULE, Messages.Column_From_Module);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 15. column with to module
        column = createColumn(table, Columns.TO_MODULE, Messages.Column_To_Module);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 16. column with from module
        column = createColumn(table, Columns.FROM_PROCEDURE, Messages.Column_From_Procedure);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 17. column with to procedure
        column = createColumn(table, Columns.TO_PROCEDURE, Messages.Column_To_Procedure);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });
    }

    private TableColumn createColumn(Table table, Columns column, String text) {
        return createColumn(table, column, text, SWT.LEFT);
    }

    private TableColumn createColumn(Table table, Columns column, String text, int style) {

        TableColumn tableColumn = dialogSettingsManager.createResizableTableColumn(table, style, column.index, column.name, column.width);
        tableColumn.setText(text);

        return tableColumn;
    }

    /**
     * Create the TableViewer
     */
    private void createTableViewer() {

        tableViewer = new TableViewer(table);
        tableViewer.setUseHashlookup(true);
        tableViewer.setColumnProperties(Columns.names());

        enableEditing(tableViewer);
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

        case JobLogExplorerFilterPanelEvents.SEARCH_UP:
            doSearchUp(event.text);
            break;

        case JobLogExplorerFilterPanelEvents.SEARCH_DOWN:
            doSearchDown(event.text);
            break;

        default:
            break;
        }

    }

    private void doApplyFilters(Object object) {

        try {

            if (object == null) {
                if (masterFilter != null) {
                    tableViewer.removeFilter(masterFilter);
                    masterFilter = null;
                }
                return;
            }

            boolean updateMasterAddFilter;
            if (masterFilter == null) {
                updateMasterAddFilter = false;
                masterFilter = new MasterFilter();
            } else {
                updateMasterAddFilter = true;
                masterFilter.removeAllFilters();
            }

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
                if (isFilterValue(filterData.fromLibrary)) {
                    masterFilter.addFilter(new FromLibraryFilter(filterData.fromLibrary));
                }
                if (isFilterValue(filterData.fromProgram)) {
                    masterFilter.addFilter(new FromProgramFilter(filterData.fromProgram));
                }
                if (isFilterValue(filterData.fromStmt)) {
                    masterFilter.addFilter(new FromStatementFilter(filterData.fromStmt));
                }
                if (isFilterValue(filterData.toLibrary)) {
                    masterFilter.addFilter(new ToLibraryFilter(filterData.toLibrary));
                }
                if (isFilterValue(filterData.toProgram)) {
                    masterFilter.addFilter(new ToProgramFilter(filterData.toProgram));
                }
                if (isFilterValue(filterData.toStmt)) {
                    masterFilter.addFilter(new ToStatementFilter(filterData.toStmt));
                }
                if (isFilterValue(filterData.text)) {
                    masterFilter.addFilter(new TextFilter(filterData.text));
                }
                if (isFilterValue(filterData.whereClause)) {
                    masterFilter.addFilter(new NativeSQLFilter(filterData.whereClause));
                }
            }

            if (masterFilter.countFilters() == 0 && !haveSelectedMessages()) {
                tableViewer.removeFilter(masterFilter);
                masterFilter = null;
            } else {
                if (updateMasterAddFilter) {
                    tableViewer.refresh();
                } else {
                    tableViewer.addFilter(masterFilter);
                }
                if (getItemCount() > 0) {
                    if (tableViewer.getTable().getSelection().length == 0) {
                        setSelection(0);
                    }
                }
            }
        } catch (ParseException e) {
            MessageDialogAsync.displayError(e.getLocalizedMessage());
        } finally {
            notifyStatusChangedListeners(JobLogExplorerStatusChangedEvent.EventType.FILTER_CHANGED);
        }
    }

    private boolean haveSelectedMessages() {

        JobLog jobLog = getInput();
        if (jobLog == null) {
            return false;
        }

        return jobLog.haveSelectedMessages();
    }

    public int getTotalNumberOfMessages() {

        JobLog jobLog = getInput();
        if (jobLog == null) {
            return 0;
        }

        return jobLog.getMessages().size();
    }

    public int getNumberOfDisplayedMessages() {

        return getItemCount();
    }

    private void doSetSelection(boolean selected) {

        TableItem[] tableItems = tableViewer.getTable().getItems();
        for (TableItem tableItem : tableItems) {
            JobLogMessage jobLogMessage = (JobLogMessage)tableItem.getData();
            jobLogMessage.setSelected(selected);
        }

        tableViewer.refresh();
    }

    private void doSearchUp(String text) {
        doSearchUp(text, table.getSelectionIndex(), 0);
    }

    private void doSearchUp(String text, int startIndex, int minIndex) {

        Table table = tableViewer.getTable();
        if (table == null || table.getItemCount() <= 0) {
            return;
        }

        boolean isNegated = false;
        String searchArg = text.toLowerCase();
        if (searchArg.startsWith(NEGATED_MARKER)) { // $NON-NLS-1$
            searchArg = searchArg.substring(1);
            isNegated = true;
        }

        int currentIndex = startIndex;
        if (currentIndex < 0) {
            currentIndex = table.getItemCount();
        }

        currentIndex--;

        while (currentIndex >= minIndex) {
            JobLogMessage jobLogMessage = (JobLogMessage)table.getItem(currentIndex).getData();
            if (isMatch(isNegated, searchArg, jobLogMessage)) {
                table.setSelection(currentIndex);
                return; // Found!
            }
            currentIndex--;
        }

        Display.getCurrent().beep();

        if (startIndex < table.getItemCount() - 1) {
            doSearchUp(text, table.getItemCount(), startIndex);
        }
    }

    private void doSearchDown(String text) {
        doSearchDown(text, table.getSelectionIndex(), table.getItemCount() - 1);
    }

    private void doSearchDown(String text, int startIndex, int maxIndex) {

        Table table = tableViewer.getTable();
        if (table == null || table.getItemCount() <= 0) {
            return;
        }

        boolean isNegated = false;
        String searchArg = text.toLowerCase();
        if (searchArg.startsWith(NEGATED_MARKER)) { // $NON-NLS-1$
            searchArg = searchArg.substring(1);
            isNegated = true;
        }

        int currentIndex = startIndex;
        if (currentIndex > maxIndex) {
            currentIndex = -1;
        }

        currentIndex++;

        while (currentIndex <= maxIndex) {
            JobLogMessage jobLogMessage = (JobLogMessage)table.getItem(currentIndex).getData();
            if (isMatch(isNegated, searchArg, jobLogMessage)) {
                table.setSelection(currentIndex);
                return; // Found!
            }
            currentIndex++;
        }

        Display.getCurrent().beep();

        if (startIndex > 0) {
            doSearchDown(text, -1, startIndex);
        }
    }

    private boolean isMatch(boolean isNegated, String searchArg, JobLogMessage jobLogMessage) {

        boolean isFound = jobLogMessage.getLowerCaseText().indexOf(searchArg) >= 0;
        if (isNegated) {
            isFound = !isFound;
        }

        return isFound;
    }

    private boolean isFilterValue(String value) {

        if (AbstractMessagePropertyFilter.UI_SPCVAL_ALL.equals(value)) {
            return false;
        }

        return !StringHelper.isNullOrEmpty(value);
    }

    public void addSelectionChangedListener(ISelectionChangedListener arg0) {
        return;
    }

    public ISelection getSelection() {
        if (tableViewer.getInput() == null) {
            return null;
        }
        return new StructuredSelection(tableViewer.getInput());
    }

    public void removeSelectionChangedListener(ISelectionChangedListener arg0) {
        return;
    }

    public void setSelection(ISelection arg0) {
        return;
    }

    public void addStatusChangedListener(IJobLogExplorerStatusChangedListener listener) {

        statusChangedListeners.add(listener);
    }

    public void removeStatusChangedListener(IJobLogExplorerStatusChangedListener listener) {

        statusChangedListeners.remove(listener);
    }

    private void notifyStatusChangedListeners(JobLogExplorerStatusChangedEvent.EventType eventType) {

        JobLogExplorerStatusChangedEvent data = new JobLogExplorerStatusChangedEvent(eventType, null);
        data.setNumberOfMessages(getTotalNumberOfMessages());
        data.setNumberOfMessagesSelected(getNumberOfDisplayedMessages());

        for (IJobLogExplorerStatusChangedListener listener : statusChangedListeners) {
            listener.statusChanged(data);
        }
    }
}
