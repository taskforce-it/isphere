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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import biz.isphere.core.Messages;
import biz.isphere.core.internal.FontHelper;
import biz.isphere.core.swt.widgets.hexeditor.internal.BinaryContent.RangeSelection;

/**
 * Status line component of the editor. Displays the current position, the
 * insert/overwrite status and the value at the cursor position.
 */
public final class StatusLine extends Composite {

    public static final String STATUS_LINE_ID = "biz.isphere.core.dataspaceeditor.AbstractDataSpaceEditorActionBarContributor.StatusLine";

    private static final String EQUALITY_SIGN = " = "; //$NON-NLS-1$
    private static final String BINARY_PREFIX = "00000000"; //$NON-NLS-1$
    private static final String HEX_PREFIX = "0x"; //$NON-NLS-1$
    private static final String CLOSE_PARANTHESIS = ")"; //$NON-NLS-1$
    private static final String OPEN_PARANTHESIS = " ("; //$NON-NLS-1$
    private static final String MINUS = " - "; //$NON-NLS-1$

    private Label positionLabel;
    private Label valueLabel;
    private Label insertModeLabel;

    /**
     * Create a status line part
     * 
     * @param parent parent in the widget hierarchy
     */
    public StatusLine(Composite parent) {
        super(parent, SWT.NONE);
        initialize();
    }

    private void initialize() {

        GridLayout statusLayout = new GridLayout(0, false);
        statusLayout.marginHeight = 0;
        setLayout(statusLayout);

        addSeparator(this);
        insertModeLabel = addLabel(this, Math.max(Messages.Mode_Insert.length(), Messages.Mode_Overwrite.length()));

        /*
         * size = start + MINUS + end + PARENTHESIS + start + MINUS + end +
         * PARENTHESIS
         */
        addSeparator(this);
        positionLabel = addLabel(this, 10 + 3 + 10 + 2 + 4 + 3 + 4 + 1);

        /*
         * size = decimal + EQUALITY_SIGN + hex + EQUALITY_SIGN + binary
         */
        addSeparator(this);
        valueLabel = addLabel(this, 2 + 3 + 4 + 3 + 8);
    }

    private void addSeparator(Composite parent) {

        Label separator = new Label(parent, SWT.SEPARATOR);
        GridData gridData = new GridData();
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = SWT.FILL;
        separator.setLayoutData(gridData);

        incrementColumns(parent);
    }

    private Label addLabel(Composite parent, int numChars) {

        Label label = new Label(this, SWT.SHADOW_NONE);
        GridData gridData1 = new GridData(FontHelper.getFontCharWidth(label) * numChars, SWT.DEFAULT);
        label.setLayoutData(gridData1);

        incrementColumns(parent);

        return label;
    }

    private void incrementColumns(Composite parent) {

        GridLayout layout = (GridLayout)getLayout();
        layout.numColumns++;
    }

    /**
     * Update the position status and value.
     * 
     * @param position
     * @param value
     * @see #updatePosition
     * @see #updateValue
     */
    public void updatePositionValue(long position, byte value) {

        updatePosition(position);
        updateValue(value);
    }

    /**
     * Update the selection status and value.
     * 
     * @param rangeSelection
     * @param value
     * @see #updateSelection
     * @see #updateValue
     */
    public void updateSelectionValue(RangeSelection rangeSelection, byte value) {
        if (rangeSelection == null) {
            throw new IllegalArgumentException("Parameter 'rangeSelection' must not be null.");
        }
        updateSelection(rangeSelection);
        updateValue(value);
    }

    /**
     * Update the insert/overwrite mode.
     * 
     * @param insert <code>true</code> for insert mode, or <code>false</code>
     *        for overwrite
     */
    public void updateInsertMode(boolean insert) {
        if (isDisposed() || insertModeLabel.isDisposed()) {
            return;
        }

        if (insert) {
            insertModeLabel.setText(Messages.Mode_Insert);
        } else {
            insertModeLabel.setText(Messages.Mode_Overwrite);
        }
    }

    /**
     * Update the position status. Displays its decimal and hex value.
     * 
     * @param position position to display
     */
    private void updatePosition(long position) {
        if (isDisposed() || positionLabel.isDisposed()) {
            return;
        }

        String text = getDecimalValue(position);

        positionLabel.setText(text);
    }

    /**
     * Update the value. Displays its decimal, hex and binary value
     * 
     * @param value value to display
     */
    private void updateValue(byte value) {
        if (isDisposed() || positionLabel.isDisposed()) {
            return;
        }

        int unsignedValue = value & 0xff;
        String text = getDecimalValue(unsignedValue) + EQUALITY_SIGN + HEX_PREFIX + getHexValue(unsignedValue) + EQUALITY_SIGN + getBinaryValue(unsignedValue);

        valueLabel.setText(text);
    }

    /**
     * Update the selection status. Displays its decimal and hex values for
     * start and end selection
     * 
     * @param rangeSelection selection array to display: [0] = start, [1] = end
     */
    private void updateSelection(RangeSelection rangeSelection) {
        if (isDisposed() || positionLabel.isDisposed()) {
            return;
        }

        long from = rangeSelection.start;
        long to = rangeSelection.end;
        String text = getDecimalValue(from) + MINUS + getDecimalValue(to) + OPEN_PARANTHESIS + getHexValue(from) + MINUS + getHexValue(to) + CLOSE_PARANTHESIS;

        positionLabel.setText(text);
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
}
