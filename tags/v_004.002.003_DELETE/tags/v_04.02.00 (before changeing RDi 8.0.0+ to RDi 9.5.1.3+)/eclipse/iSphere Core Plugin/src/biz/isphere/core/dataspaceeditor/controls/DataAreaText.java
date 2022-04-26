/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.controls;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.ClipboardHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditor.events.StatusChangedEvent;
import biz.isphere.core.dataspaceeditor.events.StatusChangedListener;
import biz.isphere.core.internal.ColorHelper;
import biz.isphere.core.internal.FontHelper;

/**
 * Wrapped SWT Text widget that implements the specific need of a Text widget
 * for editing data areas. The most important difference to the standard SWT
 * widget is, that the text is displayed in lines having a fixed length.
 */
public class DataAreaText {

    private static final String CR = "\r";
    private static final String LF = "\n";
    private static final String CRLF = CR + LF;
    private static final String EOL_CHAR = "¤";

    private Text textControl;
    private TextControlMouseMoveListener mouseMoveListener;

    int maxLength;
    int lineLength;
    int lineLengthIcludingCR;

    private boolean isInsertMode = false;
    private boolean hasFocus = false;
    private boolean isDirty;
    private int lastTopIndex = -1;

    private StatusChangedEvent lastStatusChangedEvent = null;

    Vector<StatusChangedListener> statusChangedListeners = new Vector<StatusChangedListener>();

