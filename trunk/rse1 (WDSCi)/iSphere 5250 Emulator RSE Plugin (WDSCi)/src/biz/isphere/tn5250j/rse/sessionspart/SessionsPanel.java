/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.sessionspart;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.tn5250j.Session5250;
import org.tn5250j.framework.tn5250.ScreenField;
import org.tn5250j.framework.tn5250.ScreenFields;

import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.api.ISeriesMember;
import com.ibm.etools.systems.core.messages.SystemMessageException;

import biz.isphere.core.compareeditor.CompareAction;
import biz.isphere.rse.compareeditor.RSECompareDialog;
import biz.isphere.rse.internal.RSEMember;
import biz.isphere.tn5250j.core.session.Session;
import biz.isphere.tn5250j.core.sessionspart.CoreSessionsPanel;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JGUI;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JInfo;

public class SessionsPanel extends CoreSessionsPanel {

    private static final long serialVersionUID = 1L;

    private class OpenLpexAsync {

        private String library;
        private String sourceFile;
        private String member;
        private String mode;
        private String currentLibrary;
        private String libraryList;

        public OpenLpexAsync(String library, String sourceFile, String member, String mode, String currentLibrary, String libraryList) {
            this.library = library;
            this.sourceFile = sourceFile;
            this.member = member;
            this.mode = mode;
            this.currentLibrary = currentLibrary;
            this.libraryList = libraryList;
        }

        public void start() {
            getShell().getDisplay().asyncExec(new Runnable() {
                public void run() {

                    SessionsInfo sessionsInfo = (SessionsInfo)getTN5250JInfo();

                    ISeriesConnection iSeriesConnection = ISeriesConnection.getConnection(sessionsInfo.getRSEProfil(), sessionsInfo
                        .getRSEConnection());

                    String command = "CHGLIBL LIBL(" + libraryList + ") CURLIB(" + currentLibrary + ")";

                    try {
                        iSeriesConnection.runCommand(getShell(), command);
                    } catch (SystemMessageException event) {
                    }

                    ISeriesMember iseriesMember;
                    try {
                        iseriesMember = iSeriesConnection.getISeriesMember(getShell(), library, sourceFile, member);
                        if (iseriesMember != null) {
                            if (mode.equals("*OPEN")) {
                                iseriesMember.open();
                            } else {
                                iseriesMember.browse();
                            }
                        }
                    } catch (SystemMessageException e) {
                    }

                }
            });
        }

    }

    private class OpenCompareAsync {

        private String library;
        private String sourceFile;
        private String member;

        public OpenCompareAsync(String library, String sourceFile, String member) {
            this.library = library;
            this.sourceFile = sourceFile;
            this.member = member;
        }

        public void start() {
            getShell().getDisplay().asyncExec(new Runnable() {
                public void run() {

                    SessionsInfo sessionsInfo = (SessionsInfo)getTN5250JInfo();

                    ISeriesConnection iSeriesConnection = ISeriesConnection.getConnection(sessionsInfo.getRSEProfil(), sessionsInfo
                        .getRSEConnection());

                    ISeriesMember _member;
                    try {
                        _member = iSeriesConnection.getISeriesMember(getShell(), library, sourceFile, member);
                        if (_member != null) {

                            try {

                                RSEMember rseLeftMember = new RSEMember(_member);

                                RSECompareDialog dialog = new RSECompareDialog(getShell(), true, rseLeftMember);

                                if (dialog.open() == Window.OK) {

                                    boolean editable = dialog.isEditable();
                                    boolean considerDate = dialog.isConsiderDate();
                                    boolean threeWay = dialog.isThreeWay();

                                    RSEMember rseAncestorMember = null;

                                    if (threeWay) {

                                        ISeriesMember ancestorMember = dialog.getAncestorConnection().getISeriesMember(getShell(),
                                            dialog.getAncestorLibrary(), dialog.getAncestorFile(), dialog.getAncestorMember());

                                        if (ancestorMember != null) {
                                            rseAncestorMember = new RSEMember(ancestorMember);
                                        }

                                    }

                                    RSEMember rseRightMember = null;

                                    ISeriesMember rightMember = dialog.getRightConnection().getISeriesMember(getShell(), dialog.getRightLibrary(),
                                        dialog.getRightFile(), dialog.getRightMember());

                                    if (rightMember != null) {
                                        rseRightMember = new RSEMember(rightMember);
                                    }

                                    CompareAction action = new CompareAction(editable, considerDate, threeWay, rseAncestorMember, rseLeftMember,
                                        rseRightMember, null);
                                    action.run();

                                }

                            } catch (Exception e) {
                            }

                        }
                    } catch (SystemMessageException e) {
                    }

                }
            });
        }

    }

    public SessionsPanel(TN5250JInfo tn5250jInfo, Session session, Shell shell) {
        super(tn5250jInfo, session, shell);
    }

    @Override
    public void onScreenChanged(int arg0, int arg1, int arg2, int arg3, int arg4) {
        if (arg0 == 1) {
            if (String.copyValueOf(getSession5250().getScreen().getScreenAsChars(), 2, 14).equals("TN5250J-EDITOR")) {
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
                    new OpenLpexAsync(library, sourceFile, member, mode, currentLibrary, libraryList.toString()).start();
                }
                getSessionGUI().getScreen().sendKeys("[pf3]");
            } else if (String.copyValueOf(getSession5250().getScreen().getScreenAsChars(), 32, 15).equals("TN5250J-COMPARE")) {
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
                    new OpenCompareAsync(library, sourceFile, member).start();
                }
                getSessionGUI().getScreen().sendKeys("[pf3]");
            }
        }
    }

    @Override
    public TN5250JGUI getTN5250JGUI(TN5250JInfo tn5250jInfo, Session5250 session5250) {
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

}
