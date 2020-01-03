/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view.rse;

import biz.isphere.base.internal.StringHelper;

public class WorkWithSpooledFilesInputData {

    private String connectionName;
    private String filterPoolName;
    private String filterName;
    private String[] filterStrings;

    private transient String contentId;

    public WorkWithSpooledFilesInputData(String connectionName, String filterPoolName, String filterName) {

        this.connectionName = connectionName;
        this.filterPoolName = filterPoolName;
        this.filterName = filterName;
        this.contentId = null;
    }

    public boolean referencesFilter(String connectionName, String filterPoolName, String filterName) {

        if (equals(connectionName, getConnectionName()) && equals(filterPoolName, getFilterPoolName()) && equals(filterName, getFilterName())) {
            return true;
        }

        return false;
    }

    private boolean equals(String value1, String value2) {

        if (value1 != null) {
            return value1.equals(value2);
        } else if (value2 != null) {
            return value2.equals(value1);
        } else {
            return true;
        }
    }

    public String getContentId() {

        if (contentId == null) {
            contentId = String.format("%s:%s:%s", connectionName, filterPoolName, filterName); //$NON-NLS-1$
        }

        return contentId;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getFilterPoolName() {
        return filterPoolName;
    }

    public String getFilterName() {
        return filterName;
    }

    public String[] getFilterStrings() {
        return filterStrings;
    }

    public void setFilterStrings(String[] filterStrings) {
        this.filterStrings = filterStrings;
    }

    public boolean isValid() {

        if (StringHelper.isNullOrEmpty(connectionName)) {
            return false;
        }

        if (StringHelper.isNullOrEmpty(filterName)) {
            return false;
        }

        if (filterStrings == null || filterStrings.length == 0) {
            return false;
        }

        for (String filterString : filterStrings) {
            if (StringHelper.isNullOrEmpty(filterString)) {
                return false;
            }
        }

        return true;
    }
}
