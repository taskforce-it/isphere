/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import biz.isphere.core.internal.handler.AbstractCommandHandler;
import biz.isphere.rse.resourcemanagement.filter.FilterEntryDialog;

public class OpenRSEFilterManagementHandler extends AbstractCommandHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {

        new FilterEntryDialog(getShell(event)).open();

        return null;
    }

}
