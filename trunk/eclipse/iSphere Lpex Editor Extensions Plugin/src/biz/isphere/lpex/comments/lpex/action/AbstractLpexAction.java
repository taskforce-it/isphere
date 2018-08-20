/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.comments.lpex.action;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import biz.isphere.lpex.comments.lpex.internal.Position;

import com.ibm.lpex.core.LpexAction;
import com.ibm.lpex.core.LpexView;

public abstract class AbstractLpexAction implements LpexAction {

    private Position cursorPosition;

    public boolean available(LpexView view) {
        return isEditMode(view);
    }

    protected abstract void doLines(LpexView view, int firstLine, int lastLine);

    protected abstract void doSelection(LpexView view, int line, int startColumn, int endColumn);

    protected boolean isEditMode(LpexView view) {
        return !view.queryOn("readonly"); //$NON-NLS-1$
    }

    protected boolean isTextLine(LpexView view, int element) {
        return !view.show(element);
    }

    protected boolean anythingSelected(LpexView view) {
        return view.queryOn("block.anythingSelected"); //$NON-NLS-1$
    }

    protected String getMemberType() {

        IEditorInput editorInput = getActiveEditor().getEditorInput();
        if (editorInput instanceof FileEditorInput) {
            FileEditorInput fileEditorInput = (FileEditorInput)editorInput;
            return fileEditorInput.getFile().getFileExtension();
        }

        return null;
    }

    protected String getElementText(LpexView view, int element) {
        // return the r-trimmed text
        return view.elementText(element).replaceAll("\\s+$", ""); //$NON-NLS-1$ //$NON-NLS-2$
    }

    protected void goStartOfText(LpexView view, int element) {

        String text = view.elementText(element);
        int length = 0;
        while (length <= text.length() && " ".equals(text.substring(length, length + 1))) { //$NON-NLS-1$
            length++;
        }

        length++;

        view.doCommand("set position " + length); //$NON-NLS-1$
    }

    protected int getLineLength(LpexView view) {
        return view.queryInt("length"); //$NON-NLS-1$
    }

    protected Shell getShell() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    }

    protected IEditorPart getActiveEditor() {

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            IWorkbenchPage activePage = window.getActivePage();
            if (activePage != null) {
                return activePage.getActiveEditor();
            }
        }

        return null;
    }

    protected static String getLPEXMenuAction(String label, String id) {
        return "\"" + label + "\" " + id; //$NON-NLS-1$ //$NON-NLS-2$
    }

    protected void displayMessage(LpexView view, String text) {
        view.doCommand("set messageText " + text); //$NON-NLS-1$
    }

    protected void saveCursorPosition(LpexView view) {
        cursorPosition = new Position(view.queryInt("cursorRow"), view.queryInt("displayPosition")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    protected void restoreCursorPosition(LpexView view) {
        view.doCommand("set cursorRow " + cursorPosition.getLine()); //$NON-NLS-1$
        view.doCommand("set position " + cursorPosition.getColumn()); //$NON-NLS-1$
    }

    protected int getBlockTopElement(LpexView view) {
        return view.queryInt("block.topElement"); //$NON-NLS-1$
    }

    protected int getBlockTopPosition(LpexView view) {
        return view.queryInt("block.topPosition"); //$NON-NLS-1$
    }

    protected int getBlockBottomElement(LpexView view) {
        return view.queryInt("block.bottomElement"); //$NON-NLS-1$
    }

    protected int getBLockBottomPosition(LpexView view) {
        return view.queryInt("block.bottomPosition"); //$NON-NLS-1$
    }

    protected int getCurrentElement(LpexView view) {
        return view.queryInt("element"); //$NON-NLS-1$
    }

    protected int getCurrentPosition(LpexView view) {
        return view.queryInt("position"); //$NON-NLS-1$
    }
}
