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
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FillLayout;
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
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.action.EditSqlAction;
import biz.isphere.joblogexplorer.action.ExportToExcelAction;
import biz.isphere.joblogexplorer.action.RefreshAction;
import biz.isphere.joblogexplorer.action.ResetColumnSizeAction;
import biz.isphere.joblogexplorer.editor.AbstractJobLogExplorerInput;
import biz.isphere.joblogexplorer.editor.IJobLogExplorerStatusChangedListener;
import biz.isphere.joblogexplorer.editor.JobLogExplorerStatusChangedEvent;

public class JobLogExplorerView extends ViewPart implements IJobLogExplorerStatusChangedListener, SelectionListener, ISelectionProvider {

    public static final String ID = "biz.isphere.joblogexplorer.views.JobLogExplorerView"; //$NON-NLS-1$

    private EditSqlAction editSqlAction;
    private RefreshAction reloadEntriesAction;
    private ExportToExcelAction exportToExcelAction;
    private ResetColumnSizeAction resetColumnSizeAction;

    private CTabFolder tabFolder;
    private ListenerList selectionChangedListeners;

    public JobLogExplorerView() {
        super();

        this.selectionChangedListeners = new ListenerList();
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
                    JobLogExplorerTab closedTab = (JobLogExplorerTab)event.item;
                    selectNextTab(closedTab);
                }

            }

            private void selectNextTab(CTabItem closedTab) {
                CTabItem activeTab = getSelectedViewer();
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
                        updateStatusLine();
                    } else {
                        clearStatusLine();
                    }
                }
            }
        });

        getSite().setSelectionProvider(this);

        createActions();
        initializeToolBar();

        clearStatusLine();
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

    private void disposeJobLogExplorerTabChecked(Object object) {
        if (object instanceof JobLogExplorerTab) {
            JobLogExplorerTab tabItem = (JobLogExplorerTab)object;
            tabItem.dispose();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Create view actions.
     */
    private void createActions() {

        editSqlAction = new EditSqlAction();
        reloadEntriesAction = new RefreshAction();
        exportToExcelAction = new ExportToExcelAction();
        resetColumnSizeAction = new ResetColumnSizeAction();
    }

    public void createExplorerTab(AbstractJobLogExplorerInput input) {

        if (input == null) {
            throw new IllegalArgumentException("Parameter 'input' must not be [null]."); //$NON-NLS-1$
        }

        JobLogExplorerTab jobLogExplorerTab = findExplorerTab(input);
        if (jobLogExplorerTab == null) {
            jobLogExplorerTab = new JobLogExplorerTab(tabFolder, new SqlEditorSelectionListener());
            jobLogExplorerTab.setSqlEditorVisibility(false);
            jobLogExplorerTab.addStatusChangedListener(this);
            jobLogExplorerTab.setInput(input);
        }

        tabFolder.setSelection(jobLogExplorerTab);
    }

    private JobLogExplorerTab findExplorerTab(AbstractJobLogExplorerInput input) {

        CTabItem[] tabItems = tabFolder.getItems();
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
            updateStatusLine();
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, message);
        } else {
            new UIJob("") {
                @Override
                public IStatus runInUIThread(IProgressMonitor var1) {
                    disposeJobLogExplorerTabChecked(object);
                    updateStatusLine();
                    MessageDialog.openError(getShell(), Messages.E_R_R_O_R, message);
                    return Status.OK_STATUS;
                }
            }.schedule();
        }
    }

    /**
     * Returns the tab folder.
     * 
     * @return tab folder
     */
    private CTabFolder getTabFolder() {
        if (tabFolder == null || tabFolder.isDisposed()) {
            return null;
        }
        return tabFolder;
    }

    /**
     * Returns the currently selected viewer (tab).
     * 
     * @return selected viewer
     */
    private JobLogExplorerTab getSelectedViewer() {
        CTabFolder tabFolder = getTabFolder();
        if (tabFolder == null) {
            return null;
        }
        return (JobLogExplorerTab)tabFolder.getSelection();
    }

    private void performFilterJobLogEntries(JobLogExplorerTab tabItem) throws SQLSyntaxErrorException {

        tabItem.validateWhereClause(getShell());

        tabItem.storeSqlEditorHistory();
        refreshSqlEditorHistory();

        tabItem.filterJobLogMessages();
    }

    private void refreshSqlEditorHistory() {
        for (CTabItem tabItem : tabFolder.getItems()) {
            ((JobLogExplorerTab)tabItem).refreshSqlEditorHistory();
        }
    }

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

    private Shell getShell() {
        return getSite().getShell();
    }

    private void updateStatusLine() {

        JobLogExplorerTab tabItem = getSelectedViewer();

        if (tabItem == null) {
            clearStatusLine();
            return;
        }

        String message;

        if (tabItem.getItemsCount() == tabItem.getTotalItemsCount()) {
            message = Messages.bind(Messages.Number_of_messages_A, tabItem.getTotalItemsCount());
        } else {
            message = Messages.bind(Messages.Number_of_messages_B_slash_A, tabItem.getTotalItemsCount(), tabItem.getItemsCount());
        }

        setStatusLineText(message);
        setActionEnablement(tabItem);
        fireSelectionChanged();
    }

    private void clearStatusLine() {

        setStatusLineText(""); //$NON-NLS-1$
        setActionEnablement(null);
    }

    private void setStatusLineText(String message) {

        IActionBars bars = getViewSite().getActionBars();
        bars.getStatusLineManager().setMessage(message);
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

    @Override
    public void setFocus() {
        updateStatusLine();
    }

    /**
     * Enables the actions for the current viewer,
     * 
     * @param viewer - the selected viewer (tab)
     */
    private void setActionEnablement(JobLogExplorerTab tabItem) {

        if (tabItem != null && tabItem.getItemsCount() > 0) {
            exportToExcelAction.setEnabled(true);
            exportToExcelAction.setTabTitle(tabItem.getText());
            exportToExcelAction.setSelectedItems(new StructuredSelection(tabItem.getItems()));
        } else {
            exportToExcelAction.setEnabled(false);
            exportToExcelAction.setTabTitle(null);
            exportToExcelAction.setSelectedItems(null);
        }

        if (tabItem != null && tabItem.getInput() != null) {
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

    public void statusChanged(JobLogExplorerStatusChangedEvent data) {

        if (!(data.getSource() instanceof JobLogExplorerTab)) {
            return;
        }

        JobLogExplorerTab selectedTabItem = getSelectedViewer();
        JobLogExplorerTab statusTabItem = (JobLogExplorerTab)data.getSource();

        if (selectedTabItem != null && !selectedTabItem.equals(statusTabItem)) {
            return;
        }

        updateStatusLine();

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
        updateStatusLine();
    }

    public void widgetDefaultSelected(SelectionEvent event) {
        widgetSelected(event);
    }

    private void fireSelectionChanged() {

        SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());

        Object[] listeners = selectionChangedListeners.getListeners();
        for (int i = 0; i < listeners.length; i++) {
            ISelectionChangedListener listener = (ISelectionChangedListener)listeners[i];
            listener.selectionChanged(event);
        }
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListeners.add(listener);
    }

    public ISelection getSelection() {
        if (getSelectedViewer() == null || getSelectedViewer().getJobLog() == null) {
            return new StructuredSelection(new Object[0]);
        }
        return new StructuredSelection(getSelectedViewer().getJobLog());
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListeners.remove(listener);
    }

    public void setSelection(ISelection selection) {
        return;
    }

    private class SqlEditorSelectionListener implements SelectionListener {

        public void widgetSelected(SelectionEvent event) {

            try {
                performFilterJobLogEntries(getSelectedViewer());
            } catch (SQLSyntaxErrorException e) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
                getSelectedViewer().setFocusOnSqlEditor();
            } catch (Exception e) {
                ISpherePlugin.logError("*** Error in method JobLogexplorerView.SqlEditorSelectionListener.widgetSelected() ***", e);
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            }
        }

        public void widgetDefaultSelected(SelectionEvent event) {
            widgetDefaultSelected(event);
        }
    }

}
