/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.sessionspart;

import biz.isphere.tn5250j.core.tn5250jpart.ITN5250JPart;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JInfo;

public abstract class CoreSessionsInfo extends TN5250JInfo {

    private String connection;
    private String session;

    public CoreSessionsInfo(ITN5250JPart tn5250jPart) {
        super(tn5250jPart);
        connection = "";
        session = "";
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    @Override
    public String getTN5250JDescription() {
        return connection + "/" + session;
    }

    @Override
    public boolean isTN5250JEqual(TN5250JInfo tn5250jInfo) {
        CoreSessionsInfo sessionsInfo = (CoreSessionsInfo)tn5250jInfo;
        if (connection.equals(sessionsInfo.getConnection()) && session.equals(sessionsInfo.getSession())) {
            return true;
        } else {
            return false;
        }
    }

}
