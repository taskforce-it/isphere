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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class ISphereAppearance extends PreferencePage implements IWorkbenchPreferencePage {

    private Combo textDateFormat;
    private Combo textTimeFormat;

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

        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(3, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        group.setText(Messages.DateAndTimeFormats);

        Label labelDate = new Label(group, SWT.NONE);
        labelDate.setLayoutData(createLabelLayoutData());
        labelDate.setText(Messages.Date_long_colon);

        textDateFormat = WidgetFactory.createReadOnlyCombo(group);
        textDateFormat.setToolTipText(Messages.Tooltip_Specifies_the_format_for_displaying_date_values);
        textDateFormat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        textDateFormat.setItems(Preferences.getInstance().getDateFormatLabels());

        Label labelTime = new Label(group, SWT.NONE);
        labelTime.setLayoutData(createLabelLayoutData());
        labelTime.setText(Messages.Time_long_colon);

        textTimeFormat = WidgetFactory.createReadOnlyCombo(group);
        textTimeFormat.setToolTipText(Messages.Tooltip_Specifies_the_format_for_displaying_time_values);
        textTimeFormat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        textTimeFormat.setItems(Preferences.getInstance().getTimeFormatLabels());
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
    }

    protected void setScreenToValues() {

        ISpherePlugin.getDefault();

        Preferences preferences = Preferences.getInstance();

        textDateFormat.setText(preferences.getDateFormatLabel());
        textTimeFormat.setText(preferences.getTimeFormatLabel());

        checkAllValues();
        setControlsEnablement();
    }

    protected void setScreenToDefaultValues() {

        Preferences preferences = Preferences.getInstance();

        textDateFormat.setText(preferences.getDefaultDateFormatLabel());
        textTimeFormat.setText(preferences.getDefaultTimeFormatLabel());

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