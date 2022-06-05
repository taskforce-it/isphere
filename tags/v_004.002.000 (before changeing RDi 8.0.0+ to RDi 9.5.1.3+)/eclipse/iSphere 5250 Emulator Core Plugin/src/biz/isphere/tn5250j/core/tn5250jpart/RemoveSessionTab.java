/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.tn5250jpart;

import java.util.ArrayList;

import org.eclipse.swt.custom.CTabItem;

/**
 * This class closes all multi-sessions of a given session tab of an
 * "iSphere 5250 Sessions" view and closes the tab.
 */
public class RemoveSessionTab {

    public static void run(CTabItem closedTab, ITN5250JPart tn5250jPart) {

        ArrayList arrayListTabItemTN5250J = (ArrayList)closedTab.getData(SessionTabData.TAB_ITEM_TN5250J);
        int numberOfSession = arrayListTabItemTN5250J.size();

        while (arrayListTabItemTN5250J.size() > 0 && numberOfSession > 0) {
            tn5250jPart.getTabFolderSessions().setSelection(closedTab);
            closedTab.setData(SessionTabData.LAST_FOCUS, 0);
            RemoveMultiSession.run(tn5250jPart);
            numberOfSession--;
        }

        if (arrayListTabItemTN5250J.size() == 0) {
            closedTab.dispose();
        }

    }

}
