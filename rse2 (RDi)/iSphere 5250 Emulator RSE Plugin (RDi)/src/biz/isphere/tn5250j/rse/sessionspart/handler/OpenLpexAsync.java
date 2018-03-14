/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.sessionspart.handler;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.ibm.etools.iseries.rse.ui.resources.QSYSEditableRemoteSourceFileMember;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.tn5250j.rse.Messages;
import biz.isphere.tn5250j.rse.sessionspart.SessionsInfo;

public class OpenLpexAsync extends AbstractAsyncHandler {

    private String library;
    private String sourceFile;
    private String member;
    private String mode;
    private String currentLibrary;
    private String libraryList;

    public OpenLpexAsync(Shell shell, SessionsInfo sessionsInfo, String library, String sourceFile, String member, String mode, String currentLibrary,
        String libraryList) {
        super(shell, sessionsInfo);

        this.library = library;
        this.sourceFile = sourceFile;
        this.member = member;
        this.mode = mode;
        this.currentLibrary = currentLibrary;
        this.libraryList = libraryList;
    }

    @Override
    protected String getCurrentLibrary() {
        return currentLibrary;
    }

    public void runInternally() {

        try {

            IQSYSMember iseriesMember = getConnection().getMember(library, sourceFile, member, null);
            if (iseriesMember != null) {

                String editor = "com.ibm.etools.systems.editor";
                String _editor = null;
                if (iseriesMember.getType().equals("DSPF") || iseriesMember.getType().equals("MNUDDS")) {
                    _editor = "Screen Designer";
                } else if (iseriesMember.getType().equals("PRTF")) {
                    _editor = "Report Designer";
                }

                if (_editor != null) {
                    MessageDialog dialog = new MessageDialog(getShell(), Messages.Choose_Editor, null,
                        Messages.Please_choose_the_editor_for_the_source_member, MessageDialog.INFORMATION, new String[] { _editor, "LPEX Editor" },
                        0);

                    final int dialogResult = dialog.open();
                    if (dialogResult == 0) {

                        if (iseriesMember.getType().equals("DSPF") || iseriesMember.getType().equals("MNUDDS")) {
                            editor = "com.ibm.etools.iseries.dds.tui.editor.ScreenDesigner";
                        } else if (iseriesMember.getType().equals("PRTF")) {
                            editor = "com.ibm.etools.iseries.dds.tui.editor.ReportDesigner";
                        }
                    }
                }

                QSYSEditableRemoteSourceFileMember editable = new QSYSEditableRemoteSourceFileMember(iseriesMember);
                if (mode.equals("*OPEN")) {
                    editable.open(getShell(), false, editor);
                } else {
                    editable.open(getShell(), true, editor);
                }
            } else {
                ISpherePlugin.logError("*** Could not load member " + member + " of file " + library + "/" + sourceFile + " ***", null);
            }
        } catch (Throwable e) {
        }
    }
}
