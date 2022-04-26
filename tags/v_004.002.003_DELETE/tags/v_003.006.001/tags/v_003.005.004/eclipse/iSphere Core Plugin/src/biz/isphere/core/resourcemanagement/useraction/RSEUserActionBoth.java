/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.useraction;

import biz.isphere.core.resourcemanagement.AbstractResourceBoth;

public class RSEUserActionBoth extends AbstractResourceBoth<RSEUserAction> {

    private String name;

    public RSEUserActionBoth(String name, RSEUserAction workspaceCommand, RSEUserAction repositoryCommand) {
        super(workspaceCommand, repositoryCommand);
        this.name = name;
    }

    public String getLabel() {
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
