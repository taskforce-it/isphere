/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.preferencepages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
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
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.preferences.DoNotAskMeAgainDialog;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class ISphereAppearance extends PreferencePage implements IWorkbenchPreferencePage {

    private Combo textDateFormat;
    private Combo textTimeFormat;
    private Text textAutoRefreshDelay;
    private Text textAutoRefreshThreshold;
    private Button chkboxResetWarnings;
    private Label labelResetWarnings;

    public ISphereAppearance() {
        super();
        setPreferenceStore(ISpherePlugin.getDefault().getPreferenceStore());
        getPreferenceStore();
    }

    public void init(IWorkbench arg0) {
    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        container.setLayout(gridLayout);

        createSectionDate(container);

        setScreenToValues();

        return container;
    }

    private void createSectionDate(Composite parent) {

        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(new GridLayout(2, false));
        main.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        // Date and Time Formats
        Group groupDateAndTimeFormats = new Group(main, SWT.NONE);
        groupDateAndTimeFormats.setLayout(new GridLayout(3, false));
        groupDateAndTimeFormats.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
        groupDateAndTimeFormats.setText(Messages.DateAndTimeFormats);

        Label labelDateFormat = new Label(groupDateAndTimeFormats, SWT.NONE);
        labelDateFormat.setLayoutData(createLabelLayoutData());
        labelDateFormat.setToolTipText(Messages.Tooltip_Specifies_the_format_for_displaying_date_values);
        labelDateFormat.setText(Messages.Date_long_colon);

        textDateFormat = WidgetFactory.createReadOnlyCombo(groupDateAndTimeFormats);
        textDateFormat.setToolTipText(Messages.Tooltip_Specifies_the_format_for_displaying_date_values);
        textDateFormat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        textDateFormat.setItems(Preferences.getInstance().getDateFormatLabels());

        Label labelTimeFormat = new Label(groupDateAndTimeFormats, SWT.NONE);
        labelTimeFormat.setLayoutData(createLabelLayoutData());
        labelTimeFormat.setToolTipText(Messages.Tooltip_Specifies_the_format_for_displaying_time_values);
        labelTimeFormat.setText(Messages.Time_long_colon);

        textTimeFormat = WidgetFactory.createReadOnlyCombo(groupDateAndTimeFormats);
        textTimeFormat.setToolTipText(Messages.Tooltip_Specifies_the_format_for_displaying_time_values);
        textTimeFormat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        textTimeFormat.setItems(Preferences.getInstance().getTimeFormatLabels());

        // Auto refresh delay
        Group groupAutoRefreshDelay = new Group(main, SWT.NONE);
        groupAutoRefreshDelay.setLayout(new GridLayout(3, false));
        groupAutoRefreshDelay.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
        groupAutoRefreshDelay.setText(Messages.Auto_refresh_delay);

        Label labelAutoRefreshDelay = new Label(groupAutoRefreshDelay, SWT.NONE);
        labelAutoRefreshDelay.setLayoutData(createLabelLayoutData());
        labelAutoRefreshDelay.setToolTipText(Messages.Tooltip_Delay_ms);
        labelAutoRefreshDelay.setText(Messages.Delay_ms);

        textAutoRefreshDelay = WidgetFactory.createIntegerText(groupAutoRefreshDelay);
        textAutoRefreshDelay.setTextLimit(4);
        textAutoRefreshDelay.setToolTipText(Messages.Tooltip_Delay_ms);
        textAutoRefreshDelay.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Label labelAutoRefreshThreshold = new Label(groupAutoRefreshDelay, SWT.NONE);
        labelAutoRefreshThreshold.setLayoutData(createLabelLayoutData());
        labelAutoRefreshThreshold.setToolTipText(Messages.Tooltip_Threshold_items);
        labelAutoRefreshThreshold.setText(Messages.Threshold_items);

        textAutoRefreshThreshold = WidgetFactory.createIntegerText(groupAutoRefreshDelay);
        textAutoRefreshThreshold.setTextLimit(5);
        textAutoRefreshThreshold.setToolTipText(Messages.Tooltip_Threshold_items);
        textAutoRefreshThreshold.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        // Reset warnings
        chkboxResetWarnings = WidgetFactory.createCheckbox(main);
        chkboxResetWarnings.setToolTipText(Messages.bind(Messages.Tooltip_Reset_warning_messages, Messages.Do_not_show_this_message_again));

        labelResetWarnings = new Label(main, SWT.NONE);
        labelResetWarnings.setToolTipText(Messages.bind(Messages.Tooltip_Reset_warning_messages, Messages.Do_not_show_this_message_again));
        labelResetWarnings.setText(Messages.Reset_warning_messages);
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

        Preferences preferences = Preferences.getInstance();

        preferences.setDateFormatLabel(textDateFormat.getText());
        preferences.setTimeFormatLabel(textTimeFormat.getText());
        preferences.setAutoRefreshDelay(IntHelper.tryParseInt(textAutoRefreshDelay.getText(), preferences.getDefaultAutoRefreshDelay()));
        preferences.setAutoRefreshThreshold(IntHelper.tryParseInt(textAutoRefreshThreshold.getText(), preferences.getDefaultAutoRefreshThreshold()));

        if (chkboxResetWarnings.getSelection()) {
            DoNotAskMeAgainDialog.resetAllMessages();
            chkboxResetWarnings.setSelection(false);
            labelResetWarnings.setEnabled(false);
            chkboxResetWarnings.setEnabled(false);
        }
    }

    protected void setScreenToValues() {

        ISpherePlugin.getDefault();

        Preferences preferences = Preferences.getInstance();

        textDateFormat.setText(preferences.getDateFormatLabel());
        textTimeFormat.setText(preferences.getTimeFormatLabel());
        textAutoRefreshDelay.setText(Integer.toString(preferences.getAutoRefreshDelay()));
        textAutoRefreshThreshold.setText(Integer.toString(preferences.getAutoRefreshThreshold()));
        chkboxResetWarnings.setSelection(false);

        checkAllValues();
        setControlsEnablement();
    }

    protected void setScreenToDefaultValues() {

        Preferences preferences = Preferences.getInstance();

        textDateFormat.setText(preferences.getDefaultDateFormatLabel());
        textTimeFormat.setText(preferences.getDefaultTimeFormatLabel());
        textAutoRefreshDelay.setText(Integer.toString(preferences.getDefaultAutoRefreshDelay()));
        textAutoRefreshThreshold.setText(Integer.toString(preferences.getDefaultAutoRefreshThreshold()));
        chkboxResetWarnings.setSelection(false);

        checkAllValues();
        setControlsEnablement();
    }

    private boolean checkAllValues() {

        return clearError();
    }

    private void setControlsEnablement() {

    }

    private boolean clearError() {
        setErrorMessage(null);
        setValid(true);
        return true;
    }

    private GridData createLabelLayoutData() {
        return new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
    }
}