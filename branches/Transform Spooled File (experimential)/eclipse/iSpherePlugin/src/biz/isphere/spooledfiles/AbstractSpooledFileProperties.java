/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.spooledfiles;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;

import biz.isphere.Messages;


public abstract class AbstractSpooledFileProperties extends PropertyPage {

	public AbstractSpooledFileProperties() {
		super();
		noDefaultAndApplyButton();
	}

	protected Control createContents(Composite parent) {
		
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		Composite propGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		propGroup.setLayout(layout);
		propGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		SpooledFile spooledFile = getSpooledFile();
		
		Label file = new Label(propGroup, SWT.NONE);
		file.setText(Messages.getString("File") + ":");
		Label _file = new Label(propGroup, SWT.NONE);
		_file.setText(spooledFile.getFile());
		
		Label fileNumber = new Label(propGroup, SWT.NONE);
		fileNumber.setText(Messages.getString("File_number") + ":");
		Label _fileNumber = new Label(propGroup, SWT.NONE);
		_fileNumber.setText(Integer.toString(spooledFile.getFileNumber()));
		
		Label jobName = new Label(propGroup, SWT.NONE);
		jobName.setText(Messages.getString("Job_name") + ":");
		Label _jobName = new Label(propGroup, SWT.NONE);
		_jobName.setText(spooledFile.getJobName());
		
		Label jobUser = new Label(propGroup, SWT.NONE);
		jobUser.setText(Messages.getString("Job_user") + ":");
		Label _jobUser = new Label(propGroup, SWT.NONE);
		_jobUser.setText(spooledFile.getJobUser());
		
		Label jobNumber = new Label(propGroup, SWT.NONE);
		jobNumber.setText(Messages.getString("Job_number") + ":");
		Label _jobNumber = new Label(propGroup, SWT.NONE);
		_jobNumber.setText(spooledFile.getJobNumber());
		
		Label jobSystem = new Label(propGroup, SWT.NONE);
		jobSystem.setText(Messages.getString("Job_system") + ":");
		Label _jobSystem = new Label(propGroup, SWT.NONE);
		_jobSystem.setText(spooledFile.getJobSystem());
		
		Label creationDate = new Label(propGroup, SWT.NONE);
		creationDate.setText(Messages.getString("Creation_date") + ":");
		Label _creationDate = new Label(propGroup, SWT.NONE);
		_creationDate.setText(spooledFile.getCreationDateFormated());
		
		Label creationTime = new Label(propGroup, SWT.NONE);
		creationTime.setText(Messages.getString("Creation_time") + ":");
		Label _creationTime = new Label(propGroup, SWT.NONE);
		_creationTime.setText(spooledFile.getCreationTimeFormated());
		
		Label status = new Label(propGroup, SWT.NONE);
		status.setText(Messages.getString("Status") + ":");
		Label _status = new Label(propGroup, SWT.NONE);
		_status.setText(spooledFile.getStatus());

		Label outputQueue = new Label(propGroup, SWT.NONE);
		outputQueue.setText(Messages.getString("Output_queue") + ":");
		Label _outputQueue = new Label(propGroup, SWT.NONE);
		_outputQueue.setText(spooledFile.getOutputQueueFormated());
		
		Label outputPriority = new Label(propGroup, SWT.NONE);
		outputPriority.setText(Messages.getString("Output_priority") + ":");
		Label _outputPriority = new Label(propGroup, SWT.NONE);
		_outputPriority.setText(spooledFile.getOutputPriority());
		
		Label userData = new Label(propGroup, SWT.NONE);
		userData.setText(Messages.getString("User_data") + ":");
		Label _userData = new Label(propGroup, SWT.NONE);
		_userData.setText(spooledFile.getUserData());
		
		Label formType = new Label(propGroup, SWT.NONE);
		formType.setText(Messages.getString("Form_type") + ":");
		Label _formType = new Label(propGroup, SWT.NONE);
		_formType.setText(spooledFile.getFormType());
		
		Label copies = new Label(propGroup, SWT.NONE);
		copies.setText(Messages.getString("Copies") + ":");
		Label _copies = new Label(propGroup, SWT.NONE);
		_copies.setText(Integer.toString(spooledFile.getCopies()));
		
		Label pages = new Label(propGroup, SWT.NONE);
		pages.setText(Messages.getString("Pages") + ":");
		Label _pages = new Label(propGroup, SWT.NONE);
		_pages.setText(Integer.toString(spooledFile.getPages()));
		
		Label currentPage = new Label(propGroup, SWT.NONE);
		currentPage.setText(Messages.getString("Current_page") + ":");
		Label _currentPage = new Label(propGroup, SWT.NONE);
		_currentPage.setText(Integer.toString(spooledFile.getCurrentPage()));
		
		return propGroup;
		
	}
	
	public abstract SpooledFile getSpooledFile();

}
