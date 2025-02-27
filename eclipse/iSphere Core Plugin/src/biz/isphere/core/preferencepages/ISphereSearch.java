/*******************************************************************************
 * Copyright (c) 2012-2023 iSphere Project Owners
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
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ColorHelper;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class ISphereSearch extends PreferencePage implements IWorkbenchPreferencePage {

    private static final String MODE_VIEW = "*BROWSE"; //$NON-NLS-1$
    private static final String MODE_EDIT = "*EDIT"; //$NON-NLS-1$

    private Button buttonSourceFileSearchBatchResolveEnabled;
    private Combo comboSourceFileSearchEditMode;
    private Text textSourceFileSearchSaveDirectory;
    private Button buttonSourceFileSearchAutoSaveEnabled;
    private Text textSourceFileSearchAutoSaveFileName;

    private Button buttonStreamFileSearchBatchResolveEnabled;
    private Combo comboStreamFileSearchEditMode;
    private Text textStreamFileSearchSaveDirectory;
    private Button buttonStreamFileSearchAutoSaveEnabled;
    private Text textStreamFileSearchAutoSaveFileName;
    private Combo comboMaxDepth;
    private Label labelMaxDepthWarning;

    private Combo comboMessageFileSearchEditMode;
    private Text textMessageFileSearchSaveDirectory;
    private Button buttonMessageFileSearchAutoSaveEnabled;
    private Text textMessageFileSearchAutoSaveFileName;

    private boolean hasIBMiHostContribution;

    public ISphereSearch() {
        super();
        setPreferenceStore(ISpherePlugin.getDefault().getPreferenceStore());
        getPreferenceStore();
    }

    public void init(IWorkbench workbench) {

        /*
         * Does not work without the contribution, because we cannot create an
         * AS400 object, when loading a search result.
         */
        if (IBMiHostContributionsHandler.hasContribution()) {
            hasIBMiHostContribution = true;
        } else {
            hasIBMiHostContribution = false;
        }

    }

    @Override
    public Control createContents(Composite parent) {

        TabFolder tabFolder = new TabFolder(parent, SWT.NONE);

        TabItem tabSourceFiles = new TabItem(tabFolder, SWT.NONE);
        tabSourceFiles.setText(Messages.Source_Files);
        tabSourceFiles.setControl(createTabSourceFiles(tabFolder));

        TabItem tabStreamFiles = new TabItem(tabFolder, SWT.NONE);
        tabStreamFiles.setText(Messages.Stream_Files);
        tabStreamFiles.setControl(createTabStreamFiles(tabFolder));

        TabItem tabMessageFiles = new TabItem(tabFolder, SWT.NONE);
        tabMessageFiles.setText(Messages.Message_files);
        tabMessageFiles.setControl(createTabMessageFiles(tabFolder));

        setScreenToValues();

        return tabFolder;
    }

    private Composite createTabSourceFiles(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

        createSectionSourceFileSearch(container);

        return container;
    }

    private Composite createTabStreamFiles(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

        createSectionStreamFileSearch(container);

        return container;
    }

    private Composite createTabMessageFiles(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

        createSectionMessageFileSearch(container);

        return container;
    }

    private void createSectionSourceFileSearch(Composite parent) {

        Group group = createGroup(parent, Messages.Source_file_search);

        buttonSourceFileSearchBatchResolveEnabled = createBatchResolveEnabledButton(group);

        comboSourceFileSearchEditMode = createEditModeCombo(group);

        textSourceFileSearchSaveDirectory = createSaveSearchToPathText(group,
            Messages.Tooltip_Specifies_the_folder_to_save_source_file_search_results_to);
        createSelectSearchResultSaveDirectoryButton(group, textSourceFileSearchSaveDirectory);
        buttonSourceFileSearchAutoSaveEnabled = createAutoSaveEnabledCheckbox(group);
        textSourceFileSearchAutoSaveFileName = createAutoSaveFileNameText(group);
    }

    private void createSectionStreamFileSearch(Composite parent) {

        Group group = createGroup(parent, Messages.Stream_file_search);

        buttonStreamFileSearchBatchResolveEnabled = createBatchResolveEnabledButton(group);

        Label labelMaxDepth = new Label(group, SWT.NONE);
        labelMaxDepth.setLayoutData(createLabelLayoutData());
        labelMaxDepth.setText(Messages.Max_depth_colon);

        comboMaxDepth = WidgetFactory.createIntegerCombo(group, true);
        comboMaxDepth.setToolTipText(Messages.Specifies_the_maximum_depth_of_sub_directories_included_in_the_search);
        comboMaxDepth.setLayoutData(createTextLayoutData(1, 100));
        comboMaxDepth.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                checkAllValues();
                updateMaxDepthWarning();
            }
        });
        comboMaxDepth.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                checkAllValues();
                updateMaxDepthWarning();
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                checkAllValues();
                updateMaxDepthWarning();
            }
        });
        comboMaxDepth.setItems(new String[] { "1", Preferences.UNLIMITTED });

        labelMaxDepthWarning = new Label(group, SWT.NONE);
        labelMaxDepthWarning.setLayoutData(createLabelLayoutData(2));
        labelMaxDepthWarning.setForeground(ColorHelper.getOrange());
        labelMaxDepthWarning.setText(Messages.Warning_Maximum_depth_set_to_more_than_one_level);

        comboStreamFileSearchEditMode = createEditModeCombo(group);

        textStreamFileSearchSaveDirectory = createSaveSearchToPathText(group,
            Messages.Tooltip_Specifies_the_folder_to_save_stream_file_search_results_to);
        createSelectSearchResultSaveDirectoryButton(group, textStreamFileSearchSaveDirectory);
        buttonStreamFileSearchAutoSaveEnabled = createAutoSaveEnabledCheckbox(group);
        textStreamFileSearchAutoSaveFileName = createAutoSaveFileNameText(group);
    }

    private void updateMaxDepthWarning() {
        if (labelMaxDepthWarning != null) {
            if (getMaxDepth() != 1) {
                labelMaxDepthWarning.setVisible(true);
            } else {
                labelMaxDepthWarning.setVisible(false);
            }
        }
    }

    private void createSectionMessageFileSearch(Composite parent) {

        /*
         * Does not work, because we cannot create an AS400 object, when loading
         * a search result.
         */
        if (!hasIBMiHostContribution) {
            return;
        }

        Group group = createGroup(parent, Messages.Message_file_search);

        comboMessageFileSearchEditMode = createEditModeCombo(group);

        textMessageFileSearchSaveDirectory = createSaveSearchToPathText(group,
            Messages.Tooltip_Specifies_the_folder_to_save_message_file_search_results_to);
        createSelectSearchResultSaveDirectoryButton(group, textMessageFileSearchSaveDirectory);
        buttonMessageFileSearchAutoSaveEnabled = createAutoSaveEnabledCheckbox(group);
        textMessageFileSearchAutoSaveFileName = createAutoSaveFileNameText(group);
    }

    private Group createGroup(Composite parent, String text) {

        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(4, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        group.setText(text);

        return group;
    }

    private Button createBatchResolveEnabledButton(Group group) {

        Label labelBatchResolveEnabled = new Label(group, SWT.NONE);
        labelBatchResolveEnabled.setLayoutData(createLabelLayoutData());
        labelBatchResolveEnabled.setText(Messages.Batch_resolve_enabled_colon);

        Button button = WidgetFactory.createCheckbox(group);
        button.setToolTipText(Messages.Batch_resolve_enabled_Tooltip);
        button.setLayoutData(createTextLayoutData(3));
        button.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                checkAllValues();
                setControlsEnablement();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        return button;
    }

    private Combo createEditModeCombo(Group group) {

        Label labelSoureFileSearchEditMode = new Label(group, SWT.NONE);
        labelSoureFileSearchEditMode.setLayoutData(createLabelLayoutData());
        labelSoureFileSearchEditMode.setText(Messages.Double_click_mode_colon);

        Combo combo = WidgetFactory.createReadOnlyCombo(group);
        combo.setToolTipText(Messages.Tooltip_Double_click_mode);
        combo.setLayoutData(createTextLayoutData(3, 100));
        combo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                checkAllValues();
            }
        });
        fillFileSearchEditModeCombo(combo);

        return combo;
    }

    private Text createSaveSearchToPathText(Composite group, String tooltip) {

        Label labelSoureFileSearchResultsSaveDirectory = new Label(group, SWT.NONE);
        labelSoureFileSearchResultsSaveDirectory.setLayoutData(createLabelLayoutData());
        labelSoureFileSearchResultsSaveDirectory.setText(Messages.Save_results_to_colon);

        Text text = WidgetFactory.createText(group);
        text.setToolTipText(tooltip);
        text.setLayoutData(createComboLayoutData(2));
        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateSourceFileSearchSaveDirectory()) {
                    checkAllValues();
                }
            }
        });

        return text;
    }

    private void createSelectSearchResultSaveDirectoryButton(Composite group, final Text text) {

        Button buttonSourceFileSearchResultsSaveDirectory = WidgetFactory.createPushButton(group, Messages.Browse + "..."); //$NON-NLS-1$
        buttonSourceFileSearchResultsSaveDirectory.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {

                DirectoryDialog dialog = new DirectoryDialog(getShell());
                dialog.setFilterPath(getFilterPath());
                String directory = dialog.open();
                if (directory != null) {
                    text.setText(directory);
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }

            private String getFilterPath() {
                if (!StringHelper.isNullOrEmpty(text.getText())) {
                    File directory = new File(text.getText());
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
    }

    private Button createAutoSaveEnabledCheckbox(Composite group) {

        Label labelSourceFileSearchResultsAutoSaveEnabled = new Label(group, SWT.NONE);
        labelSourceFileSearchResultsAutoSaveEnabled.setLayoutData(createLabelLayoutData());
        labelSourceFileSearchResultsAutoSaveEnabled.setText(Messages.Auto_save_enabled_colon);

        Button button = WidgetFactory.createCheckbox(group);
        button.setToolTipText(Messages.Auto_save_enabled_Tooltip);
        button.setLayoutData(createTextLayoutData(3));
        button.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                checkAllValues();
                setControlsEnablement();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        return button;
    }

    private Text createAutoSaveFileNameText(Composite group) {

        Label labelStreamFileSearchResultsAutoSaveFileName = new Label(group, SWT.NONE);
        labelStreamFileSearchResultsAutoSaveFileName.setLayoutData(createLabelLayoutData());
        labelStreamFileSearchResultsAutoSaveFileName.setText(Messages.Auto_save_file_name_colon);

        Text text = WidgetFactory.createText(group);
        text.setToolTipText(Messages.Auto_save_file_name_Tooltip);
        text.setLayoutData(createComboLayoutData(3));
        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateStreamFileSearchAutoSaveFileName()) {
                    checkAllValues();
                }
            }
        });

        return text;
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

    private int getMaxDepth() {
        Preferences preferences = Preferences.getInstance();
        if (Preferences.UNLIMITTED.equals(comboMaxDepth.getText())) {
            return preferences.getStreamFileSearchMaxDepthSpecialValueUnlimited();
        } else {
            return IntHelper.tryParseInt(comboMaxDepth.getText(), preferences.getStreamFileSearchMaxDepth());
        }
    }

    protected void setStoreToValues() {

        Preferences preferences = Preferences.getInstance();

        preferences.setSourceFileSearchBatchResolveEnabled(buttonSourceFileSearchBatchResolveEnabled.getSelection());
        preferences.setSourceFileSearchResultsEditEnabled(getComboSearchEditMode(comboSourceFileSearchEditMode));
        preferences.setSourceFileSearchResultsSaveDirectory(textSourceFileSearchSaveDirectory.getText());
        preferences.setSourceFileSearchResultsAutoSaveEnabled(buttonSourceFileSearchAutoSaveEnabled.getSelection());
        preferences.setSourceFileSearchResultsAutoSaveFileName(textSourceFileSearchAutoSaveFileName.getText());

        preferences.setStreamFileSearchBatchResolveEnabled(buttonStreamFileSearchBatchResolveEnabled.getSelection());
        preferences.setStreamFileSearchResultsEditEnabled(getComboSearchEditMode(comboStreamFileSearchEditMode));
        preferences.setStreamFileSearchResultsSaveDirectory(textStreamFileSearchSaveDirectory.getText());
        preferences.setStreamFileSearchResultsAutoSaveEnabled(buttonStreamFileSearchAutoSaveEnabled.getSelection());
        preferences.setStreamFileSearchResultsAutoSaveFileName(textStreamFileSearchAutoSaveFileName.getText());
        preferences.setStreamFileSearchMaxDepth(getMaxDepth());

        if (hasIBMiHostContribution) {
            preferences.setMessageFileSearchResultsSaveDirectory(textMessageFileSearchSaveDirectory.getText());
            preferences.setMessageFileSearchResultsAutoSaveEnabled(buttonMessageFileSearchAutoSaveEnabled.getSelection());
            preferences.setMessageFileSearchResultsAutoSaveFileName(textMessageFileSearchAutoSaveFileName.getText());
        }
    }

    protected void setScreenToValues() {

        ISpherePlugin.getDefault();

        Preferences preferences = Preferences.getInstance();

        buttonSourceFileSearchBatchResolveEnabled.setSelection(preferences.isSourceFileSearchBatchResolveEnabled());
        setComboSearchEditMode(comboSourceFileSearchEditMode, preferences.isSourceFileSearchResultsEditEnabled());
        textSourceFileSearchSaveDirectory.setText(preferences.getSourceFileSearchResultsAutoSaveDirectory());
        buttonSourceFileSearchAutoSaveEnabled.setSelection(preferences.isSourceFileSearchResultsAutoSaveEnabled());
        textSourceFileSearchAutoSaveFileName.setText(preferences.getSourceFileSearchResultsAutoSaveFileName());

        buttonStreamFileSearchBatchResolveEnabled.setSelection(preferences.isStreamFileSearchBatchResolveEnabled());
        setComboSearchEditMode(comboStreamFileSearchEditMode, preferences.isStreamFileSearchResultsEditEnabled());
        textStreamFileSearchSaveDirectory.setText(preferences.getStreamFileSearchResultsAutoSaveDirectory());
        buttonStreamFileSearchAutoSaveEnabled.setSelection(preferences.isStreamFileSearchResultsAutoSaveEnabled());
        textStreamFileSearchAutoSaveFileName.setText(preferences.getStreamFileSearchResultsAutoSaveFileName());

        int maxDepth = preferences.getStreamFileSearchMaxDepth();
        setScreenMaxDepth(maxDepth);

        if (hasIBMiHostContribution) {
            setComboSearchEditMode(comboMessageFileSearchEditMode, preferences.isMessageFileSearchResultsEditEnabled());
            textMessageFileSearchSaveDirectory.setText(preferences.getMessageFileSearchResultsAutoSaveDirectory());
            buttonMessageFileSearchAutoSaveEnabled.setSelection(preferences.isMessageFileSearchResultsAutoSaveEnabled());
            textMessageFileSearchAutoSaveFileName.setText(preferences.getMessageFileSearchResultsAutoSaveFileName());
        }

        checkAllValues();
        setControlsEnablement();
    }

    protected void setScreenToDefaultValues() {

        Preferences preferences = Preferences.getInstance();

        buttonSourceFileSearchBatchResolveEnabled.setSelection(preferences.getDefaultSourceFileSearchBatchResolveEnabled());
        setComboSearchEditMode(comboSourceFileSearchEditMode, preferences.getDefaultSourceFileSearchResultsEditEnabled());
        textSourceFileSearchSaveDirectory.setText(preferences.getDefaultSourceFileSearchResultsSaveDirectory());
        buttonSourceFileSearchAutoSaveEnabled.setSelection(preferences.getDefaultSourceFileSearchResultsAutoSaveEnabled());
        textSourceFileSearchAutoSaveFileName.setText(preferences.getDefaultSourceFileSearchResultsAutoSaveFileName());

        buttonStreamFileSearchBatchResolveEnabled.setSelection(preferences.getDefaultStreamFileSearchBatchResolveEnabled());
        setComboSearchEditMode(comboStreamFileSearchEditMode, preferences.getDefaultStreamFileSearchResultsEditEnabled());
        textStreamFileSearchSaveDirectory.setText(preferences.getDefaultStreamFileSearchResultsSaveDirectory());
        buttonStreamFileSearchAutoSaveEnabled.setSelection(preferences.getDefaultStreamFileSearchResultsAutoSaveEnabled());
        textStreamFileSearchAutoSaveFileName.setText(preferences.getDefaultStreamFileSearchResultsAutoSaveFileName());

        int maxDepth = preferences.getDefaultStreamFileSearchMaxDepth();
        setScreenMaxDepth(maxDepth);

        if (hasIBMiHostContribution) {
            setComboSearchEditMode(comboMessageFileSearchEditMode, preferences.getDefaultMessageFileSearchResultsEditEnabled());
            textMessageFileSearchSaveDirectory.setText(preferences.getDefaultMessageFileSearchResultsSaveDirectory());
            buttonMessageFileSearchAutoSaveEnabled.setSelection(preferences.getDefaultMessageFileSearchResultsAutoSaveEnabled());
            textMessageFileSearchAutoSaveFileName.setText(preferences.getDefaultMessageFileSearchResultsAutoSaveFileName());
        }

        checkAllValues();
        setControlsEnablement();
    }

    private void setScreenMaxDepth(int maxDepth) {
        Preferences preferences = Preferences.getInstance();
        if (maxDepth == preferences.getStreamFileSearchMaxDepthSpecialValueUnlimited()) {
            comboMaxDepth.setText(Preferences.UNLIMITTED);
        } else {
            comboMaxDepth.setText(Integer.toString(maxDepth));
        }
    }

    private boolean getComboSearchEditMode(Combo combo) {

        if (MODE_EDIT.equals(combo.getText())) {
            return true;
        } else {
            return false;
        }
    }

    private void setComboSearchEditMode(Combo combo, boolean enabled) {

        if (enabled) {
            combo.setText(MODE_EDIT);
        } else {
            combo.setText(MODE_VIEW);
        }
    }

    private void fillFileSearchEditModeCombo(Combo combo) {

        String[] textViews = new String[] { MODE_EDIT, MODE_VIEW };
        combo.setItems(textViews);
    }

    // private boolean validateBatchResolveEnabled() {
    //
    // return true;
    // }
    //
    // private boolean validateSourceFileSearchEditMode() {
    //
    // if (comboSourceFileSearchEditMode == null) {
    // return true;
    // }
    //
    // return clearError();
    // }

    private boolean validateSourceFileSearchSaveDirectory() {

        return validateSearchSaveDirectory(textSourceFileSearchSaveDirectory);
    }

    // private boolean validateSourceFileSearchAutoSaveEnabled() {
    //
    // return true;
    // }

    private boolean validateSourceFileSearchAutoSaveFileName() {

        return validateSearchAutoSaveFileName(textSourceFileSearchAutoSaveFileName);
    }

    private boolean validateStreamFileSearchSaveDirectory() {

        return validateSearchSaveDirectory(textSourceFileSearchSaveDirectory);
    }

    private boolean validateStreamFileSearchAutoSaveFileName() {

        return validateSearchAutoSaveFileName(textSourceFileSearchAutoSaveFileName);
    }

    // private boolean validateMessageFileSearchEditMode() {
    //
    // if (comboMessageFileSearchEditMode == null) {
    // return true;
    // }
    //
    // return clearError();
    // }

    private boolean validateMessageFileSearchSaveDirectory() {

        return validateSearchSaveDirectory(textMessageFileSearchSaveDirectory);
    }

    // private boolean validateMessageFileSearchAutoSaveEnabled() {
    //
    // return true;
    // }

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

        if (!validateSourceFileSearchSaveDirectory()) {
            return false;
        }

        if (!validateSourceFileSearchAutoSaveFileName()) {
            return false;
        }

        if (!validateStreamFileSearchSaveDirectory()) {
            return false;
        }

        if (!validateStreamFileSearchAutoSaveFileName()) {
            return false;
        }

        if (!validateMessageFileSearchSaveDirectory()) {
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

        if (buttonStreamFileSearchAutoSaveEnabled.getSelection()) {
            textStreamFileSearchAutoSaveFileName.setEnabled(true);
        } else {
            textStreamFileSearchAutoSaveFileName.setEnabled(false);
        }

        if (hasIBMiHostContribution) {
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

    private GridData createLabelLayoutData(int horizontalSpan) {
        return new GridData(SWT.LEFT, SWT.CENTER, false, false, horizontalSpan, 1);
    }

    private GridData createTextLayoutData(int horizontalSpan) {
        return new GridData(SWT.FILL, SWT.CENTER, true, false, horizontalSpan, 1);
    }

    private GridData createTextLayoutData(int horizontalSpan, int width) {
        GridData gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, horizontalSpan, 1);
        gridData.widthHint = width;
        return gridData;
    }

    private GridData createComboLayoutData(int horizontalSpan) {
        return createTextLayoutData(horizontalSpan, 300);
    }
}