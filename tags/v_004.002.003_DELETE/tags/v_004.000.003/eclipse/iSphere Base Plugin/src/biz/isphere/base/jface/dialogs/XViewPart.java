/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.jface.dialogs;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

public abstract class XViewPart extends ViewPart {

    private IHandlerActivation cmdRefreshHandlerActivation;

    public void refresh() {
    }

    protected boolean isCmdRefreshEnabled() {
        return false;
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);

        if (isCmdRefreshEnabled()) {
            IHandlerService handlerService = (IHandlerService)getSite().getService(IHandlerService.class);
            cmdRefreshHandlerActivation = handlerService.activateHandler("org.eclipse.ui.file.refresh", new LocalCmdRefreshHandler(this));
        }
    }

    @Override
    public void dispose() {

        if (cmdRefreshHandlerActivation != null) {
            IHandlerService handlerService = (IHandlerService)getSite().getService(IHandlerService.class);
            handlerService.deactivateHandler(cmdRefreshHandlerActivation);
        }

        super.dispose();
    }

    private class LocalCmdRefreshHandler extends AbstractHandler implements IHandler {

        private XViewPart view;

        public LocalCmdRefreshHandler(XViewPart view) {
            this.view = view;
        }

        public Object execute(ExecutionEvent event) throws ExecutionException {
            view.refresh();
            return null;
        }

    }

}
