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
import java.util.Set;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.ui.contentproviders.JournalViewerContentProvider;
import biz.isphere.journalexplorer.core.ui.labelproviders.JournalEntryColumnLabel;
import biz.isphere.journalexplorer.core.ui.popupmenus.JournalEntryMenuAdapter;

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

        final Menu menuTableMembers = new Menu(table);
        menuTableMembers.addMenuListener(new JournalEntryMenuAdapter(menuTableMembers, tableViewer));
        table.setMenu(menuTableMembers);

        tableViewer.setLabelProvider(new JournalEntryColumnLabel(fieldIdMapping));
        tableViewer.setContentProvider(new JournalViewerContentProvider(tableViewer));

        return tableViewer;
    }

    private JournalViewerTableColumn[] getAvailableTableColumns() {

        List<JournalViewerTableColumn> columns = new LinkedList<JournalViewerTableColumn>();

        columns.add(new JournalViewerTableColumn(ViewerColumn.ID, Messages.ColLabel_OutputFile_Rrn, Messages.Tooltip_OutputFile_Rrn, 45, SWT.RIGHT));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOENTL, Messages.Tooltip_JOENTL, 60));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOSEQN, Messages.Tooltip_JOSEQN, 60));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOCODE, Messages.Tooltip_JOCODE, 60));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOENTT, Messages.Tooltip_JOENTT, 60));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JODATE, Messages.Tooltip_JODATE, 80));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOTIME, Messages.Tooltip_JOTIME, 80));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOJOB, Messages.Tooltip_JOJOB, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOUSER, Messages.Tooltip_JOUSER, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JONBR, Messages.Tooltip_JONBR, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOPGM, Messages.Tooltip_JOPGM, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOPGMLIB, Messages.Tooltip_JOPGMLIB, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOPGMDEV, Messages.Tooltip_JOPGMDEV, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOPGMASP, Messages.Tooltip_JOPGMASP, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOOBJ, Messages.Tooltip_JOOBJ, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOLIB, Messages.Tooltip_JOLIB, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOMBR, Messages.Tooltip_JOMBR, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOCTRR, Messages.Tooltip_JOCTRR, 60));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOFLAG, Messages.Tooltip_JOFLAG, 60));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOCCID, Messages.Tooltip_JOCCID, 60));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOUSPF, Messages.Tooltip_JOUSPF, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOSYNM, Messages.Tooltip_JOSYNM, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOJID, Messages.Tooltip_JOJID, 60));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JORCST, Messages.Tooltip_JORCST, 60));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOTGR, Messages.Tooltip_JOTGR, 60));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOINCDAT, Messages.Tooltip_JOINCDAT, 60));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOIGNAPY, Messages.Tooltip_JOIGNAPY, 60));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOMINESD, Messages.Tooltip_JOMINESD, 60));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOOBJIND, Messages.Tooltip_JOOBJIND, 80));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOSYSSEQ, Messages.Tooltip_JOSYSSEQ, 140));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JORCV, Messages.Tooltip_JORCV, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JORCVLIB, Messages.Tooltip_JORCVLIB, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JORCVDEV, Messages.Tooltip_JORCVDEV, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JORCVASP, Messages.Tooltip_JORCVASP, 50));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOARM, Messages.Tooltip_JOARM, 60));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOTHDX, Messages.Tooltip_JOTHDX, 120));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOADF, Messages.Tooltip_JOADF, 60));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JORPORT, Messages.Tooltip_JORPORT, 60));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JORADR, Messages.Tooltip_JORADR, 90));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOLUW, Messages.Tooltip_JOLUW, 200));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOXID, Messages.Tooltip_JOXID, 200));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOOBJTYP, Messages.Tooltip_JOOBJTYP, 60));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOFILTYP, Messages.Tooltip_JOFILTYP, 60));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOCMTLVL, Messages.Tooltip_JOCMTLVL, 60));
        columns.add(new JournalViewerTableColumn(ViewerColumn.JOESD, Messages.Tooltip_JOESD, 350));

        return columns.toArray(new JournalViewerTableColumn[columns.size()]);
    }
}
