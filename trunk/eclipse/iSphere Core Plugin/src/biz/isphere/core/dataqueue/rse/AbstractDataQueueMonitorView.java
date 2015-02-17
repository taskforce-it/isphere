/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue.rse;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.wb.swt.SWTResourceManager;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataqueue.ContentProviderTableViewer;
import biz.isphere.core.dataqueue.ControlListenerTableViewer;
import biz.isphere.core.dataqueue.DataQueueEntryMenuAdapter;
import biz.isphere.core.dataqueue.DataQueuePropertySource;
import biz.isphere.core.dataqueue.LabelProviderTableViewer;
import biz.isphere.core.dataqueue.retrieve.description.QMHQRDQD;
import biz.isphere.core.dataqueue.retrieve.description.RDQD0100;
import biz.isphere.core.dataqueue.retrieve.message.QMHRDQM;
import biz.isphere.core.dataqueue.retrieve.message.RDQM0200;
import biz.isphere.core.dataqueue.retrieve.message.RDQM0200SenderID;
import biz.isphere.core.dataqueue.retrieve.message.RDQS0100;
import biz.isphere.core.dataqueue.retrieve.message.RDQS0200;
import biz.isphere.core.dataspaceeditordesigner.rse.AbstractDropDataObjectListerner;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogView;
import biz.isphere.core.dataspacemonitor.action.RefreshViewAction;
import biz.isphere.core.dataspacemonitor.action.RefreshViewIntervalAction;
import biz.isphere.core.internal.IJobFinishedListener;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.internal.viewmanager.IPinnableView;
import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.core.internal.viewmanager.PinViewAction;

import com.ibm.as400.access.AS400;

