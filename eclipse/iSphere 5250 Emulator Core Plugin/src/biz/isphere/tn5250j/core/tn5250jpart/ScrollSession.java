/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.tn5250jpart;

import java.util.ArrayList;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ScrollSession {

    private Display display;
    private CTabFolder tabFolderSessions;
    private java.awt.event.KeyEvent keyEvent;

    public ScrollSession(Display display, CTabFolder tabFolderSessions, java.awt.event.KeyEvent keyEvent) {
        this.display = display;
        this.tabFolderSessions = tabFolderSessions;
        this.keyEvent = keyEvent;
    }

    public void start() {
        display.asyncExec(new Runnable() {
            public void run() {
                CTabItem tabItemSession = tabFolderSessions.getSelection();
                ArrayList arrayListTabItemTN5250J = (ArrayList)tabItemSession.getData(SessionTabData.TAB_ITEM_TN5250J);
                ArrayList arrayListCompositeSession = (ArrayList)tabItemSession.getData(SessionTabData.COMPOSITE_SESSION);
                for (int idx = 0; idx < arrayListTabItemTN5250J.size(); idx++) {
                    TN5250JPanel tn5250j = (TN5250JPanel)arrayListTabItemTN5250J.get(idx);
                    if (tn5250j.getSessionGUI().hasFocus()) {
                        Composite compositeX = (Composite)arrayListCompositeSession.get(idx);
                        ScrolledComposite sc = (ScrolledComposite)compositeX.getData(SessionPanelData.SCROLLED_COMPOSITE);
                        if (keyEvent.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                            Point point = sc.getOrigin();
                            point.y = point.y - 50;
                            sc.setOrigin(point);
                        }
                        if (keyEvent.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
                            Point point = sc.getOrigin();
                            point.y = point.y + 50;
                            sc.setOrigin(point);
                        }
                        if (keyEvent.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
                            Point point = sc.getOrigin();
                            point.x = point.x - 50;
                            sc.setOrigin(point);
                        }
                        if (keyEvent.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT) {
                            Point point = sc.getOrigin();
                            point.x = point.x + 50;
                            sc.setOrigin(point);
                        }
                        break;
                    }
                }
            }
        });
    }

}
