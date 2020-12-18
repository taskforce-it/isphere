/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.preferencepages;

import java.io.File;

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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class ISphereSearch extends PreferencePage implements IWorkbenchPreferencePage {

    private static final String MODE_VIEW = "*BROWSE"; //$NON-NLS-1$
    private static final String MODE_EDIT = "*EDIT"; //$NON-NLS-1$

    private Button buttonBatchResolveEnabled;
    private Combo comboSourceFileSearchEditMode;
    private Text textSourceFileSearchSaveDirectory;
    private Button buttonSourceFileSearchAutoSaveEnabled;
    private Text textSourceFileSearchAutoSaveFileName;

    private Text textMessageFileSearchSaveDirectory;
    private Button buttonMessageFileSearchAutoSaveEnabled;
    private Text textMessageFileSearchAutoSaveFileName;

    private boolean messageFileSettingsEnabled;

    public ISphereSearch() {
        super();
        setPreferenceStore(ISpherePlugin.getDefault().getPreferenceStore());
        getPreferenceStore();
    }

    public void init(IWorkbench workbench) {

        /*
         * Does not work, because we cannot create an AS400 object, when loading
         * a search result.
         */
        if (IBMiHostContributionsHandler.hasContribution()) {
            messageFileSettingsEnabled = true;
        } else {
            messageFileSettingsEnabled = false;
        }

    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        container.setLayout(gridLayout);

        createSectionSourceFileSearch(container);
        createSectionMessageFileSearch(container);

        setScreenToValues();

        return container;
    }

    private void createSectionSourceFileSearch(Composite parent) {

        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(3, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        group.setText(Messages.Source_file_search);

        Label labelBatchResolveEnabled = new Label(group, SWT.NONE);
        labelBatchResolveEnabled.setLayoutData(createLabelLayoutData());
        labelBatchResolveEnabled.setText(Messages.Batch_resolve_enabled_colon);

        buttonBatchResolveEnabled = WidgetFactory.createCheckbox(group);
        buttonBatchResolveEnabled.setToolTipText(Messages.Batch_resolve_enabled_Tooltip);
        buttonBatchResolveEnabled.setLayoutData(createTextLayoutData(2));
        buttonBatchResolveEnabled.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                if (validateSourceFileSearchAutoSaveEnabled()) {
                    checkAllValues();
                    setControlsEnablement();
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        Label labelSoureFileSearchEditMode = new Label(group, SWT.NONE);
        labelSoureFileSearchEditMode.setLayoutData(createLabelLayoutData());
        labelSoureFileSearchEditMode.setText(Messages.Lpex_editor_mode_colon);

        comboSourceFileSearchEditMode = WidgetFactory.createReadOnlyCombo(group);
        comboSourceFileSearchEditMode.setToolTipText(Messages.Tooltip_Lpex_editor_mode);
        comboSourceFileSearchEditMode.setLayoutData(createTextLayoutData(2));
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
        GridData sourceFileSearchSaveDirectoryLayoutData = createTextLayoutData();
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
        buttonSourceFileSearchAutoSaveEnabled.setLayoutData(createTextLayoutData(2));
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
        textSourceFileSearchAutoSaveFileName.setLayoutData(createTextLayoutData(2));
        textSourceFileSearchAutoSaveFileName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateSourceFileSearchAutoSaveFileName()) {
                    checkAllValues();
                }
            }
        });
    }

    private void createSectionMessageFileSearch(Composite parent) {

        /*
         * Does not work, because we cannot create an AS400 object, when loading
         * a search result.
         */
        if (!messageFileSettingsEnabled) {
            return;
        }

        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(3, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        group.setText(Messages.Message_file_search);

        Label labelMessageFileSearchResultsSaveDirectory = new Label(group, SWT.NONE);
        labelMessageFileSearchResultsSaveDirectory.setLayoutData(createLabelLayoutData());
        labelMessageFileSearchResultsSaveDirectory.setText(Messages.Save_results_to_colon);

        textMessageFileSearchSaveDirectory = WidgetFactory.createText(group);
        textMessageFileSearchSaveDirectory.setToolTipText(Messages.Tooltip_Specifies_the_folder_to_save_message_file_search_results_to);
        GridData messageFileSearchSaveDirectoryLayoutData = createTextLayoutData();
        messageFileSearchSaveDirectoryLayoutData.widthHint = 200;
        textMessageFileSearchSaveDirectory.setLayoutData(messageFileSearchSaveDirectoryLayoutData);
        textMessageFileSearchSaveDirectory.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateMessageFileSearchSaveDirectory()) {
                    checkAllValues();
                }
            }
        });

        Button buttonMessageFileSearchResultsSaveDirectory = WidgetFactory.createPushButton(group, Messages.Browse + "..."); //$NON-NLS-1$
        buttonMessageFileSearchResultsSaveDirectory.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {

                DirectoryDialog dialog = new DirectoryDialog(getShell());
                dialog.setFilterPath(getFilterPath());
                String directory = dialog.open();
                if (directory != null) {
                    textMessageFileSearchSaveDirectory.setText(directory);
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }

            private String getFilterPath() {
                if (!StringHelper.isNullOrEmpty(textMessageFileSearchSaveDirectory.getText())) {
                    File directory = new File(textMessageFileSearchSaveDirectory.getText());
                    if (directory.exists()) {
                        if (directory.isDirectory()) {
                            return directory.getAbsolutePath();
                        } else {
                            return directory.getParentFile().getAbsolutePath();
                        }
                    }
                }
                return Preferences.getInstance().getDefaultMessageFileSearchResultsSaveDirectory();
            }
        });

        Label labelMessageFileSearchResultsAutoSaveEnabled = new Label(group, SWT.NONE);
        labelMessageFileSearchResultsAutoSaveEnabled.setLayoutData(createLabelLayoutData());
        labelMessageFileSearchResultsAutoSaveEnabled.setText(Messages.Auto_save_enabled_colon);

        buttonMessageFileSearchAutoSaveEnabled = WidgetFactory.createCheckbox(group);
        buttonMessageFileSearchAutoSaveEnabled.setToolTipText(Messages.Auto_save_enabled_Tooltip);
        buttonMessageFileSearchAutoSaveEnabled.setLayoutData(createTextLayoutData(2));
        buttonMessageFileSearchAutoSaveEnabled.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                if (validateMessageFileSearchAutoSaveEnabled()) {
                    checkAllValues();
                    setControlsEnablement();
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        Label labelMessageFileSearchResultsAutoSaveFileName = new Label(group, SWT.NONE);
        labelMessageFileSearchResultsAutoSaveFileName.setLayoutData(createLabelLayoutData());
        labelMessageFileSearchResultsAutoSaveFileName.setText(Messages.Auto_save_file_name_colon);

        textMessageFileSearchAutoSaveFileName = WidgetFactory.createText(group);
        textMessageFileSearchAutoSaveFileName.setToolTipText(Messages.Auto_save_file_name_Tooltip);
        textMessageFileSearchAutoSaveFileName.setLayoutData(createTextLayoutData(2));
        textMessageFileSearchAutoSaveFileName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateMessageFileSearchAutoSaveFileName()) {
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

        preferences.setSourceFileSearchBatchResolveEnabled(buttonBatchResolveEnabled.getSelection());
        preferences.setSourceFileSearchResultsEditEnabled(getComboSourceFileSearchEditMode());
        preferences.setSourceFileSearchResultsSaveDirectory(textSourceFileSearchSaveDirectory.getText());
        preferences.setSourceFileSearchResultsAutoSaveEnabled(buttonSourceFileSearchAutoSaveEnabled.getSelection());
        preferences.setSourceFileSearchResultsAutoSaveFileName(textSourceFileSearchAutoSaveFileName.getText());

        if (messageFileSettingsEnabled) {
            preferences.setMessageFileSearchResultsSaveDirectory(textMessageFileSearchSaveDirectory.getText());
            preferences.setMessageFileSearchResultsAutoSaveEnabled(buttonMessageFileSearchAutoSaveEnabled.getSelection());
            preferences.setMessageFileSearchResultsAutoSaveFileName(textMessageFileSearchAutoSaveFileName.getText());
        }
    }

    protected void setScreenToValues() {

        ISpherePlugin.getDefault();

        Preferences preferences = Preferences.getInstance();

         buttonBatchResolveEnabled.setSelection(preferences.isSourceFileSearchBatchResolveEnabled());
        setComboSourceFileSearchEditMode(preferences.isSourceFileSearchResultsEditEnabled());
        textSourceFileSearchSaveDirectory.setText(preferences.getSourceFileSearchResultsSaveDirectory());
        buttonSourceFileSearchAutoSaveEnabled.setSelection(preferences.isSourceFileSearchResultsAutoSaveEnabled());
        textSourceFileSearchAutoSaveFileName.setText(preferences.getSourceFileSearchResultsAutoSaveFileName());

        if (messageFileSettingsEnabled) {
            textMessageFileSearchSaveDirectory.setText(preferences.getMessageFileSearchResultsSaveDirectory());
            buttonMessageFileSearchAutoSaveEnabled.setSelection(preferences.isMessageFileSearchResultsAutoSaveEnabled());
            textMessageFileSearchAutoSaveFileName.setText(preferences.getMessageFileSearchResultsAutoSaveFileName());
        }

        checkAllValues();
        setControlsEnablement();
    }

    protected void setScreenToDefaultValues() {

        Preferences preferences = Preferences.getInstance();

        buttonBatchResolveEnabled.setSelection(preferences.getDefaultSourceFileSearchBatchResolveEnabled());
        setComboSourceFileSearchEditMode(preferences.getDefaultSourceFileSearchResultsEditEnabled());
        textSourceFileSearchSaveDirectory.setText(preferences.getDefaultSourceFileSearchResultsSaveDirectory());
        buttonSourceFileSearchAutoSaveEnabled.setSelection(preferences.getDefaultSourceFileSearchResultsAutoSaveEnabled());
        textSourceFileSearchAutoSaveFileName.setText(preferences.getDefaultSourceFileSearchResultsAutoSaveFileName());

        if (messageFileSettingsEnabled) {
            textMessageFileSearchSaveDirectory.setText(preferences.getDefaultMessageFileSearchResultsSaveDirectory());
            buttonMessageFileSearchAutoSaveEnabled.setSelection(preferences.getDefaultMessageFileSearchResultsAutoSaveEnabled());
            textMessageFileSearchAutoSaveFileName.setText(preferences.getDefaultMessageFileSearchResultsAutoSaveFileName());
        }

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

    private boolean validateBatchResolveEnabled() {

        return true;
    }

    private boolean validateSourceFileSearchEditMode() {

        if (comboSourceFileSearchEditMode == null) {
            return true;
        }

        return clearError();
    }

    private boolean validateSourceFileSearchSaveDirectory() {

        return validateSearchSaveDirectory(textSourceFileSearchSaveDirectory);
    }

    private boolean validateSourceFileSearchAutoSaveEnabled() {

        return true;
    }

    private boolean validateSourceFileSearchAutoSaveFileName() {

        return validateSearchAutoSaveFileName(textSourceFileSearchAutoSaveFileName);
    }

    private boolean validateMessageFileSearchSaveDirectory() {

        return validateSearchSaveDirectory(textMessageFileSearchSaveDirectory);
    }

    private boolean validateMessageFileSearchAutoSaveEnabled() {

        return true;
    }

    private boolean validateMessageFileSearchAutoSaveFileName() {

        return validateSearchAutoSaveFileName(textMessageFileSearchAutoSaveFileName);
    }

    private boolean validateSearchSaveDirectory(Text textSearchSaveDirectory) {

        if (textSearchSaveDirectory == null) {
            return true;
        }

        String path = textSearchSaveDirectory.getText();
        if (StringHelper.isNullOrEmpty(path)) {
            setError(Messages.Directory_must_not_be_empty);
            return false;
        }

        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }

        File directory = new File(path);
        if (!directory.exists()) {
            setError(Messages.The_specified_directory_does_not_exist);
            return false;
        }

        if (!directory.isDirectory()) {
            setError(Messages.The_specified_directory_does_not_exist);
            return false;
        }

        return clearError();
    }

    private boolean validateSearchAutoSaveFileName(Text textFileName) {

        if (textFileName == null) {
            return true;
        }

        String filename = textFileName.getText();
        if (StringHelper.isNullOrEmpty(filename)) {
            setError(Messages.File_name_must_not_be_empty);
            return false;
        }

        return clearError();
    }

    private boolean checkAllValues() {

        if (!validateBatchResolveEnabled()) {
            return false;
        }

        if (!validateSourceFileSearchEditMode()) {
            return false;
        }

        if (!validateSourceFileSearchSaveDirectory()) {
            return false;
        }

        if (!validateSourceFileSearchAutoSaveEnabled()) {
            return false;
        }

        if (!validateSourceFileSearchAutoSaveFileName()) {
            return false;
        }

        if (!validateMessageFileSearchSaveDirectory()) {
            return false;
        }

        if (!validateMessageFileSearchAutoSaveEnabled()) {
            return false;
        }

        if (!validateMessageFileSearchAutoSaveFileName()) {
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

        if (messageFileSettingsEnabled) {
            if (buttonMessageFileSearchAutoSaveEnabled.getSelection()) {
                textMessageFileSearchAutoSaveFileName.setEnabled(true);
            } else {
                textMessageFileSearchAutoSaveFileName.setEnabled(false);
            }
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
        return new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    }

    private GridData createTextLayoutData() {
        return createTextLayoutData(1);
    }

    private GridData createTextLayoutData(int horizontalSpan) {
        return new GridData(SWT.FILL, SWT.CENTER, true, false, horizontalSpan, 1);
    }
}