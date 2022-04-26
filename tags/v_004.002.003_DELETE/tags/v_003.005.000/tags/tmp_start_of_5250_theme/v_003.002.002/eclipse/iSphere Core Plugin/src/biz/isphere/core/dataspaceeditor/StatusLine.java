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
 * Contributors:
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor;

import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import biz.isphere.core.Messages;
import biz.isphere.core.internal.FontHelper;

/**
 * Status line component of the editor. Displays the current position, the
 * insert/overwrite status and the value at the cursor position.
 */
public final class StatusLine {

    public static final String STATUS_LINE_ID = "biz.isphere.core.dataspaceeditor.AbstractDataSpaceEditorActionBarContributor.StatusLine";

    private static final String EQUALITY_SIGN = " = "; //$NON-NLS-1$
    private static final String BINARY_PREFIX = "00000000"; //$NON-NLS-1$
    private static final String HEX_PREFIX = "0x"; //$NON-NLS-1$
    private static final String CLOSE_PARANTHESIS = ")"; //$NON-NLS-1$
    private static final String OPEN_PARANTHESIS = " ("; //$NON-NLS-1$
    private static final String MINUS = " - "; //$NON-NLS-1$

    private CLabel modeLabel;
    private CLabel positionLabel;
    private CLabel valueLabel;
    private CLabel messageLabel;

    private int modeWidthHint = -1;
    private int positionWidthHint = -1;
    private int valueWidthHint = -1;
    private int messageWidthHint = -1;

    boolean showMode = true;
    boolean showPosition = true;
    boolean showValue = true;
    boolean showMessage = true;

    private String mode = "";
    private long start;
    private long end = -1;
    private byte value;
    private String message = "";

    public void fill(Composite parent) {

        if (showMode) {
            addSeparator(parent);
            modeLabel = addLabel(parent, Math.max(Messages.Mode_Insert.length(), Messages.Mode_Overwrite.length()), modeWidthHint);
            modeWidthHint = getWidthHint(modeLabel);
        }

        /*
         * size = start + MINUS + end + PARENTHESIS + start + MINUS + end +
         * PARENTHESIS
         */
        if (showPosition) {
            addSeparator(parent);
            positionLabel = addLabel(parent, 10 + 3 + 10 + 2 + 4 + 3 + 4 + 1, positionWidthHint);
            positionWidthHint = getWidthHint(positionLabel);
        }

        /*
         * size = decimal + EQUALITY_SIGN + hex + EQUALITY_SIGN + binary
         */
        if (showValue) {
            addSeparator(parent);
            valueLabel = addLabel(parent, 2 + 3 + 4 + 3 + 8, valueWidthHint);
            valueWidthHint = getWidthHint(valueLabel);
        }

        if (showMessage) {
            addSeparator(parent);
            messageLabel = addLabel(parent, 200, messageWidthHint);
            messageWidthHint = getWidthHint(messageLabel);
        }

        updateControls();
    }

    public void setShowMode(boolean show) {
        showMode = show;
    }

    public void setShowPosition(boolean show) {
        showPosition = show;
    }

    public void setShowValue(boolean show) {
        showValue = show;
    }

    public void setShowMessage(boolean show) {
        showMessage = show;
    }

    public void setMessage(String message) {
        this.message = message;
        updateControls();
    }

    public void setErrorMessage(String message) {
        setMessage(message);
    }

    /**
     * Update the position status and value.
     * 
     * @param position
     * @param value
     * @see #formatPosition
     * @see #formatValue
     */
    public void setPosition(long position, byte value) {
        this.start = position;
        this.end = -1;
        this.value = value;
        updateControls();
    }

    /**
     * Update the selection status and value.
     * 
     * @param rangeSelection
     * @param value
     * @see #formatformatSelection
     * @see #formatValue
     */
    public void setSelection(Point rangeSelection, byte value) {
        if (rangeSelection == null) {
            throw new IllegalArgumentException("Parameter 'rangeSelection' must not be null.");
        }
        this.start = rangeSelection.x;
        this.end = rangeSelection.y - 1;
        updateControls();
    }

