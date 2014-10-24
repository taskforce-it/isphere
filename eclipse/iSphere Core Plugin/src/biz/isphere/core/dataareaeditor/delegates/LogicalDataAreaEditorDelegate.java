/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataareaeditor.delegates;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import biz.isphere.core.Messages;
import biz.isphere.core.dataareaeditor.DataAreaEditor;

/**
 * Editor delegate that edits a *LGL data area.
 * <p>
 * The delegate implements all the specific stuff that is needed to edit a data
 * area of type *LGL.
 */
public class LogicalDataAreaEditorDelegate extends AbstractDataAreaEditorDelegate {

    private Button dataAreaText;

    public LogicalDataAreaEditorDelegate(DataAreaEditor aDataAreaEditor) {
        super(aDataAreaEditor);
    }

    @Override
    public void createPartControl(Composite aParent) {

        Composite editorArea = createEditorArea(aParent, 2);

        Label valueLabel = new Label(editorArea, SWT.NONE);
        valueLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
        valueLabel.setText(Messages.Value_colon);

        dataAreaText = new Button(editorArea, SWT.CHECK);
        GridData dataAreaTextLayoutData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        dataAreaTextLayoutData.widthHint = 160;
        dataAreaText.setLayoutData(dataAreaTextLayoutData);

        Composite filler = new Composite(aParent, SWT.NONE);
        GridData fillerLayoutData = new GridData();
        fillerLayoutData.grabExcessHorizontalSpace = true;
        fillerLayoutData.grabExcessVerticalSpace = true;
        fillerLayoutData.horizontalSpan = 2;
        filler.setLayoutData(fillerLayoutData);

        // Set screen value
        dataAreaText.setSelection(getWrappedDataArea().getBooleanValue());

        // Add 'dirty' listener
        dataAreaText.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }

            public void widgetSelected(SelectionEvent arg0) {
                setEditorDirty();
            }
        });
    }

    @Override
    public void doSave(IProgressMonitor aMonitor) {
        Throwable exception = getWrappedDataArea().setValue(dataAreaText.getSelection());
        handleSaveException(aMonitor, exception);
    }

    @Override
    public void setInitialFocus() {
        dataAreaText.setFocus();
    }

}
