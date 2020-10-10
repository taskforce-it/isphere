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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.base.internal.DialogSettingsManager;
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
        JobTraceEntryColumnUI.MODULE_NAME, 
        JobTraceEntryColumnUI.HLL_STMT_NBR,
        JobTraceEntryColumnUI.PROC_NAME, 
        JobTraceEntryColumnUI.CALL_LEVEL,
        JobTraceEntryColumnUI.EVENT_SUB_TYPE, 
        JobTraceEntryColumnUI.CALLER_HLL_STMT_NBR, 
        JobTraceEntryColumnUI.CALLER_PROC_NAME, 
        JobTraceEntryColumnUI.CALLER_CALL_LEVEL
    };
    // @formatter:on

    private static final String NAME = "NAME";
    private static final String DEFAULT_WIDTH = "DEFAULT_WIDTH";

    private Set<String> columnNames;
    private JobTraceEntryColumnUI[] fieldIdMapping;

    public JobTraceViewerFactory() {

        this.columnNames = getColumnNames(new HashSet<JobTraceEntryColumnUI>(Arrays.asList(jobTraceEntryColumns)));
        this.fieldIdMapping = new JobTraceEntryColumnUI[columnNames.size()];
    }

    public static String getColumnName(TableColumn column) {

        if (column == null) {
            return null;
        }

        String name = (String)column.getData(NAME);
        if (name == null) {
            return null;
        }

        return name;
    }

    public static int getDefaultColumnSize(TableColumn column) {

        if (column == null) {
            return -1;
        }

        Integer width = (Integer)column.getData(DEFAULT_WIDTH);
        if (width == null) {
            return -1;
        }

        return width.intValue();
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
        columns.add(new JobTraceEntryColumn(JobTraceEntryColumnUI.MODULE_NAME));
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
}
