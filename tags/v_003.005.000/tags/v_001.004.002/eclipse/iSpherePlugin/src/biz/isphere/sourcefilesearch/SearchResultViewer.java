/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.sourcefilesearch;

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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.ISpherePlugin;
import biz.isphere.Messages;
import biz.isphere.internal.IEditor;


public class SearchResultViewer {

	private Object connection;
//	private String searchString;
	private SearchResult[] _searchResults; 
	private TableViewer tableViewerMembers;
	private Table tableMembers;
	private Object[] selectedItemsMembers;
//	private Shell shell;
	private TableViewer tableViewerStatements;
	private Table tableStatements;
	private String[] statements;
	
	private class LabelProviderTableViewerMembers extends LabelProvider implements ITableLabelProvider {

		public String getColumnText(Object element, int columnIndex) {
			if (columnIndex == 0) {
				return ((SearchResult)element).getLibrary() + "-" +
						((SearchResult)element).getFile() + "(" +
						((SearchResult)element).getMember() + ")" + " - \"" +
						((SearchResult)element).getDescription() + "\"";
			}
			return "*UNKNOWN";
		}
		
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		
	}
	
	private class ContentProviderTableViewerMembers implements IStructuredContentProvider {
		
		public Object[] getElements(Object inputElement) {
			return _searchResults;
		}
		
		
		public void dispose() {
		}
		
		
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		
	}

	private class SorterTableViewerMembers extends ViewerSorter {
		
		public int compare(Viewer viewer, Object e1, Object e2) {
			int result = ((SearchResult)e1).getLibrary().compareTo(((SearchResult)e2).getLibrary());
			if (result == 0) {
				result = ((SearchResult)e1).getFile().compareTo(((SearchResult)e2).getFile());			
				if (result == 0) {
					result = ((SearchResult)e1).getMember().compareTo(((SearchResult)e2).getMember());
				}
			}
			return result;
		}
		
	}
	
	private class LabelProviderStatements extends LabelProvider implements ITableLabelProvider {
		
