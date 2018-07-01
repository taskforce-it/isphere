/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.tn5250jpart;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import biz.isphere.tn5250j.core.Messages;
import biz.isphere.tn5250j.core.session.Session;

/**
 * First this class creates a new session tab and adds it to an
 * "iSphere 5250 Sessions" view. Then it adds a new multi-session to that tab.
 */
public class AddSessionTab {

    public static void run(TN5250JInfo tn5250jInfo) {

        ITN5250JPart tn5250jPart = tn5250jInfo.getTN5250JPart();

        String sessionDirectory = tn5250jInfo.getRSESessionDirectory();
        String qualifiedConnection = tn5250jInfo.getQualifiedConnection();
        String sessionName = tn5250jInfo.getSession();

        if (!Session.exists(sessionDirectory, qualifiedConnection, sessionName)) {
            String message = Messages.bind(Messages.Session_configuration_file_A_not_found_in_directory_colon_B, new String[] { sessionName,
                sessionDirectory });
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.E_R_R_O_R, message);
            return;
        }

        Session session = Session.load(sessionDirectory, qualifiedConnection, sessionName);
        if (session != null) {

            ArrayList<Composite> arrayListCompositeSession = new ArrayList<Composite>();
            ArrayList<TN5250JPanel> arrayListTabItemTN5250J = new ArrayList<TN5250JPanel>();

            CTabItem tabItemSession = new CTabItem(tn5250jPart.getTabFolderSessions(), SWT.NONE);
            tabItemSession.setText(tn5250jInfo.getTN5250JDescription());
            tabItemSession.setData(SessionTabData.CONNECTION, tn5250jInfo.getQualifiedConnection());
            tabItemSession.setData(SessionTabData.NAME, tn5250jInfo.getSession());
            tabItemSession.setData(SessionTabData.TN5250J_INFO, tn5250jInfo);
            tabItemSession.setData(SessionTabData.COMPOSITE_SESSION, arrayListCompositeSession);
            tabItemSession.setData(SessionTabData.TAB_ITEM_TN5250J, arrayListTabItemTN5250J);
            tabItemSession.setData(SessionTabData.SESSION, session);
            tabItemSession.setData(SessionTabData.LAST_FOCUS, new Integer(0));

            tn5250jPart.getTabFolderSessions().setSelection(tabItemSession);

            AddMultiSession.run(tn5250jPart);

        }

    }

}
