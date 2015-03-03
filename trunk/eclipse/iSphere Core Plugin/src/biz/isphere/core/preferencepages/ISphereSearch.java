/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.preferencepages;

import java.io.File;

import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.extension.WidgetFactory;

public class ISphereSearch extends PreferencePage implements IWorkbenchPreferencePage {

    private static final String MODE_VIEW = "*BROWSE"; //$NON-NLS-1$
    private static final String MODE_EDIT = "*EDIT"; //$NON-NLS-1$

    private Combo comboSourceFileSearchEditMode;
    private Text textSourceFileSearchSaveDirectory;
    private Button buttonSourceFileSearchAutoSaveEnabled;
    private Text textSourceFileSearchAutoSaveFileName;

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
        group.setLayout(new GridLayout(3, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        group.setText(Messages.Source_file_search);

        Label labelSoureFileSearchEditMode = new Label(group, SWT.NONE);
        labelSoureFileSearchEditMode.setLayoutData(createLabelLayoutData());
        labelSoureFileSearchEditMode.setText(Messages.Lpex_editor_mode_colon);

        comboSourceFileSearchEditMode = WidgetFactory.createReadOnlyCombo(group);
        comboSourceFileSearchEditMode.setToolTipText(Messages.Tooltip_Lpex_editor_mode);
        comboSourceFileSearchEditMode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        comboSourceFileSearchEditMode.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateSourceFileSearchEditMode()) {
                    checkAllValues();
                }
            }
        });
        fillSourceFileSearchEditModeCombo();

        Label labelSoureFileSearchResultsSaveDirectory = new Label(group, SWT.NONE);
        labelSoureFileSearchResultsSaveDirectory.setLayoutData(createLabelLayoutData());
        labelSoureFileSearchResultsSaveDirectory.setText(Messages.Save_results_to_colon);

        textSourceFileSearchSaveDirectory = WidgetFactory.createText(group);
        textSourceFileSearchSaveDirectory.setToolTipText(Messages.Tooltip_Specifies_the_folder_to_save_source_file_search_results_to);
        GridData sourceFileSearchSaveDirectoryLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        sourceFileSearchSaveDirectoryLayoutData.widthHint = 200;
        textSourceFileSearchSaveDirectory.setLayoutData(sourceFileSearchSaveDirectoryLayoutData);
        textSourceFileSearchSaveDirectory.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateSourceFileSearchSaveDirectory()) {
                    checkAllValues();
                }
            }
        });

        Button buttonSourceFileSearchResultsSaveDirectory = WidgetFactory.createPushButton(group, Messages.Browse + "..."); //$NON-NLS-1$
        buttonSourceFileSearchResultsSaveDirectory.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {

                DirectoryDialog dialog = new DirectoryDialog(getShell());
                dialog.setFilterPath(getFilterPath());
                String directory = dialog.open();
                if (directory != null) {
                    textSourceFileSearchSaveDirectory.setText(directory);
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }

            private String getFilterPath() {
                if (!StringHelper.isNullOrEmpty(textSourceFileSearchSaveDirectory.getText())) {
                    File directory = new File(textSourceFileSearchSaveDirectory.getText());
                    if (directory.exists()) {
                        if (directory.isDirectory()) {
                            return directory.getAbsolutePath();
                        } else {
                            return directory.getParentFile().getAbsolutePath();
                        }
                    }
                }
                return Preferences.getInstance().getDefaultSourceFileSearchResultsSaveDirectory();
            }
        });

        Label labelSourceFileSearchResultsAutoSaveEnabled = new Label(group, SWT.NONE);
        labelSourceFileSearchResultsAutoSaveEnabled.setLayoutData(createLabelLayoutData());
        labelSourceFileSearchResultsAutoSaveEnabled.setText(Messages.Auto_save_enabled_colon);

        buttonSourceFileSearchAutoSaveEnabled = WidgetFactory.createCheckbox(group);
        buttonSourceFileSearchAutoSaveEnabled.setToolTipText(Messages.Auto_save_enabled_Tooltip);
        buttonSourceFileSearchAutoSaveEnabled.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        buttonSourceFileSearchAutoSaveEnabled.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                if (validateSourceFileSearchAutoSaveEnabled()) {
                    checkAllValues();
                    setControlsEnablement();
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        Label labelSourceFileSearchResultsAutoSaveFileName = new Label(group, SWT.NONE);
        labelSourceFileSearchResultsAutoSaveFileName.setLayoutData(createLabelLayoutData());
        labelSourceFileSearchResultsAutoSaveFileName.setText(Messages.Auto_save_file_name_colon);

        textSourceFileSearchAutoSaveFileName = WidgetFactory.createText(group);
        textSourceFileSearchAutoSaveFileName.setToolTipText(Messages.Auto_save_file_name_Tooltip);
        textSourceFileSearchAutoSaveFileName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        textSourceFileSearchAutoSaveFileName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateSourceFileSearchAutoSaveFileName()) {
                    checkAllValues();
                }
            }
        });
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

        preferences.setSourceFileSearchResultsEditEnabled(getComboSourceFileSearchEditMode());
        preferences.setSourceFileSearchResultsSaveDirectory(textSourceFileSearchSaveDirectory.getText());
        preferences.setSourceFileSearchResultsAutoSaveEnabled(buttonSourceFileSearchAutoSaveEnabled.getSelection());
        preferences.setSourceFileSearchResultsAutoSaveFileName(textSourceFileSearchAutoSaveFileName.getText());

    }

    protected void setScreenToValues() {

        ISpherePlugin.getDefault();

        Preferences preferences = Preferences.getInstance();

        setComboSourceFileSearchEditMode(preferences.isSourceFileSearchResultsEditEnabled());
        textSourceFileSearchSaveDirectory.setText(preferences.getSourceFileSearchResultsSaveDirectory());
        buttonSourceFileSearchAutoSaveEnabled.setSelection(preferences.isSourceFileSearchResultsAutoSaveEnabled());
        textSourceFileSearchAutoSaveFileName.setText(preferences.getSourceFileSearchResultsAutoSaveFileName());

        checkAllValues();
        setControlsEnablement();
    }

    protected void setScreenToDefaultValues() {

        Preferences preferences = Preferences.getInstance();

        setComboSourceFileSearchEditMode(preferences.getDefaultSourceFileSearchResultsEditEnabled());
        textSourceFileSearchSaveDirectory.setText(preferences.getDefaultSourceFileSearchResultsSaveDirectory());
        buttonSourceFileSearchAutoSaveEnabled.setSelection(preferences.getDefaultSourceFileSearchResultsAutoSaveEnabled());
        textSourceFileSearchAutoSaveFileName.setText(preferences.getDefaultSourceFileSearchResultsAutoSaveFileName());

        checkAllValues();
        setControlsEnablement();
    }

    private boolean getComboSourceFileSearchEditMode() {

        if (MODE_EDIT.equals(comboSourceFileSearchEditMode.getText())) {
            return true;
        } else {
            return false;
        }
    }

    private void setComboSourceFileSearchEditMode(boolean enabled) {

        if (enabled) {
            comboSourceFileSearchEditMode.setText(MODE_EDIT);
        } else {
            comboSourceFileSearchEditMode.setText(MODE_VIEW);
        }
    }

    private void fillSourceFileSearchEditModeCombo() {

        String[] textViews = new String[] { MODE_EDIT, MODE_VIEW };
        comboSourceFileSearchEditMode.setItems(textViews);
    }

    private boolean validateSourceFileSearchEditMode() {

        if (comboSourceFileSearchEditMode == null) {
            return true;
        }

        return clearError();
    }

    private boolean validateSourceFileSearchSaveDirectory() {

        if (textSourceFileSearchSaveDirectory == null) {
            return true;
        }

        String path = textSourceFileSearchSaveDirectory.getText();
        if (StringHelper.isNullOrEmpty(path)) {
            setError("Directory must not be empty.");
            return false;
        }

        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        
        File directory = new File(path);
        if (!directory.exists()) {
            setError("The specified directory does not exist.");
            return false;
        }

        if (!directory.isDirectory()) {
            setError("The specified directory does not exist.");
            return false;
        }

        return clearError();
    }

    private boolean validateSourceFileSearchAutoSaveEnabled() {

        if (buttonSourceFileSearchAutoSaveEnabled == null) {
            return true;
        }

        return true;
    }

    private boolean validateSourceFileSearchAutoSaveFileName() {

        if (textSourceFileSearchAutoSaveFileName == null) {
            return true;
        }

        String filename = textSourceFileSearchAutoSaveFileName.getText();
        if (StringHelper.isNullOrEmpty(filename)) {
            setError("File name must not be empty.");
            return false;
        }

        return clearError();
    }

    private boolean checkAllValues() {

        if (!validateSourceFileSearchEditMode()) {
            return false;
        }

        if (!validateSourceFileSearchSaveDirectory()) {
            return false;
        }

        if (!validateSourceFileSearchAutoSaveFileName()) {
            return false;
        }

        return clearError();
    }

    private void setControlsEnablement() {

        if (buttonSourceFileSearchAutoSaveEnabled.getSelection()) {
            textSourceFileSearchAutoSaveFileName.setEnabled(true);
        } else {
            textSourceFileSearchAutoSaveFileName.setEnabled(false);
        }
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
}