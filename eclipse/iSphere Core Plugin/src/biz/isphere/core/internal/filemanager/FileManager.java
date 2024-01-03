/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.filemanager;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.preferences.DoNotAskMeAgainDialog;
import biz.isphere.core.preferences.Preferences;

public final class FileManager {

    private FileManager() {
    }

    public static void askAndOpenSavedFile(Shell shell, String file) {

        boolean doOpenFile = Preferences.getInstance().isOpenFilesAfterSaving();
        String message = "File has been saved successfully.\nWould you like to open it?";
        if (doOpenFile && MessageDialog.openQuestion(shell, Messages.Question, message)) {
            askAndOpenFileInternally(shell, file);
        }

    }

    public static void askAndOpenSavedFile(Shell shell, String doNotAskMeAgainKey, String file) {

        boolean doOpenFile = DoNotAskMeAgainDialog.openSavedFileQuestion(shell, doNotAskMeAgainKey);
        if (doOpenFile) {
            askAndOpenFileInternally(shell, file);
        }
    }

    private static void askAndOpenFileInternally(Shell shell, String file) {

        IFileStore fileStore = EFS.getLocalFileSystem().getStore(new File(file).toURI());
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

        try {
            IEditorPart editor = IDE.openEditorOnFileStore(page, fileStore);
            System.out.println("editor=" + editor);
        } catch (PartInitException e) {
            String message = ExceptionHelper.getLocalizedMessage(e);
            MessageDialog.openError(shell, Messages.E_R_R_O_R, message);
        }
    }

}
