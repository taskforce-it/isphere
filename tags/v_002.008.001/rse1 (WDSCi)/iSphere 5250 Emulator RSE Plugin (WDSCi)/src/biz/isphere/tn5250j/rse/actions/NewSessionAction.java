/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.actions;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.tn5250j.rse.DialogActionTypes;
import biz.isphere.tn5250j.rse.Messages;
import biz.isphere.tn5250j.rse.TN5250JRSEPlugin;
import biz.isphere.tn5250j.rse.model.RSESession;

import com.ibm.etools.systems.core.ui.actions.SystemBaseAction;
import com.ibm.etools.systems.subsystems.SubSystem;

import biz.isphere.tn5250j.core.session.Session;
import biz.isphere.tn5250j.core.session.SessionDetailDialog;

public class NewSessionAction extends SystemBaseAction {

    public NewSessionAction(Shell parent) {
        super(Messages.getString("New_session"), parent);
        setAvailableOffline(true);
        setImageDescriptor(TN5250JRSEPlugin.getImageDescriptor(TN5250JRSEPlugin.IMAGE_NEW));
    }

    @Override
    public void run() {
        if (getFirstSelection() instanceof SubSystem) {
            SubSystem subSystem = (SubSystem)getFirstSelection();
            if (subSystem != null) {
                Session session = new Session(TN5250JRSEPlugin.getRSESessionDirectory(subSystem.getSystemProfileName() + "-"
                    + subSystem.getSystemConnectionName()));
                session.setConnection(subSystem.getSystemProfileName() + "-" + subSystem.getSystemConnectionName());
                SessionDetailDialog sessionDetailDialog = new SessionDetailDialog(shell, TN5250JRSEPlugin.getRSESessionDirectory(subSystem
                    .getSystemProfileName()
                    + "-" + subSystem.getSystemConnectionName()), DialogActionTypes.CREATE, session);
                if (sessionDetailDialog.open() == Window.OK) {
                    RSESession rseSession = new RSESession(subSystem, session.getName(), session);
                    rseSession.create(subSystem);
                }
            }
        }
    }

}
