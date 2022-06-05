/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.viewmanager;

import org.eclipse.jface.action.Action;

/**
 * Action, that is used to change the 'pinned' state of a pinnable view. A
 * pinnable view must implement the {@link IPinnableView} interface and it must
 * register/unregister itself at a {@link IViewManager}.
 * 
 * @author Thomas Raddatz
 */
public class PinViewAction extends Action {

    private IPinnableView view;

    public PinViewAction(IPinnableView view) {
        super("PinView", Action.AS_CHECK_BOX);
        this.view = view;
    }

    @Override
    public void run() {
        view.setPinned(isChecked());
    }
}
