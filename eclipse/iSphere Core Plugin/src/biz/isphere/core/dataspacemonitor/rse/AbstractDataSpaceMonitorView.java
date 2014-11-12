/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspacemonitor.rse;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
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
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspace.rse.AbstractWrappedDataSpace;
import biz.isphere.core.dataspace.rse.SelectDataSpaceEditor;
import biz.isphere.core.dataspaceeditor.listener.ControlBackgroundPainter;
import biz.isphere.core.dataspaceeditor.model.AbstractDWidget;
import biz.isphere.core.dataspaceeditor.model.DDataSpaceValue;
import biz.isphere.core.dataspaceeditor.model.DEditor;
import biz.isphere.core.dataspaceeditor.model.DataSpaceEditorManager;
import biz.isphere.core.dataspaceeditor.repository.DataSpaceEditorRepository;
import biz.isphere.core.dataspaceeditor.rse.AbstractDropDataObjectListerner;
import biz.isphere.core.dataspaceeditor.rse.IDialogView;
import biz.isphere.core.dataspaceeditor.rse.RemoteObject;
import biz.isphere.core.dataspacemonitor.rse.action.RefreshViewAction;
import biz.isphere.core.internal.ColorHelper;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.MessageDialogAsync;

public abstract class AbstractDataSpaceMonitorView extends ViewPart implements IDialogView {

    public static final String ID = "biz.isphere.rse.dataspacemonitor.rse.DataSpaceMonitorView";

    private DataSpaceEditorRepository repository;
    private DataSpaceEditorManager manager;

    private Composite mainArea;
    private Composite dataSpaceEditor;
    private DDataSpaceValue currentDataSpaceValue;

    private Label labelObject;
    private Label labelLibrary;
    private Label labeltype;

    private Action refreshViewAction;

    public AbstractDataSpaceMonitorView() {
        manager = new DataSpaceEditorManager();
        repository = DataSpaceEditorRepository.getInstance();
    }

    @Override
    public void createPartControl(Composite parent) {

        mainArea = new Composite(parent, SWT.NONE);
        mainArea.setLayout(createGridLayoutSimple());

        dataSpaceEditor = createDataSpaceEditor(mainArea, null);

        createActions();
        initializeToolBar();
    }

