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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.internal.IDialogSettingsManager;
import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.internals.SelectionProviderIntermediate;
import biz.isphere.journalexplorer.core.model.JournalEntries;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.swt.widgets.SqlEditor;
import biz.isphere.journalexplorer.core.ui.contentproviders.JournalViewerContentProvider;
import biz.isphere.journalexplorer.core.ui.labelproviders.JournalEntryLabelProvider;
import biz.isphere.journalexplorer.core.ui.model.AbstractTypeViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumn;
import biz.isphere.journalexplorer.core.ui.model.Type5ViewerFactory;
import biz.isphere.journalexplorer.core.ui.views.JournalEntryViewerView;
import biz.isphere.journalexplorer.core.ui.views.JournalExplorerView;

/**
 * This widget is a viewer for the journal entries of an output file of the
 * DSPJRN command. It is created by a sub-class of the
 * {@link AbstractTypeViewerFactory}. It is used by the "Journal Explorer" view
 * to create the tabs for the opened output files of the DSPJRN command.
 * 
 * @see JournalEntry
 * @see JournalEntryViewerView
 */
public abstract class AbstractJournalEntriesViewer extends CTabItem implements ISelectionChangedListener, ISelectionProvider, IPropertyChangeListener {

    private IDialogSettingsManager dialogSettingsManager = null;

    private static final String COLUMN_WIDTH = "COLUMN_WIDTH_";

    private Composite container;
    private Set<ISelectionChangedListener> selectionChangedListeners;
    private boolean isSqlEditorVisible;
    private SelectionListener loadJournalEntriesSelectionListener;

    private TableViewer tableViewer;
    private JournalEntries data;
    private SqlEditor sqlEditor;
    private String whereClause;

    public AbstractJournalEntriesViewer(CTabFolder parent, SelectionListener loadJournalEntriesSelectionListener) {
        super(parent, SWT.NONE);

        setSqlEditorVisibility(false);

        this.container = new Composite(parent, SWT.NONE);
        this.selectionChangedListeners = new HashSet<ISelectionChangedListener>();
        this.isSqlEditorVisible = false;
        this.loadJournalEntriesSelectionListener = loadJournalEntriesSelectionListener;
        this.whereClause = null;

        Preferences.getInstance().addPropertyChangeListener(this);
    }

    protected abstract String getLabel();

    protected abstract String getTooltip();

    protected abstract ContentAssistProposal[] getContentAssistProposals();

    protected void createSqlEditor() {

        if (!isAvailable(sqlEditor)) {
            sqlEditor = new SqlEditor(getContainer(), SWT.NONE);
            sqlEditor.setContentAssistProposals(getContentAssistProposals());
            sqlEditor.addSelectionListener(loadJournalEntriesSelectionListener);
            sqlEditor.setWhereClause(whereClause);
            GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            gd.heightHint = 80;
            sqlEditor.setLayoutData(gd);
            getContainer().layout();
            sqlEditor.setFocus();
        }
    }

    protected void destroySqlEditor() {

        if (sqlEditor != null) {
            whereClause = sqlEditor.getWhereClause();
            // Important, must be called to ensure the SqlEditor is removed from
            // the list of preferences listeners.
            sqlEditor.dispose();
            getContainer().layout();
        }
    }

    protected void setFocusOnSqlEditor() {

        if (isSqlEditorVisible()) {
            sqlEditor.setFocus();
        }
    }

    protected String getWhereClause() {

        if (isSqlEditorVisible()) {
            return sqlEditor.getWhereClause();
        }

        return whereClause;
    }

    public boolean isFiltered() {
        return hasWhereClause();
    }

    private boolean hasWhereClause() {

        if (isSqlEditorVisible()) {
            whereClause = getWhereClause();
        }

        if (!StringHelper.isNullOrEmpty(whereClause) && whereClause.length() > 0) {
            return true;
        }

        return false;
    }

    private boolean isAvailable(Control control) {

        if (control != null && !control.isDisposed()) {
            return true;
        }

        return false;
    }

    protected void initializeComponents() {

        setText(getLabel());
        setToolTipText(getTooltip());

        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tableViewer = createTableViewer(container);
        setColumnSizes(tableViewer);
        container.layout(true);
        setControl(container);
    }

