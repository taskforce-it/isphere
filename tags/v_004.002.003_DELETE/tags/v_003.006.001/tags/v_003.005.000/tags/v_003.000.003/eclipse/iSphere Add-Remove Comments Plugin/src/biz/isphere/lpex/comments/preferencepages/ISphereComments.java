/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.comments.preferencepages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.core.preferences.DoNotAskMeAgain;
import biz.isphere.core.preferences.DoNotAskMeAgainDialog;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.lpex.comments.ISphereAddRemoveCommentsPlugin;
import biz.isphere.lpex.comments.Messages;
import biz.isphere.lpex.comments.preferences.Preferences;

public class ISphereComments extends PreferencePage implements IWorkbenchPreferencePage {

    private Button btnEnabled;

    public ISphereComments() {
        super();
        setPreferenceStore(ISphereAddRemoveCommentsPlugin.getDefault().getPreferenceStore());
        getPreferenceStore();
    }

    public void init(IWorkbench workbench) {
    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout(2, false);
        container.setLayout(gridLayout);

        createSectionGlobal(container);

        setScreenToValues();

        return container;
    }

    private void createSectionGlobal(Composite parent) {

        Label labelEnabled = new Label(parent, SWT.NONE);
        labelEnabled.setLayoutData(createLabelLayoutData());
        labelEnabled.setText(Messages.Enabled_colon);
        labelEnabled.setToolTipText(Messages.Tooltip_Enables_options_for_commenting_and_uncommenting_source_lines);

        btnEnabled = WidgetFactory.createCheckbox(parent);
        btnEnabled.setToolTipText(Messages.Tooltip_Enables_options_for_commenting_and_uncommenting_source_lines);
        btnEnabled.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        btnEnabled.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                if (validateEnabled()) {
                    checkAllValues();
                }
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });
    }

    @Override
    protected void performApply() {
        super.performApply();
    }

    @Override
    protected void performDefaults() {
        setScreenToDefaultValues();
        super.performDefaults();
    }

    @Override
    public boolean performOk() {

        if (ISphereAddRemoveCommentsPlugin.getDefault().isEnabled() != btnEnabled.getSelection()) {
            DoNotAskMeAgainDialog.openInformation(getShell(), DoNotAskMeAgain.LPEX_COMMENT_RESTART_INFORMATION,
                Messages.You_need_to_restart_the_IDE_to_activate_your_changes);
        }

        setStoreToValues();
        return super.performOk();
    }

    protected void setStoreToValues() {

        Preferences preferences = getPreferences();

        preferences.setEnabled(btnEnabled.getSelection());
    }

    protected void setScreenToValues() {

        ISphereAddRemoveCommentsPlugin.getDefault();

        Preferences preferences = getPreferences();

        btnEnabled.setSelection(preferences.isEnabled());

        checkAllValues();
        setControlsEnablement();
    }

    protected void setScreenToDefaultValues() {

        Preferences preferences = getPreferences();

        btnEnabled.setSelection(preferences.getDefaultEnabled());

        checkAllValues();
        setControlsEnablement();
    }

    private boolean validateEnabled() {

        return true;
    }

    private boolean checkAllValues() {

        if (!validateEnabled()) {
            return false;
        }

        return clearError();
    }

    private void setControlsEnablement() {

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

    private GridData createLabelLayoutData() {
        return new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
    }

    private Preferences getPreferences() {
        return Preferences.getInstance();
    }
}