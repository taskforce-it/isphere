/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.tn5250jpart;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.tn5250j.core.session.Session;

public abstract class TN5250JInfo {

    private ITN5250JPart tn5250jPart;

    public TN5250JInfo(ITN5250JPart tn5250jPart) {
        this.tn5250jPart = tn5250jPart;
    }

    public boolean isTN5250JEqual(TN5250JInfo tn5250jInfo) {
        if (getRSEProfil().equals(tn5250jInfo.getRSEProfil()) && getRSEConnection().equals(tn5250jInfo.getRSEConnection())
            && getSession().equals(tn5250jInfo.getSession())) {
            return true;
        } else {
            return false;
        }
    }

    public ITN5250JPart getTN5250JPart() {
        return tn5250jPart;
    }

    public String getQualifiedConnection() {
        return getRSEProfil() + "-" + getRSEConnection();
    }

    public abstract String getSession();

    public abstract String getTN5250JDescription();

    public abstract TN5250JPanel getTN5250JPanel(Session session, Shell shell);

    public abstract String getRSEProfil();

    public abstract String getRSEConnection();

    public abstract String getRSESessionDirectory();

    @Override
    public String toString() {
        return getRSEProfil() + "-" + getTN5250JDescription();
    }

}
