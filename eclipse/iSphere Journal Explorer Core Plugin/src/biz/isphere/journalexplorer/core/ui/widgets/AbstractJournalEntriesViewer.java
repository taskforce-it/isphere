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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.internals.SelectionProviderIntermediate;
import biz.isphere.journalexplorer.core.model.JournalEntries;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.contentproviders.JournalViewerContentProvider;
import biz.isphere.journalexplorer.core.ui.labelproviders.JournalEntryLabelProvider;
import biz.isphere.journalexplorer.core.ui.model.AbstractTypeViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumn;
import biz.isphere.journalexplorer.core.ui.views.JournalEntryViewerView;

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

    private Composite container;
    private TableViewer tableViewer;
    private JournalEntries data;
    private Set<ISelectionChangedListener> selectionChangedListeners;
    boolean isSqlEditorVisible;

    public AbstractJournalEntriesViewer(CTabFolder parent) {
        super(parent, SWT.NONE);

        setSqlEditorVisibility(false);

        this.container = new Composite(parent, SWT.NONE);

        this.selectionChangedListeners = new HashSet<ISelectionChangedListener>();
        Preferences.getInstance().addPropertyChangeListener(this);
    }

    protected abstract String getLabel();

    protected abstract String getTooltip();

    protected void initializeComponents() {

        setText(getLabel());
        setToolTipText(getTooltip());

        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tableViewer = createTableViewer(container);
        container.layout(true);
        setControl(container);
    }

    protected Composite getContainer() {
        return container;
    }

    public boolean hasSqlEditor() {
        return false;
    }

    public boolean isSqlEditorVisible() {
        return isSqlEditorVisible;
    }

    public void setSqlEditorVisibility(boolean visible) {
        this.isSqlEditorVisible = visible;
    }

    public JournalEntryColumn[] getColumns() {
        return getLabelProvider().getColumns();
    }

    protected abstract TableViewer createTableViewer(Composite container);

    public abstract void openJournal() throws Exception;

    protected void setInputData(JournalEntries data) {

        this.data = data;

        container.layout(true);
        tableViewer.setInput(null);
        tableViewer.setUseHashlookup(true);
        tableViewer.setItemCount(data.size());
        tableViewer.setInput(data);
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

    public TableViewer getTableViewer() {
        return tableViewer;
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
