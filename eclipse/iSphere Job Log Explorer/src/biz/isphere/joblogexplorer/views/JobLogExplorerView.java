/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.joblogexplorer.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.preferences.DoNotAskMeAgain;
import biz.isphere.core.preferences.DoNotAskMeAgainDialog;
import biz.isphere.core.swt.widgets.sqleditor.SQLSyntaxErrorException;
import biz.isphere.core.swt.widgets.sqleditor.SqlEditor;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.action.EditSqlAction;
import biz.isphere.joblogexplorer.action.ExportToExcelAction;
import biz.isphere.joblogexplorer.action.RefreshAction;
import biz.isphere.joblogexplorer.action.ResetColumnSizeAction;
import biz.isphere.joblogexplorer.editor.AbstractJobLogExplorerInput;
import biz.isphere.joblogexplorer.editor.IJobLogExplorerStatusChangedListener;
import biz.isphere.joblogexplorer.editor.JobLogExplorerStatusChangedEvent;

public class JobLogExplorerView extends ViewPart implements IJobLogExplorerStatusChangedListener, SelectionListener {

    public static final String ID = "biz.isphere.joblogexplorer.views.JobLogExplorerView"; //$NON-NLS-1$

    private EditSqlAction editSqlAction;
    private RefreshAction reloadEntriesAction;
    private ExportToExcelAction exportToExcelAction;
    private ResetColumnSizeAction resetColumnSizeAction;

    private CTabFolder tabFolder;

    public JobLogExplorerView() {
        super();
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

        tabFolder = new CTabFolder(container, SWT.TOP | SWT.CLOSE);
        tabFolder.addSelectionListener(this);
        tabFolder.addCTabFolder2Listener(new CTabFolder2Listener() {
            public void showList(CTabFolderEvent arg0) {
            }

            public void restore(CTabFolderEvent arg0) {
            }

            public void minimize(CTabFolderEvent arg0) {
            }

            public void maximize(CTabFolderEvent arg0) {
            }

            public void close(CTabFolderEvent event) {
                if (event.item instanceof JobLogExplorerTab) {
                    CTabItem activeTab = tabFolder.getSelection();
                    CTabItem closedTab = (CTabItem)event.item;
                    if (closedTab.equals(activeTab)) {
                        int closedTabIndex = tabFolder.getSelectionIndex();
                        int newTabIndex;
                        int maxTabIndex = tabFolder.getItemCount() - 1;
                        if (closedTabIndex < maxTabIndex) {
                            newTabIndex = closedTabIndex + 1;
                        } else {
                            newTabIndex = closedTabIndex - 1;
                        }
                        if (newTabIndex >= 0) {
                            tabFolder.setSelection(newTabIndex);
                            updateViewerStatus(getCurrentViewer());
                        } else {
                            clearViewerStatus();
                        }
                    }
                }

            }
        });

        createActions();
        initializeToolBar();

        clearViewerStatus();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Creates the actions of the view.
     */
    private void createActions() {

        editSqlAction = new EditSqlAction();
        reloadEntriesAction = new RefreshAction();
        exportToExcelAction = new ExportToExcelAction();
        resetColumnSizeAction = new ResetColumnSizeAction();
    }

    public void openExplorerTab(AbstractJobLogExplorerInput input) {

        if (input == null) {
            throw new IllegalArgumentException("Parameter 'input' must not be [null]."); //$NON-NLS-1$
        }

        JobLogExplorerTab jobLogExplorerTab = findExplorerTab(input);
        if (jobLogExplorerTab == null) {
            jobLogExplorerTab = new JobLogExplorerTab(getTabFolder(), new SqlEditorSelectionListener());
            jobLogExplorerTab.setSqlEditorVisibility(false);
            jobLogExplorerTab.addStatusChangedListener(this);
            jobLogExplorerTab.setInput(input);
        }

        tabFolder.setSelection(jobLogExplorerTab);
    }

    private JobLogExplorerTab findExplorerTab(AbstractJobLogExplorerInput input) {

        CTabItem[] tabItems = getTabFolder().getItems();
        for (CTabItem tabItem : tabItems) {
            JobLogExplorerTab jobLogTab = (JobLogExplorerTab)tabItem;
            AbstractJobLogExplorerInput tabInput = jobLogTab.getInput();
            if (tabInput == null || input.isSameInput(tabInput)) {
                return jobLogTab;
            }
        }

        return null;
    }

    private void handleDataLoadException(final Object object, final String message, final Throwable e) {

        if (Display.getCurrent() != null) {
            disposeJobLogExplorerTabChecked(object);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, message);
        } else {
            new UIJob("") {
                @Override
                public IStatus runInUIThread(IProgressMonitor var1) {
                    if (object != null) {
                        disposeJobLogExplorerTabChecked(object);
                        MessageDialog.openError(getShell(), Messages.E_R_R_O_R, message);
                    }
                    return Status.OK_STATUS;
                }
            }.schedule();
        }
    }