public abstract class AbstractDataQueueMonitorView extends ViewPart implements IDialogView, IPinnableView, IJobFinishedListener, ISelectionProvider,
    IAdaptable, ControlListener {

    public static final String ID = "biz.isphere.rse.dataqueue.rse.DataQueueMonitorView"; //$NON-NLS-1$ 

    private static final int MAXIMUM_MESSAGE_LENGTH_TO_RETRIEVE = 2048;

    private static final String CONNECTION_NAME = "connectionName"; //$NON-NLS-1$
    private static final String OBJECT = "object"; //$NON-NLS-1$
    private static final String LIBRARY = "library"; //$NON-NLS-1$
    private static final String OBJECT_TYPE = "objectType"; //$NON-NLS-1$
    private static final String DESCRIPTION = "description"; //$NON-NLS-1$
    private static final String TABLE_COLUMN = "tableColumn_"; //$NON-NLS-1$

    private Composite mainArea;
    private RemoteObject remoteObject;
    private DataQueuePropertySource propertySource;

    private Set<String> pinKeys;

    private Map<String, String> pinProperties;

    private Label labelObject;
    private Label labelLibrary;
    private Label labelType;
    private Label labelDescription;
    private int[] columnWidth;

    private Label labelInfoMessage;
    private Label labelInvalidDataWarningOrError;

    private RefreshViewAction refreshViewAction;
    private RefreshViewIntervalAction disableRefreshViewAction;
    private List<RefreshViewIntervalAction> refreshIntervalActions;
    private PinViewAction pinViewAction;
    private AutoRefreshJob autoRefreshJob;

    private Composite tableViewerArea;
    private TableViewer tableViewer;
    private Composite labelArea;

    public AbstractDataQueueMonitorView() {

        autoRefreshJob = null;

        pinKeys = new HashSet<String>();
        pinKeys.add(CONNECTION_NAME);
        pinKeys.add(OBJECT);
        pinKeys.add(LIBRARY);
        pinKeys.add(OBJECT_TYPE);
        pinKeys.add(DESCRIPTION);

        for (int i = 0; i < LabelProviderTableViewer.getNumColumns(); i++) {
            pinKeys.add(TABLE_COLUMN + i);
        }

        pinProperties = new HashMap<String, String>();
        columnWidth = new int[LabelProviderTableViewer.getNumColumns()];

        getViewManager().add(this);
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);

        getSite().setSelectionProvider(this);
    }

    @Override
    public void createPartControl(Composite parent) {

        mainArea = new Composite(parent, SWT.NONE);
        mainArea.setLayout(createGridLayoutSimple());

        createDataQueueEditorLabels(mainArea, null);

        tableViewerArea = new Composite(mainArea, SWT.NONE);
        GridLayout tableViewerAreaLayout = new GridLayout();
        tableViewerArea.setLayout(tableViewerAreaLayout);

        GridData tableViewerAreaLayoutData = new GridData();
        tableViewerAreaLayoutData.horizontalAlignment = SWT.FILL;
        tableViewerAreaLayoutData.verticalAlignment = SWT.FILL;
        tableViewerAreaLayoutData.grabExcessHorizontalSpace = true;
        tableViewerAreaLayoutData.grabExcessVerticalSpace = true;
        tableViewerArea.setLayoutData(tableViewerAreaLayoutData);

        if (!getViewManager().isPinned(this)) {
            createDataQueueEditor(tableViewerArea, null, null);
        }

        addDropSupportOnComposite(tableViewerArea);

        createActions();
        initializeToolBar();
        initializeViewMenu();

        if (!getViewManager().isLoadingView() && getViewManager().isPinned(this)) {
            restoreViewData();
        }

    }

    private void restoreViewData() {

        /*
         * The view must be restored from a UI job because otherwise the
         * IViewManager cannot load the IRSEPersistenceManager, because the
         * RSECorePlugin is not loaded (Maybe, because the UI thread is
         * blocked?).
         */
        RestoreViewJob job = new RestoreViewJob();
        job.schedule();
    }

    private void createActions() {

        refreshViewAction = new RefreshViewAction(this);
        refreshViewAction.setToolTipText(Messages.Refresh_the_contents_of_this_view);
        refreshViewAction.setImageDescriptor(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_REFRESH));

        disableRefreshViewAction = new RefreshViewIntervalAction(this, -1);

        refreshIntervalActions = new ArrayList<RefreshViewIntervalAction>();
        refreshIntervalActions.add(new RefreshViewIntervalAction(this, 1));
        refreshIntervalActions.add(new RefreshViewIntervalAction(this, 3));
        refreshIntervalActions.add(new RefreshViewIntervalAction(this, 10));
        refreshIntervalActions.add(new RefreshViewIntervalAction(this, 30));

        pinViewAction = new PinViewAction(this);
        pinViewAction.setToolTipText(Messages.Pin_View);
        pinViewAction.setImageDescriptor(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_PIN));

        refreshActionsEnablement();
    }

    private void initializeToolBar() {

        IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
        toolbarManager.add(pinViewAction);
        toolbarManager.add(disableRefreshViewAction);
        toolbarManager.add(refreshViewAction);
    }

    private void initializeViewMenu() {

        IActionBars actionBars = getViewSite().getActionBars();
        IMenuManager viewMenu = actionBars.getMenuManager();

        MenuManager layoutSubMenu = new MenuManager(Messages.Auto_refresh_menu_item);
        layoutSubMenu.add(disableRefreshViewAction);

        for (RefreshViewIntervalAction refreshAction : refreshIntervalActions) {
            layoutSubMenu.add(refreshAction);
        }

        viewMenu.add(layoutSubMenu);
    }

    @Override
    public void setFocus() {
        // nothing to set here
    }

    /**
     * This method is called from objects, that want to refresh the data of the
     * current displayed data queue. For example, it is called from
     * {@link RefreshViewAction#run()}.
     */
    public void refreshData() {
        try {

            LoadAsyncDataJob loadDataJob = new LoadAsyncDataJob(Messages.Loading_remote_objects, remoteObject);
            loadDataJob.schedule();

        } catch (Exception e) {
            ISpherePlugin.logError(e.getMessage(), e);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
        }
    }

    /**
     * Sets the data for the view.
     * <p>
     * This method is used, when a data queue is selected from the RSE tree or
     * when the object is dropped into the view.
     */
    public void setData(RemoteObject[] remoteObjects) {

        if (!checkInputData(remoteObjects)) {
            return;
        }

        if (!checkRemoteObjectType(remoteObjects[0])) {
            return;
        }

        setData(remoteObjects[0], false);
    }

    private void setData(RemoteObject remoteObject, boolean restoreView) {

        clearData();

        LoadAsyncDataJob loadDataJob = new LoadAsyncDataJob(Messages.Loading_remote_objects, remoteObject, restoreView);
        loadDataJob.schedule();

    }

    private boolean checkRemoteObjectType(RemoteObject remoteObject) {

        if (!(ISeries.DTAQ.equals(remoteObject.getObjectType()))) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                Messages.bind(Messages.Selected_object_does_not_match_expected_type, ISeries.DTAQ));
            return false;
        }

        return true;
    }

    private boolean checkInputData(RemoteObject[] remoteObjects) {

        if (remoteObjects == null || remoteObjects.length == 0) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                Messages.bind(Messages.Selected_object_does_not_match_expected_type, ISeries.DTAQ));
            return false;
        }

        if (remoteObjects.length > 1) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Can_not_display_more_than_one_object_at_a_time);
            return false;
        }

        return true;
    }

    /**
     * This method is intended to be called by batch jobs to set the data space
     * that is displayed in the view.
     * <p>
     * For example it is called by
     * {@link AbstractDropDataObjectListerner#setRemoteObjects(RemoteObject[])}.
     */
    public void dropData(RemoteObject[] remoteObjects) {

        if (isPinned()) {
            MessageDialogAsync.displayError(getShell(),
                Messages.The_object_cannot_be_dropped_because_the_view_is_pinned_Please_use_the_context_menu_to_open_a_new_view);
            return;
        }

        /*
         * Create a UI job to check the input. Afterwards create a batch job, to
         * load and set the data.
         */
        DropDataUIJob reveiveDataUIJob = new DropDataUIJob(getShell().getDisplay(), remoteObjects);
        reveiveDataUIJob.schedule();
    }

    private void createDataQueueEditorLabels(Composite parent, RemoteObject remoteObject) {

        labelArea = new Composite(parent, SWT.NONE);
        GridLayout layout = createGridLayoutSimple(8);
        labelArea.setLayout(layout);

        labelObject = createLabel(labelArea, Messages.Object_colon, ""); //$NON-NLS-1$
        labelLibrary = createLabel(labelArea, Messages.Library_colon, ""); //$NON-NLS-1$
        labelType = createLabel(labelArea, Messages.Type_colon, ""); //$NON-NLS-1$
        labelDescription = createLabel(labelArea, Messages.Text_colon, ""); //$NON-NLS-1$
    }

    private void updateDataQueueEditorLabels(RemoteObject remoteObject) {

        if (remoteObject == null) {
            labelObject.setText(""); //$NON-NLS-1$
            labelLibrary.setText(""); //$NON-NLS-1$
            labelType.setText(""); //$NON-NLS-1$
            labelDescription.setText(""); //$NON-NLS-1$
        } else {
            labelObject.setText(remoteObject.getName());
            labelLibrary.setText(remoteObject.getLibrary());
            labelType.setText(remoteObject.getObjectType());
            labelDescription.setText(remoteObject.getDescription());
        }
    }

    private Label createLabel(Composite parent, String label, String value) {

        Label labelLabel = new Label(parent, SWT.NONE);
        labelLabel.setText(label);

        Label valueLabel = new Label(parent, SWT.NONE);
        valueLabel.setText(value);

        return valueLabel;
    }

    private void createDataQueueEditor(Composite parent, RDQD0100 rdqd0100, RDQM0200 rdqm0200) {

        try {

            // Create the table viewer
            tableViewer = new TableViewer(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);

            // Set the content and label providers
            LabelProviderTableViewer labelProvider = new LabelProviderTableViewer(rdqd0100);
            tableViewer.setLabelProvider(labelProvider);
            if (rdqd0100 != null) {
                tableViewer.setContentProvider(new ContentProviderTableViewer(rdqm0200));
            }

            // Set up the table
            int columnIndex = -1;
            tableViewer.getTable().setLayoutData(createGridDataFillAndGrab());

            columnIndex++;
            TableColumn entryType = new TableColumn(tableViewer.getTable(), SWT.NONE);
            entryType.addControlListener(this);
            entryType.setData(columnIndex);
            entryType.setText(Messages.Enqueued);
            labelProvider.setEntryTypeColumnIndex(columnIndex);

            if (rdqd0100 != null && rdqd0100.isKeyed()) {
                columnIndex++;
                TableColumn messageKey = new TableColumn(tableViewer.getTable(), SWT.NONE);
                messageKey.addControlListener(this);
                messageKey.setData(columnIndex);
                messageKey.setText(Messages.Message_key);
                labelProvider.setKeyColumnIndex(columnIndex);
            }

            if (rdqd0100 != null && isSenderIdIncluded(rdqd0100)) {
                columnIndex++;
                TableColumn senderId = new TableColumn(tableViewer.getTable(), SWT.NONE);
                senderId.addControlListener(this);
                senderId.setData(columnIndex);
                senderId.setText(Messages.Sender_ID);
                labelProvider.setSenderIdColumnIndex(columnIndex);
            }

            columnIndex++;
            TableColumn messageLength = new TableColumn(tableViewer.getTable(), SWT.CENTER);
            messageLength.addControlListener(this);
            messageLength.setData(columnIndex);
            messageLength.setText(Messages.Length);
            labelProvider.setMessageTextLengthColumnIndex(columnIndex);

            columnIndex++;
            TableColumn messageText = new TableColumn(tableViewer.getTable(), SWT.NONE);
            messageText.addControlListener(this);
            messageText.setData(columnIndex);
            messageText.setText(Messages.Message_text);
            labelProvider.setMessageTextColumnIndex(columnIndex);

            tableViewer.getTable().setHeaderVisible(true);
            tableViewer.getTable().setLinesVisible(true);

            tableViewer.getTable().addControlListener(new ControlListenerTableViewer(labelProvider));

            Menu dataQueueEntryPopup = new Menu(tableViewer.getTable());
            dataQueueEntryPopup.addMenuListener(new DataQueueEntryMenuAdapter(dataQueueEntryPopup, tableViewer.getTable()));
            tableViewer.getTable().setMenu(dataQueueEntryPopup);

            // Create status line
            Composite statusLine = new Composite(parent, SWT.NONE);
            GridLayout statusLineLayout = createGridLayoutSimple(2);
            statusLineLayout.marginHeight = 0;
            statusLineLayout.marginWidth = 0;
            statusLine.setLayout(statusLineLayout);
            statusLine.setLayoutData(createGridDataFillAndGrab(1));

            labelInfoMessage = new Label(statusLine, SWT.NONE);
            labelInfoMessage.setLayoutData(createGridDataFillAndGrab(1));
            labelInfoMessage.setVisible(false);

            labelInvalidDataWarningOrError = new Label(statusLine, SWT.BORDER);
            labelInvalidDataWarningOrError.setLayoutData(createGridDataFillAndGrab(1));
            labelInvalidDataWarningOrError.setAlignment(SWT.CENTER);
            labelInvalidDataWarningOrError.setVisible(false);

        } catch (Throwable e) {
            ISpherePlugin.logError("Failed to create data queue view.", e); //$NON-NLS-1$
        }
    }

    private void clearData() {

        remoteObject = null;
        propertySource = null;

        deleteDataQueueEditor();
    }

    private void deleteDataQueueEditor() {

        if (tableViewerArea == null) {
            return;
        }

        Control children[] = tableViewerArea.getChildren();
        for (Control control : children) {
            control.dispose();
        }

        updateDataQueueEditorLabels(remoteObject);

        clearInvalidDataErrorMessage();

        mainArea.layout();
        tableViewerArea.layout();
    }

    private void setInfoMessage(String message) {

        if (labelInfoMessage != null && !labelInfoMessage.isDisposed()) {
            if (StringHelper.isNullOrEmpty(message)) {
                labelInfoMessage.setText(""); //$NON-NLS-1$
                labelInfoMessage.setVisible(false);
            } else {
                labelInfoMessage.setText(message);
                labelInfoMessage.setVisible(true);
            }
            labelInfoMessage.getParent().layout();
        }
    }

    private void clearInfoMessage() {
        setInfoMessage(null);
    }

    private void setInvalidDataErrorMessage(String message) {

        if (labelInvalidDataWarningOrError != null && !labelInvalidDataWarningOrError.isDisposed()) {
            if (StringHelper.isNullOrEmpty(message)) {
                labelInvalidDataWarningOrError.setText(""); //$NON-NLS-1$
                labelInvalidDataWarningOrError.setVisible(false);
            } else {
                labelInvalidDataWarningOrError.setText(message);
                labelInvalidDataWarningOrError.setVisible(true);
                labelInvalidDataWarningOrError.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
            }
            labelInvalidDataWarningOrError.getParent().layout();
        }
    }

    private void clearInvalidDataErrorMessage() {
        setInvalidDataErrorMessage(null);
    }

    private void refreshActionsEnablement() {

        if (remoteObject == null || isAutoRefreshOn()) {
            refreshViewAction.setEnabled(false);
        } else {
            refreshViewAction.setEnabled(true);
        }

        if (isAutoRefreshOn()) {
            disableRefreshViewAction.setEnabled(true);
        } else {
            disableRefreshViewAction.setEnabled(false);
        }

        for (RefreshViewIntervalAction refreshAction : refreshIntervalActions) {
            if (isAutoRefreshOn()) {
                if (autoRefreshJob.getInterval() == refreshAction.getInterval()) {
                    refreshAction.setEnabled(false);
                } else {
                    refreshAction.setEnabled(true);
                }
            } else {
                refreshAction.setEnabled(true);
            }
        }

        if (remoteObject == null) {
            pinViewAction.setEnabled(false);
        } else {
            pinViewAction.setEnabled(true);
        }
    }

    private boolean isAutoRefreshOn() {

        if (autoRefreshJob != null) {
            return true;
        }
        return false;
    }

    private void addDropSupportOnComposite(Composite dialogEditorComposite) {

        Transfer[] transferTypes = new Transfer[] { PluginTransfer.getInstance() };
        int operations = DND.DROP_MOVE | DND.DROP_COPY;
        DropTargetListener listener = createDropListener(this);

        DropTarget dropTarget = new DropTarget(dialogEditorComposite, operations);
        dropTarget.setTransfer(transferTypes);
        dropTarget.addDropListener(listener);
        dropTarget.setData(this);
    }

    private GridLayout createGridLayoutSimple() {
        return createGridLayoutSimple(1);
    }

    private GridLayout createGridLayoutSimple(int columns) {
        GridLayout layout = new GridLayout(columns, false);
        layout.marginHeight = 5;
        layout.horizontalSpacing = 10;
        return layout;
    }

    private GridData createGridDataSimple() {
        return new GridData();
    }

    private GridData createGridDataFillAndGrab() {
        GridData layoutData = createGridDataSimple();
        layoutData.horizontalAlignment = SWT.FILL;
        layoutData.verticalAlignment = SWT.FILL;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        return layoutData;
    }

    private GridData createGridDataFillAndGrab(int numColumns) {
        GridData layoutData = createGridDataFillAndGrab();
        layoutData.horizontalSpan = numColumns;
        layoutData.grabExcessVerticalSpace = false;
        return layoutData;
    }

    protected Shell getShell() {
        return this.getSite().getShell();
    }

    public void setRefreshInterval(int seconds) {

        if (remoteObject == null) {
            seconds = RefreshViewIntervalAction.REFRESH_OFF;
            return;
        }

        if (autoRefreshJob != null) {
            if (seconds == RefreshViewIntervalAction.REFRESH_OFF) {
                autoRefreshJob.cancel();
            } else {
                autoRefreshJob.setInterval(seconds);
            }
            return;
        }

        autoRefreshJob = new AutoRefreshJob(this, remoteObject, seconds);
        autoRefreshJob.schedule();

        refreshActionsEnablement();
    }

    public void jobFinished(Job job) {
        if (job == autoRefreshJob) {
            autoRefreshJob = null;
            refreshActionsEnablement();
        }
    }

    private void updatePinProperties() {

        if (isPinned()) {
            pinProperties.put(CONNECTION_NAME, remoteObject.getConnectionName());
            pinProperties.put(OBJECT, remoteObject.getName());
            pinProperties.put(LIBRARY, remoteObject.getLibrary());
            pinProperties.put(OBJECT_TYPE, remoteObject.getObjectType());
            pinProperties.put(DESCRIPTION, remoteObject.getDescription());
        } else {
            pinProperties.put(CONNECTION_NAME, null);
            pinProperties.put(OBJECT, null);
            pinProperties.put(LIBRARY, null);
            pinProperties.put(OBJECT_TYPE, null);
            pinProperties.put(DESCRIPTION, null);
        }

        updateColumnWidths();
    }

    private void updateColumnWidths() {
        for (int i = 0; i < columnWidth.length; i++) {
            if (isPinned()) {
                pinProperties.put(TABLE_COLUMN + i, Integer.toString(columnWidth[i]));
            } else {
                pinProperties.put(TABLE_COLUMN + i, Integer.toString(0));
            }
        }
    }

    private int getMessageLengthToRetrieve(RDQD0100 rdqd0100) {

        int messageLengthToRetrieve = MAXIMUM_MESSAGE_LENGTH_TO_RETRIEVE;
        if (rdqd0100 != null && rdqd0100.getMessageLength() < messageLengthToRetrieve) {
            messageLengthToRetrieve = rdqd0100.getMessageLength();
            if (isSenderIdIncluded(rdqd0100)) {
                messageLengthToRetrieve = messageLengthToRetrieve + RDQM0200SenderID.LENGTH_OF_SENDER_ID;
            }
        }

        return messageLengthToRetrieve;
    }

    private boolean isSenderIdIncluded(RDQD0100 rdqd0100) {

        try {
            return rdqd0100.isSenderIDIncludedInMessageText();
        } catch (UnsupportedEncodingException e) {
            return true;
        }
    }

    /**
     * Implements {@link IPinnableView#setPinned(boolean)}
     */
    public void setPinned(boolean pinned) {
        pinViewAction.setChecked(pinned);
        updatePinProperties();
    }

    /**
     * Implements {@link IPinnableView#isPinned()}
     */
    public boolean isPinned() {
        return pinViewAction.isChecked();
    }

    /**
     * Implements {@link IPinnableView#getContentId()}
     */
    public String getContentId() {
        if (remoteObject == null) {
            return null;
        }
        return remoteObject.getAbsoluteName();
    }

    /**
     * Implements {@link IPinnableView#getPinProperties()}
     */
    public Map<String, String> getPinProperties() {
        return pinProperties;
    }

    /**
     * Implements {@link IAdaptable#getAdapter(Class)}
     */
    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {

        if (adapter == IPropertySource.class) {
            return propertySource;
        }

        return null;
    }

    /**
     * Implements
     * {@link ISelectionProvider#addSelectionChangedListener(ISelectionChangedListener)}
     */
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
    };

    /**
     * Implements {@link ISelectionProvider#getSelection()}
     */
    public ISelection getSelection() {
        return new StructuredSelection(this);
    };

    /**
     * Implements
     * {@link ISelectionProvider#removeSelectionChangedListener(ISelectionChangedListener)}
     */
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
    };

    /**
     * Implements {@link ISelectionProvider#setSelection(ISelection)}
     */
    public void setSelection(ISelection selection) {
    };

    /**
     * Implements {@link ControlListener#controlMoved(ControlEvent)}
     */
    public void controlMoved(ControlEvent event) {
    }

    /**
     * Implements {@link ControlListener#controlResized(ControlEvent)}
     * <p>
     * Overridden to keep track of the column widths of the table viewer. The
     * column widths are needed when restoring a pinned view.
     * <p>
     * We cannot just get the width from the column, because the <i>table</i> of
     * the <i>tableViewer</i> is already disposed, when {@link #dispose()} is
     * called.
     */
    public void controlResized(ControlEvent event) {
        if (event.getSource() instanceof TableColumn) {
            TableColumn column = (TableColumn)event.getSource();
            if (column.getData() instanceof Integer) {
                int index = (Integer)column.getData();
                if (index >= 0 && index < columnWidth.length) {
                    columnWidth[index] = column.getWidth();
                }
            }
        }
    }

    /**
     * Overridden to update the pin properties when the view is closed.
     */
    @Override
    public void dispose() {

        updatePinProperties();
        getViewManager().remove(this);

        super.dispose();
    }

    protected abstract IViewManager getViewManager();

    protected abstract AbstractDropDataObjectListerner createDropListener(IDialogView editor);

    protected abstract AS400 getSystem(String connectionName);

    /**
     * Job, that receives data that has been passed by
     * {@link IDialogView#dropData(RemoteObject[])}. It performs error checking
     * on the UI thread and then passed the remote object and the selected
     * editor to the next job.
     * <p>
     * It is the first job in a series of three.
     */
    private class DropDataUIJob extends UIJob {

        private RemoteObject[] remoteObjects;

        public DropDataUIJob(Display jobDisplay, RemoteObject[] remoteObjects) {
            super(jobDisplay, Messages.Loading_remote_objects);
            this.remoteObjects = remoteObjects;
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor monitor) {

            setData(remoteObjects);

            return Status.OK_STATUS;
        }
    }

    /**
     * Job, that loads the data of the space object that is displayed by this
     * view.
     * <p>
     * It is the second job in a series of three.
     */
    private class LoadAsyncDataJob extends Job {

        private RemoteObject remoteObject;
        private boolean restoreView;

        public LoadAsyncDataJob(String name, RemoteObject remoteObject) {
            this(name, remoteObject, false);
        }

        public LoadAsyncDataJob(String name, RemoteObject remoteObject, boolean restoreView) {
            super(name);
            this.remoteObject = remoteObject;
            this.restoreView = restoreView;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {

            try {

                AS400 as400 = getSystem(remoteObject.getConnectionName());

                QMHQRDQD qmhqrdqd = new QMHQRDQD(as400);
                RDQD0100 rdqd0100 = qmhqrdqd.retrieveDescription(remoteObject.getName(), remoteObject.getLibrary());

                QMHRDQM qmhrdqm = new QMHRDQM(as400);
                qmhrdqm.setDataQueue(remoteObject.getName(), remoteObject.getLibrary(), rdqd0100.isSenderIDIncludedInMessageText());

                int numberOfMessages = 100;
                int messageLength = getMessageLengthToRetrieve(rdqd0100);

                RDQM0200 rdqm0200;
                if (rdqd0100.isKeyed()) {
                    rdqm0200 = qmhrdqm.retrieveMessagesByKey(RDQS0200.ORDER_GE, "", numberOfMessages, messageLength, rdqd0100.getKeyLength()); //$NON-NLS-1$
                } else {
                    rdqm0200 = qmhrdqm.retrieveMessages(RDQS0100.SELECT_ALL, numberOfMessages, messageLength);
                }

                /*
                 * Create a UI job to update the view with the new data.
                 */
                UIJob updateDataUIJob = new UpdateDataUIJob(getShell().getDisplay(), getName(), remoteObject, rdqd0100, rdqm0200, restoreView);
                updateDataUIJob.schedule();

            } catch (Throwable e) {
                ISpherePlugin.logError(e.getMessage(), e);
                MessageDialogAsync.displayError(getShell(), e.getLocalizedMessage());
            }

            return Status.OK_STATUS;
        }
    }

    /**
     * Job, that runs on the UI thread and which updates the view with the data
     * of the remote object.
     * <p>
     * It is the third and last job in a series of three.
     */
    private class UpdateDataUIJob extends UIJob {

        private RemoteObject remoteObject;
        private RDQD0100 rdqd0100;
        private RDQM0200 rdqm0200;
        private boolean restoreView;
        private IJobFinishedListener finishedListener;

        public UpdateDataUIJob(Display jobDisplay, String name, RemoteObject remoteObject, RDQD0100 rdqd0100, RDQM0200 rdqm0200) {
            this(jobDisplay, name, remoteObject, rdqd0100, rdqm0200, false);
        }

        public UpdateDataUIJob(Display jobDisplay, String name, RemoteObject remoteObject, RDQD0100 rdqd0100, RDQM0200 rdqm0200, boolean restoreView) {
            super(jobDisplay, name);

            this.remoteObject = remoteObject;
            this.rdqd0100 = rdqd0100;
            this.rdqm0200 = rdqm0200;
            this.restoreView = restoreView;
        }

        public void setJobFinishedListener(IJobFinishedListener listener) {
            this.finishedListener = listener;
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor monitor) {

            AbstractDataQueueMonitorView.this.remoteObject = remoteObject;
            AbstractDataQueueMonitorView.this.propertySource = new DataQueuePropertySource(rdqd0100);

            if (tableViewerArea == null || tableViewerArea.getChildren().length == 0) {
                createDataQueueEditor(tableViewerArea, rdqd0100, rdqm0200);
            }

            tableViewer.setInput(rdqm0200);

            updateDataQueueEditorLabels(remoteObject);

            if (rdqm0200.getMaximumMessageTextLengthRequested() < rdqm0200.getMaximumMessageLengthLoaded()) {
                setInvalidDataErrorMessage(Messages.bind(Messages.Retrieved_up_to_A_bytes_of_message_data, rdqm0200.getMaximumMessageTextLengthRequested()));
            } else {
                clearInvalidDataErrorMessage();
            }

            setInfoMessage(Messages.bind(Messages.A_messages_retrieved, rdqm0200.getNumberOfMessagesReturned()));

            refreshActionsEnablement();

            mainArea.layout();
            tableViewerArea.layout();

            if (restoreView) {
                restoreColumnWidths();
                setPinned(true);
            } else {
                setPinned(false);
            }

            if (finishedListener != null) {
                finishedListener.jobFinished(this);
            }

            return Status.OK_STATUS;
        }

        private void restoreColumnWidths() {

            TableColumn[] columns = tableViewer.getTable().getColumns();
            for (int i = 0; i < columns.length; i++) {
                int width = IntHelper.tryParseInt(pinProperties.get(TABLE_COLUMN + i), -1);
                if (width != -1) {
                    TableColumn tableColumn = columns[i];
                    tableColumn.setWidth(width);
                }
            }
        }
    }

    /**
     * Job, that restores a pinned view.
     */
    private class RestoreViewJob extends UIJob {

        public RestoreViewJob() {
            super(Messages.Restoring_view);
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor monitor) {

            IViewManager viewManager = getViewManager();
            if (!viewManager.isInitialized(5000)) {
                ISpherePlugin.logError("Could not restore view. View manager did not initialize within 5 seconds.", null); //$NON-NLS-1$
                return Status.OK_STATUS;
            }

            pinProperties = viewManager.getPinProperties(AbstractDataQueueMonitorView.this, pinKeys);

            String connectionName = pinProperties.get(CONNECTION_NAME);
            String name = pinProperties.get(OBJECT);
            String library = pinProperties.get(LIBRARY);
            String objectType = pinProperties.get(OBJECT_TYPE);
            String description = pinProperties.get(DESCRIPTION);

            if (connectionName == null || name == null || library == null || objectType == null) {
                return Status.OK_STATUS;
            }

            RemoteObject remoteObject = new RemoteObject(connectionName, name, library, objectType, description);
            setData(remoteObject, true);

            mainArea.redraw();
            mainArea.update();

            return Status.OK_STATUS;
        }
    }

    /**
     * Job, that periodically refreshes the content of the view.
     */
    private class AutoRefreshJob extends Job implements IJobFinishedListener {

        final int MILLI_SECONDS = 1000;

        private IJobFinishedListener jobFinishedListener;
        private RemoteObject remoteObject;
        private int interval;

        private UpdateDataUIJob updateDataUIJob;
        private int waitTime;

        public AutoRefreshJob(IJobFinishedListener listener, RemoteObject remoteObject, int seconds) {
            super(remoteObject.getQuaifiedObject());
            this.jobFinishedListener = listener;
            this.remoteObject = remoteObject;
            setInterval(seconds);
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {

            final int SLEEP_INTERVAL = 50;

            while (!monitor.isCanceled()) {

                try {

                    AS400 as400 = getSystem(remoteObject.getConnectionName());

                    QMHQRDQD qmhqrdqd = new QMHQRDQD(as400);
                    RDQD0100 rdqd0100 = qmhqrdqd.retrieveDescription(remoteObject.getName(), remoteObject.getLibrary());

                    QMHRDQM qmhrdqm = new QMHRDQM(as400);
                    qmhrdqm.setDataQueue(remoteObject.getName(), remoteObject.getLibrary(), rdqd0100.isSenderIDIncludedInMessageText());

                    int numberOfMessages = 100;
                    int messageLength = getMessageLengthToRetrieve(rdqd0100);

                    RDQM0200 rdqm0200;
                    if (rdqd0100.isKeyed()) {
                        rdqm0200 = qmhrdqm.retrieveMessagesByKey(RDQS0200.ORDER_GE, "", numberOfMessages, messageLength, rdqd0100.getKeyLength()); //$NON-NLS-1$
                    } else {
                        rdqm0200 = qmhrdqm.retrieveMessages(RDQS0100.SELECT_ALL, numberOfMessages, messageLength);
                    }

                    /*
                     * Create a UI job to update the view with the new data.
                     */
                    updateDataUIJob = new UpdateDataUIJob(getShell().getDisplay(), getName(), remoteObject, rdqd0100, rdqm0200);
                    updateDataUIJob.setJobFinishedListener(this);
                    updateDataUIJob.schedule();

                    waitTime = interval;
                    while ((!monitor.isCanceled() && waitTime > 0) || updateDataUIJob != null) {
                        Thread.sleep(SLEEP_INTERVAL);
                        if (waitTime > interval) {
                            waitTime = interval;
                        }
                        if (waitTime > 0) {
                            waitTime = waitTime - SLEEP_INTERVAL;
                        }
                    }

                } catch (InterruptedException e) {
                    // exit the thread
                    break;
                } catch (Throwable e) {
                    ISpherePlugin.logError(e.getMessage(), e);
                    MessageDialogAsync.displayError(getShell(), e.getLocalizedMessage());
                    break;
                }
            }

            jobFinishedListener.jobFinished(this);

            return Status.OK_STATUS;
        }

        public int getInterval() {

            return interval / MILLI_SECONDS;
        }

        public void setInterval(int seconds) {

            interval = seconds * MILLI_SECONDS;
        }

        public void jobFinished(Job job) {

            if (job == updateDataUIJob) {
                updateDataUIJob = null;
            }
        }
    }
}
