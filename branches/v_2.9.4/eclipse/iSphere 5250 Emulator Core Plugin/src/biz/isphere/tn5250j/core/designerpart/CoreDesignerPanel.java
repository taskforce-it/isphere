/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.designerpart;

import org.eclipse.swt.widgets.Shell;
import org.tn5250j.Session5250;
import org.tn5250j.TN5250jConstants;
import org.tn5250j.event.SessionChangeEvent;
import org.tn5250j.event.SessionListener;
import org.tn5250j.framework.tn5250.Screen5250;
import org.tn5250j.framework.tn5250.ScreenField;
import org.tn5250j.framework.tn5250.ScreenFields;

import biz.isphere.tn5250j.core.session.Session;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JInfo;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JPanel;

public abstract class CoreDesignerPanel extends TN5250JPanel {

    private static final long serialVersionUID = 1L;

    private class OpenDesignerAsync extends Thread {

        private Session5250 session5250;

        public OpenDesignerAsync(Session5250 session5250) {
            this.session5250 = session5250;
        }

        @Override
        public void run() {
            Screen5250 screen5250 = session5250.getScreen();
            CoreDesignerGUI designerGUI = (CoreDesignerGUI)session5250.getGUI();
            CoreDesignerInfo designerInfo = (CoreDesignerInfo)designerGUI.getTN5250JInfo();
            if (delay()) {
                boolean ok = false;
                char[] chr = screen5250.getScreenAsChars();
                if (chr[1] == 'T' && chr[2] == 'N' && chr[3] == '5' && chr[4] == '2' && chr[5] == '5' && chr[6] == '0' && chr[7] == 'J'
                    && chr[8] == '-' && chr[9] == 'D' && chr[10] == 'E' && chr[11] == 'S' && chr[12] == 'I' && chr[13] == 'G' && chr[14] == 'N'
                    && chr[15] == 'E' && chr[16] == 'R') {
                    ok = true;
                } else {
                    screen5250.sendKeys("[enter]");
                    if (delay()) {
                        ok = true;
                    }
                }
                if (ok) {
                    ScreenFields screenFields = screen5250.getScreenFields();
                    ScreenField[] screenField = screenFields.getFields();
                    for (int idx = 0; idx < screenField.length; idx++) {
                        if (idx == 0) {
                            screenField[idx].setString(designerInfo.getLibrary());
                        } else if (idx == 1) {
                            screenField[idx].setString(designerInfo.getSourceFile());
                        } else if (idx == 2) {
                            screenField[idx].setString(designerInfo.getMember());
                        } else if (idx == 3) {
                            screenField[idx].setString(designerInfo.getEditor());
                        } else if (idx == 4) {
                            screenField[idx].setString(designerInfo.getMode());
                        } else if (idx == 5) {
                            screenField[idx].setString(designerInfo.getCurrentLibrary());
                        } else if (idx >= 6) {
                            int posStart = (idx - 6) * 131;
                            if (posStart < designerInfo.getLibraryList().length()) {
                                int posEnd = (idx - 5) * 131;
                                if (posEnd > designerInfo.getLibraryList().length()) {
                                    posEnd = designerInfo.getLibraryList().length();
                                }
                                screenField[idx].setString(designerInfo.getLibraryList().substring(posStart, posEnd));
                            }
                        }
                    }
                    screen5250.updateScreen();
                    screen5250.sendKeys("[enter]");
                }
            }
        }

        private boolean delay() {
            Screen5250 screen5250 = session5250.getScreen();
            int count = 0;
            while (screen5250.getOIA().isKeyBoardLocked()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                count++;
                if (count == 20) {
                    return false;
                }
            }
            ;
            return true;
        }

    }

    public CoreDesignerPanel(TN5250JInfo tn5250jInfo, Session session, Shell shell) {
        super(tn5250jInfo, session, shell);

        SessionListener sessionListener = new SessionListener() {
            public void onSessionChanged(SessionChangeEvent event) {
                if (event.getState() == TN5250jConstants.STATE_CONNECTED) {
                    Session5250 session5250 = (Session5250)event.getSource();
                    // session5250.removeSessionListener(this);
                    new OpenDesignerAsync(session5250).start();
                }
            }
        };

        getSession5250().addSessionListener(sessionListener);

    }

    @Override
    public String getHost() {
        return "";
    }

}
