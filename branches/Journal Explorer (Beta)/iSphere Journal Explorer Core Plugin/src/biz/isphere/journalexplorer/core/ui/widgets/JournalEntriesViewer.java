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

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.journalexplorer.core.internals.SelectionProviderIntermediate;
import biz.isphere.journalexplorer.core.model.File;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.dao.JournalDAO;
import biz.isphere.journalexplorer.core.ui.contentproviders.JournalViewerContentProvider;
import biz.isphere.journalexplorer.core.ui.labelproviders.JournalColumnLabel;

public class JournalEntriesViewer extends CTabItem {

    private Composite container;
    private TableViewer tableViewer;
    private String connectionName;
    private File outputFile;
    private List<JournalEntry> data;
    private Exception dataLoadException;

    public JournalEntriesViewer(CTabFolder parent, File outputFile) {
        super(parent, SWT.NONE);

        this.outputFile = outputFile;
        this.connectionName = outputFile.getConnectionName();
        this.container = new Composite(parent, SWT.NONE);

        this.initializeComponents();
    }

    private void initializeComponents() {

        container.setLayout(new FillLayout());
        setText(connectionName + ": " + outputFile.getQualifiedName());
        createTableViewer(container);
        container.layout(true);
        setControl(container);
    }

    private void createTableViewer(Composite container) {

        Table table;
        TableColumn newColumn;

        tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.READ_ONLY | SWT.VIRTUAL);

        table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        // /
        // / RRN Column
        // /
        newColumn = new TableColumn(table, SWT.RIGHT);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(45);
        newColumn.setText("RRN");

        // /
        // / JOENTT Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(55);
        newColumn.setText("JOENTT");

        // /
        // / JOSEQN Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(55);
        newColumn.setText("JOSEQN");

        // /
        // / JOCODE Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(50);
        newColumn.setText("JOCODE");

        // /
        // / JOENTL Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(50);
        newColumn.setText("JOENTL");

        // /
        // / JODATE Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(80);
        newColumn.setText("JODATE");

        // /
        // / JOTIME Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(80);
        newColumn.setText("JOTIME");

        // /
        // / JOJOB Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(90);
        newColumn.setText("JOJOB");

        // /
        // / JOUSER Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(90);
        newColumn.setText("JOUSER");

        // /
        // / JONBR Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(90);
        newColumn.setText("JONBR");

        // /
        // / JOPGM Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(90);
        newColumn.setText("JOPGM");

        // /
        // / JOLIB Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(90);
        newColumn.setText("JOLIB");

        // /
        // / JOMBR Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(90);
        newColumn.setText("JOMBR");

        // /
        // / JOOBJ Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(90);
        newColumn.setText("JOOBJ");

        // /
        // / JOMINESD Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(50);
        newColumn.setText("JOMINESD");

        // /
        // / JOESD Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(350);
        newColumn.setText("JOESD");

        tableViewer.setLabelProvider(new JournalColumnLabel());
        tableViewer.setContentProvider(new JournalViewerContentProvider(tableViewer));
    }

    public void openJournal() throws Exception {

        dataLoadException = null;

        Runnable loadJournalDataJob = new Runnable() {

            public void run() {

                try {

                    JournalDAO journalDAO = new JournalDAO(outputFile);
                    data = journalDAO.getJournalData();
                    container.layout(true);
                    tableViewer.setInput(null);
                    tableViewer.setUseHashlookup(true);
                    tableViewer.setItemCount(data.size());
                    tableViewer.setInput(data);

                } catch (Exception e) {
                    dataLoadException = e;
                }
            }

        };

        BusyIndicator.showWhile(getDisplay(), loadJournalDataJob);

        if (dataLoadException != null) {
            throw dataLoadException;
        }
    }

    @Override
    public void dispose() {

        super.dispose();

        if (data != null) {

            data.clear();
            data = null;
        }

        if (tableViewer != null) {

            tableViewer.getTable().dispose();
            tableViewer = null;
        }
    }

    public TableViewer getTableViewer() {
        return tableViewer;
    }

    public void setAsSelectionProvider(SelectionProviderIntermediate selectionProvider) {
        selectionProvider.setSelectionProviderDelegate(tableViewer);
    }

    public void removeAsSelectionProvider(SelectionProviderIntermediate selectionProvider) {
        selectionProvider.removeSelectionProviderDelegate(tableViewer);
    }

    public void refreshTable() {
        if (tableViewer != null) {
            tableViewer.refresh(true);
        }

    }

    public JournalEntry[] getInput() {

        JournalViewerContentProvider contentProvider = (JournalViewerContentProvider)tableViewer.getContentProvider();
        return contentProvider.getInput();
    }
}
