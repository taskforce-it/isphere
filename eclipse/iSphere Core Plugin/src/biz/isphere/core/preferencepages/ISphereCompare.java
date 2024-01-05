/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.preferencepages;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.compareeditor.LoadPreviousMemberValue;
import biz.isphere.core.compareeditor.LoadPreviousStreamFileValue;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.extension.handler.WidgetFactoryContributionsHandler;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;

public class ISphereCompare extends PreferencePage implements IWorkbenchPreferencePage {

    private static final String[] IMPORT_FILE_EXTENSIONS = new String[] { "*.properties", "*.*" }; //$NON-NLS-1$ //$NON-NLS-2$

    private Text textMessageFileCompareLineWith;
    private boolean hasIBMiHostContribution;

    private Combo chkLoadingPreviousValuesRightMemberEnabled;
    private Combo chkLoadingPreviousValuesAncestorMemberEnabled;
    private Combo chkLoadingPreviousValuesRightStreamFileEnabled;
    private Combo chkLoadingPreviousValuesAncestorStreamFileEnabled;
    private Button chkIgnoreWhiteSpaces;

    private Table tblFileExtensions;
    private Button btnNew;
    private Button btnEdit;
    private Button btnRemove;
    private Button btnExport;
    private Button btnImport;

    public ISphereCompare() {
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
        tabSourceFiles.setControl(createTabSourceMembers(tabFolder));

        TabItem tabStreamFiles = new TabItem(tabFolder, SWT.NONE);
        tabStreamFiles.setText(Messages.Stream_Files);
        tabStreamFiles.setControl(createTabStreamFiles(tabFolder));

        TabItem tabMessageFiles = new TabItem(tabFolder, SWT.NONE);
        tabMessageFiles.setText(Messages.Message_files);
        tabMessageFiles.setControl(createTabMessageFiles(tabFolder));

        setScreenToValues();

        tabFolder.pack(true);

        return tabFolder;
    }

    private Composite createTabMessageFiles(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

        createSectionMessageFileCompare(container);

        return container;
    }

    private Composite createTabSourceMembers(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

        createSectionSourceMemberCompareDialog(container);

        createSectionSourceMemberCompareEditor(container);

        return container;
    }

    private Composite createTabStreamFiles(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

        createSectionSourceStreamFileCompareDialog(container);

        return container;
    }