    public DataAreaText(Composite aParent, int aLineLength) {

        textControl = new Text(aParent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        lineLength = aLineLength;
        lineLengthIcludingCR = lineLength + CRLF.length();

        textControl.addKeyListener(new TextControlKeyListener());
        textControl.addMouseListener(new TextControlMouseListener());
        textControl.addPaintListener(new TextControlPaintListener());
        textControl.addFocusListener(new TextControlFocusListener());
        textControl.setMenu(new Menu(textControl.getShell(), SWT.POP_UP));

        mouseMoveListener = new TextControlMouseMoveListener(textControl);
    }

    public void addStatusChangedListener(StatusChangedListener listener) {
        statusChangedListeners.addElement(listener);
    }

    public void removeStatusChangedListener(StatusChangedListener listener) {
        statusChangedListeners.removeElement(listener);
    }

    public void setEnabled(boolean isEnabled) {
        textControl.setEnabled(isEnabled);
    }

    public void setFont(Font aFont) {
        textControl.setFont(aFont);
    }

    public void setLayoutData(Object aLayoutData) {
        textControl.setLayoutData(aLayoutData);
    }

    public Object getLayoutData() {
        return textControl.getLayoutData();
    }

    public void setTextLimit(int aLimit) {
        maxLength = aLimit;
        int maxLengthWithCR = getNumberOfCompleteRows() * lineLengthIcludingCR + getNumberOfRemainingChars() + EOL_CHAR.length();
        textControl.setTextLimit(maxLengthWithCR);
    }

    public void setText(String aText) {
        textControl.setText(toScreen(StringHelper.getFixLength(aText, maxLength)));
    }

    public String getText() {
        return fromScreen(textControl.getText());
    }

    public boolean hasFocus() {
        return hasFocus;
    }

    /**
     * Sets the selection to the range specified by the given start and end
     * indices.
     * <p>
     * Indexing is zero based.
     * 
     * @param aStart the start of the range
     * @param anEnd the end of the range
     */
    public void setSelection(int aStart, int anEnd) {

        int numCRLF = 0;
        int i = aStart;
        while (aStart % lineLength != 0 && i < anEnd) {
            if (i % lineLength == 0) {
                numCRLF++;
            }
            i++;
        }

        int start = aStart + (aStart / lineLength) * CRLF.length();
        if (start > textControl.getTextLimit()) {
            start = textControl.getTextLimit() - 1;
        }
        int length = (anEnd - aStart) + numCRLF * CRLF.length();
        textControl.setSelection(start, start + length);
    }

    /**
     * Replaces the specified text range with the given text.
     * <p>
     * Processings rules:<br>
     * <ul>
     * <li>Text of multiple lines<br>
     * Lines are expanded to the current line length of the editor. If one line
     * exceeds the line length of the editor, all new line characters are
     * removed.</li>
     * <li>Single line text<br>
     * The text is inserted as it is.</li>
     * </ul>
     * 
     * @param aSelection - text range to be replaced
     * @param aNewText - text that replaces the specified text range
     */
    public void replaceTextRange(Point aSelection, String aNewText) {
        int start = aSelection.x;
        int length = aSelection.y;
        performReplaceTextRange(start, length, replaceCRLF(aNewText));
    }

    /**
     * Returns the start index and the end index of the selected text, where
     * Point.x is the start index and Point.y is the end index.
     * 
     * @return selection with start and end index
     */
    public Point getSelection() {
        Point selection = textControl.getSelection();
        int length = selection.y - selection.x - StringHelper.count(textControl.getSelectionText(), CRLF) * CRLF.length();
        int start = selection.x - (selection.x / lineLengthIcludingCR) * CRLF.length();
        selection = new Point(start, start + length);
        return selection;
    }

    public boolean isSelected() {

        Point selection = getSelection();
        if (selection.x != selection.y) {
            return true;
        }

        return false;
    }

    public boolean isOverwriteMode() {
        return !isInsertMode;
    }

    /**
     * Returns the currently selected text. Linefeeds are removed.
     * 
     * @return selected text
     */
    public String getSelectionText() {
        Point selection = getSelection();
        if (selection.y == 0) {
            return "";
        }
        String text = fromScreen(textControl.getText());
        text = text.substring(selection.x, selection.y);
        return text;
    }

    public boolean isEnabled() {
        return textControl.isEnabled();
    }

    public void setFocus() {
        textControl.setFocus();
    }

    public void resetDirtyFlag() {
        fireStatusChangedEvent(false);
    }

    public int getTotalNumberOfRows() {
        int rows = maxLength / lineLength;
        if (maxLength % lineLength != 0) {
            rows++;
        }
        return rows;
    }

    /*
     * Clipboard methods
     */

    public void copy() {

        if (getSelectedLength(getSelection()) == 0) {
            return;
        }

        ClipboardHelper.setText(getSelectionText());
    }

    public void cut() {

        if (getSelectedLength(getSelection()) == 0) {
            return;
        }

        copy();
        Point selection = getSelection();
        deleteSelection(selection.x, selection.y);
    }

    public void paste() {

        String text = ClipboardHelper.getText();
        if (!hasEnoughSpace(getText(), getSelectedLength(getSelection()), text.length())) {
            return;
        }

        Point selection = getSelection();
        performReplaceTextRange(selection.x, selection.y - selection.x, text);
    }

    public void delete() {

        Point selection = getSelection();
        if (selection.x == selection.y) {
            deleteSelection(selection.x, selection.x + 1);
        } else {
            deleteSelection(selection.x, selection.y);
        }
    }

    public void selectAll() {
        int length = textControl.getText().length();
        textControl.setSelection(0, length - 1);
    }

    /*
     * Private methods
     */

    private String replaceCRLF(String aText) {
        String[] lines = textToArray(aText);
        if (lines.length == 1) {
            return lines[0];
        }
        StringBuilder text = new StringBuilder();
        for (String line : lines) {
            if (line.length() > lineLength) {
                return aText.replaceAll(CR, "").replaceAll(LF, "");
            }
            text.append(StringHelper.getFixLength(line, lineLength));
        }
        return text.toString();
    }

    private String[] textToArray(String aText) {
        return aText.replaceAll(CR, "").split(LF);
    }

    private int getSelectedLength(Point aSelection) {
        return aSelection.y - aSelection.x;
    }

    private void setSelection(int aPosition) {
        setSelection(aPosition, aPosition);
    }

    private String fromScreen(String aValue) {
        String tValue;
        if (aValue.endsWith(EOL_CHAR)) {
            tValue = aValue.substring(0, aValue.length() - 1);
        } else {
            tValue = aValue;
        }

        return tValue.replaceAll(CR, "").replaceAll(LF, "");
    }

    private String toScreen(String stringValue) {
        StringBuffer wrapped = new StringBuffer();

        int offset = 0;
        while (offset + lineLength < stringValue.length()) {
            if (offset > 0) {
                wrapped.append(CRLF);
            }
            wrapped.append(stringValue.substring(offset, offset + lineLength));
            offset = offset + lineLength;
        }

        if (offset < stringValue.length()) {
            if (offset > 0) {
                wrapped.append(CRLF);
            }
            wrapped.append(stringValue.substring(offset));
        }

        return wrapped.toString() + EOL_CHAR;
    }

    public int getCaretPosition() {
        int position = (getCaretRow() - 1) * lineLength + getCaretColumn();
        return position;
    }

    private int getCaretColumn() {
        int column = (textControl.getSelection().x + 1) - (getCaretRow() - 1) * lineLengthIcludingCR;
        return column;
    }

    private int getCaretRow() {
        int row = (textControl.getSelection().x + 1) / lineLengthIcludingCR;
        if ((textControl.getSelection().x + 1) % lineLengthIcludingCR != 0) {
            row++;
        }
        return row;
    }

    private int getNumberOfCompleteRows() {
        return maxLength / lineLength;
    }

    private int getNumberOfRemainingChars() {
        return maxLength - getNumberOfCompleteRows() * lineLength;
    }

    private void performBackspace() {
        if (getCaretPosition() <= 1) {
            return;
        }

        Point selection = getSelection();
        if (getSelectedLength(selection) > 0) {
            deleteSelection(selection.x, selection.y);
        } else {
            deleteSelection(selection.x - 1, selection.x);
        }
    }

    private void performDelete() {
        if (getCaretPosition() > maxLength) {
            return;
        }

        Point selection = getSelection();
        if (getSelectedLength(selection) > 0) {
            deleteSelection(selection.x, selection.y);
        } else {
            deleteSelection(selection.x, selection.x + 1);
        }
    }

    /**
     * Insert a character typed on the keyboard.
     */
    private void performInsert(char character) {
        Point selection = getSelection();
        int length = selection.y - selection.x;
        performReplaceTextRange(selection.x, length, Character.toString(character));
    }

    /**
     * Overwrites the selected text with the character typed on the keyboard.
     */
    private void performOverwrite(char character) {
        Point selection = getSelection();
        int length = selection.y - selection.x;
        if (length == 0) {
            length = 1;
        }
        performReplaceTextRange(selection.x, length, Character.toString(character));
    }

    private void performReplaceTextRange(int aStart, int aLength, String aNewText) {

        String text = getText();

        if (isInsertMode) {
            if (!hasEnoughSpace(text, aLength, aNewText.length())) {
                fireStatusChangedEvent(Messages.Not_enough_space_to_insert_text);
                return;
            }
        }

        if (aStart >= text.length()) {
            return;
        }

        text = delete(text, aStart, aStart + aLength);
        text = insert(text, aStart, aNewText);
        updateSelection(text, aStart + aNewText.length());
    }

    private void deleteSelection(int aStart, int anEnd) {
        String text = getText();
        text = delete(text, aStart, anEnd);
        updateSelection(text, aStart);
    }

    private void updateSelection(String aText, int aStart) {
        int topIndex = textControl.getTopIndex(); // FIXME: remove flickering
        setText(aText);
        setSelection(aStart);
        textControl.setTopIndex(topIndex); // FIXME: remove flickering
        fireStatusChangedEvent(true, "");
    }

    private String delete(String aText, int aStart, int anEnd) {
        if (aStart == anEnd) {
            return aText;
        }
        aText = aText.substring(0, aStart) + aText.substring(anEnd) + StringHelper.getFixLength(" ", anEnd - aStart);
        return aText;
    }

    private String insert(String text, int aStart, String aText) {
        if (aText.length() == 0) {
            return text;
        }
        String textLeft = text.substring(0, aStart);
        String textRight = text.substring(aStart, text.length() - aText.length());
        text = textLeft + aText + textRight;
        return text;
    }

    private boolean hasEnoughSpace(String text, int aLength, int aNewLengh) {
        if (StringHelper.trimR(text).length() - aLength + aNewLengh <= maxLength) {
            return true;
        }
        return false;
    }

    private boolean isControlKey(KeyEvent anEvent) {
        return (anEvent.stateMask & SWT.CTRL) == SWT.CTRL;
    }

    private boolean isShiftKey(KeyEvent anEvent) {
        return (anEvent.stateMask & SWT.SHIFT) == SWT.SHIFT;
    }

    private void fireStatusChangedEvent(boolean anIsDirty) {
        isDirty = anIsDirty;
        fireStatusChangedEvent();
    }

    private void fireStatusChangedEvent(boolean anIsDirty, String aText) {
        isDirty = anIsDirty;
        fireStatusChangedEvent(aText);
    }

    private void fireStatusChangedEvent() {
        int count = textControl.getSelectionCount();
        int start = getCaretPosition();
        int end = start + count;
        StatusChangedEvent event = new StatusChangedEvent(textControl, textControl.getTopIndex(), start, end, getCaretRow(), getCaretColumn(),
            isInsertMode, isDirty);
        if (lastStatusChangedEvent == null || !lastStatusChangedEvent.equals(event)) {
            fireStatusChangedEvent(event);
            lastStatusChangedEvent = event;
        }
    }

    private void fireStatusChangedEvent(String aMessage) {
        int count = textControl.getSelectionCount();
        int start = getCaretPosition();
        int end = start + count;
        StatusChangedEvent event = new StatusChangedEvent(textControl, textControl.getTopIndex(), start, end, getCaretRow(), getCaretColumn(),
            isInsertMode, isDirty);
        event.message = aMessage;
        if (lastStatusChangedEvent == null || !lastStatusChangedEvent.equals(event)) {
            fireStatusChangedEvent(event);
            lastStatusChangedEvent = event;
        }
    }

    private void fireStatusChangedEvent(StatusChangedEvent anEvent) {
        for (StatusChangedListener listener : statusChangedListeners) {
            listener.statusChanged(anEvent);
        }
    }

    /**
     * Inner class, that listens for key strokes in order to perform a
     * specialized action.
     */
    private class TextControlKeyListener implements KeyListener {

        public void keyReleased(KeyEvent anEvent) {
            fireStatusChangedEvent();
        }

        public void keyPressed(KeyEvent anEvent) {
            if (anEvent.keyCode == SWT.INSERT) {
                // Toggle insert mode
                isInsertMode = !isInsertMode;

            } else if (anEvent.keyCode == SWT.HOME) {
                // HOME

            } else if (anEvent.keyCode == SWT.END) {
                // END
                if (!isShiftKey(anEvent)) {
                    if (isControlKey(anEvent)) {
                        // move to END of text
                        textControl.setSelection(textControl.getText().length() - EOL_CHAR.length());
                    } else {
                        // move to END of line
                        textControl.setSelection(getCaretRow() * lineLengthIcludingCR - CRLF.length());
                    }
                } else {
                    if (isControlKey(anEvent)) {
                        // expand selection to END of text
                        textControl.setSelection(0, textControl.getText().length() - EOL_CHAR.length());
                    } else {
                        // expand selection to END of line
                        textControl.setSelection(textControl.getCaretPosition(), getCaretRow() * lineLengthIcludingCR - CRLF.length());
                    }
                }
                anEvent.doit = false;

            } else if (anEvent.keyCode == SWT.ARROW_UP | anEvent.keyCode == SWT.ARROW_DOWN) {
                // Cursor UP, DOWN

            } else if (anEvent.keyCode == SWT.ARROW_LEFT) {
                // Cursor LEFT
                if (!isControlKey(anEvent) && !isShiftKey(anEvent)) {
                    if (getCaretPosition() <= 1) {
                        anEvent.doit = false;
                    }
                }

            } else if (anEvent.keyCode == SWT.ARROW_RIGHT) {
                // Cursor RIGHT
                if (!isControlKey(anEvent) && !isShiftKey(anEvent)) {
                    if (getCaretPosition() > maxLength) {
                        anEvent.doit = false;
                    }
                }

            } else if (anEvent.keyCode == SWT.DEL) {
                // DELETE
                performDelete();
                anEvent.doit = false;

            } else if (anEvent.keyCode == SWT.BS) {
                // BACKSPACE
                performBackspace();
                anEvent.doit = false;

            } else if (StringHelper.isAsciiPrintable(anEvent.character)) {
                // typed character
                if (isInsertMode) {
                    performInsert(anEvent.character);
                } else {
                    performOverwrite(anEvent.character);
                }
                anEvent.doit = false;
            }
        }
    };

    /**
     * Inner class that listens for mouse events in order to update the status
     * bar.
     */
    private class TextControlMouseMoveListener implements MouseMoveListener {

        private Text control;
        private int lastSelectionCount;

        public TextControlMouseMoveListener(Text control) {
            this.control = control;
        }

        public void start() {
            lastSelectionCount = textControl.getSelectionCount();
            control.addMouseMoveListener(this);
        }

        public void stop() {
            control.removeMouseMoveListener(this);
        }

        public void mouseMove(MouseEvent paramMouseEvent) {
            if (lastSelectionCount != textControl.getSelectionCount()) {
                fireStatusChangedEvent();
                lastSelectionCount = textControl.getSelectionCount();
            }
        }
    }

    /**
     * Inner class that listens for mouse events in order to update the status
     * bar.
     */
    private class TextControlMouseListener extends MouseAdapter {
        @Override
        public void mouseDown(MouseEvent anEvent) {
            mouseMoveListener.start();
            fireStatusChangedEvent();
        }

        @Override
        public void mouseUp(MouseEvent anEvent) {
            fireStatusChangedEvent();
            mouseMoveListener.stop();
        }
    }

    /**
     * Inner class that paints the protected areas and that informs the data
     * area editor when the top index has changed. The top index is the index of
     * the first row shown in the editor.
     */
    private class TextControlPaintListener implements PaintListener {
        public void paintControl(PaintEvent event) {
            paintBackground(event);

            if (textControl.getClientArea().height / textControl.getLineHeight() < 1) {
                // Enforce status change event
                lastStatusChangedEvent = null;
            }

            if (lastStatusChangedEvent == null || lastTopIndex != textControl.getTopIndex()) {
                fireStatusChangedEvent();
                lastTopIndex = textControl.getTopIndex();
            }
        }

        public void paintBackground(PaintEvent event) {

            Scrollable textWidget = (Scrollable)event.getSource();
            int rows = getTotalNumberOfRows();
            Color color = ColorHelper.getBackgroundColorOfProtectedAreas();

            GC gc = event.gc;
            gc.setBackground(color);

            int charWidht = FontHelper.getFontCharWidth(textWidget);
            int charHeight = FontHelper.getFontCharHeight(textWidget);
            int editableAreaHeight = rows * charHeight;
            gc.fillRectangle(0, editableAreaHeight, textWidget.getClientArea().width, textWidget.getClientArea().height - editableAreaHeight);

            // Remaining space of last row
            if (maxLength % lineLength == 0) {
                return;
            }

            // Have to add 2 for the EOL_CHAR, because of inaccurate charWidth
            int x = (getNumberOfRemainingChars() + 2) * charWidht;
            int y = getNumberOfCompleteRows() * charHeight;
            int width = textWidget.getClientArea().width - x;
            int height = textWidget.getClientArea().height - y;
            gc.fillRectangle(x, y, width, height);
        }
    }

    /**
     * Inner class that listens for the focus of the widget in order to let the
     * editor decide whether or not to perform a CUT or PASTE event.
     */
    private class TextControlFocusListener extends FocusAdapter {
        @Override
        public void focusGained(FocusEvent e) {
            hasFocus = true;
            fireStatusChangedEvent();
        }

        @Override
        public void focusLost(FocusEvent e) {
            hasFocus = false;
        }
    }

}
