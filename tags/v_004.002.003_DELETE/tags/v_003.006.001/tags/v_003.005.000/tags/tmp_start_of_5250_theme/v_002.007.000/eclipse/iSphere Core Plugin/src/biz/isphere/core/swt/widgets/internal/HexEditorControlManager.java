/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.internal;

import java.util.Vector;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

import biz.isphere.core.internal.FontHelper;
import biz.isphere.core.swt.widgets.CaretEvent;
import biz.isphere.core.swt.widgets.CaretListener;

public class HexEditorControlManager extends MouseAdapter implements KeyListener {

    public static final String NUM_BYTES_PER_LINE = "NUM_BYTES_PER_LINE";
    public static final String NUM_BYTES_GROUPED = "NUM_BYTES_GROUPED";
    public static final String CHARS_PER_BYTE = "NUM_BYTES_PER_BYTE";
    public static final String CHARS_PER_LINE = "CHARS_PER_LINE";
    public static final String CHARS_PER_GROUP_DELIMITER = "CHARS_PER_GROUP_DELIMITER";
    public static final String RELATED_CONTROL = "RELATED_CONTROL";
    public static final String CONTENT_TYPE = "CONTENT_TYPE";

    public static final int CONTENT_TYPE_NIBBLE = 1;
    public static final int CONTENT_TYPE_STRING = 2;

    private HexEditorInternal hexEditor;
    private StyledText activeControl;
    private Point currentSelection;

    private int nibblePosition;
    private Vector<CaretListener> caretListener;

    public HexEditorControlManager(HexEditorInternal hexEditor, StyledText activeControl) {

        this.hexEditor = hexEditor;
        this.nibblePosition = 0;
        this.currentSelection = new Point(0, 0);
        this.caretListener = new Vector<CaretListener>();

        setActiveControl(activeControl);
    }

    public int getNibblePosition() {
        return nibblePosition;
    }

    public int setNibblePosition(int nibblePosition) {

        this.nibblePosition = Math.max(0, nibblePosition);
        fireCaretListener(getActiveControl());

        return this.nibblePosition;
    }

    public int getBytePosition() {
        return nibblePosition / 2;
    }

    /**
     * Depending on the caret position (cp) given, the function returns the
     * corresponding byte index for the internal buffer. Concerns the hex part.
     * 
     * @param cp caret position in question.
     * @return index in buffer.
     */
    public int caret2BytePosition() {

        return caret2BytePosition(getCaretOffset());
    }

    public Point selection2BytePosition() {

        Point selection = getSelection();
        if (selection.x == selection.y) {
            int von = caret2BytePosition(selection.x);
            int bis = caret2BytePosition(selection.y);
            return new Point(von, bis);
        }

        int relpos;
        int von = caret2BytePosition(selection.x);
        relpos = getCaretByteRelativePosition(selection.x);
        if (relpos == 2) {
            von++;
        }

        int bis = caret2BytePosition(selection.y);
        if (isHexControl()) {
            relpos = getCaretByteRelativePosition(selection.y);
            if (relpos == 0 && bis > von) {
                bis--;
            }
            bis++;
        }

        return new Point(von, bis);
    }

    /**
     * Determines the position of the caret relative to the text bytes.
     * 
     * <pre>
     * DE AD BE EF - 38 48 55
     * &circ;   &circ;   &circ;   &circ;&circ;
     * |   |   |    returns 3,4
     * |   |    returns 2
     * |    returns 1
     *  returns 0
     * </pre>
     * 
     * @param cp caret position.
     * @return see description
     */
    public int getCaretByteRelativePosition() {

        return getCaretByteRelativePosition(getCaretOffset());
    }

    public void positionCursor(int npos) {

        int cpos = nibblePos2CaretOffset(npos);
        if (cpos == getCaretOffset()) {
            return;
        }

        setCaretOffset(cpos);

        StyledText relatedControl = getRelatedControl();
        if (relatedControl != null) {
            StyledText savedActiveControl = getActiveControl();
            activeControl = relatedControl;
            cpos = nibblePos2CaretOffset(getNibblePosition());
            setCaretOffset(cpos);
            activeControl = savedActiveControl;
        }

        // TODO: remove -> updateNibblePosFromCaretOffsetHex();

        positionEditorArea();
    }

    public int updateNibblePosFromCaretOffset() {

        int relpos = getCaretByteRelativePosition();
        int bpos = caret2BytePosition();

        return setNibblePosition(bpos * 2 + relpos);
    }

