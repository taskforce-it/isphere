/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles.view.rse;

import biz.isphere.core.internal.QualifiedJobName;
import biz.isphere.core.spooledfiles.SpooledFileFilter;
import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesInputData;

public class WorkWithSpooledFilesJobInputData extends AbstractWorkWithSpooledFilesInputData {

    private static final String INPUT_TYPE = "job://"; //$NON-NLS-1$

    private QualifiedJobName qualifiedJobName;

    public WorkWithSpooledFilesJobInputData(String connectionName, String jobName, String userName, String jobNumber) {
        super(connectionName);
        this.qualifiedJobName = new QualifiedJobName(jobName, userName, jobNumber);

        SpooledFileFilter spooledFileFilter = new SpooledFileFilter();
        spooledFileFilter.setJobName(this.qualifiedJobName.getJob());
        spooledFileFilter.setUser(this.qualifiedJobName.getUser());
        spooledFileFilter.setJobNumber(this.qualifiedJobName.getNumber());

        addFilterString(spooledFileFilter.getFilterString());
    }

    public String getFilterPoolName() {
        return ""; // //$NON-NLS-1$
    }

    @Override
    public String getFilterName() {
        return qualifiedJobName.getQualifiedJobName();
    }

    @Override
    public boolean isPersistable() {
        return false;
    }

    @Override
    public String getContentId() {
        return INPUT_TYPE + String.format("%s:%s", getConnectionName(), getFilterName()); //$NON-NLS-1$
    }
}
