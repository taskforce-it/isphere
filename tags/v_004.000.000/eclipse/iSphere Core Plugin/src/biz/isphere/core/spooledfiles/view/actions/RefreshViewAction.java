/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view.actions;

import org.eclipse.jface.action.Action;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesView;

public class RefreshViewAction extends Action {

    private AbstractWorkWithSpooledFilesView view;

    public RefreshViewAction(AbstractWorkWithSpooledFilesView view) {
        super(Messages.Refresh);
        this.view = view;

        setToolTipText(Messages.Refresh_the_contents_of_this_view);
        setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_REFRESH));
        setEnabled(false);
    }

    @Override
    public void run() {
        view.refreshData();
    }
}
