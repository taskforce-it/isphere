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
import biz.isphere.lpex.comments.ISphereLpexEditorExtensionsPlugin;
import biz.isphere.lpex.comments.Messages;
import biz.isphere.lpex.comments.preferences.Preferences;

public class ISphereLpexEditorExtensions extends PreferencePage implements IWorkbenchPreferencePage {

    private boolean hasShownWarning;

    private Button btnCommentsEnabled;
    private Button btnIndentingEnabled;

    public ISphereLpexEditorExtensions() {
        super();

        setPreferenceStore(ISphereLpexEditorExtensionsPlugin.getDefault().getPreferenceStore());
        getPreferenceStore();

        hasShownWarning = false;
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

        Label labelCommentsEnabled = new Label(parent, SWT.NONE);
        labelCommentsEnabled.setLayoutData(createLabelLayoutData());
        labelCommentsEnabled.setText(Messages.Comments_enabled_colon);
        labelCommentsEnabled.setToolTipText(Messages.Tooltip_Enables_options_for_commenting_and_uncommenting_source_lines);

        btnCommentsEnabled = WidgetFactory.createCheckbox(parent);
        btnCommentsEnabled.setToolTipText(Messages.Tooltip_Enables_options_for_commenting_and_uncommenting_source_lines);
        btnCommentsEnabled.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        btnCommentsEnabled.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                if (validateCommentsEnabled()) {
                    checkAllValues();
                }
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        Label labelIndentingEnabled = new Label(parent, SWT.NONE);
        labelIndentingEnabled.setLayoutData(createLabelLayoutData());
        labelIndentingEnabled.setText(Messages.Indention_enabled_colon);
        labelIndentingEnabled.setToolTipText(Messages.Tooltip_Enables_options_for_indenting_and_unindenting_source_lines);

        btnIndentingEnabled = WidgetFactory.createCheckbox(parent);
        btnIndentingEnabled.setToolTipText(Messages.Tooltip_Enables_options_for_indenting_and_unindenting_source_lines);
        btnIndentingEnabled.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        btnIndentingEnabled.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                if (validateIndentingEnabled()) {
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

        if (!hasShownWarning && mustRestartRdi()) {
            DoNotAskMeAgainDialog.openInformation(getShell(), DoNotAskMeAgain.LPEX_COMMENT_RESTART_INFORMATION,
                Messages.You_need_to_restart_the_IDE_to_activate_your_changes);
            hasShownWarning = true;
        }

        setStoreToValues();
        return super.performOk();
    }

    private boolean mustRestartRdi() {

        if (Preferences.getInstance().isCommentsEnabled() != btnCommentsEnabled.getSelection()) {
            return true;
        }

        if (Preferences.getInstance().isIndentionEnabled() != btnIndentingEnabled.getSelection()) {
            return true;
        }

        return false;
    }

    protected void setStoreToValues() {

        Preferences preferences = getPreferences();

        preferences.setCommentsEnabled(btnCommentsEnabled.getSelection());
        preferences.setIndentionEnabled(btnIndentingEnabled.getSelection());
    }

    protected void setScreenToValues() {

        ISphereLpexEditorExtensionsPlugin.getDefault();

        Preferences preferences = getPreferences();

        btnCommentsEnabled.setSelection(preferences.isCommentsEnabled());
        btnIndentingEnabled.setSelection(preferences.isIndentionEnabled());

        checkAllValues();
        setControlsEnablement();
    }

    protected void setScreenToDefaultValues() {

        Preferences preferences = getPreferences();

        btnCommentsEnabled.setSelection(preferences.getDefaultCommentsEnabled());
        btnIndentingEnabled.setSelection(preferences.getDefaultIndentionEnabled());

        checkAllValues();
        setControlsEnablement();
    }

    private boolean validateCommentsEnabled() {

        return true;
    }

    private boolean validateIndentingEnabled() {

        return true;
    }

    private boolean checkAllValues() {

        if (!validateCommentsEnabled()) {
            return false;
        }

        if (!validateIndentingEnabled()) {
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
        return new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    }

    private Preferences getPreferences() {
        return Preferences.getInstance();
    }
}