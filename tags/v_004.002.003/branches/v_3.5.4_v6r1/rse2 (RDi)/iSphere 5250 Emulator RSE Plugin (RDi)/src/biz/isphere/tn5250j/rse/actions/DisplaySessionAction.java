/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.actions;

import org.eclipse.rse.ui.actions.SystemBaseAction;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.tn5250j.core.session.SessionDetailDialog;
import biz.isphere.tn5250j.rse.DialogActionTypes;
import biz.isphere.tn5250j.rse.Messages;
import biz.isphere.tn5250j.rse.TN5250JRSEPlugin;
import biz.isphere.tn5250j.rse.model.RSESession;

public class DisplaySessionAction extends SystemBaseAction {

    public DisplaySessionAction(Shell parent) {
        super(Messages.Display_session, parent);
        setAvailableOffline(true);
        setImageDescriptor(TN5250JRSEPlugin.getImageDescriptor(TN5250JRSEPlugin.IMAGE_DISPLAY));
    }

    @Override
    public void run() {
        if (getFirstSelection() instanceof RSESession) {
            RSESession rseSession = (RSESession)getFirstSelection();
            if (rseSession != null) {
                new SessionDetailDialog(shell, TN5250JRSEPlugin.getRSESessionDirectory(rseSession.getRSEProfil() + "-"
                    + rseSession.getRSEConnection()), DialogActionTypes.DISPLAY, rseSession.getSession()).open();
            }
        }
    }

}