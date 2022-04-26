/*******************************************************************************
 * Copyright (c) 2004 Robert Köpferl
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * or in the file license.html
 * 
 * Contributors:
 *      Robert Köpferl <quellkode@koepferl.de> - Initial writing
 *      Thomas Raddatz <thomas.raddatz@tools400.de> - Bug fixes and enhancements
 * Created:
 *      16.02.2004
 * Project:
 *      SWT-Hexedit
 * Version:
 *      $Version$
 * Last Change:
 *      $Log$ 
 *******************************************************************************/
package biz.isphere.core.swt.widgets.internal;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Inherited;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scrollable;

import biz.isphere.base.internal.ByteArrayHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.internal.FontHelper;
import biz.isphere.core.swt.widgets.CaretListener;

/**
 * This class implements a nice Hex-control to be used in a pure SWT
 * application. It provides an interface similar to these known from other SWT
 * controls. Since SWT doesn't allow inheritance of class StyledText, this
 * control is a composition of a StyledText object and glue code around to
 * change the behavior.
 * <p>
 * Hexedit is only able to work on buffers with constant length. So the initial
 * buffer size has to be provided. However it is possible to change the buffer
 * afterwards.
 * 
 * @author Robert Köpferl
 */
public class HexEditorInternal extends Composite implements FocusListener {

    protected static final int DEFAULT_BYTES_PER_LINE = 8;
    protected static final int DEFAULT_BYTES_GROUPED = 4;
    protected static final int DEFAULT_BUFFER_SIZE = 0;

    private static final String DATA_CONTROL = "CONTROL";

    private static final String HEX_BYTE_DELIMITER = " ";
    private static final String HEX_GROUP_DELIMITER = "-" + HEX_BYTE_DELIMITER;
    private static final byte PADDING_BYTE = 0x00;

    private static final String HEX_INSERT_CARET = "HEX_INSERT_CARET";
    private static final String HEX_OVERWRITE_CARET = "HEX_OVERWRITE_CARET";
    private static final String STRING_INSERT_CARET = "STRING_INSERT_CARET";
    private static final String STRING_OVERWRITE_CARET = "STRING_OVERWRITE_CARET";
    private static final int LENGTH_OFFSET_LABEL = 8;

    // private static final int BORDER_WIDTH = 10; // WDSCi!
    private static final int BORDER_WIDTH = 0;
    private static final int LINE_SPACING = 5;

    private Label labelOffset;
    private Label labelHex;
    private Label labelString;
    private ScrolledComposite scrollableEditorArea;
    private Composite editorArea;
    private Composite offsetArea;
    private StyledText hexpart;
    private StyledText stringpart;
    private HexEditorControlManager controlManager;

    private boolean varyingMode;
    private boolean insertMode;
    private Map<String, Caret> caret;
    private Font monofont;

    // internal buffer for the hex data
    private byte[] byteData;

    // actual length of the buffer
    private int dataLength;

    // "global" clipboard
    private Clipboard clipboard;

    private int numBytesPerLine;
    private int numBytesGrouped;

    private int changesCounter; // TODO: remove debug code

    protected HexEditorInternal(Composite parent, int style) {
        this(parent, style, DEFAULT_BUFFER_SIZE, DEFAULT_BYTES_PER_LINE, DEFAULT_BYTES_GROUPED);
    }

    protected HexEditorInternal(Composite parent, int style, int buffersize) {
        this(parent, style, buffersize, DEFAULT_BYTES_PER_LINE, DEFAULT_BYTES_GROUPED);
    }

    protected HexEditorInternal(Composite parent, int style, int bufferSize, int bytesPerLine, int bytesGrouped) {
        super(parent, style | SWT.MULTI);

        // set internal parameters
        this.numBytesPerLine = bytesPerLine;

        if (bytesPerLine % bytesGrouped == 0) {
            this.numBytesGrouped = bytesGrouped;
        } else {
            this.numBytesGrouped = bytesPerLine;
        }

        // create a font for the control
        this.monofont = new Font(getDisplay(), new FontData("Courier", 10, SWT.NORMAL));

        // allocate constant buffer
        // replaceBuffer(new byte[bufferSize]);
        this.byteData = new byte[bufferSize];

        // create Clipboard
        this.clipboard = new Clipboard(this.getDisplay());

        createContent(checkStyle(style));

        setInsertMode(false);
        setVaryingMode(false);
    }

    /**
     * Retrieves the actual byte buffer of the control. Thus you can directly
     * access the bytes in the buffer. Remember that changes are not reflected
     * to the control until you for example call setByteData. The array is not
     * copied, just returned as it is.
     * 
     * @return the byte array of the actual data.
     * @see setByteData, adjustBuffer
     */
    public byte[] getByteData() {
        return byteData;
    }

    /**
     * Sets the controls byte buffer to a new one. Surrounding variables are set
     * appropriately. The Caret is positioned equal or at maximum possible. The
     * dataLength, which describes the number of bytes displayed is set equal to
     * the newly given data, so that all bytes are displayed. The array is not
     * copied, just taken as it is.
     * 
     * @param bs a byte array to which the buffer shall be set to.
     */
    public void setByteData(byte[] bs) {

        replaceBuffer(bs);
    }

