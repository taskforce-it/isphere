/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles.view.rse;

import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesInputData;
import biz.isphere.rse.connection.ConnectionManager;

import com.ibm.etools.systems.filters.SystemFilter;
import com.ibm.etools.systems.subsystems.SubSystem;

public class WorkWithSpooledFilesInputData extends AbstractWorkWithSpooledFilesInputData {

    private SubSystem subSystem;
    private SystemFilter systemFilter;

    public WorkWithSpooledFilesInputData(SubSystem subSystem, SystemFilter systemFilter) {
        this.subSystem = subSystem;
        this.systemFilter = systemFilter;
    }

    @Override
    public String getConnectionName() {
        return ConnectionManager.getConnectionName(subSystem.getSystemConnection());
    }

    @Override
    public String getFilterPoolName() {
        return systemFilter.getParentFilterPool().getName();
    }

    @Override
    public String getFilterName() {
        return systemFilter.getName();
    }

    @Override
    public String[] getFilterStrings() {
        return systemFilter.getFilterStrings();
    }

    public boolean referencesFilter(SubSystem subSystem, SystemFilter systemFilter) {

        if (this.subSystem.equals(subSystem) && this.systemFilter.equals(systemFilter)) {
            return true;
        }

        return false;
    }
}
