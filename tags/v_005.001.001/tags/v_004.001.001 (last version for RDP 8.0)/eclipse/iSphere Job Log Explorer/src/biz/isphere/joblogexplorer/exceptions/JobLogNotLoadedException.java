/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.exceptions;

import biz.isphere.joblogexplorer.Messages;

public class JobLogNotLoadedException extends BasicJobLogLoaderException {

    private static final long serialVersionUID = 7185214712627033235L;

    private String message;

    public JobLogNotLoadedException(String fileName, String reason) {
        super(Messages.bind(Messages.Could_not_load_job_log_from_file_A_Reason_B, new Object[] { fileName, reason }));
    }

    public JobLogNotLoadedException(String jobName, String userName, String jobNumber, String reason) {
        super(Messages.bind(Messages.Could_not_load_job_log_of_job_C_B_A_Reason_D, new Object[] { jobName, userName, jobNumber, reason }));
    }
}
