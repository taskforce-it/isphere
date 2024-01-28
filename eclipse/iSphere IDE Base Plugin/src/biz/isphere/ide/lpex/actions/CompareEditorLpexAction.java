/*******************************************************************************
 *   Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.ide.lpex.actions;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.ibm.lpex.core.LpexAction;
import com.ibm.lpex.core.LpexView;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.IProjectMember;
import biz.isphere.core.internal.LocalSourceLocation;
import biz.isphere.core.internal.Member;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.internal.StreamFile;
import biz.isphere.ide.Messages;
import biz.isphere.ide.lpex.RemoteSourceLocation;

/**
 * This action adds or changes the creation command of an existing (change) or
 * non-existing (add) STRPREPRC header.
 */
public class CompareEditorLpexAction implements LpexAction {

    // ID that had been assigned, when this class belonged to the iSphere Core
    // plug-in.
    public static final String ID = "iSphere.Core.CompareSourceMember";

    private static final String EMPTY = ""; //$NON-NLS-1$

    public void doAction(LpexView view) {

        try {

            Member member = null;

            String libraryName = null;
            String fileName = null;
            String memberName = null;

            //@formatter:off
            /*
             * RSE Member Filter:
             * name: C:\workspaces\rdp_096\runtime-EclipseApplication-en\RemoteSystemsTempFiles\WWSENT.DE.OBI.NET\QSYS.LIB\ISPHEREDVP.LIB\QRPGLESRC.FILE\DEMO1.RPGLE
             * source name: WWSENT.DE.OBI.NET:ISPHEREDVP/QRPGLESRC(DEMO1)
             * file input: org.eclipse.ui.part.FileEditorInput(/RemoteSystemsTempFiles/WWSENT.DE.OBI.NET/QSYS.LIB/ISPHEREDVP.LIB/QRPGLESRC.FILE/DEMO1.RPGLE)
             *  getPath(): C:/workspaces/rdp_096/runtime-EclipseApplication-en/RemoteSystemsTempFiles/WWSENT.DE.OBI.NET/QSYS.LIB/ISPHEREDVP.LIB/QRPGLESRC.FILE/DEMO1.RPGLE
             *  getFile(): L/RemoteSystemsTempFiles/WWSENT.DE.OBI.NET/QSYS.LIB/ISPHEREDVP.LIB/QRPGLESRC.FILE/DEMO1.RPGLE
             *
             * IFS Filter:
             * name: C:\workspaces\rdp_096\runtime-EclipseApplication-en\RemoteSystemsTempFiles\WWSENT.DE.OBI.NET\home\raddatz\iSphere\QRPGLESRC\DEMO2.RPGLE
             * source name: /home/raddatz/iSphere/QRPGLESRC/DEMO2.RPGLE
             * file input: org.eclipse.ui.part.FileEditorInput(/RemoteSystemsTempFiles/WWSENT.DE.OBI.NET/home/raddatz/iSphere/QRPGLESRC/DEMO2.RPGLE)
             *  getPath(): C:/workspaces/rdp_096/runtime-EclipseApplication-en/RemoteSystemsTempFiles/WWSENT.DE.OBI.NET/home/raddatz/iSphere/QRPGLESRC/DEMO2.RPGLE
             *  getFile(): L/RemoteSystemsTempFiles/WWSENT.DE.OBI.NET/home/raddatz/iSphere/QRPGLESRC/DEMO2.RPGLE
             * 
             * i Project:
             * name: C:\workspaces\rdp_096\runtime-EclipseApplication-en\iSphere\QRPGLESRC\DEMO3.RPGLE
             * source name: null
             * file input: org.eclipse.ui.part.FileEditorInput(/iSphere/QRPGLESRC/DEMO3.RPGLE)
             * 
             */
            //@formatter:on

            String sourceName = view.query("sourceName");
            String documentName = view.query("name").replace(File.separatorChar, IPath.SEPARATOR);
            IEditorPart editor = findEditor(documentName);

            if (sourceName != null) {

                if (documentName.indexOf("/QSYS.LIB/") >= 0) {

                    // Remote Systems Member Filter
                    RemoteSourceLocation remoteSourceLocation = new RemoteSourceLocation(sourceName);
                    libraryName = remoteSourceLocation.getLibraryName();
                    fileName = remoteSourceLocation.getFileName();
                    memberName = remoteSourceLocation.getMemberName();
                    String connectionName = IBMiHostContributionsHandler.getConnectionName(editor);

                    if (isValidatedSourceMember(connectionName, libraryName, fileName, memberName)) {
                        member = IBMiHostContributionsHandler.getMember(connectionName, libraryName, fileName, memberName);
                        startSourceMemberCompareEditor(view, member);
                        return;
                    }

                } else {

                    // IFS Stream File Filter
                    String connectionName = IBMiHostContributionsHandler.getConnectionName(editor);

                    if (isValidatedStreamFile(connectionName, sourceName)) {
                        StreamFile streamFile = IBMiHostContributionsHandler.getStreamFile(connectionName, sourceName);
                        startStreamFileCompareEditor(view, streamFile);
                    }

                    return;

                }

            } else {

                // i Project
                FileEditorInput editorInput = getEditorInput(editor);
                IFile file = editorInput.getFile();
                LocalSourceLocation localSourceLocation = new LocalSourceLocation(file.getFullPath().toString());
                fileName = localSourceLocation.getFileName();
                libraryName = localSourceLocation.getLibraryName();
                memberName = localSourceLocation.getMemberName();
                String connectionName = IBMiHostContributionsHandler.getConnectionName(editor);

                if (isValidatedIProjectMember(connectionName, libraryName, fileName, memberName)) {
                    member = new IProjectMember(file);
                    startSourceMemberCompareEditor(view, member);
                    return;
                }

            }

            // MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
            // Messages.bind(Messages.Could_not_download_member_2_of_file_1_of_library_0,
            // new Object[] { libraryName, fileName, memberName }));

        } catch (Throwable e) {
            MessageDialogAsync.displayNonBlockingError(getShell(), ExceptionHelper.getLocalizedMessage(e));
        }
    }

