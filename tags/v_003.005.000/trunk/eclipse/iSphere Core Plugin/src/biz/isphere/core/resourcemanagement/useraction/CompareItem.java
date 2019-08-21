/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.useraction;

public class CompareItem {

    private String property;
    private String repositoryValue;
    private String workspaceValue;

    public CompareItem(String property, boolean repositoryValue, boolean workspaceValue) {
        this(property, Boolean.toString(repositoryValue), Boolean.toString(workspaceValue));
    }

    public CompareItem(String property, String[] repositoryValue, String[] workspaceValue) {
        this(property, arrayToString(repositoryValue), arrayToString(workspaceValue));
    }

    public CompareItem(String property, String repositoryValue, String workspaceValue) {
        this.property = property;
        this.repositoryValue = repositoryValue;
        this.workspaceValue = workspaceValue;
    }

    public String getProperty() {
        return property;
    }

    public String getRepositoryValue() {
        return repositoryValue;
    }

    public String getWorkspaceValue() {
        return workspaceValue;
    }

    public boolean isEqual() {

        if (repositoryValue.equals(workspaceValue)) {
            return true;
        }

        return false;
    }

    private static String arrayToString(String[] values) {

        StringBuilder buffer = new StringBuilder();

        for (String value : values) {
            if (buffer.length() > 0) {
                buffer.append(", "); //$NON-NLS-1$
            }
            buffer.append(value);
        }

        return buffer.toString();
    }
}
