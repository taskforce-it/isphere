/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.command;

public class CommandQualifier {

    private boolean singleCompileType;

    public CommandQualifier(boolean singleCompileType) {
        this.singleCompileType = singleCompileType;
    }

    public boolean isSingleCompileType() {
        return singleCompileType;
    }

}
