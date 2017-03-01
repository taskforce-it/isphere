package biz.isphere.lpex.comments.lpex.action;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import biz.isphere.lpex.comments.lpex.delegates.ICommentDelegate;
import biz.isphere.lpex.comments.lpex.internal.Position;

import com.ibm.lpex.core.LpexAction;
import com.ibm.lpex.core.LpexView;

public abstract class AbstractLpexAction implements LpexAction {

    private Position cursorPosition;

    public boolean available(LpexView view) {
        return isEditMode(view);
    }

    public void doAction(LpexView view) {

        System.out.println("Auto check: " + view.queryOn("query autoCheck"));

        try {
            saveCursorPosition(view);

            Position start;
            Position end;
            if (anythingSelected(view)) {
                start = new Position(view.queryInt("block.topElement"), view.queryInt("block.topPosition"));
                end = new Position(view.queryInt("block.bottomElement"), view.queryInt("block.bottomPosition"));
            } else {
                start = new Position(view.queryInt("line"), view.queryInt("position"));
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

    protected abstract ICommentDelegate getDelegate(LpexView view);

    protected abstract void doLines(LpexView view, int firstLine, int lastLine);

    protected abstract void doSelection(LpexView view, int line, int startColumn, int endColumn);

    protected boolean isEditMode(LpexView view) {
        return !view.queryOn("readonly");
    }

    protected boolean isTextLine(LpexView view, int element) {
        return !view.show(element);
    }

    protected boolean anythingSelected(LpexView view) {
        return view.queryOn("block.anythingSelected");
    }

    protected String getMemberType() {

        IEditorInput editorInput = getActiveEditor().getEditorInput();
        if (editorInput instanceof FileEditorInput) {
            FileEditorInput fileEditorInput = (FileEditorInput)editorInput;
            return fileEditorInput.getFile().getFileExtension();
        }

        return null;
    }

    protected int getLineLength(LpexView view) {
        return view.queryInt("length");
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
        cursorPosition = new Position(view.queryInt("cursorRow"), view.queryInt("displayPosition"));
    }

    private void restoreCursorPosition(LpexView view) {
        view.doCommand("set cursorRow " + cursorPosition.getLine());
        view.doCommand("set position " + cursorPosition.getColumn());
    }
}
