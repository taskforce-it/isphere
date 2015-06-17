/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
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
import org.eclipse.search.ui.NewSearchUI;

import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.sourcefilesearch.SourceFileSearchPage;

public class OpenSourceFileSearchPageHandler extends AbstractHandler implements IHandler {

    public static final String ID = "biz.isphere.rse.handler.OpenSourceFileSearchPageHandler";

    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {

        NewSearchUI.openSearchDialog(ISphereRSEPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow(), SourceFileSearchPage.ID);

        return null;
    }

}
