/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.tn5250jpart;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import biz.isphere.tn5250j.core.session.Session;

public class AddMultiSession {

    @SuppressWarnings("unchecked")
    public static void run(ITN5250JPart tn5250jPart) {

        CTabItem tabItemSession = tn5250jPart.getTabFolderSessions().getSelection();

        ArrayList<Composite> arrayListCompositeSession = (ArrayList)tabItemSession.getData("CompositeSession");
        ArrayList<TN5250JPanel> arrayListTabItemTN5250J = (ArrayList)tabItemSession.getData("TabItemTN5250J");
        Session session = (Session)tabItemSession.getData("Session");
        TN5250JInfo tn5250jInfo = (TN5250JInfo)tabItemSession.getData("TN5250JInfo");

        int numberOfSessions = arrayListCompositeSession.size();

        if (numberOfSessions < 4) {

            Composite compositeControl = new Composite(tn5250jPart.getTabFolderSessions(), SWT.NONE);
            GridLayout gridLayoutControl = new GridLayout();
            gridLayoutControl.numColumns = 2;
            compositeControl.setLayout(gridLayoutControl);

            for (int idx = 0; idx < numberOfSessions; idx++) {
                Composite compositeSession = arrayListCompositeSession.get(idx);
                compositeSession.setParent(compositeControl);
            }

            CreateSession createSession = new CreateSession();
            final TN5250JPanel tn5250j = createSession
                .run(compositeControl, arrayListCompositeSession, arrayListTabItemTN5250J, session, tn5250jInfo);

            tabItemSession.setControl(compositeControl);

            SetSessionFocus.run(tn5250jPart.getTabFolderSessions().getSelectionIndex(), numberOfSessions, tn5250jPart);

            new ConnectSession(tn5250j).start();

        }

    }

}
