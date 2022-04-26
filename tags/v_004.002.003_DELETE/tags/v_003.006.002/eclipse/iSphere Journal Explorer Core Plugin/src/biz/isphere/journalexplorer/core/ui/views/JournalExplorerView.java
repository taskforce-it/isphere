/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.medfoster.sqljep.ParseException;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.preferences.DoNotAskMeAgain;
import biz.isphere.core.preferences.DoNotAskMeAgainDialog;
import biz.isphere.core.swt.widgets.sqleditor.SQLSyntaxErrorException;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.exceptions.BufferTooSmallException;
import biz.isphere.journalexplorer.core.exceptions.NoJournalEntriesLoadedException;
import biz.isphere.journalexplorer.core.internals.SelectionProviderIntermediate;
import biz.isphere.journalexplorer.core.model.JournalEntries;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.MetaTable;
import biz.isphere.journalexplorer.core.model.OutputFile;
import biz.isphere.journalexplorer.core.model.api.JrneToRtv;
import biz.isphere.journalexplorer.core.ui.actions.CompareSideBySideAction;
import biz.isphere.journalexplorer.core.ui.actions.ConfigureParsersAction;
import biz.isphere.journalexplorer.core.ui.actions.EditSqlAction;
import biz.isphere.journalexplorer.core.ui.actions.ExportToExcelAction;
import biz.isphere.journalexplorer.core.ui.actions.GenericRefreshAction;
import biz.isphere.journalexplorer.core.ui.actions.OpenJournalOutfileAction;
import biz.isphere.journalexplorer.core.ui.actions.ResetColumnSizeAction;
import biz.isphere.journalexplorer.core.ui.actions.ToggleHighlightUserEntriesAction;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumn;
import biz.isphere.journalexplorer.core.ui.widgets.AbstractJournalEntriesViewerTab;
import biz.isphere.journalexplorer.core.ui.widgets.JournalEntriesViewerForOutputFilesTab;
import biz.isphere.journalexplorer.core.ui.widgets.JournalEntriesViewerForRetrievedJournalEntriesTab;

public class JournalExplorerView extends ViewPart implements ISelectionChangedListener, SelectionListener {

    public static final String ID = "biz.isphere.journalexplorer.core.ui.views.JournalExplorerView"; //$NON-NLS-1$

    private EditSqlAction editSqlAction;
    private OpenJournalOutfileAction openJournalOutputFileAction;
    private ResetColumnSizeAction resetColumnSizeAction;
    private ExportToExcelAction exportToExcelAction;
    private CompareSideBySideAction compareSideBySideAction;
    private ToggleHighlightUserEntriesAction toggleHighlightUserEntriesAction;
    private ConfigureParsersAction configureParsersAction;
    private GenericRefreshAction reloadEntriesAction;

    private SelectionProviderIntermediate selectionProviderIntermediate;

    private CTabFolder tabFolder;

    public JournalExplorerView() {
        this.selectionProviderIntermediate = new SelectionProviderIntermediate();
    }

