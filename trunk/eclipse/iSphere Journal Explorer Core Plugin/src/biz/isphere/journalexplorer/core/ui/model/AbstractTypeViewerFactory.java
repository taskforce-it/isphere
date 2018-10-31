/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.contentproviders.JournalViewerContentProvider;
import biz.isphere.journalexplorer.core.ui.labelproviders.JournalEntryLabelProvider;
import biz.isphere.journalexplorer.core.ui.popupmenus.JournalEntryMenuAdapter;

/**
 * This class is an abstract factory for creating viewers for the different
 * output file types of the DSPJRN command.
 */
public abstract class AbstractTypeViewerFactory {

    private static final String NAME = "NAME";
    private static final String DEFAULT_WIDTH = "DEFAULT_WIDTH";

    private Set<String> columnNames;
    private JournalEntryColumnUI[] fieldIdMapping;

    public AbstractTypeViewerFactory() {
        this(null);
    }

    public AbstractTypeViewerFactory(Set<JournalEntryColumnUI> columnNames) {
        if (columnNames != null) {
            this.columnNames = getColumnNames(columnNames);
            this.fieldIdMapping = new JournalEntryColumnUI[columnNames.size()];
        } else {
            this.columnNames = null;
            this.fieldIdMapping = null;
        }
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

    private Set<String> getColumnNames(Set<JournalEntryColumnUI> columnNamesEnum) {

        Set<String> names = new HashSet<String>();

        for (JournalEntryColumnUI columnsEnum : columnNamesEnum) {
            names.add(columnsEnum.columnName());
        }

        return names;
    }

    public TableViewer createTableViewer(Composite container) {

        TableViewer tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.READ_ONLY | SWT.VIRTUAL);
        Table table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        TableColumn newColumn;

        JournalEntryColumn[] columns = getAvailableTableColumns();
        List<JournalEntryColumn> usedColumns = new LinkedList<JournalEntryColumn>();

        int i = 0;
        for (JournalEntryColumn column : columns) {
            if (columnNames.contains(column.getName())) {
                fieldIdMapping[i] = column.getColumnDef();
                newColumn = new TableColumn(table, column.getStyle());
                newColumn.setText(column.getColumnHeading());
                newColumn.setToolTipText(column.getTooltipText());
                newColumn.setWidth(column.getWidth());
                newColumn.setResizable(column.isResizable());
                newColumn.setMoveable(column.isMovebale());
                newColumn.setData(NAME, column.getName());
                newColumn.setData(DEFAULT_WIDTH, column.getWidth());
                usedColumns.add(column);
                i++;
            }
        }

        final Menu menuTableMembers = new Menu(table);
        menuTableMembers.addMenuListener(new JournalEntryMenuAdapter(menuTableMembers, tableViewer));
        table.setMenu(menuTableMembers);

        tableViewer.setLabelProvider(new JournalEntryLabelProvider(fieldIdMapping, usedColumns.toArray(new JournalEntryColumn[usedColumns.size()])));
        tableViewer.setContentProvider(new JournalViewerContentProvider(tableViewer));

        return tableViewer;
    }

