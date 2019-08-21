/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefileeditor;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.DialogActionTypes;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class FieldFormatDetail {

    private int actionType;
    private FieldFormat _fieldFormat;
    private Combo comboType;
    private Button buttonVLNo;
    private Button buttonVLYes;
    private Text textLength;
    private Text textDecimalPositions;
    private Text textBytes;
    private Label labelLength;
    private Label labelDecimalPositions;
    private Label labelBytes;
    private Validator validatorType;
    private Validator validatorLength;
    private Validator validatorDecimalPositions;
    private Validator validatorBytes;
    private StatusLineManager statusLineManager;

    public FieldFormatDetail(int actionType, FieldFormat _fieldFormat) {
        this.actionType = actionType;
        this._fieldFormat = _fieldFormat;
    }

    /**
     * @wbp.parser.entryPoint
     */
    public void createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());

        // Action

        final Label labelAction = new Label(container, SWT.CENTER | SWT.BORDER);
        labelAction.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        labelAction.setText(DialogActionTypes.getText(actionType));
        labelAction.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        if (actionType == DialogActionTypes.DELETE) {
            labelAction.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        } else {
            labelAction.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
        }

        // Header

        final Composite compositeHeader = new Composite(container, SWT.NONE);
        compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        final GridLayout gridLayoutCompositeHeader = new GridLayout();
        gridLayoutCompositeHeader.numColumns = 2;
        compositeHeader.setLayout(gridLayoutCompositeHeader);

        // Type

        final Label labelType = new Label(compositeHeader, SWT.NONE);
        labelType.setText(Messages.Type_colon);

        comboType = WidgetFactory.createCombo(compositeHeader);
        comboType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboType.setTextLimit(10);
        comboType.add(FieldFormat.QTDCHAR);
        comboType.add(FieldFormat.CHAR);
        comboType.add(FieldFormat.HEX);
        comboType.add(FieldFormat.SPP);
        comboType.add(FieldFormat.DEC);
        comboType.add(FieldFormat.BIN);
        comboType.add(FieldFormat.UBIN);
        comboType.add(FieldFormat.CCHAR);
        comboType.add(FieldFormat.DTS);
        comboType.add(FieldFormat.SYP);
        comboType.add(FieldFormat.ITV);
        if (actionType == DialogActionTypes.CREATE) {
            comboType.setText("");
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.COPY || actionType == DialogActionTypes.DELETE
            || actionType == DialogActionTypes.DISPLAY) {
            comboType.setText(_fieldFormat.getType());
        }
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            comboType.setEnabled(false);
        }

        validatorType = Validator.getCharInstance();
        validatorType.setLength(10);
        validatorType.setRestricted(true);
        validatorType.addSpecialValue(FieldFormat.QTDCHAR);
        validatorType.addSpecialValue(FieldFormat.CHAR);
        validatorType.addSpecialValue(FieldFormat.HEX);
        validatorType.addSpecialValue(FieldFormat.SPP);
        validatorType.addSpecialValue(FieldFormat.DEC);
        validatorType.addSpecialValue(FieldFormat.BIN);
        validatorType.addSpecialValue(FieldFormat.UBIN);
        validatorType.addSpecialValue(FieldFormat.CCHAR);
        validatorType.addSpecialValue(FieldFormat.DTS);
        validatorType.addSpecialValue(FieldFormat.SYP);
        validatorType.addSpecialValue(FieldFormat.ITV);

        // Variable length

        final Label labelVary = new Label(compositeHeader, SWT.NONE);
        labelVary.setText(Messages.Variable_length_colon);

        Composite compositeVary = new Composite(compositeHeader, SWT.NONE);
        compositeVary.setLayout(new GridLayout(2, false));
        compositeVary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        buttonVLNo = WidgetFactory.createRadioButton(compositeVary);
        buttonVLNo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                setVisible(false);
            }
        });
        buttonVLNo.setText(Messages.No);

        buttonVLYes = WidgetFactory.createRadioButton(compositeVary);
        buttonVLYes.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                setVisible(true);
            }
        });
        buttonVLYes.setText(Messages.Yes);

        if (actionType == DialogActionTypes.CREATE) {
            buttonVLNo.setSelection(true);
            buttonVLYes.setSelection(false);
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.COPY || actionType == DialogActionTypes.DELETE
            || actionType == DialogActionTypes.DISPLAY) {
            if (_fieldFormat.isVary()) {
                buttonVLNo.setSelection(false);
                buttonVLYes.setSelection(true);
            } else {
                buttonVLNo.setSelection(true);
                buttonVLYes.setSelection(false);
            }
        }
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            buttonVLNo.setEnabled(false);
            buttonVLYes.setEnabled(false);
        }

        // Length

        labelLength = new Label(compositeHeader, SWT.NONE);
        labelLength.setText(Messages.Length_colon);

        textLength = WidgetFactory.createIntegerText(compositeHeader);
        textLength.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textLength.setTextLimit(5);
        if (actionType == DialogActionTypes.CREATE) {
            textLength.setText("");
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.COPY || actionType == DialogActionTypes.DELETE
            || actionType == DialogActionTypes.DISPLAY) {
            textLength.setText(Integer.toString(_fieldFormat.getLength()));
        }
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textLength.setEnabled(false);
        }

        validatorLength = Validator.getIntegerInstance(5);

        // Decimal positions

        labelDecimalPositions = new Label(compositeHeader, SWT.NONE);
        labelDecimalPositions.setText(Messages.Decimal_positions_colon);

        textDecimalPositions = WidgetFactory.createIntegerText(compositeHeader);
        textDecimalPositions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textDecimalPositions.setTextLimit(2);
        if (actionType == DialogActionTypes.CREATE) {
            textDecimalPositions.setText("");
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.COPY || actionType == DialogActionTypes.DELETE
            || actionType == DialogActionTypes.DISPLAY) {
            if (_fieldFormat.getType().equals(FieldFormat.DEC)) {
                textDecimalPositions.setText(Integer.toString(_fieldFormat.getDecimalPositions()));
            } else {
                textDecimalPositions.setText("");
            }
        }
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textDecimalPositions.setEnabled(false);
        }

        validatorDecimalPositions = Validator.getIntegerInstance(2);

        // Bytes

        labelBytes = new Label(compositeHeader, SWT.NONE);
        labelBytes.setText(Messages.Bytes_colon);

        textBytes = WidgetFactory.createIntegerText(compositeHeader);
        textBytes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textBytes.setTextLimit(1);
        if (actionType == DialogActionTypes.CREATE) {
            textBytes.setText("");
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.COPY || actionType == DialogActionTypes.DELETE
            || actionType == DialogActionTypes.DISPLAY) {
            textBytes.setText(Integer.toString(_fieldFormat.getBytes()));
        }
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textBytes.setEnabled(false);
        }

        validatorBytes = Validator.getIntegerInstance(1);

        // Option

        if (actionType == DialogActionTypes.CREATE) {
            setVisible(false);
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.COPY || actionType == DialogActionTypes.DELETE
            || actionType == DialogActionTypes.DISPLAY) {
            if (_fieldFormat.isVary()) {
                setVisible(true);
            } else {
                setVisible(false);
            }
        }

        // Status line

        statusLineManager = new StatusLineManager();
        statusLineManager.createControl(container, SWT.NONE);
        Control statusLine = statusLineManager.getControl();
        final GridData gridDataStatusLine = new GridData(SWT.FILL, SWT.CENTER, true, false);
        statusLine.setLayoutData(gridDataStatusLine);

        // Set focus

        comboType.setFocus();

    }

    protected void setErrorMessage(String errorMessage) {
        if (errorMessage != null) {
            statusLineManager.setErrorMessage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_ERROR), errorMessage);
        } else {
            statusLineManager.setErrorMessage(null, null);
        }
    }

    private void setVisible(boolean vary) {

        if (vary) {

            labelLength.setVisible(false);
            textLength.setVisible(false);
            labelDecimalPositions.setVisible(false);
            textDecimalPositions.setVisible(false);
            labelBytes.setVisible(true);
            textBytes.setVisible(true);

        } else {

            labelLength.setVisible(true);
            textLength.setVisible(true);
            labelDecimalPositions.setVisible(true);
            textDecimalPositions.setVisible(true);
            labelBytes.setVisible(false);
            textBytes.setVisible(false);

        }

    }

    public boolean processButtonPressed() {
        switch (actionType) {
        case DialogActionTypes.CREATE: {
            convertData();
            if (checkData()) {
                transferData();
                return true;
            }
            return false;
        }
        case DialogActionTypes.CHANGE: {
            convertData();
            if (checkData()) {
                transferData();
                return true;
            }
            return false;
        }
        case DialogActionTypes.COPY: {
            convertData();
            if (checkData()) {
                transferData();
                return true;
            }
            return false;
        }
        case DialogActionTypes.DELETE: {
            return true;
        }
        case DialogActionTypes.DISPLAY: {
            return true;
        }
        }
        return false;
    }

    protected void convertData() {
        comboType.setText(comboType.getText().toUpperCase().trim());
    }

    protected boolean checkData() {

        // The value in field 'Type' is not valid.

        if (!validatorType.validate(comboType.getText())) {
            setErrorMessage(Messages.The_value_in_field_Type_is_not_valid);
            comboType.setFocus();
            return false;
        }

        if (buttonVLNo.getSelection()) {

            // The value in field 'Length' is not valid.

            if (!validatorLength.validate(textLength.getText()) || validatorLength.getIntegerValue() == 0) {
                setErrorMessage(Messages.The_value_in_field_Length_is_not_valid);
                textLength.setFocus();
                return false;
            }

            if (comboType.getText().equals(FieldFormat.DEC)) {

                // The value in field 'Decimal positions' is not valid.

                if (!validatorDecimalPositions.validate(textDecimalPositions.getText())) {
                    setErrorMessage(Messages.The_value_in_field_Decimal_positions_is_not_valid);
                    textDecimalPositions.setFocus();
                    return false;
                }

            } else {

                // Decimal positions may only be specified, if type is *DEC.

                if (!textDecimalPositions.getText().equals("")) {
                    setErrorMessage(Messages.Decimal_positions_may_only_be_specified_if_type_is_DEC);
                    textDecimalPositions.setFocus();
                    return false;
                }

            }

        }

        if (buttonVLYes.getSelection()) {

            // The value in field 'Bytes' is not valid.

            if (!validatorBytes.validate(textBytes.getText())) {
                setErrorMessage(Messages.The_value_in_field_Bytes_is_not_valid);
                textBytes.setFocus();
                return false;
            }

            // Bytes have to be 2 or 4.

            if (validatorBytes.getIntegerValue() != 2 && validatorBytes.getIntegerValue() != 4) {
                setErrorMessage(Messages.Bytes_have_to_be_2_or_4);
                textBytes.setFocus();
                return false;
            }

        }

        // Everything is alright

        return true;
    }

    protected void transferData() {
        _fieldFormat.setType(comboType.getText());
        _fieldFormat.setLength(0);
        _fieldFormat.setDecimalPositions(0);
        _fieldFormat.setBytes(0);
        if (buttonVLNo.getSelection()) {
            _fieldFormat.setLength(validatorLength.getIntegerValue());
            _fieldFormat.setDecimalPositions(validatorDecimalPositions.getIntegerValue());
            _fieldFormat.setVary(false);
        }
        if (buttonVLYes.getSelection()) {
            _fieldFormat.setBytes(validatorBytes.getIntegerValue());
            _fieldFormat.setVary(true);
        }
    }
}