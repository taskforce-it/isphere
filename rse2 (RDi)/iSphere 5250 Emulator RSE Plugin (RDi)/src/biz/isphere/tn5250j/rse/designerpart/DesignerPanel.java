/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.designerpart;

import org.eclipse.swt.widgets.Shell;
import org.tn5250j.Session5250;

import biz.isphere.tn5250j.core.designerpart.CoreDesignerPanel;
import biz.isphere.tn5250j.core.session.Session;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JGUI;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JInfo;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class DesignerPanel extends CoreDesignerPanel {

    private static final long serialVersionUID = 1L;

    public DesignerPanel(TN5250JInfo tn5250jInfo, Session session, Shell shell) {
        super(tn5250jInfo, session, shell);
    }

    @Override
    public TN5250JGUI getTN5250JGUI(TN5250JInfo tn5250jInfo, Session5250 session5250) {
        return new DesignerGUI(tn5250jInfo, session5250);
    }

    @Override
    public String getHost() {
        DesignerInfo designerInfo = (DesignerInfo)getTN5250JInfo();
        IBMiConnection iSeriesConnection = IBMiConnection.getConnection(designerInfo.getRSEProfil(), designerInfo.getRSEConnection());
        if (iSeriesConnection != null) {
            return iSeriesConnection.getHostName();
        }
        return "";
    }

}
