/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.delegates;

import java.math.BigDecimal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditor.AbstractDataSpaceEditor;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.swt.widgets.WidgetFactory;

/**
 * Editor delegate that edits a *DEC data area.
 * <p>
 * The delegate implements all the specific stuff that is needed to edit a data
 * area of type *DEC.
 */
public class DecimalDataAreaEditorDelegate extends AbstractDataSpaceEditorDelegate {

    private Text dataAreaText;
    private Validator validator;

    public DecimalDataAreaEditorDelegate(AbstractDataSpaceEditor aDataAreaEditor) {
        super(aDataAreaEditor);
    }

    @Override
    public void createPartControl(Composite aParent) {

        Composite editorArea = createEditorArea(aParent, 3);

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

        if (getWrappedDataSpace().getDecimalPositions() != 0) {
            dataAreaText = WidgetFactory.createDecimalText(editorArea, true);
        } else {
            dataAreaText = WidgetFactory.createIntegerText(editorArea, true);
        }

        dataAreaText.setTextLimit(getWrappedDataSpace().getTextLimit());
        GridData dataAreaTextLayoutData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        dataAreaTextLayoutData.widthHint = 160;
        dataAreaText.setLayoutData(dataAreaTextLayoutData);

        Composite verticalSpacer = new Composite(aParent, SWT.NONE);
        GridData verticalSpacerLayoutData = new GridData();
        verticalSpacerLayoutData.grabExcessHorizontalSpace = true;
        verticalSpacerLayoutData.grabExcessVerticalSpace = true;
        verticalSpacerLayoutData.horizontalSpan = 2;
        verticalSpacer.setLayoutData(verticalSpacerLayoutData);

        validator = Validator.getDecimalInstance(getWrappedDataSpace().getLength(), getWrappedDataSpace().getDecimalPositions(), true);

        // Set screen value
        dataAreaText.setText(getWrappedDataSpace().getDecimalValue().toString());

        dataAreaText.addKeyListener(new TextControlKeyListener());

        // Add 'verify' listener
        dataAreaText.addVerifyListener(new TextControlVerifyListener());

        // Add 'dirty' listener
        dataAreaText.addModifyListener(new TextControlModifyListener());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(boolean isEnabled) {
        dataAreaText.setEditable(isEnabled);
    }

    @Override
    public void doSave(IProgressMonitor aMonitor) {
        if (!validator.validate(dataAreaText.getText())) {
            getStatusBar().setMessage(Messages.Length_or_number_of_decimal_digits_on_value_not_valid);
            aMonitor.setCanceled(true);
            return;
        } else {
            getStatusBar().setMessage("");
        }

        Throwable exception = getWrappedDataSpace().setValue(new BigDecimal(dataAreaText.getText()));
        handleSaveResult(aMonitor, exception);
    }

    @Override
    public void setInitialFocus() {
        dataAreaText.setFocus();
    }

    /**
     * Class, to ensure that the value does not contain German commas. German
     * commas (;) are converted to dot (.). Furthermore it is ensured that there
     * are not multiple commas.
     * <p>
     * If the resulting value will be illegal, the action is aborted.
     */
    private class TextControlVerifyListener implements VerifyListener {
        public void verifyText(VerifyEvent event) {
            if (!StringHelper.isNullOrEmpty(event.text)) {
                if (event.text.contains(",")) {
                    event.text = event.text.replaceFirst(",", ".");
                }

                Text widget = (Text)event.getSource();
                String currentText = widget.getText();
                Point selection = widget.getSelection();
                String previewText = currentText.substring(0, selection.x) + event.text + currentText.substring(selection.y);
                if (StringHelper.count(previewText, '.') > 1) {
                    event.doit = false;
                }
            }
        }
    }

    /**
     * Class, used as a 'dirty' listener.
     */
    private class TextControlModifyListener implements ModifyListener {
        public void modifyText(ModifyEvent anEvent) {
            setEditorDirty();
        }
    }

    /**
     * Class, to filter valid keystrokes.
     */
    private class TextControlKeyListener implements KeyListener {

        public void keyReleased(KeyEvent event) {
            Text widget = (Text)event.getSource();
            if (!validator.validate(widget.getText())) {
                getStatusBar().setMessage(Messages.Length_or_number_of_decimal_digits_on_value_not_valid);
            } else {
                getStatusBar().setMessage("");
            }
        }

        public void keyPressed(KeyEvent event) {
            return;
        }
    }

}
