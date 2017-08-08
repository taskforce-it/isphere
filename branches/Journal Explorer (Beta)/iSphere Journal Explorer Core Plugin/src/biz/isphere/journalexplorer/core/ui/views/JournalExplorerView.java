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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
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
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.dialogs.AddJournalDialog;
import biz.isphere.journalexplorer.core.ui.dialogs.ConfigureParsersDialog;
import biz.isphere.journalexplorer.core.ui.widgets.JournalEntriesViewer;

public class JournalExplorerView extends ViewPart implements SelectionListener {

    public static final String ID = "biz.isphere.journalexplorer.core.ui.views.JournalExplorerView"; //$NON-NLS-1$

    private Action openJournalAction;
    private Action highlightUserEntries;
    private Action configureJoesdParsers;
    private Action reloadEntries;

    private SelectionProviderIntermediate selectionProviderIntermediate;
    private ArrayList<JournalEntriesViewer> journalViewers;
    private Preferences preferences;

    private CTabFolder tabs;

    public JournalExplorerView() {
        this.selectionProviderIntermediate = new SelectionProviderIntermediate();
        this.journalViewers = new ArrayList<JournalEntriesViewer>();
        this.preferences = Preferences.getInstance();
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

        // /
        // / openJournalAction
        // /
        openJournalAction = new Action(Messages.JournalExplorerView_OpenJournal) {

            @Override
            public void run() {
                performOpenJournal();
            }
        };
        openJournalAction.setImageDescriptor(ISphereJournalExplorerCorePlugin
            .getImageDescriptor(ISphereJournalExplorerCorePlugin.IMAGE_TABLE_BOTTOM_LEFT_CORNER_NEW_GREEN));

        // /
        // / highlightUserEntries action
        // /
        highlightUserEntries = new Action(Messages.JournalExplorerView_HighlightUserEntries, Action.AS_CHECK_BOX) {

            @Override
            public void run() {
                preferences.setHighlightUserEntries(isChecked());
                refreshAllViewers();
            }
        };
        highlightUserEntries
            .setImageDescriptor(ISphereJournalExplorerCorePlugin.getImageDescriptor(ISphereJournalExplorerCorePlugin.IMAGE_HIGHLIGHT));
        highlightUserEntries.setChecked(preferences.isHighlightUserEntries());

        // /
        // / openParserAssociations action
        // /
        configureJoesdParsers = new Action(Messages.JournalEntryView_ConfigureTableDefinitions) {
            @Override
            public void run() {
                performConfigureParsers();
            }
        };

        configureJoesdParsers.setImageDescriptor(ISphereJournalExplorerCorePlugin
            .getImageDescriptor(ISphereJournalExplorerCorePlugin.IMAGE_READ_WRITE_OBJ));

        // /
        // / reParseEntries action
        // /
        reloadEntries = new Action(Messages.JournalEntryView_ReloadEntries) {
            @Override
            public void run() {
                performRefresh();
            }
        };
        reloadEntries.setImageDescriptor(ISphereJournalExplorerCorePlugin.getImageDescriptor(ISphereJournalExplorerCorePlugin.IMAGE_REFRESH));

    }

    private void createJournalTab(File outputFile) {

        JournalEntriesViewer journalViewer = null;

        try {

            journalViewer = new JournalEntriesViewer(tabs, outputFile);
            journalViewer.setAsSelectionProvider(selectionProviderIntermediate);
            journalViewer.openJournal();

            journalViewers.add(journalViewer);
            tabs.setSelection(journalViewer);

            setActionEnablement(journalViewer);

        } catch (Exception exception) {

            MessageDialog.openError(getSite().getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(exception));

            if (journalViewer != null) {
                journalViewer.removeAsSelectionProvider(selectionProviderIntermediate);
                journalViewer.dispose();
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

    private void performOpenJournal() {

        AddJournalDialog addJournalDialog = new AddJournalDialog(getSite().getShell());
        addJournalDialog.create();
        int result = addJournalDialog.open();

        if (result == Window.OK) {
            File outputFile = new File();
            outputFile.setOutFileLibrary(addJournalDialog.getLibrary());
            outputFile.setOutFileName(addJournalDialog.getFileName());
            outputFile.setConnetionName(addJournalDialog.getConnectionName());
            createJournalTab(outputFile);
        }
    }

    protected void performConfigureParsers() {

        ConfigureParsersDialog configureParsersDialog = new ConfigureParsersDialog(getSite().getShell());
        configureParsersDialog.create();
        configureParsersDialog.open();
    }

    private void performRefresh() {

        // JournalEntryView.reParseAllEntries();
        try {
            JournalEntriesViewer viewer = (JournalEntriesViewer)tabs.getSelection();
            viewer.openJournal();
        } catch (Exception exception) {
            MessageDialog.openError(getSite().getShell(), Messages.E_R_R_O_R, exception.getMessage());
        }
    }

    /**
     * Initialize the toolbar.
     */
    private void initializeToolBar() {
        IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
        tbm.add(openJournalAction);
        tbm.add(highlightUserEntries);
        tbm.add(configureJoesdParsers);
        tbm.add(reloadEntries);
    }

    @Override
    public void setFocus() {
    }

    private void setActionEnablement(JournalEntriesViewer viewer) {

        Collection<MetaTable> joesdParser = MetaDataCache.INSTANCE.getCachedParsers();
        if (joesdParser == null || joesdParser.isEmpty()) {
            configureJoesdParsers.setEnabled(false);
        } else {
            configureJoesdParsers.setEnabled(true);
        }

        int numEntries = 0;
        if (viewer != null) {

            JournalEntry[] journalEntries = viewer.getInput();
            if (journalEntries != null) {
                numEntries = journalEntries.length;
            }
        }

        if (numEntries == 0) {
            reloadEntries.setEnabled(false);
            highlightUserEntries.setEnabled(false);
        } else {
            reloadEntries.setEnabled(true);
            highlightUserEntries.setEnabled(true);
        }
    }

    public void widgetDefaultSelected(SelectionEvent event) {
        widgetSelected(event);
    }

    public void widgetSelected(SelectionEvent event) {

        CTabFolder folder = (CTabFolder)event.getSource();
        JournalEntriesViewer viewer = (JournalEntriesViewer)folder.getSelection();
        viewer.getInput();
        setActionEnablement(viewer);
    }
}
