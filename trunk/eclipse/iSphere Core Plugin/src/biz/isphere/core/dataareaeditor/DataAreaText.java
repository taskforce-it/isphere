/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataareaeditor;

import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Composite;

import biz.isphere.base.internal.StringHelper;

public class DataAreaText extends StyledText {

    int maxLength;
    int lineLength;
    int lineLengthIcludingCR;
    String eolChar;
    boolean isModifying = false;

    public DataAreaText(Composite aParent, int aStyle, int aLineLength) {
        super(aParent, aStyle);
        lineLength = aLineLength;
        lineLengthIcludingCR = lineLength + 1;
        eolChar = new String("¤");

        addTextListeners(this);
    }

    @Override
    public void setTextLimit(int aLimit) {
        super.setTextLimit(-1);
        maxLength = aLimit;
    }

    @Override
    public int getTextLimit() {
        return maxLength;
    }

    @Override
    public void setText(String aText) {
        super.setText(toScreen(StringHelper.getFixLength(aText, maxLength)));
    }

    @Override
    public String getText() {
        return fromScreen(super.getText());
    }

    private String fromScreen(String aValue) {
        String tValue;
        if (aValue.endsWith(eolChar)) {
            tValue = aValue.substring(0, aValue.length() - 1);
        } else {
            tValue = aValue;
        }

        String[] tLines = tValue.replaceAll("\\r", "").split("\\n");
        StringBuilder stringValue = new StringBuilder();
        for (String line : tLines) {
            stringValue.append(line);
        }
        return stringValue.toString();
    }

    private String toScreen(String stringValue) {
        StringBuffer wrapped = new StringBuffer();

        int offset = 0;
        while (offset + lineLength < stringValue.length()) {
            if (offset > 0) {
                wrapped.append("\n");
            }
            wrapped.append(stringValue.substring(offset, offset + lineLength));
            offset = offset + lineLength;
        }

        if (offset < stringValue.length()) {
            if (offset > 0) {
                wrapped.append("\n");
            }
            wrapped.append(stringValue.substring(offset));
        }

        return wrapped.toString() + eolChar;
    }

    private void addTextListeners(StyledText aText) {
        aText.addVerifyListener(new VerifyListener() {

            public void verifyText(VerifyEvent anEvent) {
                if (!isModifying) {
                    StyledText textControl = (StyledText)anEvent.getSource();
                    String text = fromScreen(textControl.getText());
                    int lengthCurrent = StringHelper.trimR(text).length();
                    int lengthRemoved = anEvent.end - anEvent.start;
                    int lengthInserted = fromScreen(anEvent.text).length();
                    int newLength = lengthCurrent - lengthRemoved + lengthInserted;
                    if (newLength <= textControl.getTextLimit()) {
                        anEvent.doit = true;
                    } else {
                        anEvent.doit = false;
                    }
                }
            }
        });

        aText.addExtendedModifyListener(new ExtendedModifyListener() {
            public void modifyText(ExtendedModifyEvent anEvent) {
                if (!isModifying) {
                    isModifying = true;
                    DataAreaText textControl = (DataAreaText)anEvent.getSource();
                    String text = textControl.getText();
                    int p = textControl.getCaretOffset();
                    int limit = textControl.getTextLimit();
                    if (text.length() >= limit) {
                        text = text.substring(0, limit);
                    } else {
                        text = StringHelper.getFixLength(text, limit);
                    }
                    textControl.setText(text);
                    textControl.setCaretOffset(maxCaretPos(p));
                    isModifying = false;
                }
            }
        });
    }

    /**
     * Fixes the caret position when a character is inserted beyond the last
     * position in 'insert' mode.
     * 
     * @param aPosition - current caret position
     * @return fixed caret position
     */
    protected int maxCaretPos(int aPosition) {
        int numLines = getTextLimit() / lineLength;
        int maxPos = getTextLimit() + numLines - 1;
        if (aPosition > maxPos) {
            return maxPos;
        }
        return aPosition;
    }

    public int getCaretRow() {
        int position = getCarePosition();
        int row = position / (lineLengthIcludingCR);
        if (row * (lineLengthIcludingCR) < position) {
            row++;
        }
        return row;
    }

    public int getCaretColumn() {
        int column = getCarePosition() - numberOfColumnsOfPrecedingColumns();
        return column;
    }

    private int numberOfColumnsOfPrecedingColumns() {
        return (getCaretRow() - 1) * lineLengthIcludingCR;
    }

    private int getCarePosition() {
        return maxCaretPos(getCaretOffset() + 1);
    }
    
}
