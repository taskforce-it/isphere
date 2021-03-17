/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.progress.UIJob;
import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.RowJEP;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.internal.IResizableTableColumnsViewer;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.sqleditor.SQLSyntaxErrorException;
import biz.isphere.core.swt.widgets.sqleditor.SqlEditor;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.internals.QualifiedName;
import biz.isphere.journalexplorer.core.internals.SelectionProviderIntermediate;
import biz.isphere.journalexplorer.core.model.JournalEntries;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.MetaTable;
import biz.isphere.journalexplorer.core.model.OutputFile;
import biz.isphere.journalexplorer.core.model.SQLWhereClause;
import biz.isphere.journalexplorer.core.model.shared.JournaledFile;
import biz.isphere.journalexplorer.core.model.sqljep.NullValueVariable;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.contentproviders.JournalViewerContentProvider;
import biz.isphere.journalexplorer.core.ui.labelproviders.JournalEntryLabelProvider;
import biz.isphere.journalexplorer.core.ui.model.AbstractTypeViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumn;
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
public abstract class AbstractJournalEntriesViewerTab extends CTabItem implements IResizableTableColumnsViewer, ISelectionChangedListener,
    ISelectionProvider, IPropertyChangeListener {

    private DialogSettingsManager dialogSettingsManager = null;

    private Shell shell;
    private OutputFile outputFile;
    private Composite container;
    private Set<ISelectionChangedListener> selectionChangedListeners;
    private boolean isSqlEditorVisible;
    private SelectionListener loadJournalEntriesSelectionListener;

    private TableViewer tableViewer;
    private JournalEntries data;
    private Composite sqlEditorPanel;
    private SqlEditor sqlEditor;
    private SQLWhereClause filterClause;
    private SQLWhereClause selectClause;

    public AbstractJournalEntriesViewerTab(Shell shell, CTabFolder parent, OutputFile outputFile,
        SelectionListener loadJournalEntriesSelectionListener) {
        super(parent, SWT.NONE);

        setSqlEditorVisibility(false);

        this.shell = shell;
        this.outputFile = outputFile;
        this.container = new Composite(parent, SWT.NONE);
        this.selectionChangedListeners = new HashSet<ISelectionChangedListener>();
        this.isSqlEditorVisible = false;
        this.loadJournalEntriesSelectionListener = loadJournalEntriesSelectionListener;
        this.filterClause = null;
        this.selectClause = null;

        Preferences.getInstance().addPropertyChangeListener(this);
    }

    protected Shell getShell() {
        return shell;
    }

    protected abstract String getLabel();

    protected abstract String getTooltip();

    protected OutputFile getOutputFile() {
        return outputFile;
    }

    private ContentAssistProposal[] getContentAssistProposals() {

        if (hasTableName()) {

            try {

                List<ContentAssistProposal> proposals = new LinkedList<ContentAssistProposal>();

                ContentAssistProposal[] proposalsArray = JournalEntry.getBasicContentAssistProposals();
                addProposalsToList(proposals, proposalsArray);

                String library = getFilterClause().getLibrary();
                String file = getFilterClause().getFile();

                String connectionName = data.getConnectionName();
                MetaTable metaTable = MetaDataCache.getInstance().retrieveMetaData(connectionName, library, file, ISeries.FILE);

                if (metaTable != null) {
                    proposalsArray = metaTable.getContentAssistProposals();
                    addProposalsToList(proposals, proposalsArray);
                }

                return proposals.toArray(new ContentAssistProposal[proposals.size()]);

            } catch (Exception e) {
                return JournalEntry.getBasicContentAssistProposals();
            }

        } else {
            return JournalEntry.getBasicContentAssistProposals();
        }
    }

    private void addProposalsToList(List<ContentAssistProposal> proposals, ContentAssistProposal[] proposalsArray) {
        for (ContentAssistProposal proposal : proposalsArray) {
            proposals.add(proposal);
        }
    }

    private void createSqlEditor() {

        if (!isAvailable(sqlEditor)) {

            sqlEditorPanel = new Composite(getContainer(), SWT.BORDER);
            GridLayout ly_sqlEditorPanel = new GridLayout(3, false);
            sqlEditorPanel.setLayout(ly_sqlEditorPanel);
            sqlEditorPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            sqlEditor = WidgetFactory.createSqlEditor(sqlEditorPanel, getClass().getSimpleName(), getDialogSettingsManager(), true);
            sqlEditor.addSelectionListener(loadJournalEntriesSelectionListener);
            sqlEditor.setWhereClause(getFilterClause().getClause());
            GridData gd_sqlEditor = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            gd_sqlEditor.heightHint = 160;
            gd_sqlEditor.horizontalSpan = ly_sqlEditorPanel.numColumns;
            sqlEditor.setLayoutData(gd_sqlEditor);
            getContainer().layout();
            sqlEditor.setFocus();
            sqlEditor.setBtnExecuteLabel(Messages.ButtonLabel_Filter);
            sqlEditor.setBtnExecuteToolTipText(Messages.ButtonTooltip_Filter);
            sqlEditor.setTableNameItems(getTableNames(data.getJournaledFiles()));
            sqlEditor.addModifyListener(new SQLWhereClauseChangedListener());

            if (hasTableName()) {
                sqlEditor.setTableName(getFilterClause().getLibrary() + "/" + filterClause.getFile()); //$NON-NLS-1$
            } else {
                sqlEditor.selectTableName(0);
            }

        }
    }

    private boolean hasTableName() {

        if (!StringHelper.isNullOrEmpty(getFilterClause().getFile()) && !StringHelper.isNullOrEmpty(getFilterClause().getLibrary())) {
            return true;
        }

        return false;
    }

    private void destroySqlEditor() {

        if (sqlEditorPanel != null) {
            // Important, must be called to ensure the SqlEditor is removed from
            // the list of preferences listeners.
            sqlEditorPanel.dispose();
            sqlEditorPanel = null;
            getContainer().layout();
        }

    }

    protected void setSqlEditorEnabled(boolean enabled) {

        if (isSqlEditorVisible()) {
            sqlEditor.setEnabled(enabled);
        }
    }

    public void setFocusOnSqlEditor() {

        if (isSqlEditorVisible()) {
            sqlEditor.setFocus();
        }
    }

    public void storeSqlEditorHistory() {
        sqlEditor.storeHistory();
    }

    public void refreshSqlEditorHistory() {
        if (isAvailable(sqlEditor)) {
            sqlEditor.refreshHistory();
        }
    }

    protected void setSelectClause(SQLWhereClause whereClause) {
        this.selectClause = whereClause;
    }

    public SQLWhereClause getSelectClause() {
        return selectClause;
    }

    private void setFilterClause(SQLWhereClause whereClause) {
        this.filterClause = whereClause;
    }

    public SQLWhereClause getFilterClause() {

        if (filterClause == null) {
            setFilterClause(new SQLWhereClause());
        }

        return filterClause;
    }

    public void validateWhereClause(Shell shell, SQLWhereClause whereClause) throws SQLSyntaxErrorException {

        if (whereClause == null || !whereClause.hasClause()) {
            return;
        }

        try {

            HashMap<String, Integer> columnMapping = JournalEntry.getBasicColumnMapping();
            RowJEP sqljep = new ValidationRowJEP(whereClause);

            sqljep.parseExpression(columnMapping);
            sqljep.getValue(JournalEntry.getSampleRow());

        } catch (Exception e) {
            throw new SQLSyntaxErrorException(e);
        }

    }

    public boolean isFiltered() {
        return hasWhereClause();
    }

    private boolean hasWhereClause() {

        if (filterClause != null && filterClause.hasClause()) {
            return true;
        }

        if (selectClause != null && selectClause.hasClause()) {
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
        container.layout(true);
        setControl(container);
    }

    protected DialogSettingsManager getDialogSettingsManager() {

        if (dialogSettingsManager == null) {
            dialogSettingsManager = new DialogSettingsManager(ISphereJournalExplorerCorePlugin.getDefault().getDialogSettings(),
                AbstractJournalEntriesViewerTab.class);
        }
        return dialogSettingsManager;
    }

    protected Composite getContainer() {
        return container;
    }

    public void resetColumnWidths() {
        dialogSettingsManager.resetColumnWidths(tableViewer.getTable());
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

    public abstract void openJournal(JournalExplorerView view, SQLWhereClause whereClause, SQLWhereClause filterWhereClause) throws Exception;

    public abstract void filterJournal(JournalExplorerView view, SQLWhereClause whereClause) throws Exception;

    public abstract void closeJournal();

    public abstract boolean isLoading();

    protected void setInputData(JournalEntries data) {

        this.data = data;

        if (data != null) {
            if (isSqlEditorVisible()) {
                sqlEditor.setEnabled(false);
            }
            new PreloadMetaDataJob(data).schedule();
        }

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

        Preferences.getInstance().removePropertyChangeListener(this);

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

        if (isSqlEditorVisible) {
            sqlEditor.setContentAssistProposals(getContentAssistProposals());
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

    protected MetaTable getMetaData() {

        try {
            return MetaDataCache.getInstance().retrieveMetaData(getOutputFile());
        } catch (Exception e) {
            String fileName;
            if (getOutputFile() == null) {
                fileName = "null"; //$NON-NLS-1$
            } else {
                fileName = getOutputFile().toString();
            }
            ISpherePlugin.logError("*** Could not load meta data of file '" + fileName + "' ***", e); //$NON-NLS-1$ //$NON-NLS-2$
            return null;
        }
    }

    private String[] getTableNames(JournaledFile[] files) {

        List<String> tables = new ArrayList<String>();
        tables.add("*"); //$NON-NLS-1$

        Arrays.sort(files);
        for (JournaledFile file : files) {
            // Use simple qualified names: library/object
            String qualifiedName = new QualifiedName(file.getLibraryName(), file.getFileName()).getQualifiedName();
            tables.add(qualifiedName);
        }

        return tables.toArray(new String[tables.size()]);
    }

    private class SQLWhereClauseChangedListener implements ModifyListener {

        private String fileName = ""; //$NON-NLS-1$
        private String libraryName = ""; //$NON-NLS-1$

        public void modifyText(ModifyEvent event) {

            String oldFileName = fileName;
            String oldLibraString = libraryName;

            String sysObjectName = sqlEditor.getTableName().replaceAll("[.]", "/"); //$NON-NLS-1$ //$NON-NLS-2$
            QualifiedName objectName = QualifiedName.parse(sysObjectName);

            if (objectName != null) {
                fileName = notNull(objectName.getObjectName());
                libraryName = notNull(objectName.getLibraryName());
            } else {
                fileName = sysObjectName;
                libraryName = ""; //$NON-NLS-1$
            }

            String whereClause = sqlEditor.getWhereClause().trim();
            setFilterClause(new SQLWhereClause(fileName, libraryName, whereClause));

            if (!fileName.equals(oldFileName) || !libraryName.equals(oldLibraString)) {
                ContentAssistProposal[] proposals = getContentAssistProposals();
                if (proposals == null) {
                    return;
                }
                sqlEditor.setContentAssistProposals(proposals);
            }
        }

        private String notNull(String value) {
            if (value != null) {
                return value;
            }
            return ""; //$NON-NLS-1$;
        }
    }

    // RowJEP, which ignores undefined fields
    private class ValidationRowJEP extends RowJEP {

        private SQLWhereClause whereClause;

        public ValidationRowJEP(SQLWhereClause whereClause) {
            super(whereClause.getClause());
            this.whereClause = whereClause;
        }

        @Override
        public Entry<String, Comparable<?>> getVariable(String name) throws ParseException {
            Entry<String, Comparable<?>> variable = null;
            if (name.startsWith("JO")) { //$NON-NLS-1$
                variable = super.getVariable(name); // JO*
            } else if (!noTableSpecified()) {
                variable = new NullValueVariable(name); // XY*/with table
                whereClause.setSpecificFields(true);
            }
            return variable; // JO*
        }

        public int findColumn(String name) {
            int result = super.findColumn(name);
            if (result < 0) {
                result = -1; // XY*/no table
            }
            return result;
        }

        @Override
        public Comparable<?> getColumnObject(int index) throws ParseException {
            return super.getColumnObject(index); // JO*
        }

        private boolean noTableSpecified() {
            String library = whereClause.getLibrary();
            String file = whereClause.getFile();
            return StringHelper.isNullOrEmpty(library) || StringHelper.isNullOrEmpty(file);
        };
    }

    private class PreloadMetaDataJob extends Job {

        private JournalEntries journalEntries;

        public PreloadMetaDataJob(JournalEntries journalEntries) {
            super(Messages.Status_Loading_meta_data);
            this.journalEntries = journalEntries;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {

            try {

                if (journalEntries != null) {

                    if (isSqlEditorVisible()) {
                        setComboEnabled(false, new JournaledFile[0]);
                    }

                    final JournaledFile[] journaledFiles = journalEntries.getJournaledFiles();
                    // MetaDataCache.getInstance().preloadTables(journaledFiles);
                    if (isSqlEditorVisible()) {
                        setComboEnabled(true, journaledFiles);
                    }
                }

            } catch (Exception e) {
                MessageDialogAsync.displayError(Messages.Status_Loading_meta_data, e.getLocalizedMessage());
            }

            return Status.OK_STATUS;
        }

        private void setComboEnabled(final boolean enabled, final JournaledFile[] journaledFiles) {
            new UIJob("") { //$NON-NLS-1$
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    if (isSqlEditorVisible()) {
                        sqlEditor.setEnabled(enabled);
                        String tableName = sqlEditor.getTableName();
                        sqlEditor.setTableNameItems(getTableNames(journaledFiles));
                        sqlEditor.setTableName(tableName);
                    }
                    return Status.OK_STATUS;
                }
            }.schedule();
        }
    }
}
