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

public class EditSqlAction extends Action {

    public static final String ID = "biz.isphere.joblogexplorer.action.EditSqlAction"; //$NON-NLS-1$

    private JobLogExplorerTab tabItem;

    public EditSqlAction() {
        super(Messages.Edit_SQL, Action.AS_CHECK_BOX);
        setToolTipText(Messages.Edit_SQL);
        setImageDescriptor(ISphereJobLogExplorerPlugin.getDefault().getImageDescriptor(ISphereJobLogExplorerPlugin.IMAGE_EDIT_SQL));
        setId(ID);
    }

    @Override
    public void run() {
        tabItem.setSqlEditorVisibility(isChecked());
    }

    public void setTabItem(JobLogExplorerTab tabItem) {
        this.tabItem = tabItem;
    }
}
