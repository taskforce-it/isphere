/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.preferencepages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataqueue.action.MessageLengthAction;
import biz.isphere.core.dataqueue.action.ViewInHexAction;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class ISphereMonitors extends PreferencePage implements IWorkbenchPreferencePage {

    private Combo comboDataQueueNumberOfMessagesToRetrieve;
    private Combo comboDataQueueMaximumMessageLength;
    private Combo comboDataQueueViewInHex;
    private Text textDataQueueReplacementCharacter;
    private Button buttonDataQueueDisplayEndOfData;

    private int dataQueueNumberOfMessagesToRetrieve;
    private int dataQueueMaximumMessageLength;
    private boolean dataQueueViewInHex;
    private String dataQueueReplacementCharacter;
    private boolean dataQueueDisplayEndOfData;

    public ISphereMonitors() {
        super();
        setPreferenceStore(ISpherePlugin.getDefault().getPreferenceStore());
        getPreferenceStore();
    }

    public void init(IWorkbench workbench) {
    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        container.setLayout(gridLayout);

        createSectionDataQueue(container);

        setScreenToValues();

        return container;
    }

    private void createSectionDataQueue(Composite parent) {

        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        group.setText(Messages.Data_queue);

        Label labelNumberOfMessagesToRetrieve = new Label(group, SWT.NONE);
        labelNumberOfMessagesToRetrieve.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        labelNumberOfMessagesToRetrieve.setText(Messages.Number_of_messages_colon);

        comboDataQueueNumberOfMessagesToRetrieve = WidgetFactory.createCombo(group);
        comboDataQueueNumberOfMessagesToRetrieve.setToolTipText(Messages.Tooltip_Number_of_messages);
        comboDataQueueNumberOfMessagesToRetrieve.setTextLimit(5);
        comboDataQueueNumberOfMessagesToRetrieve.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        comboDataQueueNumberOfMessagesToRetrieve.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateDataQueueNumberOfMessagesToRetrieve()) {
                    checkAllValues();
                }
            }
        });
        fillDataQueueNumberOfMessagesToRetrieveCombo();

        Label labelMessageLength = new Label(group, SWT.NONE);
        labelMessageLength.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        labelMessageLength.setText(Messages.Maximum_message_length_colon);

        comboDataQueueMaximumMessageLength = WidgetFactory.createCombo(group);
        comboDataQueueMaximumMessageLength.setToolTipText(Messages.Tooltip_Maximum_message_length);
        comboDataQueueMaximumMessageLength.setTextLimit(5);
        comboDataQueueMaximumMessageLength.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        comboDataQueueMaximumMessageLength.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateDataQueueMessageLength()) {
                    checkAllValues();
                }
            }
        });
        fillDataQueueMessageLengthCombo();

        Label labelViewInHex = new Label(group, SWT.NONE);
        labelViewInHex.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        labelViewInHex.setText(Messages.Display_mode_colon);

        comboDataQueueViewInHex = WidgetFactory.createReadOnlyCombo(group);
        comboDataQueueViewInHex.setToolTipText(Messages.Tooltip_Display_data_mode);
        comboDataQueueViewInHex.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        comboDataQueueViewInHex.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateDataQueueViewInHex()) {
                    checkAllValues();
                }
            }
        });
        fillDataQueueViewInHexCombo();

        Label labelReplacementCharacter = new Label(group, SWT.NONE);
        labelReplacementCharacter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        labelReplacementCharacter.setText(Messages.Replacement_character_colon);

        textDataQueueReplacementCharacter = WidgetFactory.createText(group);
        textDataQueueReplacementCharacter.setToolTipText(Messages.Tooltip_Replacement_character);
        textDataQueueReplacementCharacter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        textDataQueueReplacementCharacter.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateReplacementCharacter()) {
                    checkAllValues();
                }
            }
        });
        textDataQueueReplacementCharacter.setTextLimit(1);

        Label labelDisplayEndOfData = new Label(group, SWT.NONE);
        labelDisplayEndOfData.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        labelDisplayEndOfData.setText(Messages.Display_End_Of_Data_colon);

        buttonDataQueueDisplayEndOfData = WidgetFactory.createCheckbox(group);
        buttonDataQueueDisplayEndOfData.setToolTipText(Messages.Tooltip_Display_End_Of_Data);
        buttonDataQueueDisplayEndOfData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        buttonDataQueueDisplayEndOfData.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                if (validateDataQueueDisplayEndOfData()) {
                    checkAllValues();
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
        setDataQueueDisplayEndOfDataButton();

    }

    @Override
    protected void performApply() {
        setStoreToValues();
        super.performApply();
    }

    @Override
    protected void performDefaults() {
        setScreenToDefaultValues();
        super.performDefaults();
    }

    @Override
    public boolean performOk() {
        setStoreToValues();
        return super.performOk();
    }

    protected void setStoreToValues() {

        Preferences.getInstance().setDataQueueNumberOfMessagesToRetrieve(dataQueueNumberOfMessagesToRetrieve);
        Preferences.getInstance().setDataQueueMaximumMessageLength(dataQueueMaximumMessageLength);
        Preferences.getInstance().setDataQueueViewInHex(dataQueueViewInHex);
        Preferences.getInstance().setDataQueueDisplayEndOfData(dataQueueDisplayEndOfData);
        Preferences.getInstance().setDataQueueReplacementCharacter(dataQueueReplacementCharacter);

    }

    protected void setScreenToValues() {

        ISpherePlugin.getDefault();

        dataQueueNumberOfMessagesToRetrieve = Preferences.getInstance().getDataQueueNumberOfMessagesToRetrieve();
        dataQueueMaximumMessageLength = Preferences.getInstance().getDataQueueMaximumMessageLength();
        dataQueueViewInHex = Preferences.getInstance().isDataQueueViewInHex();
        dataQueueDisplayEndOfData = Preferences.getInstance().isDataQueueDisplayEndOfData();
        dataQueueReplacementCharacter = Preferences.getInstance().getDataQueueReplacementCharacter();

        setScreenValues();

    }

    protected void setScreenToDefaultValues() {

        dataQueueNumberOfMessagesToRetrieve = Preferences.getInstance().getDefaultDataQueueNumberOfMessagesToRetrieve();
        dataQueueMaximumMessageLength = Preferences.getInstance().getDefaultDataQueueMaximumMessageLength();
        dataQueueViewInHex = Preferences.getInstance().getDefaultDataQueueViewInHex();
        dataQueueDisplayEndOfData = Preferences.getInstance().getDefaultDataQueueDisplayEndOfData();
        dataQueueReplacementCharacter = Preferences.getInstance().getDefaultDataQueueReplacementCharacter();

        setScreenValues();

    }

    protected void setScreenValues() {

        comboDataQueueNumberOfMessagesToRetrieve.setText(Integer.toString(dataQueueNumberOfMessagesToRetrieve));
        comboDataQueueMaximumMessageLength.setText(Integer.toString(dataQueueMaximumMessageLength));
        comboDataQueueViewInHex.setText(ViewInHexAction.viewInHexToString(dataQueueViewInHex));
        buttonDataQueueDisplayEndOfData.setSelection(dataQueueDisplayEndOfData);
        textDataQueueReplacementCharacter.setText(dataQueueReplacementCharacter);

    }

    private void fillDataQueueNumberOfMessagesToRetrieveCombo() {

        String[] textNumMessages = Preferences.getInstance().getDataQueueNumberOfMessagesToRetrieveItems();
        comboDataQueueNumberOfMessagesToRetrieve.setItems(textNumMessages);
    }

    private void fillDataQueueMessageLengthCombo() {

        int[] lengths = Preferences.getInstance().getDataQueueMaximumMessageLengthValues();
        String[] textLengths = new String[lengths.length];

        for (int i = 0; i < lengths.length; i++) {
            textLengths[i] = MessageLengthAction.lengthToString(lengths[i]);
        }

        comboDataQueueMaximumMessageLength.setItems(textLengths);
    }

    private void fillDataQueueViewInHexCombo() {

        String[] textViews = new String[] { ViewInHexAction.viewInHexToString(true), ViewInHexAction.viewInHexToString(false) };
        comboDataQueueViewInHex.setItems(textViews);
    }

    public void setDataQueueDisplayEndOfDataButton() {
        buttonDataQueueDisplayEndOfData.setSelection(true);
    }

    private boolean validateDataQueueNumberOfMessagesToRetrieve() {

        String textLength = comboDataQueueNumberOfMessagesToRetrieve.getText();
        if (StringHelper.isNullOrEmpty(textLength)) {
            return setError(Messages.Invalid_or_missing_numeric_value);
        }

        int numberOfMessages = IntHelper.tryParseInt(textLength, -1);
        if (numberOfMessages <= 0) {
            return setError(Messages.Invalid_or_missing_numeric_value);
        }

        dataQueueNumberOfMessagesToRetrieve = numberOfMessages;

        return clearError();
    }

    private boolean validateDataQueueMessageLength() {

        String textLength = comboDataQueueMaximumMessageLength.getText();
        if (StringHelper.isNullOrEmpty(textLength)) {
            return setError(Messages.bind(Messages.Value_is_out_of_range_The_valid_range_is_A_to_B, 1, MessageLengthAction.MAX_LENGTH));
        }

        int length = MessageLengthAction.lengthToInt(textLength);
        if (length > MessageLengthAction.MAX_LENGTH) {
            return setError(Messages.bind(Messages.Value_is_out_of_range_The_valid_range_is_A_to_B, 1, MessageLengthAction.MAX_LENGTH));
        }

        dataQueueMaximumMessageLength = length;

        return clearError();
    }

    private boolean validateDataQueueViewInHex() {

        String textViewInHex = comboDataQueueViewInHex.getText();

        dataQueueViewInHex = ViewInHexAction.viewInHexToBoolean(textViewInHex);

        return clearError();
    }

    private boolean validateReplacementCharacter() {

        String textReplacementCharacter = textDataQueueReplacementCharacter.getText();
        if (StringHelper.isNullOrEmpty(textReplacementCharacter)) {
            return setError(Messages.Invalid_or_missing_value);
        }

        dataQueueReplacementCharacter = textReplacementCharacter;

        return clearError();
    }

    private boolean validateDataQueueDisplayEndOfData() {

        dataQueueDisplayEndOfData = buttonDataQueueDisplayEndOfData.getSelection();

        return clearError();
    }

    private boolean checkAllValues() {

        return clearError();
    }

    private boolean setError(String message) {
        setErrorMessage(message);
        setValid(false);
        return false;
    }

    private boolean clearError() {
        setErrorMessage(null);
        setValid(true);
        return true;
    }

}