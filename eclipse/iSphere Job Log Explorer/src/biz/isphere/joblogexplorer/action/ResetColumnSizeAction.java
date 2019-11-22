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

public class ResetColumnSizeAction extends Action {

    public static final String ID = "biz.isphere.joblogexplorer.action.ResetColumnSizeAction"; //$NON-NLS-1$

    private JobLogExplorerTab tabItem;

    public ResetColumnSizeAction() {
        super(Messages.Reset_Column_Size, Action.AS_PUSH_BUTTON);
        setToolTipText(Messages.Reset_Column_Size);
        setImageDescriptor(ISphereJobLogExplorerPlugin.getDefault().getImageRegistry()
            .getDescriptor(ISphereJobLogExplorerPlugin.IMAGE_RESET_COLUMN_SIZE));
        setId(ID);
    }

    public void setTabItem(JobLogExplorerTab tabItem) {
        this.tabItem = tabItem;
    }

    @Override
    public void run() {

        if (tabItem == null) {
            return;
        }

        tabItem.resetColumnSize();
    }
}
