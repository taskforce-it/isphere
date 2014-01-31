/*******************************************************************************
 * Copyright (c) 2012-2013 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package de.taskforce.isphere.sourcefilesearch;

import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import de.taskforce.isphere.ISpherePlugin;
import de.taskforce.isphere.Messages;

public class ViewSearchResults extends ViewPart {

	private Action actionExportToExcel;
	private Action actionRemoveTabItem;
	private TabFolder tabFolderSearchResults;
	private Shell shell;
	
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

		actionExportToExcel = new Action("") {
			public void run() {
				exportToExcel();
			}
		};
		actionExportToExcel.setToolTipText(Messages.getString("Export_to_Excel"));
		actionExportToExcel.setImageDescriptor(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_EXCEL));
		actionExportToExcel.setEnabled(false);

		actionRemoveTabItem = new Action("") {
			public void run() {
				removeTabItem();
			}
		};
		actionRemoveTabItem.setToolTipText(Messages.getString("Remove_tab_item"));
		actionRemoveTabItem.setImageDescriptor(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_MINUS));
		actionRemoveTabItem.setEnabled(false);

	}

	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
		toolbarManager.add(actionExportToExcel);
		toolbarManager.add(actionRemoveTabItem);
	}

	private void initializeMenu() {
		// IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
	}

	public void setFocus() {
	}
	
	public void addTabItem(Object connection, String connectionName, String searchString, SearchResult[] searchResults) {
		Composite compositeSearchResult = new Composite(tabFolderSearchResults, SWT.NONE);
		compositeSearchResult.setLayout(new FillLayout());

		TabItem tabItemSearchResult = new TabItem(tabFolderSearchResults, SWT.NONE);
		tabItemSearchResult.setText(connectionName + "/" + searchString);
		
		 SearchResultViewer _searchResultViewer = new SearchResultViewer(connection, searchString, searchResults);
		 _searchResultViewer.createContents(compositeSearchResult);
		
		tabItemSearchResult.setControl(compositeSearchResult);
		tabItemSearchResult.setData("Viewer", _searchResultViewer);
		
		TabItem[] tabItemToBeSelected = new TabItem[1];
		tabItemToBeSelected[0] = tabItemSearchResult;
		tabFolderSearchResults.setSelection(tabItemToBeSelected);
		
		actionExportToExcel.setEnabled(true);		
		actionRemoveTabItem.setEnabled(true);		
	}

	public void exportToExcel() {

		int selectedTabItem = tabFolderSearchResults.getSelectionIndex();
		
		if (selectedTabItem >= 0) {
			
			SearchResultViewer _searchResultViewer = (SearchResultViewer)tabFolderSearchResults.getItem(selectedTabItem).getData("Viewer");
			
			if (_searchResultViewer != null) {
				
				SearchResult[] _searchResults = _searchResultViewer.getSearchResults();

				FileDialog dialog = new FileDialog(shell, SWT.SAVE);
				dialog.setFilterNames(new String[] {"Excel Files", "All Files"});
				dialog.setFilterExtensions(new String[] {"*.xls", "*.*"});
				dialog.setFilterPath("C:\\");
				dialog.setFileName("export.xls");
				String file = dialog.open();

				if (file != null) {

					try {
						
						WritableWorkbook workbook = Workbook.createWorkbook(new File(file));
						
						WritableSheet sheet;
						
						sheet = workbook.createSheet(Messages.getString("Members_with_statements"), 0);
						
						sheet.addCell(new jxl.write.Label(0, 0, Messages.getString("Library"))); 
						sheet.addCell(new jxl.write.Label(1, 0, Messages.getString("Source_file"))); 
						sheet.addCell(new jxl.write.Label(2, 0, Messages.getString("Member"))); 
						sheet.addCell(new jxl.write.Label(3, 0, Messages.getString("Line"))); 
						sheet.addCell(new jxl.write.Label(4, 0, Messages.getString("Statement"))); 
						
						int line = 1;
						
						for (int index1 = 0; index1 < _searchResults.length; index1++) {
							
							SearchResultStatement[] _statements = _searchResults[index1].getStatements();
							
							for (int index2 = 0; index2 < _statements.length; index2++) {
								
								sheet.addCell(new jxl.write.Label(0, line, _searchResults[index1].getLibrary())); 
								sheet.addCell(new jxl.write.Label(1, line, _searchResults[index1].getFile())); 
								sheet.addCell(new jxl.write.Label(2, line, _searchResults[index1].getMember())); 
								sheet.addCell(new jxl.write.Label(3, line, Integer.toString(_statements[index2].getStatement()))); 
								sheet.addCell(new jxl.write.Label(4, line, _statements[index2].getLine())); 

								line++;
								
							}

							line++;
						}
						
						sheet = workbook.createSheet(Messages.getString("Members"), 0);
						
						sheet.addCell(new jxl.write.Label(0, 0, Messages.getString("Library"))); 
						sheet.addCell(new jxl.write.Label(1, 0, Messages.getString("Source_file"))); 
						sheet.addCell(new jxl.write.Label(2, 0, Messages.getString("Member"))); 
						
						for (int index = 0; index < _searchResults.length; index++) {
							sheet.addCell(new jxl.write.Label(0, index + 1, _searchResults[index].getLibrary())); 
							sheet.addCell(new jxl.write.Label(1, index + 1, _searchResults[index].getFile())); 
							sheet.addCell(new jxl.write.Label(2, index + 1, _searchResults[index].getMember())); 
						}
						
						workbook.write(); 
						workbook.close(); 
						
					} 
					catch (IOException e) {
						e.printStackTrace();
					} 
					catch (WriteException e) {
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
				actionExportToExcel.setEnabled(false);
				actionRemoveTabItem.setEnabled(false);
			}
		}
	}

}