    private void disposeJobLogExplorerTabChecked(Object object) {
        if (object instanceof JobLogExplorerTab) {
            JobLogExplorerTab tabItem = (JobLogExplorerTab)object;
            tabItem.dispose();
        }
    }

    /**
     * Initialize the toolbar.
     */
    private void initializeToolBar() {

        IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
        toolBarManager.add(editSqlAction);
        toolBarManager.add(exportToExcelAction);
        toolBarManager.add(new Separator());
        toolBarManager.add(resetColumnSizeAction);
        toolBarManager.add(new Separator());
        toolBarManager.add(reloadEntriesAction);
    }

    /*
     * View settings.
     */

    @Override
    public void setFocus() {
        updateViewerStatus(getCurrentViewer());
    }

    @Override
    public void showBusy(boolean busy) {

        if (busy) {
            setBusyCursor();
        } else {
            restoreCursor();
        }
    }

    private void setBusyCursor() {

        if (Display.getCurrent() != null) {
            Cursor busyCursor = Display.getCurrent().getSystemCursor(SWT.CURSOR_WAIT);
            if (busyCursor != null) {
                getShell().setCursor(busyCursor);
            }
        }
    }

    private void restoreCursor() {
        getShell().setCursor(null);
    }

    /**
     * Enables the actions for the current viewer,
     * 
     * @param viewer - the selected viewer (tab)
     */
    private void setActionEnablement(JobLogExplorerTab tabItem) {

        if (tabItem != null && tabItem.getItemsCount() > 0) {
            exportToExcelAction.setEnabled(true);
            exportToExcelAction.setTabItem(tabItem);
        } else {
            exportToExcelAction.setEnabled(false);
            exportToExcelAction.setTabItem(null);
        }

        if (tabItem != null && tabItem.getTotalItemsCount() > 0) {
            reloadEntriesAction.setEnabled(true);
            reloadEntriesAction.setTabItem(tabItem);
        } else {
            reloadEntriesAction.setEnabled(false);
            reloadEntriesAction.setTabItem(null);
        }

        if (tabItem != null) {
            editSqlAction.setEnabled(true);
            editSqlAction.setChecked(tabItem.isSqlEditorVisible());
            editSqlAction.setTabItem(tabItem);
            resetColumnSizeAction.setEnabled(true);
            resetColumnSizeAction.setTabItem(tabItem);
        } else {
            editSqlAction.setEnabled(false);
            editSqlAction.setChecked(false);
            editSqlAction.setTabItem(null);
            resetColumnSizeAction.setEnabled(false);
            resetColumnSizeAction.setTabItem(null);
        }
    }

    private Shell getShell() {
        return getSite().getShell();
    }