    /**
     * Create contents of the view part.
     * 
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout(SWT.HORIZONTAL));

        tabFolder = new CTabFolder(container, SWT.TOP | SWT.CLOSE);
        tabFolder.addSelectionListener(this);
        tabFolder.addCTabFolder2Listener(new CTabFolder2Listener() {
            public void showList(CTabFolderEvent arg0) {
            }

            public void restore(CTabFolderEvent arg0) {
            }

            public void minimize(CTabFolderEvent arg0) {
            }

            public void maximize(CTabFolderEvent arg0) {
            }

            public void close(CTabFolderEvent event) {
                if (event.item instanceof AbstractJournalEntriesViewerTab) {
                    AbstractJournalEntriesViewerTab closedTab = (AbstractJournalEntriesViewerTab)event.item;
                    closedTab.removeAsSelectionProvider(selectionProviderIntermediate);
                    selectNextTab(closedTab);
                }
            }

            private void selectNextTab(CTabItem closedTab) {
                CTabItem activeTab = getSelectedViewer();
                if (closedTab.equals(activeTab)) {
                    int closedTabIndex = tabFolder.getSelectionIndex();
                    int newTabIndex;
                    int maxTabIndex = tabFolder.getItemCount() - 1;
                    if (closedTabIndex < maxTabIndex) {
                        newTabIndex = closedTabIndex + 1;
                    } else {
                        newTabIndex = closedTabIndex - 1;
                    }
                    if (newTabIndex >= 0) {
                        tabFolder.setSelection(newTabIndex);
                        updateStatusLine();
                    } else {
                        clearStatusLine();
                    }
                }
            }
        });

        createActions();
        initializeToolBar();

        clearStatusLine();

        getSite().setSelectionProvider(selectionProviderIntermediate);
    }

    private void disposeJournalExplorerTabChecked(Object object) {
        if (object instanceof AbstractJournalEntriesViewerTab) {
            AbstractJournalEntriesViewerTab tabItem = (AbstractJournalEntriesViewerTab)object;
            tabItem.removeAsSelectionProvider(selectionProviderIntermediate);
            tabItem.dispose();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Create view actions.
     */
    private void createActions() {

        resetColumnSizeAction = new ResetColumnSizeAction(getShell());

        exportToExcelAction = new ExportToExcelAction(getShell());

        openJournalOutputFileAction = new OpenJournalOutfileAction(getShell()) {
            @Override
            public void postRunAction() {
                OutputFile outputFile = openJournalOutputFileAction.getOutputFile();
                if (outputFile != null) {
                    String whereClause = openJournalOutputFileAction.getWhereClause();
                    createJournalTab(outputFile, whereClause);
                }
            }
        };

        editSqlAction = new EditSqlAction(getShell()) {
            @Override
            public void postRunAction() {
                AbstractJournalEntriesViewerTab tabItem = getSelectedViewer();
                tabItem.setSqlEditorVisibility(editSqlAction.isChecked());
                return;
            }
        };

        compareSideBySideAction = new CompareSideBySideAction(getShell());

        toggleHighlightUserEntriesAction = new ToggleHighlightUserEntriesAction();

        configureParsersAction = new ConfigureParsersAction(getShell()) {
            @Override
            public void run() {
                super.run();
                // if (getButtonPressed() == Dialog.OK) {
                // performReloadJournalEntries();
                // }
            };
        };

        reloadEntriesAction = new GenericRefreshAction() {
            @Override
            protected void postRunAction() {
                performReloadJournalEntries();
            }
        };

    }

    public void createJournalTab(JrneToRtv jrneToRtv) {

        JournalEntriesViewerForRetrievedJournalEntriesTab journalEntriesViewer = null;

        try {

            journalEntriesViewer = new JournalEntriesViewerForRetrievedJournalEntriesTab(tabFolder, jrneToRtv, new SqlEditorSelectionListener());

            journalEntriesViewer.setAsSelectionProvider(selectionProviderIntermediate);
            journalEntriesViewer.addSelectionChangedListener(this);

            tabFolder.setSelection(journalEntriesViewer);

            performLoadJournalEntries(journalEntriesViewer);

        } catch (Throwable e) {
            handleDataLoadException(journalEntriesViewer, e);
        }
    }

    private void createJournalTab(OutputFile outputFile, String whereClause) {

        JournalEntriesViewerForOutputFilesTab journalEntriesViewer = null;

        try {

            journalEntriesViewer = new JournalEntriesViewerForOutputFilesTab(tabFolder, outputFile, whereClause, new SqlEditorSelectionListener());

            journalEntriesViewer.setAsSelectionProvider(selectionProviderIntermediate);
            journalEntriesViewer.addSelectionChangedListener(this);

            tabFolder.setSelection(journalEntriesViewer);

            performLoadJournalEntries(journalEntriesViewer);

            setActionEnablement(journalEntriesViewer);

        } catch (Throwable e) {

            ISpherePlugin.logError("*** Error in method JournalExplorerView.createJournalTab ***", e);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            disposeJournalExplorerTabChecked(journalEntriesViewer);
        }
    }

    public void handleDataLoadException(AbstractJournalEntriesViewerTab tabItem, Throwable e) {

        if (e instanceof ParseException) {
            MessageDialog.openInformation(getShell(), Messages.DisplayJournalEntriesDialog_Title, e.getLocalizedMessage());
            return;
        } else if (e instanceof BufferTooSmallException) {
            MessageDialog.openInformation(getShell(), Messages.DisplayJournalEntriesDialog_Title, e.getLocalizedMessage());
            return;
        } else if (e instanceof NoJournalEntriesLoadedException) {
            MessageDialog.openInformation(getShell(), Messages.DisplayJournalEntriesDialog_Title, e.getLocalizedMessage());
        } else if (e instanceof SQLSyntaxErrorException) {
            MessageDialog.openInformation(getShell(), Messages.DisplayJournalEntriesDialog_Title, e.getLocalizedMessage());
        } else {
            ISpherePlugin.logError("*** Error in method JournalExplorerView.handleDataLoadException() ***", e);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }

        disposeJournalExplorerTabChecked(tabItem);

        updateStatusLine();
    }

