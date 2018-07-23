/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.filter;

import biz.isphere.core.resourcemanagement.AbstractResourceBoth;

public class RSEFilterBoth extends AbstractResourceBoth<RSEFilter> {

    private String name;

    public RSEFilterBoth(String name, RSEFilter workspaceFilter, RSEFilter repositoryFilter) {
        super(workspaceFilter, repositoryFilter);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }

}