    /**
     * converts a given nibble position into corresponding caret offset.
     * 
     * @param np
     * @return
     */
    private int nibblePos2CaretOffset(int np) {

        int charsperline = getCharsPerLine() + getLineDelimiter().length();
        int bytepos = np / 2;
        int line = bytepos / getBytesPerLine();
        int lpos = bytepos % getBytesPerLine();
        int co = (line * charsperline) + (lpos * getCharsPerByte()) + (lpos / getBytesGrouped() * 2);

        if (isHexControl()) {
            co = co + np % 2; // add the half-byte count
            return co;
        }

        return co;
    }

    private int caret2BytePosition(int cp) {

        int charsperline = getCharsPerLine();
        int position = cp % (charsperline + getLineDelimiter().length());
        int line = cp / (charsperline + getLineDelimiter().length());
        int groups = position / (getBytesGrouped() * getCharsPerByte() + getCharsPerGroupDelimiter());
        int bpos = line * getBytesPerLine() + (position - (groups * getCharsPerGroupDelimiter())) / getCharsPerByte();

        return bpos;
    }

    private int getCaretByteRelativePosition(int cp) {

        int charsperline = getCharsPerLine();
        int position = cp % (charsperline + getLineDelimiter().length());
        int blockpos = position % (getBytesGrouped() * getCharsPerByte() + getCharsPerGroupDelimiter());
        int retval = blockpos % getCharsPerByte();

        if ((blockpos >= getBytesGrouped() * getCharsPerByte())) {
            retval = blockpos - getBytesGrouped() * getCharsPerByte() + getCharsPerByte();
        }

        return Math.min(retval, 2);
    }

    public int getNumVisibleLines() {

        ScrolledComposite parent = getScrolledComposite();
        if (parent == null) {
            return 1;
        }

        StyledText control = getActiveControl();

        // int visibleHeight = parent.getClientArea().height -
        // control.getTopMargin(); // WDSCi!
        int visibleHeight = parent.getClientArea().height;
        int fontHeight = getFontCharHeight() + control.getLineSpacing();

        // return (visibleHeight - control.getTopMargin()) / fontHeight; //
        // WDSCi!
        return visibleHeight;
    }

    private void positionEditorArea() {

        ScrolledComposite parent = getScrolledComposite();
        if (parent == null) {
            return;
        }

        StyledText control = getActiveControl();

        // int visibleHeight = parent.getClientArea().height -
        // control.getTopMargin(); // WDSCi!
        int visibleHeight = parent.getClientArea().height;
        int visibleWidth = parent.getClientArea().width;

        int fontHeight = getFontCharHeight() + control.getLineSpacing();
        int fontWidth = getFontCharWidth();

        int offsetX = getLocation().x;

        int cursorTop = caret2BytePosition() / getBytesPerLine() * fontHeight;
        int cursorBottom = cursorTop + fontHeight;

        int currentTop = parent.getOrigin().y;
        int currentBottom = currentTop + visibleHeight;

        Point origin = parent.getOrigin();

        if (cursorTop < currentTop) {
            origin.y = cursorTop;
        }

        if (cursorBottom > currentBottom) {
            origin.y = cursorTop - visibleHeight + fontHeight;
        }

        int cursorLeft = getCaretOffset() % (getCharsPerLine() + getLineDelimiter().length()) * fontWidth;
        int cursorRight = cursorLeft + fontWidth;

        int currentLeft = parent.getOrigin().x;
        currentLeft += offsetX;
        int currentRight = currentLeft + visibleWidth;

        if (cursorLeft < currentLeft) {
            origin.x = cursorLeft - (getCharsPerByte() * fontWidth);
            origin.x += offsetX;
        }

        if (cursorRight > currentRight) {
            origin.x = cursorRight - visibleWidth + fontWidth * 2;
        }

        parent.setOrigin(origin);
    }

    private StyledText getActiveControl() {
        return this.activeControl;
    }

    public void setActiveControl(StyledText control) {

        activeControl = control;

        if (activeControl != null) {
            setCurrentSelection();
        }
    }

    private void setCurrentSelection() {

        int x = nibblePos2CaretOffset(currentSelection.x * 2);
        int y = nibblePos2CaretOffset(currentSelection.y * 2);
        Point selection = new Point(x, y);
        setSelection(selection);
    }

    /*
     * KeyListener methods
     */

    public void keyPressed(KeyEvent event) {
    }

    public void keyReleased(KeyEvent event) {

        keyOrMouseUp(event);
    }

    /*
     * MouseListener methods
     */

    public void mouseDown(MouseEvent event) {

        if (event.getSource() != activeControl) {
            return;
        }

    }

    public void mouseUp(MouseEvent event) {
        keyOrMouseUp(event);
    }

