/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue.action;

import org.eclipse.jface.action.Action;

import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogView;

/**
 * Action, that is used to change the 'view mode' of the
 * "Data Queue Monitor View" view. The view mode can be either 'text' or 'hex'.
 * 
 * @author Thomas Raddatz
 */
public class DisplayEndOfDataAction extends Action {

    private IDialogView view;

    public DisplayEndOfDataAction(IDialogView view) {
        super(Messages.Display_End_Of_Data, Action.AS_CHECK_BOX); //$NON-NLS-1$

        this.view = view;

        setToolTipText(Messages.Tooltip_Display_End_Of_Data);
    }

    @Override
    public void run() {
        view.changeDisplayMode();
    }
}
