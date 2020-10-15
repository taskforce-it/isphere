/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.views;

import java.util.HashMap;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.IMenuManager;
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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.RowJEP;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.internal.actions.ResetColumnSizeAction;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.preferences.DoNotAskMeAgain;
import biz.isphere.core.preferences.DoNotAskMeAgainDialog;
import biz.isphere.core.swt.widgets.sqleditor.SQLSyntaxErrorException;
import biz.isphere.jobtraceexplorer.core.Messages;
import biz.isphere.jobtraceexplorer.core.exceptions.NoJobTraceEntriesLoadedException;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntries;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;
import biz.isphere.jobtraceexplorer.core.model.JobTraceSession;
import biz.isphere.jobtraceexplorer.core.ui.actions.EditSqlAction;
import biz.isphere.jobtraceexplorer.core.ui.actions.GenericRefreshAction;
import biz.isphere.jobtraceexplorer.core.ui.actions.LoadJobTraceEntriesAction;
import biz.isphere.jobtraceexplorer.core.ui.actions.OpenJobTraceAction;
import biz.isphere.jobtraceexplorer.core.ui.actions.SaveJobTraceEntriesAction;
import biz.isphere.jobtraceexplorer.core.ui.widgets.AbstractJobTraceEntriesViewerTab;
import biz.isphere.jobtraceexplorer.core.ui.widgets.JobTraceEntriesJsonViewerTab;
import biz.isphere.jobtraceexplorer.core.ui.widgets.JobTraceEntriesSQLViewerTab;

public class JobTraceExplorerView extends ViewPart implements IDataLoadPostRun, ISelectionChangedListener, SelectionListener, ISelectionProvider {

    public static final String ID = "biz.isphere.jobtraceexplorer.core.ui.views.JobTraceExplorerView"; //$NON-NLS-1$

    private EditSqlAction editSqlAction;
    private OpenJobTraceAction openJobTraceSession;
    private ResetColumnSizeAction resetColumnSizeAction;
    private GenericRefreshAction reloadEntriesAction;

    private LoadJobTraceEntriesAction loadJournalEntriesAction;
    private SaveJobTraceEntriesAction saveJournalEntriesAction;

    private CTabFolder tabFolder;
    private ListenerList selectionChangedListeners;

    public JobTraceExplorerView() {
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
                if (event.item instanceof AbstractJobTraceEntriesViewerTab) {
                    AbstractJobTraceEntriesViewerTab closedTab = (AbstractJobTraceEntriesViewerTab)event.item;
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
        initializeMenu();

        clearStatusLine();
    }

    private void disposeJobTraceExplorerTabChecked(Object object) {
        if (object instanceof AbstractJobTraceEntriesViewerTab) {
            AbstractJobTraceEntriesViewerTab tabItem = (AbstractJobTraceEntriesViewerTab)object;
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

        resetColumnSizeAction = new ResetColumnSizeAction();

        openJobTraceSession = new OpenJobTraceAction(getShell()) {
            @Override
            public void postRunAction() {
                JobTraceSession jobTraceSession = openJobTraceSession.getJobTraceSession();
                if (jobTraceSession != null) {
                    createJobTraceTab(jobTraceSession);
                }
            }
        };

        editSqlAction = new EditSqlAction(getShell()) {
            @Override
            public void postRunAction() {
                AbstractJobTraceEntriesViewerTab tabItem = getSelectedViewer();
                tabItem.setSqlEditorVisibility(editSqlAction.isChecked());
                return;
            }
        };

        reloadEntriesAction = new GenericRefreshAction() {
            @Override
            protected void postRunAction() {
                try {
                    performReloadJobTraceEntries(getSelectedViewer());
                } catch (Exception e) {
                    MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
                }
            }
        };

        loadJournalEntriesAction = new LoadJobTraceEntriesAction(getShell(), this);
        loadJournalEntriesAction.setEnabled(false);

        saveJournalEntriesAction = new SaveJobTraceEntriesAction(getShell());
        saveJournalEntriesAction.setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_SAVE));
        saveJournalEntriesAction.setEnabled(false);

    }