    /**
     * Sets the control's buffer to a new content. The buffer remains constant
     * in its length. The buffer is filled with a copy of the new data. In case
     * the new data is longer than the buffer, it's truncated to the buffer's
     * length, otherwise just as many bytes as given are copied and the
     * displayed length is set to the minimum of the new length and the buffers
     * capacity. If null is given, the control becomes empty. The buffer length
     * becomes zero and nothing can be entered.
     * 
     * @param towhat - Content, the control will be set to.
     * @see adjustBuffer.
     */
    public void setContent(byte[] towhat) {

        if (towhat == null) {
            setByteData(new byte[0]);
        } else {
            int newVisibleLength = Math.min(towhat.length, getBufferSize());
            setMaxSize(newVisibleLength);
            replaceBytes(0, towhat); // OK
        }
    }

    /**
     * Changes the contrl's constant buffer to a new size. The contents is cut
     * if the new size is less than the current. If the new size is greater than
     * the current, the trailing bytes are filled with padding. The number of
     * shown bytes is set depending on padding. If padding==null, the # of
     * visible chars remains constant or at maximum new size. If padding was
     * given, the displayed size becomes equal to new size.
     * 
     * @param size - the new buffer size in bytes.
     * @param padding - byte value to be used as padding, null is also allowed.
     */
    public void adjustBuffer(int size, boolean padding) {

        int numBytesToCopy = Math.min(size, getBytesUsed());

        // Create new buffer and copy existing data
        byte[] newArray = new byte[size];
        System.arraycopy(byteData, 0, newArray, 0, numBytesToCopy); // OK
        byteData = newArray;

        // either fill with padding or leave dataLength as it is.
        if (padding) {
            // fill with padding until the end
            for (int i = numBytesToCopy; i < getBufferSize(); i++) {
                byteData[i] = PADDING_BYTE;
            }
        } else {
        }

        int bytesUsed;
        if (isVaryingMode()) {
            bytesUsed = Math.min(getBytesUsed(), size); // OK
        } else {
            bytesUsed = getBufferSize(); // OK
        }

        layoutControl();

        bufferChanged(bytesUsed);
    }

    /**
     * Returns the length of the internal byte buffer.
     * 
     * @return length of byte buffer
     */
    public int getBufferSize() {
        return byteData.length;
    }

    /**
     * Retrieves the current length of visible data in the control. Remember to
     * distinguish between the length of the byte buffer and the actual length
     * of valid data. This is the zero based length of the shown data. It can't
     * exceed the buffer's length.
     * 
     * @return number of bytes of entered data.
     * @see adjustBuffer.
     */
    public int getBytesUsed() {

        return dataLength;
    }

    /**
     * Sets the current length of visible data in the control. So reduces the
     * info displayed. Remember to distinguish between the length of the byte
     * buffer and the actual length of valid data. This is the zero based length
     * of the shown data. It can't exceed the buffer's length.
     * 
     * @param size - the new buffer size in bytes
     * @see adjustBuffer.
     */
    public void setMaxSize(int size) {

        if (size == dataLength) {
            return;
        }

        if (size <= getBufferSize()) {
            dataLength = size;
            layoutControl();
        }
    }

    /**
     * Retrieves the number of bytes that are grouped together. A multiple of
     * this number must be equal to genNumBytesPerLine().
     * 
     * @return
     */
    public int getNumBytesGrouped() {
        return numBytesGrouped;
    }

    /**
     * Retrieves the number of bytes that are shown in each line. Lines are
     * filled with groups of numBytesGrouped Bytes
     * 
     * @return
     */
    public int getNumBytesPerLine() {
        return numBytesPerLine;
    }

    /**
     * @return true, if the control is in a mode where new bytes are inserted in
     *         place and these behind are shifted further, false if in overwrite
     *         mode.
     */
    public boolean isInsertMode() {
        return insertMode;
    }

    /**
     * Set the mode of the control. Set to true means that existing bytes are
     * shifted towards and over the end of the buffer while new ones are
     * inserted. Set to false means that the control is in overwrite mode. Newly
     * entered bytes overwrite existing ones.
     * 
     * @param insertMode
     */
    public void setInsertMode(boolean insertMode) {

        this.insertMode = insertMode;

        if (isInsertMode()) {
            hexpart.setCaret(caret.get(HEX_INSERT_CARET));
            stringpart.setCaret(caret.get(STRING_INSERT_CARET));
        } else {
            hexpart.setCaret(caret.get(HEX_OVERWRITE_CARET));
            stringpart.setCaret(caret.get(STRING_OVERWRITE_CARET));
        }
    }

    public boolean isVaryingMode() {
        return varyingMode;
    }

    public void setVaryingMode(boolean varyingMode) {

        if (varyingMode == this.varyingMode) {
            return;
        }

        this.varyingMode = varyingMode;

        hexFormatter();
        setCaretOffset(controlManager.getNibblePosition());

        // Paint background
        this.hexpart.redraw();
        this.stringpart.redraw();

        // Paint content
        this.hexpart.update();
        this.stringpart.update();
    }

