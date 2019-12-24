/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view.rse;

import biz.isphere.base.internal.StringHelper;

public class WorkWithSpooledFilesInputData {

    private String connectionName;
    private String filterName;
    private String[] filterStrings;

    private transient String contentId;

    public WorkWithSpooledFilesInputData(String connectionName, String filterName) {

        this.connectionName = connectionName;
        this.filterName = filterName;
        this.contentId = null;
    }

    public String getContentId() {

        if (contentId == null) {
            contentId = connectionName + ":" + filterName; //$NON-NLS-1$
        }

        return contentId;
    }

    public String getConnectionName() {
        return connectionName;
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