    private void createActions() {

        refreshViewAction = new RefreshViewAction(this);
        refreshViewAction.setToolTipText("Refresh the content of the display object");
        refreshViewAction.setImageDescriptor(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_REFRESH));
        refreshViewAction.setEnabled(false);
    }

    private void initializeToolBar() {

        IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
        toolbarManager.add(refreshViewAction);
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
    public void refreshDataSynchronously() {
        try {

            LoadAsyncDataJob loadDataJob = new LoadAsyncDataJob(Messages.Loading_remote_objects, currentDataSpaceValue.getRemoteObject(), null);
            loadDataJob.schedule();

        } catch (Exception e) {
            ISpherePlugin.logError(e.getMessage(), e);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
        }
    }

    public void setData(RemoteObject[] remoteObjects) {

        if (!checkInputData(remoteObjects)) {
            return;
        }

        RemoteObject remoteObject = remoteObjects[0];
        if (!checkRemoteObjectType(remoteObject)) {
            return;
        }

        DEditor selectedEditor = loadEditorForDataSpaceObject(getShell(), remoteObject);
        if (selectedEditor == null) {
            return;
        }

        /*
         * Submit long term process to batch. Let the process load the data and
         * pass it on to the UI update job.
         */
        LoadAsyncDataJob loadDataJob = new LoadAsyncDataJob(Messages.Loading_remote_objects, remoteObject, selectedEditor);
        loadDataJob.schedule();
    }

    protected boolean checkRemoteObjectType(RemoteObject remoteObject) {

        if (!(ISeries.DTAARA.equals(remoteObject.getObjectType()) || ISeries.USRSPC.equals(remoteObject.getObjectType()))) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                Messages.Only_character_data_areas_or_user_spaces_are_allowed_to_provide_sample_data);
            return false;
        }

        return true;
    }

    protected boolean checkInputData(RemoteObject[] remoteObjects) {

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

    /**
     * This method is intended to be called by batch jobs to set the data space
     * that is displayed in the view.
     * <p>
     * For example it is called by
     * {@link AbstractDropDataObjectListerner#setRemoteObjects(RemoteObject[])}.
     */
    public void setDataAsync(RemoteObject[] remoteObjects) {

        currentDataSpaceValue = null;

        /*
         * Create a UI job to check the input and to let the user select the
         * editor. Afterwards create a batch job, to load and set the data.
         */
        ReceiveAsyncDataUIJob reveiveDataUIJob = new ReceiveAsyncDataUIJob(getShell().getDisplay(), remoteObjects);
        reveiveDataUIJob.schedule();
    }

    private DDataSpaceValue createDataSpaceValue(AbstractWrappedDataSpace dataSpace) throws Exception {
        DDataSpaceValue dataSpaceValue = DDataSpaceValue.getCharacterInstance(dataSpace.getRemoteObject(), dataSpace.getCCSIDEncoding(),
            dataSpace.getBytes());
        return dataSpaceValue;
    }

    private void replaceDataSpaceEditor(Composite parent, DEditor dEditor, DDataSpaceValue dataSpaceValue) {
        Control[] controls = parent.getChildren();
        for (Control control : controls) {
            control.dispose();
        }

        createDataSpaceSpaceEditorLabels(parent, dataSpaceValue);
        dataSpaceEditor = createDataSpaceEditor(parent, dEditor);
    }

    protected void createDataSpaceSpaceEditorLabels(Composite parent, DDataSpaceValue dataSpaceValue) {
        Composite labelArea = new Composite(parent, SWT.NONE);
        GridLayout layout = createGridLayoutSimple(6);
        labelArea.setLayout(layout);

        labelObject = createLabel(labelArea, Messages.Object_colon, dataSpaceValue.getName());
        labelLibrary = createLabel(labelArea, Messages.Library_colon, dataSpaceValue.getLibrary());
        labeltype = createLabel(labelArea, Messages.Type_colon, dataSpaceValue.getObjectType());

        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData separatorLayoutData = createGridDataFillAndGrab(6);
        separator.setLayoutData(separatorLayoutData);
    }

    private void updateDataSpaceEditorLabels(DDataSpaceValue dataSpaceValue) {
        labelObject.setText(dataSpaceValue.getName());
        labelLibrary.setText(dataSpaceValue.getLibrary());
        labeltype.setText(dataSpaceValue.getObjectType());
    }

    private Label createLabel(Composite parent, String label, String value) {

        Label labelLabel = new Label(parent, SWT.NONE);
        labelLabel.setText(label);

        Label valueLabel = new Label(parent, SWT.NONE);
        valueLabel.setText(value);

        return valueLabel;
    }

    private DEditor loadEditorForDataSpaceObject(Shell shell, RemoteObject remoteObject) {

        DEditor[] dEditors = repository.getDataSpaceEditorsForObject(remoteObject);
        if (dEditors == null || dEditors.length == 0) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.No_matching_editor_found);
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

    private void copySampleDataToControls(DDataSpaceValue dataSpaceValue) {

        currentDataSpaceValue = dataSpaceValue;

        Control[] controls = dataSpaceEditor.getChildren();
        for (Control control : controls) {
            manager.setControlValue(control, dataSpaceValue);
        }
    }

    private void refreshEditor(Composite parentArea) {

        parentArea.layout();
        if (currentDataSpaceValue != null) {
            refreshViewAction.setEnabled(true);
        } else {
            refreshViewAction.setEnabled(false);
        }
    }

    private Composite createDataSpaceEditor(Composite parent, DEditor dEditor) {

        ScrolledComposite scrollableArea = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.NONE);
        scrollableArea.setLayout(new GridLayout(1, false));
        scrollableArea.setLayoutData(new GridData(GridData.FILL_BOTH));
        scrollableArea.setExpandHorizontal(true);
        scrollableArea.setExpandVertical(true);

        int numColumns;
        if (dEditor == null) {
            numColumns = 2;
        } else {
            numColumns = dEditor.getColumns() * 2;
        }
        Composite dialogEditor = manager.createDialogArea(scrollableArea, numColumns);

        if (dEditor != null) {
            AbstractDWidget[] widgets = dEditor.getWidgets();
            Color color = ColorHelper.getBackgroundColorOfSelectedControls();
            for (AbstractDWidget widget : widgets) {
                Control control = manager.createReadOnlyWidgetControlAndAddToParent(dialogEditor, widget);
                if (control instanceof Text) {
                    control.addMouseTrackListener(new ControlBackgroundPainter(color));
                } else if (control instanceof Button) {
                    control.addMouseTrackListener(new ControlBackgroundPainter(color));
                }

                createControlDecorator(control);
                createControlPopupMenu(dialogEditor, control);
            }

            Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
            GridData separatorLayoutData = createGridDataFillAndGrab(numColumns);
            separator.setLayoutData(separatorLayoutData);
            
            Label watchInfo = new Label(parent, SWT.NONE);
            watchInfo.setText("Use the context menu to watch an item.");
            GridData watchInfoLayoutData = createGridDataFillAndGrab(numColumns);
            watchInfo.setLayoutData(watchInfoLayoutData);
        } else {

            Label dragDropInfo = new Label(dialogEditor, SWT.NONE);
            dragDropInfo.setText("Drag && drop character data area or user space from RSE tree");
            dragDropInfo.setLayoutData(createGridDataFillAndGrab(numColumns));
        }

        dialogEditor.layout();
        scrollableArea.setContent(dialogEditor);
        scrollableArea.setMinSize(dialogEditor.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        addDropSupportOnComposite(dialogEditor);

        return dialogEditor;
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
        layout.marginHeight = 0;
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

    protected abstract AbstractDropDataObjectListerner createDropListener(IDialogView editor);

    protected abstract AbstractWrappedDataSpace createDataSpaceWrapper(RemoteObject remoteObject) throws Exception;

    protected abstract void createControlPopupMenu(Composite dialogEditor, Control control);

    protected abstract void createControlDecorator(Control control);

    /**
     * Job, that receives data that has been passed by
     * {@link IDialogView#setData}. It performs error checking on the UI thread
     * and then passed the remote object and the selected editor to the next
     * job.
     * <p>
     * It is the first job in a series of three.
     */
    private class ReceiveAsyncDataUIJob extends UIJob {

        private RemoteObject[] remoteObjects;

        public ReceiveAsyncDataUIJob(Display jobDisplay, RemoteObject[] remoteObjects) {
            super(jobDisplay, Messages.Loading_remote_objects);
            this.remoteObjects = remoteObjects;
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor monitor) {

            if (!checkInputData(remoteObjects)) {
                return Status.OK_STATUS;
            }

            RemoteObject remoteObject = remoteObjects[0];
            if (!checkRemoteObjectType(remoteObject)) {
                return Status.OK_STATUS;
            }

            DEditor selectedEditor = loadEditorForDataSpaceObject(getShell(), remoteObject);
            if (selectedEditor == null) {
                return Status.OK_STATUS;
            }

            /*
             * Submit long term process to batch. Let the process load the data
             * and pass it on to the UI update job.
             */
            LoadAsyncDataJob loadDataJob = new LoadAsyncDataJob(getName(), remoteObject, selectedEditor);
            loadDataJob.schedule();

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

        public LoadAsyncDataJob(String name, RemoteObject remoteObject, DEditor dEditor) {
            super(name);
            this.remoteObject = remoteObject;
            this.dEditor = dEditor;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {

            try {

                AbstractWrappedDataSpace dataSpace = createDataSpaceWrapper(remoteObject);
                if (ISeries.DTAARA.equals(remoteObject.getObjectType())) {
                    if (!AbstractWrappedDataSpace.CHARACTER.equals(dataSpace.getDataType())) {
                        MessageDialogAsync.displayError(getShell(),
                            Messages.Only_character_data_areas_or_user_spaces_are_allowed_to_provide_sample_data);
                        return Status.OK_STATUS;
                    }
                }

                /*
                 * Create a UI job to update the view with the new data.
                 */
                UIJob updateDataUIJob = new UpdateDataSpaceDataUIJob(getShell().getDisplay(), getName(), createDataSpaceValue(dataSpace), dEditor);
                updateDataUIJob.schedule();

            } catch (Exception e) {
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
    private class UpdateDataSpaceDataUIJob extends UIJob {

        private DEditor selectedEditor;
        DDataSpaceValue dataSpaceValue;

        public UpdateDataSpaceDataUIJob(Display jobDisplay, String name, DDataSpaceValue dataSpaceValue, DEditor selectedEditor) {
            super(jobDisplay, name);
            this.selectedEditor = selectedEditor;
            this.dataSpaceValue = dataSpaceValue;
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor monitor) {

            if (selectedEditor != null) {
                replaceDataSpaceEditor(mainArea, selectedEditor, dataSpaceValue);
            } else {
                updateDataSpaceEditorLabels(dataSpaceValue);
            }

            copySampleDataToControls(dataSpaceValue);
            refreshEditor(mainArea);

            return Status.OK_STATUS;
        }
    }
}
