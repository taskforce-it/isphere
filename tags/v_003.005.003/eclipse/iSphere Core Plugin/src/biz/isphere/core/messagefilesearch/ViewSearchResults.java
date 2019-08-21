/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilesearch;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import biz.isphere.base.internal.FileHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.FilterDialog;
import biz.isphere.core.internal.IMessageFileSearchObjectFilterCreator;
import biz.isphere.core.internal.exception.LoadFileException;
import biz.isphere.core.internal.exception.SaveFileException;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.resourcemanagement.filter.RSEFilter;
import biz.isphere.core.search.DisplaySearchOptionsDialog;
import biz.isphere.core.search.SearchOptions;
import biz.isphere.core.swt.widgets.extension.handler.WidgetFactoryContributionsHandler;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;

public class ViewSearchResults extends ViewPart implements ISelectionChangedListener {

    private static final String TAB_DATA_VIEWER = "Viewer"; //$NON-NLS-1$
    private static final String TAB_PERSISTENCE_DATA = "persistenceData"; //$NON-NLS-1$

    private Action actionExportToObjectFilter;
    private Action actionExportToExcel;
    private Action actionRemoveTabItem;
    private Action actionRemoveSelectedItems;
    private Action actionInvertSelectedItems;
    private Action actionLoadSearchResult;
    private Action actionSaveSearchResult;
    private Action actionSaveAllSearchResults;
    private Action actionEnableAutoSave;

    private CTabFolder tabFolderSearchResults;
    private Shell shell;
    private SearchResultManager manager;

    private SearchResultTabFolder searchResultTabFolder;

