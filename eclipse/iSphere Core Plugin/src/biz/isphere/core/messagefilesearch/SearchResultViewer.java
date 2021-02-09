/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilesearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.DialogActionTypes;
import biz.isphere.core.internal.FilterDialog;
import biz.isphere.core.internal.IEditor;
import biz.isphere.core.internal.IMessageFileSearchObjectFilterCreator;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.internal.api.retrievemessagedescription.IQMHRTVM;
import biz.isphere.core.messagefileeditor.MessageDescription;
import biz.isphere.core.messagefileeditor.MessageDescriptionDetailDialog;
import biz.isphere.core.messagefileeditor.MessageFileEditor;
import biz.isphere.core.resourcemanagement.filter.RSEFilter;
import biz.isphere.core.search.SearchOptions;

public class SearchResultViewer {

    private String connectionName;
    private String searchString;
    private SearchResult[] _searchResults;
    private SearchOptions _searchOptions;
    private TableViewer tableViewerMessageFiles;
    private Table tableMessageFiles;
    private Object[] selectedItemsMessageFiles;
    private Shell shell;
    private TableViewer tableViewerMessageIds;
    private Table tableMessageIds;
    private SearchResultMessageId[] messageIds;

    private DialogSettingsManager dialogSettingsManager;

    private class LabelProviderTableViewerMessageFiles extends LabelProvider implements ITableLabelProvider {

        public String getColumnText(Object element, int columnIndex) {
            SearchResult searchResult = (SearchResult)element;
            if (columnIndex == 0) {
                return searchResult.getLibrary();
                // return ((SearchResult)element).getLibrary() + "/" +
                // ((SearchResult)element).getMessageFile() + " - \""
                // + ((SearchResult)element).getDescription() + "\"";
            } else if (columnIndex == 1) {
                return searchResult.getMessageFile();
            } else if (columnIndex == 2) {
                return searchResult.getDescription();
            }
            return "*UNKNOWN";
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

    }

    private class ContentProviderTableViewerMessageFiles implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            return _searchResults;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    private class SorterTableViewerMessageFiles extends ViewerSorter {

        private TableColumn initialColumn;
        private int initialDirection;
        private TableViewer tableViewer;

        public SorterTableViewerMessageFiles(TableViewer tableViewer, TableColumn column, int direction) {

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

            if (Messages.Library.equals(column.getText())) {
                result = sortByLibrary(viewer, e1, e2);
            } else if (Messages.File.equals(column.getText())) {
                result = sortByFile(viewer, e1, e2);
            } else if (Messages.Description.equals(column.getText())) {
                result = sortByDescription(viewer, e1, e2);
            } else {
                result = sortByLibrary(viewer, e1, e2);
            }

            if (tableViewer.getTable().getSortDirection() == SWT.DOWN) {
                result = result * -1;
            }

            return result;
        }

        private int sortByLibrary(Viewer viewer, Object e1, Object e2) {

            int result;

            result = ((SearchResult)e1).getLibrary().compareTo(((SearchResult)e2).getLibrary());
            if (result == 0) {
                result = ((SearchResult)e1).getMessageFile().compareTo(((SearchResult)e2).getMessageFile());
            }

            return result;
        }

        private int sortByFile(Viewer viewer, Object e1, Object e2) {

            int result;

            result = ((SearchResult)e1).getMessageFile().compareTo(((SearchResult)e2).getMessageFile());
            if (result == 0) {
                result = ((SearchResult)e1).getLibrary().compareTo(((SearchResult)e2).getLibrary());
            }

            return result;
        }

        private int sortByDescription(Viewer viewer, Object e1, Object e2) {

            int result = ((SearchResult)e1).getDescription().compareTo(((SearchResult)e2).getDescription());
            if (result == 0) {
                result = sortByLibrary(viewer, e1, e2);
            }

            return result;
        }

    }

    private class LabelProviderMessageIds extends LabelProvider implements ITableLabelProvider {

        public String getColumnText(Object element, int columnIndex) {
            if (columnIndex == 0) {
                return ((SearchResultMessageId)element).getMessageId();
            } else if (columnIndex == 1) {
                return ((SearchResultMessageId)element).getMessage();
            }
            return "*UNKNOWN";
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

    }

