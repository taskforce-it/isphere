/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.command;

import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.Messages;
import biz.isphere.core.resourcemanagement.AbstractResource;

public class CommandEditingAreaWorkspace extends AbstractCommandEditingArea {

    public CommandEditingAreaWorkspace(Composite parent, AbstractResource[] resources, boolean both, boolean singleCompileType) {
        super(parent, resources, both, singleCompileType);
    }

    @Override
    protected String[] getActions(boolean both) {
        return getActionsWorkspace(both);
    }

    public String getTitle() {
        return Messages.Commands + " " + getTitleWorkspace();
    }

}