    @Override
    public void createPartControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout());

        shell = parent.getShell();
        manager = new SearchResultManager();
        searchResultTabFolder = new SearchResultTabFolder();

        tabFolderSearchResults = new CTabFolder(container, SWT.NONE);
        tabFolderSearchResults.addCTabFolder2Listener(new CTabFolder2Adapter() {
            public void close(CTabFolderEvent event) {
                if (event instanceof CTabFolderEvent) {
                    CTabFolderEvent folderEvent = (CTabFolderEvent)event;
                    removeTabItem((CTabItem)folderEvent.item);
                }
            }
        });

        tabFolderSearchResults.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setActionEnablement();
            }
        });

        tabFolderSearchResults.addFocusListener(new FocusListener() {

            public void focusLost(FocusEvent arg0) {
                setActionEnablement();
            }

            public void focusGained(FocusEvent arg0) {
                setActionEnablement();
            }
        });

        Menu popUpMenu = new Menu(tabFolderSearchResults);
        MenuItem menuItem = new MenuItem(popUpMenu, SWT.PUSH);
        menuItem.setText(Messages.MenuItem_Display_Search_Options);
        menuItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                SearchResultTab searchResultTab = (SearchResultTab)getSelectedTab().getData(TAB_PERSISTENCE_DATA);
                if (searchResultTab.hasSearchOptions()) {
                    DisplaySearchOptionsDialog dialog = new DisplaySearchOptionsDialog(shell);
                    dialog.setInput(searchResultTab);
                    dialog.open();
                } else {
                    MessageDialog.openError(shell, Messages.E_R_R_O_R, Messages.Error_No_Search_Options_available);
                }
            }
        });

        tabFolderSearchResults.setMenu(popUpMenu);

        createActions();
        initializeToolBar();
        initializeMenu();

        loadAutoSaveSearchResults();

        setActionEnablement();
    }

    private void createActions() {

        actionExportToObjectFilter = new Action("") { //$NON-NLS-1$
            @Override
            public void run() {
                exportToObjectFilter();
            }
        };
        actionExportToObjectFilter.setToolTipText(Messages.Export_to_Object_Filter);
        actionExportToObjectFilter.setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_OBJECT_FILTER));
        actionExportToObjectFilter.setEnabled(false);

        actionExportToExcel = new Action("") { //$NON-NLS-1$
            @Override
            public void run() {
                exportToExcel();
            }
        };
        actionExportToExcel.setToolTipText(Messages.Export_to_Excel);
        actionExportToExcel.setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_EXCEL));
        actionExportToExcel.setEnabled(false);

        actionRemoveTabItem = new Action("") { //$NON-NLS-1$
            @Override
            public void run() {
                removeSelectedTabItem();
            }
        };
        actionRemoveTabItem.setToolTipText(Messages.Remove_tab_item);
        actionRemoveTabItem.setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_MINUS));
        actionRemoveTabItem.setEnabled(false);

        actionRemoveSelectedItems = new Action("") { //$NON-NLS-1$
            @Override
            public void run() {
                removeSelectedItem();
            }
        };
        actionRemoveSelectedItems.setToolTipText(Messages.Tooltip_Remove);
        actionRemoveSelectedItems.setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_REMOVE));
        actionRemoveSelectedItems.setEnabled(false);

        actionInvertSelectedItems = new Action("") { //$NON-NLS-1$
            @Override
            public void run() {
                invertSelection();
            }
        };
        actionInvertSelectedItems.setToolTipText(Messages.Tooltip_Invert_selection);
        actionInvertSelectedItems.setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry()
            .getDescriptor(ISpherePlugin.IMAGE_INVERT_SELECTION));
        actionInvertSelectedItems.setEnabled(false);

        actionLoadSearchResult = new Action(Messages.Load) {
            @Override
            public void run() {
                loadSearchResult();
            };
        };
        actionLoadSearchResult.setEnabled(false);

        actionSaveSearchResult = new Action(Messages.Save) {
            @Override
            public void run() {
                saveSearchResult();
            };
        };
        actionSaveSearchResult.setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_SAVE));
        actionSaveSearchResult.setEnabled(false);

        actionSaveAllSearchResults = new Action(Messages.Save_all) {
            @Override
            public void run() {
                saveAllSearchResults();
            };
        };
        actionSaveAllSearchResults.setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_SAVE_ALL));
        actionSaveAllSearchResults.setEnabled(false);

        actionEnableAutoSave = new EnableAutoSaveAction();
        actionEnableAutoSave.setEnabled(false);
    }

    private void initializeToolBar() {
        IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
        toolbarManager.add(actionRemoveSelectedItems);
        toolbarManager.add(actionInvertSelectedItems);
        toolbarManager.add(new Separator());
        toolbarManager.add(actionExportToObjectFilter);
        toolbarManager.add(actionExportToExcel);
        toolbarManager.add(new Separator());
        toolbarManager.add(actionRemoveTabItem);
    }

    private void initializeMenu() {

        /*
         * Does not work, because we cannot create an AS400 object, when loading
         * a search result without the contributions handler.
         */
        if (!IBMiHostContributionsHandler.hasContribution()) {
            return;
        }

        IActionBars actionBars = getViewSite().getActionBars();
        IMenuManager viewMenu = actionBars.getMenuManager();

        viewMenu.add(actionLoadSearchResult);
        viewMenu.add(new Separator());
        viewMenu.add(actionSaveSearchResult);
        viewMenu.add(actionSaveAllSearchResults);
        viewMenu.add(new Separator());
        viewMenu.add(actionEnableAutoSave);
    }

    @Override
    public void setFocus() {
    }

    public void addTabItem(String connectionName, String searchString, SearchResult[] searchResults, SearchOptions searchOptions) {
        Composite compositeSearchResult = new Composite(tabFolderSearchResults, SWT.NONE);
        compositeSearchResult.setLayout(new FillLayout());

        CTabItem tabItemSearchResult = new CTabItem(tabFolderSearchResults, SWT.CLOSE);
        tabItemSearchResult.setText(connectionName + "/" + searchString); //$NON-NLS-1$

        SearchResultViewer _searchResultViewer = new SearchResultViewer(connectionName, searchString, searchResults, searchOptions);
        _searchResultViewer.createContents(compositeSearchResult);
        _searchResultViewer.addSelectionChangedListener(this);

        tabItemSearchResult.setControl(compositeSearchResult);
        tabItemSearchResult.setData(TAB_DATA_VIEWER, _searchResultViewer);

        tabFolderSearchResults.setSelection(tabItemSearchResult);

        actionExportToObjectFilter.setEnabled(true);
        actionExportToExcel.setEnabled(true);
        actionRemoveTabItem.setEnabled(true);

        setActionEnablement();

        SearchResultTab searchResultTab = new SearchResultTab(connectionName, searchString, searchResults, searchOptions);
        searchResultTabFolder.addTab(searchResultTab);
        tabItemSearchResult.setData(TAB_PERSISTENCE_DATA, searchResultTab);
        tabItemSearchResult.setToolTipText(searchResultTab.toText());
    }

    public void exportToObjectFilter() {

        IMessageFileSearchObjectFilterCreator creator = ISpherePlugin.getMessageFileSearchObjectFilterCreator();

        if (creator != null) {

            int selectedTabItem = tabFolderSearchResults.getSelectionIndex();

            if (selectedTabItem >= 0) {

                SearchResultViewer _searchResultViewer = (SearchResultViewer)tabFolderSearchResults.getItem(selectedTabItem).getData(TAB_DATA_VIEWER);

                if (_searchResultViewer != null) {

                    FilterDialog dialog = new FilterDialog(shell, RSEFilter.TYPE_OBJECT);
                    dialog.setFilterPools(creator.getFilterPools(_searchResultViewer.getConnectionName()));
                    if (dialog.open() == Dialog.OK) {
                        if (!creator.createObjectFilter(_searchResultViewer.getConnectionName(), dialog.getFilterPool(), dialog.getFilter(),
                            dialog.getFilterUpdateType(), _searchResultViewer.getSearchResults())) {
                        }
                    }

                }

            }

        }

    }

    public void exportToExcel() {

        SearchResultViewer viewer = getSelectedViewer();
        if (viewer != null) {
            SearchOptions _searchOptions = viewer.getSearchOptions();
            SearchResult[] _searchResults = viewer.getSearchResults();
            MessageFilesToExcelExporter exporter = new MessageFilesToExcelExporter(getSite().getShell(), _searchOptions, _searchResults);
            exporter.export();
        }
    }

    private void removeAllTabItems() {

        CTabItem[] tabItems = tabFolderSearchResults.getItems();
        for (CTabItem tabItem : tabItems) {
            removeTabItem(tabItem);
        }
    }

    public void removeSelectedTabItem() {
        int selectedTabItem = tabFolderSearchResults.getSelectionIndex();
        if (selectedTabItem >= 0) {
            CTabItem tabItemSearchResult = tabFolderSearchResults.getItem(selectedTabItem);
            removeTabItem(tabItemSearchResult);
        }
    }

    private void removeTabItem(CTabItem tabItem) {
        SearchResultTab searchResultTab = (SearchResultTab)tabItem.getData(TAB_PERSISTENCE_DATA);
        searchResultTabFolder.removeTab(searchResultTab);

        tabItem.dispose();
        setActionEnablement();
    }

    public void removeSelectedItem() {

        int selectedTabItem = tabFolderSearchResults.getSelectionIndex();

        if (selectedTabItem >= 0) {
            SearchResultViewer _searchResultViewer = (SearchResultViewer)tabFolderSearchResults.getItem(selectedTabItem).getData(TAB_DATA_VIEWER);
            if (_searchResultViewer != null) {
                _searchResultViewer.removeSelectedItems();
            }
        }

    }

    public void invertSelection() {

        int selectedTabItem = tabFolderSearchResults.getSelectionIndex();

        if (selectedTabItem >= 0) {
            SearchResultViewer _searchResultViewer = (SearchResultViewer)tabFolderSearchResults.getItem(selectedTabItem).getData(TAB_DATA_VIEWER);
            if (_searchResultViewer != null) {
                _searchResultViewer.invertSelectedItems();
            }
        }

    }

    private void saveSearchResult() {

        String file = selectFile(SWT.SAVE);
        if (file == null) {
            return;
        }

        SearchResultViewer viewer = getSelectedViewer();
        if (viewer != null) {
            SearchResultTabFolder searchResults = new SearchResultTabFolder();
            searchResults.addTab(new SearchResultTab(viewer.getConnectionName(), viewer.getSearchString(), viewer.getSearchResults(), viewer
                .getSearchOptions()));
            try {
                manager.saveToXml(file, searchResults);
            } catch (SaveFileException e) {
                MessageDialog.openError(shell, Messages.E_R_R_O_R, e.getLocalizedMessage());
            }
        }
    }

    private void saveAllSearchResults() {

        String file = selectFile(SWT.SAVE);
        if (file == null) {
            return;
        }

        autoSaveAllSearchResults(file);
    }

    private void autoSaveAllSearchResults(String fileName) {

        try {
            manager.saveToXml(fileName, searchResultTabFolder);
        } catch (SaveFileException e) {
            MessageDialog.openError(shell, Messages.E_R_R_O_R, e.getLocalizedMessage());
        }
    }

    private void loadSearchResult() {

        String file = selectFile(SWT.OPEN);
        if (file == null) {
            return;
        }

        loadSearchResult(file, false);
    }

    private void loadSearchResult(String fileName, boolean replace) {

        SearchResultTabFolder searchResults = null;
        try {

            searchResults = manager.loadFromXml(fileName);
            if (searchResults == null || searchResults.getNumTabs() <= 0) {
                return;
            }

            if (!replace && searchResults.getNumTabs() > 1 && tabFolderSearchResults.getItemCount() > 0) {
                if (MessageDialog.openQuestion(shell, Messages.Question,
                    Messages.bind(Messages.Question_replace_search_results, searchResults.getNumTabs()))) {
                    removeAllTabItems();
                    ;
                }
            }

            for (SearchResultTab tab : searchResults.getTabs()) {
                String connectionName = tab.getConnectionName();
                String searchString = tab.getSearchString();
                SearchResult[] searchResult = tab.getSearchResult();
                SearchOptions searchOptions = tab.getSearchOptions();
                addTabItem(connectionName, searchString, searchResult, searchOptions);
            }

        } catch (LoadFileException e) {
            MessageDialog.openError(shell, Messages.E_R_R_O_R, e.getLocalizedMessage());
            return;
        }
    }

    private String selectFile(int style) {

        WidgetFactoryContributionsHandler factory = new WidgetFactoryContributionsHandler();
        IFileDialog dialog = factory.getFileDialog(shell, style);

        String[] filterNames = new String[] {
            "Search file search result (*." + SearchResultManager.FILE_EXTENSION + ")", FileHelper.getAllFilesText() }; //$NON-NLS-1$ //$NON-NLS-2$
        String[] filterExtensions = new String[] { "*." + SearchResultManager.FILE_EXTENSION + ";", FileHelper.getAllFilesFilter() }; //$NON-NLS-1$  //$NON-NLS-2$
        String filename = Preferences.getInstance().getMessageFileSearchResultsLastUsedFileName();

        dialog.setFilterNames(filterNames);
        dialog.setFilterExtensions(filterExtensions);
        dialog.setFileName(filename);
        dialog.setOverwrite(true);

        String selectedFileName = dialog.open();
        if (!StringHelper.isNullOrEmpty(selectedFileName) && !filename.equals(selectedFileName)) {
            Preferences.getInstance().setMessageFileSearchResultsLastUsedFileName(selectedFileName);
        }

        return selectedFileName;
    }

    private void setActionEnablement() {
        selectionChanged(null);
    }

    public void selectionChanged(SelectionChangedEvent event) {

        SearchResultViewer viewer;
        int index = tabFolderSearchResults.getSelectionIndex();
        if (index < 0) {
            viewer = null;
        } else {
            viewer = (SearchResultViewer)tabFolderSearchResults.getItem(index).getData(TAB_DATA_VIEWER);
        }

        boolean hasViewer;
        boolean hasTabItems;
        boolean hasMultipleTabItems;
        boolean hasSelectedItems;
        if (viewer == null) {
            hasViewer = false;
            hasTabItems = false;
            hasMultipleTabItems = false;
            hasSelectedItems = false;
        } else {
            hasViewer = true;
            hasTabItems = viewer.hasItems();
            hasMultipleTabItems = tabFolderSearchResults.getItemCount() > 1;
            hasSelectedItems = viewer.hasSelectedItems();
        }

        actionRemoveSelectedItems.setEnabled(hasSelectedItems);
        actionInvertSelectedItems.setEnabled(hasSelectedItems);
        actionExportToObjectFilter.setEnabled(hasTabItems);
        actionExportToExcel.setEnabled(hasTabItems);
        actionRemoveTabItem.setEnabled(hasViewer);

        actionSaveSearchResult.setEnabled(hasTabItems);
        actionSaveAllSearchResults.setEnabled(hasMultipleTabItems);
        actionLoadSearchResult.setEnabled(true);
        actionEnableAutoSave.setEnabled(true);
    }

    private SearchResultViewer getSelectedViewer() {
        return getViewer(getSelectedTab());
    }

    private CTabItem getSelectedTab() {
        int selectedTabItem = tabFolderSearchResults.getSelectionIndex();
        if (selectedTabItem >= 0) {
            return tabFolderSearchResults.getItem(selectedTabItem);
        } else {
            return null;
        }
    }

    private SearchResultViewer getViewer(CTabItem tabItem) {
        if (tabItem != null) {
            return (SearchResultViewer)tabItem.getData(TAB_DATA_VIEWER);
        } else {
            return null;
        }
    }

    /**
     * 
     */
    private void loadAutoSaveSearchResults() {

        try {

            Preferences preferences = Preferences.getInstance();

            if (preferences.isMessageFileSearchResultsAutoSaveEnabled()) {
                String fileName = preferences.getMessageFileSearchResultsSaveDirectory() + preferences.getMessageFileSearchResultsAutoSaveFileName();
                File file = new File(fileName);
                if (file.exists()) {
                    loadSearchResult(fileName, true);
                }
            }

        } catch (Throwable e) {
            ISpherePlugin.logError("Failed to load source file search results.", e); //$NON-NLS-1$
        }
    }

    @Override
    public void dispose() {

        try {

            Preferences preferences = Preferences.getInstance();

            if (preferences.isMessageFileSearchResultsAutoSaveEnabled()) {
                String fileName = preferences.getMessageFileSearchResultsSaveDirectory() + preferences.getMessageFileSearchResultsAutoSaveFileName();
                autoSaveAllSearchResults(fileName);
            }

        } catch (Throwable e) {
            ISpherePlugin.logError("Failed to save source file search results.", e); //$NON-NLS-1$
        }

        super.dispose();
    }

    private class EnableAutoSaveAction extends Action {

        public EnableAutoSaveAction() {
            super(Messages.Auto_save, SWT.CHECK);

            setChecked(Preferences.getInstance().isMessageFileSearchResultsAutoSaveEnabled());
            addPreferencesChangeListener();
        }

        public void run() {
            Preferences.getInstance().setMessageFileSearchResultsAutoSaveEnabled(isChecked());
        };

        private void addPreferencesChangeListener() {
            IPreferenceStore preferenceStore = ISpherePlugin.getDefault().getPreferenceStore();
            preferenceStore.addPropertyChangeListener(new IPropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent event) {
                    if (event.getProperty() == Preferences.SOURCE_FILE_SEARCH_RESULTS_IS_AUTO_SAVE_ENABLED) {
                        boolean enabled = (Boolean)event.getNewValue();
                        setChecked(enabled);
                    }
                }
            });
        }
    }
}
