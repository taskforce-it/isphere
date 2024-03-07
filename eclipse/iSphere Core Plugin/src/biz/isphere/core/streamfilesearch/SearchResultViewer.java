/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.streamfilesearch;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.base.internal.ClipboardHelper;
import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.internal.IResizableTableColumnsViewer;
import biz.isphere.base.internal.UIHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.DateTimeHelper;
import biz.isphere.core.internal.FilterDialog;
import biz.isphere.core.internal.IStreamFileEditor;
import biz.isphere.core.internal.IStreamFileSearchIFSFilterCreator;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.resourcemanagement.filter.RSEFilter;
import biz.isphere.core.search.SearchOptions;

public class SearchResultViewer implements IResizableTableColumnsViewer {

    private String connectionName;
    private String searchString;
    private SearchResult[] _searchResults;
    private SearchOptions _searchOptions;
    private TableViewer tableViewerStreamFiles;
    private Table tableStreamFiles;
    private Object[] selectedItemsStreamFiles;
    private Shell shell;
    private TableViewer tableViewerStatements;
    private Table tableStatements;
    private String[] statements;
    private boolean isEditMode;

    private DialogSettingsManager dialogSettingsManager;

    private class LabelProviderTableViewerStreamFiles extends LabelProvider implements ITableLabelProvider {

        private static final String UNKNOWN = "*UNKNOWN"; //$NON-NLS-1$

        public String getColumnText(Object element, int columnIndex) {
            SearchResult searchResult = (SearchResult)element;
            if (columnIndex == 0) {
                return searchResult.getDirectory();
            } else if (columnIndex == 1) {
                return searchResult.getStreamFile();
            } else if (columnIndex == 2) {
                return searchResult.getType();
            } else if (columnIndex == 3) {
                Timestamp lastChangedDate = searchResult.getLastChangedDate();
                return DateTimeHelper.getTimestampFormatted(lastChangedDate);
            } else if (columnIndex == 4) {
                return Integer.toString(searchResult.getStatementsCount());
            }
            return UNKNOWN;
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

    }

    private class ContentProviderTableViewerStreamFiles implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            return _searchResults;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    private class SorterTableViewerStreamFiles extends ViewerSorter {

        private TableColumn initialColumn;
        private int initialDirection;
        private TableViewer tableViewer;

        public SorterTableViewerStreamFiles(TableViewer tableViewer, TableColumn column, int direction) {

            this.initialColumn = column;
            this.initialDirection = direction;

            this.tableViewer = tableViewer;
            this.tableViewer.getTable().setSortColumn(column);
            this.tableViewer.getTable().setSortDirection(direction);
        }

        public void setOrder(TableColumn column) {

            int direction = changeSortDirection(column);
            if (direction == SWT.NONE) {
                direction = initialDirection;
                column = null;
            }

            this.tableViewer.getTable().setSortDirection(direction);
            this.tableViewer.getTable().setSortColumn(column);
        }

        private int changeSortDirection(TableColumn column) {

            if (column == tableViewer.getTable().getSortColumn()) {
                if (tableViewer.getTable().getSortDirection() == SWT.NONE) {
                    return SWT.UP;
                } else if (tableViewer.getTable().getSortDirection() == SWT.UP) {
                    return SWT.DOWN;
                } else {
                    return SWT.NONE;
                }
            } else {
                return SWT.UP;
            }
        }

        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {

            int result;

            TableColumn column = tableViewer.getTable().getSortColumn();
            if (column == null) {
                column = initialColumn;
            }

            if (Messages.Directory.equals(column.getText())) {
                result = sortByDirectory(viewer, e1, e2);
            } else if (Messages.Stream_file.equals(column.getText())) {
                result = sortByStreamFile(viewer, e1, e2);
            } else if (Messages.Type.equals(column.getText())) {
                result = sortByType(viewer, e1, e2);
            } else if (Messages.Last_changed.equals(column.getText())) {
                result = sortByLastChangedDate(viewer, e1, e2);
            } else if (Messages.StatementsCount.equals(column.getText())) {
                result = sortByStatementsCount(viewer, e1, e2);
            } else {
                result = sortByDirectory(viewer, e1, e2);
            }

            if (tableViewer.getTable().getSortDirection() == SWT.DOWN) {
                result = result * -1;
            }

            return result;
        }

