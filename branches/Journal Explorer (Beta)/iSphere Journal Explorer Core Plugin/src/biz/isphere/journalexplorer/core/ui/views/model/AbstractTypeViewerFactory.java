/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.views.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.journalexplorer.core.ui.contentproviders.JournalViewerContentProvider;
import biz.isphere.journalexplorer.core.ui.labelproviders.JournalColumnLabel;

public abstract class AbstractTypeViewerFactory {

    private Set<String> columnNames;
    private ViewerColumn[] fieldIdMapping;

    public AbstractTypeViewerFactory(Set<ViewerColumn> columnNames) {
        this.columnNames = getColumnNames(columnNames);
        this.fieldIdMapping = new ViewerColumn[columnNames.size()];
    }

    private Set<String> getColumnNames(Set<ViewerColumn> columnNamesEnum) {

        Set<String> names = new HashSet<String>();

        for (ViewerColumn columnsEnum : columnNamesEnum) {
            names.add(columnsEnum.fieldName());
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

        JournalViewerTableColumn[] columns = getAvailableTableColumns();

        int i = 0;
        for (JournalViewerTableColumn column : columns) {
            if (columnNames.contains(column.getName())) {
                fieldIdMapping[i] = column.getColumnDef();
                newColumn = new TableColumn(table, column.getStyle());
                newColumn.setText(column.getText());
                newColumn.setToolTipText(column.getTooltipText());
                newColumn.setWidth(column.getWidth());
                newColumn.setResizable(column.isResizable());
                newColumn.setMoveable(column.isMovebale());
                i++;
            }
        }

        tableViewer.setLabelProvider(new JournalColumnLabel(fieldIdMapping));
        tableViewer.setContentProvider(new JournalViewerContentProvider(tableViewer));

        return tableViewer;
    }

    private JournalViewerTableColumn[] getAvailableTableColumns() {

        List<JournalViewerTableColumn> columns = new LinkedList<JournalViewerTableColumn>();

        columns.add(new JournalViewerTableColumn(ViewerColumn.ID, "#RRN", "RRN in journal output file", 45, SWT.RIGHT));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOENTL, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOSEQN, null, 55));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOCODE, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOENTT, null, 55));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JODATE, null, 80));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOTIME, null, 80));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOJOB, null, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOUSER, null, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JONBR, null, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOPGM, null, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOPGMLIB, null, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOPGMDEV, null, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOPGMASP, null, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOOBJ, null, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOLIB, null, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOMBR, null, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOCTRR, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOFLAG, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOCCID, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOUSPF, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOSYNM, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOJID, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JORCST, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOTGR, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOINCDAT, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOIGNAPY, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOMINESD, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOOBJIND, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOSYSSEQ, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JORCV, null, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JORCVLIB, null, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JORCVDEV, null, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JORCVASP, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOARM, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOTHDX, null, 120));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOADF, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JORPORT, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JORADR, null, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOLUW, null, 200));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOXID, null, 200));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOOBJTYP, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOFILTYP, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOCMTLVL, null, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOESD, null, 350));

        return columns.toArray(new JournalViewerTableColumn[columns.size()]);
    }
}
