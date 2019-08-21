/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.actions;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.rse.ui.actions.SystemBaseAction;
import org.eclipse.swt.widgets.Shell;
import org.tn5250j.Session5250;
import org.tn5250j.framework.common.SessionManager;
import org.tn5250j.framework.common.Sessions;

import biz.isphere.tn5250j.core.session.SessionDetailDialog;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JInfo;
import biz.isphere.tn5250j.rse.DialogActionTypes;
import biz.isphere.tn5250j.rse.Messages;
import biz.isphere.tn5250j.rse.TN5250JRSEPlugin;
import biz.isphere.tn5250j.rse.model.RSESession;
import biz.isphere.tn5250j.rse.sessionspart.SessionsGUI;
import biz.isphere.tn5250j.rse.sessionspart.SessionsInfo;

public class DeleteSessionAction extends SystemBaseAction {

    public DeleteSessionAction(Shell parent) {
        super(Messages.Delete_session, parent);
        setAvailableOffline(true);
        setImageDescriptor(TN5250JRSEPlugin.getImageDescriptor(TN5250JRSEPlugin.IMAGE_DELETE));
    }

    @Override
    public void run() {
        if (getFirstSelection() instanceof RSESession) {
            RSESession rseSession = (RSESession)getFirstSelection();
            if (rseSession != null) {
                SessionDetailDialog sessionDetailDialog = new SessionDetailDialog(shell, TN5250JRSEPlugin.getRSESessionDirectory(rseSession
                    .getRSEProfil() + "-" + rseSession.getRSEConnection()), DialogActionTypes.DELETE, rseSession.getSession());
                if (sessionDetailDialog.open() == Dialog.OK) {
                    rseSession.delete(rseSession.getSubSystem());
                }
            }
        }
    }

    @Override
    public boolean isEnabled() {

        if (!super.isEnabled()) {
            return false;
        }

        RSESession rseSession = getRSESession();
        if (rseSession == null) {
            return false;
        }

        Sessions sessions = SessionManager.instance().getSessions();
        int count = sessions.getCount();
        for (int i = 0; i < count; i++) {
            Session5250 session5250 = sessions.item(i);

            if (session5250.getGUI() instanceof SessionsGUI) {
                SessionsGUI sessionsGUI = (SessionsGUI)session5250.getGUI();
                TN5250JInfo tn5250JInfo = sessionsGUI.getTN5250JInfo();
                if (tn5250JInfo instanceof SessionsInfo) {
                    SessionsInfo sessionsInfo = (SessionsInfo)tn5250JInfo;
                    if (rseSession.getRSEProfil().equalsIgnoreCase(sessionsInfo.getRSEProfil())
                        && rseSession.getRSEConnection().equalsIgnoreCase(sessionsInfo.getRSEConnection())
                        && rseSession.getName().equalsIgnoreCase(sessionsInfo.getSession())) {
                        if (session5250.isConnected()) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    private RSESession getRSESession() {

        if (getFirstSelection() instanceof RSESession) {
            RSESession rseSession = (RSESession)getFirstSelection();
            return rseSession;
        }

        return null;
    }

}