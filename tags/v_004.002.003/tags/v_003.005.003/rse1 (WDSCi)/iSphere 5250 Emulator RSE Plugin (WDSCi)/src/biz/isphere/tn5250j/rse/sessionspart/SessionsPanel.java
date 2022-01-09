/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.sessionspart;

import org.eclipse.swt.widgets.Shell;
import org.tn5250j.Session5250;
import org.tn5250j.framework.tn5250.ScreenField;
import org.tn5250j.framework.tn5250.ScreenFields;

import biz.isphere.tn5250j.core.session.Session;
import biz.isphere.tn5250j.core.sessionspart.CoreSessionsPanel;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JGUI;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JInfo;
import biz.isphere.tn5250j.rse.sessionspart.handler.OpenCompareAsync;
import biz.isphere.tn5250j.rse.sessionspart.handler.OpenLpexAsync;
import biz.isphere.tn5250j.rse.sessionspart.handler.SetSEPAsync;

import com.ibm.etools.iseries.core.api.ISeriesConnection;

public class SessionsPanel extends CoreSessionsPanel {

    private static final long serialVersionUID = 1L;

    private static final String TN5250J_SETDBGSEP = "TN5250J-SETDBGSEP";
    private static final int TN5250J_SETDBGSEP_OFFSET = 32;

    private static final String TN5250J_COMPARE = "TN5250J-COMPARE";
    private static final int TN5250J_COMPARE_OFFSET = 32;

    private static final String TN5250J_EDITOR = "TN5250J-EDITOR";
    private static final int TN5250J_EDITOR_OFFSET = 2;

    public SessionsPanel(TN5250JInfo tn5250jInfo, Session session, Shell shell) {
        super(tn5250jInfo, session, shell);
    }

    @Override
    public void onScreenChanged(int arg0, int arg1, int arg2, int arg3, int arg4) {

        if (arg0 == 1) {

            SessionsInfo sessionsInfo = (SessionsInfo)getTN5250JInfo();
            Runnable runnable = null;

            if (isRemoteCommandEditor()) {
                String library = "";
                String sourceFile = "";
                String member = "";
                String mode = "";
                String currentLibrary = "";
                StringBuffer libraryList = new StringBuffer("");
                ScreenFields screenFields = getSessionGUI().getScreen().getScreenFields();
                ScreenField[] screenField = screenFields.getFields();
                for (int idx = 0; idx < screenField.length; idx++) {
                    if (idx == 0) {
                        library = screenField[idx].getString().trim();
                    } else if (idx == 1) {
                        sourceFile = screenField[idx].getString().trim();
                    } else if (idx == 2) {
                        member = screenField[idx].getString().trim();
                    } else if (idx == 3) {
                        mode = screenField[idx].getString().trim();
                    } else if (idx == 4) {
                        currentLibrary = screenField[idx].getString().trim();
                    } else if (idx >= 5 && idx <= 25) {
                        libraryList.append(screenField[idx].getString().trim() + " ");
                    }
                }
                if (!library.equals("") && !sourceFile.equals("") && !member.equals("") && !mode.equals("")) {
                    runnable = new OpenLpexAsync(getShell(), sessionsInfo, library, sourceFile, member, mode, currentLibrary, libraryList.toString());
                    getShell().getDisplay().asyncExec(runnable);
                }
                getSessionGUI().getScreen().sendKeys("[pf3]");
            } else if (isRemoteCommandCompare()) {
                String library = "";
                String sourceFile = "";
                String member = "";
                ScreenFields screenFields = getSessionGUI().getScreen().getScreenFields();
                ScreenField[] screenField = screenFields.getFields();
                for (int idx = 0; idx < screenField.length; idx++) {
                    if (idx == 0) {
                        library = screenField[idx].getString().trim();
                    } else if (idx == 1) {
                        sourceFile = screenField[idx].getString().trim();
                    } else if (idx == 2) {
                        member = screenField[idx].getString().trim();
                    }
                }
                if (!library.equals("") && !sourceFile.equals("") && !member.equals("")) {
                    runnable = new OpenCompareAsync(getShell(), sessionsInfo, library, sourceFile, member);
                    getShell().getDisplay().asyncExec(runnable);
                }
                getSessionGUI().getScreen().sendKeys("[pf3]");
            } else if (isRemoteCommandSetSEP()) {
                String library = "";
                String object = "";
                String type = "";
                ScreenFields screenFields = getSessionGUI().getScreen().getScreenFields();
                ScreenField[] screenField = screenFields.getFields();
                for (int idx = 0; idx < screenField.length; idx++) {
                    if (idx == 0) {
                        library = screenField[idx].getString().trim();
                    } else if (idx == 1) {
                        object = screenField[idx].getString().trim();
                    } else if (idx == 2) {
                        type = screenField[idx].getString().trim();
                    }
                }
                if (!library.equals("") && !object.equals("") && !type.equals("")) {
                    if ("*PGM".equals(type) || "*SRVPGM".equals(type)) {
                        runnable = new SetSEPAsync(getShell(), sessionsInfo, library, object, type);
                        getShell().getDisplay().asyncExec(runnable);
                    }
                }
                getSessionGUI().getScreen().sendKeys("[pf3]");
            }
        }
    }

    private boolean isRemoteCommandEditor() {
        return TN5250J_EDITOR.equals(getRemoteHandlerId(TN5250J_EDITOR_OFFSET, TN5250J_EDITOR));
    }

    private boolean isRemoteCommandCompare() {
        return TN5250J_COMPARE.equals(getRemoteHandlerId(TN5250J_COMPARE_OFFSET, TN5250J_COMPARE));
    }

    private boolean isRemoteCommandSetSEP() {
        return TN5250J_SETDBGSEP.equals(getRemoteHandlerId(TN5250J_SETDBGSEP_OFFSET, TN5250J_SETDBGSEP));
    }

    private String getRemoteHandlerId(int offset, String id) {
        char[] chars = getSession5250().getScreen().getData(0, offset, 0, offset + id.length(), PLANE_TEXT);

        // Strip trailing 0x00
        return new String(chars).substring(0, chars.length - 1);
    }

    /**
     * This method creates the actual 5250 emulator session.
     * <p>
     * Up to 4 sessions can be added to a 5250 view.
     */
    @Override
    public TN5250JGUI getTN5250JGUI(TN5250JInfo tn5250jInfo, Session5250 session5250) {
        // Create the session GUI. AWT components start here.
        return new SessionsGUI(tn5250jInfo, session5250);
    }

    @Override
    public String getHost() {
        SessionsInfo sessionsInfo = (SessionsInfo)getTN5250JInfo();
        ISeriesConnection iSeriesConnection = ISeriesConnection.getConnection(sessionsInfo.getRSEProfil(), sessionsInfo.getRSEConnection());
        if (iSeriesConnection != null) {
            return iSeriesConnection.getHostName();
        }
        return "";
    }

    @Override
    public String getTheme() {
        return getSession().getTheme();
    }

}
