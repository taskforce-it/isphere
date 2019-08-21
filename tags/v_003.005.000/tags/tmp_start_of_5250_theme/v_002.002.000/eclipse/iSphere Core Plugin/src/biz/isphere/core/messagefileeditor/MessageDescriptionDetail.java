/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefileeditor;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.swt.widgets.NumericOnlyVerifyListener;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.DialogActionTypes;
import biz.isphere.core.internal.Validator;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.ErrorCompletingRequestException;

public class MessageDescriptionDetail {

    private static final String STATUS_BUTTON_HIDE_ADVANCED_OPTIONS = "status_buttonHideAdvancedOptions";

    private AS400 as400;
    private int actionType;
    private MessageDescription _messageDescription;
    private Text textMessageId;
    private Text textMessage;
    private CCombo comboHelpText;
    private Text textSeveriry;
    private CCombo comboCcsid;
    private Validator validatorMessageId;
    private Validator validatorMessage;
    private Validator validatorHelpText;
    private Validator validatorSeverity;
    private Validator validatorCcsid;
    private StatusLineManager statusLineManager;
    private FieldFormatViewer _fieldFormatViewer;
    private Composite compositeAdvancedOptions;
    private Button buttonHideAdvancedOptions;

    public MessageDescriptionDetail(AS400 as400, int actionType, MessageDescription _messageDescription) {
        this.as400 = as400;
        this.actionType = actionType;
        this._messageDescription = _messageDescription;
        if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            refresh(_messageDescription);
        }
    }

    /**
     * @wbp.parser.entryPoint
     */
    public void createContents(final Composite parent) {

        final Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());

        // Action

        final Label labelAction = new Label(container, SWT.CENTER | SWT.BORDER);
        labelAction.setLayoutData(getLayoutData());
        labelAction.setText(DialogActionTypes.getText(actionType));
        labelAction.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        if (actionType == DialogActionTypes.DELETE) {
            labelAction.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        } else {
            labelAction.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
        }

        // Header

        final Composite compositeHeader = new Composite(container, SWT.NONE);
        compositeHeader.setLayoutData(getLayoutData());
        final GridLayout gridLayoutCompositeHeader = new GridLayout();
        gridLayoutCompositeHeader.numColumns = 2;
        compositeHeader.setLayout(gridLayoutCompositeHeader);

        // Connection

        final Label labelConnection = new Label(compositeHeader, SWT.NONE);
        labelConnection.setText(Messages.Connection_colon);

        final Text textConnection = new Text(compositeHeader, SWT.BORDER);
        textConnection.setText(_messageDescription.getConnection());
        textConnection.setEditable(false);
        textConnection.setLayoutData(getLayoutData());

        // Library

        final Label labelLibrary = new Label(compositeHeader, SWT.NONE);
        labelLibrary.setText(Messages.Library_colon);

        final Text textLibrary = new Text(compositeHeader, SWT.BORDER);
        textLibrary.setText(_messageDescription.getLibrary());
        textLibrary.setEditable(false);
        textLibrary.setLayoutData(getLayoutData());

        // Message file

        final Label labelMessageFile = new Label(compositeHeader, SWT.NONE);
        labelMessageFile.setText(Messages.Message_file_colon);

        final Text textMessageFile = new Text(compositeHeader, SWT.BORDER);
        textMessageFile.setText(_messageDescription.getMessageFile());
        textMessageFile.setEditable(false);
        textMessageFile.setLayoutData(getLayoutData());

        // Message-Id.

        final Label labelMessageId = new Label(compositeHeader, SWT.NONE);
        labelMessageId.setText(Messages.Message_Id_colon);

        textMessageId = new Text(compositeHeader, SWT.BORDER);
        textMessageId.setLayoutData(getLayoutData());
        textMessageId.setTextLimit(7);
        if (actionType == DialogActionTypes.CREATE) {
            textMessageId.setText("");
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.COPY || actionType == DialogActionTypes.DELETE
            || actionType == DialogActionTypes.DISPLAY) {
            textMessageId.setText(_messageDescription.getMessageId());
        }
        if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textMessageId.setEditable(false);
        }

        validatorMessageId = new Validator();
        validatorMessageId.setType("*NAME");
        validatorMessageId.setLength(7);

        // Message

        final Label labelMessage = new Label(compositeHeader, SWT.NONE);
        labelMessage.setText(Messages.Message_colon);

        textMessage = new Text(compositeHeader, SWT.BORDER);
        textMessage.setLayoutData(getLayoutData());
        textMessage.setTextLimit(132);
        if (actionType == DialogActionTypes.CREATE) {
            textMessage.setText("");
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.COPY || actionType == DialogActionTypes.DELETE
            || actionType == DialogActionTypes.DISPLAY) {
            textMessage.setText(_messageDescription.getMessage());
        }
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textMessage.setEditable(false);
        }

        validatorMessage = new Validator();
        validatorMessage.setType("*CHAR");
        validatorMessage.setLength(132);

        // Helptext

        final Label labelHelpText = new Label(compositeHeader, SWT.NONE);
        labelHelpText.setText(Messages.Helptext_colon);

        comboHelpText = new CCombo(compositeHeader, SWT.BORDER);
        comboHelpText.setLayoutData(getLayoutData());
        comboHelpText.setTextLimit(3000);
        comboHelpText.add("*NONE");
        if (actionType == DialogActionTypes.CREATE) {
            comboHelpText.setText("*NONE");
        } else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.COPY || actionType == DialogActionTypes.DELETE
            || actionType == DialogActionTypes.DISPLAY) {
            comboHelpText.setText(_messageDescription.getHelpText());
        }
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            comboHelpText.setEditable(false);
        }

        validatorHelpText = new Validator();
        validatorHelpText.setType("*CHAR");
        validatorHelpText.setLength(3000);
        validatorHelpText.addSpecialValue("*NONE");

        // Field formats

        _fieldFormatViewer = new FieldFormatViewer(actionType, _messageDescription, textMessage, comboHelpText);
        _fieldFormatViewer.createContents(container);

        // Advanced options

        buttonHideAdvancedOptions = new Button(container, SWT.CHECK);
        buttonHideAdvancedOptions.setText(Messages.Advanced_options);
        buttonHideAdvancedOptions.setSelection(false);
        buttonHideAdvancedOptions.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                setAdvancedOptionsEnablement();
                buttonHideAdvancedOptions.getShell().pack();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
                return;
            }
        });

        compositeAdvancedOptions = new Composite(container, SWT.NONE);
        compositeAdvancedOptions.setLayoutData(getLayoutData());
        compositeAdvancedOptions.setLayout(new GridLayout(2, false));
        setAdvancedOptionsEnablement();

        GridData gridDataAdvancedOptions = getLayoutData();
        compositeAdvancedOptions.setLayoutData(gridDataAdvancedOptions);

        // Severity

        final Label labelSeverity = new Label(compositeAdvancedOptions, SWT.NONE);
        labelSeverity.setText(Messages.Severity_colon);

        textSeveriry = new Text(compositeAdvancedOptions, SWT.BORDER);
        textSeveriry.addVerifyListener(new NumericOnlyVerifyListener());
        textSeveriry.setLayoutData(getLayoutData(60));
        textSeveriry.setTextLimit(2);
        textSeveriry.setText(_messageDescription.getSeverity().toString());
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textSeveriry.setEditable(false);
        }

        validatorSeverity = new Validator();
        validatorSeverity.setType("*DEC");
        validatorSeverity.setLength(textSeveriry.getTextLimit());
        validatorSeverity.setPrecision(0);

        // Ccsid

        final Label labelCcsid = new Label(compositeAdvancedOptions, SWT.NONE);
        labelCcsid.setText(Messages.Ccsid_colon);

        comboCcsid = new CCombo(compositeAdvancedOptions, SWT.BORDER);
        comboCcsid.setLayoutData(getLayoutData(60));
        comboCcsid.setTextLimit(5);
        comboCcsid.add(MessageDescription.CCSID_JOB);
        comboCcsid.add(MessageDescription.CCSID_HEX);
        comboCcsid.setText(_messageDescription.getCcsidAsString());
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            comboCcsid.setEditable(false);
        }

        validatorCcsid = new Validator();
        validatorCcsid.setType("*DEC");
        validatorCcsid.setLength(textSeveriry.getTextLimit());
        validatorCcsid.setPrecision(0);
        validatorCcsid.addSpecialValue(MessageDescription.CCSID_JOB);
        validatorCcsid.addSpecialValue(MessageDescription.CCSID_HEX);

        // Status line

        statusLineManager = new StatusLineManager();
        statusLineManager.createControl(container, SWT.NONE);
        Control statusLine = statusLineManager.getControl();
        final GridData gridDataStatusLine = getLayoutData();
        statusLine.setLayoutData(gridDataStatusLine);

        // Set focus

        if (actionType == DialogActionTypes.CHANGE) {
            textMessage.setFocus();
        } else {
            textMessageId.setFocus();
        }

    }

    private void setAdvancedOptionsEnablement() {
        compositeAdvancedOptions.setVisible(buttonHideAdvancedOptions.getSelection());
        ((GridData)compositeAdvancedOptions.getLayoutData()).exclude = !buttonHideAdvancedOptions.getSelection();
    }

    private GridData getLayoutData() {
        return new GridData(SWT.FILL, SWT.CENTER, true, false);
    }

    private GridData getLayoutData(int widthHint) {
        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        gridData.widthHint = widthHint;
        return gridData;
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
            if (checkData()) {
                return true;
            }
            return false;
        }
        case DialogActionTypes.DISPLAY: {
            return true;
        }
        }
        return false;
    }

    protected void convertData() {
        textMessageId.setText(textMessageId.getText().toUpperCase().trim());
    }

    protected boolean checkData() {

        if (actionType != DialogActionTypes.DELETE) {

            // The value in field 'Message-Id.' is not valid.

            if (!validatorMessageId.validate(textMessageId.getText())) {
                setErrorMessage(Messages.The_value_in_field_Message_Id_is_not_valid);
                textMessageId.setFocus();
                return false;
            }

            // The value in field 'Message' is not valid.

            if (!validatorMessage.validate(textMessage.getText())) {
                setErrorMessage(Messages.The_value_in_field_Message_is_not_valid);
                textMessage.setFocus();
                return false;
            }

            // The value in field 'Helptext' is not valid.

            if (!validatorHelpText.validate(comboHelpText.getText())) {
                setErrorMessage(Messages.The_value_in_field_Helptext_is_not_valid);
                comboHelpText.setFocus();
                return false;
            }

            // The value in field 'Severity' is not valid.

            if (!validatorSeverity.validate(textSeveriry.getText())) {
                setErrorMessage(Messages.The_value_in_field_Severity_is_not_valid);
                textSeveriry.setFocus();
                return false;
            }

            // The value in field 'Ccsid' is not valid.

            if (!validatorCcsid.validate(comboCcsid.getText())) {
                setErrorMessage(Messages.The_value_in_field_Ccsid_is_not_valid);
                comboCcsid.setFocus();
                return false;
            }

        }

        // Befehl erstellen

        String parameterMSGF = "MSGF(" + _messageDescription.getLibrary() + "/" + _messageDescription.getMessageFile() + ")";

        String parameterMSGID = "MSGID(" + textMessageId.getText() + ")";

        String parameterMSG = "MSG('" + getStringWithQuotes(textMessage.getText()) + "')";

        String parameterSECLVL = "SECLVL('" + getStringWithQuotes(comboHelpText.getText()) + "')";

        String parameterFMT = "";
        ArrayList<?> fieldFormats = _fieldFormatViewer.getFieldFormats();
        if (fieldFormats.size() == 0) {
            parameterFMT = "FMT(*NONE)";
        } else {
            StringBuffer buffer = new StringBuffer();
            buffer.append("FMT(");
            for (int idx = 0; idx < fieldFormats.size(); idx++) {
                FieldFormat fieldFormat = (FieldFormat)fieldFormats.get(idx);
                if (idx != 0) {
                    buffer.append(" ");
                }
                buffer.append("(");
                buffer.append(fieldFormat.getType());
                if (fieldFormat.isVary()) {
                    buffer.append(" *VARY " + Integer.toString(fieldFormat.getBytes()));
                } else {
                    buffer.append(" " + Integer.toString(fieldFormat.getLength()));
                    if (fieldFormat.getType().equals("*DEC")) {
                        buffer.append(" " + Integer.toString(fieldFormat.getDecimalPositions()));
                    }
                }
                buffer.append(")");
            }
            buffer.append(")");
            parameterFMT = buffer.toString();
        }

        String parameterSEV = "SEV(" + getNumericString(textSeveriry.getText(), "0") + ")";

        String parameterCCSID;
        if (comboCcsid.getText().startsWith("*")) {
            parameterCCSID = "CCSID('" + getStringWithQuotes(comboCcsid.getText()) + "')";
        } else {
            parameterCCSID = "CCSID(" + comboCcsid.getText() + ")";
        }

        String command = "";

        if (actionType == DialogActionTypes.CREATE || actionType == DialogActionTypes.COPY) {
            command = "ADDMSGD " + parameterMSGF + " " + parameterMSGID + " " + parameterMSG + " " + parameterSECLVL + " " + parameterFMT + " "
                + parameterSEV + " " + parameterCCSID;
        } else if (actionType == DialogActionTypes.CHANGE) {
            command = "CHGMSGD " + parameterMSGF + " " + parameterMSGID + " " + parameterMSG + " " + parameterSECLVL + " " + parameterFMT + " "
                + parameterSEV + " " + parameterCCSID;
        } else if (actionType == DialogActionTypes.DELETE) {
            command = "RMVMSGD " + parameterMSGF + " " + parameterMSGID;
        }

        // Execute command

        CommandCall commandCall = new CommandCall(as400);
        try {
            if (!commandCall.run(command)) {
                AS400Message[] messageList = commandCall.getMessageList();
                if (messageList.length > 0) {
                    setErrorMessage(messageList[0].getText());
                    return false;
                } else {
                    setErrorMessage(Messages.Unknown_error_occured);
                    return false;
                }
            }
        } catch (AS400SecurityException e) {
            setErrorMessage(Messages.Unknown_error_occured);
            return false;
        } catch (ErrorCompletingRequestException e) {
            setErrorMessage(Messages.Unknown_error_occured);
            return false;
        } catch (IOException e) {
            setErrorMessage(Messages.Unknown_error_occured);
            return false;
        } catch (InterruptedException e) {
            setErrorMessage(Messages.Unknown_error_occured);
            return false;
        } catch (PropertyVetoException e) {
            setErrorMessage(Messages.Unknown_error_occured);
            return false;
        }

        // Everything is alright

        return true;
    }

    private String getNumericString(String numericValue, String defaultValue) {
        if (StringHelper.isNullOrEmpty(numericValue)) {
            return defaultValue;
        }
        return numericValue;
    }

    private String getStringWithQuotes(String stringToBeQuoted) {
        StringBuffer stringWithQuotes = new StringBuffer("");
        for (int idx = 0; idx < stringToBeQuoted.length(); idx++) {
            String character = stringToBeQuoted.substring(idx, idx + 1);
            stringWithQuotes.append(character);
            if (character.equals("'")) {
                stringWithQuotes.append("'");
            }
        }
        return stringWithQuotes.toString();
    }

    protected void transferData() {
        refresh(_messageDescription);
    }

    public FieldFormatViewer getFieldFormatViewer() {
        return _fieldFormatViewer;
    }

    public void refresh(MessageDescription _messageDescription) {

        if (actionType == DialogActionTypes.CREATE || actionType == DialogActionTypes.COPY) {
            _messageDescription.setMessageId(textMessageId.getText());
        }

        QMHRTVM qmhrtvm = new QMHRTVM();
        MessageDescription[] _description = qmhrtvm.run(as400, _messageDescription.getConnection(), _messageDescription.getLibrary(),
            _messageDescription.getMessageFile(), _messageDescription.getMessageId());
        if (_description.length == 1) {
            _messageDescription.setMessage(_description[0].getMessage());
            _messageDescription.setHelpText(_description[0].getHelpText());
            _messageDescription.setFieldFormats(_description[0].getFieldFormats());
            _messageDescription.setSeverity(_description[0].getSeverity());
            _messageDescription.setCcsid(_description[0].getCcsid());
        }

    }

    public void saveSettings(IDialogSettings dialogBoundsSettings) {
        dialogBoundsSettings.put(STATUS_BUTTON_HIDE_ADVANCED_OPTIONS, buttonHideAdvancedOptions.getSelection());
    }

    public void loadSettings(IDialogSettings dialogBoundsSettings) {
        buttonHideAdvancedOptions.setSelection(dialogBoundsSettings.getBoolean(STATUS_BUTTON_HIDE_ADVANCED_OPTIONS));
        setAdvancedOptionsEnablement();
    }

}