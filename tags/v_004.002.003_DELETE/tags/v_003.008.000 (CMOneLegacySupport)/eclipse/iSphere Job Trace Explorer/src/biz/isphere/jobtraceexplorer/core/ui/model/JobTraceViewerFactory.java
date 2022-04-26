/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;
import biz.isphere.jobtraceexplorer.core.ui.contentproviders.JobTraceViewerContentProvider;
import biz.isphere.jobtraceexplorer.core.ui.labelproviders.JobTraceEntryLabelProvider;
import biz.isphere.jobtraceexplorer.core.ui.popupmenus.JobTraceEntryMenuAdapter;

/**
 * This class is an abstract factory for creating viewers for the different
 * output file types of the DSPJRN command.
 */
public class JobTraceViewerFactory {

    // @formatter:off
    private static JobTraceEntryColumnUI[] jobTraceEntryColumns = { 
        JobTraceEntryColumnUI.ID,
        JobTraceEntryColumnUI.NANOS_SINE_STARTED,
        JobTraceEntryColumnUI.TIMESTAMP,
        JobTraceEntryColumnUI.PGM_NAME,
        JobTraceEntryColumnUI.PGM_LIB,
        JobTraceEntryColumnUI.MOD_NAME, 
        JobTraceEntryColumnUI.MOD_LIB, 
        JobTraceEntryColumnUI.HLL_STMT_NBR,
        JobTraceEntryColumnUI.PROC_NAME, 
        JobTraceEntryColumnUI.CALL_LEVEL,
        JobTraceEntryColumnUI.EVENT_SUB_TYPE, 
        JobTraceEntryColumnUI.CALLER_HLL_STMT_NBR, 
        JobTraceEntryColumnUI.CALLER_PROC_NAME, 
        JobTraceEntryColumnUI.CALLER_CALL_LEVEL
    };
    // @formatter:on

    private static final String COLUMN_NAME = "COLUMN_NAME";
    private static final String MOUSE_CURSOR_LOCATION = "MOUSE_CURSOR_LOCATION";

    private Set<String> columnNames;
    private JobTraceEntryColumnUI[] fieldIdMapping;

    public JobTraceViewerFactory() {

        this.columnNames = getColumnNames(new HashSet<JobTraceEntryColumnUI>(Arrays.asList(jobTraceEntryColumns)));
        this.fieldIdMapping = new JobTraceEntryColumnUI[columnNames.size()];
    }

    /**
     * Returns the column index for accessing row data
     * {@link JobTraceEntry#getRow()}. The associated table viewer must have
     * been produced by {@link JobTraceViewerFactory}.
     * 
     * @param tableColumn - table column of a {@link TableViewer} produced in
     *        {@link JobTraceViewerFactory}
     * @return column index
     */
    public static String getColumnName(TableColumn tableColumn) {

        Object columnName = tableColumn.getData(COLUMN_NAME);
        if (columnName instanceof String) {
            return (String)columnName;
        }

        return null;
    }

    public static boolean isColumn(TableColumn tableColumn, JobTraceEntryColumnUI columnUI) {

        String columnName = getColumnName(tableColumn);

        if (JobTraceEntryColumnUI.find(columnName) != null) {
            if (columnUI.columnName().matches(columnName)) {
                return true;
            }
        }

        return false;
    }

    public static MouseCursorLocation getMouseCursorLocation(TableViewer tableViewer) {

        Object object = tableViewer.getTable().getData(MOUSE_CURSOR_LOCATION);
        if (object instanceof MouseCursorLocation) {
            return (MouseCursorLocation)object;
        }

        return null;
    }

    private Set<String> getColumnNames(Set<JobTraceEntryColumnUI> columnNamesEnum) {

        Set<String> names = new HashSet<String>();

        for (JobTraceEntryColumnUI columnsEnum : columnNamesEnum) {
            names.add(columnsEnum.columnName());
        }

        return names;
    }

    public TableViewer createTableViewer(Composite container, DialogSettingsManager dialogSettingsManager) {

        TableViewer tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.READ_ONLY | SWT.VIRTUAL);
        tableViewer.getTable().addListener(SWT.MouseDown, new MouseLocationListener());
        tableViewer.setUseHashlookup(true);
        Table table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        TableColumn newColumn;

        JobTraceEntryColumn[] columns = getAvailableTableColumns();
        List<JobTraceEntryColumn> usedColumns = new LinkedList<JobTraceEntryColumn>();

