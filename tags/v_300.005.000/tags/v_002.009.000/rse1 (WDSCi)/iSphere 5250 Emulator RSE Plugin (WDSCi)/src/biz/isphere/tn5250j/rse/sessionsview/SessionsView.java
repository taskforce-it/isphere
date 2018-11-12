/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.sessionsview;

import java.util.Map;
import java.util.Set;

import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.tn5250j.core.session.ISession;
import biz.isphere.tn5250j.core.sessionsview.CoreSessionsView;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JInfo;
import biz.isphere.tn5250j.rse.sessionspart.SessionsInfo;

public class SessionsView extends CoreSessionsView {

    public static final String ID = ISession.SESSION_VIEW_ID;

    public SessionsView() {
        super();

        getViewManager().add(this);
    }

    protected IViewManager getViewManager() {
        return ISphereRSEPlugin.getDefault().getViewManager(IViewManager.TN5250J_SESSION_VIEWS);
    }

    @Override
    protected Map<String, String> getPinProperties(Set<String> pinKeys) {
        return getViewManager().getPinProperties(SessionsView.this, pinKeys);
    }

    @Override
    protected TN5250JInfo createSessionsInfo(CoreSessionsView sessionsView, String rseProfile, String rseConnection, String sessionName) {

        SessionsInfo sessionsInfo = new SessionsInfo(sessionsView);
        sessionsInfo.setRSEProfil(rseProfile);
        sessionsInfo.setRSEConnection(rseConnection);
        sessionsInfo.setSession(sessionName);

        return sessionsInfo;
    }

}