    /**
     * Returns the tab folder.
     * 
     * @return tab folder
     */
    private CTabFolder getTabFolder() {
        if (tabFolder == null || tabFolder.isDisposed()) {
            return null;
        }
        return tabFolder;
    }

    /**
     * Returns the currently selected viewer (tab).
     * 
     * @return selected viewer
     */
    public AbstractJournalEntriesViewerTab getSelectedViewer() {
        CTabFolder tabFolder = getTabFolder();
        if (tabFolder == null) {
            return null;
        }
        return (AbstractJournalEntriesViewerTab)tabFolder.getSelection();
    }

    private void performReloadJournalEntries() {

        try {

            AbstractJournalEntriesViewerTab tabItem = getSelectedViewer();
            performLoadJournalEntries(tabItem);

        } catch (Exception e) {
            ISpherePlugin.logError("*** Error in method JournalExplorerView.performReloadJournalEntries() ***", e);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }
    }

    private void performLoadJournalEntries(AbstractJournalEntriesViewerTab tabItem) throws Exception {

        String initialWhereClause = tabItem.getSelectClause();
        String filterWhereClause = tabItem.getFilterClause();

        tabItem.validateWhereClause(getViewSite().getShell(), initialWhereClause);
        tabItem.validateWhereClause(getViewSite().getShell(), filterWhereClause);

        tabItem.closeJournal();
        updateStatusLine();
        tabItem.openJournal(this, initialWhereClause, filterWhereClause);
    }

    private void performFilterJournalEntries(AbstractJournalEntriesViewerTab tabItem) throws Exception {

        tabItem.validateWhereClause(getViewSite().getShell(), tabItem.getFilterClause());

        tabItem.storeSqlEditorHistory();
        refreshSqlEditorHistory();

        tabItem.filterJournal(this, tabItem.getFilterClause());
    }

    private void refreshSqlEditorHistory() {
        for (CTabItem tabItem : tabFolder.getItems()) {
            ((AbstractJournalEntriesViewerTab)tabItem).refreshSqlEditorHistory();
        }
    }

    public void finishDataLoading(AbstractJournalEntriesViewerTab tabItem, boolean isFilter) {

        if (tabItem != null) {
            JournalEntries journalEntries = tabItem.getInput();
            if (journalEntries != null) {
                if (journalEntries.isCanceled()) {
                    MessageDialog.openWarning(getShell(), Messages.Title_Load_Journal_Entries,
                        Messages.Warning_Loading_journal_entries_has_been_canceled_by_the_user);
                } else {
                    int numItemsDownloaded = journalEntries.getNumberOfRowsDownloaded();
                    if (journalEntries.isOverflow() && !isFilter) {
                        String messageText;
                        int numItemsAvailable = journalEntries.getNumberOfRowsAvailable();
                        if (numItemsAvailable < 0) {
                            messageText = Messages.bind(Messages.Warning_Not_all_journal_entries_loaded_unknown_size, numItemsAvailable,
                                numItemsDownloaded);
                        } else {
                            messageText = Messages.bind(Messages.Warning_Not_all_journal_entries_loaded, numItemsAvailable, numItemsDownloaded);
                        }
                        DoNotAskMeAgainDialog.openInformation(getViewSite().getShell(), DoNotAskMeAgain.WARNING_NOT_ALL_JOURNAL_ENTRIES_LOADED,
                            messageText);
                    }
                }
            }
        }
    }

    private Shell getShell() {
        return getSite().getShell();
    }

    private void updateStatusLine() {

        AbstractJournalEntriesViewerTab tabItem = getSelectedViewer();

        if (tabItem == null || tabItem.isLoading()) {
            clearStatusLine();
            return;
        }

        JournalEntries journalEntries = tabItem.getInput();
        if (journalEntries == null) {
            clearStatusLine();
            return;
        }

        String message = null;

        int numItems = journalEntries.size();
        if (journalEntries.isOverflow()) {
            int numItemsAvailable = journalEntries.getNumberOfRowsAvailable();
            if (numItemsAvailable < 0) {
                message = Messages.bind(Messages.Number_of_journal_entries_A_more_items_available, numItems);
            } else {
                message = Messages.bind(Messages.Number_of_journal_entries_A_of_B, numItems, numItemsAvailable);
            }
        } else {
            message = Messages.bind(Messages.Number_of_journal_entries_A, numItems);
        }

        if (tabItem.isFiltered()) {
            message += " (" + Messages.subsetted_list + ")";
        }

        setStatusLineText(message);
        setActionEnablement(tabItem);
    }

    private void clearStatusLine() {

        setStatusLineText(""); //$NON-NLS-1$
        setActionEnablement(null);
    }