        int i = 0;
        for (JobTraceEntryColumn column : columns) {
            if (columnNames.contains(column.getName())) {
                fieldIdMapping[i] = column.getColumnDef();
                newColumn = dialogSettingsManager.createResizableTableColumn(table, column.getStyle(), column.getName(), column.getWidth());
                newColumn.setData(COLUMN_NAME, column.getName());
                newColumn.setText(column.getColumnHeading());
                newColumn.setToolTipText(column.getTooltipText());
                newColumn.setMoveable(column.isMovebale());
                usedColumns.add(column);
                i++;
            }
        }

        final Menu menuTableMembers = new Menu(table);
        menuTableMembers.addMenuListener(new JobTraceEntryMenuAdapter(menuTableMembers, tableViewer));
        table.setMenu(menuTableMembers);

        tableViewer
            .setLabelProvider(new JobTraceEntryLabelProvider(fieldIdMapping, usedColumns.toArray(new JobTraceEntryColumn[usedColumns.size()])));
        tableViewer.setContentProvider(new JobTraceViewerContentProvider(tableViewer));

        return tableViewer;
    }

    public static JobTraceEntryColumn[] getAvailableTableColumns() {

        List<JobTraceEntryColumn> columns = new LinkedList<JobTraceEntryColumn>();

        columns.add(new JobTraceEntryColumn(JobTraceEntryColumnUI.ID));
        columns.add(new JobTraceEntryColumn(JobTraceEntryColumnUI.NANOS_SINE_STARTED));
        columns.add(new JobTraceEntryColumn(JobTraceEntryColumnUI.TIMESTAMP));

        // Program module and procedure
        columns.add(new JobTraceEntryColumn(JobTraceEntryColumnUI.PGM_NAME));
        columns.add(new JobTraceEntryColumn(JobTraceEntryColumnUI.PGM_LIB));
        columns.add(new JobTraceEntryColumn(JobTraceEntryColumnUI.MOD_NAME));
        columns.add(new JobTraceEntryColumn(JobTraceEntryColumnUI.MOD_LIB));
        columns.add(new JobTraceEntryColumn(JobTraceEntryColumnUI.HLL_STMT_NBR));
        columns.add(new JobTraceEntryColumn(JobTraceEntryColumnUI.PROC_NAME));

        // Call level and event type
        columns.add(new JobTraceEntryColumn(JobTraceEntryColumnUI.CALL_LEVEL));
        columns.add(new JobTraceEntryColumn(JobTraceEntryColumnUI.EVENT_SUB_TYPE));

        // Caller
        columns.add(new JobTraceEntryColumn(JobTraceEntryColumnUI.CALLER_HLL_STMT_NBR));
        columns.add(new JobTraceEntryColumn(JobTraceEntryColumnUI.CALLER_PROC_NAME));
        columns.add(new JobTraceEntryColumn(JobTraceEntryColumnUI.CALLER_CALL_LEVEL));

        return sortColumnsAndApplyAppearanceAttributes(columns.toArray(new JobTraceEntryColumn[columns.size()]));
    }

    private static JobTraceEntryColumn[] sortColumnsAndApplyAppearanceAttributes(JobTraceEntryColumn[] journalEntryColumns) {

        List<JobTraceEntryColumn> sortedColumns = Arrays.asList(journalEntryColumns);

        return sortedColumns.toArray(new JobTraceEntryColumn[sortedColumns.size()]);
    }

    private class MouseLocationListener implements Listener {
        public void handleEvent(Event event) {
            Point pt = new Point(event.x, event.y);
            Table table = (Table)event.widget;
            TableItem item = table.getItem(pt);
            if (item == null) return;
            for (int i = 0; i < table.getColumnCount(); i++) {
                Rectangle rect = item.getBounds(i);
                if (rect.contains(pt)) {
                    int index = table.indexOf(item);
                    JobTraceEntry jobTraceEntry = (JobTraceEntry)table.getItem(index).getData();
                    String columnName = JobTraceViewerFactory.getColumnName(table.getColumn(i));
                    if (columnName != null) {
                        table.setData(MOUSE_CURSOR_LOCATION, new MouseCursorLocation(jobTraceEntry, columnName, i));
                    } else {
                        table.setData(MOUSE_CURSOR_LOCATION, null);
                    }
                }
            }
        }
    }
}
