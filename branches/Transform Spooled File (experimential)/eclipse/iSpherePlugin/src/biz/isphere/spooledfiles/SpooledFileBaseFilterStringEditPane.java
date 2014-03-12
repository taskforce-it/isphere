/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.spooledfiles;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import biz.isphere.Messages;
import biz.isphere.spooledfiles.SpooledFileFilter;


public class SpooledFileBaseFilterStringEditPane {
		
	private Text userText;
	private Text outqText;
	private Text outqLibText;
	private Text userDataText;
	private Text formTypeText;
	
	public void createContents(Composite composite_prompts, ModifyListener keyListener, String inputFilterString) {
		
		Label userLabel = new Label(composite_prompts, SWT.NONE);
		userLabel.setText(Messages.getString("User") + ":");
		userText = new Text(composite_prompts, SWT.BORDER);
		GridData gd = new GridData();
		gd.widthHint = 75;
		userText.setLayoutData(gd);
		userText.setTextLimit(10);
		
		Label outqLabel = new Label(composite_prompts, SWT.NONE);
		outqLabel.setText(Messages.getString("Output_queue") + ":");
		outqText = new Text(composite_prompts, SWT.BORDER);
		gd = new GridData();
		gd.widthHint = 75;
		outqText.setLayoutData(gd);
		outqText.setTextLimit(10);
		
		Label outqLibLabel = new Label(composite_prompts, SWT.NONE);
		outqLibLabel.setText(Messages.getString("___Library") + ":");
		outqLibText = new Text(composite_prompts, SWT.BORDER);
		gd = new GridData();
		gd.widthHint = 75;
		outqLibText.setLayoutData(gd);
		outqLibText.setTextLimit(10);
		
		Label dtaLabel = new Label(composite_prompts, SWT.NONE);
		dtaLabel.setText(Messages.getString("User_data") + ":");
		userDataText = new Text(composite_prompts, SWT.BORDER);
		gd = new GridData();
		gd.widthHint = 75;
		userDataText.setLayoutData(gd);
		userDataText.setTextLimit(10);
		
		Label typeLabel = new Label(composite_prompts, SWT.NONE);
		typeLabel.setText(Messages.getString("Form_type") + ":");
		formTypeText = new Text(composite_prompts, SWT.BORDER);
		gd = new GridData();
		gd.widthHint = 75;
		formTypeText.setLayoutData(gd);
		formTypeText.setTextLimit(10);
				
		resetFields();
		doInitializeFields(inputFilterString);
		
		userText.addModifyListener(keyListener);
		outqText.addModifyListener(keyListener);
		outqLibText.addModifyListener(keyListener);
		userDataText.addModifyListener(keyListener);
		formTypeText.addModifyListener(keyListener);
		
	}
	
	public Control getInitialFocusControl() {
		return userText;
	}	
	
	public void doInitializeFields(String inputFilterString) {
		if (inputFilterString != null) {
			SpooledFileFilter filter = new SpooledFileFilter(inputFilterString);
			if (filter.getUser() != null) userText.setText(filter.getUser());
			else userText.setText("*");
			if (filter.getOutputQueue() != null) outqText.setText(filter.getOutputQueue());
			else outqText.setText("*");
			if (filter.getOutputQueueLibrary() != null) outqLibText.setText(filter.getOutputQueueLibrary());
			else outqLibText.setText("*");
			if (filter.getUserData() != null) userDataText.setText(filter.getUserData());
			else userDataText.setText("*");
			if (filter.getFormType() != null) formTypeText.setText(filter.getFormType());
			else formTypeText.setText("*");
		}
	}
	
	public void resetFields() {
	    userText.setText("*");
	    outqText.setText("*");
	    outqLibText.setText("*");
	    userDataText.setText("*");
	    formTypeText.setText("*");
	}
	
	public boolean areFieldsComplete() {
		return true;
	}
	
	public String getFilterString() {
		SpooledFileFilter filter = new SpooledFileFilter();
		if ((userText.getText() != null) && (userText.getText().length() > 0) && (!userText.getText().equals("*")))
			filter.setUser(userText.getText().toUpperCase());
		if ((outqText.getText() != null) && (outqText.getText().length() > 0) && (!outqText.getText().equals("*")))
			filter.setOutputQueue(outqText.getText().toUpperCase());
		if ((outqLibText.getText() != null) && (outqLibText.getText().length() > 0) && (!outqLibText.getText().equals("*")))
			filter.setOutputQueueLibrary(outqLibText.getText().toUpperCase());
		if ((userDataText.getText() != null) && (userDataText.getText().length() > 0) && (!userDataText.getText().equals("*")))
			filter.setUserData(userDataText.getText().toUpperCase());
		if ((formTypeText.getText() != null) && (formTypeText.getText().length() > 0) && (!formTypeText.getText().equals("*")))
			filter.setFormType(formTypeText.getText().toUpperCase());
		return filter.getFilterString();
	}	

}
