/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspacemonitor.action;

import org.eclipse.jface.action.Action;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogView;

public class RefreshViewIntervalAction extends Action {

    public static final int REFRESH_OFF = -1;

    private IDialogView view;
    private int seconds;

    public RefreshViewIntervalAction(IDialogView view, int seconds) {
        super("");

        this.view = view;
        this.seconds = seconds;

        if (seconds == REFRESH_OFF) {
            setText(Messages.Auto_refresh_menu_item_stop);
            setToolTipText(Messages.Auto_refresh_menu_item_stop_tooltip);
            setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_AUTO_REFRESH_OFF));
        } else {
            setText(Messages.bind(Messages.Auto_refresh_menu_item_every_A_B, seconds, Messages.Seconds));
            setToolTipText(Messages.bind(Messages.Auto_refresh_menu_item_every_A_B_tooltip, seconds, Messages.Seconds));
        }

        setEnabled(false);
    }

    public int getInterval() {
        return seconds;
    }

    @Override
    public void run() {
        view.setRefreshInterval(seconds);
    }
}
