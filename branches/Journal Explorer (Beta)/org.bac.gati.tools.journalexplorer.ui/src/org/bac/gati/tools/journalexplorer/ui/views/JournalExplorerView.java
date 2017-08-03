package org.bac.gati.tools.journalexplorer.ui.views;

import java.util.ArrayList;

import org.bac.gati.tools.journalexplorer.ui.JournalExplorerPlugin;
import org.bac.gati.tools.journalexplorer.ui.dialogs.AddJournalDialog;
import org.bac.gati.tools.journalexplorer.ui.widgets.JournalEntriesViewer;
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

import biz.isphere.journalexplorer.core.internals.Messages;
import biz.isphere.journalexplorer.core.internals.SelectionProviderIntermediate;
import biz.isphere.journalexplorer.core.model.File;
import biz.isphere.journalexplorer.rse.shared.ui.labelprovider.JournalColumnLabel;

public class JournalExplorerView extends ViewPart {

    public static final String ID = "org.bac.gati.tools.journalexplorer.ui.views.JournalExplorerView"; //$NON-NLS-1$

    private Action openJournalAction;

    private Action highlightUserEntries;

    private Action reloadEntries;

    private CTabFolder tabs;

    private ArrayList<JournalEntriesViewer> journalViewers;

    private SelectionProviderIntermediate selectionProviderIntermediate;

    public JournalExplorerView() {
        this.selectionProviderIntermediate = new SelectionProviderIntermediate();
        this.journalViewers = new ArrayList<JournalEntriesViewer>();
    }

    /**
     * Create contents of the view part.
     * 
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout(SWT.HORIZONTAL));

        this.tabs = new CTabFolder(container, SWT.BOTTOM | SWT.CLOSE);
        this.tabs.addCTabFolder2Listener(new CTabFolder2Listener() {
            public void showList(CTabFolderEvent arg0) {
            }

            public void restore(CTabFolderEvent arg0) {
            }

            public void minimize(CTabFolderEvent arg0) {
            }

            public void maximize(CTabFolderEvent arg0) {
            }

            public void close(CTabFolderEvent event) {
                if (event.item instanceof JournalEntriesViewer) {
                    JournalEntriesViewer viewer = ((JournalEntriesViewer)event.item);

                    viewer.removeAsSelectionProvider(selectionProviderIntermediate);
                    JournalExplorerView.this.journalViewers.remove(viewer);
                }

            }
        });
        this.createActions();
        this.initializeToolBar();
        this.getSite().setSelectionProvider(this.selectionProviderIntermediate);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Create the actions.
     */
    private void createActions() {

        // /
        // / openJournalAction
        // /
        this.openJournalAction = new Action(Messages.JournalExplorerView_OpenJournal) {

            @Override
            public void run() {

                AddJournalDialog addJournalDialog = new AddJournalDialog(JournalExplorerView.this.getSite().getShell());
                addJournalDialog.create();
                int result = addJournalDialog.open();

                if (result == Window.OK) {
                    File outputFile = new File();
                    outputFile.setOutFileLibrary(addJournalDialog.getLibrary());
                    outputFile.setOutFileName(addJournalDialog.getFileName());
                    outputFile.setConnetionName(addJournalDialog.getConnectionName());
                    JournalExplorerView.this.handleAddJournal(outputFile);
                }
            }
        };
        this.openJournalAction.setImageDescriptor(JournalExplorerPlugin
            .getImageDescriptor(JournalExplorerPlugin.IMAGE_TABLE_BOTTOM_LEFT_CORNER_NEW_GREEN));

        // /
        // / highlightUserEntries action
        // /
        this.highlightUserEntries = new Action(Messages.JournalExplorerView_HighlightUserEntries) {
            @Override
            public void run() {
                JournalColumnLabel.setHighlightUserEntries(!JournalColumnLabel.isHighlightUserEntries());
                JournalExplorerView.this.refreshAllViewers();
            }
        };
        highlightUserEntries.setImageDescriptor(JournalExplorerPlugin.getImageDescriptor(JournalExplorerPlugin.IMAGE_HIGHLIGHT));

        // /
        // / reParseEntries action
        // /
        this.reloadEntries = new Action(Messages.JournalEntryView_ReloadEntries) {
            @Override
            public void run() {
                // JournalEntryView.this.reParseAllEntries();
                try {
                    JournalEntriesViewer viewer = (JournalEntriesViewer)tabs.getSelection();
                    viewer.openJournal();
                } catch (Exception exception) {
                    MessageDialog.openError(JournalExplorerView.this.getSite().getShell(), Messages.Error, exception.getMessage());
                }
            }
        };
        reloadEntries.setImageDescriptor(JournalExplorerPlugin.getImageDescriptor(JournalExplorerPlugin.IMAGE_REFRESH));

    }

    private void refreshAllViewers() {
        for (JournalEntriesViewer viewer : this.journalViewers) {
            viewer.refreshTable();
        }
    }

    private void handleAddJournal(File outputFile) {

        JournalEntriesViewer journalViewer = null;

        try {

            journalViewer = new JournalEntriesViewer(this.tabs, outputFile);
            journalViewer.setAsSelectionProvider(this.selectionProviderIntermediate);
            journalViewer.openJournal();

            this.journalViewers.add(journalViewer);
            this.tabs.setSelection(journalViewer);
        } catch (Exception exception) {
            MessageDialog.openError(this.getSite().getShell(), Messages.Error, exception.getMessage()); //$NON-NLS-1$

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
        tbm.add(this.reloadEntries);
    }

    @Override
    public void setFocus() {
    }
}
