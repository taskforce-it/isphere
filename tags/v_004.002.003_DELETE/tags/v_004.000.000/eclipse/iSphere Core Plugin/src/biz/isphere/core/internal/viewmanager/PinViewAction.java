/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.viewmanager;

import org.eclipse.jface.action.Action;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;

/**
 * Action, that is used to change the 'pinned' state of a pinable view. A
 * pinable view must implement the {@link IPinableView} interface and it must
 * register/unregister itself at a {@link IViewManager}.
 * 
 * @author Thomas Raddatz
 */
public class PinViewAction extends Action {

    private IPinableView view;

    public PinViewAction(IPinableView view) {
        super(Messages.Pin_View, Action.AS_CHECK_BOX);

        this.view = view;

        setToolTipText(Messages.Tooltip_Pin_View);
        setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_PIN));
    }

    @Override
    public void run() {
        view.setPinned(isChecked());
    }
}
