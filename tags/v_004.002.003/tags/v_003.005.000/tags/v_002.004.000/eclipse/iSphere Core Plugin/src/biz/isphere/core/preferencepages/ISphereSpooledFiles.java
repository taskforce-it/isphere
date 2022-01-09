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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.preferences.Preferences;

public class ISphereSpooledFiles extends PreferencePage implements IWorkbenchPreferencePage {

    private Button buttonDefaultFormatText;
    private Button buttonDefaultFormatHTML;
    private Button buttonDefaultFormatPDF;
    private String defaultFormat;

    private Button buttonConversionTextDefault;
    private Button buttonConversionTextUserDefined;
    private Button buttonConversionTextTransform;
    private String conversionText;
    private Text textConversionTextLibrary;
    private Validator validatorConversionTextLibrary;
    private String conversionTextLibrary;

    private Text textConversionTextCommand;
    private String conversionTextCommand;
    private Button buttonConversionHTMLDefault;
    private Button buttonConversionHTMLUserDefined;
    private Button buttonConversionHTMLTransform;
    private String conversionHTML;
    private Text textConversionHTMLLibrary;
    private Validator validatorConversionHTMLLibrary;
    private String conversionHTMLLibrary;
    private Text textConversionHTMLCommand;
    private String conversionHTMLCommand;

    private Button buttonConversionPDFDefault;
    private Button buttonConversionPDFUserDefined;
    private Button buttonConversionPDFTransform;
    private String conversionPDF;
    private Text textConversionPDFLibrary;
    private Validator validatorConversionPDFLibrary;
    private String conversionPDFLibrary;
    private Text textConversionPDFCommand;
    private String conversionPDFCommand;