		public String getColumnText(Object element, int columnIndex) {
			if (columnIndex == 0) {
				return (String)element;
			} 
			return "*UNKNOWN";
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
	
	public SearchResultViewer(Object connection, String searchString, SearchResult[] _searchResults) {
		this.connection = connection;
	//	this.searchString = searchString;
		this._searchResults = _searchResults;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void createContents(Composite parent) {	
		
	//	shell = parent.getShell();
		
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout());
		
		final SashForm sashFormSearchResult = new SashForm(container, SWT.BORDER);
		sashFormSearchResult.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		tableViewerMembers = new TableViewer(sashFormSearchResult, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewerMembers.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				retrieveSelectedTableItems();
				setStatements();
			}
		});
		tableViewerMembers.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
			    if(tableViewerMembers.getSelection() instanceof IStructuredSelection) {

			    	IStructuredSelection structuredSelection = (IStructuredSelection)tableViewerMembers.getSelection();
					SearchResult _searchResult = (SearchResult)structuredSelection.getFirstElement();
					
					IEditor editor = ISpherePlugin.getEditor();
					
					if (editor != null) {
						
						editor.openEditor(connection, _searchResult.getLibrary(), _searchResult.getFile(), _searchResult.getMember(), 0, "*OPEN");
						
					}
					
			    }
			}
		});
		tableViewerMembers.setSorter(new SorterTableViewerMembers());
		tableViewerMembers.setLabelProvider(new LabelProviderTableViewerMembers());
		tableViewerMembers.setContentProvider(new ContentProviderTableViewerMembers());

		tableMembers = tableViewerMembers.getTable();	
		tableMembers.setLinesVisible(true);
		tableMembers.setHeaderVisible(true);
		tableMembers.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final TableColumn tableColumnMember = new TableColumn(tableMembers, SWT.NONE);
		tableColumnMember.setWidth(800);
		tableColumnMember.setText(Messages.getString("Member"));
	
		final Menu menuTableMembers = new Menu(tableMembers);
		menuTableMembers.addMenuListener(new MenuAdapter() {

			private MenuItem menuItemOpenEditor;
			private MenuItem menuItemSelectAll;
			private MenuItem menuItemDeselectAll;
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

				if (!(selectedItemsMembers == null || selectedItemsMembers.length == 0)) {
					
					menuItemOpenEditor = new MenuItem(menuTableMembers, SWT.NONE);
					menuItemOpenEditor.setText(Messages.getString("Open_editor"));
					menuItemOpenEditor.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_OPEN_EDITOR));
					menuItemOpenEditor.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							executeMenuItemOpenEditor();
						}
					}); 	
					
				}
				
				menuItemSelectAll = new MenuItem(menuTableMembers, SWT.NONE);
				menuItemSelectAll.setText(Messages.getString("Select_all"));
				menuItemSelectAll.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_SELECT_ALL));
				menuItemSelectAll.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						executeMenuItemSelectAll();
					}
				}); 	

				menuItemDeselectAll = new MenuItem(menuTableMembers, SWT.NONE);
				menuItemDeselectAll.setText(Messages.getString("Deselect_all"));
				menuItemDeselectAll.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_DESELECT_ALL));
				menuItemDeselectAll.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						executeMenuItemDeselectAll();
					}
				}); 	
				
			}
		});
		tableMembers.setMenu(menuTableMembers);

		tableViewerMembers.setInput(new Object());
		
		tableViewerStatements = new TableViewer(sashFormSearchResult, SWT.FULL_SELECTION | SWT.BORDER);
		tableViewerStatements.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {

				if (selectedItemsMembers != null && selectedItemsMembers.length == 1) {
					
					SearchResult _searchResult = (SearchResult)selectedItemsMembers[0];
					
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
							} 
							catch (NumberFormatException e1) {
							}
						}
						
						IEditor editor = ISpherePlugin.getEditor();
						
						if (editor != null) {
							
							editor.openEditor(connection, _searchResult.getLibrary(), _searchResult.getFile(), _searchResult.getMember(), statementLine, "*OPEN");
							
						}
						
				    }
					
				}
			    
			}
		});
		tableViewerStatements.setLabelProvider(new LabelProviderStatements());
		tableViewerStatements.setContentProvider(new ContentProviderStatements());

		tableStatements = tableViewerStatements.getTable();
		tableStatements.setHeaderVisible(true);
		tableStatements.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final TableColumn tableColumnStatement = new TableColumn(tableStatements, SWT.NONE);
		tableColumnStatement.setWidth(800);
		tableColumnStatement.setText(Messages.getString("Statement"));

		setStatements();
		tableViewerStatements.setInput(new Object());
		
		sashFormSearchResult.setWeights(new int[] {1, 1 });
		
	}
	
	private void retrieveSelectedTableItems() {
	    if(tableViewerMembers.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection)tableViewerMembers.getSelection();
			selectedItemsMembers = structuredSelection.toArray();
	    } 
	    else {
		    selectedItemsMembers = new Object[0];
	    }
	}
	
	private void executeMenuItemSelectAll() {

		Object[] objects = new Object[tableMembers.getItemCount()];
		for (int idx=0; idx<tableMembers.getItemCount(); idx++) {
			objects[idx] = tableViewerMembers.getElementAt(idx);
		}
		tableViewerMembers.setSelection(new StructuredSelection(objects),true); 
		selectedItemsMembers = objects;
		tableMembers.setFocus();
		
		setStatements();
		
	}

	private void executeMenuItemDeselectAll() {
		
		tableViewerMembers.setSelection(new StructuredSelection(), true);
	    selectedItemsMembers = new Object[0];
		
		setStatements();
		
	}

	private void executeMenuItemOpenEditor() {

		IEditor editor = ISpherePlugin.getEditor();
		
		if (editor != null) {
			
			for (int idx = 0; idx < selectedItemsMembers.length; idx++) {

				SearchResult _searchResult = (SearchResult)selectedItemsMembers[idx];

				editor.openEditor(connection, _searchResult.getLibrary(), _searchResult.getFile(), _searchResult.getMember(), 0, "*OPEN");
				
			}
			
		}
		
	}
	
	private void setStatements() {
		if (selectedItemsMembers == null || selectedItemsMembers.length == 0) {
			statements = new String[1];
			statements[0] = Messages.getString("No_selection.");
		}
		else if (selectedItemsMembers.length == 1) {
			SearchResult _searchResult = (SearchResult)selectedItemsMembers[0];
			SearchResultStatement[] _statements = _searchResult.getStatements();
			statements = new String[_statements.length];
			for (int idx = 0; idx < _statements.length; idx++) {
				statements[idx] = "(" + Integer.toString(_statements[idx].getStatement()) + ") " +_statements[idx].getLine();
			}
		}
		else {
			statements = new String[1];
			statements[0] = Messages.getString("Multiple_selection.");
		}
		tableViewerStatements.refresh();
		
	}

	public Object getConnection() {
		return connection;
	}
	
	public SearchResult[] getSearchResults() {
		return _searchResults;
	}
	
}
