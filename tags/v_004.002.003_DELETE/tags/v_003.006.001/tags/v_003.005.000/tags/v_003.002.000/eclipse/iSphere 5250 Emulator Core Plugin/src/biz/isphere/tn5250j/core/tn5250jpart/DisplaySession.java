/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.tn5250jpart;

import org.eclipse.swt.custom.CTabItem;

/**
 * This class serves the double-click on a 5250 session item of an
 * "iSphere 5250" node of the RSE tree. It either creates a new session tab or
 * selects the tab that belongs to the session item.
 */
public class DisplaySession {

    public static void run(TN5250JInfo tn5250jInfo) {

        ITN5250JPart tn5250jPart = tn5250jInfo.getTN5250JPart();

        int tabItemNumber = tn5250jPart.findSessionTab(tn5250jInfo);
        if (tabItemNumber == -1) {

            AddSessionTab.run(tn5250jInfo);

        } else {

            CTabItem tabItem = tn5250jPart.getTabFolderSessions().getItem(tabItemNumber);
            tn5250jPart.getTabFolderSessions().setSelection(tabItem);

            SetSessionFocus.run(tn5250jPart.getTabFolderSessions().getSelectionIndex(), -1, tn5250jPart);

        }

    }

}
