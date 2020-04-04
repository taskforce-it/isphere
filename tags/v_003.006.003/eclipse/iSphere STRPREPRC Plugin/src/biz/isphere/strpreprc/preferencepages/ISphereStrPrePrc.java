/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.preferencepages;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.strpreprc.Messages;
import biz.isphere.strpreprc.model.HeaderTemplates;
import biz.isphere.strpreprc.preferences.Preferences;

public class ISphereStrPrePrc extends PreferencePage implements IWorkbenchPreferencePage {

    private Button checkboxUseParameterSections;
    private Combo comboDefaultSection;
    private Button checkboxUseTemplateFolder;
    private Text textTemplateFolder;
    private Button buttonSelectTemplateFolder;
    private Button checkboxSkipEditDialog;
    private Button buttonExportTemplates;
    private Button buttonReloadTemplates;
    private Label labelTemplatesCount;

    public ISphereStrPrePrc() {
        super();
        setPreferenceStore(ISpherePlugin.getDefault().getPreferenceStore());
        getPreferenceStore();
    }

    public void init(IWorkbench workbench) {
    }

    @Override
    public Control createContents(Composite parent) {

        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(new GridLayout(2, false));
        main.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        createSectionCommon(main);
        createSectionNewHeader(main);
        createSectionEditHeader(main);
        createSectionExport(main);

        setScreenToValues();

        return main;
    }