    /**
     * Create view menu.
     */
    private void initializeMenu() {

        IActionBars actionBars = getViewSite().getActionBars();
        IMenuManager viewMenu = actionBars.getMenuManager();

        viewMenu.add(loadJournalEntriesAction);
        viewMenu.add(new Separator());
        viewMenu.add(saveJournalEntriesAction);
    }

    public void createJobTraceTab(JobTraceSession jobTraceSession) {

        if (jobTraceSession == null) {
            throw new IllegalArgumentException("Parameter 'jobTraceSession' must not be [null]."); //$NON-NLS-1$
        }

        AbstractJobTraceEntriesViewerTab jobTraceEntriesViewer = null;

        try {

            jobTraceEntriesViewer = findExplorerTab(jobTraceSession);
            if (jobTraceEntriesViewer == null) {
                jobTraceEntriesViewer = new JobTraceEntriesSQLViewerTab(tabFolder, jobTraceSession, new SqlEditorSelectionListener());
                jobTraceEntriesViewer.addSelectionChangedListener(this);
                tabFolder.setSelection(jobTraceEntriesViewer);
                performLoadJobTraceEntries(jobTraceEntriesViewer);
            } else {
                jobTraceEntriesViewer.getJobTraceSession().setExcludeIBMData(jobTraceSession.isIBMDataExcluded());
                performReloadJobTraceEntries(jobTraceEntriesViewer);
            }

        } catch (Throwable e) {
            handleDataLoadException(jobTraceEntriesViewer, e);
        }
    }

    public void createJobTraceTab(String fileName) {

        if (StringHelper.isNullOrEmpty(fileName)) {
            throw new IllegalArgumentException("Parameter 'fileName' must not be [null]."); //$NON-NLS-1$
        }

        AbstractJobTraceEntriesViewerTab jobTraceEntriesViewer = null;

        try {

            JobTraceSession jobTraceSession = new JobTraceSession(fileName);

            jobTraceEntriesViewer = findExplorerTab(jobTraceSession);
            if (jobTraceEntriesViewer == null) {
                jobTraceEntriesViewer = new JobTraceEntriesJsonViewerTab(tabFolder, jobTraceSession, new SqlEditorSelectionListener());
                jobTraceEntriesViewer.addSelectionChangedListener(this);
                tabFolder.setSelection(jobTraceEntriesViewer);
                performLoadJobTraceEntries(jobTraceEntriesViewer);
            } else {
                tabFolder.setSelection(jobTraceEntriesViewer);
                performReloadJobTraceEntries(jobTraceEntriesViewer);
            }

        } catch (Throwable e) {
            handleDataLoadException(jobTraceEntriesViewer, e);
        }
    }

    public void handleDataLoadException(AbstractJobTraceEntriesViewerTab tabItem, Throwable e) {

        if (e instanceof ParseException) {
            MessageDialog.openInformation(getShell(), Messages.MessageDialog_Load_Job_Trace_Entries_Title, e.getLocalizedMessage());
            return;
        } else if (e instanceof NoJobTraceEntriesLoadedException) {
            MessageDialog.openInformation(getShell(), Messages.MessageDialog_Load_Job_Trace_Entries_Title, e.getLocalizedMessage());
            return;
        } else if (e instanceof SQLSyntaxErrorException) {
            MessageDialog.openInformation(getShell(), Messages.MessageDialog_Load_Job_Trace_Entries_Title, e.getLocalizedMessage());
        } else {
            ISpherePlugin.logError("*** Error in method JobTraceExplorerView.handleDataLoadException() ***", e); //$NON-NLS-1$
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }

        disposeJobTraceExplorerTabChecked(tabItem);

        updateStatusLine();
    }

