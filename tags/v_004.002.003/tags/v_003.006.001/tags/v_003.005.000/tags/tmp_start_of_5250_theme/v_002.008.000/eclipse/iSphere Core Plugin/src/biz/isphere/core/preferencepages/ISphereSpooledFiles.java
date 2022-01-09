/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.spooledfiles.SpooledFileTransformerPDF;
import biz.isphere.core.spooledfiles.SpooledFileTransformerPDF.PageSize;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class ISphereSpooledFiles extends PreferencePage implements IWorkbenchPreferencePage {

    private Button buttonLoadAsynchronously;
    private boolean isLoadAsynchronously;

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
    private Combo comboConversionPDFPageSize;
    private String conversionPDFPageSize;
    private Button chkBoxAdjustFontSize;
    private boolean adjustFontSize;
    private Combo comboSuggestedFileName;

    public ISphereSpooledFiles() {
        super();
        setPreferenceStore(ISpherePlugin.getDefault().getPreferenceStore());
        getPreferenceStore();
    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(2, false));

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

        validatorConversionTextLibrary = Validator.getLibraryNameInstance();

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

        validatorConversionHTMLLibrary = Validator.getLibraryNameInstance();

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
                chkBoxAdjustFontSize.setEnabled(false);
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
                chkBoxAdjustFontSize.setEnabled(false);
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
                chkBoxAdjustFontSize.setEnabled(true);
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

        validatorConversionPDFLibrary = Validator.getLibraryNameInstance();

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
            }
        });
        comboConversionPDFPageSize.setLayoutData(createGroupLayoutData());
        comboConversionPDFPageSize.setItems(loadAvailablePageSizes());

        chkBoxAdjustFontSize = WidgetFactory.createCheckbox(groupConversionPDF);
        chkBoxAdjustFontSize.setText(Messages.Adjust_font_size);
        chkBoxAdjustFontSize.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                adjustFontSize = chkBoxAdjustFontSize.getSelection();
            }
        });
        chkBoxAdjustFontSize.setLayoutData(createLayoutData(3));

        // Group: Replacement variables
        Group groupSubstitutionVariables = new Group(container, SWT.NONE);
        groupSubstitutionVariables.setText(Messages.Substitution_variables_for_conversion_commands);
        GridLayout gridLayoutSubstitutionVariables = new GridLayout();
        gridLayoutSubstitutionVariables.numColumns = 3;
        groupSubstitutionVariables.setLayout(gridLayoutSubstitutionVariables);
        groupSubstitutionVariables.setLayoutData(createGroupLayoutData());

        createSpooledFileVariable(groupSubstitutionVariables, "&&SPLF", Messages.File);
        createSpooledFileVariable(groupSubstitutionVariables, "&&SPLFNBR", Messages.File_number);
        createSpooledFileVariable(groupSubstitutionVariables, "&&JOBNAME", Messages.Job_name);
        createSpooledFileVariable(groupSubstitutionVariables, "&&JOBUSR", Messages.Job_user);
        createSpooledFileVariable(groupSubstitutionVariables, "&&JOBNBR", Messages.Job_number);
        createSpooledFileVariable(groupSubstitutionVariables, "&&JOBSYS", Messages.Job_system);
        createSpooledFileVariable(groupSubstitutionVariables, "&&STMFDIR", Messages.Directory);
        createSpooledFileVariable(groupSubstitutionVariables, "&&STMF", Messages.Stream_file);
        createSpooledFileVariable(groupSubstitutionVariables, "&&CODPAG", Messages.Code_page);
        createSpooledFileVariable(groupSubstitutionVariables, "&&FMT", Messages.Format);

        new Label(container, SWT.NONE).setText(Messages.Suggested_file_name);

        comboSuggestedFileName = WidgetFactory.createCombo(container);
        comboSuggestedFileName.setLayoutData(createLayoutData());
        comboSuggestedFileName.setItems(Preferences.getInstance().getSpooledFileSuggestedNames());

        buttonLoadAsynchronously = WidgetFactory.createCheckbox(container);
        buttonLoadAsynchronously.setLayoutData(createGroupLayoutData());
        buttonLoadAsynchronously.setText(Messages.Load_spooled_files_asynchronously);
        buttonLoadAsynchronously.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                isLoadAsynchronously = buttonLoadAsynchronously.getSelection();
            }
        });

        setScreenToValues();

        return container;
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
        for (Iterator<PageSize> iterator = pagesSizes.iterator(); iterator.hasNext();) {
            SpooledFileTransformerPDF.PageSize pageSize = (PageSize)iterator.next();
            pagesSizesList.add(pageSize.getFormat());
        }

        String[] pagesSizesSorted = pagesSizesList.toArray(new String[pagesSizesList.size()]);
        Arrays.sort(pagesSizesSorted);

        return pagesSizesSorted;
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

        Preferences.getInstance().setLoadSpooledFilesAsynchronousliy(isLoadAsynchronously);
        Preferences.getInstance().setSpooledFileDefaultFormat(defaultFormat);

        Preferences.getInstance().setSpooledFileConversionText(conversionText);
        Preferences.getInstance().setSpooledFileConversionLibraryText(conversionTextLibrary);
        Preferences.getInstance().setSpooledFileConversionCommandText(conversionTextCommand);

        Preferences.getInstance().setSpooledFileConversionHTML(conversionHTML);
        Preferences.getInstance().setSpooledFileConversionLibraryHTML(conversionHTMLLibrary);
        Preferences.getInstance().setSpooledFileConversionCommandHTML(conversionHTMLCommand);

        Preferences.getInstance().setSpooledFileConversionPDF(conversionPDF);
        Preferences.getInstance().setSpooledFileConversionLibraryPDF(conversionPDFLibrary);
        Preferences.getInstance().setSpooledFileConversionCommandPDF(conversionPDFCommand);

        Preferences.getInstance().setSpooledFilePageSize(conversionPDFPageSize);
        Preferences.getInstance().setSpooledFileAdjustFontSize(adjustFontSize);
        Preferences.getInstance().setSpooledFilesSuggestedFileName(comboSuggestedFileName.getText());
    }

    protected void setScreenToValues() {

        isLoadAsynchronously = Preferences.getInstance().isLoadSpooledFilesAsynchronousliy();
        defaultFormat = Preferences.getInstance().getSpooledFileConversionDefaultFormat();

        conversionText = Preferences.getInstance().getSpooledFileConversionText();
        conversionTextLibrary = Preferences.getInstance().getSpooledFileConversionTextLibrary();
        conversionTextCommand = Preferences.getInstance().getSpooledFileConversionTextCommand();

        conversionHTML = Preferences.getInstance().getSpooledFileConversionHTML();
        conversionHTMLLibrary = Preferences.getInstance().getSpooledFileConversionHTMLLibrary();
        conversionHTMLCommand = Preferences.getInstance().getSpooledFileConversionHTMLCommand();

        conversionPDF = Preferences.getInstance().getSpooledFileConversionPDF();
        conversionPDFLibrary = Preferences.getInstance().getSpooledFileConversionPDFLibrary();
        conversionPDFCommand = Preferences.getInstance().getSpooledFileConversionPDFCommand();

        conversionPDFPageSize = Preferences.getInstance().getSpooledFilePageSize();
        adjustFontSize = Preferences.getInstance().getSpooledFileAdjustFontSize();
        comboSuggestedFileName.setText(Preferences.getInstance().getSpooledFilesSuggestedFileName());

        setScreenValues();

    }

    protected void setScreenToDefaultValues() {

        isLoadAsynchronously = Preferences.getInstance().getDefaultLoadSpooledFilesAsynchronously();
        defaultFormat = Preferences.getInstance().getDefaultSpooledFileConversionDefaultFormat();

        conversionText = Preferences.getInstance().getDefaultSpooledFileConversionText();
        conversionTextLibrary = Preferences.getInstance().getDefaultSpooledFileConversionTextLibrary();
        conversionTextCommand = Preferences.getInstance().getDefaultSpooledFileConversionTextCommand();

        conversionHTML = Preferences.getInstance().getDefaultSpooledFileConversionHTML();
        conversionHTMLLibrary = Preferences.getInstance().getDefaultSpooledFileConversionHTMLLibrary();
        conversionHTMLCommand = Preferences.getInstance().getDefaultSpooledFileConversionHTMLCommand();

        conversionPDF = Preferences.getInstance().getDefaultSpooledFileConversionPDF();
        conversionPDFLibrary = Preferences.getInstance().getDefaultSpooledFileConversionPDFLibrary();
        conversionPDFCommand = Preferences.getInstance().getDefaultSpooledFileConversionPDFCommand();

        conversionPDFPageSize = Preferences.getInstance().getDefaultSpooledFilePageSize();
        adjustFontSize = Preferences.getInstance().getDefaultSpooledFileAdjustFontSize();
        comboSuggestedFileName.setText(Preferences.getInstance().getDefaultSpooledFilesSuggestedFileName());

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

        if (!StringHelper.isNullOrEmpty(conversionPDFPageSize)) {
            comboConversionPDFPageSize.setText(conversionPDFPageSize);
        } else {
            comboConversionPDFPageSize.setText(PageSize.PAGE_SIZE_A4);
        }

        chkBoxAdjustFontSize.setSelection(adjustFontSize);
        comboSuggestedFileName.setText(Preferences.getInstance().getSpooledFilesSuggestedFileName());
    }

    public void init(IWorkbench workbench) {
    }

}