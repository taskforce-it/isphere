/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.actions;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.tn5250j.core.session.SessionDetailDialog;
import biz.isphere.tn5250j.rse.DialogActionTypes;
import biz.isphere.tn5250j.rse.Messages;
import biz.isphere.tn5250j.rse.TN5250JRSEPlugin;
import biz.isphere.tn5250j.rse.model.RSESession;

import com.ibm.etools.systems.core.ui.actions.SystemBaseAction;

public class ChangeSessionAction extends SystemBaseAction {

    public ChangeSessionAction(Shell parent) {
        super(Messages.Change_session, parent);
        setAvailableOffline(true);
        setImageDescriptor(TN5250JRSEPlugin.getImageDescriptor(TN5250JRSEPlugin.IMAGE_CHANGE));
    }

    @Override
    public void run() {
        if (getFirstSelection() instanceof RSESession) {
            RSESession rseSession = (RSESession)getFirstSelection();
            if (rseSession != null) {
                SessionDetailDialog sessionDetailDialog = new SessionDetailDialog(shell, TN5250JRSEPlugin.getRSESessionDirectory(rseSession
                    .getRSEProfil()
                    + "-" + rseSession.getRSEConnection()), DialogActionTypes.CHANGE, rseSession.getSession());
                if (sessionDetailDialog.open() == Window.OK) {
                    rseSession.update(rseSession.getSubSystem());
                }
            }
        }
    }

}