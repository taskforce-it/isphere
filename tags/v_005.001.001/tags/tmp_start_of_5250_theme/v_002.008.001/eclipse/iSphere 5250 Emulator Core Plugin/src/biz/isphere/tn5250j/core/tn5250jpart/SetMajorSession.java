/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.tn5250jpart;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Display;

public class SetMajorSession {

    private Display display;
    private CTabFolder tabFolderSessions;
    private String direction;
    private ITN5250JPart tn5250jPart;

    public SetMajorSession(Display display, CTabFolder tabFolderSessions, String direction, ITN5250JPart tn5250jPart) {
        this.display = display;
        this.tabFolderSessions = tabFolderSessions;
        this.direction = direction;
        this.tn5250jPart = tn5250jPart;
    }

    public void start() {
        display.asyncExec(new Runnable() {
            public void run() {
                if (direction.equals("*NEXT")) {
                    int currentItem = tabFolderSessions.getSelectionIndex();
                    if (currentItem >= 0) {
                        int itemCount = tabFolderSessions.getItemCount() - 1;
                        int nextItem;
                        if (currentItem < itemCount) {
                            nextItem = currentItem + 1;
                        } else {
                            nextItem = 0;
                        }
                        tabFolderSessions.setSelection(nextItem);
                        SetSessionFocus.run(nextItem, -1, tn5250jPart);
                    }
                } else if (direction.equals("*PREVIOUS")) {
                    int currentItem = tabFolderSessions.getSelectionIndex();
                    if (currentItem >= 0) {
                        int itemCount = tabFolderSessions.getItemCount() - 1;
                        int nextItem;
                        if (currentItem > 0) {
                            nextItem = currentItem - 1;
                        } else {
                            nextItem = itemCount;
                        }
                        tabFolderSessions.setSelection(nextItem);
                        SetSessionFocus.run(nextItem, -1, tn5250jPart);
                    }
                }
            }
        });
    }

}