    private AbstractJobTraceEntriesViewerTab findExplorerTab(JobTraceSession input) {

        CTabItem[] tabItems = tabFolder.getItems();
        for (CTabItem tabItem : tabItems) {
            AbstractJobTraceEntriesViewerTab jobTraceEntriesViewerTab = (AbstractJobTraceEntriesViewerTab)tabItem;
            if (jobTraceEntriesViewerTab.isSameSession(input)) {
                return jobTraceEntriesViewerTab;
            }
        }

        return null;
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
    public AbstractJobTraceEntriesViewerTab getSelectedViewer() {
        CTabFolder tabFolder = getTabFolder();
        if (tabFolder == null) {
            return null;
        }
        return (AbstractJobTraceEntriesViewerTab)tabFolder.getSelection();
    }

    private void performReloadJobTraceEntries(AbstractJobTraceEntriesViewerTab tabItem) throws Exception {

        String filterWhereClause = tabItem.getFilterWhereClause();
        validateWhereClause(filterWhereClause);

        tabItem.reloadJobTraceSession(this);
    }

    private void performLoadJobTraceEntries(AbstractJobTraceEntriesViewerTab tabItem) throws Exception {

        String filterWhereClause = tabItem.getFilterWhereClause();
        validateWhereClause(filterWhereClause);

        tabItem.openJobTraceSession(this);
    }

    private void performFilterJobTraceEntries(AbstractJobTraceEntriesViewerTab tabItem) throws Exception {

        String filterWhereClause = tabItem.getFilterWhereClause();
        validateWhereClause(filterWhereClause);

        tabItem.storeSqlEditorHistory();
        refreshSqlEditorHistory();

        tabItem.filterJobTraceSession(this);
    }

    private void refreshSqlEditorHistory() {
        for (CTabItem tabItem : tabFolder.getItems()) {
            ((AbstractJobTraceEntriesViewerTab)tabItem).refreshSqlEditorHistory();
        }
    }

    public void finishDataLoading(AbstractJobTraceEntriesViewerTab tabItem, boolean isFilter) {

        if (tabItem != null && !tabItem.isDisposed()) {
            JobTraceEntries jobTraceEntries = tabItem.getInput();
            if (jobTraceEntries != null) {
                if (jobTraceEntries.isCanceled()) {
                    MessageDialog.openWarning(getShell(), Messages.MessageDialog_Load_Job_Trace_Entries_Title,
                        Messages.Warning_Loading_job_trace_entries_has_been_canceled_by_the_user);
                } else {
                    int numItemsDownloaded = jobTraceEntries.getNumberOfRowsDownloaded();
                    if (jobTraceEntries.isOverflow() && !isFilter) {
                        String messageText;
                        int numItemsAvailable = jobTraceEntries.getNumberOfRowsAvailable();
                        if (numItemsAvailable < 0) {
                            messageText = Messages.bind(Messages.Warning_Not_all_job_trace_entries_loaded_unknown_size, numItemsAvailable,
                                numItemsDownloaded);
                        } else {
                            messageText = Messages.bind(Messages.Warning_Not_all_job_trace_entries_loaded, numItemsAvailable, numItemsDownloaded);
                        }
                        DoNotAskMeAgainDialog.openInformation(getViewSite().getShell(), DoNotAskMeAgain.WARNING_NOT_ALL_JOB_TRACE_ENTRIES_LOADED,
                            messageText);
                    }
                }
            }
        }
    }

    private void validateWhereClause(String whereClause) throws SQLSyntaxErrorException {

        if (StringHelper.isNullOrEmpty(whereClause)) {
            return;
        }

        try {

            HashMap<String, Integer> columnMapping = JobTraceEntry.getColumnMapping();
            RowJEP sqljep = new RowJEP(whereClause);
            sqljep.parseExpression(columnMapping);
            sqljep.getValue(JobTraceEntry.getSampleRow());

        } catch (ParseException e) {
            throw new SQLSyntaxErrorException(e);
        }
    }

    public Shell getShell() {
        return getSite().getShell();
    }

    private void updateStatusLine() {

        AbstractJobTraceEntriesViewerTab tabItem = getSelectedViewer();

        if (tabItem == null || tabItem.isLoading()) {
            clearStatusLine();
            return;
        }

        JobTraceEntries jobTraceEntries = tabItem.getInput();
        if (jobTraceEntries == null) {
            clearStatusLine();
            return;
        }

        String message = null;

        int numItems = jobTraceEntries.size();
        if (jobTraceEntries.isOverflow()) {
            int numItemsAvailable = jobTraceEntries.getNumberOfRowsAvailable();
            if (numItemsAvailable < 0) {
                message = Messages.bind(Messages.Number_of_job_trace_entries_A_more_items_available, numItems);
            } else {
                message = Messages.bind(Messages.Number_of_job_trace_entries_A_of_B, numItems, numItemsAvailable);
            }
        } else {
            message = Messages.bind(Messages.Number_of_job_trace_entries_A, numItems);
        }

        if (tabItem.isFiltered()) {
            message += " (" + Messages.subsetted_list + ")";
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
        toolBarManager.add(openJobTraceSession);
        toolBarManager.add(editSqlAction);
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
     * Enables the actions for the current viewer.
     * 
     * @param tabItem - the selected viewer (tab)
     */
    private void setActionEnablement(AbstractJobTraceEntriesViewerTab tabItem) {

        if (tabItem == null || tabItem.getInput() == null) {
            editSqlAction.setEnabled(false);
            editSqlAction.setChecked(false);
        } else {
            editSqlAction.setEnabled(tabItem.hasSqlEditor());
            editSqlAction.setChecked(tabItem.isSqlEditorVisible());
        }

        openJobTraceSession.setEnabled(true);

        int numEntries = 0;
        JobTraceEntries jobTraceEntries = null;

        if (tabItem != null) {

            jobTraceEntries = tabItem.getInput();
            if (jobTraceEntries != null) {
                numEntries = jobTraceEntries.size();
            }
        }

        if (tabItem == null || tabItem.isLoading()) {
            reloadEntriesAction.setEnabled(false);
        } else {
            reloadEntriesAction.setEnabled(true);
        }

        if (numEntries == 0) {
            resetColumnSizeAction.setEnabled(false);
            resetColumnSizeAction.setViewer(null);
            loadJournalEntriesAction.setEnabled(true);
            saveJournalEntriesAction.setEnabled(false);
            saveJournalEntriesAction.setSelectedItems(null);
        } else {
            resetColumnSizeAction.setEnabled(true);
            resetColumnSizeAction.setViewer(getSelectedViewer());
            loadJournalEntriesAction.setEnabled(true);
            saveJournalEntriesAction.setEnabled(true);
            saveJournalEntriesAction.setSelectedItems(tabItem.getJobTraceSession());
        }

    }

    /**
     * Called by the viewer, when setSelection() is called.
     */
    public void selectionChanged(SelectionChangedEvent event) {
        updateStatusLine();
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

    /**
     * ISelectionProvider interface.
     */
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
        if (getSelectedViewer() == null || getSelectedViewer().getJobTraceSession() == null) {
            return new StructuredSelection(new Object[0]);
        }
        return new StructuredSelection(getSelectedViewer().getJobTraceSession());
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
                performFilterJobTraceEntries(getSelectedViewer());
            } catch (SQLSyntaxErrorException e) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
                getSelectedViewer().setFocusOnSqlEditor();
            } catch (Exception e) {
                ISpherePlugin.logError("*** Error in method JobTraceExplorerView.SqlEditorSelectionListener.widgetSelected() ***", e);
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            }
        }

        public void widgetDefaultSelected(SelectionEvent event) {
            widgetDefaultSelected(event);
        }
    }
}
