/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.actions;

import java.io.File;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.tn5250j.core.session.ISession;
import biz.isphere.tn5250j.core.session.Session;
import biz.isphere.tn5250j.core.session.SessionDetailDialog;
import biz.isphere.tn5250j.rse.DialogActionTypes;
import biz.isphere.tn5250j.rse.Messages;
import biz.isphere.tn5250j.rse.TN5250JRSEPlugin;
import biz.isphere.tn5250j.rse.model.RSESession;

import com.ibm.etools.systems.core.ui.actions.SystemBaseAction;
import com.ibm.etools.systems.subsystems.SubSystem;

public class NewDesignerSessionAction extends SystemBaseAction {

    private String profil;
    private String connection;

    public NewDesignerSessionAction(String profil, String connection, Shell parent) {
        super(Messages.New_designer_session, parent);
        this.profil = profil;
        this.connection = connection;
        setAvailableOffline(true);
        setImageDescriptor(TN5250JRSEPlugin.getImageDescriptor(TN5250JRSEPlugin.IMAGE_DESIGNER));
    }

    @Override
    public void run() {
        if (getFirstSelection() instanceof SubSystem) {
            SubSystem subSystem = (SubSystem)getFirstSelection();
            if (subSystem != null) {
                Session session = new Session(TN5250JRSEPlugin.getRSESessionDirectory(profil + "-" + connection));
                session.setConnection(profil + "-" + connection);
                session.setName(ISession.DESIGNER);
                session.setProgram("DESIGNERW");
                session.setLibrary("%ISPHERE%");
                SessionDetailDialog sessionDetailDialog = new SessionDetailDialog(shell, TN5250JRSEPlugin.getRSESessionDirectory(profil + "-"
                    + connection), DialogActionTypes.CREATE, session);
                if (sessionDetailDialog.open() == Window.OK) {
                    RSESession rseSession = new RSESession(subSystem, session.getName(), session);
                    rseSession.create(subSystem);
                }
            }
        }
    }

    @Override
    public boolean isEnabled() {
        String designer = TN5250JRSEPlugin.getRSESessionDirectory(profil + "-" + connection) + File.separator + ISession.DESIGNER;
        File fileDesigner = new File(designer);
        if (fileDesigner.exists()) {
            return false;
        } else {
            return true;
        }
    }

}
