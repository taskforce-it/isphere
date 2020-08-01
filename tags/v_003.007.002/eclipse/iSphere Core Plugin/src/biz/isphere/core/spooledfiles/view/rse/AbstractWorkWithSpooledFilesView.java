/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view.rse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.internal.actions.ResetColumnSizeAction;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.internal.viewmanager.IPinableView;
import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.core.internal.viewmanager.PinViewAction;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.WorkWithSpooledFilesHelper;
import biz.isphere.core.spooledfiles.view.IAutoRefreshView;
import biz.isphere.core.spooledfiles.view.ILoadSpooledFilesPostRun;
import biz.isphere.core.spooledfiles.view.IWaitForRseConnectionPostRun;
import biz.isphere.core.spooledfiles.view.LoadSpooledFilesJob;
import biz.isphere.core.spooledfiles.view.WaitForRseConnectionJob;
import biz.isphere.core.spooledfiles.view.WorkWithSpooledFilesPanel;
import biz.isphere.core.spooledfiles.view.actions.AutoRefreshRefreshIntervalAction;
import biz.isphere.core.spooledfiles.view.actions.DisableAutoRefreshViewAction;
import biz.isphere.core.spooledfiles.view.actions.RefreshViewAction;
import biz.isphere.core.spooledfiles.view.actions.RemoveAction;
import biz.isphere.core.spooledfiles.view.actions.RemoveAllAction;
import biz.isphere.core.spooledfiles.view.events.ITableItemChangeListener;
import biz.isphere.core.spooledfiles.view.events.TableItemChangedEvent;
import biz.isphere.core.spooledfiles.view.events.TableItemChangedEvent.EventType;
import biz.isphere.core.spooledfiles.view.jobs.AutoRefreshJob;
import biz.isphere.core.spooledfiles.view.listeners.AutoRefreshViewCloseListener;

