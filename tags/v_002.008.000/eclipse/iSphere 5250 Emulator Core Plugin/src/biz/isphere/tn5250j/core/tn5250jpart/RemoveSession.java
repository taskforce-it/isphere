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

public class RemoveSession {

    public static void run(CTabItem closedTab, ITN5250JPart tn5250jPart) {
        ArrayList arrayListTabItemTN5250J = (ArrayList)closedTab.getData("TabItemTN5250J");
        for (int idx = 0; idx < arrayListTabItemTN5250J.size(); idx++) {
            TN5250JPanel tn5250j = (TN5250JPanel)arrayListTabItemTN5250J.get(idx);
            tn5250j.removeScreenListener();
            tn5250j.getSession5250().disconnect();
            tn5250jPart.removeTN5250JPanel(tn5250j);
            closedTab.dispose();
        }
        if (tn5250jPart.isMultiSession() && tn5250jPart.getTabFolderSessions().getItemCount() == 0) {
            tn5250jPart.setAddSession(false);
            tn5250jPart.setRemoveSession(false);
        }

    }

}
