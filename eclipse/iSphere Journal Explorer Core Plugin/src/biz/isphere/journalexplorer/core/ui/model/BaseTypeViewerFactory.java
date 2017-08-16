/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.model;

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

public class BaseTypeViewerFactory {

    private Set<String> columnNames;
    private IJournalEntryColumn[] fieldIdMapping;

    public BaseTypeViewerFactory() {
        this(null);
    }

    public BaseTypeViewerFactory(Set<IJournalEntryColumn> columnNames) {
        if (columnNames != null) {
            this.columnNames = getColumnNames(columnNames);
            this.fieldIdMapping = new IJournalEntryColumn[columnNames.size()];
        } else {
            this.columnNames = null;
            this.fieldIdMapping = null;
        }
    }

    private Set<String> getColumnNames(Set<IJournalEntryColumn> columnNamesEnum) {

        Set<String> names = new HashSet<String>();

        for (IJournalEntryColumn columnsEnum : columnNamesEnum) {
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
                newColumn.setText(column.getText());
                newColumn.setToolTipText(column.getTooltipText());
                newColumn.setWidth(column.getWidth());
                newColumn.setResizable(column.isResizable());
                newColumn.setMoveable(column.isMovebale());
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

    public JournalEntryColumn[] getAvailableTableColumns() {

        List<JournalEntryColumn> columns = new LinkedList<JournalEntryColumn>();

        columns.add(new JournalEntryColumn(IJournalEntryColumn.ID, Messages.ColLabel_OutputFile_Rrn, Messages.Tooltip_OutputFile_Rrn, 45, SWT.RIGHT));

        // Entry seq#, code, type, ...
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOENTL, Messages.Tooltip_JOENTL, 60));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOSEQN, Messages.Tooltip_JOSEQN, 60));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOCODE, Messages.Tooltip_JOCODE, 60));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOENTT, Messages.Tooltip_JOENTT, 60));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JODATE, Messages.Tooltip_JODATE, 80));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOTIME, Messages.Tooltip_JOTIME, 80));

        // Job, that added the journal entry ...
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOJOB, Messages.Tooltip_JOJOB, 90));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOUSER, Messages.Tooltip_JOUSER, 90));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JONBR, Messages.Tooltip_JONBR, 90));
        // .. extended attributes
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOUSPF, Messages.Tooltip_JOUSPF, 90));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOTHDX, Messages.Tooltip_JOTHDX, 120));

        // Program, that added the journal entry
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOPGMLIB, Messages.Tooltip_JOPGMLIB, 90));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOPGM, Messages.Tooltip_JOPGM, 90));
        // .. extended attributes
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOPGMDEV, Messages.Tooltip_JOPGMDEV, 90));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOPGMASP, Messages.Tooltip_JOPGMASP, 90));

        // Object that was changed
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOLIB, Messages.Tooltip_JOLIB, 90));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOOBJ, Messages.Tooltip_JOOBJ, 90));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOMBR, Messages.Tooltip_JOMBR, 90));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOOBJTYP, Messages.Tooltip_JOOBJTYP, 60));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOFILTYP, Messages.Tooltip_JOFILTYP, 60));

        // System that the object resides on
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOSYNM, Messages.Tooltip_JOSYNM, 90));

        // Journal entry flags
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOCTRR, Messages.Tooltip_JOCTRR, 60));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOFLAG, Messages.Tooltip_JOFLAG, 60));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOCCID, Messages.Tooltip_JOCCID, 60));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOJID, Messages.Tooltip_JOJID, 60));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JORCST, Messages.Tooltip_JORCST, 60));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOTGR, Messages.Tooltip_JOTGR, 60));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOINCDAT, Messages.Tooltip_JOINCDAT, 60));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOIGNAPY, Messages.Tooltip_JOIGNAPY, 60));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOMINESD, Messages.Tooltip_JOMINESD, 60));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOOBJIND, Messages.Tooltip_JOOBJIND, 80));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOSYSSEQ, Messages.Tooltip_JOSYSSEQ, 140));

        // Journal receiver
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JORCV, Messages.Tooltip_JORCV, 90));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JORCVLIB, Messages.Tooltip_JORCVLIB, 90));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JORCVDEV, Messages.Tooltip_JORCVDEV, 90));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JORCVASP, Messages.Tooltip_JORCVASP, 50));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOARM, Messages.Tooltip_JOARM, 60));

        // Remote address
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOADF, Messages.Tooltip_JOADF, 60));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JORPORT, Messages.Tooltip_JORPORT, 60));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JORADR, Messages.Tooltip_JORADR, 90));

        // Logical unit of work
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOLUW, Messages.Tooltip_JOLUW, 200));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOXID, Messages.Tooltip_JOXID, 200));
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOCMTLVL, Messages.Tooltip_JOCMTLVL, 60));

        // Entry specific data
        columns.add(new JournalEntryColumn(IJournalEntryColumn.JOESD, Messages.Tooltip_JOESD, 350));

        setColumnColors(columns);

        return columns.toArray(new JournalEntryColumn[columns.size()]);
    }

    private void setColumnColors(List<JournalEntryColumn> columnNames) {

        Map<String, JournalEntryAppearance> colors = Preferences.getInstance().getJournalEntriesAppearances();

        for (JournalEntryColumn columnName : columnNames) {
            columnName.setColor(colors.get(columnName.getName()).getColor());
        }
    }
}
