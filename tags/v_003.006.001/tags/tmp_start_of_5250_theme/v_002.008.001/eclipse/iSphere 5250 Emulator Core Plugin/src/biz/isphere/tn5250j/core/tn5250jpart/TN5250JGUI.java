/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.tn5250jpart;

import java.awt.event.KeyEvent;

import org.tn5250j.Session5250;
import org.tn5250j.SessionPanel;

public abstract class TN5250JGUI extends SessionPanel {

    private static final long serialVersionUID = 1L;
    private TN5250JInfo tn5250jInfo;
    private Session5250 session5250;

    public TN5250JGUI(TN5250JInfo tn5250jInfo, Session5250 session5250) {
        super(session5250);
        this.tn5250jInfo = tn5250jInfo;
        this.session5250 = session5250;
    }

    @Override
    public void processKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.isControlDown()
            && keyEvent.isAltDown()
            && (keyEvent.getKeyCode() == KeyEvent.VK_UP || keyEvent.getKeyCode() == KeyEvent.VK_DOWN || keyEvent.getKeyCode() == KeyEvent.VK_LEFT || keyEvent
                .getKeyCode() == KeyEvent.VK_RIGHT)) {
            new ScrollSession(tn5250jInfo.getTN5250JPart().getTabFolderSessions().getDisplay(), tn5250jInfo.getTN5250JPart().getTabFolderSessions(),
                keyEvent).start();
        } else if (keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_UP) {
            if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
                new SetMajorSession(tn5250jInfo.getTN5250JPart().getTabFolderSessions().getDisplay(), tn5250jInfo.getTN5250JPart()
                    .getTabFolderSessions(), "*NEXT", tn5250jInfo.getTN5250JPart()).start();
            }
        } else if (keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
            if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
                new SetMajorSession(tn5250jInfo.getTN5250JPart().getTabFolderSessions().getDisplay(), tn5250jInfo.getTN5250JPart()
                    .getTabFolderSessions(), "*PREVIOUS", tn5250jInfo.getTN5250JPart()).start();
            }
        }

        else if (keyEvent.isAltDown() && keyEvent.getKeyCode() == KeyEvent.VK_UP) {
            if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
                new SetMinorSession(tn5250jInfo.getTN5250JPart().getTabFolderSessions().getDisplay(), tn5250jInfo.getTN5250JPart()
                    .getTabFolderSessions(), "*NEXT", tn5250jInfo.getTN5250JPart()).start();
            }
        } else if (keyEvent.isAltDown() && keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
            if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
                new SetMinorSession(tn5250jInfo.getTN5250JPart().getTabFolderSessions().getDisplay(), tn5250jInfo.getTN5250JPart()
                    .getTabFolderSessions(), "*PREVIOUS", tn5250jInfo.getTN5250JPart()).start();
            }
        } else {
            super.processKeyEvent(keyEvent);
        }
    }

    public TN5250JInfo getTN5250JInfo() {
        return tn5250jInfo;
    }

    public Session5250 getSession5250() {
        return session5250;
    }

}
