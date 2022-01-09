/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.preferencepages;

import org.eclipse.jface.preference.IPreferenceStore;
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

import biz.isphere.ISpherePlugin;
import biz.isphere.Messages;
import biz.isphere.internal.Validator;


public class ISphereSpooledFiles extends PreferencePage implements IWorkbenchPreferencePage {
	
	private IPreferenceStore store;

	private Button buttonDefaultFormatText;
	private Button buttonDefaultFormatHTML;
	private Button buttonDefaultFormatPDF;
	private String defaultFormat;

	private Button buttonConversionTextDefault;
	private Button buttonConversionTextUserDefined;
	private String conversionText;
	private Text textConversionTextLibrary;
	private Validator validatorConversionTextLibrary;
	private String conversionTextLibrary;
	private Text textConversionTextCommand;
	private String conversionTextCommand;

	private Button buttonConversionHTMLDefault;
	private Button buttonConversionHTMLUserDefined;
	private String conversionHTML;
	private Text textConversionHTMLLibrary;
	private Validator validatorConversionHTMLLibrary;
	private String conversionHTMLLibrary;
	private Text textConversionHTMLCommand;
	private String conversionHTMLCommand;

	private Button buttonConversionPDFDefault;
	private Button buttonConversionPDFUserDefined;
	private String conversionPDF;
	private Text textConversionPDFLibrary;
	private Validator validatorConversionPDFLibrary;
	private String conversionPDFLibrary;
	private Text textConversionPDFCommand;
	private String conversionPDFCommand;
	
	public ISphereSpooledFiles() {
		super();
		setPreferenceStore(ISpherePlugin.getDefault().getPreferenceStore());
		store = getPreferenceStore();
	}
	
