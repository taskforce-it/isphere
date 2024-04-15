/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization;

import java.io.Serializable;

/**
 * This class is used for storing the compare options when comparing members in
 * the <i>iSphere Synchronize Members</i> editor.
 */
@SuppressWarnings("serial")
public class CompareOptions implements Serializable {

    private boolean isIgnoreDate;
    private String memberFilter;
    private boolean isRegEx;

    public CompareOptions() {
        this(false, "*"); //$NON-NLS-1$
    }

    public CompareOptions(boolean ignoreDate, String memberFilter) {
        this.isIgnoreDate = ignoreDate;
        this.memberFilter = memberFilter;
    }

    public boolean isIgnoreDate() {
        return isIgnoreDate;
    }

    public void setIgnoreDate(boolean isIgnoreDate) {
        this.isIgnoreDate = isIgnoreDate;
    }

    public String getMemberFilter() {
        return memberFilter;
    }

    public void setMemberFilter(String memberFilter) {
        this.memberFilter = memberFilter;
    }

    public void setIsRegEx(boolean isRegEx) {
        this.isRegEx = isRegEx;
    }

    public boolean isRegEx() {
        return isRegEx;
    }
}