    public void keyOrMouseUp(TypedEvent event) {

        if (event.getSource() != activeControl) {
            return;
        }

        int npos = updateNibblePosFromCaretOffset();

        Point selection = getSelection();
        if (selection.x != selection.y) {
            currentSelection = selection2BytePosition();
            StyledText relatedControl = getRelatedControl();
            if (relatedControl != null) {
                StyledText savedActiveControl = getActiveControl();
                activeControl = relatedControl;
                setCurrentSelection();
                activeControl = savedActiveControl;
            }
            return;
        }

        positionCursor(npos);

        currentSelection.x = 0;
        currentSelection.y = 0;
    }

    /*
     * Methods wrapping the active control.
     */

    private Point getLocation() {
        return activeControl.getLocation();
    }

    private int getCaretOffset() {

        if (activeControl == null) {
            return 0;
        }

        return activeControl.getCaretOffset();
    }

    private void setCaretOffset(int offset) {
        activeControl.setCaretOffset(offset);
    }

    public String getLineDelimiter() {

        if (activeControl == null) {
            return "";
        }

        return activeControl.getLineDelimiter();
    }

    private Point getSelection() {

        if (activeControl == null) {
            return null;
        }

        return activeControl.getSelection();
    }

    private void setSelection(Point point) {
        activeControl.setSelection(point);
    }

    private ScrolledComposite getScrolledComposite() {

        if (activeControl == null) {
            return null;
        }

        return (ScrolledComposite)activeControl.getParent().getParent();
    }

    private int getFontCharHeight() {

        if (activeControl == null) {
            return 0;
        }

        return FontHelper.getFontCharHeight(activeControl);
    }

    private int getFontCharWidth() {

        if (activeControl == null) {
            return 0;
        }

        return FontHelper.getFontCharWidth(activeControl);
    }

    private int getCharsPerLine() {

        if (activeControl == null) {
            return 0;
        }

        return ((Integer)activeControl.getData(CHARS_PER_LINE)).intValue();
    }

    public int getBytesPerLine() {

        if (activeControl == null) {
            return 1;
        }

        return ((Integer)activeControl.getData(NUM_BYTES_PER_LINE)).intValue();
    }

    private int getBytesGrouped() {

        if (activeControl == null) {
            return getBytesPerLine();
        }

        return ((Integer)activeControl.getData(NUM_BYTES_GROUPED)).intValue();
    }

    private int getCharsPerByte() {

        if (activeControl == null) {
            return 1;
        }

        return ((Integer)activeControl.getData(CHARS_PER_BYTE)).intValue();
    }

    private int getCharsPerGroupDelimiter() {

        if (activeControl == null) {
            return 0;
        }

        return ((Integer)activeControl.getData(CHARS_PER_GROUP_DELIMITER)).intValue();
    }

    private StyledText getRelatedControl() {

        if (activeControl == null) {
            return null;
        }

        Object control = activeControl.getData(RELATED_CONTROL);
        if (control instanceof StyledText) {
            return (StyledText)control;
        }

        return null;
    }

    public boolean isHexControl() {

        if (activeControl == null) {
            return false;
        }

        int contentType = ((Integer)activeControl.getData(CONTENT_TYPE)).intValue();
        if (contentType == CONTENT_TYPE_NIBBLE) {
            return true;
        }

        return false;
    }

    public void configureControl(StyledText styledText, int contentType, int numBytesPerLine, int numBytesGrouped, int charsPerByte,
        int charsPerLine, int charsPerGroupDelimiter, StyledText relatedControl) {

        styledText.setData(HexEditorControlManager.CONTENT_TYPE, contentType);
        styledText.setData(HexEditorControlManager.NUM_BYTES_PER_LINE, numBytesPerLine);
        styledText.setData(HexEditorControlManager.NUM_BYTES_GROUPED, numBytesGrouped);
        styledText.setData(HexEditorControlManager.CHARS_PER_BYTE, charsPerByte);
        styledText.setData(HexEditorControlManager.CHARS_PER_LINE, charsPerLine);
        styledText.setData(HexEditorControlManager.CHARS_PER_GROUP_DELIMITER, charsPerGroupDelimiter);
        styledText.setData(HexEditorControlManager.RELATED_CONTROL, relatedControl);
    }

    public void addCaretListener(CaretListener listener) {

        if (caretListener.contains(listener)) {
            throw new RuntimeException("CaretListener already registered: " + listener);
        }
        caretListener.add(listener);
    }

    public boolean removeCaretListener(CaretListener listener) {

        return caretListener.remove(listener);
    }

    private void fireCaretListener(Control control) {
        CaretEvent event = new CaretEvent(hexEditor);

        for (CaretListener listener : caretListener) {
            listener.caretMoved(event);
        }
    }

    public boolean setFocus() {
        return getActiveControl().setFocus();
    }
}
