/*******************************************************************************
 * javahexeditor, a java hex editor
 * Copyright (C) 2006-2015 Jordi Bergenthal, pestatije(-at_)users.sourceforge.net
 * The official javahexeditor site is sourceforge.net/projects/javahexeditor
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 * SourceForge:
 *     https://sourceforge.net/projects/javahexeditor/ 
 * 
 * Contributors:
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/

package biz.isphere.core.swt.widgets.hexeditor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;

import biz.isphere.core.dataspaceeditor.dialog.IGoToTarget;
import biz.isphere.core.swt.widgets.hexeditor.internal.BinaryContent;
import biz.isphere.core.swt.widgets.hexeditor.internal.BinaryContentClipboard;
import biz.isphere.core.swt.widgets.hexeditor.internal.BinaryContentFinder;
import biz.isphere.core.swt.widgets.hexeditor.internal.DisplayedContent;
import biz.isphere.core.swt.widgets.hexeditor.internal.SWTUtility;
import biz.isphere.core.swt.widgets.hexeditor.internal.BinaryContent.ModifyEvent;
import biz.isphere.core.swt.widgets.hexeditor.internal.BinaryContent.ModifyListener;
import biz.isphere.core.swt.widgets.hexeditor.internal.BinaryContentFinder.Match;

/**
 * A hex editor, composed of two synchronized displays: an hexadecimal
 * and a basic ASCII char display. The file size has no effect on the memory
 * footprint of the editor. It has binary, ASCII and unicode find functionality.
 * Use addStateChangeListener(Listener) to listen to changes of the
 * 'overwrite/insert' and 'canUndo/canRedo' status.
 * 
 * @author Jordi
 */
public final class HexText extends Composite implements IGoToTarget {

    /**
     * Used to notify changes in state 'overwrite/insert' and 'canUndo/canRedo'.
     */
    public static interface StateChangeListener extends EventListener {
        /**
         * Notifies the listener that the state has just been changed
         */
        public void changed(StateChangeEvent event);
    }

    public static class StateChangeEvent extends EventObject {

        private static final long serialVersionUID = 1466213719374451159L;

        public boolean isInsertMode;
        public boolean isSelected;

        public StateChangeEvent(Object source, boolean isInsertMode, boolean isSelected) {
            super(source);

            this.isInsertMode = isInsertMode;
            this.isSelected = isSelected;
        }
    }

    /**
     * Map of displayed chars. Chars that cannot be displayed correctly are
     * changed for a '.' char. There are differences on which chars can
     * correctly be displayed in each operating system, charset encoding, or
     * font system.
     */
    public static final char[] byteToChar = new char[256];

    static final String[] byteToHex = new String[256];

