/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilesearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.preferences.Preferences;

public class SearchDialog extends Dialog {
	
	private HashMap<String, SearchElement> searchElements;
	private String searchString;
	private Text textString;
	private Text textFromColumn;
	private Text textToColumn;
	private Button buttonCaseNo;
	private Button buttonCaseYes;
	private Button okButton;
	private String _string;
	private int _fromColumn = 1;
	private int _toColumn = 132;
	private String _case;
	
	public SearchDialog(Shell parentShell, HashMap<String, SearchElement> searchElements) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.searchElements = searchElements;
		ISpherePlugin.getDefault().getPreferenceStore();
        // TODO: Remove disabled statements 'DE.TASKFORCE'
		// searchString = store.getString("DE.TASKFORCE.ISPHERE.MESSAGEFILESEARCH.SEARCHSTRING");
		searchString = Preferences.getInstance().getMessageFileSearchString();
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		Group groupAttributes = new Group(container, SWT.NONE);
		groupAttributes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		groupAttributes.setText(Messages.getString("Attributes"));
		groupAttributes.setLayout(new GridLayout(2, false));
		
		Label labelString = new Label(groupAttributes, SWT.NONE);
		labelString.setText(Messages.getString("String_colon"));
		
		textString = new Text(groupAttributes, SWT.BORDER);
		textString.setText(searchString);
		textString.setTextLimit(40);
		textString.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textString.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				if (textString.getText().trim().equals("")) {
					okButton.setEnabled(false);
				}
				else {
					okButton.setEnabled(true);
				}
			}
		});
		
		Label labelFromColumn = new Label(groupAttributes, SWT.NONE);
		labelFromColumn.setText(Messages.getString("From_column_colon"));
		
		textFromColumn = new Text(groupAttributes, SWT.BORDER);
		textFromColumn.setText("1");
		textFromColumn.setTextLimit(3);
		textFromColumn.setLayoutData(new GridData(50, SWT.DEFAULT));
		textFromColumn.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				_fromColumn = 0;
				try {
					_fromColumn = Integer.parseInt(textFromColumn.getText().trim());
				} 
				catch (NumberFormatException e1) {
				}
				if (_fromColumn != 0 && _toColumn != 0 && _fromColumn <= _toColumn && _toColumn <= 132) {
					okButton.setEnabled(true);
				}
				else {
					okButton.setEnabled(false);
				}
			}
		});
		
		Label labelToColumn = new Label(groupAttributes, SWT.NONE);
		labelToColumn.setText(Messages.getString("To_column_colon"));
		
		textToColumn = new Text(groupAttributes, SWT.BORDER);
		textToColumn.setText("132");
		textToColumn.setTextLimit(3);
		textToColumn.setLayoutData(new GridData(50, SWT.DEFAULT));
		textToColumn.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				_toColumn = 0;
				try {
					_toColumn = Integer.parseInt(textToColumn.getText().trim());
				} 
				catch (NumberFormatException e1) {
				}
				if (_fromColumn != 0 && _toColumn != 0 && _fromColumn <= _toColumn && _toColumn <= 132) {
					okButton.setEnabled(true);
				}
				else {
					okButton.setEnabled(false);
				}
			}
		});
		
		Label labelCaseSensitive = new Label(groupAttributes, SWT.NONE);
		labelCaseSensitive.setText(Messages.getString("Case_sensitive_colon"));
		
		Composite groupCaseSensitive = new Composite(groupAttributes, SWT.NONE);
		GridLayout editableLayout = new GridLayout();
		editableLayout.numColumns = 2;
		groupCaseSensitive.setLayout(editableLayout);
		
		buttonCaseNo = new Button(groupCaseSensitive, SWT.RADIO);
		buttonCaseNo.setText(Messages.getString("No"));
		buttonCaseNo.setSelection(true);
		
		buttonCaseYes = new Button(groupCaseSensitive, SWT.RADIO);
		buttonCaseYes.setText(Messages.getString("Yes"));
		buttonCaseYes.setSelection(false);
		
		Group groupArea = new Group(container, SWT.NONE);
		groupArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		groupArea.setText(Messages.getString("Area"));
		groupArea.setLayout(new FillLayout(SWT.HORIZONTAL));

		List listArea = new List(groupArea, SWT.BORDER| SWT.V_SCROLL | SWT.H_SCROLL);
		
		ArrayList<String> items = new ArrayList<String>();
		SortedSet<String> keys = new TreeSet<String>(searchElements.keySet());
		Iterator<String> _iterator = keys.iterator();  
		while (_iterator.hasNext()) {  
			String key = (String)_iterator.next();  
			SearchElement value = (SearchElement)searchElements.get(key);
			String item = value.getLibrary() + "/" + value.getMessageFile() + " - \"" + value.getDescription() + "\"";
			items.add(item);
		}  
		String[] _items = new String[items.size()];
		items.toArray(_items);
		listArea.setItems(_items);
				
		return container;
	}
	
	protected void okPressed() {
        // TODO: Remove disabled statements 'DE.TASKFORCE'
		// store.setValue("DE.TASKFORCE.ISPHERE.MESSAGEFILESEARCH.SEARCHSTRING", textString.getText().trim());
	    Preferences.getInstance().setMessageFileSearchString(textString.getText());
		_string = textString.getText().trim();
		if (buttonCaseNo.getSelection()) {
			_case = "*IGNORE";
		}
		else {
			_case = "*MATCH";
		}
		super.okPressed();
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, IDialogConstants.OK_ID, Messages.getString("OK"), true);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("Cancel"), false);
		if (searchString.equals("")) {
			okButton.setEnabled(false);
		}
		else {
			okButton.setEnabled(true);
		}
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("iSphere_Message_File_Search"));
	}
	
	protected Point getInitialSize() {
		return getShell().computeSize(400, 600, true);
	}
	
	public String getString() {
		return _string;
	}
	
	public int getFromColumn() {
		return _fromColumn;
	}
	
	public int getToColumn() {
		return _toColumn;
	}
	
	public String getCase() {
		return _case;
	}
	
}
