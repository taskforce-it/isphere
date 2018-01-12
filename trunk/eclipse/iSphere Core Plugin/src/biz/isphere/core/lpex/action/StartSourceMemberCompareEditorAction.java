/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.lpex.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.Member;
import biz.isphere.core.lpex.RemoteSourceLocation;

import com.ibm.lpex.core.LpexAction;
import com.ibm.lpex.core.LpexView;

/**
 * This action adds or changes the creation command of an existing (change) or
 * non-existing (add) STRPREPRC header.
 */
public class StartSourceMemberCompareEditorAction implements LpexAction {

    public static final String ID = "iSphere.Core.CompareSourceMember";

    public void doAction(LpexView view) {

        try {

            String fullPath = view.query("name").replace(File.separatorChar, IPath.SEPARATOR);

            List<IEditorReference> editors = new ArrayList<IEditorReference>();
            for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
                for (IWorkbenchPage page : window.getPages()) {
                    for (IEditorReference editorReferences : page.getEditorReferences()) {
                        IEditorPart editor = editorReferences.getEditor(false);
                        if (editor instanceof IEditorPart) {
                            IEditorInput editorInput = editorReferences.getEditor(false).getEditorInput();
                            if (editorInput instanceof FileEditorInput) {
                                FileEditorInput fileEditorInput = (FileEditorInput)editorInput;
                                String editorFullPath = fileEditorInput.getFile().getFullPath().makeAbsolute().toString();
                                if (fullPath.endsWith(editorFullPath)) {
                                    editors.add(editorReferences);
                                    String connectionName = IBMiHostContributionsHandler.getConnectionName(editor);
                                    RemoteSourceLocation remoteSourceLocation = new RemoteSourceLocation(view.query("sourceName"));
                                    Member member = IBMiHostContributionsHandler.getMember(connectionName, remoteSourceLocation.getLibraryName(),
                                        remoteSourceLocation.getFileName(), remoteSourceLocation.getMemberName());
                                    if (member != null) {

                                        if (view.queryOn("dirty")) {
                                            if (MessageDialog.openQuestion(
                                                getShell(),
                                                Messages.Question,
                                                Messages.bind(Messages.Source_member_contains_unsaved_changes_Save_member_A,
                                                    remoteSourceLocation.getMemberName()))) {
                                                view.doCommand("save");
                                            }
                                        }

                                        ArrayList<Member> members = new ArrayList<Member>();
                                        members.add(member);
                                        IBMiHostContributionsHandler.compareSourceMembers(connectionName, members, false);

                                        return;

                                    } else {

                                        MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(
                                            Messages.Unable_to_start_source_member_compare_Could_not_find_editor_with_source_member_A,
                                            remoteSourceLocation.getMemberName()));

                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Unexpected error when attempting to start the iSphere compare editor ***", e); //$NON-NLS-1$
        }
    }

    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }

    public boolean available(LpexView view) {

        if (view.query("sourceName") != null) {
            return true;
        }

        return false;
    }

    public static String getLPEXMenuAction() {
        return "\"" + Messages.Menu_Compare + "\" " + StartSourceMemberCompareEditorAction.ID; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
