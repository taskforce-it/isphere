/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.tn5250jpart;

import org.eclipse.swt.custom.CTabItem;

public class DisplaySession {

    public static void run(String sessionDirectory, String connection, String name, TN5250JInfo tn5250jInfo) {

        ITN5250JPart tn5250jPart = tn5250jInfo.getTN5250JPart();

        int tabItemNumber = -1;
        CTabItem[] tabItems = tn5250jPart.getTabFolderSessions().getItems();
        for (int idx = 0; idx < tabItems.length; idx++) {
            String tabItemConnection = (String)tabItems[idx].getData("Connection");
            String tabItemName = (String)tabItems[idx].getData("Name");
            TN5250JInfo tabItemTN5250JInfo = (TN5250JInfo)tabItems[idx].getData("TN5250JInfo");
            if (connection.equals(tabItemConnection) && name.equals(tabItemName) && tn5250jInfo.isTN5250JEqual(tabItemTN5250JInfo)) {
                tabItemNumber = idx;
                break;
            }
        }
        if (tabItemNumber == -1) {

            AddSession.run(sessionDirectory, connection, name, tn5250jInfo);

        } else {
            tn5250jPart.getTabFolderSessions().setSelection(tabItems[tabItemNumber]);

            SetSessionFocus.run(tn5250jPart.getTabFolderSessions().getSelectionIndex(), -1, tn5250jPart);

        }

    }

}
