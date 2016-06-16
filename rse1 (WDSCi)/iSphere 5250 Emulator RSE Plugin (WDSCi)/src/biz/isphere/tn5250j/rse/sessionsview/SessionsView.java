/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.sessionsview;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.tn5250j.core.sessionsview.CoreSessionsView;
import biz.isphere.tn5250j.core.tn5250jpart.AddMultiSession;
import biz.isphere.tn5250j.core.tn5250jpart.AddSession;
import biz.isphere.tn5250j.core.tn5250jpart.ITN5250JPart;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JInfo;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JPart;
import biz.isphere.tn5250j.rse.TN5250JRSEPlugin;
import biz.isphere.tn5250j.rse.sessionspart.SessionsInfo;

public class SessionsView extends CoreSessionsView {

    public static final String ID = "biz.isphere.tn5250j.rse.sessionsview.SessionsView"; //$NON-NLS-1$

    public SessionsView() {
        super();

        getViewManager().add(this);
    }

    protected IViewManager getViewManager() {
        return ISphereRSEPlugin.getDefault().getViewManager(IViewManager.TN5250J_SESSION_VIEWS);
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
                if (tn5250JInfo instanceof SessionsInfo) {
                    numSessions++;
                    // SessionsInfo sessionInfo = (SessionsInfo)tn5250JInfo;
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

            pinProperties = viewManager.getPinProperties(SessionsView.this, pinKeys);

            int numSession = IntHelper.tryParseInt(pinProperties.get(NUMBER_OF_SESSIONS), 0);
            for (int i = 1; i <= numSession; i++) {

                try {

                    pinKeys.clear();
                    pinKeys.add(PROFILE_NAME + "_" + i);
                    pinKeys.add(CONNECTION_NAME + "_" + i);
                    pinKeys.add(SESSION_NAME + "_" + i);
                    pinProperties = viewManager.getPinProperties(SessionsView.this, pinKeys);

                    String rseProfil = pinProperties.get(PROFILE_NAME + "_" + i);
                    String rseConnection = pinProperties.get(CONNECTION_NAME + "_" + i);
                    String sessionName = pinProperties.get(SESSION_NAME + "_" + i);

                    SessionsView sessionsView = (SessionsView)(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                        .showView(SessionsView.ID));

                    SessionsInfo sessionsInfo = new SessionsInfo(sessionsView);
                    sessionsInfo.setRSEProfil(rseProfil);
                    sessionsInfo.setRSEConnection(rseConnection);
                    sessionsInfo.setSession(sessionName);

                    if (sessionsView.findSessionTab(rseProfil + "-" + rseConnection, sessionName, sessionsInfo) == -1) {
                        AddSession.run(TN5250JRSEPlugin.getRSESessionDirectory(rseProfil + "-" + rseConnection), rseProfil + "-" + rseConnection,
                            sessionName, sessionsInfo);
                    } else {
                        if (sessionsView.isMultiSession()) {
                            AddMultiSession.run(sessionsView);
                        }
                    }

                } catch (Throwable e) {
                    ISpherePlugin.logError("*** Could not restore TN5250J session ***", e);
                }
            }

            SessionsView.this.setPinned(true);

            return Status.OK_STATUS;
        }
    }

}
