/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view.rse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.MenuManager;

import biz.isphere.core.Messages;
import biz.isphere.core.spooledfiles.view.IAutoRefreshView;
import biz.isphere.core.spooledfiles.view.actions.AutoRefreshRefreshIntervalAction;

public class AutoRefreshSubMenu extends MenuManager {

    private AutoRefreshRefreshIntervalAction disableAutoRefreshAction;
    private List<AutoRefreshRefreshIntervalAction> refreshIntervalActions;

    /**
     * Constructs a new Auto-Refresh sub menu.
     * 
     * @param view - view that is notified when an intervall action has been
     *        selected.
     * @param intervall - refresh inrervall in seconds
     */
    public AutoRefreshSubMenu(IAutoRefreshView view, int... intervall) {
        super(Messages.Auto_refresh_menu_item);

        createActions(view, intervall);
    }

    public void setEnabled(boolean enabled) {
        setEnabled(enabled, -1);
    }

    public void setEnabled(int autoRefreshIntervall) {
        setEnabled(true, autoRefreshIntervall);
    }

    private void setEnabled(boolean enabled, int autoRefreshIntervall) {

        if (!enabled) {
            disableAutoRefreshAction.setEnabled(false);
            for (AutoRefreshRefreshIntervalAction refreshAction : refreshIntervalActions) {
                refreshAction.setEnabled(false);
            }
        } else {
            if (autoRefreshIntervall == AutoRefreshRefreshIntervalAction.REFRESH_OFF) {
                disableAutoRefreshAction.setEnabled(false);
            } else {
                disableAutoRefreshAction.setEnabled(true);
            }
            for (AutoRefreshRefreshIntervalAction refreshAction : refreshIntervalActions) {
                if (autoRefreshIntervall == refreshAction.getInterval()) {
                    refreshAction.setEnabled(false);
                } else {
                    refreshAction.setEnabled(true);
                }
            }
        }

    }

    private void createActions(IAutoRefreshView view, int[] intervall) {

        refreshIntervalActions = new ArrayList<AutoRefreshRefreshIntervalAction>();

        disableAutoRefreshAction = new AutoRefreshRefreshIntervalAction(view, -1);
        add(disableAutoRefreshAction);

        for (int i = 0; i < intervall.length; i++) {
            AutoRefreshRefreshIntervalAction action = new AutoRefreshRefreshIntervalAction(view, intervall[i]);
            refreshIntervalActions.add(action);
            add(action);
        }
    }
}
