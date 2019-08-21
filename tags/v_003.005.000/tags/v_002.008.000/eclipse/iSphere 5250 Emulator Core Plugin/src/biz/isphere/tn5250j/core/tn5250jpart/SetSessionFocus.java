/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.tn5250jpart;

import java.awt.EventQueue;
import java.util.ArrayList;

import org.eclipse.swt.custom.CTabItem;

import biz.isphere.tn5250j.core.session.Session;

public class SetSessionFocus {

    public static void run(int majorSession, int minorSession, ITN5250JPart tn5250jPart) {
        if (majorSession >= 0) {
            CTabItem tabItem = tn5250jPart.getTabFolderSessions().getItem(majorSession);
            int newMinorSession = minorSession;
            if (newMinorSession == -1) {
                newMinorSession = ((Integer)tabItem.getData("LastFocus")).intValue();
            }
            ArrayList arrayListTabItemTN5250J = (ArrayList)tabItem.getData("TabItemTN5250J");
            final TN5250JPanel tn5250j = (TN5250JPanel)arrayListTabItemTN5250J.get(newMinorSession);
            if (tn5250j != null) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        tn5250j.getSessionGUI().grabFocus();
                    }
                });
                if (tn5250jPart.isMultiSession()) {
                    Session session = (Session)tabItem.getData("Session");
                    if (!session.getDevice().equals("")) {
                        tn5250jPart.setAddSession(false);
                        tn5250jPart.setRemoveSession(false);
                    } else {
                        if (arrayListTabItemTN5250J.size() == 4) {
                            tn5250jPart.setAddSession(false);
                        } else {
                            tn5250jPart.setAddSession(true);
                        }
                        if (arrayListTabItemTN5250J.size() == 1) {
                            tn5250jPart.setRemoveSession(false);
                        } else {
                            tn5250jPart.setRemoveSession(true);
                        }
                    }
                }
            }
        }
    }

}
