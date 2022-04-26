/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal.actions;

import org.eclipse.jface.action.Action;

import biz.isphere.base.ISphereBasePlugin;
import biz.isphere.base.Messages;
import biz.isphere.base.internal.IResizableTableColumnsViewer;

public class ResetColumnSizeAction extends Action {

    private IResizableTableColumnsViewer viewer;

    public ResetColumnSizeAction() {
        this(null);
    }

    public ResetColumnSizeAction(IResizableTableColumnsViewer viewer) {
        super(Messages.Reset_Column_Size, Action.AS_PUSH_BUTTON);
        setToolTipText(Messages.Tooltip_Reset_Column_Size);
        setImageDescriptor(ISphereBasePlugin.getDefault().getImageRegistry().getDescriptor(ISphereBasePlugin.IMAGE_RESET_COLUMN_SIZE));
        setViewer(viewer);
    }

    public void setViewer(IResizableTableColumnsViewer tabItem) {
        this.viewer = tabItem;
    }

    @Override
    public void run() {

        if (viewer == null) {
            return;
        }

        viewer.resetColumnSizes();
    }
}
