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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.internals.SelectionProviderIntermediate;
import biz.isphere.journalexplorer.core.model.File;
import biz.isphere.journalexplorer.core.model.JournalEntries;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaColumn;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.MetaTable;
import biz.isphere.journalexplorer.core.model.dao.IStatusListener;
import biz.isphere.journalexplorer.core.model.dao.JournalDAO;
import biz.isphere.journalexplorer.core.model.dao.JournalOutputType;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.swt.widgets.SqlEditor;
import biz.isphere.journalexplorer.core.ui.contentproviders.JournalViewerContentProvider;
import biz.isphere.journalexplorer.core.ui.labelproviders.JournalEntryLabelProvider;
import biz.isphere.journalexplorer.core.ui.model.AbstractTypeViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type1ViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type2ViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type3ViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type4ViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type5ViewerFactory;
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
public class JournalEntriesViewer extends CTabItem implements ISelectionChangedListener, ISelectionProvider, IPropertyChangeListener {

    private SelectionListener selectionListener;
    private IStatusListener statusListener;
    private Composite container;
    private TableViewer tableViewer;
    private String connectionName;
    private File outputFile;
    private MetaTable outputFileMetaData;
    private JournalDAO journalDAO;
    private JournalEntries data;
    private Exception dataLoadException;
    private Set<ISelectionChangedListener> selectionChangedListeners;
    private String whereClause;

    private boolean showSqlEditor;
    private SqlEditor sqlEditor;

    public JournalEntriesViewer(CTabFolder parent, File outputFile, SelectionListener selectionListener, IStatusListener statusListener) {
        super(parent, SWT.NONE);

        this.outputFile = outputFile;
        this.selectionListener = selectionListener;
        this.statusListener = statusListener;
        this.connectionName = outputFile.getConnectionName();
        this.container = new Composite(parent, SWT.NONE);

        setSqlEditorVisibility(false);

        this.selectionChangedListeners = new HashSet<ISelectionChangedListener>();
        Preferences.getInstance().addPropertyChangeListener(this);

        this.initializeComponents();
    }

    private void initializeComponents() {

        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        setText(connectionName + ": " + outputFile.getQualifiedName());
        createTableViewer(container);
        container.layout(true);
        setControl(container);
    }

    private void createTableViewer(Composite container) {

        try {

            AbstractTypeViewerFactory factory = null;
            switch (getOutfileType(outputFile)) {
            case JournalOutputType.TYPE5:
                factory = new Type5ViewerFactory();
                break;
            case JournalOutputType.TYPE4:
                factory = new Type4ViewerFactory();
                break;
            case JournalOutputType.TYPE3:
                factory = new Type3ViewerFactory();
                break;
            case JournalOutputType.TYPE2:
                factory = new Type2ViewerFactory();
                break;
            default:
                factory = new Type1ViewerFactory();
                break;
            }

            tableViewer = factory.createTableViewer(container);
            tableViewer.addSelectionChangedListener(this);

        } catch (Exception e) {
            MessageDialog.openError(getParent().getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }
    }

    private int getOutfileType(File outputFile) throws Exception {

        MetaTable metaTable = MetaDataCache.INSTANCE.retrieveMetaData(outputFile);
        metaTable.setJournalOutputFile(true);

        return metaTable.getOutfileType();
    }

    public void openJournal() throws Exception {

        dataLoadException = null;
        if (isAvailable(sqlEditor)) {
            whereClause = sqlEditor.getWhereClause().trim();
            sqlEditor.setFocus();
        }

        Runnable loadJournalDataJob = new Runnable() {

            public void run() {

                try {

                    journalDAO = new JournalDAO(outputFile);
                    journalDAO.setStatusListener(statusListener);
                    data = journalDAO.getJournalData(whereClause);
                    container.layout(true);
                    tableViewer.setInput(null);
                    tableViewer.setUseHashlookup(true);
                    tableViewer.setItemCount(data.size());
                    tableViewer.setInput(data);
                    tableViewer.setSelection(null);

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

        JournalViewerContentProvider contentProvider = (JournalViewerContentProvider)tableViewer.getContentProvider();
        return contentProvider.getInput();
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

    public boolean isSqlEditorVisible() {
        return showSqlEditor;
    }

    public void setSqlEditorVisibility(boolean isVisible) {
        showSqlEditor = isVisible;
        setSqlEditorEnablement();
    }

    private void setSqlEditorEnablement() {

        if (showSqlEditor) {
            createSqlEditor();
        } else {
            destroySqlEditor();
        }
    }

    private void createSqlEditor() {

        if (!isAvailable(sqlEditor)) {
            sqlEditor = new SqlEditor(container, SWT.NONE);
            sqlEditor.setContentAssistProposals(getContentAssistProposals());
            sqlEditor.addSelectionListener(selectionListener);
            sqlEditor.setWhereClause(journalDAO.getWhereClause());
            GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            gd.heightHint = 80;
            sqlEditor.setLayoutData(gd);
            container.layout();
            sqlEditor.setFocus();
        }
    }

    private boolean isAvailable(Control control) {

        if (control != null && !control.isDisposed()) {
            return true;
        }

        return false;
    }

    private ContentAssistProposal[] getContentAssistProposals() {

        List<ContentAssistProposal> proposals = new LinkedList<ContentAssistProposal>();

        MetaTable metaData = getMetaData();
        for (MetaColumn column : metaData.getColumns()) {
            proposals.add(new ContentAssistProposal(column.getName(), column.getFormattedType() + " - " + column.getText()));
        }

        return proposals.toArray(new ContentAssistProposal[proposals.size()]);
    }

    private MetaTable getMetaData() {

        if (outputFileMetaData == null) {
            try {
                outputFileMetaData = MetaDataCache.INSTANCE.retrieveMetaData(outputFile);
            } catch (Exception e) {
                outputFileMetaData = null;
            }
        }

        return outputFileMetaData;
    }

    private void destroySqlEditor() {

        if (sqlEditor != null) {
            // Important, must be called to ensure the SqlEditor is removed from
            // the list of preferences listeners.
            sqlEditor.dispose();
            container.layout();
        }
    }
}
