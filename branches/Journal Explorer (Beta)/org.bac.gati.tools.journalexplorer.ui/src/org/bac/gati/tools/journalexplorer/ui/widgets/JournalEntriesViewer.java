package org.bac.gati.tools.journalexplorer.ui.widgets;

import java.util.ArrayList;

import org.bac.gati.tools.journalexplorer.ui.contentProviders.JournalViewerContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
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
import biz.isphere.journalexplorer.rse.shared.ui.views.ConfigureJournalEntriesTableViewer;

public class JournalEntriesViewer extends CTabItem {

    private Composite container;

    private TableViewer tableViewer;

    private String connectionName;

    private String library;

    private String fileName;

    private ArrayList<JournalEntry> data;

    public JournalEntriesViewer(CTabFolder parent, File outputFile) {

        super(parent, SWT.NONE);
        this.library = outputFile.getOutFileLibrary();
        this.fileName = outputFile.getOutFileName();
        this.connectionName = outputFile.getConnectionName();
        this.container = new Composite(parent, SWT.NONE);
        this.initializeComponents();
    }

    private void initializeComponents() {

        this.container.setLayout(new FillLayout());
        this.setText(this.connectionName + ": " + library + "/" + fileName);
        this.createTableViewer(container);
        this.container.layout(true);
        this.setControl(this.container);
    }

    private void createTableViewer(Composite container) {

        Table table;
        TableColumn newColumn;

        this.tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.READ_ONLY | SWT.VIRTUAL);

        table = this.tableViewer.getTable();
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
        // newColumn.setLabelProvider(new JournalColumnLabel() {
        //
        // @Override
        // public Color getBackground(Object element) {
        // return
        // Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
        // }
        //
        // @Override
        // public String getText(Object element) {
        // JournalEntry journal = (JournalEntry)element;
        // return Integer.toString(journal.getRrn()).trim();
        // }
        // });

        // /
        // / JOENTT Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(55);
        newColumn.setText("JOENTT");
        // newColumn.setLabelProvider(new JournalColumnLabel() {
        // @Override
        // public String getText(Object element) {
        // JournalEntry journal = (JournalEntry)element;
        // return journal.getEntryType();
        // }
        // });

        // /
        // / JOSEQN Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(55);
        newColumn.setText("JOSEQN");
        // newColumn.setLabelProvider(new JournalColumnLabel() {
        // @Override
        // public String getText(Object element) {
        // JournalEntry journal = (JournalEntry)element;
        // return Long.toString(journal.getSequenceNumber());
        // }
        // });

        // /
        // / JOCODE Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(50);
        newColumn.setText("JOCODE");
        // newColumn.setLabelProvider(new JournalColumnLabel() {
        // @Override
        // public String getText(Object element) {
        // JournalEntry journal = (JournalEntry)element;
        // return journal.getJournalCode();
        // }
        // });

        // /
        // / JOENTL Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(50);
        newColumn.setText("JOENTL");
        // newColumn.setLabelProvider(new JournalColumnLabel() {
        // @Override
        // public String getText(Object element) {
        // JournalEntry journal = (JournalEntry)element;
        // return Integer.toString(journal.getEntryLength());
        // }
        // });

        // /
        // / JODATE Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(80);
        newColumn.setText("JODATE");
        // newColumn.setLabelProvider(new JournalColumnLabel() {
        // @Override
        // public String getText(Object element) {
        // JournalEntry journal = (JournalEntry)element;
        // Date date = journal.getDate();
        // if (date == null) {
        // return "";
        // }
        // return getDateFormatter().format(date);
        // }
        // });

        // /
        // / JOTIME Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(80);
        newColumn.setText("JOTIME");
        // newColumn.setLabelProvider(new JournalColumnLabel() {
        // @Override
        // public String getText(Object element) {
        // JournalEntry journal = (JournalEntry)element;
        // Time time = journal.getTime();
        // if (time == null) {
        // return "";
        // }
        // return getTimeFormatter().format(time);
        // }
        // });

        // /
        // / JOJOB Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(90);
        newColumn.setText("JOJOB");
        // newColumn.setLabelProvider(new JournalColumnLabel() {
        // @Override
        // public String getText(Object element) {
        // JournalEntry journal = (JournalEntry)element;
        // return journal.getJobName();
        // }
        // });

