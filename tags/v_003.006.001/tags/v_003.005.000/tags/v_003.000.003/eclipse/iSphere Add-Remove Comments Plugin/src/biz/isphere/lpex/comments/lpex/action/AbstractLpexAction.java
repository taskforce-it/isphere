package biz.isphere.lpex.comments.lpex.action;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import biz.isphere.lpex.comments.lpex.delegates.CLCommentsDelegate;
import biz.isphere.lpex.comments.lpex.delegates.DDSCommentsDelegate;
import biz.isphere.lpex.comments.lpex.delegates.ICommentDelegate;
import biz.isphere.lpex.comments.lpex.delegates.PNLGRPCommentsDelegate;
import biz.isphere.lpex.comments.lpex.delegates.RPGCommentsDelegate;
import biz.isphere.lpex.comments.lpex.exceptions.MemberTypeNotSupportedException;
import biz.isphere.lpex.comments.lpex.internal.Position;

import com.ibm.lpex.core.LpexAction;
import com.ibm.lpex.core.LpexView;

public abstract class AbstractLpexAction implements LpexAction {

    private Position cursorPosition;

    public boolean available(LpexView view) {
        return isEditMode(view);
    }

    public void doAction(LpexView view) {

        try {
            saveCursorPosition(view);

            Position start;
            Position end;
            if (anythingSelected(view)) {
                start = new Position(view.queryInt("block.topElement"), view.queryInt("block.topPosition")); //$NON-NLS-1$ //$NON-NLS-2$
                end = new Position(view.queryInt("block.bottomElement"), view.queryInt("block.bottomPosition")); //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                start = new Position(view.queryInt("element"), view.queryInt("position")); //$NON-NLS-1$ //$NON-NLS-2$
                end = start;
            }

            // Range of lines
            if (start.getLine() < end.getLine()) {
                doLines(view, start.getLine(), end.getLine());
            } else if (start.getLine() == end.getLine()) {
                // Single line
                if (start.getColumn() == end.getColumn()) {
                    doLines(view, start.getLine(), end.getLine());
                } else if (start.getColumn() < end.getColumn()) {
                    // Selection
                    doSelection(view, start.getLine(), start.getColumn(), end.getColumn());
                }
            }

        } finally {
            restoreCursorPosition(view);
        }
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

    protected ICommentDelegate getDelegate(LpexView view) throws MemberTypeNotSupportedException {

        String type = getMemberType();
        if ("CLP".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new CLCommentsDelegate(view);
        } else if ("CLLE".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new CLCommentsDelegate(view);
        } else if ("RPG".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new RPGCommentsDelegate(view);
        } else if ("RPGLE".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new RPGCommentsDelegate(view);
        } else if ("SQLRPG".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new RPGCommentsDelegate(view);
        } else if ("SQLRPGLE".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new RPGCommentsDelegate(view);
        } else if ("PRTF".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new DDSCommentsDelegate(view);
        } else if ("DSPF".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new DDSCommentsDelegate(view);
        } else if ("LF".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new DDSCommentsDelegate(view);
        } else if ("PF".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new DDSCommentsDelegate(view);
        } else if ("PNLGRP".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new PNLGRPCommentsDelegate(view);
        }

        throw new MemberTypeNotSupportedException();
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
        return view.elementText(element).replaceAll("\\s+$", "");
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

    private void saveCursorPosition(LpexView view) {
        cursorPosition = new Position(view.queryInt("cursorRow"), view.queryInt("displayPosition")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void restoreCursorPosition(LpexView view) {
        view.doCommand("set cursorRow " + cursorPosition.getLine()); //$NON-NLS-1$
        view.doCommand("set position " + cursorPosition.getColumn()); //$NON-NLS-1$
    }
}
