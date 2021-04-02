/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.externalapi;

import biz.isphere.joblogexplorer.Messages;

public class SpooledFileNotFoundException extends Exception {

    private static final long serialVersionUID = 6211854205694818288L;
    private String connectionName;
    private String jobName;
    private String userName;
    private String jobNumber;
    private String splfName;
    private int splfNumber;

    public SpooledFileNotFoundException(String connectionName, String jobName, String userName, String jobNumber, String splfName, int splfNumber) {
        this.connectionName = connectionName;
        this.jobName = jobName;
        this.userName = userName;
        this.jobNumber = jobNumber;
        this.splfName = splfName;
        this.splfNumber = splfNumber;
    }

    public String getMessage() {
        return Messages.bind(Messages.Error_Spooled_file_B_of_job_F_E_D_of_connection_A_not_found, new Object[] { connectionName, splfName,
            splfNumber, jobName, userName, jobNumber });
    };
}
