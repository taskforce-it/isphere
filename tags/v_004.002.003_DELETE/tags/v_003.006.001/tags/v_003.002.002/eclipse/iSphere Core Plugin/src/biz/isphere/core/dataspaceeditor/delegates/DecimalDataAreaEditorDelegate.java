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
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;

import biz.isphere.base.internal.ClipboardHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditor.AbstractDataSpaceEditor;
import biz.isphere.core.dataspaceeditor.StatusLine;
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
    private TextControlMouseMoveListener mouseMoveListener;

    private String statusMessage;

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
        dataAreaText.setMenu(new Menu(dataAreaText.getShell(), SWT.POP_UP));

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

        // Add 'status line/action' listener
        dataAreaText.addMouseListener(new TextControlMouseListener());

        mouseMoveListener = new TextControlMouseMoveListener(dataAreaText);
    }

    @Override
    public void setStatusMessage(String message) {
        statusMessage = message;

        updateStatusLine();
    }

    public void updateActionStatus() {

        updateActionStatus(ActionFactory.CUT.getId());
        updateActionStatus(ActionFactory.COPY.getId());
        updateActionStatus(ActionFactory.PASTE.getId());
        updateActionStatus(ActionFactory.DELETE.getId());
        updateActionStatus(ActionFactory.UNDO.getId());
        updateActionStatus(ActionFactory.REDO.getId());
        updateActionStatus(ActionFactory.SELECT_ALL.getId());

        IActionBars actionBars = getEditorSite().getActionBars();
        actionBars.updateActionBars();
    }

    public void updateActionStatus(String actionID) {

        IActionBars actionBars = getEditorSite().getActionBars();
        IAction action = actionBars.getGlobalActionHandler(actionID);
        if (action != null) {
            action.setEnabled(action.isEnabled());
        }
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
        statusLine.setShowMessage(true);

        statusLine.setMessage(statusMessage);
        statusMessage = null;
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
            setStatusMessage(Messages.Length_or_number_of_decimal_digits_on_value_not_valid);
            aMonitor.setCanceled(true);
            return;
        } else {
            setStatusMessage("");
        }

        Throwable exception = getWrappedDataSpace().setValue(new BigDecimal(dataAreaText.getText()));
        handleSaveResult(aMonitor, exception);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSelectionText() {
        return dataAreaText.getSelectionText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceSelection(String aText) {

        Point selection = dataAreaText.getSelection();
        int start = selection.x;
        int length = selection.y - selection.x;

        String text = dataAreaText.getText();
        text = delete(text, start, start + length);
        text = insert(text, start, aText);

        dataAreaText.setText(text);
    }

    private String delete(String aText, int aStart, int anEnd) {

        if (aStart == anEnd) {
            return aText;
        }

        aText = aText.substring(0, aStart) + aText.substring(anEnd);

        return aText;
    }

    private String insert(String text, int aStart, String aText) {
        if (aText.length() == 0) {
            return text;
        }
        text = text.substring(0, aStart) + aText + text.substring(aStart);
        return text;
    }

    @Override
    public void setInitialFocus() {
        dataAreaText.setFocus();
    }

    @Override
    public boolean canCut() {
        return hasSelection();
    }

    @Override
    public void doCut() {
        dataAreaText.cut();
    }

    public boolean canCopy() {
        return hasSelection();
    }

    public void doCopy() {
        dataAreaText.copy();
    }

    public boolean canPaste() {
        return ClipboardHelper.hasTextContents();
    }

    public void doPaste() {
        dataAreaText.paste();
    }

    @Override
    public boolean canDelete() {

        if (hasSelection()) {
            return true;
        } else if (dataAreaText.getSelection().y < dataAreaText.getText().length()) {
            return true;
        }

        return false;
    }

    @Override
    public void doDelete() {

        String text = dataAreaText.getText();

        Point selection = dataAreaText.getSelection();
        if (hasSelection()) {
            text = delete(text, selection.x, selection.y);
        } else {
            text = delete(text, selection.x, selection.x + 1);
        }

        dataAreaText.setText(text);

        dataAreaText.setSelection(selection.x);
    }

    @Override
    public boolean canSelectAll() {
        return true;
    }

    @Override
    public void doSelectAll() {
        dataAreaText.selectAll();
    }

    private boolean hasSelection() {
        if (dataAreaText.getSelectionCount() > 0) {
            return true;
        }
        return false;
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

                String previewText;
                if (event.text.length() > 1) {
                    previewText = event.text;
                } else {
                    previewText = currentText.substring(0, selection.x) + event.text + currentText.substring(selection.y);
                }

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
                // getStatusBar().setMessage(Messages.Length_or_number_of_decimal_digits_on_value_not_valid);
                statusMessage = Messages.Length_or_number_of_decimal_digits_on_value_not_valid;
            } else {
                // getStatusBar().setMessage("");
                statusMessage = null;
            }
            updateActionStatus();
            updateStatusLine();
        }

        public void keyPressed(KeyEvent event) {
            return;
        }
    }

    /**
     * Inner class that listens for mouse events in order to update the status
     * bar.
     */
    private class TextControlMouseListener extends MouseAdapter {
        @Override
        public void mouseDown(MouseEvent anEvent) {
            mouseMoveListener.start();
            updateActionStatus();
            updateStatusLine();
        }

        @Override
        public void mouseUp(MouseEvent anEvent) {
            updateActionStatus();
            updateStatusLine();
            mouseMoveListener.stop();
        }
    }

    /**
     * Inner class that listens for mouse events in order to update the status
     * bar.
     */
    private class TextControlMouseMoveListener implements MouseMoveListener {

        private Text control;
        private int lastSelectionCount;

        public TextControlMouseMoveListener(Text control) {
            this.control = control;
        }

        public void start() {
            lastSelectionCount = dataAreaText.getSelectionCount();
            control.addMouseMoveListener(this);
        }

        public void stop() {
            control.removeMouseMoveListener(this);
        }

        public void mouseMove(MouseEvent paramMouseEvent) {
            if (lastSelectionCount != dataAreaText.getSelectionCount()) {
                updateActionStatus();
                updateStatusLine();
                lastSelectionCount = dataAreaText.getSelectionCount();
            }
        }
    }
}
