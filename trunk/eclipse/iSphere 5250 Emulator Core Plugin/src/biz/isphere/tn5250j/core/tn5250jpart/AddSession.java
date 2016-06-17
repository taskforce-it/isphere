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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import biz.isphere.tn5250j.core.Messages;
import biz.isphere.tn5250j.core.session.Session;

public class AddSession {

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
            Composite compositeControl = new Composite(tn5250jPart.getTabFolderSessions(), SWT.NONE);
            GridLayout gridLayoutControl = new GridLayout();
            gridLayoutControl.numColumns = 1;
            compositeControl.setLayout(gridLayoutControl);

            ArrayList<Composite> arrayListCompositeSession = new ArrayList<Composite>();
            ArrayList<TN5250JPanel> arrayListTabItemTN5250J = new ArrayList<TN5250JPanel>();

            CreateSession createSession = new CreateSession();
            final TN5250JPanel tn5250j = createSession
                .run(compositeControl, arrayListCompositeSession, arrayListTabItemTN5250J, session, tn5250jInfo);

            CTabItem tabItemSession = new CTabItem(tn5250jPart.getTabFolderSessions(), SWT.NONE);
            tabItemSession.setText(tn5250jInfo.getTN5250JDescription());
            tabItemSession.setData("Connection", tn5250jInfo.getQualifiedConnection());
            tabItemSession.setData("Name", tn5250jInfo.getSession());
            tabItemSession.setData("TN5250JInfo", tn5250jInfo);
            tabItemSession.setData("CompositeSession", arrayListCompositeSession);
            tabItemSession.setData("TabItemTN5250J", arrayListTabItemTN5250J);
            tabItemSession.setData("Session", session);
            tabItemSession.setData("LastFocus", new Integer(0));
            tabItemSession.setControl(compositeControl);

            tn5250jPart.getTabFolderSessions().setSelection(tabItemSession);

            SetSessionFocus.run(tn5250jPart.getTabFolderSessions().getSelectionIndex(), 0, tn5250jPart);

            new ConnectSession(tn5250j).start();

        }

    }

}
