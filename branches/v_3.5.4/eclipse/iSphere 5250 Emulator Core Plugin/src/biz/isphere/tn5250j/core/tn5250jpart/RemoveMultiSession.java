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

/**
 * This class removes a multi-session from a tab of an "iSphere 5250 Sessions"
 * view.
 */
public class RemoveMultiSession {

    @SuppressWarnings("unchecked")
    public static void run(ITN5250JPart tn5250jPart) {

        CTabItem tabItemSession = tn5250jPart.getTabFolderSessions().getSelection();

        int sessionToDelete = ((Integer)tabItemSession.getData(SessionTabData.LAST_FOCUS)).intValue();
        ArrayList<TN5250JPanel> arrayListTabItemTN5250J = (ArrayList)tabItemSession.getData(SessionTabData.TAB_ITEM_TN5250J);
        ArrayList<Composite> arrayListCompositeSession = (ArrayList)tabItemSession.getData(SessionTabData.COMPOSITE_SESSION);

        int numberOfSessions = arrayListCompositeSession.size();

        if (numberOfSessions > 0) {

            Composite compositeControl = new Composite(tn5250jPart.getTabFolderSessions(), SWT.NONE);
            GridLayout gridLayoutControl = new GridLayout();
            if (numberOfSessions == 2) {
                gridLayoutControl.numColumns = 1;
            } else {
                gridLayoutControl.numColumns = 2;
            }
            compositeControl.setLayout(gridLayoutControl);

            for (int idx = 0; idx < numberOfSessions; idx++) {
                if (idx != sessionToDelete) {
                    Composite compositeSession = arrayListCompositeSession.get(idx);
                    compositeSession.setParent(compositeControl);
                }
            }

            tabItemSession.setControl(compositeControl);

            TN5250JPanel tn5250j = arrayListTabItemTN5250J.get(sessionToDelete);
            tn5250j.removeScreenListener();
            tn5250j.shutdown();
            arrayListTabItemTN5250J.remove(sessionToDelete);

            tn5250jPart.removeTN5250JPanel(tn5250j);

            Composite compositeSession = arrayListCompositeSession.get(sessionToDelete);
            compositeSession.dispose();
            arrayListCompositeSession.remove(sessionToDelete);

            SetSessionFocus.run(tn5250jPart.getTabFolderSessions().getSelectionIndex(), 0, tn5250jPart);

        }

    }

}