public abstract class AbstractWorkWithSpooledFilesView extends ViewPart implements IPinableView, IWaitForRseConnectionPostRun,
    ILoadSpooledFilesPostRun, ITableItemChangeListener, IAutoRefreshView, ISelectionChangedListener {

    public static final String ID = "biz.isphere.rse.spooledfiles.view.WorkWithSpooledFilesView"; //$NON-NLS-1$

    /*
     * View pin properties
     */
    private static final String CONNECTION_NAME = "connectionName"; //$NON-NLS-1$
    private static final String FILTER_POOL_NAME = "filterPoolName"; //$NON-NLS-1$
    private static final String FILTER_NAME = "filterName"; //$NON-NLS-1$
    private static final String FILTER_STRING = "filterString"; //$NON-NLS-1$
    private static final String FILTER_STRING_DELIMITER = "\\|"; //$NON-NLS-1$

    private Composite mainArea;
    private Label labelHeadline;
    private WorkWithSpooledFilesPanel workWithSpooledFilesPanel;

    private AbstractWorkWithSpooledFilesInputData inputData;
    private LoadSpooledFilesJob loadSpooledFilesJob;

    private RefreshViewAction refreshViewAction;
    private DisableAutoRefreshViewAction disableAutoRefreshViewAction;
    private PinViewAction pinViewAction;
    private ResetColumnSizeAction resetColumnSizeAction;
    private RemoveAction deleteSelectedSpooledFilesAction;
    private RemoveAllAction deleteAllSpooledFilesAction;

    private AutoRefreshSubMenu autoRefreshSubMenu;
    private AutoRefreshJob autoRefreshJob;

    private WorkWithSpooledFilesHelper workWithSpooledFilesHelper;

    private Map<String, String> pinProperties;

    public AbstractWorkWithSpooledFilesView() {

        this.workWithSpooledFilesHelper = new WorkWithSpooledFilesHelper(null, null);
        this.workWithSpooledFilesHelper.addChangedListener(this);
        this.pinProperties = new HashMap<String, String>();

        getViewManager().add(this);
    }

    @Override
    public void dispose() {

        getViewManager().remove(this);

        workWithSpooledFilesPanel.removeSelectionChangedListener(this);
        workWithSpooledFilesHelper.removeChangedListener(this);

        super.dispose();
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);

        setPartName(Messages.Spooled_Files_View);
    }

    @Override
    public void createPartControl(Composite parent) {

        parent.addDisposeListener(new AutoRefreshViewCloseListener(this));

        mainArea = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 1;
        mainArea.setLayout(layout);
        mainArea.setLayoutData(new GridData(GridData.FILL_BOTH));

        labelHeadline = new Label(mainArea, SWT.NONE);
        labelHeadline.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        workWithSpooledFilesPanel = new WorkWithSpooledFilesPanel(mainArea, SWT.NONE);
        workWithSpooledFilesPanel.setChangedListener(this);
        workWithSpooledFilesPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
        workWithSpooledFilesPanel.addSelectionChangedListener(this);

        getSite().setSelectionProvider(workWithSpooledFilesPanel);

        createActions();
        initializeToolBar();
        initializeViewMenu();

        refreshActionsEnablement();

        if (getViewManager().isPinned(this)) {
            if (!getViewManager().isLoadingView()) {
                restoreData();
            }
        }
    }

    private void updatePinProperties() {

        if (inputData == null) {
            return;
        }

        pinProperties.put(CONNECTION_NAME, inputData.getConnectionName());
        pinProperties.put(FILTER_POOL_NAME, inputData.getFilterPoolName());
        pinProperties.put(FILTER_NAME, inputData.getFilterName());

        String[] filterStrings = inputData.getFilterStrings();
        if (filterStrings == null || filterStrings.length == 0) {
            pinProperties.put(FILTER_STRING, null);
        } else {
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < filterStrings.length; i++) {
                if (buffer.length() > 0) {
                    buffer.append(FILTER_STRING_DELIMITER);
                }
                buffer.append(filterStrings[i]);
            }
            pinProperties.put(FILTER_STRING, buffer.toString());
        }
    }

    private void createActions() {

        refreshViewAction = new RefreshViewAction(this);
        refreshViewAction.setToolTipText(Messages.Refresh_the_contents_of_this_view);
        refreshViewAction.setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_REFRESH));

        disableAutoRefreshViewAction = new DisableAutoRefreshViewAction(this);

        pinViewAction = new PinViewAction(this);
        pinViewAction.setChecked(getViewManager().isPinned(this));

        resetColumnSizeAction = new ResetColumnSizeAction(workWithSpooledFilesPanel);

        deleteSelectedSpooledFilesAction = new RemoveAction() {
            public void run() {
                workWithSpooledFilesHelper.performDeleteSpooledFile(workWithSpooledFilesPanel.getSelectedItems());
            }
        };

        deleteAllSpooledFilesAction = new RemoveAllAction() {
            public void run() {
                workWithSpooledFilesHelper.performDeleteSpooledFile(workWithSpooledFilesPanel.getItems());
            }
        };
    }

    private void initializeToolBar() {

        IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
        toolbarManager.add(deleteSelectedSpooledFilesAction);
        toolbarManager.add(deleteAllSpooledFilesAction);
        toolbarManager.add(new Separator());
        toolbarManager.add(resetColumnSizeAction);
        toolbarManager.add(new Separator());
        toolbarManager.add(pinViewAction);
        toolbarManager.add(disableAutoRefreshViewAction);
        toolbarManager.add(refreshViewAction);
    }

    private void initializeViewMenu() {

        IActionBars actionBars = getViewSite().getActionBars();
        IMenuManager viewMenu = actionBars.getMenuManager();

        viewMenu.add(createAuoRefreshSubMenu());
    }

    private MenuManager createAuoRefreshSubMenu() {

        this.autoRefreshSubMenu = new AutoRefreshSubMenu(this, 10, 30, 60, 300, 900);

        return this.autoRefreshSubMenu;
    }

    /**
     * Restore the view data without blocking the UI. Waits until the RSE
     * connection is available.<br>
     * Must be called from the UI thread to get the 'Shell' for displaying error
     * messages.
     * <p>
     * Must unblock UI thread, otherwise the RSE sub-system cannot be
     * initialized. The RSE sub-system is needed for
     * ISphereHelper.checkISphereLibrary() in method
     * setInputData(WorkWithSpooledFilesInputData inputData).
     */
    private void restoreData() {

        Set<String> keySet = new HashSet<String>();
        keySet.add(CONNECTION_NAME);
        keySet.add(FILTER_POOL_NAME);
        keySet.add(FILTER_NAME);
        keySet.add(FILTER_STRING);

        pinProperties = getViewManager().getPinProperties(AbstractWorkWithSpooledFilesView.this, keySet);
        // workWithSpooledFilesPanel.restoreData(pinProperties);

        String connectionName = pinProperties.get(CONNECTION_NAME);
        if (StringHelper.isNullOrEmpty(connectionName)) {
            setPinned(false);
            return;
        }

        String filterName = pinProperties.get(FILTER_NAME);
        if (StringHelper.isNullOrEmpty(filterName)) {
            setPinned(false);
            return;
        }

        setSubTitle(connectionName, filterName);

        new WaitForRseConnectionJob(getShell(), connectionName, this).schedule();
    }

    /**
     * Called when the RSE connection is available. This method does the actual
     * job of restoring a pinned view.
     */
    public void setWaitForRseConnectionPostRunData(Shell shell, String connectionName, boolean isAvailable) {

        if (!isAvailable) {
            if (isPinned()) {
                setPinned(false);
            }
            MessageDialogAsync.displayError(shell, Messages.bind(Messages.Could_not_get_RSE_connection_A, connectionName));
            return;
        }

        String message = ISphereHelper.checkISphereLibrary(connectionName);
        if (message != null) {
            MessageDialogAsync.displayError(shell, message);
            return;
        }

        String filterName = pinProperties.get(FILTER_NAME);
        String filterPoolName = pinProperties.get(FILTER_POOL_NAME);

        final AbstractWorkWithSpooledFilesInputData inputData = produceInputData(connectionName, filterPoolName, filterName);

        if (inputData == null) {
            setPinned(false);
        } else {
            setInputDataInternally(shell, inputData);
        }
    }

    /**
     * Sets the title of the view.
     */
    public void setSubTitle(AbstractWorkWithSpooledFilesInputData inputData) {

        if (inputData == null) {
            labelHeadline.setText(Messages.EMPTY);
            return;
        }

        setSubTitle(inputData.getConnectionName(), inputData.getFilterName());
    }

    /**
     * Sets the title of the view.
     */
    public void setSubTitle(String connectionName, String filterName) {

        if (connectionName == null || filterName == null) {
            labelHeadline.setText(Messages.EMPTY);
            return;
        }

        labelHeadline.setText(connectionName + ":" + filterName); //$NON-NLS-1$
    }

    /**
     * Refreshs the view title and updates the pin properties.<br>
     * Is called when a system resource (connection or filter) has been renamed.
     */
    public void refreshTitle() {

        setSubTitle(inputData);

        if (isPinned()) {
            updatePinProperties();
        }
    }

    /**
     * Reloads the spooled files when refreshing the view data. <br>
     * Must be called from the UI thread to get the 'Shell' for displaying error
     * messages.
     */
    public void refreshData() {

        if (!ISphereHelper.checkISphereLibrary(getShell(), inputData.getConnectionName())) {
            return;
        }

        setInputDataInternally(getShell(), this.inputData);
    }

    /**
     * Returns the input data of the view.
     */
    protected AbstractWorkWithSpooledFilesInputData getInputData() {
        return inputData;
    }

    /**
     * Sets the input data for the view. <br>
     * Must be called from the UI thread to get the 'Shell' for displaying error
     * messages.
     * 
     * @param inputData - WorkWithSpooledFilesInputData
     */
    public void setInputData(AbstractWorkWithSpooledFilesInputData inputData) {

        if (!ISphereHelper.checkISphereLibrary(getShell(), inputData.getConnectionName())) {
            return;
        }

        /*
         * Clear input data to disable selecting new spooled files in
         * "WorkWithSpooledFilesPanel".
         */
        workWithSpooledFilesPanel.setInput(null, null);

        setInputDataInternally(getShell(), inputData);
    }

    /**
     * Sets the input data for the view.
     * 
     * @param shell - Shell for displaying messages
     * @param inputData - WorkWithSpooledFilesInputData
     */
    private void setInputDataInternally(Shell shell, AbstractWorkWithSpooledFilesInputData inputData) {

        if (!inputData.isValid()) {
            setPinned(false);
            updateStatusLine();
            return;
        }

        this.inputData = inputData;

        if (loadSpooledFilesJob != null) {
            updateStatusLine();
            return;
        }

        loadSpooledFilesJob = new LoadSpooledFilesJob(inputData.getConnectionName(), inputData.getFilterName(), inputData.getFilterStrings(), this);
        loadSpooledFilesJob.schedule();
    }

    /**
     * Called, when the spooled files have been loaded. This method starts a UI
     * job for loading the spooled files into the viewer.
     */
    public void setLoadSpooledFilesPostRunData(final String connectionName, final String filterName, final SpooledFile[] spooledFiles) {

        new UIJob(Messages.EMPTY) {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {

                setInputDataChecked();
                setSubTitleChecked();

                if (isPinned()) {
                    updatePinProperties();
                }

                updateStatusChecked();

                loadSpooledFilesJob = null;

                return Status.OK_STATUS;
            }

            private void setSubTitleChecked() {

                if (isDisposed(labelHeadline)) {
                    return;
                }

                setSubTitle(inputData);
            }

            private void setInputDataChecked() {

                if (isDisposed(workWithSpooledFilesPanel)) {
                    return;
                }

                // if (spooledFiles.length == 0) {
                // setPinned(false);
                // }

                workWithSpooledFilesPanel.setInput(connectionName, spooledFiles);
                workWithSpooledFilesHelper.setShell(getShell());
                workWithSpooledFilesHelper.setConnection(connectionName);
                refreshActionsEnablement();
            }

            private void updateStatusChecked() {

                if (isDisposed(workWithSpooledFilesPanel)) {
                    return;
                }

                updateStatusLine();
            }

            private boolean isDisposed(Widget widget) {
                return widget.isDisposed();
            }

        }.schedule();

    }

    @Override
    public void setFocus() {
        workWithSpooledFilesPanel.setFocus();
        updateStatusLine();
    }

    /*
     * ISelectionChangedListener methods
     */

    public void selectionChanged(SelectionChangedEvent event) {
        refreshActionsEnablement();
        updateStatusLine();
    }

    /*
     * ITableItemChangedListener methods
     */

    public void itemChanged(TableItemChangedEvent event) {

        /*
         * For events HOLD, RELEASED and MESSAGE, the spooled file refreshes
         * itself automatically. For all other events we need to refresh the
         * spooled file here.
         */

        if (event.isEvent(EventType.DELETED)) {
            getWorkWithSpooledFilesPanel().remove(event.getSpooledFiles());
        } else {
            event.getSpooledFile().refresh();
            getWorkWithSpooledFilesPanel().update(event.getSpooledFile());
        }

        refreshActionsEnablement();
    }

    /*
     * IPinableView methods
     */

    public boolean isPinned() {
        return pinViewAction.isChecked();
    }

    public void setPinned(boolean pinned) {

        pinViewAction.setChecked(pinned);

        if (pinned) {
            updatePinProperties();
        }
    }

    public String getContentId() {

        if (inputData == null) {
            return null;
        }

        return inputData.getContentId();
    }

    public Map<String, String> getPinProperties() {

        Map<String, String> panelPinProperties = workWithSpooledFilesPanel.getPinProperties();
        if (panelPinProperties != null) {
            pinProperties.putAll(panelPinProperties);
        }

        return pinProperties;
    }

    /*
     * IAutoRefreshView methods
     */

    public Shell getShell() {
        return getSite().getShell();
    }

    public void setRefreshInterval(int seconds) {

        try {

            if (!hasInputData()) {
                seconds = AutoRefreshRefreshIntervalAction.REFRESH_OFF;
            }

            if (autoRefreshJob != null) {
                if (seconds == AutoRefreshRefreshIntervalAction.REFRESH_OFF) {
                    autoRefreshJob.cancel();
                } else {
                    autoRefreshJob.setInterval(seconds);
                }
            } else {
                if (seconds != AutoRefreshRefreshIntervalAction.REFRESH_OFF) {
                    autoRefreshJob = new AutoRefreshJob(this, seconds);
                    autoRefreshJob.schedule();
                }
            }

        } finally {
            refreshActionsEnablement();
        }
    }

    public void jobFinished(Job job) {
        if (job == autoRefreshJob) {
            autoRefreshJob = null;
            refreshActionsEnablement();
        }
    }

    /*
     * Private and protected methods
     */

    protected WorkWithSpooledFilesPanel getWorkWithSpooledFilesPanel() {
        return workWithSpooledFilesPanel;
    }

    private void refreshActionsEnablement() {

        if (!hasInputData() || isAutoRefreshOn()) {
            refreshViewAction.setEnabled(false);
        } else {
            refreshViewAction.setEnabled(true);
        }

        if (hasInputData() || isPinned()) {
            pinViewAction.setEnabled(true);
        } else {
            pinViewAction.setEnabled(false);
        }

        resetColumnSizeAction.setEnabled(true);

        if (isAutoRefreshOn()) {
            autoRefreshSubMenu.setEnabled(autoRefreshJob.getInterval());
            disableAutoRefreshViewAction.setEnabled(true);
        } else {
            if (hasInputData()) {
                autoRefreshSubMenu.setEnabled(AutoRefreshRefreshIntervalAction.REFRESH_OFF);
            } else {
                autoRefreshSubMenu.setEnabled(false);
            }
            disableAutoRefreshViewAction.setEnabled(false);
        }

        if (workWithSpooledFilesPanel.getSelection().isEmpty()) {
            deleteSelectedSpooledFilesAction.setEnabled(false);
        } else {
            deleteSelectedSpooledFilesAction.setEnabled(true);
        }

        if (workWithSpooledFilesPanel.getItemCount() == 0) {
            deleteAllSpooledFilesAction.setEnabled(false);
        } else {
            deleteAllSpooledFilesAction.setEnabled(true);
        }
    }

    private boolean isAutoRefreshOn() {

        if (autoRefreshJob != null) {
            return true;
        }

        return false;
    }

    private boolean hasInputData() {
        return inputData != null;
    }

    private void updateStatusLine() {

        int countTotal = workWithSpooledFilesPanel.getItemCount();
        int countSelected = workWithSpooledFilesPanel.getSelectionCount();

        if (countTotal == 0) {
            setStatusLineText(Messages.No_data_available);
        } else if (countSelected == 0) {
            setStatusLineText(Messages.bind(Messages.Spooled_files_A, countTotal));
        } else if (countSelected == 1) {
            SpooledFile spooledFile = workWithSpooledFilesPanel.getSelectedItems()[0];
            setStatusLineText(Messages.bind(Messages.Spooled_file_A_B, spooledFile.getFile(), spooledFile.getStatus()));
        } else {
            setStatusLineText(Messages.bind(Messages.Spooled_files_A_of_B, countSelected, countTotal));
        }
    }

    private void setStatusLineText(String message) {

        IActionBars bars = getViewSite().getActionBars();
        bars.getStatusLineManager().setMessage(message);
    }

    protected abstract IViewManager getViewManager();

    protected abstract AbstractWorkWithSpooledFilesInputData produceInputData(String connectionName, String filterPoolName, String filterName);
}
