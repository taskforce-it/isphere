/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspacemonitor.rse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.wb.swt.SWTResourceManager;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspace.rse.AbstractWrappedDataSpace;
import biz.isphere.core.dataspace.rse.SelectDataSpaceEditor;
import biz.isphere.core.dataspaceeditordesigner.listener.ControlBackgroundPainter;
import biz.isphere.core.dataspaceeditordesigner.model.AbstractDWidget;
import biz.isphere.core.dataspaceeditordesigner.model.DDataSpaceValue;
import biz.isphere.core.dataspaceeditordesigner.model.DEditor;
import biz.isphere.core.dataspaceeditordesigner.model.DataSpaceEditorManager;
import biz.isphere.core.dataspaceeditordesigner.repository.DataSpaceEditorRepository;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogView;
import biz.isphere.core.dataspacemonitor.action.RefreshViewAction;
import biz.isphere.core.dataspacemonitor.action.RefreshViewIntervalAction;
import biz.isphere.core.internal.ColorHelper;
import biz.isphere.core.internal.IJobFinishedListener;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.internal.viewmanager.IPinableView;
import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.core.internal.viewmanager.PinViewAction;
import biz.isphere.core.rse.AbstractDropRemoteObjectListerner;

public abstract class AbstractDataSpaceMonitorView extends ViewPart implements IDialogView, IPinableView, IJobFinishedListener {

    public static final String ID = "biz.isphere.rse.dataspacemonitor.rse.DataSpaceMonitorView"; //$NON-NLS-1$ 

    private static final String CONNECTION_NAME = "connectionName"; //$NON-NLS-1$
    private static final String OBJECT = "object"; //$NON-NLS-1$
    private static final String LIBRARY = "library"; //$NON-NLS-1$
    private static final String OBJECT_TYPE = "objectType"; //$NON-NLS-1$
    private static final String DESCRIPTION = "description"; //$NON-NLS-1$
    private static final String EDITOR = "editor"; //$NON-NLS-1$

    private DataSpaceEditorRepository repository;
    private DataSpaceEditorManager manager;
    private WatchItemManager watchManager;

    private Composite mainArea;
    private Composite dataSpaceEditor;
    private DEditor currentDEditor;
    private DDataSpaceValue currentDataSpaceValue;
    private Set<String> pinKeys;
    private Map<String, String> pinProperties;

    private Label labelObject;
    private Label labelLibrary;
    private Label labelType;
    private Label labelDescription;

    private Label labelInvalidDataWarningOrError;

    private RefreshViewAction refreshViewAction;
    private RefreshViewIntervalAction disableRefreshViewAction;
    private List<RefreshViewIntervalAction> refreshIntervalActions;
    private PinViewAction pinViewAction;
    private AutoRefreshJob autoRefreshJob;

    public AbstractDataSpaceMonitorView() {

        manager = new DataSpaceEditorManager();
        repository = DataSpaceEditorRepository.getInstance();
        watchManager = new WatchItemManager();
        autoRefreshJob = null;

        pinKeys = new HashSet<String>();
        pinKeys.add(CONNECTION_NAME);
        pinKeys.add(OBJECT);
        pinKeys.add(LIBRARY);
        pinKeys.add(OBJECT_TYPE);
        pinKeys.add(DESCRIPTION);
        pinKeys.add(EDITOR);

        pinProperties = new HashMap<String, String>();

        getViewManager().add(this);
    }

