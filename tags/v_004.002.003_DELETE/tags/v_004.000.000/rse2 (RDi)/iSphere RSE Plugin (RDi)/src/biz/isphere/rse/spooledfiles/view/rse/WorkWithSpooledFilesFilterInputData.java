/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles.view.rse;

import org.eclipse.rse.core.filters.ISystemFilterReference;

import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesInputData;
import biz.isphere.rse.connection.ConnectionManager;

public class WorkWithSpooledFilesFilterInputData extends AbstractWorkWithSpooledFilesInputData {

    private static final String INPUT_TYPE = "filter://"; //$NON-NLS-1$

    private ISystemFilterReference filterReference;

    public WorkWithSpooledFilesFilterInputData(ISystemFilterReference filterReference) {
        super(ConnectionManager.getConnectionName(filterReference.getSubSystem().getHost()));

        this.filterReference = filterReference;

        for (String filterString : this.filterReference.getReferencedFilter().getFilterStrings()) {
            addFilterString(filterString);
        }
    }

    @Override
    public String getFilterPoolName() {
        return filterReference.getReferencedFilter().getParentFilterPool().getName();
    }

    @Override
    public String getFilterName() {
        return filterReference.getReferencedFilter().getName();
    }

    @Override
    public boolean isPersistable() {
        return true;
    }

    public String getContentId() {
        return INPUT_TYPE + String.format("%s:%s:%s:%s", getConnectionName(), getFilterReferenceName(), getFilterPoolName(), getFilterName()); //$NON-NLS-1$
    }

    private String getFilterReferenceName() {
        return filterReference.getName();
    }
}
