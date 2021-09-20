/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.bindingdirectoryeditor;

import java.util.ArrayList;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.DialogActionTypes;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class BindingDirectoryEntryDetail {

    private String level;
    private int actionType;
    private BindingDirectoryEntry _bindingDirectoryEntry;
    private ArrayList<BindingDirectoryEntry> _bindingDirectoryEntries;
    private int ccsid;
    private Combo comboLibrary;
    private Text textObject;
    private Combo comboObjectType;
    private Combo comboActivation;
    private Validator validatorLibrary;
    private Validator validatorObject;
    private Validator validatorObjectType;
    private Validator validatorActivation;
    private StatusLineManager statusLineManager;

    public BindingDirectoryEntryDetail(String level, int actionType, BindingDirectoryEntry _bindingDirectoryEntry,
        ArrayList<BindingDirectoryEntry> _bindingDirectoryEntries, int ccsid) {
        this.level = level;
        this.actionType = actionType;
        this._bindingDirectoryEntry = _bindingDirectoryEntry;
        this._bindingDirectoryEntries = _bindingDirectoryEntries;
        this.ccsid = ccsid;
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

        // Library

        final Label labelLibrary = new Label(compositeHeader, SWT.NONE);
        labelLibrary.setText(Messages.Library_colon);

        comboLibrary = WidgetFactory.createUpperCaseCombo(compositeHeader);
        comboLibrary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboLibrary.setTextLimit(10);
        comboLibrary.add("*LIBL");
        if (actionType == DialogActionTypes.CREATE) {
            comboLibrary.setText("*LIBL");
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.COPY || actionType == DialogActionTypes.DELETE
            || actionType == DialogActionTypes.DISPLAY) {
            comboLibrary.setText(_bindingDirectoryEntry.getLibrary());
        }
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            comboLibrary.setEnabled(false);
        }

        // TODO: fix library name validator (pass CCSID) - DONE
        validatorLibrary = Validator.getLibraryNameInstance(ccsid, ISeries.SPCVAL_LIBL);

        // Object

        final Label labelObject = new Label(compositeHeader, SWT.NONE);
        labelObject.setText(Messages.Object_colon);

        textObject = WidgetFactory.createUpperCaseText(compositeHeader);
        textObject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textObject.setTextLimit(10);
        if (actionType == DialogActionTypes.CREATE) {
            textObject.setText("");
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.COPY || actionType == DialogActionTypes.DELETE
            || actionType == DialogActionTypes.DISPLAY) {
            textObject.setText(_bindingDirectoryEntry.getObject());
        }
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textObject.setEnabled(false);
        }

        // TODO: fix name validator (pass CCSID) - DONE
        validatorObject = Validator.getNameInstance(ccsid);

        // Object type

        final Label labelObjectType = new Label(compositeHeader, SWT.NONE);
        labelObjectType.setText(Messages.Object_type_colon);

        comboObjectType = WidgetFactory.createReadOnlyCombo(compositeHeader);
        comboObjectType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboObjectType.setTextLimit(10);
        comboObjectType.add("*SRVPGM");
        comboObjectType.add("*MODULE");
        if (actionType == DialogActionTypes.CREATE) {
            comboObjectType.select(0);
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.COPY || actionType == DialogActionTypes.DELETE
            || actionType == DialogActionTypes.DISPLAY) {
            comboObjectType.setText(_bindingDirectoryEntry.getObjectType());
        }
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            comboObjectType.setEnabled(false);
        }
        if (level.compareTo("V6R1M0") >= 0) {

            comboObjectType.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent event) {
                    if (comboObjectType.getText().toUpperCase().trim().equals("*SRVPGM")) {
                        comboActivation.setEnabled(true);
                        if (comboActivation.getText().trim().length() == 0) {
                            comboActivation.setText("*IMMED");
                        }
                    } else {
                        comboActivation.setText("");
                        comboActivation.setEnabled(false);
                    }
                }

                public void widgetDefaultSelected(SelectionEvent event) {
                    widgetSelected(event);
                }
            });
        }

        validatorObjectType = Validator.getCharInstance();
        validatorObjectType.setLength(10);
        validatorObjectType.setRestricted(true);
        validatorObjectType.addSpecialValue("*SRVPGM");
        validatorObjectType.addSpecialValue("*MODULE");

        // Activation

        if (level.compareTo("V6R1M0") >= 0) {

            final Label labelActivation = new Label(compositeHeader, SWT.NONE);
            labelActivation.setText(Messages.Activation_colon);

            comboActivation = WidgetFactory.createReadOnlyCombo(compositeHeader);
            comboActivation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            comboActivation.setTextLimit(10);
            comboActivation.add("*IMMED");
            comboActivation.add("*DEFER");
            if (actionType == DialogActionTypes.CREATE) {
                comboActivation.select(0);
            } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.COPY || actionType == DialogActionTypes.DELETE
                || actionType == DialogActionTypes.DISPLAY) {
                if (comboObjectType.getText().toUpperCase().trim().equals("*SRVPGM")) {
                    comboActivation.setText(_bindingDirectoryEntry.getActivation());
                    comboActivation.setEnabled(true);
                } else {
                    comboActivation.setText("");
                    comboActivation.setEnabled(false);
                }
            }
            if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
                comboActivation.setEnabled(false);
            }

            validatorActivation = Validator.getCharInstance();
            validatorActivation.setLength(10);
            validatorActivation.setRestricted(true);
            validatorActivation.addSpecialValue("*IMMED");
            validatorActivation.addSpecialValue("*DEFER");

        }

        // Status line

        statusLineManager = new StatusLineManager();
        statusLineManager.createControl(container, SWT.NONE);
        Control statusLine = statusLineManager.getControl();
        final GridData gridDataStatusLine = new GridData(SWT.FILL, SWT.CENTER, true, false);
        statusLine.setLayoutData(gridDataStatusLine);

        // Set focus

        comboLibrary.setFocus();

    }

    protected void setErrorMessage(String errorMessage) {
        if (errorMessage != null) {
            statusLineManager.setErrorMessage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_ERROR), errorMessage);
        } else {
            statusLineManager.setErrorMessage(null, null);
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
        comboLibrary.setText(comboLibrary.getText().toUpperCase().trim());
        textObject.setText(textObject.getText().toUpperCase().trim());
        comboObjectType.setText(comboObjectType.getText().toUpperCase().trim());
        if (level.compareTo("V6R1M0") >= 0) {
            comboActivation.setText(comboActivation.getText().toUpperCase().trim());
        }
    }

    protected boolean checkData() {

        // The value in field 'Library' is not valid.

        if (!validatorLibrary.validate(comboLibrary.getText())) {
            setErrorMessage(Messages.The_value_in_field_Library_is_not_valid);
            comboLibrary.setFocus();
            return false;
        }

        // The value in field 'Object' is not valid.

        if (!validatorObject.validate(textObject.getText())) {
            setErrorMessage(Messages.The_value_in_field_Object_is_not_valid);
            textObject.setFocus();
            return false;
        }

        // The value in field 'Object type' is not valid.

        if (!validatorObjectType.validate(comboObjectType.getText())) {
            setErrorMessage(Messages.The_value_in_field_Object_type_is_not_valid);
            comboObjectType.setFocus();
            return false;
        }

        // The value in field 'Activation' is not valid.

        if (level.compareTo("V6R1M0") >= 0 && comboObjectType.getText().equals("*SRVPGM") && !validatorActivation.validate(comboActivation.getText())) {
            setErrorMessage(Messages.The_value_in_field_Activation_is_not_valid);
            comboActivation.setFocus();
            return false;
        }

        // The entry already exists.

        if (actionType == DialogActionTypes.CREATE
            || actionType == DialogActionTypes.COPY
            || (actionType == DialogActionTypes.CHANGE && (!_bindingDirectoryEntry.getLibrary().equals(comboLibrary.getText())
                || !_bindingDirectoryEntry.getObject().equals(textObject.getText()) || !_bindingDirectoryEntry.getObjectType().equals(
                comboObjectType.getText())))) {
            for (int idx = 0; idx < _bindingDirectoryEntries.size(); idx++) {
                BindingDirectoryEntry entry = _bindingDirectoryEntries.get(idx);
                if (entry.getLibrary().equals(comboLibrary.getText()) && entry.getObject().equals(textObject.getText())
                    && entry.getObjectType().equals(comboObjectType.getText())) {
                    setErrorMessage(Messages.The_entry_does_already_exist);
                    comboLibrary.setFocus();
                    return false;
                }
            }
        }

        // Everything is alright

        return true;
    }

    protected void transferData() {
        _bindingDirectoryEntry.setLibrary(comboLibrary.getText());
        _bindingDirectoryEntry.setObject(textObject.getText());
        _bindingDirectoryEntry.setObjectType(comboObjectType.getText());
        if (level.compareTo("V6R1M0") >= 0 && comboObjectType.getText().equals("*SRVPGM")) {
            _bindingDirectoryEntry.setActivation(comboActivation.getText());
        } else {
            _bindingDirectoryEntry.setActivation("");
        }
    }
}