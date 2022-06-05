/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.delegates;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspace.rse.AbstractWrappedDataSpace;
import biz.isphere.core.dataspaceeditor.AbstractDataSpaceEditor;
import biz.isphere.core.dataspaceeditor.StatusLine;
import biz.isphere.core.dataspaceeditordesigner.listener.DataModifiedEvent;
import biz.isphere.core.dataspaceeditordesigner.listener.IWidgetModifyListener;
import biz.isphere.core.dataspaceeditordesigner.model.AbstractDWidget;
import biz.isphere.core.dataspaceeditordesigner.model.DDataSpaceValue;
import biz.isphere.core.dataspaceeditordesigner.model.DEditor;
import biz.isphere.core.dataspaceeditordesigner.model.DataSpaceEditorManager;
import biz.isphere.core.internal.exception.ValueTooLargeException;

/**
 * Editor delegate that edits a data space using a provided editor.
 * <p>
 * The delegate implements all the specific stuff that is needed to edit a data
 * area of type *LGL.
 */
public class DataSpaceEditorDelegate extends AbstractDataSpaceEditorDelegate implements IWidgetModifyListener {

    private Shell shell;
    private DEditor dEditor;

    private DataSpaceEditorManager manager;
    private List<Control> controls;
    private DDataSpaceValue dataSpaceValue;
    private Composite dialogEditor;

    public DataSpaceEditorDelegate(AbstractDataSpaceEditor aDataAreaEditor, DEditor dEditor) {
        super(aDataAreaEditor);
        this.shell = aDataAreaEditor.getSite().getShell();
        this.dEditor = dEditor;
        this.manager = new DataSpaceEditorManager();
    }

    @Override
    public void createPartControl(Composite aParent) {

        Composite editorArea = createEditorArea(aParent, 3);
        GridLayout editorAreaLayout = (GridLayout)editorArea.getLayout();
        editorAreaLayout.marginLeft = 0;
        GridData editorAreaLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        editorArea.setLayoutData(editorAreaLayoutData);

        ScrolledComposite scrollableArea = new ScrolledComposite(editorArea, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        scrollableArea.setLayout(new GridLayout(1, false));
        scrollableArea.setLayoutData(new GridData(GridData.FILL_BOTH));
        scrollableArea.setExpandHorizontal(true);
        scrollableArea.setExpandVertical(true);

        int columnsPerEditorColumn = 2;
        dialogEditor = manager.createDialogArea(scrollableArea, dEditor, columnsPerEditorColumn);
        GridLayout dialogEditorLayout = (GridLayout)dialogEditor.getLayout();
        dialogEditorLayout.marginLeft = 0;

        controls = new ArrayList<Control>();
        if (dEditor == null) {

            Label errorLabel = new Label(dialogEditor, SWT.NONE);
            errorLabel.setText(Messages.No_editor_selected_Cannot_display_any_data);
        } else {

            AbstractDWidget[] widgets = dEditor.getWidgets();
            for (AbstractDWidget widget : widgets) {
                Control control = manager.createWidgetControlAndAddToParent(dialogEditor, columnsPerEditorColumn, widget);
                controls.add(control);
            }

            // Set screen value
            try {
                AbstractWrappedDataSpace dataSpace = getWrappedDataSpace();
                byte[] bytes = getWrappedDataSpace().getBytes();
                dataSpaceValue = DDataSpaceValue.getCharacterInstance(dataSpace.getRemoteObject(), dataSpace.getCCSIDEncoding(), bytes);
                for (Control control : controls) {
                    manager.setControlValue(control, dataSpaceValue);
                }
            } catch (Throwable e) {
                ISpherePlugin.logError(e.getMessage(), e);
                MessageDialog.openError(shell, Messages.E_R_R_O_R, e.getLocalizedMessage());
            }

            // Add 'dirty' listener
            for (Control control : controls) {
                manager.addControlModifyListener(control, this);
            }
        }

        dialogEditor.layout();
        scrollableArea.setContent(dialogEditor);
        scrollableArea.setMinSize(dialogEditor.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    @Override
    public void setStatusMessage(String message) {

    }

    /**
     * Updates the status line.
     */
    @Override
    public void updateStatusLine() {

        StatusLine statusLine = getStatusLine();
        if (statusLine == null) {
            return;
        }

        statusLine.setShowMode(false);
        statusLine.setShowPosition(false);
        statusLine.setShowValue(false);
        statusLine.setShowMessage(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(boolean isEnabled) {
        dialogEditor.setEnabled(isEnabled);
    }

    @Override
    public void doSave(IProgressMonitor aMonitor) {

        Throwable exception = null;
        for (Control control : controls) {
            exception = manager.updateDataSpaceFromControl(control, dataSpaceValue);
            if (exception != null) {
                break;
            }
        }

        if (exception == null) {
            try {
                getWrappedDataSpace().setBytes(dataSpaceValue.getBytes());
            } catch (Throwable e) {
                exception = e;
            }
        }

        handleSaveResult(aMonitor, exception);

        if (exception != null) {
            if (mustLogException(exception)) {
                ISpherePlugin.logError(exception.getMessage(), exception);
            }
            MessageDialog.openError(shell, Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(exception));
        }
    }

    private boolean mustLogException(Throwable exception) {

        if (exception instanceof ValueTooLargeException) {
            return false;
        }

        return true;
    }

    @Override
    public void setInitialFocus() {
        // nothing to do here
    }

    public void dataModified(DataModifiedEvent event) {
        setEditorDirty();
    }

}
