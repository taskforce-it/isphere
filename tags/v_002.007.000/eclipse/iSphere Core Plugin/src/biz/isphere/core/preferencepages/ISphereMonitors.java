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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataqueue.action.MessageLengthAction;
import biz.isphere.core.dataqueue.action.ViewInHexAction;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class ISphereMonitors extends PreferencePage implements IWorkbenchPreferencePage {

    private Combo comboDataQueueMaximumMessageLength;
    private Combo comboDataQueueViewInHex;
    private Button buttonDisplayEndOfData;

    private int dataQueueMaximumMessageLength;
    private boolean dataQueueViewInHex;
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

        Label labelHostName = new Label(group, SWT.NONE);
        labelHostName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        labelHostName.setText(Messages.Maximum_message_length_colon);

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

        Label labelDisplayEndOfData = new Label(group, SWT.NONE);
        labelDisplayEndOfData.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        labelDisplayEndOfData.setText(Messages.Display_End_Of_Data_colon);
        
        buttonDisplayEndOfData = WidgetFactory.createCheckbox(group);
        buttonDisplayEndOfData.setToolTipText(Messages.Tooltip_Display_End_Of_Data);
        buttonDisplayEndOfData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        buttonDisplayEndOfData.addSelectionListener(new SelectionListener() {
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

        Preferences.getInstance().setDataQueueMaximumMessageLength(dataQueueMaximumMessageLength);
        Preferences.getInstance().setDataQueueViewInHex(dataQueueViewInHex);
        Preferences.getInstance().setDataQueueDisplayEndOfData(dataQueueDisplayEndOfData);

    }

    protected void setScreenToValues() {

        ISpherePlugin.getDefault();

        dataQueueMaximumMessageLength = Preferences.getInstance().getDataQueueMaximumMessageLength();
        dataQueueViewInHex = Preferences.getInstance().isDataQueueViewInHex();
        dataQueueDisplayEndOfData = Preferences.getInstance().isDataQueueDisplayEndOfData();

        setScreenValues();

    }

    protected void setScreenToDefaultValues() {

        dataQueueMaximumMessageLength = Preferences.getInstance().getDefaultDataQueueMaximumMessageLength();
        dataQueueViewInHex = Preferences.getInstance().getDefaultDataQueueViewInHex();
        dataQueueDisplayEndOfData = Preferences.getInstance().getDefaultDataQueueDisplayEndOfData();

        setScreenValues();

    }

    protected void setScreenValues() {

        comboDataQueueMaximumMessageLength.setText(Integer.toString(dataQueueMaximumMessageLength));
        comboDataQueueViewInHex.setText(ViewInHexAction.viewInHexToString(dataQueueViewInHex));
        buttonDisplayEndOfData.setSelection(dataQueueDisplayEndOfData);

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

        String[] textViews = new String[] {ViewInHexAction.viewInHexToString(true), ViewInHexAction.viewInHexToString(false)};
        comboDataQueueViewInHex.setItems(textViews);
    }

    public void setDataQueueDisplayEndOfDataButton() {
        buttonDisplayEndOfData.setSelection(true);
    }
    
    private boolean validateDataQueueMessageLength() {

        String textLength = comboDataQueueMaximumMessageLength.getText();
        if (StringHelper.isNullOrEmpty(textLength)) {
            return setError(Messages.bind( Messages.Value_is_out_of_range_The_valid_range_is_A_to_B, 1,  MessageLengthAction.MAX_LENGTH ));
        }

        int length = MessageLengthAction.lengthToInt(textLength);
        if (length > MessageLengthAction.MAX_LENGTH) {
            return setError(Messages.bind( Messages.Value_is_out_of_range_The_valid_range_is_A_to_B, 1,  MessageLengthAction.MAX_LENGTH ));
        }

        dataQueueMaximumMessageLength = length;

        return clearError();
    }

    private boolean validateDataQueueViewInHex() {

        String textViewInHex = comboDataQueueViewInHex.getText();
        
        dataQueueViewInHex = ViewInHexAction.viewInHexToBoolean(textViewInHex);

        return clearError();
    }

    private boolean validateDataQueueDisplayEndOfData() {

        dataQueueDisplayEndOfData = buttonDisplayEndOfData.getSelection();

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