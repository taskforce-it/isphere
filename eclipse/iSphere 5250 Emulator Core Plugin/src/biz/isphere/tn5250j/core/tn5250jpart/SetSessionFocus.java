/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.tn5250jpart;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.tn5250j.core.session.Session;

public class SetSessionFocus {

    public static void run(int majorSession, int minorSession, final ITN5250JPart tn5250jPart) {
        if (majorSession >= 0) {
            if (tn5250jPart instanceof IWorkbenchPart) {
                IWorkbenchPart part = (IWorkbenchPart)tn5250jPart;
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop(part);
            }
            CTabItem tabItem = tn5250jPart.getTabFolderSessions().getItem(majorSession);
            int newMinorSession = minorSession;
            if (newMinorSession == -1) {
                newMinorSession = ((Integer)tabItem.getData(SessionTabData.LAST_FOCUS)).intValue();
            }
            ArrayList arrayListTabItemTN5250J = (ArrayList)tabItem.getData(SessionTabData.TAB_ITEM_TN5250J);
            if (arrayListTabItemTN5250J.size() == 0) {
                if (tn5250jPart.isMultiSession()) {
                    tn5250jPart.setAddSession(false);
                    tn5250jPart.setRemoveSession(false);
                }
                return;
            }

            TN5250JPanel tn5250j = (TN5250JPanel)arrayListTabItemTN5250J.get(newMinorSession);
            if (tn5250j != null) {

                // MUST be outside the UIJob!
                tn5250jPart.getTabFolderSessions().getItem(tn5250jPart.getTabFolderSessions().getSelectionIndex());

                // Works fine, but 5250 sessions does not get focus when RDi
                // starts.
                // EventQueue.invokeLater(new GrabFocusRunnable(tn5250j));

                // Using a UIJob ensures that the 5250 sessions gets the focus
                // when RDi is started and the 5250 sessions view is visible.
                new GrabFocusJob(tn5250j).schedule();

                if (tn5250jPart.isMultiSession()) {
                    Session session = (Session)tabItem.getData(SessionTabData.SESSION);
                    if (!session.getDevice().equals("")) {
                        tn5250jPart.setAddSession(false);
                        tn5250jPart.setRemoveSession(false);
                    } else {
                        if (arrayListTabItemTN5250J.size() >= 4) {
                            tn5250jPart.setAddSession(false);
                        } else {
                            tn5250jPart.setAddSession(true);
                        }
                        if (arrayListTabItemTN5250J.size() <= 1) {
                            tn5250jPart.setRemoveSession(false);
                        } else {
                            tn5250jPart.setRemoveSession(true);
                        }
                    }
                }
            }
        }
    }

    private static class GrabFocusRunnable implements Runnable {

        private TN5250JPanel tn5250j;

        public GrabFocusRunnable(TN5250JPanel tn5250j) {
            this.tn5250j = tn5250j;
        }

        public void run() {
            tn5250j.getSessionGUI().grabFocus();
        }
    }

    private static class GrabFocusJob extends UIJob { // $NON-NLS-1$

        private TN5250JPanel tn5250j;

        public GrabFocusJob(TN5250JPanel tn5250j) {
            super("");
            this.tn5250j = tn5250j;
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor arg0) {
            tn5250j.getSessionGUI().grabFocus();
            return Status.OK_STATUS;
        }
    }
}
