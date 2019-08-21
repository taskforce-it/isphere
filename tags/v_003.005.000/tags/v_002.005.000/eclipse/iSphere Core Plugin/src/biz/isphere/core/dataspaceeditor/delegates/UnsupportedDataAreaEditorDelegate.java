/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.delegates;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import biz.isphere.core.dataspace.rse.AbstractWrappedDataSpace;
import biz.isphere.core.dataspaceeditor.AbstractDataSpaceEditor;

/**
 * Fallback editor delegate that is used in case the is a unknown data area
 * type.
 */
public class UnsupportedDataAreaEditorDelegate extends AbstractDataSpaceEditorDelegate {

    private Composite editorArea;

    public UnsupportedDataAreaEditorDelegate(AbstractDataSpaceEditor aDataAreaEditor) {
        super(aDataAreaEditor);
    }

    @Override
    public void createPartControl(Composite aParent) {

        editorArea = createEditorArea(aParent, 1);

        Label info1 = new Label(editorArea, SWT.NONE);
        info1.setText("Unsupported data area type. Please contact the developer.");

        AbstractWrappedDataSpace dataArea = getWrappedDataSpace();

        Label info2 = new Label(editorArea, SWT.NONE);
        info2.setText(dataArea.toString());

        Composite filler = new Composite(aParent, SWT.NONE);
        GridData fillerLayoutData = new GridData();
        fillerLayoutData.grabExcessHorizontalSpace = true;
        fillerLayoutData.grabExcessVerticalSpace = true;
        fillerLayoutData.horizontalSpan = 2;
        filler.setLayoutData(fillerLayoutData);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(boolean isEnabled) {
        // not required
    }

    @Override
    public void doSave(IProgressMonitor aMonitor) {
    }

    @Override
    public void setInitialFocus() {
    }

}
