/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilesearch;

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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.DialogActionTypes;
import biz.isphere.core.internal.IEditor;
import biz.isphere.core.messagefileeditor.MessageDescription;
import biz.isphere.core.messagefileeditor.MessageDescriptionDetailDialog;
import biz.isphere.core.messagefileeditor.MessageFileEditor;
import biz.isphere.core.messagefileeditor.QMHRTVM;

public class SearchResultViewer {

    private Object connection;
    // private String searchString;
    private SearchResult[] _searchResults;
    private TableViewer tableViewerMessageFiles;
    private Table tableMessageFiles;
    private Object[] selectedItemsMessageFiles;
    private Shell shell;
    private TableViewer tableViewerMessageIds;
    private Table tableMessageIds;
    private SearchResultMessageId[] messageIds;

    private class LabelProviderTableViewerMessageFiles extends LabelProvider implements ITableLabelProvider {

        public String getColumnText(Object element, int columnIndex) {
            if (columnIndex == 0) {
                return ((SearchResult)element).getLibrary() + "/" + ((SearchResult)element).getMessageFile() + " - \""
                    + ((SearchResult)element).getDescription() + "\"";
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

        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            int result = ((SearchResult)e1).getLibrary().compareTo(((SearchResult)e2).getLibrary());
            if (result == 0) {
                result = ((SearchResult)e1).getMessageFile().compareTo(((SearchResult)e2).getMessageFile());
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

    public SearchResultViewer(Object connection, String searchString, SearchResult[] _searchResults) {
        this.connection = connection;
        // this.searchString = searchString;
        this._searchResults = _searchResults;
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
        tableViewerMessageFiles.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                if (tableViewerMessageFiles.getSelection() instanceof IStructuredSelection) {

                    IStructuredSelection structuredSelection = (IStructuredSelection)tableViewerMessageFiles.getSelection();
                    SearchResult _searchResult = (SearchResult)structuredSelection.getFirstElement();

                    IEditor editor = ISpherePlugin.getEditor();

                    if (editor != null) {

                        MessageFileEditor.openEditor(_searchResult.getAS400(), _searchResult.getHost(), _searchResult.getLibrary(),
                            _searchResult.getMessageFile(), IEditor.EDIT);

                    }

                }
            }
        });
        tableViewerMessageFiles.setSorter(new SorterTableViewerMessageFiles());
        tableViewerMessageFiles.setLabelProvider(new LabelProviderTableViewerMessageFiles());
        tableViewerMessageFiles.setContentProvider(new ContentProviderTableViewerMessageFiles());

        tableMessageFiles = tableViewerMessageFiles.getTable();
        tableMessageFiles.setLinesVisible(true);
        tableMessageFiles.setHeaderVisible(true);
        tableMessageFiles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TableColumn tableColumnMessageFile = new TableColumn(tableMessageFiles, SWT.NONE);
        tableColumnMessageFile.setWidth(800);
        tableColumnMessageFile.setText(Messages.Message_file);

        final Menu menuTableMessageFiles = new Menu(tableMessageFiles);
        menuTableMessageFiles.addMenuListener(new MenuAdapter() {

            private MenuItem menuItemOpenEditor;
            private MenuItem menuItemSelectAll;
            private MenuItem menuItemDeselectAll;

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
                if (!((menuItemSelectAll == null) || (menuItemSelectAll.isDisposed()))) {
                    menuItemSelectAll.dispose();
                }
                if (!((menuItemDeselectAll == null) || (menuItemDeselectAll.isDisposed()))) {
                    menuItemDeselectAll.dispose();
                }
            }

            public void createMenuItems() {

                if (!(selectedItemsMessageFiles == null || selectedItemsMessageFiles.length == 0)) {

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
        });
        tableMessageFiles.setMenu(menuTableMessageFiles);

        tableViewerMessageFiles.setInput(new Object());

        tableViewerMessageIds = new TableViewer(sashFormSearchResult, SWT.FULL_SELECTION | SWT.BORDER);
        tableViewerMessageIds.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {

                if (selectedItemsMessageFiles != null && selectedItemsMessageFiles.length == 1) {

                    SearchResult _searchResult = (SearchResult)selectedItemsMessageFiles[0];

                    if (tableViewerMessageIds.getSelection() instanceof IStructuredSelection) {

                        IStructuredSelection structuredSelection = (IStructuredSelection)tableViewerMessageIds.getSelection();

                        SearchResultMessageId _searchResultMessageId = (SearchResultMessageId)structuredSelection.getFirstElement();

                        QMHRTVM qmhrtvm = new QMHRTVM();
                        MessageDescription[] _messageDescription = qmhrtvm.run(_searchResult.getAS400(), _searchResult.getHost(),
                            _searchResult.getLibrary(), _searchResult.getMessageFile(), _searchResultMessageId.getMessageId());
                        if (_messageDescription.length == 1) {

                            MessageDescriptionDetailDialog _messageDescriptionDetailDialog = new MessageDescriptionDetailDialog(shell, _searchResult
                                .getAS400(), DialogActionTypes.CHANGE, _messageDescription[0]);
                            _messageDescriptionDetailDialog.open();

                        }

                    }

                }

            }
        });
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

                MessageFileEditor.openEditor(_searchResult.getAS400(), _searchResult.getHost(), _searchResult.getLibrary(),
                    _searchResult.getMessageFile(), IEditor.EDIT);

            }

        }

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

    public Object getConnection() {
        return connection;
    }

    public SearchResult[] getSearchResults() {
        return _searchResults;
    }

}