    public static JournalEntryColumn[] getAvailableTableColumns() {

        List<JournalEntryColumn> columns = new LinkedList<JournalEntryColumn>();

        columns
            .add(new JournalEntryColumn(JournalEntryColumnUI.ID, Messages.ColLabel_OutputFile_Rrn, Messages.Tooltip_OutputFile_Rrn, 45, SWT.RIGHT));

        // Entry seq#, code, type, ...
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOENTL, Messages.Tooltip_JOENTL, 60));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOSEQN, Messages.Tooltip_JOSEQN, 140));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOCODE, Messages.Tooltip_JOCODE, 60));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOENTT, Messages.Tooltip_JOENTT, 60));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JODATE, Messages.Tooltip_JODATE, 80));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOTIME, Messages.Tooltip_JOTIME, 80));

        // Job, that added the journal entry ...
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOJOB, Messages.Tooltip_JOJOB, 90));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOUSER, Messages.Tooltip_JOUSER, 90));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JONBR, Messages.Tooltip_JONBR, 90));
        // .. extended attributes
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOUSPF, Messages.Tooltip_JOUSPF, 90));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOTHDX, Messages.Tooltip_JOTHDX, 120));

        // Program, that added the journal entry
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOPGMLIB, Messages.Tooltip_JOPGMLIB, 90));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOPGM, Messages.Tooltip_JOPGM, 90));
        // .. extended attributes
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOPGMDEV, Messages.Tooltip_JOPGMDEV, 90));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOPGMASP, Messages.Tooltip_JOPGMASP, 90));

        // Object that was changed
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOLIB, Messages.Tooltip_JOLIB, 90));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOOBJ, Messages.Tooltip_JOOBJ, 90));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOMBR, Messages.Tooltip_JOMBR, 90));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOOBJTYP, Messages.Tooltip_JOOBJTYP, 70));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOFILTYP, Messages.Tooltip_JOFILTYP, 70));

        // System that the object resides on
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOSYNM, Messages.Tooltip_JOSYNM, 90));

        // Journal entry flags
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOCTRR, Messages.Tooltip_JOCTRR, 140));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOFLAG, Messages.Tooltip_JOFLAG, 60));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOCCID, Messages.Tooltip_JOCCID, 140));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOJID, Messages.Tooltip_JOJID, 150));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JORCST, Messages.Tooltip_JORCST, 60));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOTGR, Messages.Tooltip_JOTGR, 60));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOINCDAT, Messages.Tooltip_JOINCDAT, 60));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOIGNAPY, Messages.Tooltip_JOIGNAPY, 60));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOMINESD, Messages.Tooltip_JOMINESD, 60));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOOBJIND, Messages.Tooltip_JOOBJIND, 80));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOSYSSEQ, Messages.Tooltip_JOSYSSEQ, 140));

        // Journal receiver
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JORCV, Messages.Tooltip_JORCV, 90));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JORCVLIB, Messages.Tooltip_JORCVLIB, 90));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JORCVDEV, Messages.Tooltip_JORCVDEV, 90));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JORCVASP, Messages.Tooltip_JORCVASP, 70));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOARM, Messages.Tooltip_JOARM, 60));

        // Remote address
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOADF, Messages.Tooltip_JOADF, 60));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JORPORT, Messages.Tooltip_JORPORT, 70));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JORADR, Messages.Tooltip_JORADR, 90));

        // Logical unit of work
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOLUW, Messages.Tooltip_JOLUW, 60));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOXID, Messages.Tooltip_JOXID, 60));
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOCMTLVL, Messages.Tooltip_JOCMTLVL, 70));

        // Null value indicators
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JONVI, Messages.Tooltip_JONVI, 60));

        // Entry specific data
        columns.add(new JournalEntryColumn(JournalEntryColumnUI.JOESD, Messages.Tooltip_JOESD, 350));

        return sortColumnsAndApplyAppearanceAttributes(columns.toArray(new JournalEntryColumn[columns.size()]), Preferences.getInstance()
            .getSortedJournalEntryAppearancesAttributes());
    }

    private static JournalEntryColumn[] sortColumnsAndApplyAppearanceAttributes(JournalEntryColumn[] journalEntryColumns,
        JournalEntryAppearanceAttributes[] sortedNames) {

        Map<String, JournalEntryColumn> journalEntryColumnsMap = new HashMap<String, JournalEntryColumn>();
        for (JournalEntryColumn journalEntryColumn : journalEntryColumns) {
            journalEntryColumnsMap.put(journalEntryColumn.getName(), journalEntryColumn);
        }

        List<JournalEntryColumn> sortedColumns = new LinkedList<JournalEntryColumn>();
        for (JournalEntryAppearanceAttributes journalEntryAppearanceAttributes : sortedNames) {
            JournalEntryColumn journalEntryColumn = journalEntryColumnsMap.get(journalEntryAppearanceAttributes.getColumnName());
            if (journalEntryColumn != null) {
                journalEntryColumn.setColor(journalEntryAppearanceAttributes.getColor());
                sortedColumns.add(journalEntryColumn);
            }
        }

        if (sortedColumns.size() == 0) {
            return journalEntryColumns;
        }

        return sortedColumns.toArray(new JournalEntryColumn[sortedColumns.size()]);
    }
}