    private void createSectionMessageFileCompare(Composite parent) {

        /*
         * Does not work without the contribution, because we cannot create an
         * AS400 object, when loading a search result.
         */
        if (!hasIBMiHostContribution) {
            return;
        }

        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(3, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        group.setText(Messages.Message_descriptions_compare);

        Label labelMessageFileSearchResultsAutoSaveFileName = new Label(group, SWT.NONE);
        labelMessageFileSearchResultsAutoSaveFileName.setLayoutData(createLabelLayoutData());
        labelMessageFileSearchResultsAutoSaveFileName.setText(Messages.Line_width_colon);
        labelMessageFileSearchResultsAutoSaveFileName
            .setToolTipText(Messages.Tooltip_Line_with_of_word_wrap_of_first_and_second_level_text_when_comparing_message_descriptions);

        textMessageFileCompareLineWith = WidgetFactory.createIntegerText(group);
        textMessageFileCompareLineWith
            .setToolTipText(Messages.Tooltip_Line_with_of_word_wrap_of_first_and_second_level_text_when_comparing_message_descriptions);
        textMessageFileCompareLineWith.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        textMessageFileCompareLineWith.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateMessageFileCompareLineWidth()) {
                    checkAllValues();
                }
            }
        });
    }

    private void createSectionSourceMemberCompareDialog(Composite parent) {

        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        group.setText(Messages.Source_member_compare_dialog);

        Label labelLoadingPreviousValuesRightMemberEnabled = new Label(group, SWT.NONE);
        labelLoadingPreviousValuesRightMemberEnabled.setText(Messages.Load_previous_values_right);
        labelLoadingPreviousValuesRightMemberEnabled.setToolTipText(Messages.Tooltip_Load_previous_values_right);

        chkLoadingPreviousValuesRightMemberEnabled = WidgetFactory.createReadOnlyCombo(group);
        chkLoadingPreviousValuesRightMemberEnabled.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        chkLoadingPreviousValuesRightMemberEnabled.setToolTipText(Messages.Tooltip_Load_previous_values_right);
        chkLoadingPreviousValuesRightMemberEnabled.setItems(loadPreviousMemberValuesItems());
        chkLoadingPreviousValuesRightMemberEnabled.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                checkAllValues();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        Label labelLoadingPreviousValuesAncestorMemberEnabled = new Label(group, SWT.NONE);
        labelLoadingPreviousValuesAncestorMemberEnabled.setText(Messages.Load_previous_values_ancestor);
        labelLoadingPreviousValuesAncestorMemberEnabled.setToolTipText(Messages.Tooltip_Load_previous_values_ancestor);

        chkLoadingPreviousValuesAncestorMemberEnabled = WidgetFactory.createReadOnlyCombo(group);
        chkLoadingPreviousValuesAncestorMemberEnabled.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        chkLoadingPreviousValuesAncestorMemberEnabled.setToolTipText(Messages.Tooltip_Load_previous_values_ancestor);
        chkLoadingPreviousValuesAncestorMemberEnabled.setItems(loadPreviousMemberValuesItems());
        chkLoadingPreviousValuesAncestorMemberEnabled.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                checkAllValues();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });
    }

    private void createSectionSourceStreamFileCompareDialog(Composite parent) {

        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        group.setText(Messages.Source_stream_file_compare_dialog);

        Label labelLoadingPreviousValuesRightStreamFileEnabled = new Label(group, SWT.NONE);
        labelLoadingPreviousValuesRightStreamFileEnabled.setText(Messages.Load_previous_values_right);
        labelLoadingPreviousValuesRightStreamFileEnabled.setToolTipText(Messages.Tooltip_Load_previous_stream_file_values_right);

        chkLoadingPreviousValuesRightStreamFileEnabled = WidgetFactory.createReadOnlyCombo(group);
        chkLoadingPreviousValuesRightStreamFileEnabled.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        chkLoadingPreviousValuesRightStreamFileEnabled.setToolTipText(Messages.Tooltip_Load_previous_stream_file_values_right);
        chkLoadingPreviousValuesRightStreamFileEnabled.setItems(loadPreviousStreamFileValuesItems());
        chkLoadingPreviousValuesRightStreamFileEnabled.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                checkAllValues();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        Label labelLoadingPreviousValuesAncestorStreamFileEnabled = new Label(group, SWT.NONE);
        labelLoadingPreviousValuesAncestorStreamFileEnabled.setText(Messages.Load_previous_values_ancestor);
        labelLoadingPreviousValuesAncestorStreamFileEnabled.setToolTipText(Messages.Tooltip_Load_previous_stream_file_values_ancestor);

        chkLoadingPreviousValuesAncestorStreamFileEnabled = WidgetFactory.createReadOnlyCombo(group);
        chkLoadingPreviousValuesAncestorStreamFileEnabled.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        chkLoadingPreviousValuesAncestorStreamFileEnabled.setToolTipText(Messages.Tooltip_Load_previous_stream_file_values_ancestor);
        chkLoadingPreviousValuesAncestorStreamFileEnabled.setItems(loadPreviousStreamFileValuesItems());
        chkLoadingPreviousValuesAncestorStreamFileEnabled.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                checkAllValues();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });
    }

    private void createSectionSourceMemberCompareEditor(Composite parent) {

        Group group = new Group(parent, SWT.NONE);
        GridLayout groupLayout = new GridLayout(2, false);
        groupLayout.marginBottom = 10;
        groupLayout.marginWidth = 0;
        groupLayout.horizontalSpacing = 2;
        groupLayout.verticalSpacing = 4;
        group.setLayout(groupLayout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
        group.setText(Messages.Source_member_compare_editor);

        Composite options = new Composite(group, SWT.NONE);
        options.setLayout(new GridLayout());
        options.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));

        chkIgnoreWhiteSpaces = WidgetFactory.createCheckbox(options, Messages.Ignore_white_spaces);
        chkIgnoreWhiteSpaces.setToolTipText(Messages.Tooltip_Ignore_white_spaces);
        chkIgnoreWhiteSpaces.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        chkIgnoreWhiteSpaces.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                checkAllValues();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        Label lblFileExtensions = new Label(options, SWT.NONE);
        lblFileExtensions.setText(Messages.Compare_Filter_File_extensions);
        lblFileExtensions.setToolTipText(Messages.Tooltip_Compare_Filter_File_extensions);
        lblFileExtensions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite tblComposite = new Composite(group, SWT.NONE);
        tblComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
        tblComposite.setLayout(new GridLayout(1, false));

        tblFileExtensions = new Table(tblComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        GridData gd_tblFileExtensions = new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1);
        gd_tblFileExtensions.heightHint = 250;
        tblFileExtensions.setLayoutData(gd_tblFileExtensions);
        tblFileExtensions.setHeaderVisible(false);
        tblFileExtensions.setLinesVisible(true);
        tblFileExtensions.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent anEvent) {
                setControlsEnablement();
            }

            public void widgetDefaultSelected(SelectionEvent anEvent) {
                performEdit(anEvent);
            }
        });

        TableColumn tblclmnFileExtension = new TableColumn(tblFileExtensions, SWT.NONE);
        tblclmnFileExtension.setWidth(220);
        tblclmnFileExtension.setText(Messages.Compare_Filter_File_extensions);

        Composite btnComposite = new Composite(group, SWT.NONE);
        RowLayout rl_btnComposite = new RowLayout(SWT.VERTICAL);
        rl_btnComposite.wrap = false;
        rl_btnComposite.fill = true;
        rl_btnComposite.pack = false;
        rl_btnComposite.marginBottom = 0;
        rl_btnComposite.marginTop = 0;
        rl_btnComposite.marginRight = 0;
        btnComposite.setLayout(rl_btnComposite);
        btnComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        btnNew = WidgetFactory.createPushButton(btnComposite);
        btnNew.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent anEvent) {
                performNew(anEvent);
            }
        });
        btnNew.setText(Messages.Button_New);

        btnEdit = WidgetFactory.createPushButton(btnComposite);
        btnEdit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent anEvent) {
                performEdit(anEvent);
            }
        });
        btnEdit.setText(Messages.Button_Edit);

        btnRemove = WidgetFactory.createPushButton(btnComposite);
        btnRemove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent anEvent) {
                performRemove(anEvent);
            }
        });
        btnRemove.setText(Messages.Button_Remove);
        btnRemove.setLayoutData(new RowData(76, SWT.DEFAULT));

        new Label(btnComposite, SWT.HORIZONTAL);

        btnExport = WidgetFactory.createPushButton(btnComposite);
        btnExport.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent anEvent) {
                performExport(anEvent);
            }
        });
        btnExport.setText(Messages.Button_Export);

        btnImport = WidgetFactory.createPushButton(btnComposite);
        btnImport.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent anEvent) {
                performImport(anEvent);
            }
        });
        btnImport.setText(Messages.Button_Import);
    }

    private String[] loadPreviousMemberValuesItems() {
        return new String[] { LoadPreviousMemberValue.NONE.label(), LoadPreviousMemberValue.CONNECTION_LIBRARY_FILE_MEMBER.label(),
            LoadPreviousMemberValue.CONNECTION_LIBRARY_FILE.label(), LoadPreviousMemberValue.CONNECTION_LIBRARY.label(),
            LoadPreviousMemberValue.CONNECTION.label() };
    }

    private String[] loadPreviousStreamFileValuesItems() {
        return new String[] { LoadPreviousStreamFileValue.NONE.label(), LoadPreviousStreamFileValue.CONNECTION_DIRECTORY_FILE.label(),
            LoadPreviousStreamFileValue.CONNECTION_DIRECTORY.label(), LoadPreviousStreamFileValue.CONNECTION.label() };
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

        Preferences preferences = getPreferences();

        if (hasIBMiHostContribution) {
            int defaultLineWidth = preferences.getDefaultMessageFileCompareMinLineWidth();
            preferences.setMessageFileCompareLineWidth(IntHelper.tryParseInt(textMessageFileCompareLineWith.getText(), defaultLineWidth));
        }

        preferences.setSourceMemberCompareIgnoreWhiteSpaces(chkIgnoreWhiteSpaces.getSelection());

        LoadPreviousMemberValue previousMemberValue;
        previousMemberValue = LoadPreviousMemberValue.valueOfLabel(chkLoadingPreviousValuesRightMemberEnabled.getText());
        preferences.setSourceMemberCompareLoadingPreviousValuesOfRightMemberEnabled(previousMemberValue);
        previousMemberValue = LoadPreviousMemberValue.valueOfLabel(chkLoadingPreviousValuesAncestorMemberEnabled.getText());
        preferences.setSourceMemberCompareLoadingPreviousValuesOfAncestorMemberEnabled(previousMemberValue);

        LoadPreviousStreamFileValue previousStreamFileValue;
        previousStreamFileValue = LoadPreviousStreamFileValue.valueOfLabel(chkLoadingPreviousValuesRightStreamFileEnabled.getText());
        preferences.setSourceStreamFileCompareLoadingPreviousValuesOfRightStreamFileEnabled(previousStreamFileValue);
        previousStreamFileValue = LoadPreviousStreamFileValue.valueOfLabel(chkLoadingPreviousValuesAncestorStreamFileEnabled.getText());
        preferences.setSourceStreamFileCompareLoadingPreviousValuesOfAncestorStreamFileEnabled(previousStreamFileValue);

        preferences.setFileExtensions(getFileExtensionsArray());
    }

    protected void setScreenToValues() {

        ISpherePlugin.getDefault();

        Preferences preferences = getPreferences();

        if (hasIBMiHostContribution) {
            textMessageFileCompareLineWith.setText(Integer.toString(preferences.getMessageFileCompareLineWidth()));
        }

        chkIgnoreWhiteSpaces.setSelection(preferences.isSourceMemberCompareIgnoreWhiteSpaces());

        setPreviousMemberValueSelection(chkLoadingPreviousValuesRightMemberEnabled,
            preferences.getSourceMemberCompareLoadingPreviousValuesOfRightMember());
        setPreviousMemberValueSelection(chkLoadingPreviousValuesAncestorMemberEnabled,
            preferences.getSourceMemberCompareLoadingPreviousValuesOfAncestorMember());

        setPreviousStreamFileValueSelection(chkLoadingPreviousValuesRightStreamFileEnabled,
            preferences.getSourceStreamFileCompareLoadingPreviousValuesOfRightStreamFile());
        setPreviousStreamFileValueSelection(chkLoadingPreviousValuesAncestorStreamFileEnabled,
            preferences.getSourceStreamFileCompareLoadingPreviousValuesOfAncestorStreamFile());

        String[] fileExtensions = preferences.getFileExtensions();
        setFileExtensionsArray(fileExtensions);

        checkAllValues();
        setControlsEnablement();
    }

    private void setPreviousMemberValueSelection(Combo combo, LoadPreviousMemberValue value) {
        String[] items = combo.getItems();
        for (int i = 0; i < items.length; i++) {
            if (value.label().equals(items[i])) {
                combo.select(i);
                return;
            }
        }
        setPreviousMemberValueSelection(combo, LoadPreviousMemberValue.NONE);
    }

    private void setPreviousStreamFileValueSelection(Combo combo, LoadPreviousStreamFileValue value) {
        String[] items = combo.getItems();
        for (int i = 0; i < items.length; i++) {
            if (value.label().equals(items[i])) {
                combo.select(i);
                return;
            }
        }
        setPreviousStreamFileValueSelection(combo, LoadPreviousStreamFileValue.NONE);
    }

    protected void setScreenToDefaultValues() {

        Preferences preferences = getPreferences();

        if (hasIBMiHostContribution) {
            textMessageFileCompareLineWith.setText(Integer.toString(preferences.getDefaultMessageFileCompareMinLineWidth()));
        }

        chkIgnoreWhiteSpaces.setSelection(preferences.getDefaultSourceMemberCompareIgnoreWhiteSpaces());

        setPreviousMemberValueSelection(chkLoadingPreviousValuesRightMemberEnabled,
            preferences.getDefaultSourceMemberCompareLoadingPreviousValuesEnabled());
        setPreviousMemberValueSelection(chkLoadingPreviousValuesAncestorMemberEnabled,
            preferences.getDefaultSourceMemberCompareLoadingPreviousValuesEnabled());

        setPreviousStreamFileValueSelection(chkLoadingPreviousValuesRightStreamFileEnabled,
            preferences.getDefaultSourceStreamFileCompareLoadingPreviousValuesEnabled());
        setPreviousStreamFileValueSelection(chkLoadingPreviousValuesAncestorStreamFileEnabled,
            preferences.getDefaultSourceStreamFileCompareLoadingPreviousValuesEnabled());

        setFileExtensionsArray(preferences.getDefaultFileExtensions());

        checkAllValues();
        setControlsEnablement();
    }

    private boolean validateMessageFileCompareLineWidth() {

        if (!hasIBMiHostContribution) {
            return true;
        }

        int minLineWidth = 15;

        int lineWidth = IntHelper.tryParseInt(textMessageFileCompareLineWith.getText(), minLineWidth);
        if (lineWidth < minLineWidth) {
            setError(Messages.bind(Messages.Minimum_line_width_is_A_characters, minLineWidth));
            return false;
        }

        return true;
    }

    private boolean checkAllValues() {

        if (!validateMessageFileCompareLineWidth()) {
            return false;
        }

        return clearError();
    }

    private void setControlsEnablement() {

        btnNew.setEnabled(true);
        btnImport.setEnabled(true);

        if (tblFileExtensions.getSelectionCount() == 1) {
            btnEdit.setEnabled(true);
        } else {
            btnEdit.setEnabled(false);
        }

        if (tblFileExtensions.getSelectionCount() > 0) {
            btnRemove.setEnabled(true);
        } else {
            btnRemove.setEnabled(false);
        }

        if (tblFileExtensions.getItems().length > 0) {
            btnExport.setEnabled(true);
        } else {
            btnExport.setEnabled(false);
        }
    }

    private String[] getFileExtensionsArray() {
        TableItem[] tItems = tblFileExtensions.getItems();
        String[] tFileExtensions = new String[tItems.length];
        for (int i = 0; i < tItems.length; i++) {
            tFileExtensions[i] = tItems[i].getText();
        }
        return tFileExtensions;
    }

    private void setFileExtensionsArray(String[] aFileExtensions) {
        tblFileExtensions.removeAll();
        for (String tExtension : aFileExtensions) {
            new TableItem(tblFileExtensions, SWT.NONE).setText(tExtension);
        }
        // TODO: sort file extensions array
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

    private void performNew(SelectionEvent anEvent) {
        FileExtensionEditor tEditor = FileExtensionEditor.getEditorForNew(getShell(), tblFileExtensions);
        if (tEditor.open() == SWT.OK) {
            // TODO: sort file extensions array
        }

        setControlsEnablement();
    }

    private void performEdit(SelectionEvent anEvent) {
        FileExtensionEditor tEditor = FileExtensionEditor.getEditorForEdit(getShell(), tblFileExtensions);
        if (tEditor.open() == SWT.OK) {
            // TODO: sort file extensions array
        }
    }

    private void performRemove(SelectionEvent anEvent) {
        if (tblFileExtensions.getSelectionCount() <= 0) {
            return;
        }
        tblFileExtensions.remove(tblFileExtensions.getSelectionIndices());
        tblFileExtensions.redraw();

        setControlsEnablement();
    }

    private void performExport(SelectionEvent anEvent) {
        WidgetFactoryContributionsHandler factory = new WidgetFactoryContributionsHandler();
        IFileDialog tFileDialog = factory.getFileDialog(getShell(), SWT.SAVE);
        tFileDialog.setText(Messages.Export_Compare_Filter_File_Extensions);
        tFileDialog.setFileName("CompareFilterFileExtensions"); //$NON-NLS-1$
        tFileDialog.setFilterPath(Preferences.getInstance().getImportExportLocation());
        tFileDialog.setFilterExtensions(IMPORT_FILE_EXTENSIONS);
        tFileDialog.setFilterIndex(0);
        tFileDialog.setOverwrite(true);

        String tExportFile = tFileDialog.open();
        if (tExportFile == null) {
            return;
        }

        if (exportCompareFileExtensions(tExportFile, getFileExtensionsArray())) {
            Preferences.getInstance().setImportExportLocation(tExportFile);
        }
    }

    private void performImport(SelectionEvent anEvent) {
        WidgetFactoryContributionsHandler factory = new WidgetFactoryContributionsHandler();
        IFileDialog fileDialog = factory.getFileDialog(getShell(), SWT.OPEN);
        fileDialog.setText(Messages.Import_Compare_Filter_File_Extensions);
        fileDialog.setFileName(""); //$NON-NLS-1$
        fileDialog.setFilterPath(Preferences.getInstance().getImportExportLocation());
        fileDialog.setFilterExtensions(IMPORT_FILE_EXTENSIONS);
        fileDialog.setFilterIndex(0);

        String location = fileDialog.open();
        if (location != null) {
            importCompareFileExtensions(location);
            setControlsEnablement();
        }
    }

    private boolean exportCompareFileExtensions(String aLocation, String[] aFileExtensions) {

        try {
            Properties tExportData = new Properties();
            for (String tItem : aFileExtensions) {
                tExportData.put(tItem, ""); //$NON-NLS-1$
            }

            FileOutputStream tOutStream = new FileOutputStream(aLocation);
            tExportData.store(tOutStream, "Compare Filter File Extensions"); //$NON-NLS-1$
            tOutStream.flush();
            tOutStream.close();
            return true;
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
        }

        return false;
    }

    private boolean importCompareFileExtensions(String aLocation) {

        try {
            Properties tImportData = new Properties();
            FileInputStream tInputStream = new FileInputStream(aLocation);
            tImportData.load(tInputStream);
            tInputStream.close();

            ArrayList<String> tList = new ArrayList<String>();
            for (Object tItem : tImportData.keySet()) {
                tList.add((String)tItem);
            }
            setFileExtensionsArray(tList.toArray(new String[tList.size()]));
            return true;
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
        }

        return false;
    }

    private Preferences getPreferences() {
        return Preferences.getInstance();
    }
}