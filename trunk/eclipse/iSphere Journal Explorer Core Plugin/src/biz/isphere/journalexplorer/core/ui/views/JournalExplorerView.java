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

    private CTabFolder tabs;

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

        tabs = new CTabFolder(container, SWT.TOP | SWT.CLOSE);
        tabs.addSelectionListener(this);
        tabs.addCTabFolder2Listener(new CTabFolder2Listener() {
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
                    cleanupClosedTab((AbstractJournalEntriesViewerTab)event.item);
                    setActionEnablement(null);
                    clearStatusLine();
                }

            }
        });

        createActions();
        initializeToolBar();
        getSite().setSelectionProvider(selectionProviderIntermediate);

        setActionEnablement(null);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Create the actions.
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
                AbstractJournalEntriesViewerTab viewer = getSelectedViewer();
                viewer.setSqlEditorVisibility(editSqlAction.isChecked());
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

            journalEntriesViewer = new JournalEntriesViewerForRetrievedJournalEntriesTab(tabs, jrneToRtv, new SqlEditorSelectionListener());

            journalEntriesViewer.setAsSelectionProvider(selectionProviderIntermediate);
            journalEntriesViewer.addSelectionChangedListener(this);

            tabs.setSelection(journalEntriesViewer);

            performLoadJournalEntries(journalEntriesViewer);

            setActionEnablement(journalEntriesViewer);

        } catch (Throwable e) {
            handleDataLoadException(journalEntriesViewer, e);
        }
    }

    private void createJournalTab(OutputFile outputFile, String whereClause) {

        JournalEntriesViewerForOutputFilesTab journalEntriesViewer = null;

        try {

            journalEntriesViewer = new JournalEntriesViewerForOutputFilesTab(tabs, outputFile, whereClause, new SqlEditorSelectionListener());

            journalEntriesViewer.setAsSelectionProvider(selectionProviderIntermediate);
            journalEntriesViewer.addSelectionChangedListener(this);

            tabs.setSelection(journalEntriesViewer);

            performLoadJournalEntries(journalEntriesViewer);

            setActionEnablement(journalEntriesViewer);

        } catch (Throwable e) {

            ISpherePlugin.logError("*** Error in method JournalExplorerView.createJournalTab ***", e);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));

            if (journalEntriesViewer != null) {
                journalEntriesViewer.removeAsSelectionProvider(selectionProviderIntermediate);
                journalEntriesViewer.dispose();
            }
        }
    }

    public void handleDataLoadException(AbstractJournalEntriesViewerTab journalEntriesViewer, Throwable e) {

        updateStatusLine();

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

        if (journalEntriesViewer != null) {
            journalEntriesViewer.removeAsSelectionProvider(selectionProviderIntermediate);
            journalEntriesViewer.dispose();
        }
    }

    private void cleanupClosedTab(AbstractJournalEntriesViewerTab viewer) {

        viewer.removeAsSelectionProvider(selectionProviderIntermediate);
    }

    private void performReloadJournalEntries() {

        try {

            AbstractJournalEntriesViewerTab viewer = getSelectedViewer();
            performLoadJournalEntries(viewer);

        } catch (Exception e) {
            ISpherePlugin.logError("*** Error in method JournalExplorerView.performReloadJournalEntries() ***", e);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }
    }

    private AbstractJournalEntriesViewerTab getSelectedViewer() {
        return (AbstractJournalEntriesViewerTab)tabs.getSelection();
    }

    private void performLoadJournalEntries(AbstractJournalEntriesViewerTab viewer) throws Exception {

        String initialWhereClause = viewer.getSelectClause();
        String filterWhereClause = viewer.getFilterClause();

        viewer.validateWhereClause(getViewSite().getShell(), initialWhereClause);
        viewer.validateWhereClause(getViewSite().getShell(), filterWhereClause);

        viewer.closeJournal();
        updateStatusLine();
        viewer.openJournal(this, initialWhereClause, filterWhereClause);
    }

    private void performFilterJournalEntries(AbstractJournalEntriesViewerTab viewer) throws Exception {

        viewer.validateWhereClause(getViewSite().getShell(), viewer.getFilterClause());

        viewer.filterJournal(this, viewer.getFilterClause());
    }

    public void finishDataLoading(AbstractJournalEntriesViewerTab viewer, boolean isFilter) {

        if (viewer != null) {
            JournalEntries journalEntries = viewer.getInput();
            if (journalEntries != null) {
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

    private void clearStatusLine() {

        IActionBars bars = getViewSite().getActionBars();
        bars.getStatusLineManager().setMessage(""); //$NON-NLS-1$
    }

    private void updateStatusLine() {

        String message = null;

        IActionBars bars = getViewSite().getActionBars();
        AbstractJournalEntriesViewerTab viewer = getSelectedViewer();
        if (viewer != null) {

            if (viewer.isLoading()) {
                message = null;
            } else {
                JournalEntries journalEntries = viewer.getInput();
                if (journalEntries != null) {
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
                    if (viewer.isFiltered()) {
                        message += " (" + Messages.subsetted_list + ")";
                    }
                }
            }
        }

        if (message != null) {
            bars.getStatusLineManager().setMessage(message);
        } else {
            clearStatusLine();
        }
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
     * Enables the actions for the current viewer,
     * 
     * @param viewer - the selected viewer (tab)
     */
    private void setActionEnablement(AbstractJournalEntriesViewerTab viewer) {

        if (viewer == null || viewer.getInput() == null) {
            editSqlAction.setEnabled(false);
            editSqlAction.setChecked(false);
        } else {
            editSqlAction.setEnabled(viewer.hasSqlEditor());
            editSqlAction.setChecked(viewer.isSqlEditorVisible());
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
        if (viewer != null) {

            columns = viewer.getColumns();
            journalEntries = viewer.getInput();
            if (journalEntries != null) {
                numEntries = journalEntries.size();
            }

            selection = viewer.getSelection();
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

    private Shell getShell() {
        return getSite().getShell();
    }

    /**
     * Called by the viewer, when items are selected by the user.
     */
    public void selectionChanged(SelectionChangedEvent event) {
        setActionEnablement(getCurrentViewer());
        updateStatusLine();
    }

    /**
     * Called by the UI, when the user selects a different viewer (tab).
     */
    public void widgetSelected(SelectionEvent event) {

        setActionEnablement(getCurrentViewer());

        // Trigger a SWT selection event
        getCurrentViewer().setSelection(getCurrentViewer().getSelection());
    }

    public void widgetDefaultSelected(SelectionEvent event) {
        widgetSelected(event);
    }

    /**
     * Returns the currently selected viewer (tab).
     * 
     * @return selected viewer
     */
    public AbstractJournalEntriesViewerTab getCurrentViewer() {
        return getSelectedViewer();
    }

    private class SqlEditorSelectionListener implements SelectionListener {
        public void widgetSelected(SelectionEvent event) {
            try {
                getSelectedViewer().storeSqlEditorHistory();
                refreshSqlEditorHistory();
                performFilterJournalEntries(getSelectedViewer());
            } catch (SQLSyntaxErrorException e) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
                getSelectedViewer().setFocusOnSqlEditor();
            } catch (Exception e) {
                ISpherePlugin.logError("*** Error in method JournalExplorerView.SqlEditorSelectionListener.widgetSelected() ***", e);
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            }
        }

        private void refreshSqlEditorHistory() {
            for (CTabItem tabItem : tabs.getItems()) {
                ((AbstractJournalEntriesViewerTab)tabItem).refreshSqlEditorHistory();
            }
        }

        public void widgetDefaultSelected(SelectionEvent arg0) {
        }
    }
}