    @Override
    public void createPartControl(Composite parent) {

        mainArea = new Composite(parent, SWT.NONE);
        mainArea.setLayout(createGridLayoutSimple());

        dataSpaceEditor = createDataSpaceEditor(mainArea, null);

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
        refreshViewAction.setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_REFRESH));

        disableRefreshViewAction = new RefreshViewIntervalAction(this, -1);

        refreshIntervalActions = new ArrayList<RefreshViewIntervalAction>();
        refreshIntervalActions.add(new RefreshViewIntervalAction(this, 1));
        refreshIntervalActions.add(new RefreshViewIntervalAction(this, 3));
        refreshIntervalActions.add(new RefreshViewIntervalAction(this, 10));
        refreshIntervalActions.add(new RefreshViewIntervalAction(this, 30));

        pinViewAction = new PinViewAction(this);
        pinViewAction.setToolTipText(Messages.Pin_View);
        pinViewAction.setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_PIN));

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
     * current displayed data space. For example, it is called from
     * {@link RefreshViewAction#run()}.
     */
    public void refreshData() {
        try {

            if (noObjectAvailable()) {
                return;
            }

            if (isAutoRefreshOn()) {
                return;
            }

            LoadAsyncDataJob loadDataJob = new LoadAsyncDataJob(Messages.Loading_remote_objects, currentDataSpaceValue.getRemoteObject());
            loadDataJob.schedule();

        } catch (Exception e) {
            ISpherePlugin.logError(e.getMessage(), e);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
        }
    }

    /**
     * This method is called from objects, that want to change the display mode,
     * such as "display in hex" or "display end of data".
     */
    public void changeDisplayMode() {
    }

    /**
     * Sets the data for the view and lets the user select the desired editor.
     * <p>
     * This method is used, when a user space or data area is selected from the
     * RSE tree or when the object is dropped into the view.
     */
    public void setData(RemoteObject[] remoteObjects) {

        if (isAutoRefreshOn()) {
            MessageDialogAsync.displayError(getShell(), Messages.The_object_cannot_be_monitored_because_auto_refresh_is_active);
            return;
        }

        setData(remoteObjects, null);
    }

    /**
     * Sets the data for the view and specifies the editor to use.
     * <p>
     * This method is used when a pinned view is restored.
     * 
     * @param remoteObjects - data that is displayed
     * @param editorName - name of the editor
     */
    private void setData(RemoteObject[] remoteObjects, String editorName) {

        if (!checkInputData(remoteObjects)) {
            return;
        }

        RemoteObject remoteObject = remoteObjects[0];
        if (!checkRemoteObjectType(remoteObject)) {
            return;
        }

        DEditor selectedEditor = null;
        if (editorName == null) {
            selectedEditor = loadEditorForDataSpaceObject(getShell(), remoteObject);
            if (selectedEditor == null && ISeries.USRSPC.equals(remoteObject.getObjectType())) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.No_matching_editor_found);
                /*
                 * Cancel job, because we need an editor and user spaces might
                 * be quite large. This way we do not load it for nothing, in
                 * case we cannot find an editor. For all types of data areas we
                 * can generate the editor. Although it might be quite ugly,
                 * generating a text field for a 2000-byte character value.
                 */
                return;
            }
        } else if (!DataSpaceEditorManager.GENERATED.equals(editorName)) {
            selectedEditor = repository.getDataSpaceEditorsForObject(remoteObject, editorName);
        }

        /*
         * Submit long term process to batch. Let the process load the data and
         * pass it on to the UI update job.
         */
        LoadAsyncDataJob loadDataJob = new LoadAsyncDataJob(Messages.Loading_remote_objects, remoteObject, selectedEditor);
        loadDataJob.schedule();

        return;
    }

    private boolean checkRemoteObjectType(RemoteObject remoteObject) {

        if (!(ISeries.DTAARA.equals(remoteObject.getObjectType()) || ISeries.USRSPC.equals(remoteObject.getObjectType()))) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                Messages.bind(Messages.Selected_object_does_not_match_expected_type_A, ISeries.DTAARA + "/" + ISeries.USRSPC));
            return false;
        }

        return true;
    }

    private boolean checkInputData(RemoteObject[] remoteObjects) {

        if (remoteObjects == null || remoteObjects.length == 0) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Dropped_object_does_not_match_expected_type);
            return false;
        }

        if (remoteObjects.length > 1) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.Can_not_display_more_than_one_object_at_a_time);
            return false;
        }

        return true;
    }

    private boolean noObjectAvailable() {
        return currentDataSpaceValue == null;
    }

    /**
     * This method is intended to be called by batch jobs to set the data space
     * that is displayed in the view.
     * <p>
     * For example it is called by
     * {@link AbstractDropRemoteObjectListerner#setRemoteObjects(RemoteObject[])}.
     */
    public void dropData(RemoteObject[] remoteObjects, Object target) {

        if (isPinned()) {
            MessageDialogAsync.displayError(getShell(),
                Messages.The_object_cannot_be_monitored_because_the_view_is_pinned_Please_use_the_context_menu_to_open_a_new_view);
            return;
        }

        if (isAutoRefreshOn()) {
            MessageDialogAsync.displayError(getShell(), Messages.The_object_cannot_be_monitored_because_auto_refresh_is_active);
            return;
        }

        currentDataSpaceValue = null;

        /*
         * Create a UI job to check the input and to let the user select the
         * editor. Afterwards create a batch job, to load and set the data.
         */
        DropDataUIJob reveiveDataUIJob = new DropDataUIJob(getShell().getDisplay(), remoteObjects);
        reveiveDataUIJob.schedule();
    }

    private DDataSpaceValue createDataSpaceValue(AbstractWrappedDataSpace dataSpace) throws Throwable {
        DDataSpaceValue dataSpaceValue = DDataSpaceValue.getCharacterInstance(dataSpace.getRemoteObject(), dataSpace.getCCSIDEncoding(),
            dataSpace.getBytes());
        return dataSpaceValue;
    }

    private void replaceDataSpaceEditor(DEditor dEditor, DDataSpaceValue dataSpaceValue) {
        Control[] controls = mainArea.getChildren();
        for (Control control : controls) {
            control.dispose();
        }

        createDataSpaceSpaceEditorLabels(mainArea, dataSpaceValue);
        dataSpaceEditor = createDataSpaceEditor(mainArea, dEditor);
    }

    private void createDataSpaceSpaceEditorLabels(Composite parent, DDataSpaceValue dataSpaceValue) {
        Composite labelArea = new Composite(parent, SWT.NONE);
        GridLayout layout = createGridLayoutSimple(8);
        labelArea.setLayout(layout);

        labelObject = createLabel(labelArea, Messages.Object_colon, dataSpaceValue.getName());
        labelLibrary = createLabel(labelArea, Messages.Library_colon, dataSpaceValue.getLibrary());
        labelType = createLabel(labelArea, Messages.Type_colon, dataSpaceValue.getObjectType());
        labelDescription = createLabel(labelArea, Messages.Text_colon, dataSpaceValue.getDescrption());

        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData separatorLayoutData = createGridDataFillAndGrab(6);
        separator.setLayoutData(separatorLayoutData);
    }

    private void updateDataSpaceEditorLabels(DDataSpaceValue dataSpaceValue) {
        labelObject.setText(dataSpaceValue.getName());
        labelLibrary.setText(dataSpaceValue.getLibrary());
        labelType.setText(dataSpaceValue.getObjectType());
        labelDescription.setText(dataSpaceValue.getDescrption());
    }

    private Label createLabel(Composite parent, String label, String value) {

        Label labelLabel = new Label(parent, SWT.NONE);
        labelLabel.setText(label);

        Label valueLabel = new Label(parent, SWT.NONE);
        valueLabel.setText(value);

        return valueLabel;
    }

    private Composite createDataSpaceEditor(Composite parent, DEditor dEditor) {

        ScrolledComposite scrollableArea = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.NONE);
        scrollableArea.setLayout(new GridLayout(1, false));
        scrollableArea.setLayoutData(new GridData(GridData.FILL_BOTH));
        scrollableArea.setExpandHorizontal(true);
        scrollableArea.setExpandVertical(true);

        Composite dialogEditor = manager.createDialogArea(scrollableArea, dEditor, 2);
        int numColumns = ((GridLayout)dialogEditor.getLayout()).numColumns;

        if (dEditor != null) {

            AbstractDWidget[] widgets = dEditor.getWidgets();
            Color color = ColorHelper.getBackgroundColorOfSelectedControls();
            for (AbstractDWidget widget : widgets) {
                Control control = manager.createReadOnlyWidgetControlAndAddToParent(dialogEditor, 2, widget);
                if (control instanceof Text) {
                    control.addMouseTrackListener(new ControlBackgroundPainter(color));
                } else if (control instanceof Button) {
                    control.addMouseTrackListener(new ControlBackgroundPainter(color));
                }

                createControlDecorator(control);
                createControlPopupMenu(watchManager, dialogEditor, control);
            }

            Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
            GridData separatorLayoutData = createGridDataFillAndGrab(numColumns);
            separator.setLayoutData(separatorLayoutData);

            Composite statusLine = new Composite(parent, SWT.NONE);
            GridLayout statusLineLayout = createGridLayoutSimple(3);
            statusLineLayout.marginHeight = 0;
            statusLine.setLayout(statusLineLayout);
            statusLine.setLayoutData(createGridDataFillAndGrab(1));

            Label watchInfo = new Label(statusLine, SWT.NONE);
            watchInfo.setText(Messages.Use_the_context_menu_to_watch_an_item);
            watchInfo.setLayoutData(createGridDataFillAndGrab(1));

            Label generatedEditor = new Label(statusLine, SWT.NONE);
            if (dEditor.isGenerated()) {
                generatedEditor.setText(Messages.Generated_editor);
            } else {
                generatedEditor.setText(dEditor.getNameAndDescription());
            }
            generatedEditor.setLayoutData(createGridDataFillAndGrab(1));

            labelInvalidDataWarningOrError = new Label(statusLine, SWT.BORDER);
            labelInvalidDataWarningOrError.setText(Messages.Invalid_data_warning_Editor_might_not_be_suitable_for_the_data);
            labelInvalidDataWarningOrError.setAlignment(SWT.CENTER);
            labelInvalidDataWarningOrError.setLayoutData(createGridDataFillAndGrab(1));
            labelInvalidDataWarningOrError.setVisible(false);

        } else {

            Label dragDropInfo = new Label(parent, SWT.NONE);
            dragDropInfo.setText(Messages.Drag_drop_character_data_area_or_user_space_from_RSE_tree);
            dragDropInfo.setLayoutData(createGridDataFillAndGrab(numColumns));
        }

        dialogEditor.layout();
        scrollableArea.setContent(dialogEditor);
        scrollableArea.setMinSize(dialogEditor.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        addDropSupportOnComposite(dialogEditor);

        currentDEditor = dEditor;

        return dialogEditor;
    }

    private DEditor loadEditorForDataSpaceObject(Shell shell, RemoteObject remoteObject) {

        DEditor[] dEditors = repository.getDataSpaceEditorsForObject(remoteObject);
        if (dEditors == null || dEditors.length == 0) {
            return null;
        }

        if (dEditors.length == 1) {
            return dEditors[0];
        }

        SelectDataSpaceEditor dialogSelector = new SelectDataSpaceEditor(shell, dEditors);
        if (dialogSelector.open() == Dialog.OK) {
            return dialogSelector.getSelectedDialog();
        }

        return null;
    }

    private void copyDataToControls(DDataSpaceValue dataSpaceValue) {

        boolean hasInvalidDataWarning = false;
        boolean hasInvalidDataError = false;
        String errorItem = ""; //$NON-NLS-1$

        currentDataSpaceValue = dataSpaceValue;

        Control[] controls = dataSpaceEditor.getChildren();
        for (Control control : controls) {
            if (manager.isManagedControl(control)) {
                setControlValue(dataSpaceValue, control);
                if (!hasInvalidDataError) {
                    if (manager.hasInvalidDataError(control)) {
                        hasInvalidDataError = manager.hasInvalidDataError(control);
                        errorItem = manager.getPayloadFromControl(control).getWidget().getLabel();
                    } else if (!hasInvalidDataWarning && manager.hasInvalidDataWarning(control)) {
                        hasInvalidDataWarning = manager.hasInvalidDataWarning(control);
                        errorItem = manager.getPayloadFromControl(control).getWidget().getLabel();
                    }
                }
            }
        }

        if (hasInvalidDataError) {
            labelInvalidDataWarningOrError.setText(errorItem
                + ": " + Messages.Invalid_data_error_An_exception_was_thrown_when_copying_the_data_to_the_screen); //$NON-NLS-1$
            labelInvalidDataWarningOrError.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
            labelInvalidDataWarningOrError.setVisible(true);
        } else if (hasInvalidDataWarning) {
            labelInvalidDataWarningOrError.setText(errorItem + ": " + Messages.Invalid_data_warning_Editor_might_not_be_suitable_for_the_data); //$NON-NLS-1$
            labelInvalidDataWarningOrError.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
            labelInvalidDataWarningOrError.setVisible(true);
        } else {
            labelInvalidDataWarningOrError.setText("");
            labelInvalidDataWarningOrError.setVisible(false);
        }
    }

    private void setControlValue(DDataSpaceValue dataSpaceValue, Control control) {
        manager.setControlValue(control, dataSpaceValue);
        if (watchManager.isWatchedControl(control)) {
            watchManager.setCurrentValue(control, manager.getControlValue(control));
        }
    }

    private void refreshEditor() {

        mainArea.layout();

        refreshActionsEnablement();
    }

    private void refreshActionsEnablement() {

        if (noObjectAvailable() || isAutoRefreshOn()) {
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
                if (noObjectAvailable()) {
                    refreshAction.setEnabled(false);
                } else {
                    refreshAction.setEnabled(true);
                }
            }
        }

        if (noObjectAvailable()) {
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

    private Shell getShell() {
        return this.getSite().getShell();
    }

    public void setRefreshInterval(int seconds) {

        if (noObjectAvailable()) {
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

        autoRefreshJob = new AutoRefreshJob(this, currentDataSpaceValue.getRemoteObject(), seconds);
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
            RemoteObject remoteObject = currentDataSpaceValue.getRemoteObject();
            pinProperties.put(CONNECTION_NAME, remoteObject.getConnectionName());
            pinProperties.put(OBJECT, remoteObject.getName());
            pinProperties.put(LIBRARY, remoteObject.getLibrary());
            pinProperties.put(OBJECT_TYPE, remoteObject.getObjectType());
            pinProperties.put(DESCRIPTION, remoteObject.getDescription());
            pinProperties.put(EDITOR, currentDEditor.getName());
        } else {
            pinProperties.put(CONNECTION_NAME, null);
            pinProperties.put(OBJECT, null);
            pinProperties.put(LIBRARY, null);
            pinProperties.put(OBJECT_TYPE, null);
            pinProperties.put(DESCRIPTION, null);
            pinProperties.put(EDITOR, null);
        }
    }

    /**
     * Implements {@link IPinableView#setPinned(boolean)}
     */
    public void setPinned(boolean pinned) {
        pinViewAction.setChecked(pinned);
        updatePinProperties();
    }

    /**
     * Implements {@link IPinableView#isPinned()}
     */
    public boolean isPinned() {
        return pinViewAction.isChecked();
    }

    /**
     * Implements {@link IPinableView#getContentId()}
     */
    public String getContentId() {
        if (noObjectAvailable()) {
            return null;
        }
        RemoteObject remoteObject = currentDataSpaceValue.getRemoteObject();
        return remoteObject.getAbsoluteName();
    }

    /**
     * Implements {@link IPinableView#getPinProperties()}
     */
    public Map<String, String> getPinProperties() {
        return pinProperties;
    }

    @Override
    public void dispose() {

        if (autoRefreshJob != null) {
            autoRefreshJob.cancel();
        }

        getViewManager().remove(this);

        super.dispose();
    }

    protected abstract IViewManager getViewManager();

    protected abstract AbstractDropRemoteObjectListerner createDropListener(IDialogView editor);

    protected abstract AbstractWrappedDataSpace createDataSpaceWrapper(RemoteObject remoteObject) throws Exception;

    protected abstract void createControlPopupMenu(WatchItemManager watchManager, Composite dialogEditor, Control control);

    protected abstract void createControlDecorator(Control control);

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
        private DEditor dEditor;
        private boolean refreshViewData;

        public LoadAsyncDataJob(String name, RemoteObject remoteObject) {
            this(name, remoteObject, null);
            this.refreshViewData = true;
        }

        public LoadAsyncDataJob(String name, RemoteObject remoteObject, DEditor dEditor) {
            super(name);
            this.remoteObject = remoteObject;
            this.dEditor = dEditor;
            this.refreshViewData = false;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {

            try {

                AbstractWrappedDataSpace dataSpace = createDataSpaceWrapper(remoteObject);
                if (dataSpace == null) {
                    MessageDialogAsync.displayError(
                        getShell(),
                        Messages.bind(Messages.Object_A_of_type_C_in_Library_B_not_found_or_does_no_longer_exist,
                            new String[] { remoteObject.getName(), remoteObject.getLibrary(), remoteObject.getObjectType() }));
                    return Status.OK_STATUS;
                }

                /*
                 * Now that we know the type of the data area (= dataSpace), we
                 * can generate an editor on-the-fly if we do not yet have one.
                 */
                if (this.dEditor == null && !this.refreshViewData) {
                    this.dEditor = AbstractDataSpaceMonitorView.this.manager.generateEditor(dataSpace);
                }

                /*
                 * Create a UI job to update the view with the new data.
                 */
                UIJob updateDataUIJob = new UpdateDataUIJob(getShell().getDisplay(), getName(), createDataSpaceValue(dataSpace), this.dEditor);
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

        private DDataSpaceValue dataSpaceValue;
        private DEditor selectedEditor;
        private IJobFinishedListener finishedListener;

        public UpdateDataUIJob(Display jobDisplay, String name, DDataSpaceValue dataSpaceValue, DEditor selectedEditor) {
            super(jobDisplay, name);
            this.dataSpaceValue = dataSpaceValue;
            this.selectedEditor = selectedEditor;

        }

        public void setJobFinishedListener(IJobFinishedListener listener) {
            this.finishedListener = listener;
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor monitor) {

            if (selectedEditor != null) {
                replaceDataSpaceEditor(this.selectedEditor, this.dataSpaceValue);
            } else {
                updateDataSpaceEditorLabels(this.dataSpaceValue);
            }

            copyDataToControls(this.dataSpaceValue);
            refreshEditor();
            updatePinProperties();

            if (finishedListener != null) {
                finishedListener.jobFinished(this);
            }

            return Status.OK_STATUS;
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

            pinViewAction.setChecked(false);

            IViewManager viewManager = getViewManager();
            if (!viewManager.isInitialized(5000)) {
                ISpherePlugin.logError("Could not restore view. View manager did not initialize within 5 seconds.", null); //$NON-NLS-1$
                return Status.OK_STATUS;
            }

            pinProperties = viewManager.getPinProperties(AbstractDataSpaceMonitorView.this, pinKeys);

            String connectionName = pinProperties.get(CONNECTION_NAME);
            String name = pinProperties.get(OBJECT);
            String library = pinProperties.get(LIBRARY);
            String objectType = pinProperties.get(OBJECT_TYPE);
            String description = pinProperties.get(DESCRIPTION);
            String editorName = pinProperties.get(EDITOR);

            if (connectionName == null || name == null || library == null || objectType == null || editorName == null) {
                return Status.OK_STATUS;
            }

            RemoteObject remoteObject = new RemoteObject(connectionName, name, library, objectType, description);
            setData(new RemoteObject[] { remoteObject }, editorName);

            pinViewAction.setChecked(true);

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
            super(remoteObject.getQualifiedObject());
            this.jobFinishedListener = listener;
            this.remoteObject = remoteObject;
            setInterval(seconds);
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {

            final int SLEEP_INTERVAL = 50;

            while (!monitor.isCanceled()) {

                try {

                    AbstractWrappedDataSpace dataSpace = createDataSpaceWrapper(remoteObject);
                    if (dataSpace == null) {
                        MessageDialogAsync.displayError(
                            getShell(),
                            Messages.bind(Messages.Object_A_of_type_C_in_Library_B_not_found_or_does_no_longer_exist,
                                new String[] { remoteObject.getName(), remoteObject.getLibrary(), remoteObject.getObjectType() }));
                        monitor.setCanceled(true);
                    } else {
                        /*
                         * Create a UI job to update the view with the new data.
                         */
                        updateDataUIJob = new UpdateDataUIJob(getShell().getDisplay(), getName(), createDataSpaceValue(dataSpace), null);
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
