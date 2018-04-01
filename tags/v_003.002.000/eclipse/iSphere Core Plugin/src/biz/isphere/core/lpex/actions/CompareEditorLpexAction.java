/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.lpex.actions;

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

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.Member;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.internal.IProjectMember;
import biz.isphere.core.lpex.RemoteSourceLocation;

import com.ibm.lpex.core.LpexAction;
import com.ibm.lpex.core.LpexView;

/**
 * This action adds or changes the creation command of an existing (change) or
 * non-existing (add) STRPREPRC header.
 */
public class CompareEditorLpexAction implements LpexAction {

    public static final String ID = "iSphere.Core.CompareSourceMember";

    public void doAction(LpexView view) {

        try {

            Member member = null;

            String libraryName = null;
            String fileName = null;
            String memberName = null;

            String sourceName = view.query("sourceName");
            if (sourceName != null) {

                RemoteSourceLocation remoteSourceLocation = new RemoteSourceLocation(sourceName);
                libraryName = remoteSourceLocation.getLibraryName();
                fileName = remoteSourceLocation.getFileName();
                memberName = remoteSourceLocation.getMemberName();

                String documentName = view.query("name").replace(File.separatorChar, IPath.SEPARATOR);
                IEditorPart editor = findEditor(documentName);
                String connectionName = IBMiHostContributionsHandler.getConnectionName(editor);

                if (isValidated(connectionName, libraryName, fileName, memberName)) {
                    member = IBMiHostContributionsHandler.getMember(connectionName, libraryName, fileName, memberName);
                }

            } else {

                String documentName = view.query("name").replace(File.separatorChar, IPath.SEPARATOR);
                IEditorPart editor = findEditor(documentName);
                FileEditorInput editorInput = getEditorInput(editor);
                IFile file = editorInput.getFile();
                member = new IProjectMember(file);

                libraryName = member.getLibrary();
                fileName = member.getSourceFile();
                memberName = member.getMember();

            }

            if (member != null) {

                String connectionName = member.getConnection();

                if (view.queryOn("dirty")) {
                    if (MessageDialog.openQuestion(getShell(), Messages.Question,
                        Messages.bind(Messages.Source_member_contains_unsaved_changes_Save_member_A, memberName))) {
                        view.doCommand("save");
                    }
                }

                ArrayList<Member> members = new ArrayList<Member>();
                members.add(member);
                IBMiHostContributionsHandler.compareSourceMembers(connectionName, members, false);

            } else {

                MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                    Messages.bind(Messages.Could_not_download_member_2_of_file_1_of_library_0, new Object[] { libraryName, fileName, memberName }));

            }

        } catch (Throwable e) {
            //            ISpherePlugin.logError("*** Unexpected error when attempting to start the iSphere compare editor ***", e); //$NON-NLS-1$
            MessageDialogAsync.displayError(getShell(), ExceptionHelper.getLocalizedMessage(e));
        }
    }

    private FileEditorInput getEditorInput(IEditorPart editor) {

        if (editor.getEditorInput() instanceof FileEditorInput) {
            return (FileEditorInput)editor.getEditorInput();
        }

        return null;
    }

    private boolean isValidated(String connectionName, String libraryName, String fileName, String memberName) {

        if (StringHelper.isNullOrEmpty(connectionName)) {
            return false;
        }

        if (StringHelper.isNullOrEmpty(libraryName)) {
            return false;
        }

        if (StringHelper.isNullOrEmpty(fileName)) {
            return false;
        }

        if (StringHelper.isNullOrEmpty(memberName)) {
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
}
