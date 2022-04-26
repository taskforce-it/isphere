/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.internal.IEditor;
import biz.isphere.rse.Messages;

import com.ibm.etools.iseries.rse.ui.resources.QSYSEditableRemoteSourceFileMember;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.systems.editor.SystemTextEditor;

public class Editor implements IEditor {

    public void openEditor(Object connection, String library, String file, String member, int statement, String mode) {

        if (connection instanceof IBMiConnection) {

            IBMiConnection _connection = (IBMiConnection)connection;

            try {

                IQSYSMember _member = _connection.getMember(library, file, member, null);

                if (_member != null) {

                    Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

                    String editor = "com.ibm.etools.systems.editor";

                    if (statement == 0) {

                        String _editor = null;
                        if (_member.getType().equals("DSPF") || _member.getType().equals("MNUDDS")) {
                            _editor = "Screen Designer";
                        } else if (_member.getType().equals("PRTF")) {
                            _editor = "Report Designer";
                        }

                        if (_editor != null) {

                            MessageDialog dialog = new MessageDialog(shell, Messages.Choose_Editor, null,
                                Messages.Please_choose_the_editor_for_the_source_member, MessageDialog.INFORMATION, new String[] { _editor,
                                    "LPEX Editor" }, 0);

                            final int dialogResult = dialog.open();

                            if (dialogResult == 0) {

                                if (_member.getType().equals("DSPF") || _member.getType().equals("MNUDDS")) {
                                    editor = "com.ibm.etools.iseries.dds.tui.editor.ScreenDesigner";
                                } else if (_member.getType().equals("PRTF")) {
                                    editor = "com.ibm.etools.iseries.dds.tui.editor.ReportDesigner";
                                }

                            }

                        }

                    }

                    QSYSEditableRemoteSourceFileMember mbr = new QSYSEditableRemoteSourceFileMember(_member);

                    if (mbr != null) {

                        if (mode.equals(IEditor.EDIT)) {
                            mbr.open(editor, false, null);
                        } else if (mode.equals(IEditor.BROWSE)) {
                            mbr.open(editor, true, null);
                        }

                        if (statement != 0) {
                            if (!mbr.openIsCanceled()) {
                                SystemTextEditor systemTextEditor = mbr.getEditor();
                                if (systemTextEditor != null) {
                                    systemTextEditor.gotoLine(statement);
                                }
                            }
                        }

                    }

                }

            }

            catch (SystemMessageException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

}