    @Override
    public boolean setFocus() {
        return controlManager.setFocus();
    }

    /**
     * Sets the caret offset in nibbles.
     * 
     * @param nibbleOffset - caret offset, relative to the first nibble in the
     *        text.
     */
    public void setCaretOffset(int nibbleOffset) {

        int npos;
        if (isVaryingMode()) {
            npos = Math.min(getBytesUsed() * 2, nibbleOffset);
        } else {
            npos = Math.min(getBufferSize() * 2 - 1, nibbleOffset);
        }

        npos = controlManager.setNibblePosition(npos);

        controlManager.positionCursor(npos);
    }

    /**
     * Returns the caret position relative to the start of the first nibble of
     * the text.
     * 
     * @return the caret position (nibbles) relative to the start of the first
     *         nibble of the text.
     */
    public int getCaretOffset() {
        return controlManager.getNibblePosition();
    }

    /**
     * {@inheritDoc}
     */
    public void setEnabled(boolean enabled) {

        hexpart.setEnabled(enabled);
        stringpart.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    /**
     * {@inheritDoc}
     */
    public void focusGained(FocusEvent event) {

        controlManager.setActiveControl((StyledText)event.widget);

        int npos = controlManager.getNibblePosition();
        if (event.widget == stringpart) {
            if (npos % 2 == 1) {
                npos = npos - 1;
            }
        }

        Point sel = controlManager.selection2BytePosition();
        if (sel.x == sel.y) {
            setCaretOffset(npos);
        }

        Event ev = new Event();
        ev.widget = this;
        notifyListeners(SWT.FocusIn, ev);
    }

    /**
     * {@inheritDoc}
     */
    public void focusLost(FocusEvent arg0) {

        Event ev = new Event();
        ev.widget = this;
        notifyListeners(SWT.FocusOut, ev);
    }

    public void addCaretListener(CaretListener listener) {
        controlManager.addCaretListener(listener);
    }

    public void removeCaretListener(CaretListener listener) {
        controlManager.removeCaretListener(listener);
    }

    // ############################################
    // # ------ Start of protected methods ------ #
    // ############################################

    protected boolean copySelectionToClipboard() {

        TextTransfer transfer = TextTransfer.getInstance();
        Point sel = controlManager.selection2BytePosition();
        if (sel.y <= sel.x) {
            return true;
        }

        int von = sel.x;
        int bis = sel.y;

        byte[] buf = new byte[bis - von];
        System.arraycopy(byteData, von, buf, 0, bis - von); // OK

        if (controlManager.isHexControl()) {
            clipboard.setContents(new Object[] { ByteArrayHelper.bytearray2string(buf) }, new TextTransfer[] { transfer });
        } else {
            clipboard.setContents(new Object[] { new String(buf) }, new TextTransfer[] { transfer });
        }

        return true;
    }

    protected boolean deleteSelection() {

        // cut out the byte sub array, copy to clipboard, repaint
        Point sel = controlManager.selection2BytePosition();
        if (sel.y <= sel.x) {
            return false;
        }

        int von = sel.x;
        int bis = sel.y;

        // cut out:
        deleteBytes(von, bis - von);

        return true;
    }

    protected String getClipboardData() {

        TextTransfer transfer = TextTransfer.getInstance();
        String cbdata = (String)clipboard.getContents(transfer);
        return cbdata;
    }

    protected int pasteSelection(String cbdata) {

        if (cbdata == null || cbdata.length() == 0) {
            return -1;
        }

        int np2 = controlManager.getBytePosition();

        try {

            byte[] insdta;
            if (controlManager.isHexControl()) {
                insdta = ByteArrayHelper.string2bytearray(cbdata);
            } else {
                insdta = cbdata.getBytes();
            }

            if (insdta.length > getBufferSize() - getBytesUsed()) {
                Display.getCurrent().beep();
                return -1;
            }

            if (isInsertMode()) {
                return insertBytes(np2, insdta);
            } else {
                return replaceBytes(np2, insdta);
            }

        } catch (Exception e) {
            // Ignore exceptions due to invalid data from clipboard
        }

        return -1;
    }

    // #####################################################
    // # --- Start of possible package-private methods --- #
    // #####################################################

    private void replaceBuffer(byte[] b) {

        byteData = b;

        bufferChanged(b.length);
    }

    private void replaceByte(int pos, byte b) {

        if (pos < getBufferSize()) {
            int bytesUsed = getBytesUsed();
            if (byteData[pos] != b) {
                byteData[pos] = b;
                if (pos + 1 > getBytesUsed()) {
                    bytesUsed = pos + 1;
                }
            }
            bufferChanged(bytesUsed, false);
        }
    }

    private int replaceBytes(int pos, byte[] b) {

        if (pos < getBufferSize()) {
            int length = Math.min(b.length, getBufferSize() - pos);
            System.arraycopy(b, 0, byteData, pos, length); // OK
            int bytesUsed;
            if (pos + length > getBytesUsed()) {
                bytesUsed = pos + length;
            } else {
                bytesUsed = getBytesUsed();
            }
            bufferChanged(bytesUsed);
            return b.length;
        }

        return -1;
    }

    private void insertByte(int pos, byte b) {

        if (pos < getBufferSize()) {
            System.arraycopy(byteData, pos, byteData, pos + 1, getBufferSize() - pos - 1);
            setMaxSize(Math.min(getBytesUsed() + 1, getBufferSize()));
            replaceByte(pos, b);
        }
    }

    private int insertBytes(int pos, byte[] b) {

        if (pos < getBufferSize() - b.length) {
            System.arraycopy(byteData, pos, byteData, Math.min(pos + b.length, byteData.length), Math.max(byteData.length - (pos + b.length), 0)); // OK
            System.arraycopy(b, 0, byteData, pos, Math.min(b.length, byteData.length - pos)); // OK
            int bytesUsed = Math.min(getBytesUsed() + b.length, byteData.length);
            bufferChanged(bytesUsed);
            return b.length;
        }

        return -1;
    }

    void deleteByte(int pos) {

        if (pos < getBufferSize()) {
            System.arraycopy(byteData, pos + 1, byteData, pos, getBufferSize() - pos - 1); // OK
            byteData[getBufferSize() - 1] = PADDING_BYTE;
            int bytesUsed = Math.max(0, getBytesUsed() - 1);
            bufferChanged(bytesUsed);
        }
    }

    private void deleteBytes(int pos, int length) {

        if (pos < getBufferSize() - length) {
            int end = pos + length;
            System.arraycopy(byteData, end, byteData, pos, getBufferSize() - end); // OK
            System.arraycopy(new byte[length], 0, byteData, getBufferSize() - end, length);
            int bytesUsed = Math.max(0, getBytesUsed() - length);
            bufferChanged(bytesUsed);
            setCaretOffset(pos * 2);
        }
    }

    // ############################################
    // # ------- Start of private methods ------- #
    // ############################################

    private void hexFormatter() {

        int numVisibleBytes;
        if (isVaryingMode()) {
            numVisibleBytes = getBytesUsed();
        } else {
            numVisibleBytes = getBufferSize();
        }

        changesCounter++;
        System.out.println("#" + changesCounter + ": Formatting hex buffer (" + numVisibleBytes + ") ...");

        StringBuffer hexBuf = new StringBuffer(getFormattedHexBufferSize(numVisibleBytes));
        StringBuffer stringBuf = new StringBuffer(getBytesUsed());

        for (int i = 0; i < numVisibleBytes; i++) {

            byte currentByte = byteData[i];

            // if currentByte is not the first byte, append separator
            // Convert and append high nibble (bit7 - bit4)
            hexBuf.append(nibble2char(currentByte >> 4));

            // Convert and append low nibble (bit3 - bit0)
            hexBuf.append(nibble2char(currentByte));

            try {
                stringBuf.append(new String(new byte[] { currentByte }, Charset.defaultCharset().name()));
            } catch (UnsupportedEncodingException e) {
                stringBuf.append("~");
            }

            if ((i + 1) % numBytesGrouped == 0) {
                if ((i + 1) % numBytesPerLine == 0) {
                    // insert new line character
                    hexBuf.append(hexpart.getLineDelimiter());
                    stringBuf.append(stringpart.getLineDelimiter());
                } else {
                    // space between Groups
                    hexBuf.append(HEX_BYTE_DELIMITER);
                    hexBuf.append(HEX_GROUP_DELIMITER);
                }
            } else {
                // space between Bytes
                hexBuf.append(HEX_BYTE_DELIMITER);
            }
        }

        hexpart.setText(hexBuf.toString());
        stringpart.setText(stringBuf.toString());
    }

    private int getFormattedHexBufferSize(int bufferSize) {

        int charsperline = getHexCharsPerLine() + hexpart.getLineDelimiter().length();
        int line = bufferSize / getNumBytesPerLine();
        int lpos = bufferSize % getNumBytesPerLine();
        int co = (line * charsperline) + (lpos * getCharsPerHexByte()) + (lpos / getNumBytesGrouped() * 2);

        return co;
    }

    private void createContent(int style) {

        int innerStyle = SWT.BORDER;

        // setLayout(new FillLayout(SWT.VERTICAL));
        setLayout(createEditorLayout(1));
        setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, true));

