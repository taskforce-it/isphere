/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view.rse;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
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
import biz.isphere.core.internal.viewmanager.IPinnableView;
import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.core.internal.viewmanager.PinViewAction;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.view.ILoadSpooledFilesPostRun;
import biz.isphere.core.spooledfiles.view.IWaitForRseConnectionPostRun;
import biz.isphere.core.spooledfiles.view.LoadSpooledFilesJob;
import biz.isphere.core.spooledfiles.view.WaitForRseConnectionJob;
import biz.isphere.core.spooledfiles.view.WorkWithSpooledFilesMenuAdapter;
import biz.isphere.core.spooledfiles.view.WorkWithSpooledFilesPanel;
import biz.isphere.core.spooledfiles.view.actions.RefreshViewAction;
import biz.isphere.core.spooledfiles.view.events.ITableItemChangeListener;
import biz.isphere.core.spooledfiles.view.events.TableItemChangedEvent;
import biz.isphere.core.spooledfiles.view.events.TableItemChangedEvent.EventType;

public abstract class AbstractWorkWithSpooledFilesView extends ViewPart implements IPinnableView, IWaitForRseConnectionPostRun,
    ILoadSpooledFilesPostRun, ITableItemChangeListener {

    public static final String ID = "biz.isphere.rse.spooledfiles.view.WorkWithSpooledFilesView"; //$NON-NLS-1$ 

    /*
     * View pin properties
     */
    private static final String CONNECTION_NAME = "connectionName"; //$NON-NLS-1$
    private static final String FILTER_NAME = "filterName"; //$NON-NLS-1$
    private static final String FILTER_STRING = "filterString_"; //$NON-NLS-1$
    private static final String FILTER_STRING_DELIMITER = "\\|"; //$NON-NLS-1$

    private Composite mainArea;
    private Label labelHeadline;
    private WorkWithSpooledFilesPanel workWithSpooledFilesPanel;

    private WorkWithSpooledFilesInputData inputData;
    private LoadSpooledFilesJob loadSpooledFilesJob;

    private RefreshViewAction refreshViewAction;
    private PinViewAction pinViewAction;
    private ResetColumnSizeAction resetColumnSizeAction;

    private Map<String, String> pinProperties;

    public static String produceContentId(String connectionName, String filterName) {
        String contentId = connectionName + ":" + filterName; //$NON-NLS-1$
        return contentId;
    }

    public AbstractWorkWithSpooledFilesView() {
        getViewManager().add(this);
    }

    @Override
    public void dispose() {

        getViewManager().remove(this);

        super.dispose();
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);

        setPartName(Messages.Spooled_Files_View);
        initializePinProperties();
    }

    @Override
    public void createPartControl(Composite parent) {

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
        workWithSpooledFilesPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
        workWithSpooledFilesPanel.setPinProperties(pinProperties);

        createActions();
        initializeToolBar();

        if (getViewManager().isPinned(this)) {
            if (!getViewManager().isLoadingView()) {
                restoreData();
            }
        }
    }

    private void initializePinProperties() {

        pinProperties = new HashMap<String, String>();
        pinProperties.put(CONNECTION_NAME, null);
        pinProperties.put(FILTER_NAME, null);
        pinProperties.put(FILTER_STRING, null);
    }

    private void updatePinProperties() {

        if (inputData == null) {
            return;
        }

        pinProperties.put(CONNECTION_NAME, inputData.getConnectionName());
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

        pinViewAction = new PinViewAction(this);
        pinViewAction.setChecked(getViewManager().isPinned(this));

        resetColumnSizeAction = new ResetColumnSizeAction(workWithSpooledFilesPanel);

        refreshActionsEnablement();
    }

    private void initializeToolBar() {

        IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
        toolbarManager.add(resetColumnSizeAction);
        toolbarManager.add(new Separator());
        toolbarManager.add(pinViewAction);
        toolbarManager.add(refreshViewAction);
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

        pinProperties = getViewManager().getPinProperties(AbstractWorkWithSpooledFilesView.this, pinProperties.keySet());
        workWithSpooledFilesPanel.restorePinProperties(pinProperties);

        String connectionName = pinProperties.get(CONNECTION_NAME);
        if (StringHelper.isNullOrEmpty(connectionName)) {
            setPinned(false);
            return;
        }

        new WaitForRseConnectionJob(getShell(), connectionName, this).schedule();
    }

    public void setWaitForRseConnectionPostRunData(Shell shell, String connectionName, boolean isAvailable) {

        if (!isAvailable) {
            MessageDialogAsync.displayError(shell, Messages.bind(Messages.Could_not_get_RSE_connection_A, connectionName));
            return;
        }

        String message = ISphereHelper.checkISphereLibrary(connectionName);
        if (message != null) {
            MessageDialogAsync.displayError(shell, message);
            return;
        }

        String filterName = pinProperties.get(FILTER_NAME);
        String filterString = pinProperties.get(FILTER_STRING);
        String[] filterStrings;
        if (!StringHelper.isNullOrEmpty(filterString)) {
            filterStrings = filterString.split(FILTER_STRING_DELIMITER);
        } else {
            filterStrings = new String[0];
        }

        final WorkWithSpooledFilesInputData inputData = new WorkWithSpooledFilesInputData(connectionName, filterName);
        inputData.setFilterStrings(filterStrings);

        setInputDataInternally(shell, inputData, -1);
    }

    /**
     * Reloads the spooled files when refreshing the view data. <br>
     * Must be called from the UI thread to get the 'Shell' for displaying error
     * messages.
     */
    public void refreshData() {
        // refreshData(getWorkWithSpooledFilesPanel().getViewer().getTable().getSelectionIndex());
        refreshData(-1);
    }

    public void refreshData(int itemIndex) {

        if (!ISphereHelper.checkISphereLibrary(getShell(), inputData.getConnectionName())) {
            return;
        }

        setInputDataInternally(getShell(), this.inputData, itemIndex);
    }

    /**
     * Sets the input data for the view. <br>
     * Must be called from the UI thread to get the 'Shell' for displaying error
     * messages.
     * 
     * @param inputData - WorkWithSpooledFilesInputData
     */
    public void setInputData(WorkWithSpooledFilesInputData inputData) {

        if (!ISphereHelper.checkISphereLibrary(getShell(), inputData.getConnectionName())) {
            return;
        }

        setInputDataInternally(getShell(), inputData, -1);
    }

    /**
     * Sets the input data for the view.
     * 
     * @param shell - Shell for displaying messages
     * @param inputData - WorkWithSpooledFilesInputData
     */
    private void setInputDataInternally(Shell shell, WorkWithSpooledFilesInputData inputData, int itemIndex) {

        if (loadSpooledFilesJob != null || !inputData.isValid()) {
            setPinned(false);
            return;
        }

        this.inputData = inputData;

        loadSpooledFilesJob = new LoadSpooledFilesJob(inputData.getConnectionName(), inputData.getFilterName(), inputData.getFilterStrings(), this);
        loadSpooledFilesJob.setItemIndex(itemIndex);
        loadSpooledFilesJob.schedule();
    }

    public void setLoadSpooledFilesPostRunData(final String connectionName, final String filterName, final SpooledFile[] spooledFiles,
        final int itemIndex) {

        new UIJob(Messages.EMPTY) {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {

                setHeadline();
                setInputData();
                setPosition();
                setMenu();

                if (isPinned()) {
                    updatePinProperties();
                } else {
                    initializePinProperties();
                }

                loadSpooledFilesJob = null;

                return Status.OK_STATUS;
            }

            private void setHeadline() {
                labelHeadline.setText(connectionName + ":" + filterName); //$NON-NLS-1$
            }

            private void setInputData() {

                if (spooledFiles.length == 0) {
                    setPinned(false);
                }

                workWithSpooledFilesPanel.setInput(spooledFiles);
                refreshActionsEnablement();
            }

            private void setPosition() {

                if (itemIndex < 0) {
                    return;
                } else if (itemIndex > spooledFiles.length - 1) {
                    workWithSpooledFilesPanel.getViewer().getTable().setSelection(spooledFiles.length - 1);
                } else {
                    workWithSpooledFilesPanel.getViewer().getTable().setSelection(itemIndex);
                }
            }

            private void setMenu() {

                String connectionName = inputData.getConnectionName();
                Table table = workWithSpooledFilesPanel.getViewer().getTable();

                Menu menu = new Menu(table);
                WorkWithSpooledFilesMenuAdapter menuAdapter = new WorkWithSpooledFilesMenuAdapter(menu, connectionName,
                    workWithSpooledFilesPanel.getViewer());
                menuAdapter.addChangedListener(AbstractWorkWithSpooledFilesView.this);
                menu.addMenuListener(menuAdapter);

                workWithSpooledFilesPanel.setMenu(menu);
                workWithSpooledFilesPanel.setDoubleClickListener(menuAdapter);
            }

        }.schedule();

    }

    @Override
    public void setFocus() {
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
            refreshData(event.getItemIndex());
        } else if (event.isEvent(EventType.CHANGED)) {
            event.getSpooledFile().refresh();
            getWorkWithSpooledFilesPanel().getViewer().update(event.getSpooledFile(), null);
        }
    }

    /*
     * IPinnableView methods
     */

    public boolean isPinned() {
        return pinViewAction.isChecked();
    }

    public void setPinned(boolean pinned) {

        pinViewAction.setChecked(pinned);

        if (pinned) {
            updatePinProperties();
        } else {
            initializePinProperties();
        }
    }

    public String getContentId() {

        if (inputData == null) {
            return null;
        }

        return inputData.getContentId();
    }

    public Map<String, String> getPinProperties() {
        return pinProperties;
    }

    /*
     * Private and protected methods
     */

    protected WorkWithSpooledFilesPanel getWorkWithSpooledFilesPanel() {
        return workWithSpooledFilesPanel;
    }

    private Shell getShell() {
        return getSite().getShell();
    }

    private void refreshActionsEnablement() {

        if (noObjectAvailable()) {
            refreshViewAction.setEnabled(false);
        } else {
            refreshViewAction.setEnabled(true);
        }

        if (noObjectAvailable()) {
            pinViewAction.setEnabled(false);
        } else {
            pinViewAction.setEnabled(true);
        }

        resetColumnSizeAction.setEnabled(true);
    }

    private boolean noObjectAvailable() {
        return inputData == null;
    }

    protected abstract IViewManager getViewManager();
}
