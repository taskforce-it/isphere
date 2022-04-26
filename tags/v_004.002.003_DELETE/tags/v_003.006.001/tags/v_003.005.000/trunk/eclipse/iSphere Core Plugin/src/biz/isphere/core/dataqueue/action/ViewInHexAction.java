/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue.action;

import org.eclipse.jface.action.Action;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogView;

/**
 * Action, that is used to change the 'view mode' of the
 * "Data Queue Monitor View" view. The view mode can be either 'text' or 'hex'.
 * 
 * @author Thomas Raddatz
 */
public class ViewInHexAction extends Action {

    private static final String VIEW_HEX = "*HEX"; //$NON-NLS-1$
    private static final String VIEW_STRING = "*STRING"; //$NON-NLS-1$

    private IDialogView view;

    public ViewInHexAction(IDialogView view) {
        super(Messages.View_in_Hex, Action.AS_CHECK_BOX); //$NON-NLS-1$

        this.view = view;

        setToolTipText(Messages.Tooltip_View_in_Hex);
        setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_VIEW_IN_HEX));
    }

    @Override
    public void run() {
        view.changeDisplayMode();
    }

    public static boolean viewInHexToBoolean(String text) {

        if (VIEW_HEX.equals(text)) {
            return true;
        } else {
            return false;
        }
    }

    public static String viewInHexToString(boolean isViewInHex) {

        if (isViewInHex) {
            return VIEW_HEX;
        } else {
            return VIEW_STRING;
        }
    }
}
