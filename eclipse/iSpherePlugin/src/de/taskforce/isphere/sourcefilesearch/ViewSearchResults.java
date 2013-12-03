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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import de.taskforce.isphere.ISpherePlugin;
import de.taskforce.isphere.Messages;

public class ViewSearchResults extends ViewPart {

	private Action actionRemoveTabItem;
	private TabFolder tabFolderSearchResults;
//	private Shell shell;
	
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout());
		
	//	shell = parent.getShell();

		tabFolderSearchResults = new TabFolder(container, SWT.NONE);
		
		createActions();
		initializeToolBar();
		initializeMenu();
		
	}

	private void createActions() {

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
		
		TabItem[] tabItemToBeSelected = new TabItem[1];
		tabItemToBeSelected[0] = tabItemSearchResult;
		tabFolderSearchResults.setSelection(tabItemToBeSelected);
		
		actionRemoveTabItem.setEnabled(true);		
	}
	
	public void removeTabItem() {
		int selectedTabItem = tabFolderSearchResults.getSelectionIndex();
		if (selectedTabItem >= 0) {
			tabFolderSearchResults.getItem(selectedTabItem).dispose();
			if (tabFolderSearchResults.getItemCount() == 0) {
				actionRemoveTabItem.setEnabled(false);
			}
		}
	}
	
}
