/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.command;

import biz.isphere.core.resourcemanagement.AbstractResourceBoth;

public class RSECommandBoth extends AbstractResourceBoth<RSECommand> {

    private String name;

    public RSECommandBoth(String name, RSECommand workspaceCommand, RSECommand repositoryCommand) {
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