    // Up to 2.1 GByte: 8 hex digits + ':'
    private static final int charsForAddress = 9;
    private final Color colorBlue = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
    final Color colorLightShadow = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
    private final Color colorNormalShadow = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
    static final FontData fontDataDefault = new FontData("Courier New", 10, SWT.NORMAL);
    private static String headerRow = null;
    private static final byte[] hexToNibble = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15 };
    private static final int maxScreenResolution = 1920;
    private static final int minCharSize = 5;
    private static final char[] nibbleToHex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    private static final int SET_TEXT = 0;
    private static final int SHIFT_FORWARD = 1; // frame
    private static final int SHIFT_BACKWARD = 2;

    private static int HEX_EDITOR = 1;
    private static int CHAR_EDITOR = 2;

    public static final int OVERWRITE = 1; // CHANGED (new)
    public static final int INSERT = 2; // CHANGED (new)
    int myModes = INSERT | OVERWRITE; // CHANGED (new)
    private List<StateChangeListener> myStateChangeListeners; // CHANGED (new)

    private int charsForFileSizeAddress = 0;
    private String charset;

    private CharsetEncoder myCharsetEncoder;
    private CharBuffer myCharBuffer;

    private boolean delayedInQueue = false;
    private Runnable delayedWaiting;
    boolean dragging = false;
    int fontCharWidth = -1;
    private List<Integer> highlightRangesInScreen;
    private List<Long> mergeChangeRanges;
    private List<Integer> mergeHighlightRanges;
    private int mergeIndexChange = -2;
    private int mergeIndexHighlight = -2;
    private boolean mergeRangesIsBlue = false;
    private boolean mergeRangesIsHighlight = false;
    private int mergeRangesPosition = -1;
    int myBytesPerLine = 16;
    boolean myCaretStickToStart = false; // stick to end
    private BinaryContentClipboard myClipboard;
    private BinaryContent myContent;
    long myEnd = 0L;
    private BinaryContentFinder myFinder;
    boolean myInserting = false;
    private KeyListener myKeyAdapter = new MyKeyAdapter();
    int myLastFocusedTextArea = -1; // 1 or 2;
    private long myLastLocationPosition = -1L;
    private List<SelectionListener> myLongSelectionListeners;
    private String myPreviousCharset;
    private long myPreviousFindEnd = -1;
    private boolean myPreviousFindCaseSensitive = false;
    private String myPreviousFindString;
    private boolean myPreviousFindStringWasHex = false;
    private int myPreviousLine = -1;
    private long myPreviousRedrawStart = -1;
    long myStart = 0L;
    long myTextAreasStart = -1L;
    private final MyTraverseAdapter myTraverseAdapter = new MyTraverseAdapter();
    int myUpANibble = 0; // always 0 or 1
    private final MyVerifyKeyAdapter myVerifyKeyAdapter = new MyVerifyKeyAdapter();
    private int numberOfLines = 16;
    private int numberOfLines_1 = numberOfLines - 1;
    private boolean stopSearching = false;
    private byte[] tmpRawBuffer = new byte[maxScreenResolution / minCharSize / 3 * maxScreenResolution / minCharSize];
    int verticalBarFactor = 0;

    // binary content modify listener
    private BinaryContent.ModifyListener myContentModifyListener;

    // visual components
    private Color colorCaretLine;
    private Color colorHighlight;
    private Font fontCurrent; // disposed externally
    private Font fontDefault; // disposed internally
    private GridData gridData5;
    private GridData gridData6;
    private GC styledText1GC;
    private GC styledText2GC;
    // indentation means containment (ie. 'textSeparator' and 'styledText' are
    // contained within 'column')
    private Composite hexEditVertivalRulerArea;
    private Text topLeftRulerSeparator;
    private StyledText hexEditVerticalRuler;
    private Composite hexEditEditorArea;
    private Composite hexEditHorizontalRulerArea;
    private StyledText hexEditHorizontalRuler;
    private StyledText hexEdit;
    private Composite charEditEditorArea;
    private Text charEditHorizontalRuler;
    private StyledText charEdit;

    private GridData gridData4;

    /**
     * compose byte-to-hex map
     */
    private void composeByteToHexMap() {
        for (int i = 0; i < 256; ++i) {
            byteToHex[i] = Character.toString(nibbleToHex[i >>> 4]) + nibbleToHex[i & 0x0f];
        }
    }

    /**
     * compose byte-to-char map
     */
    private void composeByteToCharMap() {
        if (charset == null || charEdit == null) {
            return;
        }

        CharsetDecoder d = Charset.forName(charset).newDecoder().onMalformedInput(CodingErrorAction.REPLACE)
            .onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith(".");
        ByteBuffer bb = ByteBuffer.allocate(1);
        CharBuffer cb = CharBuffer.allocate(1);
        for (int i = 0; i < 256; ++i) {
            if (i < 0x20 || i == 0x7f) {
                byteToChar[i] = '.';
            } else {
                bb.clear();
                bb.put((byte)i);
                bb.rewind();
                cb.clear();
                d.reset();
                d.decode(bb, cb, true);
                d.flush(cb);
                cb.rewind();
                char decoded = cb.get();

                // neither font metrics nor graphic context work for charset
                // 8859-1 chars between 128 and
                // 159
                String text = charEdit.getText();
                charEdit.setText("|" + decoded);
                if (charEdit.getLocationAtOffset(2).x - charEdit.getLocationAtOffset(1).x < charEdit.getLocationAtOffset(1).x
                    - charEdit.getLocationAtOffset(0).x) {
                    decoded = '.';
                }
                charEdit.setText(text);
                byteToChar[i] = decoded;
            }
        }
    }

    /**
     * compose header row
     */
    private void composeHeaderRow() {
        StringBuilder rowChars = new StringBuilder();
        for (int i = 0; i < maxScreenResolution / minCharSize / 3; ++i) {
            rowChars.append(byteToHex[i & 0x0ff]).append(' ');
        }
        headerRow = rowChars.toString().toUpperCase();
    }

    public String getCharset() {
        return charset;
    }

    private String getSystemCharset() {
        return System.getProperty("file.encoding", "utf-8");
    }

    public void setCharset(String name) {

        if ((name == null) || (name.length() == 0)) {
            name = getSystemCharset();
        }

        charset = name;
        myCharsetEncoder = Charset.forName(charset).newEncoder().onMalformedInput(CodingErrorAction.REPLACE);

        composeByteToCharMap();
        redrawTextAreas(true);
    }

    /**
     * Converts a hex String to byte[]. Will convert full bytes only, odd number
     * of hex characters will have a leading '0' added. Big endian.
     * 
     * @param hexString an hex string (ie. "0fdA1").
     * @return the byte[] value of the hex string
     */
    public byte[] hexStringToByte(String hexString) {

        if ((hexString.length() & 1) == 1) {
            hexString = '0' + hexString; // nibbles promote to a full byte
        }

        byte[] tmp = new byte[hexString.length() / 2];

        for (int i = 0; i < tmp.length; ++i) {
            String hexByte = hexString.substring(i * 2, i * 2 + 2);
            tmp[i] = (byte)Integer.parseInt(hexByte, 16);
        }

        return tmp;
    }

    private class MyKeyAdapter extends KeyAdapter {
        public MyKeyAdapter() {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.keyCode) {
            case SWT.ARROW_UP:
            case SWT.ARROW_DOWN:
            case SWT.ARROW_LEFT:
            case SWT.ARROW_RIGHT:
            case SWT.END:
            case SWT.HOME:
            case SWT.PAGE_UP:
            case SWT.PAGE_DOWN:
                boolean ctrlKey = (e.stateMask & SWT.CONTROL) != 0;
                if ((e.stateMask & SWT.SHIFT) != 0) { // shift mod2
                    long newPos = doNavigateKeyPressed(ctrlKey, e.keyCode, getCaretPosition(), false);
                    shiftStartAndEnd(newPos);
                } else { // if no modifier or control or alt
                    myEnd = myStart = doNavigateKeyPressed(ctrlKey, e.keyCode, getCaretPosition(), e.widget == hexEdit && !myInserting);
                    myCaretStickToStart = false;
                }
                ensureCaretIsVisible();

                Runnable delayed = new Runnable() {
                    public void run() {
                        redrawTextAreas(false);
                        runnableEnd();
                    }
                };
                runnableAdd(delayed);

                notifyLongSelectionListeners();
                e.doit = false;
                break;
            case SWT.INSERT:
                if ((e.stateMask & SWT.MODIFIER_MASK) == 0) {
                    redrawCaret(true);
                    notifyStateChangeListeners();
                } else if (e.stateMask == SWT.SHIFT) {
                    paste();
                } else if (e.stateMask == SWT.CONTROL) {
                    copy();
                }
                break;
            case 'a':
                if (e.stateMask == SWT.CONTROL) {
                    selectAll(); // control mod1
                }
                break;
            case 'c':
                if (e.stateMask == SWT.CONTROL) {
                    copy(); // control mod1
                }
                break;
            case 'v':
                if (e.stateMask == SWT.CONTROL) {
                    paste(); // control mod1
                }
                break;
            case 'x':
                if (e.stateMask == SWT.CONTROL) {
                    cut(); // control mod1
                }
                break;
            case 'y':
                if (e.stateMask == SWT.CONTROL) {
                    redo(); // control mod1
                }
                break;
            case 'z':
                if (e.stateMask == SWT.CONTROL) {
                    undo(); // control mod1
                }
                break;
            default:
                break;
            }
        }
    }

    private class MyMouseAdapter extends MouseAdapter {
        int charLen;

        public MyMouseAdapter(boolean hexContent) {
            charLen = 1;
            if (hexContent) {
                charLen = 3;
            }
        }

        @Override
        public void mouseDown(MouseEvent e) {
            if (e.button == 1) {
                dragging = true;
            }
            int textOffset = 0;
            try {
                textOffset = ((StyledText)e.widget).getOffsetAtLocation(new Point(e.x, e.y));
            } catch (IllegalArgumentException ex) {
                textOffset = ((StyledText)e.widget).getCharCount();
            }
            int byteOffset = textOffset / charLen;
            ((StyledText)e.widget).setTopIndex(0);
            if (e.button == 1 && (e.stateMask & SWT.MODIFIER_MASK & ~SWT.SHIFT) == 0) {
                // no modifier or shift
                if ((e.stateMask & SWT.MODIFIER_MASK) == 0) {
                    myCaretStickToStart = false;
                    myStart = myEnd = myTextAreasStart + byteOffset;
                } else { // shift
                    shiftStartAndEnd(myTextAreasStart + byteOffset);
                }
                refreshCaretsPosition();
                setFocus();
                refreshSelections();

                notifyLongSelectionListeners();
            }
        }

        @Override
        public void mouseUp(MouseEvent e) {
            if (e.button == 1) {
                dragging = false;
                notifyLongSelectionListeners();
            }
        }
    }

    private class MyMouseMoveListener implements MouseMoveListener {

        public MyMouseMoveListener(boolean hexContent) {
        }

        public void mouseMove(MouseEvent paramMouseEvent) {
            if (dragging) {
                notifyLongSelectionListeners();
            }
        }
    }

    private class MyPaintAdapter implements PaintListener {
        boolean hexContent = false;

        MyPaintAdapter(boolean isHexText) {
            hexContent = isHexText;
        }

        public void paintControl(PaintEvent event) {
            event.gc.setForeground(colorLightShadow);
            int lineWidth = 1;
            int charLen = 1;
            int rightHalfWidth = 0; // is 1, but better to tread on leftmost
            // char pixel than rightmost one
            if (hexContent) {
                lineWidth = fontCharWidth;
                charLen = 3;
                rightHalfWidth = (lineWidth + 1) / 2; // line spans to both
                // sides of its position
            }
            event.gc.setLineWidth(lineWidth);
            for (int block = 8; block <= myBytesPerLine; block += 8) {
                int xPos = (charLen * block) * fontCharWidth - rightHalfWidth;
                event.gc.drawLine(xPos, event.y, xPos, event.y + event.height);
            }
        }
    }

    private class MySelectionAdapter extends SelectionAdapter {
        int charLen;

        public MySelectionAdapter(boolean hexContent) {
            charLen = 1;
            if (hexContent) {
                charLen = 3;
            }
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            if (!dragging) {
                return;
            }

            boolean selection = myStart != myEnd;
            int lower = e.x / charLen;
            int higher = e.y / charLen;
            int caretPos = ((StyledText)e.widget).getCaretOffset() / charLen;
            myCaretStickToStart = caretPos < higher || caretPos < lower;
            if (lower > higher) {
                lower = higher;
                higher = e.x / charLen;
            }

            select(myTextAreasStart + lower, myTextAreasStart + higher);
            if (selection != (myStart != myEnd)) {
                notifyLongSelectionListeners();
            }

            redrawTextAreas(false);
        }
    }

    private class MyTraverseAdapter implements TraverseListener {
        public MyTraverseAdapter() {
        }

        public void keyTraversed(TraverseEvent e) {
            if (e.detail == SWT.TRAVERSE_TAB_NEXT) {
                e.doit = true;
            }
        }
    }

    private class MyVerifyKeyAdapter implements VerifyKeyListener {
        public MyVerifyKeyAdapter() {
        }

        public void verifyKey(VerifyEvent e) {
            // Log.log(this, "verifyKey={0}", e);
            if ((e.character == SWT.DEL || e.character == SWT.BS) && myInserting) {
                if (!deleteSelected()) {
                    if (e.character == SWT.BS) {
                        myStart += myUpANibble;
                        if (myStart > 0L) {
                            myContent.delete(myStart - 1L, 1L);
                            myEnd = --myStart;
                        }
                    } else { // e.character == SWT.DEL
                        myContent.delete(myStart, 1L);
                    }
                    ensureWholeScreenIsVisible();
                    ensureCaretIsVisible();
                    Runnable delayed = new Runnable() {
                        public void run() {
                            redrawTextAreas(true);
                            runnableEnd();
                        }
                    };
                    runnableAdd(delayed);
                    updateScrollBar();

                    notifyLongSelectionListeners();
                }
                myUpANibble = 0;
            } else {
                doModifyKeyPressed(e);
            }

            e.doit = false;
        }
    }

    private final class MyFinderRunnable implements Runnable {
        private Match match;

        public MyFinderRunnable() {

        }

        public void run() {
            match = myFinder.getNextMatch();
        }

        public Match getMatch() {
            if (match == null) {
                throw new IllegalStateException("Field 'match' must not be null.");
            }
            return match;
        }
    }

    /**
     * Create a binary text editor
     * 
     * @param parent parent in the widget hierarchy
     * @param style not used for the moment
     */
    public HexText(final Composite parent, int style) {
        super(parent, style | SWT.BORDER | SWT.V_SCROLL);

        colorCaretLine = new Color(Display.getCurrent(), 232, 242, 254); // very
        // light
        // blue
        colorHighlight = new Color(Display.getCurrent(), 255, 248, 147); // mellow
        // yellow
        highlightRangesInScreen = new ArrayList<Integer>();

        composeByteToHexMap();
        composeHeaderRow();

        myClipboard = new BinaryContentClipboard(parent.getDisplay());
        myLongSelectionListeners = new ArrayList<SelectionListener>();
        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                colorCaretLine.dispose();
                colorHighlight.dispose();
                if (fontDefault != null && !fontDefault.isDisposed()) {
                    fontDefault.dispose();
                }
                try {
                    myClipboard.dispose();
                } catch (IOException ex) {
                    // SWTUtility.showMessage(parent.getShell(),
                    // SWT.ICON_WARNING | SWT.OK,
                    // Texts.HEX_TEXTS_TITLE_INCONSISTENT_CLIPBOARD_FILES,
                    // Texts.HEX_TEXTS_MESSAGE_INCONSISTENT_CLIPBOARD_FILES,
                    // BinaryContentClipboard.CLIPBOARD_FOLDER_PATH,
                    // BinaryContentClipboard.CLIPBOARD_FILE_NAME,
                    // TextUtility.format(BinaryContentClipboard.CLIPBOARD_FILE_NAME_PASTED,
                    // "..."));
                }
            }
        });
        initialize();
        myLastFocusedTextArea = HEX_EDITOR;
        myPreviousLine = -1;
    }

    /**
     * redraw the caret with respect of Inserting/Overwriting mode
     * 
     * @param focus
     */
    public void redrawCaret(boolean focus) {
        drawUnfocusedCaret(false);
        setInsertMode(focus ? (!myInserting) : myInserting);
        if (myInserting && myUpANibble != 0) {
            myUpANibble = 0;
            refreshCaretsPosition();
            if (focus) {
                setFocus();
            }
        } else {
            drawUnfocusedCaret(true);
        }
        if (focus) {
            notifyLongSelectionListeners();
        }
    }

    /**
     * Adds a long selection listener. Events sent to the listener have long
     * start and end points. The start point is formed by event.width as the
     * most significant int and event.x as the least significant int. The end
     * point is similarly formed by event.height and event.y A listener can
     * obtain the long selection with this code:
     * getLongSelection(SelectionEvent) long start = ((long)event.width) << 32 |
     * (event.x & 0x0ffffffffL) Similarly for the end point: long end =
     * ((long)event.height) << 32 | (event.y & 0x0ffffffffL)
     * 
     * @param listener the listener
     * @see StyledText#addSelectionListener(org.eclipse.swt.events.SelectionListener)
     */
    public void addLongSelectionListener(SelectionListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Parameter 'listener' must not be null.");
        }
        if (!myLongSelectionListeners.contains(listener)) {
            myLongSelectionListeners.add(listener);
        }
    }

    /**
     * This method initializes composite
     */
    private void initialize() {

        GridLayout gridLayout1 = new GridLayout();
        gridLayout1.numColumns = 3;
        gridLayout1.marginHeight = 0;
        gridLayout1.verticalSpacing = 0;
        gridLayout1.horizontalSpacing = 0;
        gridLayout1.marginWidth = 0;
        setLayout(gridLayout1);

        // Column 1: vertical hex ruler
        hexEditVertivalRulerArea = new Composite(this, SWT.NONE);
        hexEditVertivalRulerArea.setLayout(createEditorLayout());
        hexEditVertivalRulerArea.setBackground(colorLightShadow);
        GridData gridDataColumn = new GridData(SWT.BEGINNING, SWT.FILL, false, true);
        hexEditVertivalRulerArea.setLayoutData(gridDataColumn);

        GridData gridDataTextSeparator = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        gridDataTextSeparator.widthHint = 10;
        topLeftRulerSeparator = new Text(hexEditVertivalRulerArea, SWT.SEPARATOR);
        topLeftRulerSeparator.setEnabled(false);
        topLeftRulerSeparator.setBackground(colorLightShadow);
        topLeftRulerSeparator.setLayoutData(gridDataTextSeparator);

        hexEditVerticalRuler = new StyledText(hexEditVertivalRulerArea, SWT.MULTI | SWT.READ_ONLY);
        hexEditVerticalRuler.setEditable(false);
        hexEditVerticalRuler.setEnabled(false);
        hexEditVerticalRuler.setBackground(colorLightShadow);
        hexEditVerticalRuler.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        fontDefault = new Font(Display.getCurrent(), fontDataDefault);
        fontCurrent = fontDefault;
        hexEditVerticalRuler.setFont(fontCurrent);
        GC styledTextGC = new GC(hexEditVerticalRuler);
        fontCharWidth = styledTextGC.getFontMetrics().getAverageCharWidth();
        styledTextGC.dispose();
        GridData gridDataAddresses = new GridData(SWT.BEGINNING, SWT.FILL, false, true);
        gridDataAddresses.heightHint = numberOfLines * hexEditVerticalRuler.getLineHeight();
        hexEditVerticalRuler.setLayoutData(gridDataAddresses);
        setAddressesGridDataWidthHint();
        hexEditVerticalRuler.setContent(new DisplayedContent(charsForAddress, numberOfLines));

        // Column 2: hex editor
        hexEditEditorArea = new Composite(this, SWT.NONE);
        hexEditEditorArea.setLayout(createEditorLayout());
        hexEditEditorArea.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
        GridData gridDataColumn1 = new GridData(SWT.BEGINNING, SWT.FILL, false, true);
        hexEditEditorArea.setLayoutData(gridDataColumn1);

        // ruler header
        hexEditHorizontalRulerArea = new Composite(hexEditEditorArea, SWT.NONE);
        hexEditHorizontalRulerArea.setLayout(createHorizontalLayout());
        hexEditHorizontalRulerArea.setBackground(colorLightShadow);
        GridData gridDataColumn1Header = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
        hexEditHorizontalRulerArea.setLayoutData(gridDataColumn1Header);

        gridData4 = createHexEditGridData();
        hexEditHorizontalRuler = new StyledText(hexEditHorizontalRulerArea, SWT.SINGLE | SWT.READ_ONLY);
        hexEditHorizontalRuler.setEditable(false);
        hexEditHorizontalRuler.setEnabled(false);
        hexEditHorizontalRuler.setBackground(colorLightShadow);
        hexEditHorizontalRuler.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        hexEditHorizontalRuler.setLayoutData(gridData4);
        hexEditHorizontalRuler.setFont(fontCurrent);
        refreshHeader();

        // editor
        hexEdit = new StyledText(hexEditEditorArea, SWT.MULTI);
        hexEdit.setFont(fontCurrent);
        styledText1GC = new GC(hexEdit);
        gridData5 = createHexEditGridData();
        hexEdit.setLayoutData(gridData5);
        hexEdit.addKeyListener(myKeyAdapter);
        FocusListener myFocusAdapter = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                drawUnfocusedCaret(false);
                myLastFocusedTextArea = HEX_EDITOR;
                if (e.widget == charEdit) {
                    myLastFocusedTextArea = CHAR_EDITOR;
                }
                getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        drawUnfocusedCaret(true);
                    }
                });
            }
        };
        hexEdit.addFocusListener(myFocusAdapter);
        hexEdit.addMouseListener(new MyMouseAdapter(true));
        hexEdit.addMouseMoveListener(new MyMouseMoveListener(true));
        hexEdit.addPaintListener(new MyPaintAdapter(true));
        hexEdit.addTraverseListener(myTraverseAdapter);
        hexEdit.addVerifyKeyListener(myVerifyKeyAdapter);
        hexEdit.setContent(new DisplayedContent(myBytesPerLine * 3, numberOfLines));
        hexEdit.setDoubleClickEnabled(false);
        hexEdit.addSelectionListener(new MySelectionAdapter(true));
        // StyledText.setCaretOffset() version 3.448 bug resets the caret size
        // if using the default one,
        // so we use not the default one.
        Caret defaultCaret = hexEdit.getCaret();
        Caret nonDefaultCaret = new Caret(defaultCaret.getParent(), defaultCaret.getStyle());
        nonDefaultCaret.setBounds(defaultCaret.getBounds());
        hexEdit.setCaret(nonDefaultCaret);

        // Column 3: character editor
        charEditEditorArea = new Composite(this, SWT.NONE);
        charEditEditorArea.setLayout(createEditorLayout());
        charEditEditorArea.setBackground(hexEdit.getBackground());
        GridData gridDataColumn2 = new GridData(SWT.FILL, SWT.FILL, true, true);
        charEditEditorArea.setLayoutData(gridDataColumn2);

        // ruler header
        charEditHorizontalRuler = new Text(charEditEditorArea, SWT.NONE);
        charEditHorizontalRuler.setEnabled(false);
        charEditHorizontalRuler.setBackground(colorLightShadow);
        GridData gridDataTextSeparator2 = new GridData();
        gridDataTextSeparator2.horizontalAlignment = SWT.FILL;
        gridDataTextSeparator2.verticalAlignment = SWT.FILL;
        gridDataTextSeparator2.grabExcessHorizontalSpace = true;
        charEditHorizontalRuler.setLayoutData(gridDataTextSeparator2);
        makeFirstRowSameHeight();

        // editor
        charEdit = new StyledText(charEditEditorArea, SWT.MULTI);
        charEdit.setFont(fontCurrent);
        int width = myBytesPerLine * fontCharWidth + 1; // one pixel for caret
                                                        // in
        // last column
        gridData6 = new GridData();
        gridData6.verticalAlignment = SWT.FILL;
        gridData6.widthHint = charEdit.computeTrim(0, 0, width, 0).width;
        gridData6.grabExcessVerticalSpace = true;
        charEdit.setLayoutData(gridData6);
        charEdit.addKeyListener(myKeyAdapter);
        charEdit.addFocusListener(myFocusAdapter);
        charEdit.addMouseListener(new MyMouseAdapter(false));
        charEdit.addMouseMoveListener(new MyMouseMoveListener(false));
        charEdit.addPaintListener(new MyPaintAdapter(false));
        charEdit.addTraverseListener(myTraverseAdapter);
        charEdit.addVerifyKeyListener(myVerifyKeyAdapter);
        charEdit.setContent(new DisplayedContent(myBytesPerLine, numberOfLines));
        charEdit.setDoubleClickEnabled(false);
        charEdit.addSelectionListener(new MySelectionAdapter(false));
        charEdit.setBackgroundMode(SWT.INHERIT_NONE);

        // StyledText.setCaretOffset() version 3.448 bug resets the caret size
        // if using the default one,
        // so we use not the default one.
        defaultCaret = charEdit.getCaret();
        nonDefaultCaret = new Caret(defaultCaret.getParent(), defaultCaret.getStyle());
        nonDefaultCaret.setBounds(defaultCaret.getBounds());
        charEdit.setCaret(nonDefaultCaret);
        styledText2GC = new GC(charEdit);
        setCharset(null);

        super.setFont(fontCurrent);
        ScrollBar vertical = getVerticalBar();
        vertical.setSelection(0);
        vertical.setMinimum(0);
        vertical.setIncrement(1);
        vertical.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                e.doit = false;
                long previousStart = myTextAreasStart;
                myTextAreasStart = getVerticalBar().getSelection();
                myTextAreasStart = (myTextAreasStart << verticalBarFactor) * myBytesPerLine;
                if (previousStart == myTextAreasStart) {
                    return;
                }

                Runnable delayed = new Runnable() {
                    public void run() {
                        redrawTextAreas(false);
                        setFocus();
                        runnableEnd();
                    }
                };
                runnableAdd(delayed);
            }
        });
        updateScrollBar();
        addMouseListener(new org.eclipse.swt.events.MouseAdapter() {
            @Override
            public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
                setFocus();
            }
        });
        addControlListener(new org.eclipse.swt.events.ControlAdapter() {
            @Override
            public void controlResized(org.eclipse.swt.events.ControlEvent e) {
                updateTextsMetrics();
            }
        });
        addDisposeListener(new org.eclipse.swt.events.DisposeListener() {
            public void widgetDisposed(org.eclipse.swt.events.DisposeEvent e) {
                if (myContent != null) {
                    myContent.dispose();
                }
            }
        });
    }

    private GridData createHexEditGridData() {

        GridData gridData = new GridData();
        gridData.horizontalIndent = 1;
        gridData.verticalAlignment = SWT.FILL;
        gridData.grabExcessVerticalSpace = true;

        return gridData;
    }

    private GridLayout createHorizontalLayout() {
        GridLayout column1HeaderLayout = new GridLayout();
        column1HeaderLayout.marginHeight = 0;
        column1HeaderLayout.marginWidth = 0;
        return column1HeaderLayout;
    }

    private GridLayout createEditorLayout() {
        GridLayout column2Layout = new GridLayout();
        column2Layout.marginHeight = 0;
        column2Layout.verticalSpacing = 1;
        column2Layout.horizontalSpacing = 0;
        column2Layout.marginWidth = 0;
        return column2Layout;
    }

    public boolean isValid() {
        return myContent != null;
    }

    public boolean isEditable() {
        return myContent != null;
    }

    /**
     * Tells whether the last action can be redone
     * 
     * @return true: an action can be redone
     */
    public boolean canRedo() {
        return myContent != null && myContent.canRedo();
    }

    /**
     * Tells whether the content of the editor is dirty
     * 
     * @return true: content has been changed
     */
    public boolean isDirty() {
        return myContent != null && myContent.isDirty();
    }

    /**
     * Tells whether the last action can be undone
     * 
     * @return true: an action can be undone
     */
    public boolean canUndo() {
        return myContent != null && myContent.canUndo();
    }

    /**
     * Copies the selection into the clipboard. If nothing is selected leaves
     * the clipboard with its current contents. The clipboard will hold text
     * data (for pasting into a text editor) and binary data (internal for
     * HexText). Text data is limited to 4Mbytes, binary data is limited by disk
     * space.
     */
    public void copy() {

        if (myStart >= myEnd) {
            return;
        }

        if (myLastFocusedTextArea == HEX_EDITOR) {
            myClipboard.setHexContents(myContent, myStart, myEnd - myStart);
        } else {
            myClipboard.setTextContents(myContent, charset, myStart, myEnd - myStart);
        }
    }

    private StringBuilder cookAddresses(long address, int limit) {
        StringBuilder theText = new StringBuilder();
        for (int i = 0; i < limit; i += myBytesPerLine, address += myBytesPerLine) {
            boolean indenting = true;
            for (int j = (charsForAddress - 2) * 4; j > 0; j -= 4) {
                int nibble = ((int)(address >>> j)) & 0x0f;
                if (nibble != 0) {
                    indenting = false;
                }
                if (indenting) {
                    if (j >= (charsForFileSizeAddress * 4)) {
                        theText.append(' ');
                    } else {
                        theText.append('0');
                    }
                } else {
                    theText.append(nibbleToHex[nibble]);
                }
            }
            theText.append(nibbleToHex[((int)address) & 0x0f]).append(':');
        }

        return theText;
    }

    private StringBuilder cookTexts(boolean hex, int length) {
        if (length > tmpRawBuffer.length) {
            length = tmpRawBuffer.length;
        }
        StringBuilder result;

        if (hex) {
            result = new StringBuilder(length * 3);
            for (int i = 0; i < length; ++i) {
                result.append(byteToHex[tmpRawBuffer[i] & 0x0ff]).append(' ');
            }
        } else {
            result = new StringBuilder(length);
            for (int i = 0; i < length; ++i) {
                result.append(byteToChar[tmpRawBuffer[i] & 0x0ff]);
            }
        }

        return result;
    }

    /**
     * Calls copy();deleteSelected();
     * 
     * @see #copy() #deleteSelected()
     */
    public void cut() {
        copy();
        deleteSelected();
    }

    /**
     * While in insert mode, trims the selection
     * 
     * @return did delete something
     */
    public boolean deleteNotSelected() {
        if (!myInserting || myStart < 1L && myEnd >= myContent.length()) {
            return false;
        }

        myContent.delete(myEnd, myContent.length() - myEnd);
        myContent.delete(0L, myStart);
        myStart = 0L;
        myEnd = myContent.length();

        myUpANibble = 0;
        ensureWholeScreenIsVisible();
        restoreStateAfterModify();

        return true;
    }

    /**
     * While in insert mode, deletes the selection
     * 
     * @return did delete something
     */
    public boolean deleteSelected() {
        if (!handleSelectedPreModify()) {
            return false;
        }
        myUpANibble = 0;
        ensureWholeScreenIsVisible();
        restoreStateAfterModify();

        return true;
    }

    void doModifyKeyPressed(KeyEvent event) {
        char aChar = event.character;
        if (aChar == '\0' || aChar == '\b' || aChar == '\u007f' || event.stateMask == SWT.CTRL || event.widget == hexEdit
            && ((event.stateMask & SWT.MODIFIER_MASK) != 0 || aChar < '0' || aChar > '9' && aChar < 'A' || aChar > 'F' && aChar < 'a' || aChar > 'f')) {
            return;
        }

        if (getCaretPosition() == myContent.length() && !myInserting) {
            ensureCaretIsVisible();
            redrawTextAreas(false);
            return;
        }
        handleSelectedPreModify();
        try {
            if (myInserting) {
                if (event.widget == charEdit) {
                    myContent.insert(transcodeChar(aChar), getCaretPosition());
                } else if (myUpANibble == 0) {
                    myContent.insert((byte)(hexToNibble[aChar - '0'] << 4), getCaretPosition());
                } else {
                    myContent.overwrite(hexToNibble[aChar - '0'], 4, 4, getCaretPosition());
                }
            } else {
                if (event.widget == charEdit) {
                    myContent.overwrite(transcodeChar(aChar), getCaretPosition());
                } else {
                    myContent.overwrite(hexToNibble[aChar - '0'], myUpANibble * 4, 4, getCaretPosition());
                }
                myContent.get(ByteBuffer.wrap(tmpRawBuffer, 0, 1), null, getCaretPosition());
                int offset = (int)(getCaretPosition() - myTextAreasStart);
                hexEdit.replaceTextRange(offset * 3, 2, byteToHex[tmpRawBuffer[0] & 0x0ff]);
                hexEdit.setStyleRange(new StyleRange(offset * 3, 2, colorBlue, null));
                charEdit.replaceTextRange(offset, 1, Character.toString(byteToChar[tmpRawBuffer[0] & 0x0ff]));
                charEdit.setStyleRange(new StyleRange(offset, 1, colorBlue, null));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        myStart = myEnd = incrementPosWithinLimits(getCaretPosition(), event.widget == hexEdit);
        Runnable delayed = new Runnable() {
            public void run() {
                ensureCaretIsVisible();
                redrawTextAreas(false);
                if (myInserting) {
                    updateScrollBar();
                    redrawTextAreas(true);
                }
                refreshSelections();
                runnableEnd();
            }
        };
        runnableAdd(delayed);
        notifyLongSelectionListeners();
    }

    private byte transcodeChar(char aChar) {

        try {

            if (myCharBuffer==null) {
                myCharBuffer=CharBuffer.allocate(1);
            }else {
                myCharBuffer.clear();
            }
            myCharBuffer.append(aChar);
            myCharBuffer.rewind();
            ByteBuffer buffer = myCharsetEncoder.encode(myCharBuffer);

            return buffer.get(0);

        } catch (Throwable e) {
            return (byte)aChar;
        }
    }

    long doNavigateKeyPressed(boolean ctrlKey, int keyCode, long oldPos, boolean countNibbles) {
        if (!countNibbles) {
            myUpANibble = 0;
        }

        switch (keyCode) {
        case SWT.ARROW_UP:
            if (oldPos >= myBytesPerLine) {
                oldPos -= myBytesPerLine;
            }
            break;

        case SWT.ARROW_DOWN:
            if (oldPos <= myContent.length() - myBytesPerLine) {
                oldPos += myBytesPerLine;
            }
            if (countNibbles && oldPos == myContent.length()) {
                myUpANibble = 0;
            }
            break;

        case SWT.ARROW_LEFT:
            if (countNibbles && (oldPos > 0 || oldPos == 0 && myUpANibble > 0)) {
                if (myUpANibble == 0) {
                    --oldPos;
                }
                myUpANibble ^= 1; // 1->0, 0->1
            }
            if (!countNibbles && oldPos > 0) {
                --oldPos;
            }
            break;

        case SWT.ARROW_RIGHT:
            oldPos = incrementPosWithinLimits(oldPos, countNibbles);
            break;

        case SWT.END:
            if (ctrlKey) {
                oldPos = myContent.length();
            } else {
                oldPos = oldPos - oldPos % myBytesPerLine + myBytesPerLine - 1L;
                if (oldPos >= myContent.length()) {
                    oldPos = myContent.length();
                }
            }
            myUpANibble = 0;
            if (countNibbles && oldPos < myContent.length()) {
                myUpANibble = 1;
            }
            break;

        case SWT.HOME:
            if (ctrlKey) {
                oldPos = 0;
            } else {
                oldPos = oldPos - oldPos % myBytesPerLine;
            }
            myUpANibble = 0;
            break;

        case SWT.PAGE_UP:
            if (oldPos >= myBytesPerLine) {
                oldPos = oldPos - myBytesPerLine * numberOfLines_1;
                if (oldPos < 0L) {
                    oldPos = (oldPos + myBytesPerLine * numberOfLines_1) % myBytesPerLine;
                }
            }
            break;

        case SWT.PAGE_DOWN:
            if (oldPos <= myContent.length() - myBytesPerLine) {
                oldPos = oldPos + myBytesPerLine * numberOfLines_1;
                if (oldPos > myContent.length()) {
                    oldPos = oldPos - ((oldPos - 1 - myContent.length()) / myBytesPerLine + 1) * myBytesPerLine;
                }
            }
            if (countNibbles && oldPos == myContent.length()) {
                myUpANibble = 0;
            }
            break;
        }

        return oldPos;
    }

    void drawUnfocusedCaret(boolean visible) {
        if (hexEdit.isDisposed()) {
            return;
        }

        GC unfocusedGC = null;
        Caret unfocusedCaret = null;
        int chars = 0;
        int shift = 0;
        if (myLastFocusedTextArea == HEX_EDITOR) {
            unfocusedCaret = charEdit.getCaret();
            unfocusedGC = styledText2GC;
        } else {
            unfocusedCaret = hexEdit.getCaret();
            unfocusedGC = styledText1GC;
            chars = 1;
            if (hexEdit.getCaretOffset() % 3 == 1) {
                shift = -1;
            }
        }
        if (unfocusedCaret.getVisible()) {
            Rectangle unfocused = unfocusedCaret.getBounds();
            unfocusedGC.setForeground(visible ? colorNormalShadow : colorCaretLine);
            unfocusedGC.drawRectangle(unfocused.x + shift * unfocused.width, unfocused.y, unfocused.width << chars, unfocused.height - 1);
        }
    }

    void ensureCaretIsVisible() {
        long caretPos = getCaretPosition();
        long posInLine = caretPos % myBytesPerLine;

        if (myTextAreasStart > caretPos) {
            myTextAreasStart = caretPos - posInLine;
        } else if (myTextAreasStart + myBytesPerLine * numberOfLines < caretPos || myTextAreasStart + myBytesPerLine * numberOfLines == caretPos
            && caretPos != myContent.length()) {
            myTextAreasStart = caretPos - posInLine - myBytesPerLine * numberOfLines_1;
            if (caretPos == myContent.length() && posInLine == 0) {
                myTextAreasStart = caretPos - myBytesPerLine * numberOfLines;
            }
            if (myTextAreasStart < 0L) {
                myTextAreasStart = 0L;
            }
        } else {

            return;
        }
        getVerticalBar().setSelection((int)((myTextAreasStart / myBytesPerLine) >>> verticalBarFactor));
    }

    void ensureWholeScreenIsVisible() {
        if (myTextAreasStart + myBytesPerLine * numberOfLines > myContent.length()) {
            myTextAreasStart = myContent.length() - (myContent.length() - 1L) % myBytesPerLine - 1L - myBytesPerLine * numberOfLines_1;
        }

        if (myTextAreasStart < 0L) {
            myTextAreasStart = 0L;
        }
    }

    /**
     * Performs a find on the text and sets the selection accordingly. The find
     * starts at the current caret position.
     * 
     * @param findString the literal to find
     * @param isHexString consider the literal as an hex string (ie. "0fdA1").
     *        Used for binary finds. Will search full bytes only, odd number of
     *        hex characters will have a leading '0' added.
     * @param searchForward look for matches after current position
     * @param caseSensitive match upper case with lower case characters
     * @return whether a match was found
     * @throws CharacterCodingException
     */
    public Match findAndSelect(int offset, String findString, boolean isHexString, boolean searchForward, boolean caseSensitive)
        throws CharacterCodingException {
        if (findString == null) {
            throw new IllegalArgumentException("Parameter 'findString' must not be null.");
        }
        Match result = findAndSelectInternal(offset, findString, isHexString, searchForward, caseSensitive, true);

        return result;
    }

    private Match findAndSelectInternal(int offset, String findString, boolean isHexString, boolean searchForward, boolean caseSensitive,
        boolean updateGui) throws CharacterCodingException {
        if (findString == null) {
            throw new IllegalArgumentException("Parameter 'findString' must not be null.");
        }

        if (!initFinder(offset, findString, isHexString, searchForward, caseSensitive)) {
            return null;
        }

        MyFinderRunnable finderRunnable = new MyFinderRunnable();
        SWTUtility.blockUntilFinished(finderRunnable);
        Match match = finderRunnable.getMatch();
        if (match.getException() != null) {
            return match;
        }

        if (match.isFound()) {
            myCaretStickToStart = false;
            if (updateGui) {
                setSelection(match.getStartPosition(), match.getEndPosition());
            } else {
                select(match.getStartPosition(), match.getEndPosition());
            }
            myPreviousFindEnd = getCaretPosition();
        }

        return match;
    }

    /**
     * Get caret position in file, which can be out of view
     * 
     * @return the current caret position
     */
    public long getCaretPosition() {
        if (myCaretStickToStart) {
            return myStart;
        }
        return myEnd;
    }

    public byte getActualValue() {
        return getValue(getCaretPosition());
    }

    public byte getValue(long pos) {
        try {
            myContent.get(ByteBuffer.wrap(tmpRawBuffer, 0, 1), null, pos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmpRawBuffer[0];
    }

    /**
     * Get the binary content
     * 
     * @return the content being edited
     */
    public BinaryContent getContent() {
        return myContent;
    }

    public long getContentLength() {
        return myContent.length();
    }

    private void getHighlightRangesInScreen(long start, int length) {
        highlightRangesInScreen.clear();
        if (myLastLocationPosition >= start && myLastLocationPosition < start + length) {
            highlightRangesInScreen.add(new Integer((int)(myLastLocationPosition - myTextAreasStart)));
            highlightRangesInScreen.add(new Integer(1));
        }
    }

    /**
     * Gets the selection start and end points as long values
     * 
     * @return 2 elements long array, first one the start point (inclusive),
     *         second one the end point (exclusive)
     */
    public Point getSelection() {
        return new Point((int)myStart, (int)myEnd);
    }

    public boolean isSelected() {
        return (myStart != myEnd);
    }

    private boolean handleSelectedPreModify() {
        if (myStart == myEnd || !myInserting) {
            return false;
        }

        myContent.delete(myStart, myEnd - myStart);
        myEnd = myStart;

        return true;
    }

    private long incrementPosWithinLimits(long oldPos, boolean countNibbles) {
        if (oldPos < myContent.length()) if (countNibbles) {
            if (myUpANibble > 0) {
                ++oldPos;
            }
            myUpANibble ^= 1; // 1->0, 0->1
        } else {
            ++oldPos;
        }

        return oldPos;
    }

    private boolean initFinder(int offset, String findString, boolean isHexString, boolean searchForward, boolean caseSensitive)
        throws CharacterCodingException {

        if (!searchForward) {
            myCaretStickToStart = true;
        }

        if (myFinder == null || !findString.equals(myPreviousFindString) || isHexString != myPreviousFindStringWasHex
            || caseSensitive != myPreviousFindCaseSensitive || myPreviousCharset != charset) {
            myPreviousFindString = findString;
            myPreviousFindStringWasHex = isHexString;
            myPreviousFindCaseSensitive = caseSensitive;
            myPreviousCharset = charset;

            if (isHexString) {
                myFinder = new BinaryContentFinder(hexStringToByte(findString), myContent);
            } else {
                myFinder = new BinaryContentFinder(findString, charset, myContent, caseSensitive);
            }
            myFinder.setNewStart(getCaretPosition());
        }

        if (myPreviousFindEnd != getCaretPosition()) {
            myFinder.setNewStart(getCaretPosition());
        }

        myFinder.setDirectionForward(searchForward);

        if (searchForward) {
            if (offset < 0) {
                offset = 0;
            }
            myFinder.setNewStart(offset);
        } else {
            if (offset < 0) {
                offset = (int)myContent.length() - 1;
            }
            myFinder.setNewStart(offset + 1);
        }

        return true;
    }

    /**
     * Tells whether the input is in overwrite or insert mode
     * 
     * @return true: overwriting, false: inserting
     */
    public boolean isOverwriteMode() {
        return !myInserting;
    }

    private void makeFirstRowSameHeight() {
        ((GridData)topLeftRulerSeparator.getLayoutData()).heightHint = hexEditHorizontalRuler.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
        ((GridData)charEditHorizontalRuler.getLayoutData()).heightHint = hexEditHorizontalRuler.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
    }

    /**
     * Merge ranges of changes in file with ranges of highlighted elements.
     * Finds lowest range border, finds next lowest range border. That's the
     * first result. Keeps going until last range border.
     * 
     * @param changeRanges
     * @param highlightRanges
     * @return list of StyleRanges, each with a style of type 'changed',
     *         'highlighted', or both.
     */
    public List<StyleRange> mergeRanges(List<Long> changeRanges, List<Integer> highlightRanges) {
        if (!mergerInit(changeRanges, highlightRanges)) {
            return null;
        }
        List<StyleRange> result = new ArrayList<StyleRange>();
        mergerNext();
        int start = mergeRangesPosition;
        boolean blue = mergeRangesIsBlue;
        boolean highlight = mergeRangesIsHighlight;
        while (mergerNext()) {
            if (blue || highlight) {
                result.add(new StyleRange(start, mergeRangesPosition - start, blue ? colorBlue : null, highlight ? colorHighlight : null));
            }
            start = mergeRangesPosition;
            blue = mergeRangesIsBlue;
            highlight = mergeRangesIsHighlight;
        }

        return result;
    }

    private boolean mergerCatchUps() {
        boolean withinRange = false;
        if (mergeChangeRanges != null && mergeChangeRanges.size() > mergeIndexChange) {
            withinRange = true;
            if (mergerPosition(true) < mergeRangesPosition) {
                ++mergeIndexChange;
            }
        }
        if (mergeHighlightRanges != null && mergeHighlightRanges.size() > mergeIndexHighlight) {
            withinRange = true;
            if (mergerPosition(false) < mergeRangesPosition) {
                ++mergeIndexHighlight;
            }
        }

        return withinRange;
    }

    /**
     * Initialise merger variables
     * 
     * @param changeRanges
     * @param highlightRanges
     * @return whether the parameters hold any data
     */
    private boolean mergerInit(List<Long> changeRanges, List<Integer> highlightRanges) {
        if ((changeRanges == null || changeRanges.size() < 2) && (highlightRanges == null || highlightRanges.size() < 2)) {
            return false;
        }
        this.mergeChangeRanges = changeRanges;
        this.mergeHighlightRanges = highlightRanges;
        mergeRangesIsBlue = false;
        mergeRangesIsHighlight = false;
        mergeRangesPosition = -1;
        mergeIndexChange = 0;
        mergeIndexHighlight = 0;

        return true;
    }

    private int mergerMinimumInChangesHighlights() {
        int change = Integer.MAX_VALUE;
        if (mergeChangeRanges != null && mergeChangeRanges.size() > mergeIndexChange) {
            change = mergerPosition(true);
        }
        int highlight = Integer.MAX_VALUE;
        if (mergeHighlightRanges != null && mergeHighlightRanges.size() > mergeIndexHighlight) {
            highlight = mergerPosition(false);
        }
        int result = Math.min(change, highlight);
        if (change == result) {
            mergeRangesIsBlue = (mergeIndexChange & 1) == 0;
        }
        if (highlight == result) {
            mergeRangesIsHighlight = (mergeIndexHighlight & 1) == 0;
        }

        return result;
    }

    private boolean mergerNext() {
        ++mergeRangesPosition;
        if (!mergerCatchUps()) {
            return false;
        }
        mergeRangesPosition = mergerMinimumInChangesHighlights();

        return true;
    }

    private int mergerPosition(boolean changesNotHighlights) {
        int result = -1;
        if (changesNotHighlights) {
            result = (int)(mergeChangeRanges.get(mergeIndexChange & 0xfffffffe).longValue() - myTextAreasStart);
            if ((mergeIndexChange & 1) == 1) {
                result = (int)Math.min(myBytesPerLine * numberOfLines, result + mergeChangeRanges.get(mergeIndexChange).longValue());
            }
        } else {
            result = mergeHighlightRanges.get(mergeIndexHighlight & 0xfffffffe).intValue();
            if ((mergeIndexHighlight & 1) == 1) {
                result += mergeHighlightRanges.get(mergeIndexHighlight).intValue();
            }
        }

        return result;
    }

    private void notifyLongSelectionListeners() {
        if (myLongSelectionListeners.isEmpty()) {
            return;
        }

        Event basicEvent = new Event();
        basicEvent.widget = this;
        SelectionEvent anEvent = new SelectionEvent(basicEvent);
        anEvent.width = (int)(myStart >>> 32);
        anEvent.x = (int)myStart;
        anEvent.height = (int)(myEnd >>> 32);
        anEvent.y = (int)myEnd;

        for (int i = 0; i < myLongSelectionListeners.size(); ++i) {
            myLongSelectionListeners.get(i).widgetSelected(anEvent);
        }
    }

    public boolean canPaste() {
        return myClipboard.hasContents();
    }

    /**
     * Pastes the clipboard content. The result depends on which insertion mode
     * is currently active: Insert mode replaces the selection with the
     * DND.CLIPBOARD clipboard contents or, if there is no selection, inserts at
     * the current caret offset. Overwrite mode replaces contents at the current
     * caret offset, unless pasting would overflow the content length, in which
     * case does nothing.
     */
    public void paste() {

        if (!myClipboard.hasContents()) {
            return;
        }

        handleSelectedPreModify();
        long caretPos = getCaretPosition();

        long total;
        if (myLastFocusedTextArea == HEX_EDITOR) {
            total = myClipboard.getHexContents(myContent, charset, caretPos, myInserting);
        } else {
            total = myClipboard.getTextContents(myContent, charset, caretPos, myInserting);
        }

        myStart = caretPos;
        myEnd = caretPos + total;
        myCaretStickToStart = false;

        redrawTextAreas(true);
        restoreStateAfterModify();
    }

    /**
     * Redoes the last undone action
     */
    public void redo() {
        undo(false);
    }

    private void redrawTextAreas(int mode, StringBuilder newText, StringBuilder resultHex, StringBuilder resultChar, List<StyleRange> viewRanges) {
        hexEdit.getCaret().setVisible(false);
        charEdit.getCaret().setVisible(false);
        if (mode == SET_TEXT) {
            hexEditVerticalRuler.getContent().setText(newText.toString());
            hexEdit.getContent().setText(resultHex.toString());
            charEdit.getContent().setText(resultChar.toString());
            myPreviousLine = -1;
        } else {
            boolean forward = mode == SHIFT_FORWARD;
            hexEditVerticalRuler.setRedraw(false);
            hexEdit.setRedraw(false);
            charEdit.setRedraw(false);
            ((DisplayedContent)hexEditVerticalRuler.getContent()).shiftLines(newText.toString(), forward);
            ((DisplayedContent)hexEdit.getContent()).shiftLines(resultHex.toString(), forward);
            ((DisplayedContent)charEdit.getContent()).shiftLines(resultChar.toString(), forward);
            hexEditVerticalRuler.setRedraw(true);
            hexEdit.setRedraw(true);
            charEdit.setRedraw(true);
            if (myPreviousLine >= 0 && myPreviousLine < numberOfLines) {
                myPreviousLine += newText.length() / charsForAddress * (forward ? 1 : -1);
            }
            if (myPreviousLine < -1 || myPreviousLine >= numberOfLines) {
                myPreviousLine = -1;
            }
        }
        if (viewRanges != null) {
            for (Iterator<StyleRange> i = viewRanges.iterator(); i.hasNext();) {
                StyleRange styleRange = i.next();
                charEdit.setStyleRange(styleRange);
                styleRange = (StyleRange)styleRange.clone();
                styleRange.start *= 3;
                styleRange.length *= 3;
                hexEdit.setStyleRange(styleRange);
            }
        }
    }

    void redrawTextAreas(boolean fromScratch) {
        if (myContent == null || hexEdit.isDisposed()) {
            return;
        }

        long newLinesStart = myTextAreasStart;
        int linesShifted = numberOfLines;
        int mode = SET_TEXT;
        if (!fromScratch && myPreviousRedrawStart >= 0L) {
            long lines = (myTextAreasStart - myPreviousRedrawStart) / myBytesPerLine;
            if (Math.abs(lines) < numberOfLines) {
                mode = lines > 0L ? SHIFT_BACKWARD : SHIFT_FORWARD;
                linesShifted = Math.abs((int)lines);
                if (linesShifted < 1) {
                    refreshSelections();
                    refreshCaretsPosition();

                    return;
                }
                if (mode == SHIFT_BACKWARD) {
                    newLinesStart = myTextAreasStart + (numberOfLines - (int)lines) * myBytesPerLine;
                }
            }
        }
        myPreviousRedrawStart = myTextAreasStart;

        StringBuilder newText = cookAddresses(newLinesStart, linesShifted * myBytesPerLine);

        ArrayList<Long> changeRanges = new ArrayList<Long>();
        int actuallyRead = 0;
        try {
            actuallyRead = myContent.get(ByteBuffer.wrap(tmpRawBuffer, 0, linesShifted * myBytesPerLine), changeRanges, newLinesStart);
        } catch (IOException e) {
            actuallyRead = 0;
        }
        StringBuilder resultHex = cookTexts(true, actuallyRead);
        StringBuilder resultChar = cookTexts(false, actuallyRead);
        getHighlightRangesInScreen(newLinesStart, linesShifted * myBytesPerLine);
        List<StyleRange> viewRanges = mergeRanges(changeRanges, highlightRangesInScreen);
        redrawTextAreas(mode, newText, resultHex, resultChar, viewRanges);
        refreshSelections();
        refreshCaretsPosition();
    }

    void refreshCaretsPosition() {
        drawUnfocusedCaret(false);
        long caretLocation = getCaretPosition() - myTextAreasStart;
        if (caretLocation >= 0L && caretLocation < myBytesPerLine * numberOfLines || getCaretPosition() == myContent.length()
            && caretLocation == myBytesPerLine * numberOfLines) {
            int tmp = (int)caretLocation;
            if (tmp == myBytesPerLine * numberOfLines) {
                hexEdit.setCaretOffset(tmp * 3 - 1);
                charEdit.setCaretOffset(tmp);
            } else {
                hexEdit.setCaretOffset(tmp * 3 + myUpANibble);
                charEdit.setCaretOffset(tmp);
            }
            int line = hexEdit.getLineAtOffset(hexEdit.getCaretOffset());
            if (line != myPreviousLine) {
                if (myPreviousLine >= 0 && myPreviousLine < numberOfLines) {
                    hexEdit.setLineBackground(myPreviousLine, 1, null);
                    charEdit.setLineBackground(myPreviousLine, 1, null);
                }
                hexEdit.setLineBackground(line, 1, colorCaretLine);
                charEdit.setLineBackground(line, 1, colorCaretLine);
                myPreviousLine = line;
            }
            hexEdit.getCaret().setVisible(true);
            charEdit.getCaret().setVisible(true);
            getDisplay().asyncExec(new Runnable() {
                public void run() {
                    drawUnfocusedCaret(true);
                }
            });
        } else {
            hexEdit.getCaret().setVisible(false);
            charEdit.getCaret().setVisible(false);
        }
    }

    private void refreshHeader() {
        hexEditHorizontalRuler.setText(headerRow.substring(0, Math.min(myBytesPerLine * 3, headerRow.length())) + " ");
    }

    void refreshSelections() {
        if (myStart >= myEnd || myStart > myTextAreasStart + myBytesPerLine * numberOfLines || myEnd <= myTextAreasStart) {
            return;
        }

        long startLocation = myStart - myTextAreasStart;
        if (startLocation < 0L) {
            startLocation = 0L;
        }
        int intStart = (int)startLocation;

        long endLocation = myEnd - myTextAreasStart;
        if (endLocation > myBytesPerLine * numberOfLines) {
            endLocation = myBytesPerLine * numberOfLines;
        }
        int intEnd = (int)endLocation;

        if (myCaretStickToStart) {
            int tmp = intStart;
            intStart = intEnd;
            intEnd = tmp;
        }

        int adjustEnd = 0;
        if (myEnd == myContent.length()) {
            adjustEnd = 1;
        }

        hexEdit.setSelection(intStart * 3, (intEnd * 3) - adjustEnd);
        hexEdit.setTopIndex(0);
        charEdit.setSelection(intStart, intEnd);
        charEdit.setTopIndex(0);
    }

    /**
     * Removes the specified selection listener
     * 
     * @param listener
     * @see StyledText#removeSelectionListener(org.eclipse.swt.events.SelectionListener)
     */
    public void removeLongSelectionListener(SelectionListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Parameter 'listener' must not be null"); //$NON-NLS-1$
        }

        myLongSelectionListeners.remove(listener);
    }

    /**
     * Replaces the selection. The result depends on which insertion mode is
     * currently active: Insert mode replaces the selection with the
     * replaceString or, if there is no selection, inserts at the current caret
     * offset. Overwrite mode replaces contents at the current selection start.
     * 
     * @param replaceString the new string
     * @param isHexString consider the literal as an hex string (ie. "0fdA1").
     *        Used for binary finds. Will replace full bytes only, odd number of
     *        hex characters will have a leading '0' added.
     */
    public void replace(String replaceString, boolean isHexString) {
        if (replaceString == null) {
            throw new IllegalArgumentException("Parameter 'replaceString' must not be null.");
        }
        handleSelectedPreModify();
        byte[] replaceData = replaceString.getBytes();
        if (isHexString) {
            replaceData = hexStringToByte(replaceString);
        }
        ByteBuffer newSelection = ByteBuffer.wrap(replaceData);
        if (myInserting) {
            myContent.insert(newSelection, myStart);
        } else {
            newSelection.limit((int)Math.min(newSelection.limit(), myContent.length() - myStart));
            myContent.overwrite(newSelection, myStart);
        }
        myEnd = myStart + newSelection.limit() - newSelection.position();
        myCaretStickToStart = false;
        redrawTextAreas(true);
        restoreStateAfterModify();
    }

    /**
     * Replaces all occurrences of findString with replaceString. The find
     * starts at the current caret position.
     * 
     * @param findString the literal to find
     * @param isFindHexString consider the literal as an hex string (ie.
     *        "0fdA1"). Used for binary finds. Will search full bytes only, odd
     *        number of hex characters will have a leading '0' added.
     * @param searchForward look for matches after current position
     * @param ignoreCase match upper case with lower case characters
     * @param replaceString the new string
     * @param isReplaceHexString consider the literal as an hex string (ie.
     *        "0fdA1"). Used for binary finds. Will replace full bytes only, odd
     *        number of hex characters will have a leading '0' added.
     * @return An array with [0]=number of replacements, [1]=last replaced start
     *         position
     * @throws IOException
     */
    public long[] replaceAll(int offset, String findString, boolean isFindHexString, boolean searchForward, boolean ignoreCase, String replaceString,
        boolean isReplaceHexString) throws IOException {
        if (findString == null) {
            throw new IllegalArgumentException("Parameter 'findString' must not be null.");
        }
        if (replaceString == null) {
            throw new IllegalArgumentException("Parameter 'replaceString' must not be null.");
        }
        long replacements = 0;
        long lastStartPosition = 0;
        stopSearching = false;
        while (!stopSearching) {

            Match match = findAndSelectInternal(offset, findString, isFindHexString, searchForward, ignoreCase, false);
            if (match.isFound()) {
                replacements++;
                lastStartPosition = match.getStartPosition();
                replace(replaceString, isReplaceHexString);
            } else {
                stopSearching = true;
                if (match.getException() != null) {
                    throw match.getException();
                }
            }
        }
        if (replacements > 0) {
            Point selection = getSelection();
            setSelection(selection.x, selection.y);
        }

        return new long[] { replacements, lastStartPosition };
    }

    private void restoreStateAfterModify() {
        ensureCaretIsVisible();
        redrawTextAreas(true);
        updateScrollBar();

        notifyLongSelectionListeners();
    }

    void runnableAdd(Runnable delayed) {
        if (delayedInQueue) {
            delayedWaiting = delayed;
        } else {
            delayedInQueue = true;
            Display.getCurrent().asyncExec(delayed);
        }
    }

    void runnableEnd() {
        if (delayedWaiting != null) {
            Display.getCurrent().asyncExec(delayedWaiting);
            delayedWaiting = null;
        } else {
            delayedInQueue = false;
        }
    }

    /**
     * Sets the selection to the entire text. Caret remains either at the
     * selection start or end
     */
    public void selectAll() {
        select(0L, myContent.length());
        refreshSelections();
    }

    /**
     * Sets the selection from start to end.
     * 
     * @param start
     * @param end
     */
    public void selectBlock(long start, long end) {
        select(start, end);
        refreshSelections();
        showMark(start);
    }

    void select(long start, long end) {
        myUpANibble = 0;
        boolean selection = myStart != myEnd;
        myStart = 0L;
        if (start > 0L) {
            myStart = start;
            if (myStart > myContent.length()) {
                myStart = myContent.length();
            }
        }

        myEnd = myStart;
        if (end > myStart) {
            myEnd = end;
            if (myEnd > myContent.length()) {
                myEnd = myContent.length();
            }
        }

        if (selection != (myStart != myEnd)) {
            notifyLongSelectionListeners();
        }
    }

    private void setAddressesGridDataWidthHint() {
        ((GridData)hexEditVerticalRuler.getLayoutData()).widthHint = charsForAddress * fontCharWidth;
    }

    public void setInsertMode(boolean insert) {

        // CHANGED (start)
        if (insert && ((myModes & INSERT) == 0)) {
            return;
        }

        if (!insert && ((myModes & OVERWRITE) == 0)) {
            return;
        }
        // CHANGED (end)

        myInserting = insert;
        int width = 0;
        int height = hexEdit.getCaret().getSize().y;
        if (!myInserting) {
            width = fontCharWidth;
        }

        hexEdit.getCaret().setSize(width, height);
        charEdit.getCaret().setSize(width, height);
    }

    // CHANGED (new)
    /**
     * Specifies what modes (insert/overwrite) are active.
     * 
     * @param modes modes the user can use for updating data
     */
    public void setModes(int modes) {

        if (modes == INSERT) {
            myModes = modes;
            setInsertMode(true);
        } else if (modes == OVERWRITE) {
            myModes = modes;
            setInsertMode(false);
        } else if (modes == (INSERT | OVERWRITE)) {
            myModes = modes;
        }
    }

    /**
     * Sets the content to be displayed. Replacing an existing content keeps the
     * display area in the same position, but only if it falls within the new
     * content's limits.
     * 
     * @param aContent the content to be displayed
     */
    public void setContentProvider(BinaryContent aContent) {

        boolean firstContent = (myContent == null);

        myContent = aContent;
        myFinder = null;
        if (myContent != null) {
            myContent.setActionsHistory();
        }

        if (firstContent || myEnd > myContent.length() || myTextAreasStart >= myContent.length()) {
            myTextAreasStart = myStart = myEnd = 0L;
            myCaretStickToStart = false;
        }

        charsForFileSizeAddress = Long.toHexString(myContent.length()).length();

        if (myContentModifyListener == null) {
            myContentModifyListener = new BinaryContent.ModifyListener() {
                public void modified(ModifyEvent event) {
                    notifyListeners(SWT.Modify, null);
                }
            };
        } else {
            myContent.removeModifyListener(myContentModifyListener);
        }

        myContent.addModifyListener(myContentModifyListener);

        updateScrollBar();
        redrawTextAreas(true);

        notifyLongSelectionListeners();
    }

    /**
     * Causes the receiver to have the keyboard focus. Within Eclipse, never
     * call setFocus() before the workbench has called
     * EditorActionBarContributor.setActiveEditor()
     * 
     * @see Composite#setFocus()
     */
    @Override
    public boolean setFocus() {
        redrawCaret(false);
        if (myLastFocusedTextArea == HEX_EDITOR) {
            return hexEdit.setFocus();
        }
        return charEdit.setFocus();
    }

    /**
     * @see Control#setFont(org.eclipse.swt.graphics.Font) Font height must not
     *      be 1 or 2.
     * @throws IllegalArgumentException if font height is 1 or 2
     */
    @Override
    public void setFont(Font font) {
        // bugfix: HexText's raw array overflows when font is very small and
        // window very big
        // very small sizes would compromise responsiveness in large windows,
        // and they are too small
        // to see anyway
        if (font != null) {
            int newSize = font.getFontData()[0].getHeight();
            if (newSize == 1 || newSize == 2) {
                throw new IllegalArgumentException("Font size is " + newSize + ", too small"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        fontCurrent = font;
        if (fontCurrent == null) {
            fontCurrent = fontDefault;
        }
        super.setFont(fontCurrent);
        hexEditHorizontalRuler.setFont(fontCurrent);
        hexEditHorizontalRuler.pack(true);
        GC gc = new GC(hexEditHorizontalRuler);
        fontCharWidth = gc.getFontMetrics().getAverageCharWidth();
        gc.dispose();
        makeFirstRowSameHeight();
        hexEditVerticalRuler.setFont(fontCurrent);
        setAddressesGridDataWidthHint();
        hexEditVerticalRuler.pack(true);
        hexEdit.setFont(fontCurrent);
        hexEdit.pack(true);
        charEdit.setFont(fontCurrent);
        charEdit.pack(true);
        updateTextsMetrics();
        layout();
        setInsertMode(myInserting);
    }

    /**
     * Sets the selection. The caret may change position but stays at the same
     * selection point (if it was at the start of the selection it will move to
     * the new start, otherwise to the new end point). The new selection is made
     * visible
     * 
     * @param start inclusive start selection position
     * @param end exclusive end selection position
     */
    public void setSelection(long start, long end) {
        select(start, end);
        ensureCaretIsVisible();
        redrawTextAreas(false);
    }

    void shiftStartAndEnd(long newPos) {
        if (myCaretStickToStart) {
            myStart = Math.min(newPos, myEnd);
            myEnd = Math.max(newPos, myEnd);
        } else {
            myEnd = Math.max(newPos, myStart);
            myStart = Math.min(newPos, myStart);
        }
        myCaretStickToStart = myEnd != newPos;
    }

    /**
     * Shows the position on screen.
     * 
     * @param position where relocation should go
     */
    public void showMark(long position) {
        myLastLocationPosition = position;
        if (position < 0) {
            return;
        }

        position = position - position % myBytesPerLine;
        myTextAreasStart = position;
        if (numberOfLines > 2) {
            myTextAreasStart = position - (numberOfLines / 2) * myBytesPerLine;
        }
        ensureWholeScreenIsVisible();
        redrawTextAreas(true);
        // setFocus();
        updateScrollBar();
    }

    /**
     * Stop findAndSelect() or replaceAll() calls. Long running searches can be
     * stopped from another thread.
     */
    public void stopSearching() {
        stopSearching = true;
        if (myFinder != null) {
            myFinder.stopSearching();
        }
    }

    private long totalNumberOfLines() {
        long result = 1L;
        if (myContent != null) {
            result = (myContent.length() - 1L) / myBytesPerLine + 1L;
        }

        return result;
    }

    /**
     * Undoes the last action
     */
    public void undo() {
        undo(true);
    }

    private void undo(boolean previousAction) {
        long[] selection = previousAction ? myContent.undo() : myContent.redo();
        if (selection == null) {
            return;
        }

        myUpANibble = 0;
        myStart = selection[0];
        myEnd = selection[1];
        myCaretStickToStart = false;
        ensureWholeScreenIsVisible();
        restoreStateAfterModify();
    }

    private void updateNumberOfLines() {
        int height = getClientArea().height - hexEditHorizontalRuler.computeSize(SWT.DEFAULT, SWT.DEFAULT, false).y;

        numberOfLines = height / hexEditVerticalRuler.getLineHeight();
        if (numberOfLines < 1) {
            numberOfLines = 1;
        }

        numberOfLines_1 = numberOfLines - 1;

        ((DisplayedContent)hexEditVerticalRuler.getContent()).setDimensions(charsForAddress, numberOfLines);
        ((DisplayedContent)hexEdit.getContent()).setDimensions(myBytesPerLine * 3, numberOfLines);
        ((DisplayedContent)charEdit.getContent()).setDimensions(myBytesPerLine, numberOfLines);
    }

    void updateScrollBar() {
        ScrollBar vertical = getVerticalBar();
        long max = totalNumberOfLines();
        verticalBarFactor = 0;
        while (max > Integer.MAX_VALUE) {
            max >>>= 1;
            ++verticalBarFactor;
        }
        vertical.setMaximum((int)max);
        vertical.setSelection((int)((myTextAreasStart / myBytesPerLine) >>> verticalBarFactor));
        vertical.setPageIncrement(numberOfLines_1);
        vertical.setThumb(numberOfLines);
    }

    void updateTextsMetrics() {
        int width = getClientArea().width - hexEditVerticalRuler.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
        int displayedNumberWidth = fontCharWidth * 4; // styledText1 and
        // styledText2
        myBytesPerLine = (width / displayedNumberWidth) & 0xfffffff8; // 0, 8,
        // 16, 24,
        // etc.
        if (myBytesPerLine < 16) {
            myBytesPerLine = 16;
        }

        // gridData4.widthHint = hexEdit.computeTrim(0, 0, myBytesPerLine * 3 *
        // fontCharWidth, 100).width;
        gridData5.widthHint = hexEdit.computeTrim(0, 0, myBytesPerLine * 3 * fontCharWidth, 100).width;
        gridData6.widthHint = charEdit.computeTrim(0, 0, myBytesPerLine * fontCharWidth, 100).width;
        updateNumberOfLines();
        changed(new Control[] { hexEditHorizontalRuler, hexEditVerticalRuler, hexEdit, charEdit });
        updateScrollBar();
        refreshHeader();
        myTextAreasStart = (((long)getVerticalBar().getSelection()) * myBytesPerLine) << verticalBarFactor;
        redrawTextAreas(true);
    }

    /**
     * Add a listener to the list of listeners to be notified when there is a
     * change in the state
     * 
     * @param listener to be notified of the changes
     */
    public void addStateChangeListener(StateChangeListener listener) {
        if (myStateChangeListeners == null) {
            myStateChangeListeners = new ArrayList<StateChangeListener>();
        }

        myStateChangeListeners.add(listener);
    }

    /**
     * Remove a listener to the list of listeners to be notified when there is a
     * change in the state
     * 
     * @param listener not to be notified of the change
     */
    public void removeStateChangeListener(ModifyListener listener) {
        if (myStateChangeListeners != null) {
            myStateChangeListeners.remove(listener);
        }
    }

    private void notifyStateChangeListeners() {
        if (myStateChangeListeners == null) {
            return;
        }

        for (int i = 0; i < myStateChangeListeners.size(); ++i) {
            myStateChangeListeners.get(i).changed(new StateChangeEvent(this, !isOverwriteMode(), isSelected()));
        }
    }
}
