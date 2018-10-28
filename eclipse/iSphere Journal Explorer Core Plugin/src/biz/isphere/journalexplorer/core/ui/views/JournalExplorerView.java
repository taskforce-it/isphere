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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.preferences.DoNotAskMeAgain;
import biz.isphere.core.preferences.DoNotAskMeAgainDialog;
import biz.isphere.journalexplorer.core.Messages;
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
import biz.isphere.journalexplorer.core.ui.actions.ToggleHighlightUserEntriesAction;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumn;
import biz.isphere.journalexplorer.core.ui.widgets.AbstractJournalEntriesViewer;
import biz.isphere.journalexplorer.core.ui.widgets.JournalEntriesViewerForOutputFiles;
import biz.isphere.journalexplorer.core.ui.widgets.JournalEntriesViewerForRetrievedJournalEntries;

public class JournalExplorerView extends ViewPart implements ISelectionChangedListener, SelectionListener {

    public static final String ID = "biz.isphere.journalexplorer.core.ui.views.JournalExplorerView"; //$NON-NLS-1$

    private EditSqlAction editSqlAction;
    private OpenJournalOutfileAction openJournalOutputFileAction;
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
                if (event.item instanceof JournalEntriesViewerForOutputFiles) {
                    cleanupClosedTab((JournalEntriesViewerForOutputFiles)event.item);
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

        exportToExcelAction = new ExportToExcelAction(getSite().getShell());

        openJournalOutputFileAction = new OpenJournalOutfileAction(getSite().getShell()) {
            @Override
            public void postRunAction() {
                OutputFile outputFile = openJournalOutputFileAction.getOutputFile();
                if (outputFile != null) {
                    createJournalTab(outputFile);
                }
            }
        };

        editSqlAction = new EditSqlAction(getSite().getShell()) {
            @Override
            public void postRunAction() {
                AbstractJournalEntriesViewer viewer = getSelectedViewer();
                viewer.setSqlEditorVisibility(editSqlAction.isChecked());
                return;
            }
        };

        compareSideBySideAction = new CompareSideBySideAction(getSite().getShell());

        toggleHighlightUserEntriesAction = new ToggleHighlightUserEntriesAction();

        configureParsersAction = new ConfigureParsersAction(getSite().getShell()) {
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

        JournalEntriesViewerForRetrievedJournalEntries journalEntriesViewer = null;

        try {

            journalEntriesViewer = new JournalEntriesViewerForRetrievedJournalEntries(tabs, jrneToRtv);

            journalEntriesViewer.setAsSelectionProvider(selectionProviderIntermediate);
            journalEntriesViewer.addSelectionChangedListener(this);

            tabs.setSelection(journalEntriesViewer);

            performLoadJournalEntries(journalEntriesViewer);

            setActionEnablement(journalEntriesViewer);

        } catch (Throwable e) {

            if (e instanceof NoJournalEntriesLoadedException) {
                MessageDialog.openInformation(getSite().getShell(), Messages.DisplayJournalEntriesDialog_Title, e.getLocalizedMessage());
            } else {
                ISpherePlugin.logError("*** Error in method JournalExplorerView.createJournalTab(1) ***", e);
                MessageDialog.openError(getSite().getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            }

            if (journalEntriesViewer != null) {
                journalEntriesViewer.removeAsSelectionProvider(selectionProviderIntermediate);
                journalEntriesViewer.dispose();
            }
        }
    }

    private void createJournalTab(OutputFile outputFile) {

        JournalEntriesViewerForOutputFiles journalEntriesViewer = null;

        try {

            journalEntriesViewer = new JournalEntriesViewerForOutputFiles(tabs, outputFile, new SelectionListener() {
                public void widgetSelected(SelectionEvent event) {
                    try {
                        performLoadJournalEntries(getSelectedViewer());
                    } catch (Exception e) {
                        ISpherePlugin.logError("*** Error in method JournalExplorerView.createJournalTab(2) ***", e);
                        MessageDialog.openError(getSite().getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
                    }
                }

                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
            });

            journalEntriesViewer.setAsSelectionProvider(selectionProviderIntermediate);
            journalEntriesViewer.addSelectionChangedListener(this);

            tabs.setSelection(journalEntriesViewer);

            performLoadJournalEntries(journalEntriesViewer);

            setActionEnablement(journalEntriesViewer);

        } catch (Throwable e) {

            ISpherePlugin.logError("*** Error in method JournalExplorerView.createJournalTab(3) ***", e);
            MessageDialog.openError(getSite().getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));

            if (journalEntriesViewer != null) {
                journalEntriesViewer.removeAsSelectionProvider(selectionProviderIntermediate);
                journalEntriesViewer.dispose();
            }
        }
    }

    private void cleanupClosedTab(JournalEntriesViewerForOutputFiles viewer) {

        viewer.removeAsSelectionProvider(selectionProviderIntermediate);
    }

    private void performReloadJournalEntries() {

        try {

            AbstractJournalEntriesViewer viewer = getSelectedViewer();
            performLoadJournalEntries(viewer);

        } catch (Exception e) {
            ISpherePlugin.logError("*** Error in method JournalExplorerView.createJournalTab(4) ***", e);
            MessageDialog.openError(getSite().getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }
    }

    private AbstractJournalEntriesViewer getSelectedViewer() {
        return (AbstractJournalEntriesViewer)tabs.getSelection();
    }

    private void performLoadJournalEntries(AbstractJournalEntriesViewer viewer) throws Exception {

        viewer.openJournal();
        updateStatusLine();

        if (viewer != null) {
            JournalEntries journalEntries = viewer.getInput();
            if (journalEntries != null) {
                int numItems = journalEntries.size();
                int numItemsAvailable = journalEntries.getNumberOfRowsAvailable();
                if (journalEntries.isOverflow()) {
                    String messageText;
                    if (numItemsAvailable < 0) {
                        messageText = Messages.bind(Messages.Warning_Not_all_journal_entries_loaded_unknown_size, numItemsAvailable, numItems);
                    } else {
                        messageText = Messages.bind(Messages.Warning_Not_all_journal_entries_loaded, numItemsAvailable, numItems);
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

        int numItems = -1;
        int numItemsAvailable = -1;

        AbstractJournalEntriesViewer viewer = getSelectedViewer();
        if (viewer != null) {
            JournalEntries journalEntries = viewer.getInput();
            if (journalEntries != null) {
                numItems = journalEntries.size();
                if (journalEntries.isOverflow()) {
                    numItemsAvailable = journalEntries.getNumberOfRowsAvailable();
                }
            }
        }

        IActionBars bars = getViewSite().getActionBars();
        if (numItems >= 0) {
            String message;
            if (numItems < numItemsAvailable) {
                message = Messages.bind(Messages.Number_of_journal_entries_A_of_B, numItems, numItemsAvailable);
            } else {
                message = Messages.bind(Messages.Number_of_journal_entries_A, numItems);
            }
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
    private void setActionEnablement(AbstractJournalEntriesViewer viewer) {

        if (viewer == null || viewer.getInput() == null) {
            editSqlAction.setEnabled(false);
            editSqlAction.setChecked(false);
        } else {
            editSqlAction.setEnabled(viewer.hasSqlEditor());
            editSqlAction.setChecked(viewer.isSqlEditorVisible());
        }

        Collection<MetaTable> joesdParser = MetaDataCache.INSTANCE.getCachedParsers();
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
        } else {
            exportToExcelAction.setColumns(columns);
            exportToExcelAction.setEnabled(true);
            exportToExcelAction.setSelectedItems(journalEntries.getItems().toArray(new JournalEntry[journalEntries.size()]));
            reloadEntriesAction.setEnabled(true);
            toggleHighlightUserEntriesAction.setEnabled(true);
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
    public AbstractJournalEntriesViewer getCurrentViewer() {

        // CTabItem tabItem = tabs.getItem(tabs.getSelectionIndex());
        // if (tabItem instanceof JournalEntriesViewerForOutputFiles) {
        // JournalEntriesViewerForOutputFiles viewer =
        // (JournalEntriesViewerForOutputFiles)tabItem;
        // return viewer;
        // }
        //
        // return null;
        return getSelectedViewer();
    }
}
