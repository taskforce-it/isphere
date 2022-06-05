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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ProcessSessionFocus {

    private Display display;
    private CTabFolder tabFolderSessions;
    private String mode;
    private java.awt.event.FocusEvent event;

    public ProcessSessionFocus(Display display, CTabFolder tabFolderSessions, String mode, java.awt.event.FocusEvent event) {
        this.display = display;
        this.tabFolderSessions = tabFolderSessions;
        this.mode = mode;
        this.event = event;
    }

    public void start() {
        display.asyncExec(new Runnable() {
            public void run() {
                int color;
                if (mode.equals("*GAINED")) {
                    color = SWT.COLOR_RED;
                } else if (mode.equals("*LOST")) {
                    color = SWT.COLOR_WHITE;
                } else {
                    color = SWT.COLOR_BLACK;
                }
                CTabItem[] tabItems = tabFolderSessions.getItems();
                for (int idx1 = 0; idx1 < tabItems.length; idx1++) {
                    if (!tabItems[idx1].isDisposed()) {
                        ArrayList arrayListCompositeSession = (ArrayList)tabItems[idx1].getData("CompositeSession");
                        for (int idx2 = 0; idx2 < arrayListCompositeSession.size(); idx2++) {
                            Composite compositeSession = (Composite)arrayListCompositeSession.get(idx2);
                            TN5250JPanel tn5250j = (TN5250JPanel)compositeSession.getData("CompositeTN5250J");
                            if (tn5250j.getSessionGUI() == event.getSource()) {
                                compositeSession.setBackground(Display.getCurrent().getSystemColor(color));
                                if (mode.equals("*GAINED")) {
                                    tabItems[idx1].setData("LastFocus", new Integer(idx2));
                                }
                            }
                        }
                    }

                }
            }
        });
    }

}
