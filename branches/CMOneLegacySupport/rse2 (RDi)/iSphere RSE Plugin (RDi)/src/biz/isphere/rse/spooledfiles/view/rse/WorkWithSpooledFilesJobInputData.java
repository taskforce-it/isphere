/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles.view.rse;

import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.subsystems.ISubSystem;

import biz.isphere.core.internal.QualifiedJobName;
import biz.isphere.core.spooledfiles.SpooledFileFilter;
import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesInputData;

public class WorkWithSpooledFilesJobInputData extends AbstractWorkWithSpooledFilesInputData {

    private String connectionName;
    private QualifiedJobName qualifiedJobName;

    private SpooledFileFilter spooledFileFilter;

    public WorkWithSpooledFilesJobInputData(String connectionName, QualifiedJobName qualifiedJobName) {
        this.connectionName = connectionName;
        this.qualifiedJobName = qualifiedJobName;

        this.spooledFileFilter = new SpooledFileFilter();
        this.spooledFileFilter.setJobName(this.qualifiedJobName.getJob());
        this.spooledFileFilter.setUser(this.qualifiedJobName.getUser());
        this.spooledFileFilter.setJobNumber(this.qualifiedJobName.getNumber());
    }

    @Override
    public String getConnectionName() {
        return connectionName;
    }

    @Override
    public String getFilterPoolName() {
        return "";
    }

    @Override
    public String getFilterName() {
        return qualifiedJobName.getQualifiedJobName();
    }

    @Override
    public String[] getFilterStrings() {
        return new String[] { spooledFileFilter.getFilterString() };
    }

    @Override
    public boolean isPersistable() {
        return false;
    }

    public boolean referencesFilter(ISubSystem subSystem, ISystemFilter systemFilter) {
        return false;
    }
}
