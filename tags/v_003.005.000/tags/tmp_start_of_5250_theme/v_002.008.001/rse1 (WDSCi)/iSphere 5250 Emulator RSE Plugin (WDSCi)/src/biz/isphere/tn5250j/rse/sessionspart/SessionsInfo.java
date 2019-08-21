/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.sessionspart;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.tn5250j.core.session.Session;
import biz.isphere.tn5250j.core.sessionspart.CoreSessionsInfo;
import biz.isphere.tn5250j.core.tn5250jpart.ITN5250JPart;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JInfo;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JPanel;

public class SessionsInfo extends CoreSessionsInfo {

    private String rseProfil;
    private String rseConnection;

    public SessionsInfo(ITN5250JPart tn5250jPart) {
        super(tn5250jPart);
        rseProfil = "";
        rseConnection = "";
    }

    public String getRSEProfil() {
        return rseProfil;
    }

    public void setRSEProfil(String rseProfil) {
        this.rseProfil = rseProfil;
        setConnection(this.rseProfil + "-" + this.rseConnection);
    }

    public String getRSEConnection() {
        return rseConnection;
    }

    public void setRSEConnection(String rseConnection) {
        this.rseConnection = rseConnection;
        setConnection(this.rseProfil + "-" + this.rseConnection);
    }

    @Override
    public String getTN5250JDescription() {
        return rseConnection + "/" + getSession();
    }

    @Override
    public boolean isTN5250JEqual(TN5250JInfo tn5250jInfo) {
        SessionsInfo sessionsInfo = (SessionsInfo)tn5250jInfo;
        if (rseProfil.equals(sessionsInfo.getRSEProfil()) && rseConnection.equals(sessionsInfo.getRSEConnection())
            && getSession().equals(sessionsInfo.getSession())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public TN5250JPanel getTN5250JPanel(Session session, Shell shell) {
        return new SessionsPanel(this, session, shell);
    }

}
