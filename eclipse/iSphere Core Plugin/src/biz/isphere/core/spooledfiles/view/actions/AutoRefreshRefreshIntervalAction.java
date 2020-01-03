/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view.actions;

import java.math.BigDecimal;

import org.eclipse.jface.action.Action;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.spooledfiles.view.IAutoRefreshView;

public class AutoRefreshRefreshIntervalAction extends Action {

    public static final int REFRESH_OFF = -1;

    private IAutoRefreshView view;
    private int seconds;

    public AutoRefreshRefreshIntervalAction(IAutoRefreshView view, int seconds) {
        super("");

        this.view = view;
        this.seconds = seconds;

        if (seconds == REFRESH_OFF) {
            setText(Messages.Auto_refresh_menu_item_stop);
            setToolTipText(Messages.Auto_refresh_menu_item_stop_tooltip);
            setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_AUTO_REFRESH_OFF));
        } else {
            setText(getText(seconds));
            setToolTipText(getTooltipText(seconds));
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

    private String getText(int seconds) {
        if (isMinutes(seconds)) {
            return Messages.bind(Messages.Auto_refresh_menu_item_every_A_B, seconds, Messages.Seconds);
        } else {
            BigDecimal minutes = getMinutes(seconds);
            return Messages.bind(Messages.Auto_refresh_menu_item_every_A_B, minutes, Messages.Minutes);
        }
    }

    private String getTooltipText(int seconds) {
        if (isMinutes(seconds)) {
            return Messages.bind(Messages.Auto_refresh_menu_item_every_A_B_tooltip, seconds, Messages.Seconds);
        } else {
            BigDecimal minutes = getMinutes(seconds);
            return Messages.bind(Messages.Auto_refresh_menu_item_every_A_B_tooltip, minutes, Messages.Minutes);
        }
    }

    private boolean isMinutes(int seconds) {
        return seconds < 60;
    }

    private BigDecimal getMinutes(int seconds2) {
        return new BigDecimal(seconds / (double)60);
    }
}
