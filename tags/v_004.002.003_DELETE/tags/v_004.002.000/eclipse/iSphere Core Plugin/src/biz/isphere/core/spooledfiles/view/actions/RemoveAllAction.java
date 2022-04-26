/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view.actions;

import org.eclipse.jface.action.Action;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;

public class RemoveAllAction extends Action {

    public RemoveAllAction() {
        setToolTipText(Messages.Tooltip_Remove_all);
        setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_REMOVE_ALL));
        setEnabled(false);
    }
}