        private int sortByDirectory(Viewer viewer, Object e1, Object e2) {

            int result;

            result = ((SearchResult)e1).getDirectory().compareTo(((SearchResult)e2).getDirectory());
            if (result == 0) {
                result = ((SearchResult)e1).getStreamFile().compareTo(((SearchResult)e2).getStreamFile());
            }

            return result;
        }

        private int sortByStreamFile(Viewer viewer, Object e1, Object e2) {

            int result = ((SearchResult)e1).getStreamFile().compareTo(((SearchResult)e2).getStreamFile());
            if (result == 0) {
                result = ((SearchResult)e1).getDirectory().compareTo(((SearchResult)e2).getDirectory());
            }

            return result;
        }

        private int sortByType(Viewer viewer, Object e1, Object e2) {

            int result;

            result = ((SearchResult)e1).getType().compareTo(((SearchResult)e2).getType());
            if (result == 0) {
                result = sortByStreamFile(viewer, e1, e2);
            }

            return result;
        }

        private int sortByLastChangedDate(Viewer viewer, Object e1, Object e2) {

            int result = ((SearchResult)e1).getLastChangedDate().compareTo(((SearchResult)e2).getLastChangedDate());
            if (result == 0) {
                result = sortByStreamFile(viewer, e1, e2);
            }

            return result;
        }

        private int sortByStatementsCount(Viewer viewer, Object e1, Object e2) {

            Integer count1 = ((SearchResult)e1).getStatementsCount();
            Integer count2 = ((SearchResult)e2).getStatementsCount();

            int result = count1.compareTo(count2);
            if (result == 0) {
                result = sortByStreamFile(viewer, e1, e2);
            }

            return result;
        }
    }

    private class LabelProviderStatements extends LabelProvider implements ITableLabelProvider {

        private static final String UNKNOWN = "*UNKNOWN"; //$NON-NLS-1$

        public String getColumnText(Object element, int columnIndex) {
            if (columnIndex == 0) {
                return (String)element;
            }
            return UNKNOWN;
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

    }

    private class ContentProviderStatements implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            return statements;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    private class TableViewerStreamFilesDoubleClickListener implements IDoubleClickListener {

        public void doubleClick(DoubleClickEvent event) {

            if (tableViewerStreamFiles.getSelection() instanceof IStructuredSelection) {

                IStructuredSelection structuredSelection = (IStructuredSelection)tableViewerStreamFiles.getSelection();
                SearchResult _searchResult = (SearchResult)structuredSelection.getFirstElement();
                IStreamFileEditor editor = ISpherePlugin.getStreamFileEditor();

                if (editor != null) {
                    editor.openEditor(connectionName, _searchResult.getDirectory(), _searchResult.getStreamFile(), 0, getEditMode());
                }
            }
        }
    }

    private class TableStreamFilesMenuAdapter extends MenuAdapter {

        private Menu menuTableStreamFiles;
        private MenuItem menuItemOpenEditor;
        private MenuItem menuItemOpenViewer;
        private MenuItem menuItemSelectAll;
        private MenuItem menuItemDeselectAll;
        private MenuItem menuItemInvertSelection;
        private MenuItem menuCopySelected;
        private MenuItem menuItemRemove;
        private MenuItem menuItemSeparator1;
        private MenuItem menuStreamFileSearch;
        private MenuItem menuItemSeparator2;
        private MenuItem menuCreateFilterFromSelectedStreamFiles;
        private MenuItem menuExportSelectedStreamFilesToExcel;

        public TableStreamFilesMenuAdapter(Menu menuTableStreamFiles) {
            this.menuTableStreamFiles = menuTableStreamFiles;
        }

        @Override
        public void menuShown(MenuEvent event) {
            retrieveSelectedTableItems();
            destroyMenuItems();
            createMenuItems();
        }

        public void destroyMenuItems() {
            dispose(menuItemOpenEditor);
            dispose(menuItemOpenViewer);
            dispose(menuItemSelectAll);
            dispose(menuItemDeselectAll);
            dispose(menuItemInvertSelection);
            dispose(menuCopySelected);
            dispose(menuItemRemove);
            dispose(menuItemSeparator1);
            dispose(menuStreamFileSearch);
            dispose(menuItemSeparator2);
            dispose(menuCreateFilterFromSelectedStreamFiles);
            dispose(menuExportSelectedStreamFilesToExcel);
        }