    /**
     * Called by the viewer, when items are selected by the user.
     */
    public void finishedDataLoading(JobLogExplorerTab tabItem) {
        tabFolder.setSelection(tabItem);

        new UIJob("") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                DoNotAskMeAgainDialog.openInformation(getShell(), DoNotAskMeAgain.INFORMATION_USAGE_JOB_LOG_EXPLORER,
                    Messages.Use_the_exclamation_mark_to_negate_a_search_argument_eg_Completion);
                return Status.OK_STATUS;
            }
        }.schedule();

    }

    public CTabFolder getTabFolder() {
        if (tabFolder == null || tabFolder.isDisposed()) {
            return null;
        }
        return tabFolder;
    }

    private void updateViewerStatusChecked(Object object) {
        if (object instanceof JobLogExplorerTab) {
            updateViewerStatus((JobLogExplorerTab)object);
        }
    }

    private void updateViewerStatus(JobLogExplorerTab tabItem) {

        if (tabItem == null) {
            clearViewerStatus();
            setActionEnablement(tabItem);
            return;
        }

        String message;

        if (tabItem.getItemsCount() == tabItem.getTotalItemsCount()) {
            message = Messages.bind(Messages.Number_of_messages_A, tabItem.getTotalItemsCount());
        } else {
            message = Messages.bind(Messages.Number_of_messages_B_slash_A, tabItem.getTotalItemsCount(), tabItem.getItemsCount());
        }

        updateViewerStatus(message);
        setActionEnablement(tabItem);
    }

    private void clearViewerStatus() {

        setActionEnablement(null);
        updateViewerStatus(""); //$NON-NLS-1$
    }

    private void updateViewerStatus(String message) {

        IActionBars bars = getViewSite().getActionBars();
        bars.getStatusLineManager().setMessage(message);
    }

    public void statusChanged(JobLogExplorerStatusChangedEvent data) {

        updateViewerStatusChecked(data.getSource());

        if (data.getEventType() == JobLogExplorerStatusChangedEvent.EventType.STARTED_DATA_LOADING) {
            showBusy(true);
        } else {
            showBusy(false);
        }

        if (data.getEventType() == JobLogExplorerStatusChangedEvent.EventType.DATA_LOAD_ERROR) {
            handleDataLoadException(data.getSource(), data.getMessage(), data.getThrowable());
        }
    }

    /**
     * Called by the UI, when the user selects a different viewer (tab).
     */
    public void widgetSelected(SelectionEvent event) {

        Object source = event.getSource();
        if (source instanceof CTabFolder) {
            JobLogExplorerTab tabItem = getCurrentViewer();
            updateViewerStatus(tabItem);
        }
    }

    public void widgetDefaultSelected(SelectionEvent event) {
        widgetSelected(event);
    }

    /**
     * Returns the currently selected viewer (tab).
     * 
     * @return selected viewer
     */
    public JobLogExplorerTab getCurrentViewer() {
        CTabFolder tabFolder = getTabFolder();
        if (tabFolder == null) {
            return null;
        }
        return (JobLogExplorerTab)tabFolder.getSelection();
    }

    private void performFilterJobLogEntries(JobLogExplorerTab tabItem) throws SQLSyntaxErrorException {

        tabItem.validateWhereClause(getShell());
        tabItem.filterJobLogMessages();
    }

    private class SqlEditorSelectionListener implements SelectionListener {

        public void widgetSelected(SelectionEvent event) {
            Object source = event.getSource();
            if (source instanceof Button) {
                Button button = (Button)event.getSource();

                try {
                    getCurrentViewer().storeSqlEditorHistory();
                    refreshSqlEditorHistory();
                    performFilterJobLogEntries(getCurrentViewer());
                } catch (SQLSyntaxErrorException e) {
                    MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
                    getCurrentViewer().setFocusOnSqlEditor();
                } catch (Exception e) {
                    ISpherePlugin.logError("*** Error in method JobLogexplorerView.SqlEditorSelectionListener.widgetSelected() ***", e);
                    MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
                }

            }
        }

        private void refreshSqlEditorHistory() {
            for (CTabItem tabItem : tabFolder.getItems()) {
                ((JobLogExplorerTab)tabItem).refreshSqlEditorHistory();
            }
        }

        private SqlEditor getSqlEditor(Composite parent) {

            if (parent instanceof SqlEditor) {
                return (SqlEditor)parent;
            } else if (parent instanceof Composite) {
                return getSqlEditor(parent.getParent());
            }

            return null;
        }

        public void widgetDefaultSelected(SelectionEvent event) {
            widgetDefaultSelected(event);
        }
    }

}
