/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api.debugger.viewtext;

import java.io.UnsupportedEncodingException;

import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

public class SDVT0100 extends APIFormat {

    private static final String OFFSET_NEXT_LINE = "offsetNextLine"; //$NON-NLS-1$
    private static final String LINE_LENGTH = "lineLength"; //$NON-NLS-1$
    private static final String OFFSET_LINE = "offsLine"; //$NON-NLS-1$

    /**
     * Constructs a SDMV0100 object.
     * 
     * @param system - System that calls the API
     * @param bytes - buffer that contains the retrieved debug views
     * @throws UnsupportedEncodingException
     */
    public SDVT0100(AS400 system, byte[] bytes) {
        super(system, "SDMV0100");

        createStructure();

        setBytes(bytes);
    }

    /**
     * Returns the offset of the next line.
     * 
     * @return offset next line
     */
    public int getOffsetNextLine() {
        return getInt4Value(OFFSET_NEXT_LINE);
    }

    /**
     * Returns the length of the text.
     * 
     * @return text length
     */
    public int getLineLength() {
        return getInt4Value(LINE_LENGTH);
    }

    /**
     * Returns the offset of the text.
     * 
     * @return offset line text
     */
    public int getOffsetLine() {
        return getInt4Value(OFFSET_LINE);
    }

    /**
     * Returns the text line.
     * 
     * @throws UnsupportedEncodingException
     */
    public String getLine() throws UnsupportedEncodingException {

        String viewText = convertToText(getBytesAt(getLength(), getLineLength()));

        return viewText;
    }

    /**
     * Creates the SDMV0100 structure.
     */
    protected void createStructure() {

        addInt4Field(OFFSET_NEXT_LINE, 0);
        addInt4Field(LINE_LENGTH, 4);
        addInt4Field(OFFSET_LINE, 8);
    }

}