        private void dispose(MenuItem menuItem) {
            if (!((menuItem == null) || (menuItem.isDisposed()))) {
                menuItem.dispose();
            }
        }

        public void createMenuItems() {

            if (hasSelectedItems()) {
                menuItemOpenEditor = new MenuItem(menuTableStreamFiles, SWT.NONE);
                menuItemOpenEditor.setText(Messages.Open_for_edit);
                menuItemOpenEditor.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_OPEN_EDITOR));
                menuItemOpenEditor.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeMenuItemOpenEditor(0);
                    }
                });

                menuItemOpenViewer = new MenuItem(menuTableStreamFiles, SWT.NONE);
                menuItemOpenViewer.setText(Messages.Open_for_browse);
                menuItemOpenViewer.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_OPEN_VIEWER));
                menuItemOpenViewer.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeMenuItemOpenViewer(0);
                    }
                });
            }

            if (hasItems()) {
                menuItemSelectAll = new MenuItem(menuTableStreamFiles, SWT.NONE);
                menuItemSelectAll.setText(Messages.Select_all);
                menuItemSelectAll.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_SELECT_ALL));
                menuItemSelectAll.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeMenuItemSelectAll();
                    }
                });

                menuItemDeselectAll = new MenuItem(menuTableStreamFiles, SWT.NONE);
                menuItemDeselectAll.setText(Messages.Deselect_all);
                menuItemDeselectAll.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_DESELECT_ALL));
                menuItemDeselectAll.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeMenuItemDeselectAll();
                    }
                });
            }

            if (hasSelectedItems()) {
                menuItemInvertSelection = new MenuItem(menuTableStreamFiles, SWT.NONE);
                menuItemInvertSelection.setText(Messages.Invert_selection);
                menuItemInvertSelection.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_INVERT_SELECTION));
                menuItemInvertSelection.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeMenuItemInvertSelectedItems();
                    }
                });

                menuCopySelected = new MenuItem(menuTableStreamFiles, SWT.NONE);
                menuCopySelected.setText(Messages.Copy_selected);
                menuCopySelected.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_COPY_TO_CLIPBOARD));
                menuCopySelected.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeMenuItemCopySelectedItems();
                    }
                });

                menuItemRemove = new MenuItem(menuTableStreamFiles, SWT.NONE);
                menuItemRemove.setText(Messages.Remove);
                menuItemRemove.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_REMOVE));
                menuItemRemove.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeMenuItemRemoveSelectedItems();
                    }
                });

                menuItemSeparator1 = new MenuItem(menuTableStreamFiles, SWT.SEPARATOR);

                menuStreamFileSearch = new MenuItem(menuTableStreamFiles, SWT.NONE);
                menuStreamFileSearch.setText(Messages.iSphere_Stream_File_Search);
                menuStreamFileSearch.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_SOURCE_FILE_SEARCH));
                menuStreamFileSearch.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeMenuItemStreamFileSearch();
                    }
                });

                menuItemSeparator2 = new MenuItem(menuTableStreamFiles, SWT.SEPARATOR);

                menuCreateFilterFromSelectedStreamFiles = new MenuItem(menuTableStreamFiles, SWT.NONE);
                menuCreateFilterFromSelectedStreamFiles.setText(Messages.Export_to_IFS_Filter);
                menuCreateFilterFromSelectedStreamFiles.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_IFS_FILTER));
                menuCreateFilterFromSelectedStreamFiles.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeMenuItemCreateFilterFromSelectedStreamFiles();
                    }
                });

                menuExportSelectedStreamFilesToExcel = new MenuItem(menuTableStreamFiles, SWT.NONE);
                menuExportSelectedStreamFilesToExcel.setText(Messages.Export_to_Excel);
                menuExportSelectedStreamFilesToExcel.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_EXCEL));
                menuExportSelectedStreamFilesToExcel.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeMenuItemExportSelectedStreamFilesToExcel();
                    }
                });

            }

        }
    }

    private class TableViewerStatementsDoubleClickListener implements IDoubleClickListener {

        public void doubleClick(DoubleClickEvent event) {

            if (selectedItemsStreamFiles != null && selectedItemsStreamFiles.length == 1) {

                SearchResult _searchResult = (SearchResult)selectedItemsStreamFiles[0];
                if (tableViewerStatements.getSelection() instanceof IStructuredSelection) {

                    IStructuredSelection structuredSelection = (IStructuredSelection)tableViewerStatements.getSelection();
                    String statement = (String)structuredSelection.getFirstElement();
                    int statementLine = 0;
                    int startAt = statement.indexOf('(');
                    int endAt = statement.indexOf(')');

                    if (startAt != -1 && endAt != -1 && startAt < endAt) {
                        String _statementLine = statement.substring(startAt + 1, endAt);
                        try {
                            statementLine = Integer.parseInt(_statementLine);
                        } catch (NumberFormatException e1) {
                        }
                    }

                    IStreamFileEditor editor = ISpherePlugin.getStreamFileEditor();
                    if (editor != null) {
                        editor.openEditor(connectionName, _searchResult.getDirectory(), _searchResult.getStreamFile(), statementLine, getEditMode());
                    }
                }
            }
        }
    }

    private class TableStatementsMenuAdapter extends MenuAdapter {

        private Menu menuTableStatements;
        private MenuItem menuItemOpenEditor;
        private MenuItem menuItemOpenViewer;

        public TableStatementsMenuAdapter(Menu menuTableStatements) {
            this.menuTableStatements = menuTableStatements;
        }

        @Override
        public void menuShown(MenuEvent event) {
            retrieveSelectedTableItems();
            destroyMenuItems();
            createMenuItems();
        }

        public void destroyMenuItems() {
            if (!((menuItemOpenEditor == null) || (menuItemOpenEditor.isDisposed()))) {
                menuItemOpenEditor.dispose();
            }
            if (!((menuItemOpenViewer == null) || (menuItemOpenViewer.isDisposed()))) {
                menuItemOpenViewer.dispose();
            }
        }

        public void createMenuItems() {

            if (hasSelectedItems()) {
                menuItemOpenEditor = new MenuItem(menuTableStatements, SWT.NONE);
                menuItemOpenEditor.setText(Messages.Open_for_edit);
                menuItemOpenEditor.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_OPEN_EDITOR));
                menuItemOpenEditor.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeMenuItemOpenEditor(getStatementLine());
                    }
                });

                menuItemOpenViewer = new MenuItem(menuTableStatements, SWT.NONE);
                menuItemOpenViewer.setText(Messages.Open_for_browse);
                menuItemOpenViewer.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_OPEN_VIEWER));
                menuItemOpenViewer.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeMenuItemOpenViewer(getStatementLine());
                    }
                });
            }
        }
    }

    public SearchResultViewer(String connectionName, String searchString, SearchResult[] _searchResults, SearchOptions _searchOptions) {
        this.connectionName = connectionName;
        this.searchString = searchString;
        this._searchResults = _searchResults;
        this._searchOptions = _searchOptions;
    }

    /**
     * @wbp.parser.entryPoint
     */
    public void createContents(Composite parent) {

        shell = parent.getShell();

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());

        final SashForm sashFormSearchResult = new SashForm(container, SWT.BORDER);
        sashFormSearchResult.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        tableViewerStreamFiles = new TableViewer(sashFormSearchResult, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        tableViewerStreamFiles.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                retrieveSelectedTableItems();
                setStatements();
            }
        });
        tableViewerStreamFiles.addDoubleClickListener(new TableViewerStreamFilesDoubleClickListener());

        tableViewerStreamFiles.setLabelProvider(new LabelProviderTableViewerStreamFiles());
        tableViewerStreamFiles.setContentProvider(new ContentProviderTableViewerStreamFiles());

        tableStreamFiles = tableViewerStreamFiles.getTable();
        tableStreamFiles.setLinesVisible(true);
        tableStreamFiles.setHeaderVisible(true);
        tableStreamFiles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TableColumn tableColumnDirectory = createTableColumn(tableStreamFiles, "directory", 300, Messages.Directory, 0);
        final TableColumn tableColumnStreamFile = createTableColumn(tableStreamFiles, "streamFile", 150, Messages.Stream_file, 1);
        final TableColumn tableColumnType = createTableColumn(tableStreamFiles, "type", 100, Messages.Type, 2);
        final TableColumn tableColumnLastChangedDate = createTableColumn(tableStreamFiles, "lastChanged", 120, Messages.Last_changed, 3);
        final TableColumn tableColumnStatementsCount = createTableColumn(tableStreamFiles, "statementCount", 80, Messages.StatementsCount, 4);

        final Menu menuTableStreamFiles = new Menu(tableStreamFiles);
        menuTableStreamFiles.addMenuListener(new TableStreamFilesMenuAdapter(menuTableStreamFiles));
        tableStreamFiles.setMenu(menuTableStreamFiles);

        final SorterTableViewerStreamFiles sorterTableViewerStreamFiles = new SorterTableViewerStreamFiles(tableViewerStreamFiles,
            tableColumnDirectory, SWT.UP);
        tableViewerStreamFiles.setSorter(sorterTableViewerStreamFiles);
        sorterTableViewerStreamFiles.setOrder(null);

        Listener sortListener = new Listener() {
            public void handleEvent(Event e) {
                TableColumn column = (TableColumn)e.widget;
                sorterTableViewerStreamFiles.setOrder(column);
                tableViewerStreamFiles.refresh();
            }
        };

        tableColumnDirectory.addListener(SWT.Selection, sortListener);
        tableColumnStreamFile.addListener(SWT.Selection, sortListener);
        tableColumnType.addListener(SWT.Selection, sortListener);
        tableColumnLastChangedDate.addListener(SWT.Selection, sortListener);
        tableColumnStatementsCount.addListener(SWT.Selection, sortListener);

        tableViewerStreamFiles.setInput(new Object());

        tableViewerStatements = new TableViewer(sashFormSearchResult, SWT.FULL_SELECTION | SWT.BORDER);
        tableViewerStatements.addDoubleClickListener(new TableViewerStatementsDoubleClickListener());
        tableViewerStatements.setLabelProvider(new LabelProviderStatements());
        tableViewerStatements.setContentProvider(new ContentProviderStatements());

        tableStatements = tableViewerStatements.getTable();
        tableStatements.setHeaderVisible(true);
        tableStatements.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createTableColumn(tableStatements, "statement", 800, Messages.Statement, 1);

        final Menu menuTableStatement = new Menu(tableStatements);
        menuTableStatement.addMenuListener(new TableStatementsMenuAdapter(menuTableStatement));
        tableStatements.setMenu(menuTableStatement);

        setStatements();
        tableViewerStatements.setInput(new Object());

        sashFormSearchResult.setWeights(new int[] { 1, 1 });

    }

    private TableColumn createTableColumn(Table table, String columnName, int width, String label, int index) {

        TableColumn column = getDialogSettingsManager().createResizableTableColumn(table, SWT.LEFT, columnName, width, index);
        column.setText(label);

        return column;
    }

    private int getStatementLine() {

        int statementLine = 0;

        if (selectedItemsStreamFiles != null && selectedItemsStreamFiles.length == 1) {

            if (tableViewerStatements.getSelection() instanceof IStructuredSelection) {

                IStructuredSelection structuredSelection = (IStructuredSelection)tableViewerStatements.getSelection();

                String statement = (String)structuredSelection.getFirstElement();
                int startAt = statement.indexOf('(');
                int endAt = statement.indexOf(')');
                if (startAt != -1 && endAt != -1 && startAt < endAt) {
                    String _statementLine = statement.substring(startAt + 1, endAt);
                    try {
                        statementLine = Integer.parseInt(_statementLine);
                    } catch (NumberFormatException e1) {
                    }
                }
            }
        }

        return statementLine;
    }

    private String getEditMode() {

        if (isEditMode) {
            return IStreamFileEditor.EDIT;
        } else {
            return IStreamFileEditor.DISPLAY;
        }
    }

    private void retrieveSelectedTableItems() {
        if (tableViewerStreamFiles.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection)tableViewerStreamFiles.getSelection();
            selectedItemsStreamFiles = structuredSelection.toArray();
        } else {
            selectedItemsStreamFiles = new Object[0];
        }
    }

    private void executeMenuItemSelectAll() {

        Object[] objects = new Object[tableStreamFiles.getItemCount()];
        for (int idx = 0; idx < tableStreamFiles.getItemCount(); idx++) {
            objects[idx] = tableViewerStreamFiles.getElementAt(idx);
        }
        tableViewerStreamFiles.setSelection(new StructuredSelection(objects), true);
        selectedItemsStreamFiles = objects;
        tableStreamFiles.setFocus();

        setStatements();

    }

    private void executeMenuItemDeselectAll() {

        tableViewerStreamFiles.setSelection(new StructuredSelection(), true);
        selectedItemsStreamFiles = new Object[0];

        setStatements();

    }

    private void executeMenuItemOpenEditor(int statement) {

        IStreamFileEditor editor = ISpherePlugin.getStreamFileEditor();

        if (editor != null) {

            for (int idx = 0; idx < selectedItemsStreamFiles.length; idx++) {

                SearchResult _searchResult = (SearchResult)selectedItemsStreamFiles[idx];
                editor.openEditor(connectionName, _searchResult.getDirectory(), _searchResult.getStreamFile(), statement, IStreamFileEditor.EDIT);
            }
        }
    }

    private void executeMenuItemOpenViewer(int statement) {

        IStreamFileEditor editor = ISpherePlugin.getStreamFileEditor();

        if (editor != null) {

            for (int idx = 0; idx < selectedItemsStreamFiles.length; idx++) {

                SearchResult _searchResult = (SearchResult)selectedItemsStreamFiles[idx];
                editor.openEditor(connectionName, _searchResult.getDirectory(), _searchResult.getStreamFile(), statement, IStreamFileEditor.DISPLAY);
            }
        }
    }

    private void executeMenuItemInvertSelectedItems() {

        ContentProviderTableViewerStreamFiles contentProvider = (ContentProviderTableViewerStreamFiles)tableViewerStreamFiles.getContentProvider();
        List<Object> allItems = new ArrayList<Object>(Arrays.asList(contentProvider.getElements(null)));
        allItems.removeAll(Arrays.asList(selectedItemsStreamFiles));
        executeMenuItemDeselectAll();
        tableViewerStreamFiles.setSelection(new StructuredSelection(allItems), true);

    }

    private void executeMenuItemCopySelectedItems() {

        if (selectedItemsStreamFiles.length > 0) {
            StringBuilder list = new StringBuilder();
            list.append(Messages.Directory);
            list.append("\t");
            list.append(Messages.Stream_file);
            list.append("\t");
            list.append(Messages.Type);
            list.append("\t");
            list.append(Messages.Last_changed);
            list.append("\t");
            list.append(Messages.StatementsCount);
            list.append("\n");
            for (Object item : selectedItemsStreamFiles) {
                SearchResult searchResult = (SearchResult)item;
                list.append(searchResult.getDirectory());
                list.append("\t");
                list.append(searchResult.getStreamFile());
                list.append("\t");
                list.append(searchResult.getType());
                list.append("\t");
                list.append(getLastChangedDate(searchResult));
                list.append("\t");
                list.append(searchResult.getStatementsCount());
                list.append("\n");
            }
            ClipboardHelper.setText(list.toString());
        }

    }

    private String getLastChangedDate(SearchResult searchResult) {

        String date = Preferences.getInstance().getDateFormatter().format(searchResult.getLastChangedDate());
        String time = Preferences.getInstance().getTimeFormatter().format(searchResult.getLastChangedDate());

        return date + " " + time;
    }

    private void executeMenuItemStreamFileSearch() {

        HashMap<String, SearchElement> _searchElements = new LinkedHashMap<String, SearchElement>();

        for (int i = 0; i < selectedItemsStreamFiles.length; i++) {
            SearchResult searchResult = (SearchResult)selectedItemsStreamFiles[i];
            SearchElement _searchElement = new SearchElement(searchResult);
            _searchElements.put(_searchElement.getKey(), _searchElement);
        }

        SearchDialog dialog = new SearchDialog(getShell(), _searchElements, true);
        if (dialog.open() == Dialog.OK) {

            SearchOptions searchOptions = dialog.getSearchOptions();
            ArrayList<SearchElement> selectedElements = dialog.getSelectedElements();

            SearchPostRun postRun = new SearchPostRun();
            postRun.setConnection(null);
            postRun.setConnectionName(connectionName);
            postRun.setSearchString(searchOptions.getCombinedSearchString());
            postRun.setSearchElements(_searchElements);
            postRun.setWorkbenchWindow(UIHelper.getActivePage().getWorkbenchWindow());

            Connection jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(connectionName);
            SearchExec searchExec = new SearchExec();
            searchExec.execute(connectionName, jdbcConnection, searchOptions, selectedElements, postRun);

        }

    }

    private void executeMenuItemCreateFilterFromSelectedStreamFiles() {

        IStreamFileSearchIFSFilterCreator creator = ISpherePlugin.getStreamFileSearchIFSFilterCreator();

        if (creator != null) {

            SearchResult[] _selectedStreamFiles = new SearchResult[selectedItemsStreamFiles.length];
            for (int i = 0; i < _selectedStreamFiles.length; i++) {
                _selectedStreamFiles[i] = (SearchResult)selectedItemsStreamFiles[i];
            }

            FilterDialog dialog = new FilterDialog(getShell(), RSEFilter.TYPE_MEMBER);
            dialog.setFilterPools(creator.getFilterPools(getConnectionName()));
            if (dialog.open() == Dialog.OK) {
                if (!creator.createIFSFilter(getConnectionName(), dialog.getFilterPool(), dialog.getFilter(), dialog.getFilterUpdateType(),
                    _selectedStreamFiles)) {
                }
            }
        }
    }

    private void executeMenuItemExportSelectedStreamFilesToExcel() {

        SearchResult[] _selectedStreamFiles = new SearchResult[selectedItemsStreamFiles.length];
        for (int i = 0; i < _selectedStreamFiles.length; i++) {
            _selectedStreamFiles[i] = (SearchResult)selectedItemsStreamFiles[i];
        }

        StreamFileToExcelExporter exporter = new StreamFileToExcelExporter(getShell(), getSearchOptions(), _selectedStreamFiles);
        if (_selectedStreamFiles.length != getSearchResults().length) {
            exporter.setPartialExport(true);
        }
        exporter.export();
    }

    private void executeMenuItemRemoveSelectedItems() {

        List<SearchResult> searchResult = new ArrayList<SearchResult>(Arrays.asList(_searchResults));
        searchResult.removeAll(Arrays.asList(selectedItemsStreamFiles));
        _searchResults = searchResult.toArray(new SearchResult[searchResult.size()]);
        tableViewerStreamFiles.remove(selectedItemsStreamFiles);

    }

    private void setStatements() {
        if (selectedItemsStreamFiles == null || selectedItemsStreamFiles.length == 0) {
            statements = new String[1];
            statements[0] = Messages.No_selection;
        } else if (selectedItemsStreamFiles.length == 1) {
            SearchResult _searchResult = (SearchResult)selectedItemsStreamFiles[0];
            SearchResultStatement[] _statements = _searchResult.getStatements();
            statements = new String[_statements.length];
            for (int idx = 0; idx < _statements.length; idx++) {
                statements[idx] = "(" + Integer.toString(_statements[idx].getStatement()) + ") " + _statements[idx].getLine(); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } else {
            statements = new String[1];
            statements[0] = Messages.Multiple_selection;
        }
        tableViewerStatements.refresh();

    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getSearchString() {
        return searchString;
    }

    public SearchResult[] getSearchResults() {
        return _searchResults;
    }

    public SearchOptions getSearchOptions() {
        return _searchOptions;
    }

    public boolean hasItems() {

        if (tableViewerStreamFiles != null && tableViewerStreamFiles.getContentProvider() != null) {
            ContentProviderTableViewerStreamFiles contentProvider = (ContentProviderTableViewerStreamFiles)tableViewerStreamFiles
                .getContentProvider();
            if (contentProvider.getElements(null).length > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSelectedItems() {

        if (selectedItemsStreamFiles != null && selectedItemsStreamFiles.length > 0) {
            return true;
        }
        return false;
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        tableViewerStreamFiles.addSelectionChangedListener(listener);
    }

    public void removeSelectedItems() {
        executeMenuItemRemoveSelectedItems();
    }

    public void invertSelectedItems() {
        executeMenuItemInvertSelectedItems();
    }

    public void setEditEnabled(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public boolean isEditEnabled() {
        return this.isEditMode;
    }

    public void resetColumnWidths() {
        getDialogSettingsManager().resetColumnWidths(tableStreamFiles);
        getDialogSettingsManager().resetColumnWidths(tableStatements);
    }

    private Shell getShell() {
        return shell;
    }

    private DialogSettingsManager getDialogSettingsManager() {

        if (dialogSettingsManager == null) {
            dialogSettingsManager = new DialogSettingsManager(ISpherePlugin.getDefault().getDialogSettings(), getClass());
        }
        return dialogSettingsManager;
    }
}
