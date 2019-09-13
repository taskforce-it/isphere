/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.comparefilter.contributions.extension.handler.CompareFilterContributionsHandler;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.extension.handler.WidgetFactoryContributionsHandler;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;

public class ISphereCompare extends PreferencePage implements IWorkbenchPreferencePage {

    private static final String[] IMPORT_FILE_EXTENSIONS = new String[] { "*.properties", "*.*" }; //$NON-NLS-1$ //$NON-NLS-2$

    private Text textMessageFileCompareLineWith;
    private boolean messageFileCompareEnabled;

    private Button chkLoadingPreviousValuesRightMemberEnabled;
    private Button chkLoadingPreviousValuesAncestorMemberEnabled;

    private Table tblFileExtensions;
    private Button btnNew;
    private Button btnEdit;
    private Button btnRemove;
    private Button btnExport;
    private Button btnImport;
    private boolean sourceFileCompareEnabled;

    public ISphereCompare() {
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
            messageFileCompareEnabled = true;
        } else {
            messageFileCompareEnabled = false;
        }

        if (CompareFilterContributionsHandler.hasContribution()) {
            sourceFileCompareEnabled = true;
        } else {
            sourceFileCompareEnabled = false;
        }

    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        container.setLayout(gridLayout);

        createSectionMessageFileCompare(container);

        createSectionSourceMemberCompare(container);

        createSectionSourceFileCompare(container);

        setScreenToValues();

        return container;
    }

    private void createSectionMessageFileCompare(Composite parent) {

        /*
         * Does not work, because we cannot create an AS400 object, when loading
         * a search result.
         */
        if (!messageFileCompareEnabled) {
            return;
        }

        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(3, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        group.setText(Messages.Message_descriptions_compare);

        Label labelMessageFileSearchResultsAutoSaveFileName = new Label(group, SWT.NONE);
        labelMessageFileSearchResultsAutoSaveFileName.setLayoutData(createLabelLayoutData());
        labelMessageFileSearchResultsAutoSaveFileName.setText(Messages.Line_width_colon);

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

    private void createSectionSourceMemberCompare(Composite parent) {

        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(1, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        group.setText(Messages.Source_member_compare_dialog);

        chkLoadingPreviousValuesRightMemberEnabled = WidgetFactory.createCheckbox(group, Messages.Load_previous_values_right);
        chkLoadingPreviousValuesRightMemberEnabled.setToolTipText(Messages.Tooltip_Load_previous_values_right);
        chkLoadingPreviousValuesRightMemberEnabled.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                checkAllValues();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        chkLoadingPreviousValuesAncestorMemberEnabled = WidgetFactory.createCheckbox(group, Messages.Load_previous_values_ancestor);
        chkLoadingPreviousValuesAncestorMemberEnabled.setToolTipText(Messages.Tooltip_Load_previous_values_ancestor);
        chkLoadingPreviousValuesAncestorMemberEnabled.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                checkAllValues();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });
    }

    private void createSectionSourceFileCompare(Composite parent) {

        if (!sourceFileCompareEnabled) {
            return;
        }

        Group group = new Group(parent, SWT.NONE);
        GridLayout groupLayout = new GridLayout(2, false);
        groupLayout.marginBottom = 10;
        groupLayout.marginWidth = 0;
        groupLayout.horizontalSpacing = 2;
        groupLayout.verticalSpacing = 4;
        group.setLayout(groupLayout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        group.setText(Messages.Source_member);

        Composite tblComposite = new Composite(group, SWT.NONE);
        tblComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        tblComposite.setLayout(new GridLayout(1, false));

        tblFileExtensions = new Table(tblComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        GridData gd_tblFileExtensions = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_tblFileExtensions.heightHint = 0;
        tblFileExtensions.setLayoutData(gd_tblFileExtensions);
        tblFileExtensions.setHeaderVisible(true);
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

        if (messageFileCompareEnabled) {
            int defaultLineWidth = preferences.getDefaultMessageFileCompareMinLineWidth();
            preferences.setMessageFileCompareLineWidth(IntHelper.tryParseInt(textMessageFileCompareLineWith.getText(), defaultLineWidth));
        }

        preferences.setSourceMemberCompareLoadingPreviousValuesOfRightMemberEnabled(chkLoadingPreviousValuesRightMemberEnabled.getSelection());
        preferences.setSourceMemberCompareLoadingPreviousValuesOfAncestorMemberEnabled(chkLoadingPreviousValuesAncestorMemberEnabled.getSelection());

        if (sourceFileCompareEnabled) {
            CompareFilterContributionsHandler.setFileExtensions(getFileExtensionsArray());
        }
    }

    protected void setScreenToValues() {

        ISpherePlugin.getDefault();

        Preferences preferences = getPreferences();

        if (messageFileCompareEnabled) {
            textMessageFileCompareLineWith.setText(Integer.toString(preferences.getMessageFileCompareLineWidth()));
        }

        chkLoadingPreviousValuesRightMemberEnabled.setSelection(preferences.isSourceMemberCompareLoadingPreviousValuesOfRightMemberEnabled());
        chkLoadingPreviousValuesAncestorMemberEnabled.setSelection(preferences.isSourceMemberCompareLoadingPreviousValuesOfAncestorMemberEnabled());

        if (sourceFileCompareEnabled) {
            String[] fileExtensions = CompareFilterContributionsHandler.getFileExtensions();
            setFileExtensionsArray(fileExtensions);
        }

        checkAllValues();
        setControlsEnablement();
    }

    protected void setScreenToDefaultValues() {

        Preferences preferences = getPreferences();

        if (messageFileCompareEnabled) {
            textMessageFileCompareLineWith.setText(Integer.toString(preferences.getDefaultMessageFileCompareMinLineWidth()));
        }

        chkLoadingPreviousValuesRightMemberEnabled.setSelection(preferences.getDefaultSourceMemberCompareLoadingPreviousValuesEnabled());
        chkLoadingPreviousValuesAncestorMemberEnabled.setSelection(preferences.getDefaultSourceMemberCompareLoadingPreviousValuesEnabled());

        if (sourceFileCompareEnabled) {
            setFileExtensionsArray(CompareFilterContributionsHandler.getDefaultFileExtensions());
        }

        checkAllValues();
        setControlsEnablement();
    }

    private boolean validateMessageFileCompareLineWidth() {

        if (!messageFileCompareEnabled) {
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

        if (sourceFileCompareEnabled) {

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
        tFileDialog.setFilterPath(CompareFilterContributionsHandler.getImportExportLocation());
        tFileDialog.setFilterExtensions(IMPORT_FILE_EXTENSIONS);
        tFileDialog.setFilterIndex(0);
        tFileDialog.setOverwrite(true);

        String tExportFile = tFileDialog.open();
        if (tExportFile == null) {
            return;
        }

        if (exportCompareFileExtensions(tExportFile, getFileExtensionsArray())) {
            CompareFilterContributionsHandler.setImportExportLocation(tExportFile);
        }
    }

    private void performImport(SelectionEvent anEvent) {
        WidgetFactoryContributionsHandler factory = new WidgetFactoryContributionsHandler();
        IFileDialog fileDialog = factory.getFileDialog(getShell(), SWT.OPEN);
        fileDialog.setText(Messages.Import_Compare_Filter_File_Extensions);
        fileDialog.setFileName(""); //$NON-NLS-1$
        fileDialog.setFilterPath(CompareFilterContributionsHandler.getImportExportLocation());
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