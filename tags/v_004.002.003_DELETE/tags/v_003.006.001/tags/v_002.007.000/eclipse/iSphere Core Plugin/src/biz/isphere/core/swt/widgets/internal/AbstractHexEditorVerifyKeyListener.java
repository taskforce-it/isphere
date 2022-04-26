/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractHexEditorVerifyKeyListener implements VerifyKeyListener {

    protected static final int CURSOR_RIGHT = -1;
    protected static final int CURSOR_LEFT = -2;
    protected static final int CURSOR_UP = -3;
    protected static final int CURSOR_DOWN = -4;
    protected static final int CURSOR_HOME = -5;
    protected static final int CURSOR_END = -6;
    protected static final int CURSOR_HOME_OF_LINE = -7;
    protected static final int CURSOR_END_OF_LINE = -8;
    protected static final int CURSOR_PAGE_UP = -9;
    protected static final int CURSOR_PAGE_DOWN = -10;

    private HexEditorInternal swtHexEdit;
    private HexEditorControlManager controlManager;

    public AbstractHexEditorVerifyKeyListener(HexEditorInternal swtHexEdit, HexEditorControlManager hexCursorPositioner) {

        this.swtHexEdit = swtHexEdit;
        this.controlManager = hexCursorPositioner;
    }

    public void verifyKey(VerifyEvent event) {

        // no action
        event.doit = false;

        switch (event.keyCode) {
        case SWT.TAB:
            handleTraversal(event);
            ;
        }

        if (performToggleInsertMode(event)) {
            return;
        }

        if (performCopyPaste(event)) {
            return;
        }

        if (performDelete(event)) {
            return;
        }

        if (performCursorMovement(event)) {
            return;
        }

        performEdit(event);
    }

    protected abstract void handleTraversal(VerifyEvent event);

    protected boolean performToggleInsertMode(VerifyEvent event) {

        if (isShiftKey(event) || isControlKey(event)) {
            return false;
        }

        if (event.keyCode == SWT.INSERT) {
            swtHexEdit.setInsertMode(!swtHexEdit.isInsertMode());
            return true;
        }

        return false;
    }

    protected boolean performCopyPaste(VerifyEvent event) {

        // Cut: Ctrl+X
        if (isControlKey(event) && event.character == 24) {

            if (swtHexEdit.copySelectionToClipboard()) {
                swtHexEdit.deleteSelection();
            }

            return true;
        }

        // Copy: Ctrl+C || Ctrl+INS
        if (isControlKey(event) && event.character == 3 || isControlKey(event) && event.keyCode == SWT.INSERT) {

            swtHexEdit.copySelectionToClipboard();

            return true;
        }

        // Paste: Ctrl+V | Shift+INS
        if (isControlKey(event) && event.character == 22 || isShiftKey(event) && event.keyCode == SWT.INSERT) {

            if (swtHexEdit.getByteData() == null) {
                Display.getCurrent().beep();
                return true;
            }

            String cbdata = swtHexEdit.getClipboardData();
            if (cbdata == null) {
                Display.getCurrent().beep();
                return true;
            }

            // cut out selection:
            swtHexEdit.deleteSelection();

            // paste a hex-string into the data array
            int numBytesPasted = swtHexEdit.pasteSelection(cbdata);
            if (numBytesPasted > 0) {
                moveNibbleCursor(CURSOR_RIGHT, numBytesPasted * 2);
            }

            return true;
        }

        return false;
    }

    protected boolean performDelete(VerifyEvent event) {

        switch (event.keyCode) {
        case SWT.BS:
            if (swtHexEdit.deleteSelection()) {
                return true;
            }

            int newPos = 1;
            int relpos = controlManager.getCaretByteRelativePosition();
            if (relpos == 0 && controlManager.getNibblePosition() > 0) {
                swtHexEdit.deleteByte(Math.max((controlManager.getBytePosition() - 1), 0));
                newPos++;
            } else {
                swtHexEdit.deleteByte(controlManager.getBytePosition());
            }

            moveNibbleCursor(CURSOR_LEFT, newPos);
            return true;

        case SWT.DEL:
            if (swtHexEdit.deleteSelection()) {
                return true;
            }

            swtHexEdit.deleteByte(controlManager.caret2BytePosition());
            swtHexEdit.setCaretOffset(controlManager.getNibblePosition());
            return true;
        }

        return false;
    }

    private boolean isSelectionMode(VerifyEvent event) {

        if (event.keyCode == SWT.ARROW_LEFT || event.keyCode == SWT.ARROW_RIGHT || event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN
            || event.keyCode == SWT.HOME || event.keyCode == SWT.END) {
            return true;
        }

        return false;
    }

    protected boolean performCursorMovement(VerifyEvent event) {

        if (isShiftKey(event)) {
            if (isSelectionMode(event)) {
                event.doit = true;
                return true;
            }
        }

        switch (event.keyCode) {
        case SWT.ARROW_LEFT:
            if (controlManager.isHexControl()) {
                moveNibbleCursor(CURSOR_LEFT, 1);
            } else {
                moveNibbleCursor(CURSOR_LEFT, 2);
            }
            return true;

        case SWT.ARROW_RIGHT:
            if (controlManager.isHexControl()) {
                moveNibbleCursor(CURSOR_RIGHT, 1);
            } else {
                moveNibbleCursor(CURSOR_RIGHT, 2);
            }
            return true;

        case SWT.ARROW_UP:
            moveNibbleCursor(CURSOR_UP);
            return true;

        case SWT.ARROW_DOWN:
            moveNibbleCursor(CURSOR_DOWN);
            return true;

        case SWT.PAGE_UP:
            moveNibbleCursor(CURSOR_PAGE_UP);
            return true;

        case SWT.PAGE_DOWN:
            moveNibbleCursor(CURSOR_PAGE_DOWN);
            return true;

        case SWT.HOME:
            if (isControlKey(event)) {
                moveNibbleCursor(CURSOR_HOME);
            } else {
                moveNibbleCursor(CURSOR_HOME_OF_LINE);
            }
            return true;

        case SWT.END:
            if (isControlKey(event)) {
                moveNibbleCursor(CURSOR_END);
            } else {
                moveNibbleCursor(CURSOR_END_OF_LINE);
            }
            return true;
        }

        return false;
    }

    protected boolean isControlKey(KeyEvent event) {
        return (event.stateMask & SWT.CTRL) == SWT.CTRL;
    }

    protected boolean isShiftKey(KeyEvent event) {
        return (event.stateMask & SWT.SHIFT) == SWT.SHIFT;
    }

    protected void moveNibbleCursor(int direction) {

        if (direction == CURSOR_RIGHT) {
            moveNibbleCursor(direction, 1);
        } else if (direction == CURSOR_LEFT) {
            moveNibbleCursor(direction, 1);
        } else if (direction == CURSOR_DOWN) {
            moveNibbleCursor(CURSOR_RIGHT, getNumNibblesPerLine());
        } else if (direction == CURSOR_UP) {
            moveNibbleCursor(CURSOR_LEFT, getNumNibblesPerLine());
        } else if (direction == CURSOR_PAGE_DOWN) {
            moveNibbleCursor(CURSOR_RIGHT, (getNumNibblesPerLine() * getNumVisibleLines()) /* + getNumNibblesPerLine() */);
        } else if (direction == CURSOR_PAGE_UP) {
            moveNibbleCursor(CURSOR_LEFT, (getNumNibblesPerLine() * getNumVisibleLines()) /* + getNumNibblesPerLine() */);
        } else if (direction == CURSOR_HOME) {
            swtHexEdit.setCaretOffset(0);
        } else if (direction == CURSOR_END) {

            int nibbleOffset;
            if (swtHexEdit.isVaryingMode()) {
                if (swtHexEdit.getBytesUsed() < swtHexEdit.getBufferSize()) {
                    nibbleOffset = swtHexEdit.getBytesUsed() * 2;
                } else {
                    nibbleOffset = swtHexEdit.getBytesUsed() * 2 - 1;
                }
            } else {
                nibbleOffset = swtHexEdit.getBufferSize() * 2 - 1;
            }

            swtHexEdit.setCaretOffset(nibbleOffset);

        } else if (direction == CURSOR_HOME_OF_LINE) {
            swtHexEdit.setCaretOffset(getFirstNibbleOfLine());
        } else if (direction == CURSOR_END_OF_LINE) {
            swtHexEdit.setCaretOffset(getLastNibbleOfLine());
        } else {
            throw new IllegalArgumentException("Invalid parameter 'direction': " + direction);
        }
    }

    protected void moveNibbleCursor(int direction, int positions) {

        int newPosition = controlManager.getNibblePosition();

        if (direction == CURSOR_RIGHT) {
            newPosition = newPosition + positions;
            if (newPosition >= swtHexEdit.getBufferSize() * 2) {
                newPosition = swtHexEdit.getBytesUsed() * 2 - 1;
            }
        } else if (direction == CURSOR_LEFT) {
            newPosition = newPosition - positions;
        } else {
            throw new IllegalArgumentException("Invalid parameter 'direction': " + direction);
        }

        swtHexEdit.setCaretOffset(newPosition);
    }

    private int getNumNibblesPerLine() {
        return controlManager.getBytesPerLine() * 2;
    }

    private int getNumVisibleLines() {
        return controlManager.getNumVisibleLines();
    }

    private int getFirstNibbleOfLine() {

        int lineNbr = (controlManager.caret2BytePosition() / controlManager.getBytesPerLine());
        return (lineNbr * controlManager.getBytesPerLine() * controlManager.getLineDelimiter().length());
    }

    private int getLastNibbleOfLine() {

        int lineNbr = (controlManager.caret2BytePosition() / controlManager.getBytesPerLine()) + 1;
        return (lineNbr * controlManager.getBytesPerLine() * 2) - 1;
    }

    protected abstract boolean performEdit(VerifyEvent event);
}
