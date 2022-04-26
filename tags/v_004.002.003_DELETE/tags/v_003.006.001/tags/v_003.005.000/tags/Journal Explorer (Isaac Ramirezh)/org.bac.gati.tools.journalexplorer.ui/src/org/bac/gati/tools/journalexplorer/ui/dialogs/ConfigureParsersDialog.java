package org.bac.gati.tools.journalexplorer.ui.dialogs;

import org.bac.gati.tools.journalexplorer.internals.Messages;
import org.bac.gati.tools.journalexplorer.internals.QualifiedName;
import org.bac.gati.tools.journalexplorer.model.MetaDataCache;
import org.bac.gati.tools.journalexplorer.model.MetaTable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;

public class ConfigureParsersDialog extends Dialog {
	
	TableViewer tableViewer;
	
	private Table table;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ConfigureParsersDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite container = (Composite) super.createDialogArea(parent);
		FillLayout fl_container = new FillLayout(SWT.HORIZONTAL);
		fl_container.marginHeight = 10;
		fl_container.marginWidth = 10;
		container.setLayout(fl_container);
		
		this.tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		this.table = tableViewer.getTable();
		this.table.setLinesVisible(true);
		this.table.setHeaderVisible(true);
		this.tableViewer.setContentProvider(new ArrayContentProvider());
		
		this.configureTable();
		this.populate();

