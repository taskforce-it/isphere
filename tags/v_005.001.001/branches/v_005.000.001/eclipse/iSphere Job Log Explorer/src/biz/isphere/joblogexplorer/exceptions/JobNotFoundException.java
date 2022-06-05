/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.exceptions;

import biz.isphere.joblogexplorer.Messages;

public class JobNotFoundException extends BasicJobLogLoaderException {

    private static final long serialVersionUID = 8470572378546941786L;

    private String jobName;
    private String userName;
    private String jobNumber;

    public JobNotFoundException(String jobName, String userName, String jobNumber, String message) {
        super(message);
        this.jobName = jobName;
        this.userName = userName;
        this.jobNumber = jobNumber;
    }

    @Override
    public String getMessage() {
        String message = Messages.bind(Messages.Job_C_B_A_not_found, new Object[] { jobName, userName, jobNumber });
        return message;
    }
}