    private void startStreamFileCompareEditor(LpexView view, StreamFile streamFile) throws Exception {

        String connectionName = streamFile.getConnection();
        if (StringHelper.isNullOrEmpty(connectionName)) {
            displayError(Messages.bind(Messages.Could_not_get_RSE_connection_A, "[null]"));
            return;
        }

        if (view.queryOn("dirty")) {
            if (askQuestion(Messages.bind(Messages.Source_member_contains_unsaved_changes_Save_member_A, streamFile.getStreamFile()))) {
                view.doCommand("save");
            }
        }

        ArrayList<StreamFile> streamFiles = new ArrayList<StreamFile>();
        streamFiles.add(streamFile);
        IBMiHostContributionsHandler.compareStreamFiles(connectionName, streamFiles, false);
    }

    private void startSourceMemberCompareEditor(LpexView view, Member member) throws Exception {

        String connectionName = member.getConnection();
        if (StringHelper.isNullOrEmpty(connectionName)) {
            displayError(Messages.bind(Messages.Could_not_get_RSE_connection_A, "[null]"));
            return;
        }

        if (view.queryOn("dirty")) {
            if (askQuestion(Messages.bind(Messages.Source_member_contains_unsaved_changes_Save_member_A, member.getMember()))) {
                view.doCommand("save");
            }
        }

        ArrayList<Member> members = new ArrayList<Member>();
        members.add(member);
        IBMiHostContributionsHandler.compareSourceMembers(connectionName, members, false);
    }

    private FileEditorInput getEditorInput(IEditorPart editor) {

        if (editor.getEditorInput() instanceof FileEditorInput) {
            return (FileEditorInput)editor.getEditorInput();
        }

        return null;
    }

    private boolean isValidatedStreamFile(String connectionName, String streamFileName) {

        if (StringHelper.isNullOrEmpty(connectionName)) {
            displayError(Messages.bind(Messages.Connection_A_not_found, EMPTY));
            return false;
        }

        if (StringHelper.isNullOrEmpty(streamFileName)) {
            displayError(Messages.bind(Messages.Stream_file_A_not_found, EMPTY));
            return false;
        }

        return true;
    }

    private boolean isValidatedIProjectMember(String connectionName, String libraryName, String fileName, String memberName) {

        if (StringHelper.isNullOrEmpty(connectionName) && StringHelper.isNullOrEmpty(libraryName)) {
            displayError(Messages.i_Project_connection_and_library_not_set);
            return false;
        }

        if (StringHelper.isNullOrEmpty(connectionName)) {
            displayError(Messages.bind(Messages.Connection_A_not_found, EMPTY));
            return false;
        }

        if (StringHelper.isNullOrEmpty(libraryName)) {
            displayError(Messages.bind(Messages.Library_A_not_found, EMPTY));
            return false;
        }

        if (StringHelper.isNullOrEmpty(fileName)) {
            displayError(Messages.bind(Messages.File_A_not_found, EMPTY));
            return false;
        }

        if (StringHelper.isNullOrEmpty(memberName)) {
            displayError(Messages.bind(Messages.Member_A_not_found, EMPTY));
            return false;
        }

        return true;
    }

    private boolean isValidatedSourceMember(String connectionName, String libraryName, String fileName, String memberName) {

        if (StringHelper.isNullOrEmpty(connectionName)) {
            displayError(Messages.bind(Messages.Connection_A_not_found, EMPTY));
            return false;
        }

        if (StringHelper.isNullOrEmpty(libraryName)) {
            displayError(Messages.bind(Messages.Library_A_not_found, EMPTY));
            return false;
        }

        if (StringHelper.isNullOrEmpty(fileName)) {
            displayError(Messages.bind(Messages.File_A_not_found, EMPTY));
            return false;
        }

        if (StringHelper.isNullOrEmpty(memberName)) {
            displayError(Messages.bind(Messages.Member_A_not_found, EMPTY));
            return false;
        }

        return true;
    }

    private IEditorPart findEditor(String documentName) {

        for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
            for (IWorkbenchPage page : window.getPages()) {
                for (IEditorReference editorReferences : page.getEditorReferences()) {
                    IEditorPart editor = editorReferences.getEditor(false);
                    if (editor instanceof IEditorPart) {
                        IEditorInput editorInput = editorReferences.getEditor(false).getEditorInput();
                        if (editorInput instanceof FileEditorInput) {
                            FileEditorInput fileEditorInput = (FileEditorInput)editorInput;
                            String editorFullPath = fileEditorInput.getFile().getFullPath().makeAbsolute().toString();
                            if (documentName.endsWith(editorFullPath)) {
                                return editor;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }

    public boolean available(LpexView view) {

        // if (view.query("sourceName") != null) {
        // return true;
        // }
        //
        // return false;
        return true;
    }

    public static String getLPEXMenuAction() {
        return "\"" + Messages.Menu_Compare + "\" " + CompareEditorLpexAction.ID; //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void displayError(String message) {
        MessageDialog.openError(getShell(), Messages.E_R_R_O_R, message);
    }

    private boolean askQuestion(String question) {
        return MessageDialog.openQuestion(getShell(), Messages.Question, question);
    }
}