		return container;
	}

	private void configureTable() {
		///
		/// journaledObject column
		///
		TableViewerColumn journaledObject = new TableViewerColumn(this.tableViewer, SWT.NONE);
		journaledObject.getColumn().setWidth(150);
		journaledObject.getColumn().setText(Messages.ConfigureParsersDialog_JournalObject);
		journaledObject.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				
				if (element instanceof MetaTable) {
					MetaTable currentElement = (MetaTable) element;
					return QualifiedName.getName(currentElement.getLibrary(), currentElement.getName());
				} else {
					return null;
				}
			}
			
			@Override
			public Image getImage(Object element) {
				if (element instanceof MetaTable) {
					if (((MetaTable) element).isLoaded()) {
						return null;
					} else { 
						return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_ERROR);
					}
				} else {
					return null;
				}
			}
		});
		
		///
		/// parserLibrary column
		///
		TableViewerColumn parserLibrary = new TableViewerColumn(this.tableViewer, SWT.NONE);
		parserLibrary.getColumn().setWidth(150);
		parserLibrary.getColumn().setText(Messages.ConfigureParsersDialog_DefinitionLibrary);
		parserLibrary.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				
				if (element instanceof MetaTable) {
					MetaTable currentElement = (MetaTable) element;
					return currentElement.getDefinitionLibrary().trim();
				} else {
					return null;
				}
			}
		});
		parserLibrary.setEditingSupport(new ParserLibraryEditingSupport(tableViewer));
		
		///
		/// parserObject column
		///
		TableViewerColumn parserObject = new TableViewerColumn(this.tableViewer, SWT.NONE);
		parserObject.getColumn().setWidth(150);
		parserObject.getColumn().setText(Messages.ConfigureParsersDialog_DefinitionObject);
		parserObject.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				
				if (element instanceof MetaTable) {
					MetaTable currentElement = (MetaTable) element;
					return currentElement.getDefinitionName().trim();
				} else {
					return null;
				}
			}
		});
		parserObject.setEditingSupport(new ParserNameEditingSupport(tableViewer));
		
		///
		/// parsingOffset column
		///
		TableViewerColumn parsingOffset = new TableViewerColumn(this.tableViewer, SWT.NONE);
		parsingOffset.getColumn().setWidth(170);
		parsingOffset.getColumn().setText(Messages.ConfigureParsersDialog_ParsingOffset);
		parsingOffset.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				
				if (element instanceof MetaTable) {
					MetaTable currentElement = (MetaTable) element;
					return Integer.toString(currentElement.getParsingOffset());
				} else {
					return null;
				}
			}
		});
		parsingOffset.setEditingSupport(new ParsingOffsetEditingSupport(tableViewer));
	}
	
	private void populate() {
		this.tableViewer.setInput(MetaDataCache.INSTANCE.getCachedParsers());
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(643, 300);
	}
	
	class ParserLibraryEditingSupport extends EditingSupport {

		private final CellEditor cellEditor;
		private TableViewer viewer;
		
		
		public ParserLibraryEditingSupport(TableViewer viewer) {
			super(viewer);
			this.viewer = viewer;
			this.cellEditor = new TextCellEditor(viewer.getTable());
		}

		@Override
		protected boolean canEdit(Object arg0) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object arg0) {
			return this.cellEditor;
		}

		@Override
		protected Object getValue(Object object) {
			if (object instanceof MetaTable) {
				MetaTable currentItem = (MetaTable) object;
				return currentItem.getDefinitionLibrary().trim();
			} else {
				return null;
			}
		}

		@Override
		protected void setValue(Object element, Object userInput) {
			if (element instanceof MetaTable) {
				MetaTable currentItem = (MetaTable) element;
				String input = String.valueOf(userInput).trim(); 
				
				if (!input.equals(currentItem.getDefinitionLibrary().trim())) {
					
					currentItem.setDefinitionLibrary(input);
					currentItem.setLoaded(false);
					
					MetaDataCache.INSTANCE.saveMetaData(currentItem);
					
					this.viewer.update(element, null);
				}
			}
		}
	}
	
	class ParserNameEditingSupport extends EditingSupport {

		private final CellEditor cellEditor;
		private TableViewer viewer;
		
		
		public ParserNameEditingSupport(TableViewer viewer) {
			super(viewer);
			this.viewer = viewer;
			this.cellEditor = new TextCellEditor(viewer.getTable());
		}

		@Override
		protected boolean canEdit(Object arg0) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object arg0) {
			return this.cellEditor;
		}

		@Override
		protected Object getValue(Object object) {
			if (object instanceof MetaTable) {
				MetaTable currentItem = (MetaTable) object;
				return currentItem.getDefinitionName().trim();
			} else {
				return null;
			}
		}

		@Override
		protected void setValue(Object element, Object userInput) {
			if (element instanceof MetaTable) {
				MetaTable currentItem = (MetaTable) element;
				String input = String.valueOf(userInput).trim();
				
				if (!input.equals(currentItem.getDefinitionName().trim())) {
					currentItem.setDefinitionName(input);
					currentItem.setLoaded(false);
					
					MetaDataCache.INSTANCE.saveMetaData(currentItem);
					
					this.viewer.update(element, null);
				}
			}
		}
	}

	class ParsingOffsetEditingSupport extends EditingSupport {

		private final CellEditor cellEditor;
		private TableViewer viewer;
		
		
		public ParsingOffsetEditingSupport(TableViewer viewer) {
			super(viewer);
			this.viewer = viewer;
			this.cellEditor = new TextCellEditor(viewer.getTable());
		}

		@Override
		protected boolean canEdit(Object arg0) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object arg0) {
			return this.cellEditor;
		}

		@Override
		protected Object getValue(Object object) {
			if (object instanceof MetaTable) {
				MetaTable currentItem = (MetaTable) object;
				return Integer.toString(currentItem.getParsingOffset());
			} else {
				return null;
			}
		}

		@Override
		protected void setValue(Object element, Object userInput) {
			if (element instanceof MetaTable) {
				MetaTable currentItem = (MetaTable) element;
				int input = Integer.valueOf(userInput.toString());
				
				if (input != currentItem.getParsingOffset()) {
					currentItem.setParsingOffset(input);
					
					MetaDataCache.INSTANCE.saveMetaData(currentItem);
					
					this.viewer.update(element, null);
				}
			}
		}
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.ConfigureParsersDialog_SetDefinitions);
	}
	
}
