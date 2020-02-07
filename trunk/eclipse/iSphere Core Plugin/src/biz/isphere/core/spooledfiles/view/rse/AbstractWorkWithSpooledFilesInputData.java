/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view.rse;

import biz.isphere.base.internal.StringHelper;

public abstract class AbstractWorkWithSpooledFilesInputData {

    private transient String contentId;

    public AbstractWorkWithSpooledFilesInputData() {

        this.contentId = null;
    }

    private boolean isSameValue(String value1, String value2) {

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
            contentId = String.format("%s:%s:%s", getConnectionName(), getFilterPoolName(), getFilterName()); //$NON-NLS-1$
        }

        return contentId;
    }

    public boolean isValid() {

        if (StringHelper.isNullOrEmpty(getConnectionName())) {
            return false;
        }

        if (StringHelper.isNullOrEmpty(getFilterName())) {
            return false;
        }

        if (getFilterStrings() == null || getFilterStrings().length == 0) {
            return false;
        }

        for (String filterString : getFilterStrings()) {
            if (StringHelper.isNullOrEmpty(filterString)) {
                return false;
            }
        }

        return true;
    }

    public abstract String getConnectionName();

    public abstract String getFilterPoolName();

    public abstract String getFilterName();

    public abstract String[] getFilterStrings();
}