    private void createSectionCommon(Composite parent) {

        Label labelUseParameterSections = new Label(parent, SWT.NONE);
        labelUseParameterSections.setLayoutData(createLabelLayoutData());
        labelUseParameterSections.setText(Messages.Use_parameter_sections_colon);
        labelUseParameterSections.setToolTipText(Messages.Tooltip_Use_parameter_sections);

        checkboxUseParameterSections = WidgetFactory.createCheckbox(parent);
        checkboxUseParameterSections.setToolTipText(Messages.Tooltip_Use_parameter_sections);
        checkboxUseParameterSections.setLayoutData(createLayoutData(1));
        checkboxUseParameterSections.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if (validateUseParameterSections()) {
                    validateAll();
                }
                setControlsEnablement();
            }
        });
    }

    private void createSectionNewHeader(Composite parent) {

        Group groupAddHeader = new Group(parent, SWT.NONE);
        groupAddHeader.setLayout(new GridLayout(3, false));
        groupAddHeader.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
        groupAddHeader.setText(Messages.Add_header);

        Label labelDefaultSection = new Label(groupAddHeader, SWT.NONE);
        labelDefaultSection.setLayoutData(createLabelLayoutData());
        labelDefaultSection.setText(Messages.Default_section_colon);
        labelDefaultSection.setToolTipText(Messages.Tooltip_Default_section);

        comboDefaultSection = WidgetFactory.createReadOnlyCombo(groupAddHeader);
        comboDefaultSection.setToolTipText(Messages.Tooltip_Default_section);
        comboDefaultSection.setLayoutData(createFillLayoutData(2));
        comboDefaultSection.setItems(Preferences.getInstance().getSections());
        comboDefaultSection.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if (validateUseImportantSection()) {
                    validateAll();
                }
                setControlsEnablement();
            }
        });

        Label labelUseTemplateFolder = new Label(groupAddHeader, SWT.NONE);
        labelUseTemplateFolder.setLayoutData(createLabelLayoutData());
        labelUseTemplateFolder.setText(Messages.Use_template_directory_colon);
        labelUseTemplateFolder.setToolTipText(Messages.Tooltip_Use_template_directory_colon);

        checkboxUseTemplateFolder = WidgetFactory.createCheckbox(groupAddHeader);
        checkboxUseTemplateFolder.setToolTipText(Messages.Tooltip_Use_template_directory_colon);
        checkboxUseTemplateFolder.setLayoutData(createLayoutData(2));
        checkboxUseTemplateFolder.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                if (validateUseTemplateDirectory()) {
                    validateAll();
                }
                setControlsEnablement();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        Label labelTemplatesDirectory = new Label(groupAddHeader, SWT.NONE);
        labelTemplatesDirectory.setLayoutData(createLabelLayoutData());
        labelTemplatesDirectory.setText(Messages.Templates_directory_colon);
        labelTemplatesDirectory.setToolTipText(Messages.Tooltip_Templates_directory_colon);

        textTemplateFolder = WidgetFactory.createText(groupAddHeader);
        textTemplateFolder.setToolTipText(Messages.Tooltip_Templates_directory_colon);
        GridData sourceFileSearchSaveDirectoryLayoutData = createFillLayoutData(1);
        sourceFileSearchSaveDirectoryLayoutData.widthHint = 200;
        textTemplateFolder.setLayoutData(sourceFileSearchSaveDirectoryLayoutData);
        textTemplateFolder.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateTemplateDirectory()) {
                    validateAll();
                }
                setControlsEnablement();
            }
        });

        buttonSelectTemplateFolder = WidgetFactory.createPushButton(groupAddHeader, Messages.Browse + "..."); //$NON-NLS-1$
        buttonSelectTemplateFolder.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {

                DirectoryDialog dialog = new DirectoryDialog(getShell());
                dialog.setFilterPath(getFilterPath());
                String directory = dialog.open();
                if (directory != null) {
                    textTemplateFolder.setText(directory);
                }
            }

            private String getFilterPath() {
                if (!StringHelper.isNullOrEmpty(textTemplateFolder.getText())) {
                    File directory = new File(textTemplateFolder.getText());
                    if (directory.exists()) {
                        if (directory.isDirectory()) {
                            return directory.getAbsolutePath();
                        } else {
                            return directory.getParentFile().getAbsolutePath();
                        }
                    }
                }
                return Preferences.getInstance().getInitialTemplateDirectory();
            }
        });

    }

    private void createSectionEditHeader(Composite parent) {

        Group groupEditHeader = new Group(parent, SWT.NONE);
        groupEditHeader.setLayout(new GridLayout(3, false));
        groupEditHeader.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
        groupEditHeader.setText(Messages.Edit_header);

        Label labelSkipEditDialog = new Label(groupEditHeader, SWT.NONE);
        labelSkipEditDialog.setLayoutData(createLabelLayoutData());
        labelSkipEditDialog.setText(Messages.Skip_edit_dialog_colon);
        labelSkipEditDialog.setToolTipText(Messages.Tooltip_Skip_edit_dialog_colon);

        checkboxSkipEditDialog = WidgetFactory.createCheckbox(groupEditHeader);
        checkboxSkipEditDialog.setToolTipText(Messages.Tooltip_Skip_edit_dialog_colon);
        checkboxSkipEditDialog.setLayoutData(createLayoutData(1));
        checkboxSkipEditDialog.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if (validateSkipEditDialog()) {
                    validateAll();
                }
                setControlsEnablement();
            }
        });
    }

    private void createSectionExport(Composite parent) {

        buttonExportTemplates = WidgetFactory.createPushButton(parent, Messages.Export);
        buttonExportTemplates.setLayoutData(createButtonLayoutData(2));
        buttonExportTemplates.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                String errorMessage = HeaderTemplates.getInstance().save(textTemplateFolder.getText());
                updateNumberOfCachedTemplates();
                if (!StringHelper.isNullOrEmpty(errorMessage)) {
                    MessageDialog.openError(getShell(), Messages.E_R_R_O_R, errorMessage);
                } else {
                    MessageDialog.openInformation(getShell(), Messages.Export, Messages.Templates_successfully_exported_to_folder_colon + "\n\n"
                        + textTemplateFolder.getText());
                }
            }
        });
        buttonExportTemplates.setToolTipText(Messages.Tooltip_Export);

        buttonReloadTemplates = WidgetFactory.createPushButton(parent, Messages.Clear_Cache);
        buttonReloadTemplates.setLayoutData(createButtonLayoutData(1));
        buttonReloadTemplates.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                performClearTemplateCache(true);
            }
        });
        buttonReloadTemplates.setToolTipText(Messages.Tooltip_Clear_Cache);

        labelTemplatesCount = new Label(parent, SWT.NONE);
        updateNumberOfCachedTemplates();
    }

    @Override
    protected void performApply() {
        performClearTemplateCache(false);
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
        performClearTemplateCache(false);
        setStoreToValues();
        return super.performOk();
    }

    protected void setStoreToValues() {

        Preferences preferences = Preferences.getInstance();

        preferences.setUseParameterSections(checkboxUseParameterSections.getSelection());
        preferences.setDefaultSection(comboDefaultSection.getText());

        preferences.setTemplateDirectory(textTemplateFolder.getText());
        preferences.setUseTemplateDirectory(checkboxUseTemplateFolder.getSelection());
        preferences.setSkipEditDialog(checkboxSkipEditDialog.getSelection());
    }

    protected void setScreenToValues() {

        ISpherePlugin.getDefault();

        Preferences preferences = Preferences.getInstance();

        checkboxUseParameterSections.setSelection(preferences.useParameterSections());
        comboDefaultSection.setText(preferences.getDefaultSection());

        textTemplateFolder.setText(preferences.getTemplateDirectory());
        checkboxUseTemplateFolder.setSelection(preferences.useTemplateDirectory());
        checkboxSkipEditDialog.setSelection(preferences.skipEditDialog());

        validateAll();
        setControlsEnablement();
    }

    protected void setScreenToDefaultValues() {

        Preferences preferences = Preferences.getInstance();

        checkboxUseParameterSections.setSelection(preferences.getInitialUseParameterSections());
        comboDefaultSection.setText(preferences.getInitialDefaultSection());

        textTemplateFolder.setText(preferences.getInitialTemplateDirectory());
        checkboxUseTemplateFolder.setSelection(preferences.getInitialUseTemplateDirectory());
        checkboxSkipEditDialog.setSelection(preferences.getInitialSkipEditDialog());

        validateAll();
        setControlsEnablement();
    }

    private void performClearTemplateCache(boolean enforce) {

        Preferences preferences = Preferences.getInstance();

        boolean isDirty = false;

        if (checkboxUseTemplateFolder.getSelection() != preferences.useTemplateDirectory()) {
            isDirty = true;
        }

        if (textTemplateFolder.getText().equalsIgnoreCase(preferences.getTemplateDirectory())) {
            isDirty = true;
        }

        if (enforce || isDirty) {
            HeaderTemplates.getInstance().clearTemplatesCache();
        }

        updateNumberOfCachedTemplates();
    }

    private void updateNumberOfCachedTemplates() {
        labelTemplatesCount.setText(Messages.bind(Messages.Cached_templates_A, HeaderTemplates.getInstance().getNumberOfTemplates()));
    }

    private boolean validateUseParameterSections() {

        return true;
    }

    private boolean validateUseImportantSection() {

        return true;
    }

    private boolean validateUseTemplateDirectory() {

        return true;
    }

    private boolean validateTemplateDirectory() {

        return validateDirectory(textTemplateFolder, !checkboxUseTemplateFolder.getSelection());
    }

    private boolean validateDirectory(Text textDirectory, boolean emptyAllowed) {

        String path = textDirectory.getText();
        if (StringHelper.isNullOrEmpty(path)) {
            if (!emptyAllowed) {
                setError(Messages.Directory_must_not_be_empty);
                return false;
            } else {
                return true;
            }
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

    private boolean validateSkipEditDialog() {

        return true;
    }

    private boolean validateAll() {

        if (!validateUseParameterSections()) {
            return false;
        }

        if (!validateUseImportantSection()) {
            return false;
        }

        if (!validateUseTemplateDirectory()) {
            return false;
        }

        if (!validateTemplateDirectory()) {
            return false;
        }

        if (!validateSkipEditDialog()) {
            return false;
        }

        return clearError();
    }

    private void setControlsEnablement() {

        if (checkboxUseParameterSections.getSelection()) {
            comboDefaultSection.setEnabled(true);
        } else {
            comboDefaultSection.setEnabled(false);
        }

        if (checkboxUseTemplateFolder.getSelection()) {
            textTemplateFolder.setEnabled(true);
            buttonSelectTemplateFolder.setEnabled(true);
        } else {
            textTemplateFolder.setEnabled(false);
            buttonSelectTemplateFolder.setEnabled(false);
        }

        if (isValid() && textTemplateFolder.isEnabled()) {
            buttonExportTemplates.setEnabled(true);
            buttonReloadTemplates.setEnabled(true);
        } else {
            buttonExportTemplates.setEnabled(false);
            buttonReloadTemplates.setEnabled(false);
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
        return createLayoutData(1);
    }

    private GridData createButtonLayoutData(int horizontalSpan) {
        GridData gd = createLayoutData(horizontalSpan);
        gd.widthHint = 120;
        return gd;
    }

    private GridData createLayoutData(int horizontalSpan) {
        return new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, horizontalSpan, 1);
    }

    private GridData createFillLayoutData(int horizontalSpan) {
        return new GridData(SWT.FILL, SWT.BEGINNING, true, false, horizontalSpan, 1);
    }
}