        // /
        // / JOUSER Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(90);
        newColumn.setText("JOUSER");
        // newColumn.setLabelProvider(new JournalColumnLabel() {
        // @Override
        // public String getText(Object element) {
        // JournalEntry journal = (JournalEntry)element;
        // return journal.getJobUserName();
        // }
        // });

        // /
        // / JONBR Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(90);
        newColumn.setText("JONBR");
        // newColumn.setLabelProvider(new JournalColumnLabel() {
        // @Override
        // public String getText(Object element) {
        // JournalEntry journal = (JournalEntry)element;
        // return Integer.toString(journal.getJobNumber());
        // }
        // });

        // /
        // / JOPGM Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(90);
        newColumn.setText("JOPGM");
        // newColumn.setLabelProvider(new JournalColumnLabel() {
        // @Override
        // public String getText(Object element) {
        // JournalEntry journal = (JournalEntry)element;
        // return journal.getProgramName();
        // }
        // });

        // /
        // / JOLIB Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(90);
        newColumn.setText("JOLIB");
        // newColumn.setLabelProvider(new JournalColumnLabel() {
        // @Override
        // public String getText(Object element) {
        // JournalEntry journal = (JournalEntry)element;
        // return journal.getObjectLibrary();
        // }
        // });

        // /
        // / JOMBR Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(90);
        newColumn.setText("JOMBR");
        // newColumn.setLabelProvider(new JournalColumnLabel() {
        // @Override
        // public String getText(Object element) {
        // JournalEntry journal = (JournalEntry)element;
        // return journal.getMemberName();
        // }
        // });

        // /
        // / JOOBJ Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(90);
        newColumn.setText("JOOBJ");
        // newColumn.setLabelProvider(new JournalColumnLabel() {
        // @Override
        // public String getText(Object element) {
        // JournalEntry journal = (JournalEntry)element;
        // return journal.getObjectName();
        // }
        // });

        // /
        // / JOMINESD Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(50);
        newColumn.setText("JOMINESD");
        // newColumn.setLabelProvider(new JournalColumnLabel() {
        // @Override
        // public String getText(Object element) {
        // JournalEntry journal = (JournalEntry)element;
        // return journal.getMinimizedSpecificData();
        // }
        // });

        // /
        // / JOESD Column
        // /
        newColumn = new TableColumn(table, SWT.NONE);
        newColumn.setMoveable(true);
        newColumn.setResizable(true);
        newColumn.setWidth(350);
        newColumn.setText("JOESD");
        // newColumn.setLabelProvider(new JournalColumnLabel() {
        // @Override
        // public String getText(Object element) {
        // JournalEntry journal = (JournalEntry)element;
        //
        // // For displaying purposes, replace the null ending character
        // // for a blank.
        // // Otherwise, the string was truncate by JFace
        // String stringSpecificData = journal.getStringSpecificData();
        // if (stringSpecificData.indexOf('\0') >= 0) {
        // return stringSpecificData.replace('\0', ' ').substring(1, 200);
        // } else {
        // return stringSpecificData;
        // }
        // }
        // });

        ConfigureJournalEntriesTableViewer.configureTableViewer(tableViewer);

        this.tableViewer.setContentProvider(new JournalViewerContentProvider(this.tableViewer));
    }

    public void openJournal() throws Exception {

        JournalDAO journalDAO = new JournalDAO(this.connectionName, this.library, this.fileName);
        this.data = journalDAO.getJournalData();
        this.container.layout(true);
        this.tableViewer.setInput(null);
        this.tableViewer.setUseHashlookup(true);
        this.tableViewer.setItemCount(data.size());
        this.tableViewer.setInput(data);
    }

    @Override
    public void dispose() {

        super.dispose();

        if (this.data != null) {

            this.data.clear();
            this.data = null;
        }

        if (this.tableViewer != null) {

            this.tableViewer.getTable().dispose();
            this.tableViewer = null;
        }
    }

    public TableViewer getTableViewer() {
        return tableViewer;
    }

    public void setAsSelectionProvider(SelectionProviderIntermediate selectionProvider) {
        selectionProvider.setSelectionProviderDelegate(this.tableViewer);
    }

    public void removeAsSelectionProvider(SelectionProviderIntermediate selectionProvider) {
        selectionProvider.removeSelectionProviderDelegate(this.tableViewer);
    }

    public void refreshTable() {
        if (this.tableViewer != null) {
            this.tableViewer.refresh(true);
        }

    }

}
