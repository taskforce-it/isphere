/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefileeditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.BasicMessageFormatter;
import biz.isphere.core.internal.DialogActionTypes;
import biz.isphere.core.internal.MessageDescriptionHelper;
import biz.isphere.core.internal.Size;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class MessageDescriptionDetail {

    private static final String SPECIAL_VALUE_NONE = "*NONE"; //$NON-NLS-1$

    private static final String STATUS_BUTTON_HIDE_ADVANCED_OPTIONS = "status_buttonHideAdvancedOptions"; //$NON-NLS-1$
    private static final String WEIGHT_SASH_FORM_HELP_TEXT_FIELD_FORMATS = "weight_sashFormHelpTextFieldFormats_"; //$NON-NLS-1$
    private static final String FORMAT_MESSAGE_HELP_TEXT = "formatMessageHelpText"; //$NON-NLS-1$

    private int actionType;
    private MessageDescription _messageDescription;
    private Text textConnection;
    private Text textLibrary;
    private Text textMessageFile;
    private Text textMessageId;
    private Text textMessage;
    private Text textHelpText;
    private Text textSeveriry;
    private Combo comboCcsid;
    private Validator validatorMessageId;
    private Validator validatorMessage;
    private Validator validatorHelpText;
    private Validator validatorSeverity;
    private Validator validatorCcsid;
    private StatusLineManager statusLineManager;
    private FieldFormatViewer _fieldFormatViewer;
    private Composite compositeAdvancedOptions;
    private Button buttonFormatHelpText;
    private Button buttonHideAdvancedOptions;
    private Text textTextLength;
    private SashForm sashContainer;
    private BasicMessageFormatter messageFormatter;

    public MessageDescriptionDetail(int actionType, MessageDescription _messageDescription) {
        this.actionType = actionType;
        this._messageDescription = _messageDescription;

        if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.COPY || actionType == DialogActionTypes.DELETE
            || actionType == DialogActionTypes.DISPLAY) {
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
        labelAction.setLayoutData(getLayoutDataFillHorizontal());
        labelAction.setText(DialogActionTypes.getText(actionType));
        labelAction.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        if (actionType == DialogActionTypes.DELETE) {
            labelAction.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        } else {
            labelAction.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
        }

        // Create sash form
        // Composite sashComposite = new Composite(container, SWT.BORDER);
        // sashComposite.setLayout(new GridLayout());
        // sashComposite.setLayoutData(getLayoutDataFillBoth());

        sashContainer = new SashForm(container, SWT.VERTICAL);
        sashContainer.setLayoutData(getLayoutDataFillBoth());

        // Composite containerTop = new Composite(sashContainer, SWT.BORDER);
        // containerTop.setLayout(new GridLayout());
        // containerTop.setLayoutData(getLayoutDataFillBoth());
        //
        // Composite containerBottom = new Composite(sashContainer, SWT.BORDER);
        // containerBottom.setLayout(new GridLayout());
        // containerBottom.setLayoutData(getLayoutDataFillBoth());

        // Header

        createHeader(sashContainer);

        // Field formats

        createFieldFormats(sashContainer);

        sashContainer.setWeights(new int[] { 4, 2 });

        // Advanced options group

        createAdvancedOptionsGroup(container);

        // Status line

        statusLineManager = new StatusLineManager();
        statusLineManager.createControl(container, SWT.NONE);
        Control statusLine = statusLineManager.getControl();
        final GridData gridDataStatusLine = getLayoutDataFillHorizontal();
        statusLine.setLayoutData(gridDataStatusLine);

        // Set focus

        if (actionType == DialogActionTypes.CHANGE) {
            textMessage.setFocus();
        } else {
            textMessageId.setFocus();
        }
    }

    private void createHeader(final Composite container) {

        final Composite compositeHeader = new Composite(container, SWT.NONE);
        compositeHeader.setLayoutData(getLayoutDataFillHorizontal());

        final GridLayout gridLayoutCompositeHeader = new GridLayout();
        gridLayoutCompositeHeader.numColumns = 2;
        compositeHeader.setLayout(gridLayoutCompositeHeader);
        compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Connection

        final Label labelConnection = new Label(compositeHeader, SWT.NONE);
        labelConnection.setText(Messages.Connection_colon);

        textConnection = WidgetFactory.createReadOnlyText(compositeHeader);
        textConnection.setLayoutData(getLayoutDataFillHorizontal());

        // Library

        final Label labelLibrary = new Label(compositeHeader, SWT.NONE);
        labelLibrary.setText(Messages.Library_colon);

        textLibrary = WidgetFactory.createReadOnlyText(compositeHeader);
        textLibrary.setLayoutData(getLayoutDataFillHorizontal());

        // Message file

        final Label labelMessageFile = new Label(compositeHeader, SWT.NONE);
        labelMessageFile.setText(Messages.Message_file_colon);

        textMessageFile = WidgetFactory.createReadOnlyText(compositeHeader);
        textMessageFile.setLayoutData(getLayoutDataFillHorizontal());

        // Message-Id.

        final Label labelMessageId = new Label(compositeHeader, SWT.NONE);
        labelMessageId.setText(Messages.Message_Id_colon);

        textMessageId = WidgetFactory.createText(compositeHeader);
        textMessageId.setLayoutData(getLayoutDataFillHorizontal());
        textMessageId.setTextLimit(7);
        if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textMessageId.setEditable(false);
        }

        validatorMessageId = Validator.getMessageIdInstance();

        // Message

        final Label labelMessage = new Label(compositeHeader, SWT.NONE);
        labelMessage.setText(Messages.Message_colon);

        final Composite compositeMessage = new Composite(compositeHeader, SWT.NONE);
        compositeMessage.setLayoutData(getLayoutDataFillHorizontal());

        final GridLayout gridLayoutCompositeMessage = new GridLayout();
        gridLayoutCompositeMessage.numColumns = 3;
        gridLayoutCompositeMessage.marginWidth = 0;
        gridLayoutCompositeMessage.marginHeight = 0;
        compositeMessage.setLayout(gridLayoutCompositeMessage);

        textMessage = WidgetFactory.createText(compositeMessage);
        textMessage.setLayoutData(getLayoutDataFillHorizontal());
        textMessage.setTextLimit(132);
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textMessage.setEditable(false);
        }
        textMessage.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                textTextLength.setText(Integer.toString(textMessage.getText().length()));
            }
        });

        validatorMessage = Validator.getCharInstance();
        validatorMessage.setLength(132);

        Label labelTextLength = new Label(compositeMessage, SWT.NONE);
        labelTextLength.setText(Messages.Text_length + ":");

        textTextLength = WidgetFactory.createReadOnlyText(compositeMessage);
        textTextLength.setLayoutData(new GridData(Size.getSize(25), SWT.DEFAULT));

        // Helptext

        createSecondLevelTextEditor(compositeHeader);
    }

    private void createFieldFormats(final Composite container) {

        _fieldFormatViewer = new FieldFormatViewer(actionType, _messageDescription, textMessage, textHelpText);
        _fieldFormatViewer.createContents(container);
    }

    private void createAdvancedOptionsGroup(final Composite container) {

        buttonHideAdvancedOptions = WidgetFactory.createCheckbox(container);
        buttonHideAdvancedOptions.setText(Messages.Advanced_options);
        buttonHideAdvancedOptions.setSelection(false);
        buttonHideAdvancedOptions.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                setAdvancedOptionsEnablement();
                Shell shell = buttonHideAdvancedOptions.getShell();
                Point size = shell.getSize();
                Point newSize = shell.computeSize(size.x, SWT.DEFAULT, false);
                newSize.x = size.x;
                shell.setSize(newSize);
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
                return;
            }
        });

        compositeAdvancedOptions = new Composite(container, SWT.NONE);
        compositeAdvancedOptions.setLayoutData(getLayoutDataFillHorizontal());
        compositeAdvancedOptions.setLayout(new GridLayout(2, false));
        setAdvancedOptionsEnablement();

        GridData gridDataAdvancedOptions = getLayoutDataFillHorizontal();
        compositeAdvancedOptions.setLayoutData(gridDataAdvancedOptions);

        // Severity

        final Label labelSeverity = new Label(compositeAdvancedOptions, SWT.NONE);
        labelSeverity.setText(Messages.Severity_colon);

        textSeveriry = WidgetFactory.createIntegerText(compositeAdvancedOptions);
        textSeveriry.setLayoutData(getLayoutData(60));
        textSeveriry.setTextLimit(2);
        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textSeveriry.setEditable(false);
        }

        validatorSeverity = Validator.getIntegerInstance(textSeveriry.getTextLimit());

        // Ccsid

        final Label labelCcsid = new Label(compositeAdvancedOptions, SWT.NONE);
        labelCcsid.setText(Messages.Ccsid_colon);

        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            comboCcsid = WidgetFactory.createReadOnlyCombo(compositeAdvancedOptions);
        } else {
            comboCcsid = WidgetFactory.createCombo(compositeAdvancedOptions);
        }
        comboCcsid.setLayoutData(getLayoutData(60));
        comboCcsid.setTextLimit(5);
        comboCcsid.add(MessageDescription.CCSID_JOB);
        comboCcsid.add(MessageDescription.CCSID_HEX);

        validatorCcsid = Validator.getIntegerInstance(comboCcsid.getTextLimit());
        validatorCcsid.addSpecialValue(MessageDescription.CCSID_JOB);
        validatorCcsid.addSpecialValue(MessageDescription.CCSID_HEX);
    }

    private void createSecondLevelTextEditor(Composite compositeHeader) {

        final Label labelHelpText = new Label(compositeHeader, SWT.NONE);
        labelHelpText.setText(Messages.Helptext_colon);
        labelHelpText.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

        textHelpText = WidgetFactory.createMultilineText(compositeHeader, true, false);
        GridData gridData = getLayoutDataFillBoth();
        gridData.heightHint = 80;
        textHelpText.setLayoutData(gridData);
        textHelpText.setTextLimit(3000);

        if (actionType == DialogActionTypes.DISPLAY || actionType == DialogActionTypes.DELETE) {
            buttonFormatHelpText = WidgetFactory.createCheckbox(compositeHeader);
            buttonFormatHelpText.setText(Messages.Format_help_text);
            buttonFormatHelpText.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent event) {
                    if (buttonFormatHelpText.getSelection()) {
                        textHelpText.setText(getMessageFormatter().formatHelpText(_messageDescription.getHelpText()));
                    } else {
                        textHelpText.setText(_messageDescription.getHelpText());
                    }
                }

                public void widgetDefaultSelected(SelectionEvent event) {
                }
            });
        }

        if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
            textHelpText.setEditable(false);
        }

        textHelpText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent paramModifyEvent) {
                if (StringHelper.isNullOrEmpty(textHelpText.getText())) {
                    textHelpText.setText(SPECIAL_VALUE_NONE);
                }
            }
        });

        validatorHelpText = Validator.getCharInstance();
        validatorHelpText.setLength(textHelpText.getTextLimit());
        validatorHelpText.addSpecialValue(SPECIAL_VALUE_NONE);

    }

    private void loadScreenValues() {

        textConnection.setText(_messageDescription.getConnection());
        textLibrary.setText(_messageDescription.getLibrary());
        textMessageFile.setText(_messageDescription.getMessageFile());

        if (actionType == DialogActionTypes.CREATE) {
            textMessageId.setText("");
            textMessage.setText("");
            textHelpText.setText(SPECIAL_VALUE_NONE);
        } else {
            textMessageId.setText(_messageDescription.getMessageId());
            textMessage.setText(_messageDescription.getMessage());
            if (buttonFormatHelpText != null && buttonFormatHelpText.getSelection()) {
                textHelpText.setText(getMessageFormatter().formatHelpText(_messageDescription.getHelpText()));
            } else {
                textHelpText.setText(_messageDescription.getHelpText());
            }
        }

        textSeveriry.setText(_messageDescription.getSeverity().toString());
        comboCcsid.setText(_messageDescription.getCcsidAsString());
    }

    private void setAdvancedOptionsEnablement() {
        compositeAdvancedOptions.setVisible(buttonHideAdvancedOptions.getSelection());
        ((GridData)compositeAdvancedOptions.getLayoutData()).exclude = !buttonHideAdvancedOptions.getSelection();
    }

    private GridData getLayoutDataFillHorizontal() {
        return new GridData(SWT.FILL, SWT.CENTER, true, false);
    }

    private GridData getLayoutDataFillBoth() {
        return new GridData(SWT.FILL, SWT.FILL, true, true);
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
            if (checkDataAndExecuteAction()) {
                reloadMessageDescription();
                return true;
            }
            return false;
        }
        case DialogActionTypes.CHANGE: {
            convertData();
            if (checkDataAndExecuteAction()) {
                reloadMessageDescription();
                return true;
            }
            return false;
        }
        case DialogActionTypes.COPY: {
            convertData();
            if (checkDataAndExecuteAction()) {
                reloadMessageDescription();
                return true;
            }
            return false;
        }
        case DialogActionTypes.DELETE: {
            if (checkDataAndExecuteAction()) {
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

    protected boolean checkDataAndExecuteAction() {

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

            if (!validatorHelpText.validate(textHelpText.getText())) {
                setErrorMessage(Messages.The_value_in_field_Helptext_is_not_valid);
                textHelpText.setFocus();
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

        // Update message description
        _messageDescription.setMessage(textMessage.getText());
        _messageDescription.setHelpText(textHelpText.getText());
        _messageDescription.setFieldFormats(_fieldFormatViewer.getFieldFormats());
        _messageDescription.setSeverity(IntHelper.tryParseInt(textSeveriry.getText(), 0));

        if (comboCcsid.getText().startsWith("*")) {
            _messageDescription.setCcsid(comboCcsid.getText());
        } else {
            _messageDescription.setCcsid(IntHelper.tryParseInt(comboCcsid.getText(), 0));
        }

        try {

            // Execute command
            String message = null;
            if (actionType == DialogActionTypes.CREATE || actionType == DialogActionTypes.COPY) {
                _messageDescription.setMessageId(textMessageId.getText());
                message = MessageDescriptionHelper.addMessageDescription(_messageDescription);
            } else if (actionType == DialogActionTypes.CHANGE) {
                message = MessageDescriptionHelper.changeMessageDescription(_messageDescription);
            } else if (actionType == DialogActionTypes.DELETE) {
                message = MessageDescriptionHelper.removeMessageDescription(_messageDescription);
            }

            if (message != null) {
                setErrorMessage(message);
                return false;
            }

        } catch (Exception e) {
            setErrorMessage(Messages.Unknown_error_occured);
            return false;
        }

        // Everything is alright

        return true;
    }

    protected void reloadMessageDescription() {

        if (actionType == DialogActionTypes.CREATE || actionType == DialogActionTypes.COPY) {
            _messageDescription.setMessageId(textMessageId.getText());
        }

        refresh(_messageDescription);
    }

    public FieldFormatViewer getFieldFormatViewer() {
        return _fieldFormatViewer;
    }

    public void refresh(MessageDescription _messageDescription) {

        _messageDescription = MessageDescriptionHelper.retrieveMessageDescription(_messageDescription.getConnection(),
            _messageDescription.getMessageFile(), _messageDescription.getLibrary(), _messageDescription.getMessageId());
    }

    public void saveSettings(DialogSettingsManager dialogSettingsManager) {

        if (buttonFormatHelpText != null) {
            dialogSettingsManager.storeValue(FORMAT_MESSAGE_HELP_TEXT, buttonFormatHelpText.getSelection());
        }

        dialogSettingsManager.storeValue(STATUS_BUTTON_HIDE_ADVANCED_OPTIONS, buttonHideAdvancedOptions.getSelection());

        storeIntArray(dialogSettingsManager, WEIGHT_SASH_FORM_HELP_TEXT_FIELD_FORMATS, sashContainer.getWeights());
    }

    public void loadSettings(DialogSettingsManager dialogSettingsManager) {

        if (buttonFormatHelpText != null) {
            buttonFormatHelpText.setSelection(dialogSettingsManager.loadBooleanValue(FORMAT_MESSAGE_HELP_TEXT, false));
        }

        buttonHideAdvancedOptions.setSelection(dialogSettingsManager.loadBooleanValue(STATUS_BUTTON_HIDE_ADVANCED_OPTIONS, false));
        setAdvancedOptionsEnablement();

        int[] weights = loadIntArray(dialogSettingsManager, WEIGHT_SASH_FORM_HELP_TEXT_FIELD_FORMATS, new int[] { 4, 2 });
        sashContainer.setWeights(weights);

        loadScreenValues();
    }

    protected void storeIntArray(DialogSettingsManager dialogSettingsManager, String aKey, int[] aValue) {

        for (int i = 0; i < aValue.length; i++) {
            dialogSettingsManager.storeValue(getArrayKey(aKey, i), aValue[i]);
        }
    }

    protected int[] loadIntArray(DialogSettingsManager dialogSettingsManager, String aKey, int[] aDefault) {

        List<Integer> intValues = new ArrayList<Integer>();

        int i = 0;
        int value = 0;
        do {

            if (i < aDefault.length) {
                value = dialogSettingsManager.loadIntValue(getArrayKey(aKey, i), aDefault[i]);
            } else {
                value = dialogSettingsManager.loadIntValue(getArrayKey(aKey, i), -1);
            }

            if (value != -1) {
                intValues.add(new Integer(value));
            }

            i++;

        } while (value != -1);

        int[] values = new int[intValues.size()];
        for (int j = 0; j < intValues.size(); j++) {
            values[j] = intValues.get(j).intValue();
        }

        return values;
    }

    private String getArrayKey(String aBaseKey, int index) {
        return aBaseKey + index;
    }

    private BasicMessageFormatter getMessageFormatter() {

        if (messageFormatter == null) {
            messageFormatter = new BasicMessageFormatter();
        }

        return messageFormatter;
    }
}