/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.sessionsview;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.viewmanager.IPinnableView;
import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.core.internal.viewmanager.PinViewAction;
import biz.isphere.tn5250j.core.TN5250JCorePlugin;
import biz.isphere.tn5250j.core.tn5250jpart.AddMultiSession;
import biz.isphere.tn5250j.core.tn5250jpart.AddSession;
import biz.isphere.tn5250j.core.tn5250jpart.ITN5250JPart;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JInfo;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JPart;
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

    protected void updatePinProperties() {

        getViewManager().clearViewStatus(this);

        pinProperties.clear();

        TN5250JInfo[] tn5250JInfos = getSessionInfos();
        pinProperties.put(NUMBER_OF_SESSIONS, Integer.toString(tn5250JInfos.length));

        if (tn5250JInfos.length == 0) {
            return;
        }

        try {
            int numSessions = 0;
            for (TN5250JInfo tn5250JInfo : tn5250JInfos) {
                numSessions++;
                pinProperties.put(PROFILE_NAME + "_" + numSessions, tn5250JInfo.getRSEProfil());
                pinProperties.put(CONNECTION_NAME + "_" + numSessions, tn5250JInfo.getRSEConnection());
                pinProperties.put(SESSION_NAME + "_" + numSessions, tn5250JInfo.getSession());

                getTabFolderSessions().getItems();

                ITN5250JPart part = tn5250JInfo.getTN5250JPart();
                if (part != null) {
                    if (part instanceof TN5250JPart) {
                        TN5250JPart tn5250JPart = (TN5250JPart)part;
                        tn5250JPart.getSessionInfos();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void restoreViewData() {

        /*
         * The view must be restored from a UI job because otherwise the
         * IViewManager cannot load the IRSEPersistenceManager, because the
         * RSECorePlugin is not loaded (Maybe, because the UI thread is
         * blocked?).
         */
        RestoreViewJob job = new RestoreViewJob();
        job.schedule();
    }

    protected abstract IViewManager getViewManager();

    protected abstract CoreSessionsView showView() throws PartInitException;

    protected abstract Map<String, String> getPinProperties(Set<String> pinKeys);

    protected abstract TN5250JInfo createSessionsInfo(CoreSessionsView sessionsView, String rseProfile, String rseConnection, String sessionName);

    /**
     * Overridden to update the pin properties when the view is closed.
     */
    @Override
    public void dispose() {

        updatePinProperties();
        getViewManager().remove(this);

        super.dispose();
    }

    /**
     * Job, that restores a pinned view.
     */
    private class RestoreViewJob extends UIJob {

        public RestoreViewJob() {
            super(Messages.Restoring_view);
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor monitor) {

            IViewManager viewManager = getViewManager();
            if (!viewManager.isInitialized(5000)) {
                ISpherePlugin.logError("Could not restore view. View manager did not initialize within 5 seconds.", null); //$NON-NLS-1$
                return Status.OK_STATUS;
            }

            Set<String> pinKeys = new HashSet<String>();
            pinKeys.add(NUMBER_OF_SESSIONS);

            pinProperties = getPinProperties(pinKeys);

            int numSession = IntHelper.tryParseInt(pinProperties.get(NUMBER_OF_SESSIONS), 0);
            for (int i = 1; i <= numSession; i++) {

                try {

                    pinKeys.clear();
                    pinKeys.add(PROFILE_NAME + "_" + i);
                    pinKeys.add(CONNECTION_NAME + "_" + i);
                    pinKeys.add(SESSION_NAME + "_" + i);
                    pinProperties = getPinProperties(pinKeys);

                    String rseProfil = pinProperties.get(PROFILE_NAME + "_" + i);
                    String rseConnection = pinProperties.get(CONNECTION_NAME + "_" + i);
                    String sessionName = pinProperties.get(SESSION_NAME + "_" + i);

                    CoreSessionsView sessionsView = showView();

                    TN5250JInfo sessionsInfo = createSessionsInfo(sessionsView, rseProfil, rseConnection, sessionName);

                    int tabIndex = sessionsView.findSessionTab(sessionsInfo);
                    if (tabIndex == -1) {
                        AddSession.run(sessionsInfo);
                    } else {
                        if (sessionsView.isMultiSession()) {
                            AddMultiSession.run(sessionsView, tabIndex);
                        }
                    }

                } catch (Throwable e) {
                    ISpherePlugin.logError("*** Could not restore TN5250J session ***", e);
                }
            }

            CoreSessionsView.this.setPinned(true);

            return Status.OK_STATUS;
        }
    }
}
