/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.preferencepages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.SpooledFileTransformerPDF;
import biz.isphere.core.spooledfiles.SpooledFileTransformerPDF.PageSize;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class ISphereSpooledFiles extends PreferencePage implements IWorkbenchPreferencePage {

    private static final Boolean SHOW_SUBSTITUTION_VARIABLES_YES = Boolean.TRUE;
    private static final Boolean SHOW_SUBSTITUTION_VARIABLES_NO = Boolean.FALSE;

    // Tab "Conversion"
    private Button buttonDefaultFormatText;
    private Button buttonDefaultFormatHTML;
    private Button buttonDefaultFormatPDF;
    private String defaultFormat;

    // Tab "Font"

    // Tab "Conversion": *TEXT
    private Button buttonConversionTextDefault;
    private Button buttonConversionTextUserDefined;
    private Button buttonConversionTextTransform;
    private String conversionText;
    private Text textConversionTextLibrary;
    private Validator validatorConversionTextLibrary;
    private String conversionTextLibrary;
    private Text textConversionTextCommand;
    private String conversionTextCommand;
    private Combo comboConversionTextEditAllowed;
    private boolean isConversionTextEditAllowed;

    // Tab "Conversion": *HTML
    private Button buttonConversionHTMLDefault;
    private Button buttonConversionHTMLUserDefined;
    private Button buttonConversionHTMLTransform;
    private String conversionHTML;
    private Text textConversionHTMLLibrary;
    private Validator validatorConversionHTMLLibrary;
    private String conversionHTMLLibrary;
    private Text textConversionHTMLCommand;
    private String conversionHTMLCommand;
    private Combo comboConversionHTMLEditAllowed;
    private boolean isConversionHTMLEditAllowed;

    // Tab "Conversion": *PDF
    private Button buttonConversionPDFDefault;
    private Button buttonConversionPDFUserDefined;
    private Button buttonConversionPDFTransform;
    private String conversionPDF;
    private Text textConversionPDFLibrary;
    private Validator validatorConversionPDFLibrary;
    private String conversionPDFLibrary;
    private Text textConversionPDFCommand;
    private String conversionPDFCommand;
    private Combo comboConversionPDFPageSize;
    private String conversionPDFPageSize;
    private Button chkBoxConversionPDFAdjustFontSize;
    private boolean adjustFontSize;
    private Label labelConversionPDFUsingFontSize;

    private Group groupSubstitutionVariables;

    // Tab "General"
    private Button buttonLoadAsynchronously;
    private boolean isLoadAsynchronously;
    private Text textMaxNumSpooledFiles;
    private int maxNumSpooledFiles;
    private Combo comboSuggestedFileName;
    private String suggestedFileName;
    private Text textRSEDescription;
    private String rseDescription;

    public ISphereSpooledFiles() {
        super();
        setPreferenceStore(ISpherePlugin.getDefault().getPreferenceStore());
        getPreferenceStore();
    }

    @Override
    public Control createContents(Composite parent) {

        TabFolder tabFolder = new TabFolder(parent, SWT.NONE);

        TabItem tabGeneral = new TabItem(tabFolder, SWT.NONE);
        tabGeneral.setText("General");
        tabGeneral.setControl(createTabGeneral(tabFolder));

        TabItem tabOpenSave = new TabItem(tabFolder, SWT.NONE);
        tabOpenSave.setText("Conversion");
        tabOpenSave.setControl(createTabConversion(tabFolder));

        setScreenToValues();

        tabFolder.pack(true);

        return tabFolder;
    }

    private Composite createTabGeneral(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(2, false));
        container.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

        buttonLoadAsynchronously = WidgetFactory.createCheckbox(container);
        buttonLoadAsynchronously.setLayoutData(createGroupLayoutData());
        buttonLoadAsynchronously.setText(Messages.Load_spooled_files_asynchronously);
        buttonLoadAsynchronously.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                isLoadAsynchronously = buttonLoadAsynchronously.getSelection();
            }
        });

        new Label(container, SWT.NONE).setText(Messages.Maximum_number_of_spooled_files_to_load_colon);

        textMaxNumSpooledFiles = WidgetFactory.createIntegerText(container);
        textMaxNumSpooledFiles.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkError();
            }
        });
        textMaxNumSpooledFiles.setLayoutData(createLayoutData());
        textMaxNumSpooledFiles.setTextLimit(6);

        Label labelSuggestedFileName = new Label(container, SWT.NONE);
        labelSuggestedFileName.setText(Messages.Suggested_file_name);

        comboSuggestedFileName = WidgetFactory.createCombo(container);
        comboSuggestedFileName.setLayoutData(createLayoutData());
        comboSuggestedFileName.setItems(Preferences.getInstance().getSpooledFileSuggestedNames());
        comboSuggestedFileName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                suggestedFileName = comboSuggestedFileName.getText();
            }
        });

        Label labelRSEDescription = new Label(container, SWT.NONE);
        labelRSEDescription.setText(Messages.RSE_spooled_file_text_colon);

        textRSEDescription = WidgetFactory.createText(container);
        textRSEDescription.setLayoutData(createLayoutData());
        textRSEDescription.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                rseDescription = textRSEDescription.getText();
            }
        });

        createGroupSubstitutionVariables(container, Messages.Substitution_variables_for_file_name);

        return container;
    }

    private Composite createTabConversion(Composite parent) {

        final Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(2, false));
        container.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

        // Group: Default conversion
        Group groupDefaultFormat = new Group(container, SWT.NONE);
        groupDefaultFormat.setText(Messages.When_double_clicking_on_a_spooled_file_open_it_as);
        groupDefaultFormat.setLayout(new GridLayout(3, false));
        groupDefaultFormat.setLayoutData(createGroupLayoutData());

        buttonDefaultFormatText = WidgetFactory.createRadioButton(groupDefaultFormat);
        buttonDefaultFormatText.setText(IPreferences.OUTPUT_FORMAT_TEXT);
        buttonDefaultFormatText.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                defaultFormat = IPreferences.OUTPUT_FORMAT_TEXT;
            }
        });

        buttonDefaultFormatHTML = WidgetFactory.createRadioButton(groupDefaultFormat);
        buttonDefaultFormatHTML.setText(IPreferences.OUTPUT_FORMAT_HTML);
        buttonDefaultFormatHTML.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                defaultFormat = IPreferences.OUTPUT_FORMAT_HTML;
            }
        });

        buttonDefaultFormatPDF = WidgetFactory.createRadioButton(groupDefaultFormat);
        buttonDefaultFormatPDF.setText(IPreferences.OUTPUT_FORMAT_PDF);
        buttonDefaultFormatPDF.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                defaultFormat = IPreferences.OUTPUT_FORMAT_PDF;
            }
        });

        // Group: Text conversion
        Group groupConversionText = new Group(container, SWT.NONE);
        groupConversionText.setText(Messages.Conversion_to_format + " *TEXT");
        GridLayout gridLayoutConversionText = new GridLayout();
        gridLayoutConversionText.numColumns = 3;
        groupConversionText.setLayout(gridLayoutConversionText);
        groupConversionText.setLayoutData(createGroupLayoutData());

        buttonConversionTextDefault = WidgetFactory.createRadioButton(groupConversionText);
        buttonConversionTextDefault.setText(Messages.Default);
        buttonConversionTextDefault.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                conversionText = IPreferences.SPLF_CONVERSION_DEFAULT;
                textConversionTextLibrary.setEnabled(false);
                textConversionTextCommand.setEnabled(false);
                checkError();
            }
        });

        buttonConversionTextUserDefined = WidgetFactory.createRadioButton(groupConversionText);
        buttonConversionTextUserDefined.setText(Messages.User_defined);
        buttonConversionTextUserDefined.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                conversionText = IPreferences.SPLF_CONVERSION_USER_DEFINED;
                textConversionTextLibrary.setEnabled(true);
                textConversionTextCommand.setEnabled(true);
                checkError();
            }
        });

        buttonConversionTextTransform = WidgetFactory.createRadioButton(groupConversionText);
        buttonConversionTextTransform.setText(Messages.Transform);
        buttonConversionTextTransform.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                conversionText = IPreferences.SPLF_CONVERSION_TRANSFORM;
                textConversionTextLibrary.setEnabled(false);
                textConversionTextCommand.setEnabled(false);
                checkError();
            }
        });

        Label labelConversionTextLibrary = new Label(groupConversionText, SWT.NONE);
        labelConversionTextLibrary.setText(Messages.Library_colon);

        textConversionTextLibrary = WidgetFactory.createText(groupConversionText);
        textConversionTextLibrary.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkError();
            }
        });
        textConversionTextLibrary.setLayoutData(createGroupLayoutData());
        textConversionTextLibrary.setTextLimit(10);

        // TODO: fix library name validator (pass CCSID) - DONE
        validatorConversionTextLibrary = Validator.getLibraryNameInstance(getDefaultSystemCcsid());

        Label labelConversionTextCommand = new Label(groupConversionText, SWT.NONE);
        labelConversionTextCommand.setText(Messages.Command_colon);

        textConversionTextCommand = WidgetFactory.createText(groupConversionText);
        textConversionTextCommand.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkError();
            }
        });
        textConversionTextCommand.setLayoutData(createGroupLayoutData());
        textConversionTextCommand.setTextLimit(256);

        Label labelConversionTextEditAllowed = new Label(groupConversionText, SWT.NONE);
        labelConversionTextEditAllowed.setText(Messages.Open_spooled_file_in_colon);

        comboConversionTextEditAllowed = WidgetFactory.createReadOnlyCombo(groupConversionText);
        comboConversionTextEditAllowed.setLayoutData(createGroupLayoutData());
        comboConversionTextEditAllowed.setItems(Preferences.getInstance().getSpooledFileAllowEditLabels());
        comboConversionTextEditAllowed.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (Messages.Label_Viewer.equals(comboConversionTextEditAllowed.getText())) {
                    isConversionTextEditAllowed = false;
                } else {
                    isConversionTextEditAllowed = true;
                }
            }
        });

        String colorsAndFonts = Messages.bind(Messages.Change_viewer_font_Basic_Text_Font, new String[] {
            "<a href=\"org.eclipse.ui.preferencePages.ColorsAndFonts\">", "</a>" });

        Link lnkJavaTaskTags = new Link(groupConversionText, SWT.MULTI | SWT.WRAP);
        lnkJavaTaskTags.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 3, 1));
        lnkJavaTaskTags.setText(colorsAndFonts);
        lnkJavaTaskTags.pack();
        lnkJavaTaskTags.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                PreferencesUtil.createPreferenceDialogOn(getShell(), e.text, null, null);
            }
        });

        // Group: HTML conversion
        Group groupConversionHTML = new Group(container, SWT.NONE);
        groupConversionHTML.setText(Messages.Conversion_to_format + " *HTML");
        GridLayout gridLayoutConversionHTML = new GridLayout();
        gridLayoutConversionHTML.numColumns = 3;
        groupConversionHTML.setLayout(gridLayoutConversionHTML);
        groupConversionHTML.setLayoutData(createGroupLayoutData());

        buttonConversionHTMLDefault = WidgetFactory.createRadioButton(groupConversionHTML);
        buttonConversionHTMLDefault.setText(Messages.Default);
        buttonConversionHTMLDefault.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                conversionHTML = IPreferences.SPLF_CONVERSION_DEFAULT;
                textConversionHTMLLibrary.setEnabled(false);
                textConversionHTMLCommand.setEnabled(false);
                checkError();
            }
        });

        buttonConversionHTMLUserDefined = WidgetFactory.createRadioButton(groupConversionHTML);
        buttonConversionHTMLUserDefined.setText(Messages.User_defined);
        buttonConversionHTMLUserDefined.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                conversionHTML = IPreferences.SPLF_CONVERSION_USER_DEFINED;
                textConversionHTMLLibrary.setEnabled(true);
                textConversionHTMLCommand.setEnabled(true);
                checkError();
            }
        });

        buttonConversionHTMLTransform = WidgetFactory.createRadioButton(groupConversionHTML);
        buttonConversionHTMLTransform.setText(Messages.Transform);
        buttonConversionHTMLTransform.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                conversionHTML = IPreferences.SPLF_CONVERSION_TRANSFORM;
                textConversionHTMLLibrary.setEnabled(false);
                textConversionHTMLCommand.setEnabled(false);
                checkError();
            }
        });

        Label labelConversionHTMLLibrary = new Label(groupConversionHTML, SWT.NONE);
        labelConversionHTMLLibrary.setText(Messages.Library_colon);

        textConversionHTMLLibrary = WidgetFactory.createText(groupConversionHTML);
        textConversionHTMLLibrary.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkError();
            }
        });
        textConversionHTMLLibrary.setLayoutData(createGroupLayoutData());
        textConversionHTMLLibrary.setTextLimit(10);

        // TODO: fix library name validator (pass CCSID) - DONE
        validatorConversionHTMLLibrary = Validator.getLibraryNameInstance(getDefaultSystemCcsid());

        Label labelConversionHTMLCommand = new Label(groupConversionHTML, SWT.NONE);
        labelConversionHTMLCommand.setText(Messages.Command_colon);

        textConversionHTMLCommand = WidgetFactory.createText(groupConversionHTML);
        textConversionHTMLCommand.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkError();
            }
        });
        textConversionHTMLCommand.setLayoutData(createGroupLayoutData());
        textConversionHTMLCommand.setTextLimit(256);

        Label labelConversionHTMLEditAllowed = new Label(groupConversionHTML, SWT.NONE);
        labelConversionHTMLEditAllowed.setText(Messages.Open_spooled_file_in_colon);

        comboConversionHTMLEditAllowed = WidgetFactory.createReadOnlyCombo(groupConversionHTML);
        comboConversionHTMLEditAllowed.setLayoutData(createGroupLayoutData());
        comboConversionHTMLEditAllowed.setItems(Preferences.getInstance().getSpooledFileAllowEditLabels());
        comboConversionHTMLEditAllowed.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (Messages.Label_Viewer.equals(comboConversionHTMLEditAllowed.getText())) {
                    isConversionHTMLEditAllowed = false;
                } else {
                    isConversionHTMLEditAllowed = true;
                }
            }
        });

        // Group: PDF conversion
        Group groupConversionPDF = new Group(container, SWT.NONE);
        groupConversionPDF.setText(Messages.Conversion_to_format + " *PDF");
        GridLayout gridLayoutConversionPDF = new GridLayout();
        gridLayoutConversionPDF.numColumns = 3;
        groupConversionPDF.setLayout(gridLayoutConversionPDF);
        groupConversionPDF.setLayoutData(createGroupLayoutData());

        buttonConversionPDFDefault = WidgetFactory.createRadioButton(groupConversionPDF);
        buttonConversionPDFDefault.setText(Messages.Default);
        buttonConversionPDFDefault.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                conversionPDF = IPreferences.SPLF_CONVERSION_DEFAULT;
                textConversionPDFLibrary.setEnabled(false);
                textConversionPDFCommand.setEnabled(false);
                comboConversionPDFPageSize.setEnabled(false);
                setPDFOptionsEnablement();
                checkError();
            }
        });

        buttonConversionPDFUserDefined = WidgetFactory.createRadioButton(groupConversionPDF);
        buttonConversionPDFUserDefined.setText(Messages.User_defined);
        buttonConversionPDFUserDefined.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                conversionPDF = IPreferences.SPLF_CONVERSION_USER_DEFINED;
                textConversionPDFLibrary.setEnabled(true);
                textConversionPDFCommand.setEnabled(true);
                comboConversionPDFPageSize.setEnabled(false);
                setPDFOptionsEnablement();
                checkError();
            }
        });

        buttonConversionPDFTransform = WidgetFactory.createRadioButton(groupConversionPDF);
        buttonConversionPDFTransform.setText(Messages.Transform);
        buttonConversionPDFTransform.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                conversionPDF = IPreferences.SPLF_CONVERSION_TRANSFORM;
                textConversionPDFLibrary.setEnabled(false);
                textConversionPDFCommand.setEnabled(false);
                comboConversionPDFPageSize.setEnabled(true);
                setPDFOptionsEnablement();
                checkError();
            }
        });

        Label labelConversionPDFLibrary = new Label(groupConversionPDF, SWT.NONE);
        labelConversionPDFLibrary.setText(Messages.Library_colon);

        textConversionPDFLibrary = WidgetFactory.createText(groupConversionPDF);
        textConversionPDFLibrary.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkError();
            }
        });
        textConversionPDFLibrary.setLayoutData(createGroupLayoutData());
        textConversionPDFLibrary.setTextLimit(10);

        // TODO: fix library name validator (pass CCSID) - DONE
        validatorConversionPDFLibrary = Validator.getLibraryNameInstance(getDefaultSystemCcsid());

        Label labelConversionPDFCommand = new Label(groupConversionPDF, SWT.NONE);
        labelConversionPDFCommand.setText(Messages.Command_colon);

        textConversionPDFCommand = WidgetFactory.createText(groupConversionPDF);
        textConversionPDFCommand.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkError();
            }
        });
        textConversionPDFCommand.setLayoutData(createGroupLayoutData());
        textConversionPDFCommand.setTextLimit(256);

        Label labelConversionPDFPageSize = new Label(groupConversionPDF, SWT.NONE);
        labelConversionPDFPageSize.setText(Messages.PageSize_colon);

        comboConversionPDFPageSize = WidgetFactory.createReadOnlyCombo(groupConversionPDF);
        comboConversionPDFPageSize.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                conversionPDFPageSize = comboConversionPDFPageSize.getText();
                setPDFOptionsEnablement();
            }
        });
        comboConversionPDFPageSize.setLayoutData(createGroupLayoutData());
        comboConversionPDFPageSize.setItems(loadAvailablePageSizes());

        chkBoxConversionPDFAdjustFontSize = WidgetFactory.createCheckbox(groupConversionPDF);
        chkBoxConversionPDFAdjustFontSize.setText(Messages.Adjust_font_size);
        chkBoxConversionPDFAdjustFontSize.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                adjustFontSize = chkBoxConversionPDFAdjustFontSize.getSelection();
            }
        });
        chkBoxConversionPDFAdjustFontSize.setLayoutData(createLayoutData(2));

        labelConversionPDFUsingFontSize = new Label(groupConversionPDF, SWT.NONE);
        labelConversionPDFUsingFontSize.setText(Messages.Using_font_size_of_TEXT_conversion);

        final Button showVars = WidgetFactory.createPushButton(container);
        showVars.setData(SHOW_SUBSTITUTION_VARIABLES_NO);
        showVars.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performToggleSubstitutionVariable(container, showVars);
            }
        });

        performToggleSubstitutionVariable(container, showVars);

        return container;
    }

    private void performToggleSubstitutionVariable(final Composite container, Button showVars) {

        if ((Boolean)showVars.getData()) {
            showVars.setText(Messages.Hide_substitution_variables);
            showVars.setData(SHOW_SUBSTITUTION_VARIABLES_NO);
            groupSubstitutionVariables = createGroupSubstitutionVariables(container, Messages.Substitution_variables_for_conversion_commands);
            updateControl(container);
        } else {
            showVars.setText(Messages.Show_substitution_variables);
            showVars.setData(SHOW_SUBSTITUTION_VARIABLES_YES);
            if (groupSubstitutionVariables != null && !groupSubstitutionVariables.isDisposed()) {
                groupSubstitutionVariables.dispose();
                groupSubstitutionVariables = null;
                updateControl(container);
            }
        }
    }

    private void updateControl(Composite composite) {

        composite.pack();
        composite.getParent().pack();
        getShell().pack();

        composite.layout();
        composite.getParent().layout();
        getShell().layout();

        composite.update();
        composite.getParent().update();
        getShell().update();
    }

    private void setPDFOptionsEnablement() {

        if (IPreferences.SPLF_CONVERSION_TRANSFORM.equals(conversionPDF)) {
            if (!PageSize.PAGE_SIZE_FONT.equals(conversionPDFPageSize)) {
                chkBoxConversionPDFAdjustFontSize.setEnabled(true);
                labelConversionPDFUsingFontSize.setVisible(false);
            } else {
                chkBoxConversionPDFAdjustFontSize.setEnabled(false);
                labelConversionPDFUsingFontSize.setVisible(true);
            }
        } else {
            chkBoxConversionPDFAdjustFontSize.setEnabled(false);
            labelConversionPDFUsingFontSize.setVisible(false);
        }
    }

    private Group createGroupSubstitutionVariables(Composite container, String headline) {

        // Group: Replacement variables
        Group groupSubstitutionVariables = new Group(container, SWT.NONE);
        groupSubstitutionVariables.setText(headline);
        GridLayout gridLayoutSubstitutionVariables = new GridLayout();
        gridLayoutSubstitutionVariables.numColumns = 3;
        groupSubstitutionVariables.setLayout(gridLayoutSubstitutionVariables);
        groupSubstitutionVariables.setLayoutData(createGroupLayoutData());

        createSpooledFileVariable(groupSubstitutionVariables, "&" + SpooledFile.VARIABLE_SPLF, Messages.File);
        createSpooledFileVariable(groupSubstitutionVariables, "&" + SpooledFile.VARIABLE_SPLFNBR, Messages.File_number);
        createSpooledFileVariable(groupSubstitutionVariables, "&" + SpooledFile.VARIABLE_JOBNAME, Messages.Job_name);
        createSpooledFileVariable(groupSubstitutionVariables, "&" + SpooledFile.VARIABLE_JOBUSR, Messages.Job_user);
        createSpooledFileVariable(groupSubstitutionVariables, "&" + SpooledFile.VARIABLE_JOBNBR, Messages.Job_number);
        createSpooledFileVariable(groupSubstitutionVariables, "&" + SpooledFile.VARIABLE_JOBSYS, Messages.Job_system);
        createSpooledFileVariable(groupSubstitutionVariables, "&" + SpooledFile.VARIABLE_STMFDIR, Messages.Directory);
        createSpooledFileVariable(groupSubstitutionVariables, "&" + SpooledFile.VARIABLE_STMF, Messages.Stream_file);
        createSpooledFileVariable(groupSubstitutionVariables, "&" + SpooledFile.VARIABLE_CODPAG, Messages.Code_page);
        createSpooledFileVariable(groupSubstitutionVariables, "&" + SpooledFile.VARIABLE_FMT, Messages.Format);

        createSpooledFileVariable(groupSubstitutionVariables, "&" + SpooledFile.VARIABLE_STATUS, Messages.Format);
        createSpooledFileVariable(groupSubstitutionVariables, "&" + SpooledFile.VARIABLE_CTIME_STAMP, Messages.Creation_Timestamp);
        createSpooledFileVariable(groupSubstitutionVariables, "&" + SpooledFile.VARIABLE_CDATE, Messages.Creation_Date);
        createSpooledFileVariable(groupSubstitutionVariables, "&" + SpooledFile.VARIABLE_CTIME, Messages.Creation_Time);
        createSpooledFileVariable(groupSubstitutionVariables, "&" + SpooledFile.VARIABLE_USRDTA, Messages.User_data);

        createSpooledFileVariable(groupSubstitutionVariables, "&" + SpooledFile.VARIABLE_OUTQ, Messages.Output_queue);
        createSpooledFileVariable(groupSubstitutionVariables, "&" + SpooledFile.VARIABLE_OUTQLIB, Messages.Output_queue_library);
        createSpooledFileVariable(groupSubstitutionVariables, "&" + SpooledFile.VARIABLE_PAGES, Messages.Pages);

        return groupSubstitutionVariables;
    }

    private GridData createGroupLayoutData() {
        return createLayoutData(2);
    }

    private GridData createLayoutData() {
        return createLayoutData(1);
    }

    private GridData createLayoutData(int horizontalSpan) {
        return new GridData(SWT.FILL, SWT.CENTER, true, false, horizontalSpan, 1);
    }

    private void createSpooledFileVariable(Group parent, String variable, String description) {

        new Label(parent, SWT.NONE).setText(variable);
        new Label(parent, SWT.NONE).setText(":"); //$NON-NLS-1$
        new Label(parent, SWT.NONE).setText(description);
    }

    private String[] loadAvailablePageSizes() {

        Set<PageSize> pagesSizes = SpooledFileTransformerPDF.getPageSizes();

        List<String> pagesSizesList = new ArrayList<String>();
        pagesSizesList.add(PageSize.PAGE_SIZE_CALCULATE);
        pagesSizesList.add(PageSize.PAGE_SIZE_FONT);
        for (Iterator<PageSize> iterator = pagesSizes.iterator(); iterator.hasNext();) {
            SpooledFileTransformerPDF.PageSize pageSize = (PageSize)iterator.next();
            pagesSizesList.add(pageSize.getFormat());
        }

        String[] pagesSizesSorted = pagesSizesList.toArray(new String[pagesSizesList.size()]);
        Arrays.sort(pagesSizesSorted);

        return pagesSizesSorted;
    }

    public void checkError() {

        rseDescription = textRSEDescription.getText().toUpperCase().trim();
        conversionTextLibrary = textConversionTextLibrary.getText().toUpperCase().trim();
        conversionTextCommand = textConversionTextCommand.getText().toUpperCase().trim();
        conversionHTMLLibrary = textConversionHTMLLibrary.getText().toUpperCase().trim();
        conversionHTMLCommand = textConversionHTMLCommand.getText().toUpperCase().trim();
        conversionPDFLibrary = textConversionPDFLibrary.getText().toUpperCase().trim();
        conversionPDFCommand = textConversionPDFCommand.getText().toUpperCase().trim();

        if (conversionText.equals(IPreferences.SPLF_CONVERSION_USER_DEFINED)) {

            if (conversionTextLibrary.equals("") || !validatorConversionTextLibrary.validate(conversionTextLibrary)) {
                setErrorMessage("*TEXT - " + Messages.The_value_in_field_Library_is_not_valid);
                setValid(false);
                return;
            }

            if (conversionTextCommand.equals("")) {
                setErrorMessage("*TEXT - " + Messages.The_value_in_field_Command_is_not_valid);
                setValid(false);
                return;
            }

        }

        if (conversionHTML.equals(IPreferences.SPLF_CONVERSION_USER_DEFINED)) {

            if (conversionHTMLLibrary.equals("") || !validatorConversionHTMLLibrary.validate(conversionHTMLLibrary)) {
                setErrorMessage("*HTML - " + Messages.The_value_in_field_Library_is_not_valid);
                setValid(false);
                return;
            }

            if (conversionHTMLCommand.equals("")) {
                setErrorMessage("*HTML - " + Messages.The_value_in_field_Command_is_not_valid);
                setValid(false);
                return;
            }

        }

        if (conversionPDF.equals(IPreferences.SPLF_CONVERSION_USER_DEFINED)) {

            if (conversionPDFLibrary.equals("") || !validatorConversionPDFLibrary.validate(conversionPDFLibrary)) {
                setErrorMessage("*PDF - " + Messages.The_value_in_field_Library_is_not_valid);
                setValid(false);
                return;
            }

            if (conversionPDFCommand.equals("")) {
                setErrorMessage("*PDF - " + Messages.The_value_in_field_Command_is_not_valid);
                setValid(false);
                return;
            }

        }

        maxNumSpooledFiles = IntHelper.tryParseInt(textMaxNumSpooledFiles.getText(), -1);
        if (maxNumSpooledFiles <= -1) {
            setErrorMessage(Messages.The_value_in_field_max_Num_SplF_is_not_valid);
            setValid(false);
            return;
        }

        setErrorMessage(null);
        setValid(true);
        return;

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

        Preferences.getInstance().setLoadSpooledFilesAsynchronousliy(isLoadAsynchronously);
        Preferences.getInstance().setSpooledFileDefaultFormat(defaultFormat);

        Preferences.getInstance().setSpooledFileConversionText(conversionText);
        Preferences.getInstance().setSpooledFileConversionLibraryText(conversionTextLibrary);
        Preferences.getInstance().setSpooledFileConversionCommandText(conversionTextCommand);
        Preferences.getInstance().setSpooledFileConversionTextEditAllowed(isConversionTextEditAllowed);

        Preferences.getInstance().setSpooledFileConversionHTML(conversionHTML);
        Preferences.getInstance().setSpooledFileConversionLibraryHTML(conversionHTMLLibrary);
        Preferences.getInstance().setSpooledFileConversionCommandHTML(conversionHTMLCommand);
        Preferences.getInstance().setSpooledFileConversionHTMLEditAllowed(isConversionHTMLEditAllowed);

        Preferences.getInstance().setSpooledFileConversionPDF(conversionPDF);
        Preferences.getInstance().setSpooledFileConversionLibraryPDF(conversionPDFLibrary);
        Preferences.getInstance().setSpooledFileConversionCommandPDF(conversionPDFCommand);

        Preferences.getInstance().setSpooledFilePageSize(conversionPDFPageSize);
        Preferences.getInstance().setSpooledFileAdjustFontSize(adjustFontSize);
        Preferences.getInstance().setSpooledFilesSuggestedFileName(suggestedFileName);
        Preferences.getInstance().setSpooledFileMaxFilesToLoad(maxNumSpooledFiles);

        Preferences.getInstance().setSpooledFileRSEDescription(rseDescription);
    }

    protected void setScreenToValues() {

        isLoadAsynchronously = Preferences.getInstance().isLoadSpooledFilesAsynchronousliy();
        defaultFormat = Preferences.getInstance().getSpooledFileConversionDefaultFormat();

        conversionText = Preferences.getInstance().getSpooledFileConversionText();
        conversionTextLibrary = Preferences.getInstance().getSpooledFileConversionTextLibrary();
        conversionTextCommand = Preferences.getInstance().getSpooledFileConversionTextCommand();
        isConversionTextEditAllowed = Preferences.getInstance().isSpooledFileConversionTextEditAllowed();

        conversionHTML = Preferences.getInstance().getSpooledFileConversionHTML();
        conversionHTMLLibrary = Preferences.getInstance().getSpooledFileConversionHTMLLibrary();
        conversionHTMLCommand = Preferences.getInstance().getSpooledFileConversionHTMLCommand();
        isConversionHTMLEditAllowed = Preferences.getInstance().isSpooledFileConversionHTMLEditAllowed();

        conversionPDF = Preferences.getInstance().getSpooledFileConversionPDF();
        conversionPDFLibrary = Preferences.getInstance().getSpooledFileConversionPDFLibrary();
        conversionPDFCommand = Preferences.getInstance().getSpooledFileConversionPDFCommand();

        conversionPDFPageSize = Preferences.getInstance().getSpooledFilePageSize();
        adjustFontSize = Preferences.getInstance().getSpooledFileAdjustFontSize();
        suggestedFileName = Preferences.getInstance().getSpooledFilesSuggestedFileName();
        maxNumSpooledFiles = Preferences.getInstance().getSpooledFilesMaxFilesToLoad();

        rseDescription = Preferences.getInstance().getSpooledFileRSEDescription();

        setScreenValues();

    }

    protected void setScreenToDefaultValues() {

        isLoadAsynchronously = Preferences.getInstance().getDefaultLoadSpooledFilesAsynchronously();
        defaultFormat = Preferences.getInstance().getDefaultSpooledFileConversionDefaultFormat();

        conversionText = Preferences.getInstance().getDefaultSpooledFileConversionText();
        conversionTextLibrary = Preferences.getInstance().getDefaultSpooledFileConversionTextLibrary();
        conversionTextCommand = Preferences.getInstance().getDefaultSpooledFileConversionTextCommand();
        isConversionTextEditAllowed = Preferences.getInstance().getDefaultSpooledFileConversionTextEditAllowed();

        conversionHTML = Preferences.getInstance().getDefaultSpooledFileConversionHTML();
        conversionHTMLLibrary = Preferences.getInstance().getDefaultSpooledFileConversionHTMLLibrary();
        conversionHTMLCommand = Preferences.getInstance().getDefaultSpooledFileConversionHTMLCommand();
        isConversionHTMLEditAllowed = Preferences.getInstance().getDefaultSpooledFileConversionHTMLEditAllowed();

        conversionPDF = Preferences.getInstance().getDefaultSpooledFileConversionPDF();
        conversionPDFLibrary = Preferences.getInstance().getDefaultSpooledFileConversionPDFLibrary();
        conversionPDFCommand = Preferences.getInstance().getDefaultSpooledFileConversionPDFCommand();

        conversionPDFPageSize = Preferences.getInstance().getDefaultSpooledFilePageSize();
        adjustFontSize = Preferences.getInstance().getDefaultSpooledFileAdjustFontSize();
        suggestedFileName = Preferences.getInstance().getDefaultSpooledFilesSuggestedFileName();
        maxNumSpooledFiles = Preferences.getInstance().getDefaultSpooledFileMaxFilesToLoad();

        rseDescription = Preferences.getInstance().getDefaultSpooledFileRSEDescription();

        setScreenValues();

    }

    protected void setScreenValues() {

        buttonLoadAsynchronously.setSelection(isLoadAsynchronously);

        buttonDefaultFormatText.setSelection(false);
        buttonDefaultFormatHTML.setSelection(false);
        buttonDefaultFormatPDF.setSelection(false);
        if (IPreferences.OUTPUT_FORMAT_TEXT.equals(defaultFormat)) {
            buttonDefaultFormatText.setSelection(true);
        } else if (IPreferences.OUTPUT_FORMAT_HTML.equals(defaultFormat)) {
            buttonDefaultFormatHTML.setSelection(true);
        } else if (IPreferences.OUTPUT_FORMAT_PDF.equals(defaultFormat)) {
            buttonDefaultFormatPDF.setSelection(true);
        }

        buttonConversionTextDefault.setSelection(false);
        buttonConversionTextUserDefined.setSelection(false);
        buttonConversionTextTransform.setSelection(false);
        if (conversionText.equals(IPreferences.SPLF_CONVERSION_DEFAULT)) {
            buttonConversionTextDefault.setSelection(true);
        } else if (conversionText.equals(IPreferences.SPLF_CONVERSION_USER_DEFINED)) {
            buttonConversionTextUserDefined.setSelection(true);
        } else if (conversionText.equals(IPreferences.SPLF_CONVERSION_TRANSFORM)) {
            buttonConversionTextTransform.setSelection(true);
        }
        textConversionTextLibrary.setText(conversionTextLibrary);
        if (conversionText.equals(IPreferences.SPLF_CONVERSION_DEFAULT)) {
            textConversionTextLibrary.setEnabled(false);
        } else if (conversionText.equals(IPreferences.SPLF_CONVERSION_USER_DEFINED)) {
            textConversionTextLibrary.setEnabled(true);
        } else if (conversionText.equals(IPreferences.SPLF_CONVERSION_TRANSFORM)) {
            textConversionTextLibrary.setEnabled(false);
        }
        textConversionTextCommand.setText(conversionTextCommand);
        if (conversionText.equals(IPreferences.SPLF_CONVERSION_DEFAULT)) {
            textConversionTextCommand.setEnabled(false);
        } else if (conversionText.equals(IPreferences.SPLF_CONVERSION_USER_DEFINED)) {
            textConversionTextCommand.setEnabled(true);
        } else if (conversionText.equals(IPreferences.SPLF_CONVERSION_TRANSFORM)) {
            textConversionTextCommand.setEnabled(false);
        }
        if (isConversionTextEditAllowed) {
            comboConversionTextEditAllowed.setText(Messages.Label_Editor);
        } else {
            comboConversionTextEditAllowed.setText(Messages.Label_Viewer);
        }

        buttonConversionHTMLDefault.setSelection(false);
        buttonConversionHTMLUserDefined.setSelection(false);
        buttonConversionHTMLTransform.setSelection(false);
        if (conversionHTML.equals(IPreferences.SPLF_CONVERSION_DEFAULT)) {
            buttonConversionHTMLDefault.setSelection(true);
        } else if (conversionHTML.equals(IPreferences.SPLF_CONVERSION_USER_DEFINED)) {
            buttonConversionHTMLUserDefined.setSelection(true);
        } else if (conversionHTML.equals(IPreferences.SPLF_CONVERSION_TRANSFORM)) {
            buttonConversionHTMLTransform.setSelection(true);
        }
        textConversionHTMLLibrary.setText(conversionHTMLLibrary);
        if (conversionHTML.equals(IPreferences.SPLF_CONVERSION_DEFAULT)) {
            textConversionHTMLLibrary.setEnabled(false);
        } else if (conversionHTML.equals(IPreferences.SPLF_CONVERSION_USER_DEFINED)) {
            textConversionHTMLLibrary.setEnabled(true);
        } else if (conversionHTML.equals(IPreferences.SPLF_CONVERSION_TRANSFORM)) {
            textConversionHTMLLibrary.setEnabled(false);
        }
        textConversionHTMLCommand.setText(conversionHTMLCommand);
        if (conversionHTML.equals(IPreferences.SPLF_CONVERSION_DEFAULT)) {
            textConversionHTMLCommand.setEnabled(false);
        } else if (conversionHTML.equals(IPreferences.SPLF_CONVERSION_USER_DEFINED)) {
            textConversionHTMLCommand.setEnabled(true);
        } else if (conversionHTML.equals(IPreferences.SPLF_CONVERSION_TRANSFORM)) {
            textConversionHTMLCommand.setEnabled(false);
        }
        if (isConversionHTMLEditAllowed) {
            comboConversionHTMLEditAllowed.setText(Messages.Label_Editor);
        } else {
            comboConversionHTMLEditAllowed.setText(Messages.Label_Viewer);
        }

        buttonConversionPDFDefault.setSelection(false);
        buttonConversionPDFUserDefined.setSelection(false);
        buttonConversionPDFTransform.setSelection(false);
        if (conversionPDF.equals(IPreferences.SPLF_CONVERSION_DEFAULT)) {
            buttonConversionPDFDefault.setSelection(true);
        } else if (conversionPDF.equals(IPreferences.SPLF_CONVERSION_USER_DEFINED)) {
            buttonConversionPDFUserDefined.setSelection(true);
        } else if (conversionPDF.equals(IPreferences.SPLF_CONVERSION_TRANSFORM)) {
            buttonConversionPDFTransform.setSelection(true);
        }
        textConversionPDFLibrary.setText(conversionPDFLibrary);
        if (conversionPDF.equals(IPreferences.SPLF_CONVERSION_DEFAULT)) {
            textConversionPDFLibrary.setEnabled(false);
        } else if (conversionPDF.equals(IPreferences.SPLF_CONVERSION_USER_DEFINED)) {
            textConversionPDFLibrary.setEnabled(true);
        } else if (conversionPDF.equals(IPreferences.SPLF_CONVERSION_TRANSFORM)) {
            textConversionPDFLibrary.setEnabled(false);
        }
        textConversionPDFCommand.setText(conversionPDFCommand);
        if (conversionPDF.equals(IPreferences.SPLF_CONVERSION_DEFAULT)) {
            textConversionPDFCommand.setEnabled(false);
        } else if (conversionPDF.equals(IPreferences.SPLF_CONVERSION_USER_DEFINED)) {
            textConversionPDFCommand.setEnabled(true);
        } else if (conversionPDF.equals(IPreferences.SPLF_CONVERSION_TRANSFORM)) {
            textConversionPDFCommand.setEnabled(false);
        }

        if (!StringHelper.isNullOrEmpty(conversionPDFPageSize)) {
            comboConversionPDFPageSize.setText(conversionPDFPageSize);
        } else {
            comboConversionPDFPageSize.setText(PageSize.PAGE_SIZE_A4);
        }

        chkBoxConversionPDFAdjustFontSize.setSelection(adjustFontSize);
        setPDFOptionsEnablement();

        comboSuggestedFileName.setText(suggestedFileName);
        textMaxNumSpooledFiles.setText(Integer.toString(maxNumSpooledFiles));

        textRSEDescription.setText(rseDescription);
    }

    public void init(IWorkbench workbench) {
    }

    private int getDefaultSystemCcsid() {
        return Preferences.getInstance().getSystemCcsid();
    }

}