    private class ContentProviderMessageIds implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            return messageIds;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    private class TableViewerMessageFilesDoubleClickListener implements IDoubleClickListener {
        public void doubleClick(DoubleClickEvent event) {

            if (tableViewerMessageFiles.getSelection() instanceof IStructuredSelection) {

                IStructuredSelection structuredSelection = (IStructuredSelection)tableViewerMessageFiles.getSelection();
                SearchResult _searchResult = (SearchResult)structuredSelection.getFirstElement();

                IEditor editor = ISpherePlugin.getEditor();
                if (editor != null) {
                    String connectionName = _searchResult.getConnectionName();
                    String bindingDirectory = _searchResult.getMessageFile();
                    String library = _searchResult.getLibrary();
                    String objectType = ISeries.MSGF;
                    String description = _searchResult.getDescription();

                    RemoteObject remoteObject = new RemoteObject(connectionName, bindingDirectory, library, objectType, description);
                    MessageFileEditor.openEditor(_searchResult.getConnectionName(), remoteObject, IEditor.EDIT);
                }
            }
        }
    }

    private class TableMessageFilesMenuAdapter extends MenuAdapter {

        private Menu menuTableMessageFiles;
        private MenuItem menuItemOpenEditor;
        private MenuItem menuItemSelectAll;
        private MenuItem menuItemDeselectAll;
        private MenuItem menuItemInvertSelection;
        private MenuItem menuCopySelected;
        private MenuItem menuItemRemove;
        private MenuItem menuItemSeparator2;
        private MenuItem menuCreateFilterFromSelectedMembers;
        private MenuItem menuExportSelectedMembersToExcel;

        public TableMessageFilesMenuAdapter(Menu menuTableMessageFiles) {
            this.menuTableMessageFiles = menuTableMessageFiles;
        }

        @Override
        public void menuShown(MenuEvent event) {
            retrieveSelectedTableItems();
            destroyMenuItems();
            createMenuItems();
        }

        public void destroyMenuItems() {
            dispose(menuItemOpenEditor);
            dispose(menuItemSelectAll);
            dispose(menuItemDeselectAll);
            dispose(menuItemInvertSelection);
            dispose(menuCopySelected);
            dispose(menuItemRemove);
            dispose(menuItemSeparator2);
            dispose(menuCreateFilterFromSelectedMembers);
            dispose(menuExportSelectedMembersToExcel);
        }

        private void dispose(MenuItem menuItem) {
            if (!((menuItem == null) || (menuItem.isDisposed()))) {
                menuItem.dispose();
            }
        }