    public ISphereSpooledFiles() {
        super();
        setPreferenceStore(ISpherePlugin.getDefault().getPreferenceStore());
        getPreferenceStore();
    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        container.setLayout(gridLayout);

        Group groupDefaultFormat = new Group(container, SWT.NONE);
        groupDefaultFormat.setText(Messages.When_double_clicking_on_a_spooled_file_open_it_as);
        GridLayout gridLayoutDefaultFormat = new GridLayout();
        gridLayoutDefaultFormat.numColumns = 3;
        groupDefaultFormat.setLayout(gridLayoutDefaultFormat);
        groupDefaultFormat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        buttonDefaultFormatText = new Button(groupDefaultFormat, SWT.RADIO);
        buttonDefaultFormatText.setText(IPreferences.OUTPUT_FORMAT_TEXT);
        buttonDefaultFormatText.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                defaultFormat = IPreferences.OUTPUT_FORMAT_TEXT;
            }
        });

        buttonDefaultFormatHTML = new Button(groupDefaultFormat, SWT.RADIO);
        buttonDefaultFormatHTML.setText(IPreferences.OUTPUT_FORMAT_HTML);
        buttonDefaultFormatHTML.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                defaultFormat = IPreferences.OUTPUT_FORMAT_HTML;
            }
        });

        buttonDefaultFormatPDF = new Button(groupDefaultFormat, SWT.RADIO);
        buttonDefaultFormatPDF.setText(IPreferences.OUTPUT_FORMAT_PDF);
        buttonDefaultFormatPDF.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                defaultFormat = IPreferences.OUTPUT_FORMAT_PDF;
            }
        });

        Group groupConversionText = new Group(container, SWT.NONE);
        groupConversionText.setText(Messages.Conversion_to_format + " *TEXT");
        GridLayout gridLayoutConversionText = new GridLayout();
        gridLayoutConversionText.numColumns = 3;
        groupConversionText.setLayout(gridLayoutConversionText);
        groupConversionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        buttonConversionTextDefault = new Button(groupConversionText, SWT.RADIO);
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

        buttonConversionTextUserDefined = new Button(groupConversionText, SWT.RADIO);
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

        buttonConversionTextTransform = new Button(groupConversionText, SWT.RADIO);
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

        textConversionTextLibrary = new Text(groupConversionText, SWT.BORDER);
        textConversionTextLibrary.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkError();
            }
        });
        textConversionTextLibrary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        textConversionTextLibrary.setTextLimit(10);

        validatorConversionTextLibrary = new Validator();
        validatorConversionTextLibrary.setType("*NAME");
        validatorConversionTextLibrary.setLength(10);
        validatorConversionTextLibrary.setRestricted(false);

        Label labelConversionTextCommand = new Label(groupConversionText, SWT.NONE);
        labelConversionTextCommand.setText(Messages.Command_colon);

        textConversionTextCommand = new Text(groupConversionText, SWT.BORDER);
        textConversionTextCommand.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkError();
            }
        });
        textConversionTextCommand.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        textConversionTextCommand.setTextLimit(256);

        Group groupConversionHTML = new Group(container, SWT.NONE);
        groupConversionHTML.setText(Messages.Conversion_to_format + " *HTML");
        GridLayout gridLayoutConversionHTML = new GridLayout();
        gridLayoutConversionHTML.numColumns = 3;
        groupConversionHTML.setLayout(gridLayoutConversionHTML);
        groupConversionHTML.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        buttonConversionHTMLDefault = new Button(groupConversionHTML, SWT.RADIO);
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

        buttonConversionHTMLUserDefined = new Button(groupConversionHTML, SWT.RADIO);
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

        buttonConversionHTMLTransform = new Button(groupConversionHTML, SWT.RADIO);
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

        textConversionHTMLLibrary = new Text(groupConversionHTML, SWT.BORDER);
        textConversionHTMLLibrary.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkError();
            }
        });
        textConversionHTMLLibrary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        textConversionHTMLLibrary.setTextLimit(10);

        validatorConversionHTMLLibrary = new Validator();
        validatorConversionHTMLLibrary.setType("*NAME");
        validatorConversionHTMLLibrary.setLength(10);
        validatorConversionHTMLLibrary.setRestricted(false);

        Label labelConversionHTMLCommand = new Label(groupConversionHTML, SWT.NONE);
        labelConversionHTMLCommand.setText(Messages.Command_colon);

        textConversionHTMLCommand = new Text(groupConversionHTML, SWT.BORDER);
        textConversionHTMLCommand.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkError();
            }
        });
        textConversionHTMLCommand.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        textConversionHTMLCommand.setTextLimit(256);

        Group groupConversionPDF = new Group(container, SWT.NONE);
        groupConversionPDF.setText(Messages.Conversion_to_format + " *PDF");
        GridLayout gridLayoutConversionPDF = new GridLayout();
        gridLayoutConversionPDF.numColumns = 3;
        groupConversionPDF.setLayout(gridLayoutConversionPDF);
        groupConversionPDF.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        buttonConversionPDFDefault = new Button(groupConversionPDF, SWT.RADIO);
        buttonConversionPDFDefault.setText(Messages.Default);
        buttonConversionPDFDefault.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                conversionPDF = IPreferences.SPLF_CONVERSION_DEFAULT;
                textConversionPDFLibrary.setEnabled(false);
                textConversionPDFCommand.setEnabled(false);
                checkError();
            }
        });

        buttonConversionPDFUserDefined = new Button(groupConversionPDF, SWT.RADIO);
        buttonConversionPDFUserDefined.setText(Messages.User_defined);
        buttonConversionPDFUserDefined.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                conversionPDF = IPreferences.SPLF_CONVERSION_USER_DEFINED;
                textConversionPDFLibrary.setEnabled(true);
                textConversionPDFCommand.setEnabled(true);
                checkError();
            }
        });

        buttonConversionPDFTransform = new Button(groupConversionPDF, SWT.RADIO);
        buttonConversionPDFTransform.setText(Messages.Transform);
        buttonConversionPDFTransform.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                conversionPDF = IPreferences.SPLF_CONVERSION_TRANSFORM;
                textConversionPDFLibrary.setEnabled(false);
                textConversionPDFCommand.setEnabled(false);
                checkError();
            }
        });

        Label labelConversionPDFLibrary = new Label(groupConversionPDF, SWT.NONE);
        labelConversionPDFLibrary.setText(Messages.Library_colon);

        textConversionPDFLibrary = new Text(groupConversionPDF, SWT.BORDER);
        textConversionPDFLibrary.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkError();
            }
        });
        textConversionPDFLibrary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        textConversionPDFLibrary.setTextLimit(10);

        validatorConversionPDFLibrary = new Validator();
        validatorConversionPDFLibrary.setType("*NAME");
        validatorConversionPDFLibrary.setLength(10);
        validatorConversionPDFLibrary.setRestricted(false);

        Label labelConversionPDFCommand = new Label(groupConversionPDF, SWT.NONE);
        labelConversionPDFCommand.setText(Messages.Command_colon);

        textConversionPDFCommand = new Text(groupConversionPDF, SWT.BORDER);
        textConversionPDFCommand.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkError();
            }
        });
        textConversionPDFCommand.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        textConversionPDFCommand.setTextLimit(256);

        Group groupSubstitutionVariables = new Group(container, SWT.NONE);
        groupSubstitutionVariables.setText(Messages.Substitution_variables_for_conversion_commands);
        GridLayout gridLayoutSubstitutionVariables = new GridLayout();
        gridLayoutSubstitutionVariables.numColumns = 3;
        groupSubstitutionVariables.setLayout(gridLayoutSubstitutionVariables);
        groupSubstitutionVariables.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Text labelSubstitutionVariable11 = new Text(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable11.setText("&SPLF");
        labelSubstitutionVariable11.setEditable(false);

        Label labelSubstitutionVariable12 = new Label(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable12.setText(":");

        Label labelSubstitutionVariable13 = new Label(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable13.setText(Messages.File);

        Text labelSubstitutionVariable21 = new Text(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable21.setText("&SPLFNBR");
        labelSubstitutionVariable21.setEditable(false);

        Label labelSubstitutionVariable22 = new Label(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable22.setText(":");

        Label labelSubstitutionVariable23 = new Label(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable23.setText(Messages.File_number);

        Text labelSubstitutionVariable31 = new Text(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable31.setText("&JOBNAME");
        labelSubstitutionVariable31.setEditable(false);

        Label labelSubstitutionVariable32 = new Label(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable32.setText(":");

        Label labelSubstitutionVariable33 = new Label(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable33.setText(Messages.Job_name);

        Text labelSubstitutionVariable41 = new Text(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable41.setText("&JOBUSR");
        labelSubstitutionVariable41.setEditable(false);

        Label labelSubstitutionVariable42 = new Label(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable42.setText(":");

        Label labelSubstitutionVariable43 = new Label(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable43.setText(Messages.Job_user);

        Text labelSubstitutionVariable51 = new Text(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable51.setText("&JOBNBR");
        labelSubstitutionVariable51.setEditable(false);

        Label labelSubstitutionVariable52 = new Label(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable52.setText(":");

        Label labelSubstitutionVariable53 = new Label(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable53.setText(Messages.Job_number);

        Text labelSubstitutionVariable61 = new Text(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable61.setText("&STMFDIR");
        labelSubstitutionVariable61.setEditable(false);

        Label labelSubstitutionVariable62 = new Label(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable62.setText(":");

        Label labelSubstitutionVariable63 = new Label(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable63.setText(Messages.Directory);

        Text labelSubstitutionVariable71 = new Text(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable71.setText("&STMF");
        labelSubstitutionVariable71.setEditable(false);

        Label labelSubstitutionVariable72 = new Label(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable72.setText(":");

        Label labelSubstitutionVariable73 = new Label(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable73.setText(Messages.Stream_file);

        Text labelSubstitutionVariable81 = new Text(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable81.setText("&CODPAG");
        labelSubstitutionVariable81.setEditable(false);

        Label labelSubstitutionVariable82 = new Label(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable82.setText(":");

        Label labelSubstitutionVariable83 = new Label(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable83.setText(Messages.Code_page);

        Text labelSubstitutionVariable91 = new Text(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable91.setText("&FMT");
        labelSubstitutionVariable91.setEditable(false);

        Label labelSubstitutionVariable92 = new Label(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable92.setText(":");

        Label labelSubstitutionVariable93 = new Label(groupSubstitutionVariables, SWT.NONE);
        labelSubstitutionVariable93.setText(Messages.Format);

        setScreenToValues();

        return container;
    }

    public void checkError() {

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

        // TODO: Remove disabled statements 'DE.TASKFORCE'
        // store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.DEFAULT_FORMAT",
        // defaultFormat);
        Preferences.getInstance().setSpooledFileDefaultFormat(defaultFormat);

        // store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT",
        // conversionText);
        // store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT.LIBRARY",
        // conversionTextLibrary);
        // store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT.COMMAND",
        // conversionTextCommand);
        Preferences.getInstance().setSpooledFileConversionText(conversionText);
        Preferences.getInstance().setSpooledFileConversionLibraryText(conversionTextLibrary);
        Preferences.getInstance().setSpooledFileConversionCommandText(conversionTextCommand);

        // store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML",
        // conversionHTML);
        // store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML.LIBRARY",
        // conversionHTMLLibrary);
        // store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML.COMMAND",
        // conversionHTMLCommand);
        Preferences.getInstance().setSpooledFileConversionHTML(conversionHTML);
        Preferences.getInstance().setSpooledFileConversionLibraryHTML(conversionHTMLLibrary);
        Preferences.getInstance().setSpooledFileConversionCommandHTML(conversionHTMLCommand);

        // store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF",
        // conversionPDF);
        // store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF.LIBRARY",
        // conversionPDFLibrary);
        // store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF.COMMAND",
        // conversionPDFCommand);
        Preferences.getInstance().setSpooledFileConversionPDF(conversionPDF);
        Preferences.getInstance().setSpooledFileConversionLibraryPDF(conversionPDFLibrary);
        Preferences.getInstance().setSpooledFileConversionCommandPDF(conversionPDFCommand);

    }

    protected void setScreenToValues() {

        // TODO: Remove disabled statements 'DE.TASKFORCE'
        // defaultFormat =
        // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.DEFAULT_FORMAT");
        defaultFormat = Preferences.getInstance().getSpooledFileConversionDefaultFormat();

        // conversionText =
        // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT");
        // conversionTextLibrary =
        // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT.LIBRARY");
        // conversionTextCommand =
        // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT.COMMAND");
        conversionText = Preferences.getInstance().getSpooledFileConversionText();
        conversionTextLibrary = Preferences.getInstance().getSpooledFileConversionTextLibrary();
        conversionTextCommand = Preferences.getInstance().getSpooledFileConversionTextCommand();

        // conversionHTML =
        // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML");
        // conversionHTMLLibrary =
        // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML.LIBRARY");
        // conversionHTMLCommand =
        // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML.COMMAND");
        conversionHTML = Preferences.getInstance().getSpooledFileConversionHTML();
        conversionHTMLLibrary = Preferences.getInstance().getSpooledFileConversionHTMLLibrary();
        conversionHTMLCommand = Preferences.getInstance().getSpooledFileConversionHTMLCommand();

        // conversionPDF =
        // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF");
        // conversionPDFLibrary =
        // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF.LIBRARY");
        // conversionPDFCommand =
        // store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF.COMMAND");
        conversionPDF = Preferences.getInstance().getSpooledFileConversionPDF();
        conversionPDFLibrary = Preferences.getInstance().getSpooledFileConversionPDFLibrary();
        conversionPDFCommand = Preferences.getInstance().getSpooledFileConversionPDFCommand();

        setScreenValues();

    }

    protected void setScreenToDefaultValues() {

        // TODO: Remove disabled statements 'DE.TASKFORCE'
        // defaultFormat =
        // store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.DEFAULT_FORMAT");
        defaultFormat = Preferences.getInstance().getDefaultSpooledFileConversionDefaultFormat();

        // conversionText =
        // store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT");
        // conversionTextLibrary =
        // store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT.LIBRARY");
        // conversionTextCommand =
        // store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT.COMMAND");
        conversionText = Preferences.getInstance().getDefaultSpooledFileConversionText();
        conversionTextLibrary = Preferences.getInstance().getDefaultSpooledFileConversionTextLibrary();
        conversionTextCommand = Preferences.getInstance().getDefaultSpooledFileConversionTextCommand();

        // conversionHTML =
        // store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML");
        // conversionHTMLLibrary =
        // store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML.LIBRARY");
        // conversionHTMLCommand =
        // store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML.COMMAND");
        conversionHTML = Preferences.getInstance().getDefaultSpooledFileConversionHTML();
        conversionHTMLLibrary = Preferences.getInstance().getDefaultSpooledFileConversionHTMLLibrary();
        conversionHTMLCommand = Preferences.getInstance().getDefaultSpooledFileConversionHTMLCommand();

        // conversionPDF =
        // store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF");
        // conversionPDFLibrary =
        // store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF.LIBRARY");
        // conversionPDFCommand =
        // store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF.COMMAND");
        conversionPDF = Preferences.getInstance().getDefaultSpooledFileConversionPDF();
        conversionPDFLibrary = Preferences.getInstance().getDefaultSpooledFileConversionPDFLibrary();
        conversionPDFCommand = Preferences.getInstance().getDefaultSpooledFileConversionPDFCommand();

        setScreenValues();

    }

    protected void setScreenValues() {

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

    }

    public void init(IWorkbench workbench) {
    }

}