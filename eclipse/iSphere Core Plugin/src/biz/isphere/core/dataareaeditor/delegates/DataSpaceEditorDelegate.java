/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataareaeditor.delegates;

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
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataareaeditor.AbstractDataAreaEditor;
import biz.isphere.core.dataspace.rse.AbstractWrappedDataSpace;
import biz.isphere.core.dataspaceeditordesigner.listener.DataModifiedEvent;
import biz.isphere.core.dataspaceeditordesigner.listener.IWidgetModifyListener;
import biz.isphere.core.dataspaceeditordesigner.model.AbstractDWidget;
import biz.isphere.core.dataspaceeditordesigner.model.DDataSpaceValue;
import biz.isphere.core.dataspaceeditordesigner.model.DEditor;
import biz.isphere.core.dataspaceeditordesigner.model.DataSpaceEditorManager;

/**
 * Editor delegate that edits a data space using a provided editor.
 * <p>
 * The delegate implements all the specific stuff that is needed to edit a data
 * area of type *LGL.
 */
public class DataSpaceEditorDelegate extends AbstractDataAreaEditorDelegate implements IWidgetModifyListener {

    private Shell shell;
    private DEditor dEditor;

    private DataSpaceEditorManager manager;
    private List<Control> controls;
    private DDataSpaceValue dataSpaceValue;

    public DataSpaceEditorDelegate(AbstractDataAreaEditor aDataAreaEditor, DEditor dEditor) {
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

        int numColumns;
        if (dEditor == null) {
            numColumns = 2;
        } else {
            numColumns = dEditor.getColumns() * 2;
        }
        Composite dialogEditor = manager.createDialogArea(scrollableArea, numColumns);
        GridLayout dialogEditorLayout = (GridLayout)dialogEditor.getLayout();
        dialogEditorLayout.marginLeft = 0;

        AbstractDWidget[] widgets = dEditor.getWidgets();
        controls = new ArrayList<Control>();
        for (AbstractDWidget widget : widgets) {
            Control control = manager.createWidgetControlAndAddToParent(dialogEditor, widget);
            controls.add(control);
        }

        dialogEditor.layout();
        scrollableArea.setContent(dialogEditor);
        scrollableArea.setMinSize(dialogEditor.computeSize(SWT.DEFAULT, SWT.DEFAULT));

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
            ISpherePlugin.logError(exception.getMessage(), exception);
            MessageDialog.openError(shell, Messages.E_R_R_O_R, exception.getLocalizedMessage());
        }
    }

    @Override
    public void setInitialFocus() {
        // nothing to do here
    }

    public void dataModified(DataModifiedEvent event) {
        setEditorDirty();
    }

}