        public void createMenuItems() {

            if (hasSelectedItems()) {
                menuItemOpenEditor = new MenuItem(menuTableMessageFiles, SWT.NONE);
                menuItemOpenEditor.setText(Messages.Open_editor);
                menuItemOpenEditor.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_OPEN_EDITOR));
                menuItemOpenEditor.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeMenuItemOpenEditor();
                    }
                });
            }

            if (hasItems()) {
                menuItemSelectAll = new MenuItem(menuTableMessageFiles, SWT.NONE);
                menuItemSelectAll.setText(Messages.Select_all);
                menuItemSelectAll.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_SELECT_ALL));
                menuItemSelectAll.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeMenuItemSelectAll();
                    }
                });

                menuItemDeselectAll = new MenuItem(menuTableMessageFiles, SWT.NONE);
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
                menuItemInvertSelection = new MenuItem(menuTableMessageFiles, SWT.NONE);
                menuItemInvertSelection.setText(Messages.Invert_selection);
                menuItemInvertSelection.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_INVERT_SELECTION));
                menuItemInvertSelection.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeMenuItemInvertSelectedItems();
                    }
                });

                menuCopySelected = new MenuItem(menuTableMessageFiles, SWT.NONE);
                menuCopySelected.setText(Messages.Copy_selected);
                menuCopySelected.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_COPY_TO_CLIPBOARD));
                menuCopySelected.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeMenuItemCopySelectedItems();
                    }
                });

                menuItemRemove = new MenuItem(menuTableMessageFiles, SWT.NONE);
                menuItemRemove.setText(Messages.Remove);
                menuItemRemove.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_REMOVE));
                menuItemRemove.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeMenuItemRemoveSelectedItems();
                    }
                });

                menuItemSeparator2 = new MenuItem(menuTableMessageFiles, SWT.SEPARATOR);

                menuCreateFilterFromSelectedMembers = new MenuItem(menuTableMessageFiles, SWT.NONE);
                menuCreateFilterFromSelectedMembers.setText(Messages.Export_to_Object_Filter);
                menuCreateFilterFromSelectedMembers.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_MEMBER_FILTER));
                menuCreateFilterFromSelectedMembers.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeMenuItemCreateFilterFromSelectedMembers();
                    }
                });

                menuExportSelectedMembersToExcel = new MenuItem(menuTableMessageFiles, SWT.NONE);
                menuExportSelectedMembersToExcel.setText(Messages.Export_to_Excel);
                menuExportSelectedMembersToExcel.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_EXCEL));
                menuExportSelectedMembersToExcel.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeMenuItemExportSelectedMembersToExcel();
                    }
                });
            }

        }
    }

    private class TableViewerMessageIDsDoubleClickListener implements IDoubleClickListener {
        public void doubleClick(DoubleClickEvent event) {

            if (selectedItemsMessageFiles != null && selectedItemsMessageFiles.length == 1) {

                SearchResult _searchResult = (SearchResult)selectedItemsMessageFiles[0];
                if (tableViewerMessageIds.getSelection() instanceof IStructuredSelection) {

                    IStructuredSelection structuredSelection = (IStructuredSelection)tableViewerMessageIds.getSelection();
                    SearchResultMessageId _searchResultMessageId = (SearchResultMessageId)structuredSelection.getFirstElement();

                    IQMHRTVM qmhrtvm = new IQMHRTVM(_searchResult.getAS400(), _searchResult.getConnectionName());
                    qmhrtvm.setMessageFile(_searchResult.getMessageFile(), _searchResult.getLibrary());
                    MessageDescription _messageDescription = qmhrtvm.retrieveMessageDescription(_searchResultMessageId.getMessageId());

                    if (_messageDescription != null) {
                        MessageDescriptionDetailDialog _messageDescriptionDetailDialog = new MessageDescriptionDetailDialog(shell,
                            DialogActionTypes.CHANGE, _messageDescription);
                        _messageDescriptionDetailDialog.open();
                    } else {
                        MessageDialog.openError(
                            shell,
                            Messages.E_R_R_O_R,
                            Messages.bind(Messages.Message_identifier_A_not_found_in_message_file_B_in_C,
                                new String[] { _searchResultMessageId.getMessageId(), _searchResult.getMessageFile(), _searchResult.getLibrary() }));
                    }
                }
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

        tableViewerMessageFiles = new TableViewer(sashFormSearchResult, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        tableViewerMessageFiles.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                retrieveSelectedTableItems();
                setMessageIds();
            }
        });
        tableViewerMessageFiles.addDoubleClickListener(new TableViewerMessageFilesDoubleClickListener());

        tableViewerMessageFiles.setLabelProvider(new LabelProviderTableViewerMessageFiles());
        tableViewerMessageFiles.setContentProvider(new ContentProviderTableViewerMessageFiles());

        tableMessageFiles = tableViewerMessageFiles.getTable();
        tableMessageFiles.setLinesVisible(true);
        tableMessageFiles.setHeaderVisible(true);
        tableMessageFiles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // final TableColumn tableColumnMessageFile = new
        // TableColumn(tableMessageFiles, SWT.NONE);
        // tableColumnMessageFile.setWidth(800);
        // tableColumnMessageFile.setText(Messages.Message_file);

        final TableColumn tableColumnLibrary = createTableColumn(tableMessageFiles, "library", 100, Messages.Library, 0);
        final TableColumn tableColumnFile = createTableColumn(tableMessageFiles, "file", 100, Messages.Message_file, 0);
        final TableColumn tableColumnDescription = createTableColumn(tableMessageFiles, "description", 300, Messages.Description, 4);

        final Menu menuTableMessageFiles = new Menu(tableMessageFiles);
        menuTableMessageFiles.addMenuListener(new TableMessageFilesMenuAdapter(menuTableMessageFiles));
        tableMessageFiles.setMenu(menuTableMessageFiles);

        final SorterTableViewerMessageFiles sorterTableViewerMessageFiles = new SorterTableViewerMessageFiles(tableViewerMessageFiles,
            tableColumnLibrary, SWT.UP);
        tableViewerMessageFiles.setSorter(sorterTableViewerMessageFiles);
        sorterTableViewerMessageFiles.setOrder(null);

        Listener sortListener = new Listener() {
            public void handleEvent(Event e) {
                TableColumn column = (TableColumn)e.widget;
                sorterTableViewerMessageFiles.setOrder(column);
                tableViewerMessageFiles.refresh();
            }
        };

        tableColumnLibrary.addListener(SWT.Selection, sortListener);
        tableColumnFile.addListener(SWT.Selection, sortListener);
        tableColumnDescription.addListener(SWT.Selection, sortListener);

        tableViewerMessageFiles.setInput(new Object());

        tableViewerMessageIds = new TableViewer(sashFormSearchResult, SWT.FULL_SELECTION | SWT.BORDER);
        tableViewerMessageIds.addDoubleClickListener(new TableViewerMessageIDsDoubleClickListener());
        tableViewerMessageIds.setLabelProvider(new LabelProviderMessageIds());
        tableViewerMessageIds.setContentProvider(new ContentProviderMessageIds());

        tableMessageIds = tableViewerMessageIds.getTable();
        tableMessageIds.setHeaderVisible(true);
        tableMessageIds.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TableColumn tableColumnMessageId = new TableColumn(tableMessageIds, SWT.NONE);
        tableColumnMessageId.setWidth(125);
        tableColumnMessageId.setText(Messages.Message_Id);

        final TableColumn tableColumnMessage = new TableColumn(tableMessageIds, SWT.NONE);
        tableColumnMessage.setWidth(700);
        tableColumnMessage.setText(Messages.Message);

        setMessageIds();
        tableViewerMessageIds.setInput(new Object());

        sashFormSearchResult.setWeights(new int[] { 1, 1 });

    }

    private TableColumn createTableColumn(Table table, String columnName, int width, String label, int index) {

        TableColumn column = getDialogSettingsManager().createResizableTableColumn(table, SWT.LEFT, columnName, width, index);
        column.setText(label);

        return column;
    }

    private void retrieveSelectedTableItems() {
        if (tableViewerMessageFiles.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection)tableViewerMessageFiles.getSelection();
            selectedItemsMessageFiles = structuredSelection.toArray();
        } else {
            selectedItemsMessageFiles = new Object[0];
        }
    }

    private void executeMenuItemSelectAll() {

        Object[] objects = new Object[tableMessageFiles.getItemCount()];
        for (int idx = 0; idx < tableMessageFiles.getItemCount(); idx++) {
            objects[idx] = tableViewerMessageFiles.getElementAt(idx);
        }
        tableViewerMessageFiles.setSelection(new StructuredSelection(objects), true);
        selectedItemsMessageFiles = objects;
        tableMessageFiles.setFocus();

        setMessageIds();

    }

    private void executeMenuItemDeselectAll() {

        tableViewerMessageFiles.setSelection(new StructuredSelection(), true);
        selectedItemsMessageFiles = new Object[0];

        setMessageIds();

    }

    private void executeMenuItemOpenEditor() {

        IEditor editor = ISpherePlugin.getEditor();

        if (editor != null) {

            for (int idx = 0; idx < selectedItemsMessageFiles.length; idx++) {

                SearchResult _searchResult = (SearchResult)selectedItemsMessageFiles[idx];

                String connectionName = _searchResult.getConnectionName();
                String bindingDirectory = _searchResult.getMessageFile();
                String library = _searchResult.getLibrary();
                String objectType = ISeries.MSGF;
                String description = _searchResult.getDescription();

                RemoteObject remoteObject = new RemoteObject(connectionName, bindingDirectory, library, objectType, description);
                MessageFileEditor.openEditor(connectionName, remoteObject, IEditor.EDIT);
            }
        }
    }

    private void executeMenuItemInvertSelectedItems() {

        IEditor editor = ISpherePlugin.getEditor();

        if (editor != null) {
            ContentProviderTableViewerMessageFiles contentProvider = (ContentProviderTableViewerMessageFiles)tableViewerMessageFiles
                .getContentProvider();
            List<Object> allItems = new ArrayList<Object>(Arrays.asList(contentProvider.getElements(null)));
            allItems.removeAll(Arrays.asList(selectedItemsMessageFiles));
            executeMenuItemDeselectAll();
            tableViewerMessageFiles.setSelection(new StructuredSelection(allItems), true);
        }
    }

    private void executeMenuItemCopySelectedItems() {

        IEditor editor = ISpherePlugin.getEditor();

        if (editor != null) {
            if (selectedItemsMessageFiles.length > 0) {
                StringBuilder list = new StringBuilder();
                list.append(Messages.Library);
                list.append("\t");
                list.append(Messages.Message_file);
                list.append("\t");
                list.append(Messages.Description);
                list.append("\t");
                list.append(Messages.Message_Id);
                list.append("\t");
                list.append(Messages.Message);
                list.append("\n");
                for (Object item : selectedItemsMessageFiles) {
                    SearchResult searchResult = (SearchResult)item;
                    for (SearchResultMessageId msgId : searchResult.getMessageIds()) {
                        list.append(searchResult.getLibrary());
                        list.append("\t");
                        list.append(searchResult.getMessageFile());
                        list.append("\t");
                        list.append(searchResult.getDescription());
                        list.append("\t");
                        list.append(msgId.getMessageId());
                        list.append("\t");
                        list.append(msgId.getMessage());
                        list.append("\n");
                    }
                }
                ClipboardHelper.setText(list.toString());
            }
        }
    }

    private void executeMenuItemRemoveSelectedItems() {

        IEditor editor = ISpherePlugin.getEditor();

        if (editor != null) {
            List<SearchResult> searchResult = new ArrayList<SearchResult>(Arrays.asList(_searchResults));
            searchResult.removeAll(Arrays.asList(selectedItemsMessageFiles));
            _searchResults = searchResult.toArray(new SearchResult[searchResult.size()]);
            tableViewerMessageFiles.remove(selectedItemsMessageFiles);
        }
    }

    private void executeMenuItemCreateFilterFromSelectedMembers() {

        IMessageFileSearchObjectFilterCreator creator = ISpherePlugin.getMessageFileSearchObjectFilterCreator();

        if (creator != null) {

            SearchResult[] _selectedMessageFiles = new SearchResult[selectedItemsMessageFiles.length];
            for (int i = 0; i < _selectedMessageFiles.length; i++) {
                _selectedMessageFiles[i] = (SearchResult)selectedItemsMessageFiles[i];
            }

            FilterDialog dialog = new FilterDialog(shell, RSEFilter.TYPE_MEMBER);
            dialog.setFilterPools(creator.getFilterPools(getConnectionName()));
            if (dialog.open() == Dialog.OK) {
                if (!creator.createObjectFilter(getConnectionName(), dialog.getFilterPool(), dialog.getFilter(), dialog.getFilterUpdateType(),
                    _selectedMessageFiles)) {
                }
            }
        }
    }

    private void executeMenuItemExportSelectedMembersToExcel() {

        SearchResult[] _selectedMessageFiles = new SearchResult[selectedItemsMessageFiles.length];
        for (int i = 0; i < _selectedMessageFiles.length; i++) {
            _selectedMessageFiles[i] = (SearchResult)selectedItemsMessageFiles[i];
        }

        MessageFilesToExcelExporter exporter = new MessageFilesToExcelExporter(shell, getSearchOptions(), _selectedMessageFiles);
        if (_selectedMessageFiles.length != getSearchResults().length) {
            exporter.setPartialExport(true);
        }
        exporter.export();
    }

    private void setMessageIds() {
        if (selectedItemsMessageFiles == null || selectedItemsMessageFiles.length == 0) {

            SearchResultMessageId messageId = new SearchResultMessageId();
            messageId.setMessageId("./.");
            messageId.setMessage(Messages.No_selection);

            messageIds = new SearchResultMessageId[1];
            messageIds[0] = messageId;

        } else if (selectedItemsMessageFiles.length == 1) {
            SearchResult _searchResult = (SearchResult)selectedItemsMessageFiles[0];
            messageIds = _searchResult.getMessageIds();
        } else {

            SearchResultMessageId messageId = new SearchResultMessageId();
            messageId.setMessageId("./.");
            messageId.setMessage(Messages.Multiple_selection);

            messageIds = new SearchResultMessageId[1];
            messageIds[0] = messageId;

        }
        tableViewerMessageIds.refresh();

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

        if (tableViewerMessageFiles != null && tableViewerMessageFiles.getContentProvider() != null) {
            ContentProviderTableViewerMessageFiles contentProvider = (ContentProviderTableViewerMessageFiles)tableViewerMessageFiles
                .getContentProvider();
            if (contentProvider.getElements(null).length > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSelectedItems() {

        if (selectedItemsMessageFiles != null && selectedItemsMessageFiles.length > 0) {
            return true;
        }
        return false;
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        tableViewerMessageFiles.addSelectionChangedListener(listener);
    }

    public void removeSelectedItems() {
        executeMenuItemRemoveSelectedItems();
    }

    public void invertSelectedItems() {
        executeMenuItemInvertSelectedItems();
    }

    private DialogSettingsManager getDialogSettingsManager() {

        if (dialogSettingsManager == null) {
            dialogSettingsManager = new DialogSettingsManager(ISpherePlugin.getDefault().getDialogSettings(), getClass());
        }
        return dialogSettingsManager;
    }
}
