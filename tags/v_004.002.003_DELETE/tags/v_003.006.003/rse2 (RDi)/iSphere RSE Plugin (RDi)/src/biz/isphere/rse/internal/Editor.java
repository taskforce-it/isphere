/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.IEditor;
import biz.isphere.rse.Messages;

import com.ibm.etools.iseries.rse.ui.resources.QSYSEditableRemoteSourceFileMember;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.systems.editor.SystemTextEditor;

public class Editor implements IEditor {

    public void openEditor(String connectionName, String library, String file, String member, int statement, String mode) {

        IBMiConnection _connection = IBMiConnection.getConnection(connectionName);

        try {

            IQSYSMember _member = _connection.getMember(library, file, member, null);
            if (_member != null) {

                String editor = "com.ibm.etools.systems.editor";

                QSYSEditableRemoteSourceFileMember mbr = new QSYSEditableRemoteSourceFileMember(_member);
                if (statement == 0 && !isOpenInEditor(mbr)) {

                    String _editor = null;
                    if (_member.getType().equals("DSPF") || _member.getType().equals("MNUDDS")) {
                        _editor = "Screen Designer";
                    } else if (_member.getType().equals("PRTF")) {
                        _editor = "Report Designer";
                    }

                    if (_editor != null) {

                        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                        MessageDialog dialog = new MessageDialog(shell, Messages.Choose_Editor, null,
                            Messages.Please_choose_the_editor_for_the_source_member, MessageDialog.INFORMATION,
                            new String[] { _editor, "LPEX Editor" }, 0);

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

                if (mbr != null) {

                    if (!isOpenInEditor(mbr)) {
                        if (mode.equals(IEditor.EDIT)) {
                            mbr.open(editor, false, null);
                        } else if (mode.equals(IEditor.BROWSE)) {
                            mbr.open(editor, true, null);
                        }
                    } else {
                        /*
                         * Hack, to keep the editor read-only due to a bug in
                         * QSYSEditableRemoteSourceFileMember.internalOpen
                         * (String strEditorID, boolean readOnly, int
                         * lineNumber, IProgressMonitor monitor).
                         */
                        IEditorPart editorPart = findEditorPart(mbr);
                        mbr.setEditor(editorPart);
                    }

                    if (statement != 0) {
                        if (!mbr.openIsCanceled()) {
                            SystemTextEditor systemTextEditor = mbr.getEditor();
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(mbr.getEditorPart());
                            if (systemTextEditor != null) {
                                systemTextEditor.gotoLine(statement);
                            }
                        }
                    }

                }

            }

        }

        catch (Throwable e) {
            ISpherePlugin.logError("Failed to open Lpex editor.", e); //$NON-NLS-1$
        }

    }

    private boolean isOpenInEditor(QSYSEditableRemoteSourceFileMember mbr) throws CoreException {
        return !(mbr.checkOpenInEditor() == -1);
    }

    private IEditorPart findEditorPart(QSYSEditableRemoteSourceFileMember member) {

        IFile localFileResource = member.getLocalResource();
        if (localFileResource == null) {
            return null;
        }

        // See:
        // http://stackoverflow.com/questions/516704/enumerating-all-my-eclipse-editors
        for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
            for (IWorkbenchPage page : window.getPages()) {
                for (IEditorReference editor : page.getEditorReferences()) {
                    IEditorPart part = editor.getEditor(false);
                    if (part != null) {
                        IEditorInput input = part.getEditorInput();
                        if (input instanceof IFileEditorInput) {
                            IFileEditorInput fileInput = (IFileEditorInput)input;
                            if (localFileResource.equals(fileInput.getFile())) {
                                return part;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

}
