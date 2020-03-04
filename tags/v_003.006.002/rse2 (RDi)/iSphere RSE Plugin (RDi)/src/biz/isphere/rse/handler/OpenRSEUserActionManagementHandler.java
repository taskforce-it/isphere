/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import biz.isphere.rse.resourcemanagement.useraction.UserActionEntryDialog;

public class OpenRSEUserActionManagementHandler extends AbstractHandler implements IHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {

        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        new UserActionEntryDialog(shell).open();

        return null;
    }
}
