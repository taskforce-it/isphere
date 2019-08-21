/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefileeditor;

import java.io.Serializable;

public class FieldFormat implements Serializable {

    private static final long serialVersionUID = -7764037395725259022L;

    private String type;
    private boolean vary;
    private int bytes;
    private int length;
    private int decimalPositions;

    public static final String ITV = "*ITV"; //$NON-NLS-1$
    public static final String SYP = "*SYP"; //$NON-NLS-1$
    public static final String DTS = "*DTS"; //$NON-NLS-1$
    public static final String CCHAR = "*CCHAR"; //$NON-NLS-1$
    public static final String BIN = "*BIN"; //$NON-NLS-1$
    public static final String UBIN = "*UBIN"; //$NON-NLS-1$
    public static final String DEC = "*DEC"; //$NON-NLS-1$
    public static final String SPP = "*SPP"; //$NON-NLS-1$
    public static final String HEX = "*HEX"; //$NON-NLS-1$
    public static final String CHAR = "*CHAR"; //$NON-NLS-1$
    public static final String QTDCHAR = "*QTDCHAR"; //$NON-NLS-1$

    public FieldFormat() {
        type = "";
        vary = false;
        bytes = 0;
        length = 0;
        decimalPositions = 0;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isVary() {
        return vary;
    }

    public void setVary(boolean vary) {
        this.vary = vary;
    }

    public int getBytes() {
        return bytes;
    }

    public void setBytes(int bytes) {
        this.bytes = bytes;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getDecimalPositions() {
        return decimalPositions;
    }

    public void setDecimalPositions(int decimalPositions) {
        this.decimalPositions = decimalPositions;
    }

    public String asComparableText(int index) {

        StringBuilder buffer = new StringBuilder();

        if (index > 0) {
            buffer.append("&"); //$NON-NLS-1$
            buffer.append(index);
            buffer.append(": "); //$NON-NLS-1$
        }

        if (isVary()) {
            buffer.append(getType().trim());
            buffer.append("(");
            buffer.append(getBytes());
        } else {
            buffer.append(getType().trim());
            buffer.append("(");
            buffer.append(getLength());
        }

        if (FieldFormat.DEC.equals(getType())) {
            buffer.append(", ");
            buffer.append(getDecimalPositions());
        }

        buffer.append(")");

        if (isVary()) {
            buffer.append(", *VARY");
        }

        return buffer.toString();
    }

    @Override
    public String toString() {
        return asComparableText(-1);
    }
}
