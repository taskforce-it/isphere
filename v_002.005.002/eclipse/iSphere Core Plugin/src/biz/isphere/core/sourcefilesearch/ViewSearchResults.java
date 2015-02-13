/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcefilesearch;

import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.FilterDialog;
import biz.isphere.core.internal.ISourceFileSearchMemberFilterCreator;
import biz.isphere.core.swt.widgets.extension.handler.WidgetFactoryContributionsHandler;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;

public class ViewSearchResults extends ViewPart implements ISelectionChangedListener {

    private static final String TAB_DATA_VIEWER = "Viewer";

    private Action actionExportToMemberFilter;
    private Action actionExportToExcel;
    private Action actionRemoveTabItem;
    private Action actionRemoveSelectedItems;
    private Action actionInvertSelectedItems;
    private TabFolder tabFolderSearchResults;
    private Shell shell;

    @Override
    public void createPartControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout());

        shell = parent.getShell();

        tabFolderSearchResults = new TabFolder(container, SWT.NONE);

        createActions();
        initializeToolBar();
        initializeMenu();

    }

    private void createActions() {

        actionExportToMemberFilter = new Action("") {
            @Override
            public void run() {
                exportToMemberFilter();
            }
        };
        actionExportToMemberFilter.setToolTipText(Messages.Export_to_Member_Filter);
        actionExportToMemberFilter.setImageDescriptor(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_MEMBER_FILTER));
        actionExportToMemberFilter.setEnabled(false);

        actionExportToExcel = new Action("") {
            @Override
            public void run() {
                exportToExcel();
            }
        };
        actionExportToExcel.setToolTipText(Messages.Export_to_Excel);
        actionExportToExcel.setImageDescriptor(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_EXCEL));
        actionExportToExcel.setEnabled(false);

        actionRemoveTabItem = new Action("") {
            @Override
            public void run() {
                removeTabItem();
            }
        };
        actionRemoveTabItem.setToolTipText(Messages.Remove_tab_item);
        actionRemoveTabItem.setImageDescriptor(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_MINUS));
        actionRemoveTabItem.setEnabled(false);

        actionRemoveSelectedItems = new Action("") {
            @Override
            public void run() {
                removeSelectedItem();
            }
        };
        actionRemoveSelectedItems.setToolTipText(Messages.Tooltip_Remove);
        actionRemoveSelectedItems.setImageDescriptor(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_REMOVE));
        actionRemoveSelectedItems.setEnabled(false);

        actionInvertSelectedItems = new Action("") {
            @Override
            public void run() {
                invertSelection();
            }
        };
        actionInvertSelectedItems.setToolTipText(Messages.Tooltip_Invert_selection);
        actionInvertSelectedItems.setImageDescriptor(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_INVERT_SELECTION));
        actionInvertSelectedItems.setEnabled(false);

    }

    private void initializeToolBar() {
        IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
        toolbarManager.add(actionRemoveSelectedItems);
        toolbarManager.add(actionInvertSelectedItems);
        toolbarManager.add(new Separator());
        toolbarManager.add(actionExportToMemberFilter);
        toolbarManager.add(actionExportToExcel);
        toolbarManager.add(new Separator());
        toolbarManager.add(actionRemoveTabItem);
    }

    private void initializeMenu() {
        // IMenuManager menuManager =
        // getViewSite().getActionBars().getMenuManager();
    }

    @Override
    public void setFocus() {
    }

    public void addTabItem(Object connection, String connectionName, String searchString, SearchResult[] searchResults) {
        Composite compositeSearchResult = new Composite(tabFolderSearchResults, SWT.NONE);
        compositeSearchResult.setLayout(new FillLayout());

        TabItem tabItemSearchResult = new TabItem(tabFolderSearchResults, SWT.NONE);
        tabItemSearchResult.setText(connectionName + "/" + searchString);

        SearchResultViewer _searchResultViewer = new SearchResultViewer(connection, searchString, searchResults);
        _searchResultViewer.createContents(compositeSearchResult);
        _searchResultViewer.addSelectionChangedListener(this);

        tabItemSearchResult.setControl(compositeSearchResult);
        tabItemSearchResult.setData(TAB_DATA_VIEWER, _searchResultViewer);

        TabItem[] tabItemToBeSelected = new TabItem[1];
        tabItemToBeSelected[0] = tabItemSearchResult;
        tabFolderSearchResults.setSelection(tabItemToBeSelected);

        setActionEnablement();

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
    }

    public void exportToMemberFilter() {

        ISourceFileSearchMemberFilterCreator creator = ISpherePlugin.getSourceFileSearchMemberFilterCreator();

        if (creator != null) {

            int selectedTabItem = tabFolderSearchResults.getSelectionIndex();

            if (selectedTabItem >= 0) {

                SearchResultViewer _searchResultViewer = (SearchResultViewer)tabFolderSearchResults.getItem(selectedTabItem).getData(TAB_DATA_VIEWER);

                if (_searchResultViewer != null) {

                    FilterDialog dialog = new FilterDialog(shell);
                    if (dialog.open() == Dialog.OK) {
                        if (!creator.createMemberFilter(_searchResultViewer.getConnection(), dialog.getFilter(),
                            _searchResultViewer.getSearchResults())) {

                            MessageBox errorBox = new MessageBox(shell, SWT.ICON_ERROR);
                            errorBox.setText(Messages.E_R_R_O_R);
                            errorBox.setMessage(Messages.The_filter_could_not_be_created);
                            errorBox.open();

                        }
                    }

                }

            }

        }

    }

    public void exportToExcel() {

        int selectedTabItem = tabFolderSearchResults.getSelectionIndex();

        if (selectedTabItem >= 0) {

            SearchResultViewer _searchResultViewer = (SearchResultViewer)tabFolderSearchResults.getItem(selectedTabItem).getData(TAB_DATA_VIEWER);

            if (_searchResultViewer != null) {

                SearchResult[] _searchResults = _searchResultViewer.getSearchResults();

                WidgetFactoryContributionsHandler factory = new WidgetFactoryContributionsHandler();
                IFileDialog dialog = factory.getFileDialog(shell, SWT.SAVE);

                dialog.setFilterNames(new String[] { "Excel Files", "All Files" });
                dialog.setFilterExtensions(new String[] { "*.xls", "*.*" });
                dialog.setFilterPath("C:\\");
                dialog.setFileName("export.xls");
                dialog.setOverwrite(true);
                String file = dialog.open();

                if (file != null) {

                    try {

                        WritableWorkbook workbook = Workbook.createWorkbook(new File(file));

                        WritableSheet sheet;

                        sheet = workbook.createSheet(Messages.Members_with_statements, 0);

                        sheet.addCell(new jxl.write.Label(0, 0, Messages.Library));
                        sheet.addCell(new jxl.write.Label(1, 0, Messages.Source_file));
                        sheet.addCell(new jxl.write.Label(2, 0, Messages.Member));
                        sheet.addCell(new jxl.write.Label(3, 0, Messages.Description));
                        sheet.addCell(new jxl.write.Label(4, 0, Messages.Line));
                        sheet.addCell(new jxl.write.Label(5, 0, Messages.Statement));

                        int line = 1;

                        for (int index1 = 0; index1 < _searchResults.length; index1++) {

                            SearchResultStatement[] _statements = _searchResults[index1].getStatements();

                            for (int index2 = 0; index2 < _statements.length; index2++) {

                                sheet.addCell(new jxl.write.Label(0, line, _searchResults[index1].getLibrary()));
                                sheet.addCell(new jxl.write.Label(1, line, _searchResults[index1].getFile()));
                                sheet.addCell(new jxl.write.Label(2, line, _searchResults[index1].getMember()));
                                sheet.addCell(new jxl.write.Label(3, line, _searchResults[index1].getDescription()));
                                sheet.addCell(new jxl.write.Label(4, line, Integer.toString(_statements[index2].getStatement())));
                                sheet.addCell(new jxl.write.Label(5, line, _statements[index2].getLine()));

                                line++;

                            }

                            line++;
                        }

                        sheet = workbook.createSheet(Messages.Members, 0);

                        sheet.addCell(new jxl.write.Label(0, 0, Messages.Library));
                        sheet.addCell(new jxl.write.Label(1, 0, Messages.Source_file));
                        sheet.addCell(new jxl.write.Label(2, 0, Messages.Member));
                        sheet.addCell(new jxl.write.Label(3, 0, Messages.Description));

                        for (int index = 0; index < _searchResults.length; index++) {
                            sheet.addCell(new jxl.write.Label(0, index + 1, _searchResults[index].getLibrary()));
                            sheet.addCell(new jxl.write.Label(1, index + 1, _searchResults[index].getFile()));
                            sheet.addCell(new jxl.write.Label(2, index + 1, _searchResults[index].getMember()));
                            sheet.addCell(new jxl.write.Label(3, index + 1, _searchResults[index].getDescription()));
                        }

                        workbook.write();
                        workbook.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (WriteException e) {
                        e.printStackTrace();
                    }

                }

            }

        }

    }

    public void removeTabItem() {
        int selectedTabItem = tabFolderSearchResults.getSelectionIndex();
        if (selectedTabItem >= 0) {
            tabFolderSearchResults.getItem(selectedTabItem).dispose();
            if (tabFolderSearchResults.getItemCount() == 0) {
                actionExportToMemberFilter.setEnabled(false);
                actionExportToExcel.setEnabled(false);
                actionRemoveTabItem.setEnabled(false);
                actionRemoveSelectedItems.setEnabled(false);
            }
        }
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

    private void setActionEnablement() {
        selectionChanged(null);
    }

    public void selectionChanged(SelectionChangedEvent event) {

        SearchResultViewer viewer;
        int index = tabFolderSearchResults.getSelectionIndex();
        if (index < 0) {
            viewer = null;
        } else {
            viewer = (SearchResultViewer)tabFolderSearchResults.getItem(index).getData(
                TAB_DATA_VIEWER);
        }

        boolean hasViewer;
        boolean hasItems;
        boolean hasSelectedItems;
        if (viewer == null) {
            hasViewer = false;
            hasItems = false;
            hasSelectedItems = false;
        } else {
            hasViewer = true;
            hasItems = viewer.hasItems();
            hasSelectedItems = viewer.hasSelectedItems();
        }

        actionRemoveSelectedItems.setEnabled(hasSelectedItems);
        actionInvertSelectedItems.setEnabled(hasSelectedItems);
        actionExportToMemberFilter.setEnabled(hasItems);
        actionExportToExcel.setEnabled(hasItems);
        actionRemoveTabItem.setEnabled(hasViewer);
    }
}
