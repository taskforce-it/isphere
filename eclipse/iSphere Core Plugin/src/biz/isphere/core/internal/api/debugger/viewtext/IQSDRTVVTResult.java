/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api.debugger.viewtext;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.internal.api.APIFormat;
import biz.isphere.core.internal.api.debugger.moduleviews.IQSDRTVMV;

import com.ibm.as400.access.AS400;

/**
 * Class to hold the result of the IQSDRTVMV API.
 * 
 * @author Thomas Raddatz
 */
public class IQSDRTVVTResult extends APIFormat {

    private static final String BYTES_RETURNED = "bytRtn"; //$NON-NLS-1$
    private static final String BYTES_AVAILABLE = "bytAvl"; //$NON-NLS-1$
    private static final String NUMBER_OF_LINES_RETURNED = "numLinesRtn"; //$NON-NLS-1$
    private static final String NUMBER_OF_LINES_AVAILABLE = "numLinesAvl"; //$NON-NLS-1$
    private static final String LENGTH_OF_LINE = "lineLength"; //$NON-NLS-1$
    private static final String FIRST_LINE = "firstLine"; //$NON-NLS-1$
    private static final String LAST_LINE = "lastLine"; //$NON-NLS-1$
    private static final String LENGTH_OF_LINE_ENTRY = "lenLineE"; //$NON-NLS-1$
    private static final String OFFSET_FIRST_LINE = "offsFirstLine"; //$NON-NLS-1$

    private String format;

    /**
     * Constructs a IQSDRTVVTResult object.
     * 
     * @param system - System that executed the retrieve request
     * @param bytes - returned by the IQSDRTVMV API
     * @param format - format of the returned data ({@link IQSDRTVMV#VEWL0100})
     */
    public IQSDRTVVTResult(AS400 system, byte[] bytes, String format) {
        super(system, "IQSDRTVMVHeader");

        this.format = format;

        createStructure();

        setBytes(bytes);
    }

    public String getFormat() {
        return format;
    }

    /**
     * Returns the number of bytes returned by the IQSDRTVMV API.
     * 
     * @return number of bytes returned
     */
    public int getBytesReturned() {
        return getInt4Value(BYTES_RETURNED);
    }

    /**
     * Returns the number of bytes available.
     * 
     * @return bytes available
     */
    public int getBytesAvailable() {
        return getInt4Value(BYTES_AVAILABLE);
    }

    /**
     * Returns the number of lines returned by the IQSDRTVVT API.
     * 
     * @return number of lines returned
     */
    public int getNumberOfLinesReturned() {
        return getInt4Value(NUMBER_OF_LINES_RETURNED);
    }

    /**
     * Returns the number of lines available by the IQSDRTVVT API.
     * 
     * @return number of lines available
     */
    public int getNumberOfLinesAvailable() {
        return getInt4Value(NUMBER_OF_LINES_AVAILABLE);
    }

    /**
     * Returns the length of a line.
     * 
     * @return line length
     */
    public int getLineLength() {
        return getInt4Value(LENGTH_OF_LINE);
    }

    /**
     * Returns the index of the first line.
     * 
     * @return index first line
     */
    public int getFirstLine() {
        return getInt4Value(FIRST_LINE);
    }

    /**
     * Returns the index of the last line.
     * 
     * @return index last line
     */
    public int getLastLine() {
        return getInt4Value(LAST_LINE);
    }

    /**
     * Returns the length of a line entry.
     * 
     * @return length of line entry
     */
    public int getLengthOfLineEntry() {
        return getInt4Value(LENGTH_OF_LINE_ENTRY);
    }

    /**
     * Returns the lines retrieved by the IQSDRTVVT API.
     * 
     * @return list of lines
     * @throws UnsupportedEncodingException
     */
    public List<String> getLines() throws UnsupportedEncodingException {

        List<String> lines = new LinkedList<String>();

        SDVT0100 sdvt0100 = null;
        if (IQSDRTVVT.SDVT0100.equals(format)) {
            sdvt0100 = new SDVT0100(getSystem(), getBytes());
        } else {
            throw new IllegalArgumentException("Invalid format: " + format); //$NON-NLS-1$
        }

        int offset = getOffsetFirstLine();

        for (int i = 0; i < getNumberOfLinesReturned(); i++) {

            sdvt0100.setOffset(offset);
            lines.add(StringHelper.trimR(sdvt0100.getLine()));

            offset = sdvt0100.getOffsetNextLine();
        }

        return lines;
    }

    /**
     * Returns the offset to the first line.
     * 
     * @return offset of first line
     */
    public int getOffsetFirstLine() {
        return getInt4Value(OFFSET_FIRST_LINE);
    }

    /**
     * Creates the sub fields of the API format.
     */
    private void createStructure() {

        addInt4Field(BYTES_RETURNED, 0);
        addInt4Field(BYTES_AVAILABLE, 4);
        addInt4Field(NUMBER_OF_LINES_RETURNED, 8);
        addInt4Field(NUMBER_OF_LINES_AVAILABLE, 12);
        addInt4Field(LENGTH_OF_LINE, 16);
        addInt4Field(FIRST_LINE, 20);
        addInt4Field(LAST_LINE, 24);
        addInt4Field(LENGTH_OF_LINE_ENTRY, 28);
        addInt4Field(OFFSET_FIRST_LINE, 32);
    }
}
