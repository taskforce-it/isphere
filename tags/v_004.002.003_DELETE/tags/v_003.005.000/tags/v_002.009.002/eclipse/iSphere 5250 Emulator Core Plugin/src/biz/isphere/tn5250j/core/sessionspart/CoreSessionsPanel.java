/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.sessionspart;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.tn5250j.core.session.Session;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JInfo;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JPanel;

public abstract class CoreSessionsPanel extends TN5250JPanel {

    private static final long serialVersionUID = 1L;

    public CoreSessionsPanel(TN5250JInfo tn5250jInfo, Session session, Shell shell) {
        super(tn5250jInfo, session, shell);
    }

    @Override
    public String getHost() {
        return "";
    }

}
