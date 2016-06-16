/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.sessionsview;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;

import biz.isphere.core.internal.viewmanager.IPinnableView;
import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.core.internal.viewmanager.PinViewAction;
import biz.isphere.tn5250j.core.TN5250JCorePlugin;
import biz.isphere.tn5250j.core.tn5250jview.TN5250JView;

public abstract class CoreSessionsView extends TN5250JView implements IPinnableView {

    protected static final String PROFILE_NAME = "profileName"; //$NON-NLS-1$
    protected static final String CONNECTION_NAME = "connectionName"; //$NON-NLS-1$
    protected static final String SESSION_NAME = "sessionName"; //$NON-NLS-1$
    protected static final String NUMBER_OF_SESSIONS = "numberOfSessions"; //$NON-NLS-1$

    protected PinViewAction pinViewAction;
    protected Map<String, String> pinProperties;

    public CoreSessionsView() {
        super();

        pinProperties = new HashMap<String, String>();
    }

    @Override
    public boolean isMultiSession() {
        return TN5250JCorePlugin.getDefault().getPreferenceStore().getBoolean("BIZ.ISPHERE.TN5250J.AREA.MULTI_SESSIONS_ENABLED");
    }

    public boolean isPinned() {
        return pinViewAction.isChecked();
    }

    public String getContentId() {
        return null;
    }

    public Map<String, String> getPinProperties() {
        return pinProperties;
    }

    public void setPinned(boolean pinned) {
        pinViewAction.setChecked(pinned);
        updatePinProperties();
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        createActions();
        initializeToolBar();
        initializeViewMenu();

        if (!getViewManager().isPinned(this)) {
            // createDataQueueEditor(tableViewerArea, null, null);
        } else {
            if (!getViewManager().isLoadingView()) {
                restoreViewData();
            }
        }
    }

    protected void createActions() {

        pinViewAction = new PinViewAction(this);

        refreshActionsEnablement();
    }

    protected void initializeToolBar() {

        IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
        toolbarManager.add(pinViewAction);
    }

    protected void initializeViewMenu() {

        IActionBars actionBars = getViewSite().getActionBars();
        IMenuManager viewMenu = actionBars.getMenuManager();
    }

    private void refreshActionsEnablement() {

        pinViewAction.setEnabled(true);
    }

    protected abstract IViewManager getViewManager();

    protected abstract void restoreViewData();

    protected abstract void updatePinProperties();

    /**
     * Overridden to update the pin properties when the view is closed.
     */
    @Override
    public void dispose() {

        updatePinProperties();
        getViewManager().remove(this);

        super.dispose();
    }
}
