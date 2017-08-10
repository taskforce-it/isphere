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
import org.eclipse.ui.part.ViewPart;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.internals.SelectionProviderIntermediate;
import biz.isphere.journalexplorer.core.model.File;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.MetaTable;
import biz.isphere.journalexplorer.core.ui.actions.ConfigureParsersAction;
import biz.isphere.journalexplorer.core.ui.actions.GenericRefreshAction;
import biz.isphere.journalexplorer.core.ui.actions.OpenJournalOutfileAction;
import biz.isphere.journalexplorer.core.ui.actions.ShowSideBySideAction;
import biz.isphere.journalexplorer.core.ui.actions.ToggleHighlightUserEntriesAction;
import biz.isphere.journalexplorer.core.ui.widgets.JournalEntriesViewer;

public class JournalExplorerView extends ViewPart implements ISelectionChangedListener, SelectionListener {

    public static final String ID = "biz.isphere.journalexplorer.core.ui.views.JournalExplorerView"; //$NON-NLS-1$

    private OpenJournalOutfileAction openJournalOutputFileAction;
    private ShowSideBySideAction showSideBySideAction;
    private ToggleHighlightUserEntriesAction toggleHighlightUserEntriesAction;
    private ConfigureParsersAction configureParsersAction;
    private GenericRefreshAction reloadEntriesAction;

    private SelectionProviderIntermediate selectionProviderIntermediate;
    private ArrayList<JournalEntriesViewer> journalViewers;

    private CTabFolder tabs;

    public JournalExplorerView() {
        this.selectionProviderIntermediate = new SelectionProviderIntermediate();
        this.journalViewers = new ArrayList<JournalEntriesViewer>();
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
                if (event.item instanceof JournalEntriesViewer) {
                    cleanupClosedTab((JournalEntriesViewer)event.item);
                    setActionEnablement(null);
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

        openJournalOutputFileAction = new OpenJournalOutfileAction(getSite().getShell()) {
            @Override
            public void postRunAction() {
                File outputFile = openJournalOutputFileAction.getOutputFile();
                createJournalTab(outputFile);
            }
        };

        showSideBySideAction = new ShowSideBySideAction(getSite().getShell());

        toggleHighlightUserEntriesAction = new ToggleHighlightUserEntriesAction() {
            @Override
            public void postRunAction() {
                refreshAllViewers();
            }
        };

        configureParsersAction = new ConfigureParsersAction(getSite().getShell()) {
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

    private void createJournalTab(File outputFile) {

        JournalEntriesViewer journalEntriesViewer = null;

        try {

            journalEntriesViewer = new JournalEntriesViewer(tabs, outputFile);
            journalEntriesViewer.setAsSelectionProvider(selectionProviderIntermediate);
            journalEntriesViewer.addSelectionChangedListener(this);

            journalViewers.add(journalEntriesViewer);
            tabs.setSelection(journalEntriesViewer);

            performLoadJournalEntries(journalEntriesViewer);

            setActionEnablement(journalEntriesViewer);

        } catch (Exception exception) {

            MessageDialog.openError(getSite().getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(exception));

            if (journalEntriesViewer != null) {
                journalEntriesViewer.removeAsSelectionProvider(selectionProviderIntermediate);
                journalEntriesViewer.dispose();
            }
        }
    }

    private void cleanupClosedTab(JournalEntriesViewer viewer) {

        viewer.removeAsSelectionProvider(selectionProviderIntermediate);
        journalViewers.remove(viewer);
    }

    private void refreshAllViewers() {
        for (JournalEntriesViewer viewer : journalViewers) {
            viewer.refreshTable();
        }
    }

    private void performReloadJournalEntries() {

        JournalEntriesViewer viewer = (JournalEntriesViewer)tabs.getSelection();
        performLoadJournalEntries(viewer);
    }

    private void performLoadJournalEntries(JournalEntriesViewer viewer) {

        try {
            viewer.openJournal();
        } catch (Exception e) {
            ISphereJournalExplorerCorePlugin.logError(ExceptionHelper.getLocalizedMessage(e), e);
            MessageDialog.openError(getSite().getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }
    }

    /**
     * Initialize the toolbar.
     */
    private void initializeToolBar() {

        IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
        tbm.add(openJournalOutputFileAction);
        tbm.add(toggleHighlightUserEntriesAction);
        tbm.add(showSideBySideAction);
        tbm.add(configureParsersAction);
        tbm.add(reloadEntriesAction);
    }

    @Override
    public void setFocus() {
    }

    private void setActionEnablement(JournalEntriesViewer viewer) {

        Collection<MetaTable> joesdParser = MetaDataCache.INSTANCE.getCachedParsers();
        if (joesdParser == null || joesdParser.isEmpty()) {
            configureParsersAction.setEnabled(false);
        } else {
            configureParsersAction.setEnabled(true);
        }

        int numEntries = 0;
        if (viewer != null) {

            JournalEntry[] journalEntries = viewer.getInput();
            if (journalEntries != null) {
                numEntries = journalEntries.length;
            }
        }

        if (numEntries == 0) {
            reloadEntriesAction.setEnabled(false);
            toggleHighlightUserEntriesAction.setEnabled(false);

            showSideBySideAction.setEnabled(false);
        } else {
            reloadEntriesAction.setEnabled(true);
            toggleHighlightUserEntriesAction.setEnabled(true);
        }
    }

    public void selectionChanged(SelectionChangedEvent event) {

        showSideBySideAction.setEnabled(false);

        Object selection = event.getSelection();
        if (selection instanceof StructuredSelection) {

            StructuredSelection strucuredSelection = (StructuredSelection)selection;

            if (strucuredSelection.size() == 2) {
                showSideBySideAction.setEnabled(true);
            }

            List<JournalEntry> selectedItems = new ArrayList<JournalEntry>();
            for (Iterator<?> iterator = strucuredSelection.iterator(); iterator.hasNext();) {
                Object object = iterator.next();
                if (object instanceof JournalEntry) {
                    JournalEntry journalEntry = (JournalEntry)object;
                    selectedItems.add(journalEntry);
                }
            }

            showSideBySideAction.setSelectedItems(selectedItems.toArray(new JournalEntry[selectedItems.size()]));
        }
    }

    public void widgetDefaultSelected(SelectionEvent event) {
        widgetSelected(event);
    }

    public void widgetSelected(SelectionEvent event) {
        Object source = event.getSource();
        if (source instanceof CTabFolder) {
            CTabFolder tabFolder = (CTabFolder)source;
            CTabItem tabItem = tabFolder.getItem(tabFolder.getSelectionIndex());
            if (tabItem instanceof JournalEntriesViewer) {
                JournalEntriesViewer viewer = (JournalEntriesViewer)tabItem;
                setActionEnablement(viewer);
            }
        }
    }
}
