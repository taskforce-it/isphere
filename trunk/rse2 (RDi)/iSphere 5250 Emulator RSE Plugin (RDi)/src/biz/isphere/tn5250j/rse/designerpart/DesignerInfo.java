/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.designerpart;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.tn5250j.core.designerpart.CoreDesignerInfo;
import biz.isphere.tn5250j.core.session.Session;
import biz.isphere.tn5250j.core.tn5250jpart.ITN5250JPart;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JPanel;
import biz.isphere.tn5250j.rse.TN5250JRSEPlugin;

public class DesignerInfo extends CoreDesignerInfo {

    private String rseProfil;
    private String rseConnection;

    public DesignerInfo(ITN5250JPart tn5250jPart) {
        super(tn5250jPart);
        rseProfil = "";
        rseConnection = "";
    }

    public String getRSEProfil() {
        return rseProfil;
    }

    public void setRSEProfil(String rseProfil) {
        this.rseProfil = rseProfil;
    }

    public String getRSEConnection() {
        return rseConnection;
    }

    public void setRSEConnection(String rseConnection) {
        this.rseConnection = rseConnection;
    }

    @Override
    public String getRSESessionDirectory() {
        return TN5250JRSEPlugin.getRSESessionDirectory(getQualifiedConnection());
    }

    @Override
    public TN5250JPanel getTN5250JPanel(Session session, Shell shell) {
        return new DesignerPanel(this, session, shell);
    }

}
