/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
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
import biz.isphere.core.swt.widgets.extension.WidgetFactory;

public class ISphereSearch extends PreferencePage implements IWorkbenchPreferencePage {

    private static final String MODE_VIEW = "*BROWSE";

    private static final String MODE_EDIT = "*EDIT";

    private Combo comboSoureFileSearchEditMode;

    private boolean sourceFileSearchEditMode;

    public ISphereSearch() {
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

        createSectionSourceFileSearch(container);

        setScreenToValues();

        return container;
    }

    private void createSectionSourceFileSearch(Composite parent) {

        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        group.setText(Messages.Source_file_search);

        Label labelSoureFileSearchEditMode = new Label(group, SWT.NONE);
        labelSoureFileSearchEditMode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        labelSoureFileSearchEditMode.setText(Messages.Lpex_editor_mode);

        comboSoureFileSearchEditMode = WidgetFactory.createReadOnlyCombo(group);
        comboSoureFileSearchEditMode.setToolTipText(Messages.Tooltip_Lpex_editor_mode);
        comboSoureFileSearchEditMode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        comboSoureFileSearchEditMode.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateSourcefileSearchEditMode()) {
                    checkAllValues();
                }
            }
        });
        fillSourceFileSearchEditModeCombo();

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

        Preferences.getInstance().setSourceFileSearchResultsEditEnabled(sourceFileSearchEditMode);

    }

    protected void setScreenToValues() {

        ISpherePlugin.getDefault();

        sourceFileSearchEditMode = Preferences.getInstance().isSourceFileSearchResultsEditEnabled();

        setScreenValues();

    }

    protected void setScreenToDefaultValues() {

        sourceFileSearchEditMode = Preferences.getInstance().getDefaultSourceFileSearchResultsEditEnabled();

        setScreenValues();

    }

    protected void setScreenValues() {

        if (sourceFileSearchEditMode) {
            comboSoureFileSearchEditMode.setText(MODE_EDIT);
        } else {
            comboSoureFileSearchEditMode.setText(MODE_VIEW);
        }

    }

    private void fillSourceFileSearchEditModeCombo() {

        String[] textViews = new String[] { MODE_EDIT, MODE_VIEW };
        comboSoureFileSearchEditMode.setItems(textViews);
    }

    private boolean validateSourcefileSearchEditMode() {

        String textSoureFileSearchEditMode = comboSoureFileSearchEditMode.getText();

        if (MODE_EDIT.equals(textSoureFileSearchEditMode)) {
            sourceFileSearchEditMode = true;
        } else {
            sourceFileSearchEditMode = false;
        }

        return clearError();
    }

    private boolean checkAllValues() {

        return clearError();
    }

//    private boolean setError(String message) {
//        setErrorMessage(message);
//        setValid(false);
//        return false;
//    }

    private boolean clearError() {
        setErrorMessage(null);
        setValid(true);
        return true;
    }

}