    private void setStatusLineText(String message) {

        IActionBars bars = getViewSite().getActionBars();
        bars.getStatusLineManager().setMessage(message);
    }

    /**
     * Initialize the toolbar.
     */
    private void initializeToolBar() {

        IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
        toolBarManager.add(openJournalOutputFileAction);
        toolBarManager.add(editSqlAction);
        toolBarManager.add(exportToExcelAction);
        toolBarManager.add(new Separator());
        toolBarManager.add(compareSideBySideAction);
        toolBarManager.add(toggleHighlightUserEntriesAction);
        toolBarManager.add(configureParsersAction);
        toolBarManager.add(new Separator());
        toolBarManager.add(resetColumnSizeAction);
        toolBarManager.add(new Separator());
        toolBarManager.add(reloadEntriesAction);
    }

    @Override
    public void setFocus() {
        updateStatusLine();
    }

    /**
     * Enables the actions for the current viewer.
     * 
     * @param tabItem - the selected viewer (tab)
     */
    private void setActionEnablement(AbstractJournalEntriesViewerTab tabItem) {

        if (tabItem == null || tabItem.getInput() == null) {
            editSqlAction.setEnabled(false);
            editSqlAction.setChecked(false);
        } else {
            editSqlAction.setEnabled(tabItem.hasSqlEditor());
            editSqlAction.setChecked(tabItem.isSqlEditorVisible());
        }

        openJournalOutputFileAction.setEnabled(true);

        Collection<MetaTable> joesdParser = MetaDataCache.getInstance().getCachedParsers();
        if (joesdParser == null || joesdParser.isEmpty()) {
            configureParsersAction.setEnabled(false);
        } else {
            configureParsersAction.setEnabled(true);
        }

        int numEntries = 0;
        JournalEntryColumn[] columns = null;
        JournalEntries journalEntries = null;
        StructuredSelection selection = new StructuredSelection(new JournalEntry[0]);
        if (tabItem != null) {

            columns = tabItem.getColumns();
            journalEntries = tabItem.getInput();
            if (journalEntries != null) {
                numEntries = journalEntries.size();
            }

            selection = tabItem.getSelection();
        }

        if (numEntries == 0) {
            exportToExcelAction.setColumns(null);
            exportToExcelAction.setEnabled(false);
            exportToExcelAction.setSelectedItems(new JournalEntry[0]);
            reloadEntriesAction.setEnabled(false);
            toggleHighlightUserEntriesAction.setEnabled(false);
            resetColumnSizeAction.setEnabled(false);
            resetColumnSizeAction.setViewer(null);
        } else {
            exportToExcelAction.setColumns(columns);
            exportToExcelAction.setEnabled(true);
            exportToExcelAction.setSelectedItems(journalEntries.getItems().toArray(new JournalEntry[journalEntries.size()]));
            reloadEntriesAction.setEnabled(true);
            toggleHighlightUserEntriesAction.setEnabled(true);
            resetColumnSizeAction.setEnabled(true);
            resetColumnSizeAction.setViewer(getSelectedViewer());
        }

        if (selection != null && selection.size() == 2) {
            compareSideBySideAction.setEnabled(true);
        } else {
            compareSideBySideAction.setEnabled(false);
        }

        List<JournalEntry> selectedItems = new ArrayList<JournalEntry>();
        for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
            Object object = iterator.next();
            if (object instanceof JournalEntry) {
                JournalEntry journalEntry = (JournalEntry)object;
                selectedItems.add(journalEntry);
            }
        }

        JournalEntry[] selectedJournalEntries = selectedItems.toArray(new JournalEntry[selectedItems.size()]);
        compareSideBySideAction.setSelectedItems(selectedJournalEntries);
    }

    /**
     * Called by the viewer, when setSelection() is called.
     */
    public void selectionChanged(SelectionChangedEvent event) {
        updateStatusLine();
    }

    /**
     * Called by the UI, when the user selects a different viewer (tab).
     */
    public void widgetSelected(SelectionEvent event) {
        updateStatusLine();
    }

    public void widgetDefaultSelected(SelectionEvent event) {
        widgetSelected(event);
    }

    private class SqlEditorSelectionListener implements SelectionListener {

        public void widgetSelected(SelectionEvent event) {
            try {
                performFilterJournalEntries(getSelectedViewer());
            } catch (SQLSyntaxErrorException e) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
                getSelectedViewer().setFocusOnSqlEditor();
            } catch (Exception e) {
                ISpherePlugin.logError("*** Error in method JournalExplorerView.SqlEditorSelectionListener.widgetSelected() ***", e);
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            }
        }

        public void widgetDefaultSelected(SelectionEvent event) {
            widgetDefaultSelected(event);
        }
    }
}