        Composite scrollableHeaderArea = new Composite(this, SWT.NONE);
        scrollableHeaderArea.setLayout(createScrollableAreaLayout());

        Composite headerArea = new Composite(scrollableHeaderArea, SWT.NONE);
        headerArea.setLayout(createEditorLayout(0));

        headerArea.setLayoutData(createEditorAreaLayoutData());

        labelOffset = createHeadlineLabel(headerArea, innerStyle, "Offset");
        labelHex = createHeadlineLabel(headerArea, innerStyle, "Hex-Data");
        labelString = createHeadlineLabel(headerArea, innerStyle, "Dump");

        int numColumns = ((GridLayout)headerArea.getLayout()).numColumns;

        Composite scrollableContainer = new Composite(this, SWT.NONE);
        scrollableContainer.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, true));
        scrollableContainer.setLayout(new FillLayout());

        scrollableEditorArea = new ScrolledComposite(scrollableContainer, SWT.V_SCROLL | SWT.NONE);
        // scrollableEditorArea.setLayout(createScrollableAreaLayout());

        editorArea = new Composite(scrollableEditorArea, SWT.NONE);
        editorArea.setLayout(createEditorLayout(numColumns));

        editorArea.setLayoutData(createEditorAreaLayoutData());
        editorArea.addFocusListener(this);

        controlManager = new HexEditorControlManager(this, null);
        createOffsetArea(editorArea, innerStyle);
        createHexControl(editorArea, innerStyle, controlManager);
        createStringControl(editorArea, innerStyle, controlManager);

        scrollableEditorArea.setContent(editorArea);
        scrollableEditorArea.setExpandHorizontal(true);
        scrollableEditorArea.setExpandVertical(true);
        // scrollableEditorArea.setShowFocusedControl(true); // WDSCi!
        scrollableEditorArea.setAlwaysShowScrollBars(true);

        hexpart.setData(DATA_CONTROL, stringpart);
        stringpart.setData(DATA_CONTROL, hexpart);

        layoutControl();

        createCarets();

        hexFormatter();
    }

    private GridLayout createEditorLayout(int numColumns) {
        GridLayout editorAreaLayout = new GridLayout(numColumns, false);
        editorAreaLayout.marginHeight = 0;
        editorAreaLayout.marginWidth = 0;
        editorAreaLayout.verticalSpacing = 0;
        editorAreaLayout.horizontalSpacing = 0;
        return editorAreaLayout;
    }

    private GridData createEditorAreaLayoutData() {
        return new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
    }

    private GridLayout createScrollableAreaLayout() {
        GridLayout headerScrollableLayout = new GridLayout(1, false);
        headerScrollableLayout.marginHeight = 0;
        headerScrollableLayout.marginWidth = 0;
        headerScrollableLayout.verticalSpacing = 0;
        headerScrollableLayout.horizontalSpacing = 0;
        return headerScrollableLayout;
    }

    private Label createHeadlineLabel(Composite parent, int style, String text) {

        incrementLayoutColumns(parent);

        Composite labelComposite = new Composite(parent, style);
        GridLayout layout = createScrollableAreaLayout();
        layout.marginLeft = BORDER_WIDTH;
        labelComposite.setLayout(layout);
        labelComposite.setLayoutData(new GridData());

        Label label = new Label(labelComposite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        label.setText(text);

        return label;
    }

    private int checkStyle(int style) {

        int checkedStyle = style & (SWT.BORDER);

        return checkedStyle;
    }

    private void createOffsetArea(Composite parent, int style) {

        offsetArea = new Composite(parent, style);
        offsetArea.setFont(monofont);

        GridLayout offsetAreaLayout = new GridLayout();
        offsetAreaLayout.marginHeight = BORDER_WIDTH;
        // offsetAreaLayout.marginWidth = BORDER_WIDTH; // WDSCi
        offsetAreaLayout.marginWidth = 5;
        offsetAreaLayout.verticalSpacing = LINE_SPACING;
        offsetAreaLayout.horizontalSpacing = 0;
        offsetArea.setLayout(offsetAreaLayout);
    }

    private void createHexControl(Composite parent, int style, HexEditorControlManager hexCursorPositioner) {

        hexpart = new StyledText(parent, style);

        // hexpart.setData("*HEX");
        hexpart.setFont(monofont);
        hexpart.addVerifyKeyListener(new HexControlVerifyListener(this, hexCursorPositioner));

        // Focus forwarding
        hexpart.addFocusListener(this);

        hexpart.setLineSpacing(LINE_SPACING);
        // hexpart.setMargins(BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH,
        // BORDER_WIDTH); // WDSCi!
        hexpart.addMouseListener(hexCursorPositioner);
        hexpart.addKeyListener(hexCursorPositioner);
        hexpart.addPaintListener(new BackgroundPainter(getNumBytesPerLine(), getCharsPerHexByte(), getNumBytesGrouped(),
            getCharsPerGroupHexDelimiter()));

        hexCursorPositioner.configureControl(hexpart, HexEditorControlManager.CONTENT_TYPE_NIBBLE, getNumBytesPerLine(), getNumBytesGrouped(),
            getCharsPerHexByte(), getHexCharsPerLine(), getCharsPerGroupHexDelimiter(), stringpart);
    }

    private void createStringControl(Composite parent, int style, HexEditorControlManager hexCursorPositioner) {

        stringpart = new StyledText(parent, style);
        // stringpart.setData("*STRING");
        stringpart.setFont(monofont);
        stringpart.addVerifyKeyListener(new StringControlVerifyKeyListener(this, hexCursorPositioner));

        // Focus forwarding
        stringpart.addFocusListener(this);

        stringpart.setLineSpacing(LINE_SPACING);
        // stringpart.setMargins(BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH,
        // BORDER_WIDTH); // WDSCi!
        stringpart.addMouseListener(hexCursorPositioner);
        stringpart.addKeyListener(hexCursorPositioner);
        stringpart.addPaintListener(new BackgroundPainter(getNumBytesPerLine(), getCharsPerStringByte(), getNumBytesPerLine(),
            getCharsPerGroupStringDelimiter()));

        hexCursorPositioner.configureControl(stringpart, HexEditorControlManager.CONTENT_TYPE_STRING, getNumBytesPerLine(), getNumBytesPerLine(),
            getCharsPerStringByte(), getStringCharsPerLine(), getCharsPerGroupStringDelimiter(), hexpart);
    }

    private void incrementLayoutColumns(Composite parent) {
        GridLayout layout = (GridLayout)parent.getLayout();
        layout.numColumns++;
    }

    private void layoutControl() {

        configureControls(controlManager);

        // Hack to fix a problem in computeSize(), which
        // calculates a wrong size for the "insert" cursor.
        boolean insertMode = isInsertMode();
        if (insertMode) {
            setInsertMode(false);
        }

        updateOffsetArea();

        scrollableEditorArea.setMinSize(editorArea.computeSize(SWT.DEFAULT, 600));
        editorArea.layout(true);
        editorArea.redraw();

        GridData gridData;
        gridData = (GridData)labelOffset.getParent().getLayoutData();
        gridData.widthHint = offsetArea.getClientArea().width;

        gridData = (GridData)labelHex.getParent().getLayoutData();
        gridData.widthHint = hexpart.getClientArea().width;

        gridData = (GridData)labelString.getParent().getLayoutData();
        gridData.horizontalAlignment = SWT.BEGINNING;
        gridData.verticalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = false;
        gridData.grabExcessVerticalSpace = true;
        gridData.widthHint = stringpart.getClientArea().width;

        if (insertMode) {
            setInsertMode(true);
        }
    }

    /**
     * The intention of this method is to source out the calculation of the
     * sizes of the part controls into an own method which is called by
     * constructor and whenever size matters.
     */
    private void configureControls(HexEditorControlManager hexCursorPositioner) {

        hexpart.setLayoutData(getGridData(hexpart, getHexCharsPerLine()));
        stringpart.setLayoutData(getGridData(stringpart, getStringCharsPerLine()));

        GridData offsetAreaLayoutData = getGridData(offsetArea, LENGTH_OFFSET_LABEL);
        offsetAreaLayoutData.widthHint += BORDER_WIDTH * 2;
        offsetArea.setLayoutData(offsetAreaLayoutData);

        GridData hexPartGridData = (GridData)hexpart.getLayoutData();
        // offsetAreaLayoutData.heightHint = hexPartGridData.heightHint +
        // hexpart.getBottomMargin() + hexpart.getTopMargin(); // WDSCi!
        offsetAreaLayoutData.heightHint = hexPartGridData.heightHint;
    }

    private void updateOffsetArea() {

        if (getNumTotalLines() == offsetArea.getChildren().length) {
            return;
        }
        deleteOffsetLables();
        createOffsetLabels(getNumTotalLines());
    }

    private void deleteOffsetLables() {
        for (Control control : offsetArea.getChildren()) {
            control.dispose();
        }
    }

    private void createOffsetLabels(int numRows) {
        for (int i = 0; i < numRows; i++) {
            createOffsetLabel(offsetArea, "" + Integer.toHexString(i * getNumBytesPerLine()));
        }
        // offsetArea.layout(true);
    }

    private void createOffsetLabel(Composite parent, String text) {

        Label label = new Label(parent, SWT.NONE);
        label.setFont(monofont);
        GridData offsetHeadlineLayoutData = new GridData();
        offsetHeadlineLayoutData.horizontalAlignment = GridData.END;
        offsetHeadlineLayoutData.grabExcessHorizontalSpace = true;
        label.setLayoutData(offsetHeadlineLayoutData);
        label.setText(StringHelper.getFixLengthLeading(text, LENGTH_OFFSET_LABEL).replaceAll(" ", "0"));
        label.setAlignment(SWT.RIGHT);
        label.setVisible(true);
    }

    private int getCharsPerGroupHexDelimiter() {
        return HEX_GROUP_DELIMITER.length();
    }

    private int getCharsPerGroupStringDelimiter() {
        return 0;
    }

    private int getCharsPerHexByte() {
        return 2 + HEX_BYTE_DELIMITER.length();
    }

    private int getCharsPerStringByte() {
        return 1;
    }

    private GridData getGridData(Control control, int charsPerLine) {

        return getGridData(control, charsPerLine, getNumTotalLines());
    }

    private GridData getGridData(Control control, int charsPerLine, int numLines) {

        GridData gd = (GridData)control.getLayoutData();
        if (gd == null) {
            gd = createEditorAreaLayoutData();
        }

        gd.widthHint = (FontHelper.getFontCharWidth(control) * charsPerLine);
        gd.heightHint = (FontHelper.getFontCharHeight(control) * numLines) + (numLines - 1) * LINE_SPACING;
        gd.horizontalIndent = 0;
        gd.verticalIndent = 0;

        return gd;
    }

    private void createCarets() {

        if (caret != null) {
            return;
        }

        caret = new HashMap<String, Caret>();

        caret.put(HEX_INSERT_CARET, hexpart.getCaret());
        caret.put(HEX_OVERWRITE_CARET, createOverwriteCaret(hexpart));

        caret.put(STRING_INSERT_CARET, stringpart.getCaret());
        caret.put(STRING_OVERWRITE_CARET, createOverwriteCaret(stringpart));
    }

    private Caret createOverwriteCaret(StyledText control) {

        Caret baseCaret = control.getCaret();
        Point size = baseCaret.getSize();
        size.x = FontHelper.getFontCharWidth(control);

        Caret overwriteCaret = new Caret(baseCaret.getParent(), SWT.NONE);
        overwriteCaret.setSize(size);

        return overwriteCaret;
    }

    private int getHexCharsPerLine() {
        return (numBytesPerLine * 3) + (2 * numBytesPerLine / numBytesGrouped) - 3;
    }

    private int getStringCharsPerLine() {
        return numBytesPerLine;
    }

    private int getNumLinesUsed() {
        if (isVaryingMode()) {
            return getNumLines(getBytesUsed());
        } else {
            return getNumTotalLines();
        }
    }

    private int getNumTotalLines() {
        return getNumLines(getBufferSize());
    }

    private int getNumLines(int length) {

        int visibleLength;
        // if (isVaryingMode()) {
        // visibleLength = getVisibleLength();
        // } else {
        // visibleLength = getBufferSize();
        // }
        visibleLength = length;

        int minLines = visibleLength / numBytesPerLine;
        if (minLines * numBytesPerLine < visibleLength) {
            minLines++;
        }

        if (minLines != 0) {
            return minLines;
        }

        return getParent().getClientArea().height / FontHelper.getFontCharHeight(getParent());
    }

    private void bufferChanged(int bytesUsed) {
        bufferChanged(bytesUsed, true);
    }

    private void bufferChanged(int bytesUsed, boolean doFormat) {

        System.out.println("Buffer changed!");

        if (bytesUsed != getBytesUsed()) {
            setMaxSize(bytesUsed);
        }

        if (hexpart != null) {
            hexFormatter(); // TODO: do not format on byte replace

            // ensure cursor stays in visible area
            setCaretOffset(controlManager.getNibblePosition());
        }

        callModifiedListeners();
    }

    /**
     * Calls all registered listeners. Does not provide usable event
     * information. Thus the receiver is required to ask details.
     */
    private void callModifiedListeners() {

        Event ev = new Event();
        ev.data = getByteData();
        ev.widget = this;
        notifyListeners(SWT.Modify, ev);
    }

    /**
     * Make a nibble (0000-1111) to a character ('0'..'F')
     * 
     * @param nibble
     * @return
     */
    private char nibble2char(int nibble) {

        // Filter the low nibble (we know then that 0 <= nibble < 16!)
        nibble &= 0x0F;

        // Convert to '0' - '9' or 'A' - 'F'
        if (nibble < 10) {
            return (char)('0' + nibble);
        } else {
            return (char)('A' + nibble - 10);
        }
    }

    /**
     * {@link Inherited}
     */
    @Override
    public void dispose() {

        monofont.dispose();
        clipboard.dispose();

        super.dispose();
    }

    private class StringControlVerifyKeyListener extends AbstractHexEditorVerifyKeyListener {

        public StringControlVerifyKeyListener(HexEditorInternal swtHexEdit, HexEditorControlManager hexCursorPositioner) {
            super(swtHexEdit, hexCursorPositioner);
        }

        protected boolean performEdit(VerifyEvent event) {

            if (isCharDigit(event)) {

                // Cut out selection:
                deleteSelection();

                int bpos = controlManager.caret2BytePosition();

                // insert or overwrite a byte
                if (isInsertMode()) {
                    if (getBytesUsed() < getBufferSize()) {
                        // before current byte: insert a byte into the array
                        insertByte(bpos, (byte)(event.character));
                    } else {
                        Display.getCurrent().beep();
                        return false;
                    }
                } else {
                    // preserve array bounds
                    if (bpos < getBufferSize()) {
                        replaceByte(bpos, (byte)event.character);
                    }
                }

                moveNibbleCursor(CURSOR_RIGHT, 2);

                return true;
            }

            return false;
        }

        @Override
        protected void handleTraversal(VerifyEvent event) {
            if (hexpart != null) {
                hexpart.setFocus();
            }
        }

        private boolean isCharDigit(VerifyEvent event) {

            if ((byte)event.character == 0) {
                return false;
            }

            if (' ' == event.character) {
                return true;
            }

            if (!Character.isWhitespace(event.character)) {
                return true;
            }

            return false;
        }
    }

    private class HexControlVerifyListener extends AbstractHexEditorVerifyKeyListener {

        private int lastInsertBytePos = -1;

        public HexControlVerifyListener(HexEditorInternal swtHexEdit, HexEditorControlManager hexCursorPositioner) {
            super(swtHexEdit, hexCursorPositioner);
        }

        protected boolean performEdit(VerifyEvent event) {

            if (isHexDigit(event)) {

                // Cut out selection:
                deleteSelection();

                int relpos = controlManager.getCaretByteRelativePosition();
                int bpos = controlManager.caret2BytePosition();

                boolean canOverwriteLowNibble;
                if (!isInsertMode() || lastInsertBytePos == bpos) {
                    canOverwriteLowNibble = true;
                } else {
                    canOverwriteLowNibble = false;
                }

                // Reset last insert position
                lastInsertBytePos = -1;

                // insert or overwrite a byte
                if (relpos == 1) {
                    // set the low-nibble
                    if (canOverwriteLowNibble) {
                        byte b = (byte)((byteData[bpos] & 0xF0) | char2nibble(event.character));
                        replaceByte(bpos, b);
                    } else {
                        Display.getCurrent().beep();
                        return false;
                    }
                } else {
                    if (isInsertMode()) {
                        if (relpos == 0 && getBytesUsed() < getBufferSize()) {
                            // before current byte: insert a byte into the array
                            insertByte(bpos, (byte)(char2nibble(event.character) << 4));
                            lastInsertBytePos = bpos;
                        } else {
                            Display.getCurrent().beep();
                            return false;
                        }
                    } else {
                        int maxPos;
                        if (isVaryingMode()) {
                            maxPos = getBytesUsed();
                        } else {
                            maxPos = getBufferSize();
                        }
                        // preserve array bounds
                        if (bpos < maxPos) {
                            if (relpos == 0) {
                                // write high nibble
                                byte b = (byte)((byteData[bpos] & 0x0F) | (char2nibble(event.character) << 4));
                                replaceByte(bpos, b);
                            }
                        }
                    }
                }

                moveNibbleCursor(CURSOR_RIGHT);

                return true;
            }

            return false;
        }

        @Override
        protected void handleTraversal(VerifyEvent event) {
            if (stringpart != null) {
                stringpart.setFocus();
            }
        }

        private boolean isHexDigit(VerifyEvent event) {
            return event.character >= '0' && event.character <= '9' || event.character >= 'A' && event.character <= 'F' || event.character >= 'a'
                && event.character <= 'f';
        }

        /**
         * Converts a hex digit into an integer (bit3 - bit0)
         * 
         * @param c hex digit to be converted
         * @return value of c if it is a hex digit, -1 otherwise
         */
        private int char2nibble(char c) {

            if (c >= '0' && c <= '9') {
                return c - '0';
            }

            if (c >= 'A' && c <= 'F') {
                return 10 + c - 'A';
            }

            if (c >= 'a' && c <= 'f') {
                return 10 + c - 'a';
            }

            return -1;
        }
    }

    private class BackgroundPainter implements PaintListener {

        private int bytesPerLine;
        private int charsPerByte;
        private int numBytesGrouped;
        private int charsPerGroupDelimiter;

        public BackgroundPainter(int bytesPerLine, int charsPerByte, int numBytesGrouped, int charsPerGroupDelimiter) {
            this.bytesPerLine = bytesPerLine;
            this.charsPerByte = charsPerByte;
            this.numBytesGrouped = numBytesGrouped;
            this.charsPerGroupDelimiter = charsPerGroupDelimiter;
        }

        public void paintControl(PaintEvent event) {
            paintBackground(event);
        }

        public void paintBackground(PaintEvent event) {

            Scrollable textWidget = (Scrollable)event.getSource();

            int rows = getNumLinesUsed();
            Color color = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY); // ColorHelper.getBackgroundColorOfProtectedAreas();

            GC gc = event.gc;
            gc.setBackground(color);

            int charWidht = FontHelper.getFontCharWidth(textWidget);
            int charHeight = FontHelper.getFontCharHeight(textWidget);
            int rowsHeight = rows * charHeight;

            int clientWidth = textWidget.getClientArea().width;
            int clientHeight = textWidget.getClientArea().height;

            Rectangle paintArea = new Rectangle(0, 0, 0, 0);
            paintArea.x = BORDER_WIDTH;
            paintArea.y = BORDER_WIDTH + rowsHeight + ((getNumLinesUsed()) * LINE_SPACING);
            paintArea.width = clientWidth - BORDER_WIDTH * 2;
            paintArea.height = clientHeight - paintArea.y - BORDER_WIDTH;
            if (paintArea.height > 0) {
                gc.fillRectangle(paintArea);
            }

            // Remaining space of last row
            int size;
            if (isVaryingMode()) {
                size = getBytesUsed();
            } else {
                size = getBufferSize();
            }

            if (size % bytesPerLine == 0) {
                return;
            }

            int numCharsLeft = size % bytesPerLine;
            int numGroups = numCharsLeft / numBytesGrouped;

            int indentX = (numCharsLeft) * charsPerByte * charWidht;
            indentX = indentX + numGroups * charsPerGroupDelimiter * charWidht;

            paintArea.x = paintArea.x + indentX;
            paintArea.y = paintArea.y - charHeight - LINE_SPACING;
            paintArea.width = paintArea.width - indentX;

            if (paintArea.height > 0) {
                paintArea.height = charHeight + LINE_SPACING;
            } else {
                paintArea.height = charHeight;
            }

            gc.fillRectangle(paintArea);

            return;
        }
    }
}
