package org.bac.gati.tools.journalexplorer.ui.widgets;

import java.util.ArrayList;

import org.bac.gati.tools.journalexplorer.internals.SelectionProviderIntermediate;
import org.bac.gati.tools.journalexplorer.model.Journal;
import org.bac.gati.tools.journalexplorer.model.dao.JournalDAO;
import org.bac.gati.tools.journalexplorer.ui.contentProviders.JournalViewerContentProvider;
import org.bac.gati.tools.journalexplorer.ui.labelProviders.JournalColumnLabel;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class JournalViewer extends CTabItem {

	private Composite container;
	
	private TableViewer tableViewer;
	
	private IBMiConnection connection;
	
	private String library;
	
	private String fileName;
	
	private ArrayList<Journal> data;
	
	public JournalViewer(CTabFolder parent, String library, String fileName, IBMiConnection connection) {
		
		super(parent, SWT.NONE);
		this.library    = library;
		this.fileName   = fileName;
		this.connection = connection;
		this.container  = new Composite(parent, SWT.NONE);
		this.initializeComponents();
	}

	private void initializeComponents() {
		
		this.container.setLayout(new FillLayout());
		this.setText(this.connection.getHostName() + ": " + library + "/" + fileName);
		this.initializeTable();
		this.container.layout(true);
		this.setControl(this.container);
	}

	private void initializeTable() {  
		
		Table table;
		TableViewerColumn newColumn;
		
		this.tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.READ_ONLY | SWT.VIRTUAL );
		
		table = this.tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setLinesVisible(true);
		table.setHeaderVisible(true); 
		
		///
		/// RRN Column
		///
		newColumn = new TableViewerColumn(this.tableViewer, SWT.RIGHT);
		newColumn.getColumn().setMoveable(true);
		newColumn.getColumn().setResizable(true);
		newColumn.getColumn().setWidth(45);
		newColumn.getColumn().setText("RRN");
		newColumn.setLabelProvider(new JournalColumnLabel() {
			
			@Override
			public Color getBackground(Object element) {
				return Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
			}
			
			@Override
			public String getText(Object element) {
				Journal journal = (Journal) element;
				return Integer.toString(journal.getRrn()).trim();
			}
		});
		
		///
		/// JOENTT Column
		///
		newColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
		newColumn.getColumn().setMoveable(true);
		newColumn.getColumn().setResizable(true);
		newColumn.getColumn().setWidth(55);
		newColumn.getColumn().setText("JOENTT");
		newColumn.setLabelProvider(new JournalColumnLabel() {
			@Override
			public String getText(Object element) {
				Journal journal = (Journal) element;
				return journal.getEntryType();
			}
		});
				
		///
		/// JOSEQN Column
		///
		newColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
		newColumn.getColumn().setMoveable(true);
		newColumn.getColumn().setResizable(true);
		newColumn.getColumn().setWidth(55);
		newColumn.getColumn().setText("JOSEQN");
		newColumn.setLabelProvider(new JournalColumnLabel() {
			@Override
			public String getText(Object element) {
				Journal journal = (Journal) element;
				return Long.toString(journal.getSequenceNumber());
			}
		});
				
		///
		/// JOCODE Column
		///
		newColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
		newColumn.getColumn().setMoveable(true);
		newColumn.getColumn().setResizable(true);
		newColumn.getColumn().setWidth(50);
		newColumn.getColumn().setText("JOCODE");
		newColumn.setLabelProvider(new JournalColumnLabel() {
			@Override
			public String getText(Object element) {
				Journal journal = (Journal) element;
				return journal.getJournalCode();
			}
		});
				
		///
		/// JOENTL Column
		///
		newColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
		newColumn.getColumn().setMoveable(true);
		newColumn.getColumn().setResizable(true);
		newColumn.getColumn().setWidth(50);
		newColumn.getColumn().setText("JOENTL");
		newColumn.setLabelProvider(new JournalColumnLabel() {
			@Override
			public String getText(Object element) {
				Journal journal = (Journal) element;
				return Integer.toString(journal.getEntryLength());
			}
		});
		
//		//TODO new JournalTimeColumnLabel().addColumnTo(tableViewer);
		
		///
		/// JODATE Column
		///
		newColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
		newColumn.getColumn().setMoveable(true);
		newColumn.getColumn().setResizable(true);
		newColumn.getColumn().setWidth(150);
		newColumn.getColumn().setText("JODATE");
		newColumn.setLabelProvider(new JournalColumnLabel() {
			@Override
			public String getText(Object element) {
				Journal journal = (Journal) element;
				return journal.getDate().toString();
			}
		});
		
		///
		/// JOJOB Column
		///
		newColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
		newColumn.getColumn().setMoveable(true);
		newColumn.getColumn().setResizable(true);
		newColumn.getColumn().setWidth(90);
		newColumn.getColumn().setText("JOJOB");
		newColumn.setLabelProvider(new JournalColumnLabel() {
			@Override
			public String getText(Object element) {
				Journal journal = (Journal) element;
				return journal.getJobName();
			}
		});
		
		///
		/// JOUSER Column
		///
		newColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
		newColumn.getColumn().setMoveable(true);
		newColumn.getColumn().setResizable(true);
		newColumn.getColumn().setWidth(90);
		newColumn.getColumn().setText("JOUSER");
		newColumn.setLabelProvider(new JournalColumnLabel() {
			@Override
			public String getText(Object element) {
				Journal journal = (Journal) element;
				return journal.getJobUserName();
			}
		});
				
		///
		/// JONBR Column
		///
		newColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
		newColumn.getColumn().setMoveable(true);
		newColumn.getColumn().setResizable(true);
		newColumn.getColumn().setWidth(90);
		newColumn.getColumn().setText("JONBR");
		newColumn.setLabelProvider(new JournalColumnLabel() {
			@Override
			public String getText(Object element) {
				Journal journal = (Journal) element;
				return Integer.toString(journal.getJobNumber());
			}
		});
				
		///
		/// JOPGM Column
		///
		newColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
		newColumn.getColumn().setMoveable(true);
		newColumn.getColumn().setResizable(true);
		newColumn.getColumn().setWidth(90);
		newColumn.getColumn().setText("JOPGM");
		newColumn.setLabelProvider(new JournalColumnLabel() {
			@Override
			public String getText(Object element) {
				Journal journal = (Journal) element;
				return journal.getProgramName();
			}
		});
				
		///
		/// JOLIB Column
		///
		newColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
		newColumn.getColumn().setMoveable(true);
		newColumn.getColumn().setResizable(true);
		newColumn.getColumn().setWidth(90);
		newColumn.getColumn().setText("JOLIB");
		newColumn.setLabelProvider(new JournalColumnLabel() {
			@Override
			public String getText(Object element) {
				Journal journal = (Journal) element;
				return journal.getObjectLibrary();
			}
		});
				
		///
		/// JOMBR Column
		///
		newColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
		newColumn.getColumn().setMoveable(true);
		newColumn.getColumn().setResizable(true);
		newColumn.getColumn().setWidth(90);
		newColumn.getColumn().setText("JOMBR");
		newColumn.setLabelProvider(new JournalColumnLabel() {
			@Override
			public String getText(Object element) {
				Journal journal = (Journal) element;
				return journal.getMemberName();
			}
		});
		
		///
		/// JOOBJ Column
		/// 
		newColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
		newColumn.getColumn().setMoveable(true);
		newColumn.getColumn().setResizable(true);
		newColumn.getColumn().setWidth(90);
		newColumn.getColumn().setText("JOOBJ");
		newColumn.setLabelProvider(new JournalColumnLabel() {
			@Override
			public String getText(Object element) {
				Journal journal = (Journal) element;
				return journal.getObjectName();
			}
		});
		///
		/// JOMINESD Column
		///
		newColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
		newColumn.getColumn().setMoveable(true);
		newColumn.getColumn().setResizable(true);
		newColumn.getColumn().setWidth(50);
		newColumn.getColumn().setText("JOMINESD");
		newColumn.setLabelProvider(new JournalColumnLabel() {
			@Override
			public String getText(Object element) {
				Journal journal = (Journal) element;
				return journal.getMinimizedSpecificData();
			}
		});
		
		///
		/// JOESD Column
		///
		newColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
		newColumn.getColumn().setMoveable(true);
		newColumn.getColumn().setResizable(true);
		newColumn.getColumn().setWidth(350);
		newColumn.getColumn().setText("JOESD");
		newColumn.setLabelProvider(new JournalColumnLabel() {
			@Override
			public String getText(Object element) {
				Journal journal = (Journal) element;
				
				//For displaying purposes, replace the null ending character for a blank.
				//Otherwise, the string was truncate by JFace
				return journal.getStringSpecificData().replace('\0', ' ').substring(1, 200);
			}
		});
		
		this.tableViewer.setContentProvider(new JournalViewerContentProvider(this.tableViewer));
	}
	
	public void openJournal() throws Exception {
		
		JournalDAO journalDAO = new JournalDAO(this.connection, this.library, this.fileName); 
		this.data = journalDAO.getJournalData(); 
		this.container.layout(true);
		this.tableViewer.setUseHashlookup(true);
		this.tableViewer.setItemCount(data.size());
		this.tableViewer.setInput(data);
	}
	
	@Override
	public void dispose() {
		
		super.dispose();
		
		if (this.data != null) {
			
			this.data.clear();
			this.data = null;
		}
		
		if (this.tableViewer != null) {
			
			this.tableViewer.getTable().dispose();
			this.tableViewer = null;
		}
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}

	public void setAsSelectionProvider(SelectionProviderIntermediate selectionProvider) {
		selectionProvider.setSelectionProviderDelegate(this.tableViewer);
	}
	
	public void removeAsSelectionProvider(SelectionProviderIntermediate selectionProvider) {
		selectionProvider.removeSelectionProviderDelegate(this.tableViewer);
	}

	public void refreshTable() {
		 if (this.tableViewer != null) {
			 this.tableViewer.refresh(true);
		 }
		
	}
	
}
