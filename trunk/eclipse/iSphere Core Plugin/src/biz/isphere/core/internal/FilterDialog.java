/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;


public class FilterDialog extends Dialog {

	private Text textFilter;
	private StatusLineManager statusLineManager;
	private String filter = null;
	
	public FilterDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		Composite compositeFilter = new Composite(container, SWT.NONE);
		compositeFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		compositeFilter.setLayout(new GridLayout(2, false));
		
		Label labelFilter = new Label(compositeFilter, SWT.NONE);
		labelFilter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		labelFilter.setText(Messages.getString("Filter_colon"));
		
		textFilter = new Text(compositeFilter, SWT.BORDER);
		textFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textFilter.setText("");
        
		statusLineManager = new StatusLineManager(); 
        statusLineManager.createControl(container, SWT.NONE);
        Control statusLine = statusLineManager.getControl();        
		final GridData gridDataStatusLine = new GridData(SWT.FILL, SWT.CENTER, true, false);
		statusLine.setLayoutData(gridDataStatusLine);
		
		return container;
	}

	private void setErrorMessage(String errorMessage) {
		if (errorMessage != null) {
			statusLineManager.setErrorMessage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_ERROR), errorMessage);
		}
		else {
			statusLineManager.setErrorMessage(null, null);
		}
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.getString("OK"), true);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("Cancel"), false);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("Specify_a_filter"));
	}

	protected void okPressed() {
		
		textFilter.setText(textFilter.getText().trim());

		if (!Validator.validateFile(textFilter.getText())) {
			setErrorMessage(Messages.getString("The_value_in_field_'Filter'_is_not_valid."));
			textFilter.setFocus();
			return;
		}
		
		filter = textFilter.getText();
		
		super.okPressed();
		
	}
	
	protected Point getInitialSize() {
		return getShell().computeSize(Size.getSize(250), SWT.DEFAULT, true);
	}
	
	public String getFilter() {
		return filter;
	}
}
