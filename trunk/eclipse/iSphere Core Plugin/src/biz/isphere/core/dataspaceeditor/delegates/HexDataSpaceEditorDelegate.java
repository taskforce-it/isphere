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
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditor.AbstractDataSpaceEditor;
import biz.isphere.core.swt.widgets.CaretEvent;
import biz.isphere.core.swt.widgets.CaretListener;
import biz.isphere.core.swt.widgets.HexEditor;

/**
 * Editor delegate that edits a *LGL data area.
 * <p>
 * The delegate implements all the specific stuff that is needed to edit a data
 * area of type *LGL.
 */
public class HexDataSpaceEditorDelegate extends AbstractDataSpaceEditorDelegate {

    private HexEditor dataAreaText;

    public HexDataSpaceEditorDelegate(AbstractDataSpaceEditor aDataAreaEditor) {
        super(aDataAreaEditor);
    }

    @Override
    public void createPartControl(Composite aParent) {

        ScrolledComposite editorAreaScrollable = new ScrolledComposite(aParent, SWT.H_SCROLL | SWT.NONE);
        editorAreaScrollable.setLayout(new GridLayout(1, false));
        editorAreaScrollable.setLayoutData(new GridData(GridData.FILL_BOTH));
        editorAreaScrollable.setExpandHorizontal(true);
        editorAreaScrollable.setExpandVertical(true);

        Composite editorArea = createEditorArea(editorAreaScrollable, 3);
        editorArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label lblValue = new Label(editorArea, SWT.NONE);
        GridData lblValueLayoutData = new GridData();
        lblValueLayoutData.widthHint = AbstractDataSpaceEditor.VALUE_LABEL_WIDTH_HINT;
        lblValueLayoutData.verticalAlignment = GridData.BEGINNING;
        lblValue.setLayoutData(lblValueLayoutData);
        lblValue.setText(Messages.Value_colon);

        Composite horizontalSpacer = new Composite(editorArea, SWT.NONE);
        GridData horizontalSpacerLayoutData = new GridData();
        horizontalSpacerLayoutData.widthHint = AbstractDataSpaceEditor.SPACER_WIDTH_HINT;
        horizontalSpacerLayoutData.heightHint = 1;
        horizontalSpacer.setLayoutData(horizontalSpacerLayoutData);

        dataAreaText = new HexEditor(editorArea, SWT.BORDER, 0, 16, 8);
        GridData dataAreaTextLayoutData = new GridData(SWT.BEGINNING, SWT.FILL, false, true);
        dataAreaText.setLayoutData(dataAreaTextLayoutData);

        Composite verticalSpacer = new Composite(editorAreaScrollable, SWT.NONE);
        GridData verticalSpacerLayoutData = new GridData();
        verticalSpacerLayoutData.grabExcessHorizontalSpace = true;
        verticalSpacerLayoutData.grabExcessVerticalSpace = true;
        verticalSpacerLayoutData.horizontalSpan = 2;
        verticalSpacer.setLayoutData(verticalSpacerLayoutData);

        editorAreaScrollable.setContent(editorArea);
        editorAreaScrollable.setMinSize(editorArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        // Set screen value
        try {
            dataAreaText.setByteData(getWrappedDataSpace().getBytes());
        } catch (Throwable e) {
        }

        // Add 'dirty' listener
        dataAreaText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent arg0) {
                setEditorDirty();
            }
        });

        dataAreaText.addCaretListener(new CaretListener() {
            public void caretMoved(CaretEvent event) {
                getStatusBar().setPosition((event.caretOffset / 2) + 1);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(boolean isEnabled) {
        dataAreaText.setEnabled(isEnabled);
    }

    @Override
    public void doSave(IProgressMonitor aMonitor) {
        try {
            getWrappedDataSpace().setBytes(dataAreaText.getByteData());
            handleSaveResult(aMonitor, null);
        } catch (Throwable e) {
            handleSaveResult(aMonitor, e);
        }
    }

    @Override
    public void setInitialFocus() {
        dataAreaText.setFocus();
    }

}
