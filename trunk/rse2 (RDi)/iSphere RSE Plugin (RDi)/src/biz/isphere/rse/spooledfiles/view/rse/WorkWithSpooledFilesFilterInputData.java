/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles.view.rse;

import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.filters.ISystemFilterReference;
import org.eclipse.rse.core.subsystems.ISubSystem;

import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesInputData;
import biz.isphere.rse.connection.ConnectionManager;

public class WorkWithSpooledFilesFilterInputData extends AbstractWorkWithSpooledFilesInputData {

    private ISubSystem subSystem;
    private ISystemFilter systemFilter;

    public WorkWithSpooledFilesFilterInputData(ISystemFilterReference filterReference) {
        this.subSystem = filterReference.getSubSystem();
        this.systemFilter = filterReference.getReferencedFilter();
    }

    @Override
    public String getConnectionName() {
        return ConnectionManager.getConnectionName(subSystem.getHost());
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

    public boolean referencesFilter(ISubSystem subSystem, ISystemFilter systemFilter) {

        if (this.subSystem.equals(subSystem) && this.systemFilter.equals(systemFilter)) {
            return true;
        }

        return false;
    }
}
