/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;

import biz.isphere.core.Messages;

public abstract class AbstractSpooledFileProperties extends PropertyPage {

    public AbstractSpooledFileProperties() {
        super();
        noDefaultAndApplyButton();
    }

    @Override
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
        file.setText(Messages.File + ":");
        Label _file = new Label(propGroup, SWT.NONE);
        _file.setText(spooledFile.getFile());

        Label fileNumber = new Label(propGroup, SWT.NONE);
        fileNumber.setText(Messages.File_number + ":");
        Label _fileNumber = new Label(propGroup, SWT.NONE);
        _fileNumber.setText(Integer.toString(spooledFile.getFileNumber()));

        Label jobName = new Label(propGroup, SWT.NONE);
        jobName.setText(Messages.Job_name + ":");
        Label _jobName = new Label(propGroup, SWT.NONE);
        _jobName.setText(spooledFile.getJobName());

        Label jobUser = new Label(propGroup, SWT.NONE);
        jobUser.setText(Messages.Job_user + ":");
        Label _jobUser = new Label(propGroup, SWT.NONE);
        _jobUser.setText(spooledFile.getJobUser());

        Label jobNumber = new Label(propGroup, SWT.NONE);
        jobNumber.setText(Messages.Job_number + ":");
        Label _jobNumber = new Label(propGroup, SWT.NONE);
        _jobNumber.setText(spooledFile.getJobNumber());

        Label jobSystem = new Label(propGroup, SWT.NONE);
        jobSystem.setText(Messages.Job_system + ":");
        Label _jobSystem = new Label(propGroup, SWT.NONE);
        _jobSystem.setText(spooledFile.getJobSystem());

        Label creationDate = new Label(propGroup, SWT.NONE);
        creationDate.setText(Messages.Creation_date + ":");
        Label _creationDate = new Label(propGroup, SWT.NONE);
        _creationDate.setText(spooledFile.getCreationDateFormated());

        Label creationTime = new Label(propGroup, SWT.NONE);
        creationTime.setText(Messages.Creation_time + ":");
        Label _creationTime = new Label(propGroup, SWT.NONE);
        _creationTime.setText(spooledFile.getCreationTimeFormated());

        Label status = new Label(propGroup, SWT.NONE);
        status.setText(Messages.Status + ":");
        Label _status = new Label(propGroup, SWT.NONE);
        _status.setText(spooledFile.getStatus());

        Label outputQueue = new Label(propGroup, SWT.NONE);
        outputQueue.setText(Messages.Output_queue + ":");
        Label _outputQueue = new Label(propGroup, SWT.NONE);
        _outputQueue.setText(spooledFile.getOutputQueueFormated());

        Label outputPriority = new Label(propGroup, SWT.NONE);
        outputPriority.setText(Messages.Output_priority + ":");
        Label _outputPriority = new Label(propGroup, SWT.NONE);
        _outputPriority.setText(spooledFile.getOutputPriority());

        Label userData = new Label(propGroup, SWT.NONE);
        userData.setText(Messages.User_data + ":");
        Label _userData = new Label(propGroup, SWT.NONE);
        _userData.setText(spooledFile.getUserData());

        Label formType = new Label(propGroup, SWT.NONE);
        formType.setText(Messages.Form_type + ":");
        Label _formType = new Label(propGroup, SWT.NONE);
        _formType.setText(spooledFile.getFormType());

        Label copies = new Label(propGroup, SWT.NONE);
        copies.setText(Messages.Copies + ":");
        Label _copies = new Label(propGroup, SWT.NONE);
        _copies.setText(Integer.toString(spooledFile.getCopies()));

        Label pages = new Label(propGroup, SWT.NONE);
        pages.setText(Messages.Pages + ":");
        Label _pages = new Label(propGroup, SWT.NONE);
        _pages.setText(Integer.toString(spooledFile.getPages()));

        Label currentPage = new Label(propGroup, SWT.NONE);
        currentPage.setText(Messages.Current_page + ":");
        Label _currentPage = new Label(propGroup, SWT.NONE);
        _currentPage.setText(Integer.toString(spooledFile.getCurrentPage()));

        return propGroup;

    }

    public abstract SpooledFile getSpooledFile();

}