	public Control createContents(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		
		Group groupDefaultFormat = new Group(container, SWT.NONE);
		groupDefaultFormat.setText(Messages.getString("When_double_clicking_on_a_spooled_file_open_it_as"));
		GridLayout gridLayoutDefaultFormat = new GridLayout();
		gridLayoutDefaultFormat.numColumns = 3;
		groupDefaultFormat.setLayout(gridLayoutDefaultFormat);
		groupDefaultFormat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		buttonDefaultFormatText = new Button(groupDefaultFormat, SWT.RADIO);
		buttonDefaultFormatText.setText("*TEXT");
		buttonDefaultFormatText.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				defaultFormat = "*TEXT";
			}
		});

		buttonDefaultFormatHTML = new Button(groupDefaultFormat, SWT.RADIO);
		buttonDefaultFormatHTML.setText("*HTML");
		buttonDefaultFormatHTML.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				defaultFormat = "*HTML";
			}
		});

		buttonDefaultFormatPDF = new Button(groupDefaultFormat, SWT.RADIO);
		buttonDefaultFormatPDF.setText("*PDF");
		buttonDefaultFormatPDF.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				defaultFormat = "*PDF";
			}
		});
		
		Group groupConversionText = new Group(container, SWT.NONE);
		groupConversionText.setText(Messages.getString("Conversion_to_format") + " *TEXT");
		GridLayout gridLayoutConversionText = new GridLayout();
		gridLayoutConversionText.numColumns = 2;
		groupConversionText.setLayout(gridLayoutConversionText);
		groupConversionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		buttonConversionTextDefault = new Button(groupConversionText, SWT.RADIO);
		buttonConversionTextDefault.setText(Messages.getString("Default"));
		buttonConversionTextDefault.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				conversionText = "*DFT";
				textConversionTextLibrary.setEnabled(false);
				textConversionTextCommand.setEnabled(false);
				checkError();
			}
		});

		buttonConversionTextUserDefined = new Button(groupConversionText, SWT.RADIO);
		buttonConversionTextUserDefined.setText(Messages.getString("User_defined"));
		buttonConversionTextUserDefined.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				conversionText = "*USRDEF";
				textConversionTextLibrary.setEnabled(true);
				textConversionTextCommand.setEnabled(true);
				checkError();
			}
		});
		
		Label labelConversionTextLibrary = new Label(groupConversionText, SWT.NONE);
		labelConversionTextLibrary.setText(Messages.getString("Library_colon"));

		textConversionTextLibrary = new Text(groupConversionText, SWT.BORDER);
		textConversionTextLibrary.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				checkError();
			}
		});
		textConversionTextLibrary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textConversionTextLibrary.setTextLimit(10);
		
		validatorConversionTextLibrary = new Validator();
		validatorConversionTextLibrary.setType("*NAME");
		validatorConversionTextLibrary.setLength(10);
		validatorConversionTextLibrary.setRestricted(false);
		
		Label labelConversionTextCommand = new Label(groupConversionText, SWT.NONE);
		labelConversionTextCommand.setText(Messages.getString("Command_colon"));

		textConversionTextCommand = new Text(groupConversionText, SWT.BORDER);
		textConversionTextCommand.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				checkError();
			}
		});
		textConversionTextCommand.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textConversionTextCommand.setTextLimit(256);
		
		Group groupConversionHTML = new Group(container, SWT.NONE);
		groupConversionHTML.setText(Messages.getString("Conversion_to_format") + " *HTML");
		GridLayout gridLayoutConversionHTML = new GridLayout();
		gridLayoutConversionHTML.numColumns = 2;
		groupConversionHTML.setLayout(gridLayoutConversionHTML);
		groupConversionHTML.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		buttonConversionHTMLDefault = new Button(groupConversionHTML, SWT.RADIO);
		buttonConversionHTMLDefault.setText(Messages.getString("Default"));
		buttonConversionHTMLDefault.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				conversionHTML = "*DFT";
				textConversionHTMLLibrary.setEnabled(false);
				textConversionHTMLCommand.setEnabled(false);
				checkError();
			}
		});

		buttonConversionHTMLUserDefined = new Button(groupConversionHTML, SWT.RADIO);
		buttonConversionHTMLUserDefined.setText(Messages.getString("User_defined"));
		buttonConversionHTMLUserDefined.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				conversionHTML = "*USRDEF";
				textConversionHTMLLibrary.setEnabled(true);
				textConversionHTMLCommand.setEnabled(true);
				checkError();
			}
		});
		
		Label labelConversionHTMLLibrary = new Label(groupConversionHTML, SWT.NONE);
		labelConversionHTMLLibrary.setText(Messages.getString("Library_colon"));

		textConversionHTMLLibrary = new Text(groupConversionHTML, SWT.BORDER);
		textConversionHTMLLibrary.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				checkError();
			}
		});
		textConversionHTMLLibrary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textConversionHTMLLibrary.setTextLimit(10);
		
		validatorConversionHTMLLibrary = new Validator();
		validatorConversionHTMLLibrary.setType("*NAME");
		validatorConversionHTMLLibrary.setLength(10);
		validatorConversionHTMLLibrary.setRestricted(false);
		
		Label labelConversionHTMLCommand = new Label(groupConversionHTML, SWT.NONE);
		labelConversionHTMLCommand.setText(Messages.getString("Command_colon"));

		textConversionHTMLCommand = new Text(groupConversionHTML, SWT.BORDER);
		textConversionHTMLCommand.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				checkError();
			}
		});
		textConversionHTMLCommand.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textConversionHTMLCommand.setTextLimit(256);
		
		Group groupConversionPDF = new Group(container, SWT.NONE);
		groupConversionPDF.setText(Messages.getString("Conversion_to_format") + " *PDF");
		GridLayout gridLayoutConversionPDF = new GridLayout();
		gridLayoutConversionPDF.numColumns = 2;
		groupConversionPDF.setLayout(gridLayoutConversionPDF);
		groupConversionPDF.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		buttonConversionPDFDefault = new Button(groupConversionPDF, SWT.RADIO);
		buttonConversionPDFDefault.setText(Messages.getString("Default"));
		buttonConversionPDFDefault.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				conversionPDF = "*DFT";
				textConversionPDFLibrary.setEnabled(false);
				textConversionPDFCommand.setEnabled(false);
				checkError();
			}
		});

		buttonConversionPDFUserDefined = new Button(groupConversionPDF, SWT.RADIO);
		buttonConversionPDFUserDefined.setText(Messages.getString("User_defined"));
		buttonConversionPDFUserDefined.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				conversionPDF = "*USRDEF";
				textConversionPDFLibrary.setEnabled(true);
				textConversionPDFCommand.setEnabled(true);
				checkError();
			}
		});
		
		Label labelConversionPDFLibrary = new Label(groupConversionPDF, SWT.NONE);
		labelConversionPDFLibrary.setText(Messages.getString("Library_colon"));

		textConversionPDFLibrary = new Text(groupConversionPDF, SWT.BORDER);
		textConversionPDFLibrary.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				checkError();
			}
		});
		textConversionPDFLibrary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textConversionPDFLibrary.setTextLimit(10);
		
		validatorConversionPDFLibrary = new Validator();
		validatorConversionPDFLibrary.setType("*NAME");
		validatorConversionPDFLibrary.setLength(10);
		validatorConversionPDFLibrary.setRestricted(false);
		
		Label labelConversionPDFCommand = new Label(groupConversionPDF, SWT.NONE);
		labelConversionPDFCommand.setText(Messages.getString("Command_colon"));

		textConversionPDFCommand = new Text(groupConversionPDF, SWT.BORDER);
		textConversionPDFCommand.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				checkError();
			}
		});
		textConversionPDFCommand.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textConversionPDFCommand.setTextLimit(256);
		
		Group groupSubstitutionVariables = new Group(container, SWT.NONE);
		groupSubstitutionVariables.setText(Messages.getString("Substitution_variables_for_conversion_commands"));
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
		labelSubstitutionVariable13.setText(Messages.getString("File"));

		Text labelSubstitutionVariable21 = new Text(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable21.setText("&SPLFNBR");
		labelSubstitutionVariable21.setEditable(false);
		
		Label labelSubstitutionVariable22 = new Label(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable22.setText(":");
		
		Label labelSubstitutionVariable23 = new Label(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable23.setText(Messages.getString("File_number"));
		
		Text labelSubstitutionVariable31 = new Text(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable31.setText("&JOBNAME");
		labelSubstitutionVariable31.setEditable(false);
		
		Label labelSubstitutionVariable32 = new Label(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable32.setText(":");
		
		Label labelSubstitutionVariable33 = new Label(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable33.setText(Messages.getString("Job_name"));
		
		Text labelSubstitutionVariable41 = new Text(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable41.setText("&JOBUSR");
		labelSubstitutionVariable41.setEditable(false);
		
		Label labelSubstitutionVariable42 = new Label(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable42.setText(":");
		
		Label labelSubstitutionVariable43 = new Label(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable43.setText(Messages.getString("Job_user"));
		
		Text labelSubstitutionVariable51 = new Text(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable51.setText("&JOBNBR");
		labelSubstitutionVariable51.setEditable(false);
		
		Label labelSubstitutionVariable52 = new Label(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable52.setText(":");
		
		Label labelSubstitutionVariable53 = new Label(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable53.setText(Messages.getString("Job_number"));
		
		Text labelSubstitutionVariable61 = new Text(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable61.setText("&STMFDIR");
		labelSubstitutionVariable61.setEditable(false);
		
		Label labelSubstitutionVariable62 = new Label(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable62.setText(":");
		
		Label labelSubstitutionVariable63 = new Label(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable63.setText(Messages.getString("Directory"));

		Text labelSubstitutionVariable71 = new Text(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable71.setText("&STMF");
		labelSubstitutionVariable71.setEditable(false);
		
		Label labelSubstitutionVariable72 = new Label(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable72.setText(":");
		
		Label labelSubstitutionVariable73 = new Label(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable73.setText(Messages.getString("Stream_file"));
		
		Text labelSubstitutionVariable81 = new Text(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable81.setText("&CODPAG");
		labelSubstitutionVariable81.setEditable(false);
		
		Label labelSubstitutionVariable82 = new Label(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable82.setText(":");
		
		Label labelSubstitutionVariable83 = new Label(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable83.setText(Messages.getString("Code_page"));
		
		Text labelSubstitutionVariable91 = new Text(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable91.setText("&FMT");
		labelSubstitutionVariable91.setEditable(false);
		
		Label labelSubstitutionVariable92 = new Label(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable92.setText(":");
		
		Label labelSubstitutionVariable93 = new Label(groupSubstitutionVariables, SWT.NONE);
		labelSubstitutionVariable93.setText(Messages.getString("Format"));
		
		setScreenToValues();
		
		return container;
	}

	public void checkError() {
		
		if (conversionText.equals("*USRDEF")) {
			
			conversionTextLibrary = textConversionTextLibrary.getText().toUpperCase().trim();
			if (conversionTextLibrary.equals("") || !validatorConversionTextLibrary.validate(conversionTextLibrary)) {
				setErrorMessage("*TEXT - " + Messages.getString("The_value_in_field_'Library'_is_not_valid."));
				setValid(false);
				return;
			}
			
			conversionTextCommand = textConversionTextCommand.getText().toUpperCase().trim();
			if (conversionTextCommand.equals("")) {
				setErrorMessage("*TEXT - " + Messages.getString("The_value_in_field_'Command'_is_not_valid."));
				setValid(false);
				return;
			}
			
		}
		
		if (conversionHTML.equals("*USRDEF")) {
			
			conversionHTMLLibrary = textConversionHTMLLibrary.getText().toUpperCase().trim();
			if (conversionHTMLLibrary.equals("") || !validatorConversionHTMLLibrary.validate(conversionHTMLLibrary)) {
				setErrorMessage("*HTML - " + Messages.getString("The_value_in_field_'Library'_is_not_valid."));
				setValid(false);
				return;
			}
			
			conversionHTMLCommand = textConversionHTMLCommand.getText().toUpperCase().trim();
			if (conversionHTMLCommand.equals("")) {
				setErrorMessage("*HTML - " + Messages.getString("The_value_in_field_'Command'_is_not_valid."));
				setValid(false);
				return;
			}
			
		}
		
		if (conversionPDF.equals("*USRDEF")) {
			
			conversionPDFLibrary = textConversionPDFLibrary.getText().toUpperCase().trim();
			if (conversionPDFLibrary.equals("") || !validatorConversionPDFLibrary.validate(conversionPDFLibrary)) {
				setErrorMessage("*PDF - " + Messages.getString("The_value_in_field_'Library'_is_not_valid."));
				setValid(false);
				return;
			}
			
			conversionPDFCommand = textConversionPDFCommand.getText().toUpperCase().trim();
			if (conversionPDFCommand.equals("")) {
				setErrorMessage("*PDF - " + Messages.getString("The_value_in_field_'Command'_is_not_valid."));
				setValid(false);
				return;
			}
			
		}
		
		setErrorMessage(null);
		setValid(true);
		return;
		
	}

	protected void performApply() {
		setStoreToValues();
		super.performApply();
	}

	protected void performDefaults() {
		setScreenToDefaultValues();
		super.performDefaults();
	}

	public boolean performOk() {
		setStoreToValues();
		return super.performOk();
	}
	
	protected void setStoreToValues() {
		
		store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.DEFAULT_FORMAT", defaultFormat);

		store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT", conversionText);
		store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT.LIBRARY", conversionTextLibrary);
		store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT.COMMAND", conversionTextCommand);

		store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML", conversionHTML);
		store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML.LIBRARY", conversionHTMLLibrary);
		store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML.COMMAND", conversionHTMLCommand);

		store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF", conversionPDF);
		store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF.LIBRARY", conversionPDFLibrary);
		store.setValue("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF.COMMAND", conversionPDFCommand);
	
	}

	protected void setScreenToValues() {

		defaultFormat = store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.DEFAULT_FORMAT");
		
		conversionText = store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT");
		conversionTextLibrary = store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT.LIBRARY");
		conversionTextCommand = store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT.COMMAND");
		
		conversionHTML = store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML");
		conversionHTMLLibrary = store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML.LIBRARY");
		conversionHTMLCommand = store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML.COMMAND");
		
		conversionPDF = store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF");
		conversionPDFLibrary = store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF.LIBRARY");
		conversionPDFCommand = store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF.COMMAND");

		setScreenValues();	
		
	}

	protected void setScreenToDefaultValues() {

		defaultFormat = store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.DEFAULT_FORMAT");
		
		conversionText = store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT");
		conversionTextLibrary = store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT.LIBRARY");
		conversionTextCommand = store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT.COMMAND");
		
		conversionHTML = store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML");
		conversionHTMLLibrary = store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML.LIBRARY");
		conversionHTMLCommand = store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML.COMMAND");
		
		conversionPDF = store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF");
		conversionPDFLibrary = store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF.LIBRARY");
		conversionPDFCommand = store.getDefaultString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF.COMMAND");

		setScreenValues();	
		
	}
	
	protected void setScreenValues() {

		buttonDefaultFormatText.setSelection(false);
		buttonDefaultFormatHTML.setSelection(false);
		buttonDefaultFormatPDF.setSelection(false);
		if (defaultFormat.equals("*TEXT")) {
			buttonDefaultFormatText.setSelection(true);
		}
		else if (defaultFormat.equals("*HTML")) {
			buttonDefaultFormatHTML.setSelection(true);
		}
		else if (defaultFormat.equals("*PDF")) {
			buttonDefaultFormatPDF.setSelection(true);
		}
		
		buttonConversionTextDefault.setSelection(false);
		buttonConversionTextUserDefined.setSelection(false);
		if (conversionText.equals("*DFT")) {
			buttonConversionTextDefault.setSelection(true);
		}
		else if (conversionText.equals("*USRDEF")) {
			buttonConversionTextUserDefined.setSelection(true);
		}
		textConversionTextLibrary.setText(conversionTextLibrary);
		if (conversionText.equals("*DFT")) {
			textConversionTextLibrary.setEnabled(false);
		}
		else if (conversionText.equals("*USRDEF")) {
			textConversionTextLibrary.setEnabled(true);
		}
		textConversionTextCommand.setText(conversionTextCommand);
		if (conversionText.equals("*DFT")) {
			textConversionTextCommand.setEnabled(false);
		}
		else if (conversionText.equals("*USRDEF")) {
			textConversionTextCommand.setEnabled(true);
		}
		
		buttonConversionHTMLDefault.setSelection(false);
		buttonConversionHTMLUserDefined.setSelection(false);
		if (conversionHTML.equals("*DFT")) {
			buttonConversionHTMLDefault.setSelection(true);
		}
		else if (conversionHTML.equals("*USRDEF")) {
			buttonConversionHTMLUserDefined.setSelection(true);
		}
		textConversionHTMLLibrary.setText(conversionHTMLLibrary);
		if (conversionHTML.equals("*DFT")) {
			textConversionHTMLLibrary.setEnabled(false);
		}
		else if (conversionHTML.equals("*USRDEF")) {
			textConversionHTMLLibrary.setEnabled(true);
		}
		textConversionHTMLCommand.setText(conversionHTMLCommand);
		if (conversionHTML.equals("*DFT")) {
			textConversionHTMLCommand.setEnabled(false);
		}
		else if (conversionHTML.equals("*USRDEF")) {
			textConversionHTMLCommand.setEnabled(true);
		}
		
		buttonConversionPDFDefault.setSelection(false);
		buttonConversionPDFUserDefined.setSelection(false);
		if (conversionPDF.equals("*DFT")) {
			buttonConversionPDFDefault.setSelection(true);
		}
		else if (conversionPDF.equals("*USRDEF")) {
			buttonConversionPDFUserDefined.setSelection(true);
		}
		textConversionPDFLibrary.setText(conversionPDFLibrary);
		if (conversionPDF.equals("*DFT")) {
			textConversionPDFLibrary.setEnabled(false);
		}
		else if (conversionPDF.equals("*USRDEF")) {
			textConversionPDFLibrary.setEnabled(true);
		}
		textConversionPDFCommand.setText(conversionPDFCommand);
		if (conversionPDF.equals("*DFT")) {
			textConversionPDFCommand.setEnabled(false);
		}
		else if (conversionPDF.equals("*USRDEF")) {
			textConversionPDFCommand.setEnabled(true);
		}
		
	}
	
	public void init(IWorkbench workbench) {
	}

}