    private void setColumnSizes(TableViewer tableViewer) {

        ControlAdapter columnResizeListener = new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent event) {
                TableColumn column = (TableColumn)event.getSource();
                getDialogSettingsManager().storeValue(produceColumnWidthKey(column), column.getWidth());
            }
        };

        for (int i = 0; i < tableViewer.getTable().getColumnCount(); i++) {
            TableColumn column = tableViewer.getTable().getColumn(i);
            int width = getDialogSettingsManager().loadIntValue(produceColumnWidthKey(column), getDefaultColumnSize(column));
            column.setWidth(width);
            column.addControlListener(columnResizeListener);
        }
    }

    private String produceColumnWidthKey(TableColumn column) {
        return COLUMN_WIDTH + Type5ViewerFactory.getColumnName(column);
    }

    private IDialogSettingsManager getDialogSettingsManager() {

        if (dialogSettingsManager == null) {
            dialogSettingsManager = new IDialogSettingsManager(AbstractJournalEntriesViewer.class);
        }
        return dialogSettingsManager;
    }

    protected Composite getContainer() {
        return container;
    }

    public void resetColumnSizes() {

        tableViewer.getTable().setVisible(false);

        for (int i = 0; i < tableViewer.getTable().getColumnCount(); i++) {
            TableColumn column = tableViewer.getTable().getColumn(i);
            column.setWidth(getDefaultColumnSize(column));
        }

        tableViewer.getTable().setVisible(true);
    }

    private int getDefaultColumnSize(TableColumn column) {
        return Type5ViewerFactory.getDefaultColumnSize(column);
    }

    public boolean hasSqlEditor() {
        return false;
    }

    public boolean isSqlEditorVisible() {
        return isSqlEditorVisible;
    }

    public void setSqlEditorVisibility(boolean visible) {

        if (!hasSqlEditor()) {
            this.isSqlEditorVisible = false;
        } else {
            this.isSqlEditorVisible = visible;
        }

        setSqlEditorEnablement();
    }

    private void setSqlEditorEnablement() {

        if (hasSqlEditor()) {
            if (isSqlEditorVisible()) {
                createSqlEditor();
            } else {
                destroySqlEditor();
            }
        }
    }

    public JournalEntryColumn[] getColumns() {
        return getLabelProvider().getColumns();
    }

    protected abstract TableViewer createTableViewer(Composite container);

    public abstract void openJournal(JournalExplorerView view) throws Exception;

    public abstract void closeJournal();

    public abstract boolean isLoading();

    protected void setInputData(JournalEntries data) {

        this.data = data;

        container.layout(true);
        tableViewer.setInput(null);
        tableViewer.setUseHashlookup(true);

        if (data != null) {
            tableViewer.setItemCount(data.size());
            tableViewer.setInput(data);
            tableViewer.getTable().setEnabled(true);
        } else {
            tableViewer.setItemCount(0);
            tableViewer.getTable().setEnabled(false);
        }

        tableViewer.setSelection(null);
    }

    @Override
    public void dispose() {

        if (data != null) {

            data.clear();
            data = null;
        }

        if (tableViewer != null) {

            tableViewer.getTable().dispose();
            tableViewer = null;
        }

        Preferences.getInstance().addPropertyChangeListener(this);

        super.dispose();
    }

    public void setAsSelectionProvider(SelectionProviderIntermediate selectionProvider) {
        selectionProvider.setSelectionProviderDelegate(tableViewer);
    }

    public void removeAsSelectionProvider(SelectionProviderIntermediate selectionProvider) {
        selectionProvider.removeSelectionProviderDelegate(tableViewer);
    }

    private void refreshTable() {
        if (tableViewer != null) {
            tableViewer.refresh(true);
        }

    }

    public StructuredSelection getSelection() {

        ISelection selection = tableViewer.getSelection();
        if (selection instanceof StructuredSelection) {
            return (StructuredSelection)selection;
        }

        return new StructuredSelection(new JournalEntry[0]);
    }

    public void setSelection(ISelection selection) {
        // satisfy the ISelectionProvider interface
        tableViewer.setSelection(selection);
    }

    public JournalEntry[] getSelectedItems() {

        List<JournalEntry> selectedItems = new LinkedList<JournalEntry>();

        StructuredSelection selection = getSelection();
        Iterator<?> iterator = selection.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (object instanceof JournalEntry) {
                selectedItems.add((JournalEntry)object);
            }
        }

        return selectedItems.toArray(new JournalEntry[selectedItems.size()]);
    }

    public JournalEntries getInput() {

        JournalViewerContentProvider contentProvider = getContentProvider();
        return contentProvider.getInput();
    }

    private JournalViewerContentProvider getContentProvider() {
        return (JournalViewerContentProvider)tableViewer.getContentProvider();
    }

    private JournalEntryLabelProvider getLabelProvider() {
        return (JournalEntryLabelProvider)tableViewer.getLabelProvider();
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListeners.add(listener);
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListeners.remove(listener);
    }

    public void selectionChanged(SelectionChangedEvent event) {

        SelectionChangedEvent newEvent = new SelectionChangedEvent(this, event.getSelection());

        for (ISelectionChangedListener selectionChangedListener : selectionChangedListeners) {
            selectionChangedListener.selectionChanged(newEvent);
        }
    }

    public void propertyChange(PropertyChangeEvent event) {

        if (event.getProperty() == null) {
            return;
        }

        if (Preferences.HIGHLIGHT_USER_ENTRIES.equals(event.getProperty())) {
            refreshTable();
            return;
        }

        if (Preferences.ENABLED.equals(event.getProperty())) {
            refreshTable();
            return;
        }

        if (event.getProperty().startsWith(Preferences.COLORS)) {
            JournalEntryLabelProvider labelProvider = (JournalEntryLabelProvider)tableViewer.getLabelProvider();
            String columnName = event.getProperty().substring(Preferences.COLORS.length());
            Object object = event.getNewValue();
            if (object instanceof String) {
                String rgb = (String)event.getNewValue();
                if (columnName != null) {
                    Color color = ISphereJournalExplorerCorePlugin.getDefault().getColor(rgb);
                    labelProvider.setColumnColor(columnName, color);
                }
            }
            refreshTable();
            return;
        }
    }
}
