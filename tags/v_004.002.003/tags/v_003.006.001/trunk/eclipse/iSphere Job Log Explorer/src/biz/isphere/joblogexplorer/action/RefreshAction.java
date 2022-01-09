/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.action;

import org.eclipse.jface.action.Action;

import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.views.JobLogExplorerTab;

public class RefreshAction extends Action {

    public static final String ID = "biz.isphere.joblogexplorer.action.RefreshAction"; //$NON-NLS-1$

    private JobLogExplorerTab tabItem;

    public RefreshAction() {
        super(Messages.Refresh, Action.AS_PUSH_BUTTON);
        setToolTipText(Messages.Refresh);
        setImageDescriptor(ISphereJobLogExplorerPlugin.getDefault().getImageRegistry().getDescriptor(ISphereJobLogExplorerPlugin.IMAGE_REFRESH));
        setId(ID);
    }

    @Override
    public void run() {

        if (tabItem == null) {
            return;
        }

        tabItem.setInput(tabItem.getInput());
    }

    public void setTabItem(JobLogExplorerTab tabItem) {
        this.tabItem = tabItem;
    }
}
