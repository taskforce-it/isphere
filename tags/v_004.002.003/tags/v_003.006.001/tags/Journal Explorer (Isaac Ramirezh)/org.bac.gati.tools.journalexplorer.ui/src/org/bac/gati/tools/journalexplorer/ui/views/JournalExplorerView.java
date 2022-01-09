package org.bac.gati.tools.journalexplorer.ui.views;

import java.util.ArrayList;

import org.bac.gati.tools.journalexplorer.internals.Messages;
import org.bac.gati.tools.journalexplorer.internals.SelectionProviderIntermediate;
import org.bac.gati.tools.journalexplorer.ui.dialogs.AddJournalDialog;
import org.bac.gati.tools.journalexplorer.ui.labelProviders.JournalColumnLabel;
import org.bac.gati.tools.journalexplorer.ui.widgets.JournalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class JournalExplorerView extends ViewPart {

	public static final String ID = "org.bac.gati.tools.journalexplorer.ui.views.JournalExplorerView";  //$NON-NLS-1$

	private Action openJournalAction;
	
	private Action highlightUserEntries;
	
	private CTabFolder tabs;
	
	private ArrayList<JournalViewer> journalViewers;
	
	private SelectionProviderIntermediate selectionProviderIntermediate;
	
	public JournalExplorerView() {
		this.selectionProviderIntermediate = new SelectionProviderIntermediate();
		this.journalViewers = new ArrayList<JournalViewer>();
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE); 
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		this.tabs = new CTabFolder(container, SWT.BOTTOM | SWT.CLOSE);
		this.tabs.addCTabFolder2Listener(new CTabFolder2Listener() {
			@Override
			public void showList(CTabFolderEvent arg0) { }
			
			@Override
			public void restore(CTabFolderEvent arg0) { }
			
			@Override
			public void minimize(CTabFolderEvent arg0) { }
			
			@Override
			public void maximize(CTabFolderEvent arg0) { }
			
			@Override
			public void close(CTabFolderEvent event) {
				if (event.item instanceof JournalViewer) {
					JournalViewer viewer = ((JournalViewer) event.item);
					
					viewer.removeAsSelectionProvider(selectionProviderIntermediate);
					JournalExplorerView.this.journalViewers.remove(viewer);
				}
				
			}
		});
		this.createActions();
		this.initializeToolBar();
		this.getSite().setSelectionProvider(this.selectionProviderIntermediate);
	}

	public void dispose() {
		super.dispose();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		
		/// 
		/// openJournalAction
		///
		this.openJournalAction = new Action(Messages.JournalExplorerView_OpenJournal) {
			
			@Override
			public void run() {
				
				AddJournalDialog addJournalDialog = new AddJournalDialog(JournalExplorerView.this.getSite().getShell());
				addJournalDialog.create();
				int result = addJournalDialog.open();
				
				if (result == Window.OK)
				{
					JournalExplorerView.this.handleAddJournal(
							addJournalDialog.getLibrary(),
							addJournalDialog.getFileName(),
							addJournalDialog.getConnection());
				}
			}
		};
		this.openJournalAction.setImageDescriptor(ResourceManager.getPluginImageDescriptor("org.bac.gati.tools.journalexplorer.ui", "/icons/table_bottom_left_corner_new_green.png")); //$NON-NLS-1$ //$NON-NLS-2$
		
		///
		/// highlightUserEntries action
		///
		this.highlightUserEntries = new Action(Messages.JournalExplorerView_HighlightUserEntries) { 
			@Override
			public void run() {
				JournalColumnLabel.setHighlightUserEntries(!JournalColumnLabel.isHighlightUserEntries());
				JournalExplorerView.this.refreshAllViewers();
			}
		};
		highlightUserEntries.setImageDescriptor(ResourceManager.getPluginImageDescriptor("org.bac.gati.tools.journalexplorer.ui", "icons/highlight.png")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private void refreshAllViewers() {
		for (JournalViewer viewer : this.journalViewers) {
			viewer.refreshTable();
		}
	}

	private void handleAddJournal(String library, String fileName, IBMiConnection connection) {
		
		JournalViewer journalViewer = null;
		
		try {
			
			journalViewer = new JournalViewer(this.tabs, library, fileName, connection);
			journalViewer.setAsSelectionProvider(this.selectionProviderIntermediate);
			journalViewer.openJournal();
			
			this.journalViewers.add(journalViewer);
			this.tabs.setSelection(journalViewer);
		}
		catch (Exception exception) {
			MessageDialog.openError(this.getSite().getShell(), "Error", exception.getMessage()); //$NON-NLS-1$
			
			if (journalViewer != null) {
				journalViewer.removeAsSelectionProvider(this.selectionProviderIntermediate);
				journalViewer.dispose();
			}
		}
	}
	
	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
		tbm.add(this.openJournalAction);
		tbm.add(this.highlightUserEntries);
	}

	@Override
	public void setFocus() { 
	}
}
