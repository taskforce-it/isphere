/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles.view;

import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesView;
import biz.isphere.rse.ISphereRSEPlugin;

public class WorkWithSpooledFilesView extends AbstractWorkWithSpooledFilesView {

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

    }

    protected IViewManager getViewManager() {
        return ISphereRSEPlugin.getDefault().getViewManager(IViewManager.SPOOLED_FILES_VIEWS);
    }
}
