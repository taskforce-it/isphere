/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.sessionspart;

import org.tn5250j.Session5250;

import biz.isphere.tn5250j.core.sessionspart.CoreSessionsGUI;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JInfo;

public class SessionsGUI extends CoreSessionsGUI {

    private static final long serialVersionUID = 1L;

    public SessionsGUI(TN5250JInfo tn5250jInfo, Session5250 session5250) {
        super(tn5250jInfo, session5250);
    }

}