    /**
     * Update the insert/overwrite mode.
     * 
     * @param insert <code>true</code> for insert mode, or <code>false</code>
     *        for overwrite
     */
    public void setInsertMode(boolean insert) {
        if (insert) {
            mode = Messages.Mode_Insert;
        } else {
            mode = Messages.Mode_Overwrite;
        }
        updateControls();
    }

    private void addSeparator(Composite parent) {

        Label separator = new Label(parent, SWT.SEPARATOR);
        StatusLineLayoutData gridData = new StatusLineLayoutData();
        separator.setLayoutData(gridData);
    }

    private CLabel addLabel(Composite parent, int numChars, int widthHint) {

        CLabel label = new CLabel(parent, SWT.SHADOW_NONE);
        StatusLineLayoutData gridData1 = new StatusLineLayoutData();
        if (widthHint > 0) {
            gridData1.widthHint = widthHint;
        } else {
            gridData1.widthHint = (FontHelper.getFontCharWidth(label) * numChars) + 6;
        }
        label.setLayoutData(gridData1);

        return label;
    }

    private void updateControls() {

        if (isOKForUpdate(messageLabel)) {
            if (message != null) {
                messageLabel.setText(message);
                messageLabel.setToolTipText(message);
            } else {
                messageLabel.setText(""); //$NON-NLS-1$
                messageLabel.setToolTipText(""); //$NON-NLS-1$
            }
            messageLabel.setVisible(showMessage);
        }

        if (isOKForUpdate(modeLabel)) {
            modeLabel.setText(mode);
            modeLabel.setVisible(showMode);
        }

        if (isOKForUpdate(positionLabel)) {
            String text;
            if (end < 0) {
                text = formatPosition(start);
            } else {
                text = formatformatSelection(start, end);
            }
            positionLabel.setText(text);
            positionLabel.setVisible(showPosition);
        }

        if (isOKForUpdate(valueLabel)) {
            valueLabel.setText(formatValue(value));
            valueLabel.setVisible(showValue);
        }
    }

    private boolean isOKForUpdate(Control control) {
        return control != null && !control.isDisposed();
    }

    /**
     * Update the position status. Displays its decimal and hex value.
     * 
     * @param position position to display
     */
    private String formatPosition(long position) {
        return getDecimalValue(position) + OPEN_PARANTHESIS + getHexValue(start) + CLOSE_PARANTHESIS;
    }

    /**
     * Update the value. Displays its decimal, hex and binary value
     * 
     * @param value value to display
     */
    private String formatValue(byte value) {
        int unsignedValue = value & 0xff;
        return getDecimalValue(unsignedValue) + EQUALITY_SIGN + getHexValue(unsignedValue) + EQUALITY_SIGN + getBinaryValue(unsignedValue);
    }

    /**
     * Update the selection status. Displays its decimal and hex values for
     * start and end selection
     * 
     * @param rangeSelection selection array to display: [0] = start, [1] = end
     */
    private String formatformatSelection(long start, long end) {
        return getDecimalValue(start) + MINUS + getDecimalValue(end) + OPEN_PARANTHESIS + getHexValue(start) + MINUS + getHexValue(end)
            + CLOSE_PARANTHESIS;
    }

    private String getDecimalValue(long from) {
        return Long.toString(from);
    }

    private String getHexValue(long from) {
        return HEX_PREFIX + Long.toHexString(from).toUpperCase();
    }

    private String getBinaryValue(int unsignedValue) {
        String binaryText = BINARY_PREFIX + Integer.toBinaryString(unsignedValue);
        binaryText = binaryText.substring(binaryText.length() - 8);
        return binaryText;
    }

    private int getWidthHint(Control control) {
        StatusLineLayoutData layoutData = (StatusLineLayoutData)control.getLayoutData();
        return layoutData.widthHint;
    }
}
