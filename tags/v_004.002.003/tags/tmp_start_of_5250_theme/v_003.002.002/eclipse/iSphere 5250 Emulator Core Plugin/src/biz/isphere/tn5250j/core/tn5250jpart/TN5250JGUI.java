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
import org.tn5250j.keyboard.KeyMapper;

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

        if (KeyMapper.isNextMajorSessionKeyStroke(keyEvent)) {
            if (isKeyReleased(keyEvent)) {
                new SetMajorSession(tn5250jInfo.getTN5250JPart().getTabFolderSessions().getDisplay(), tn5250jInfo.getTN5250JPart()
                    .getTabFolderSessions(), "*NEXT", tn5250jInfo.getTN5250JPart()).start();
                keyEvent.consume();
            }
            return;
        }

        if (KeyMapper.isPreviousMajorSessionKeyStroke(keyEvent)) {
            if (isKeyReleased(keyEvent)) {
                new SetMajorSession(tn5250jInfo.getTN5250JPart().getTabFolderSessions().getDisplay(), tn5250jInfo.getTN5250JPart()
                    .getTabFolderSessions(), "*PREVIOUS", tn5250jInfo.getTN5250JPart()).start();
                keyEvent.consume();
            }
            return;
        }

        if (KeyMapper.isNextMinorSessionKeyStroke(keyEvent)) {
            if (isKeyReleased(keyEvent)) {
                new SetMinorSession(tn5250jInfo.getTN5250JPart().getTabFolderSessions().getDisplay(), tn5250jInfo.getTN5250JPart()
                    .getTabFolderSessions(), "*NEXT", tn5250jInfo.getTN5250JPart()).start();
                keyEvent.consume();
            }
            return;
        }

        if (KeyMapper.isPreviousMinorSessionKeyStroke(keyEvent)) {
            if (isKeyReleased(keyEvent)) {
                new SetMinorSession(tn5250jInfo.getTN5250JPart().getTabFolderSessions().getDisplay(), tn5250jInfo.getTN5250JPart()
                    .getTabFolderSessions(), "*PREVIOUS", tn5250jInfo.getTN5250JPart()).start();
                keyEvent.consume();
            }
            return;
        }

        if (KeyMapper.isScrollSessionUpKeyStroke(keyEvent) || KeyMapper.isScrollSessionDownKeyStroke(keyEvent)
            || KeyMapper.isScrollSessionLeftKeyStroke(keyEvent) || KeyMapper.isScrollSessionRightKeyStroke(keyEvent)) {
            new ScrollSession(tn5250jInfo.getTN5250JPart().getTabFolderSessions().getDisplay(), tn5250jInfo.getTN5250JPart().getTabFolderSessions(),
                keyEvent).start();
            keyEvent.consume();
            return;
        }

        super.processKeyEvent(keyEvent);
    }

    private boolean isKeyReleased(KeyEvent keyEvent) {
        return keyEvent.getID() == KeyEvent.KEY_RELEASED;
    }

    public TN5250JInfo getTN5250JInfo() {
        return tn5250jInfo;
    }

    public Session5250 getSession5250() {
        return session5250;
    }

}
