/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.api.listjoblog;

import java.io.UnsupportedEncodingException;

import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

public class OLJL0100_Field extends APIFormat {

    private static final String OFFSET_TO_NEXT_FIELD = "offsetToNextField"; //$NON-NLS-1$
    private static final String LENGTH_OF_FIELD_INFORMATION = "lengthOfFieldInformation"; //$NON-NLS-1$
    private static final String IDENTIFIER_FIELD = "identifierField"; //$NON-NLS-1$
    private static final String TYPE_OF_DATA = "typeOfData"; //$NON-NLS-1$
    private static final String STATUS_OF_DATA = "statusOfData"; //$NON-NLS-1$
    private static final String RESERVED_1 = "reserved_1"; //$NON-NLS-1$
    private static final String LENGTH_OF_DATA = "lengthOfData"; //$NON-NLS-1$
    private static final String MIXED_0 = "data"; //$NON-NLS-1$
    private static final String MIXED_1 = "mixed_1"; //$NON-NLS-1$

    private static final String TYPE_CHARACTER = "C"; //$NON-NLS-1$
    private static final String TYPE_BINARY = "B"; //$NON-NLS-1$
    private static final String TYPE_MIXED = "M"; //$NON-NLS-1$

    private int offsetData;

    public OLJL0100_Field(AS400 system) {
        super(system, "OLJL0100_Field"); //$NON-NLS-1$

        createStructure();
    }

    public int getOffsetToNextField() {
        return getInt4Value(OFFSET_TO_NEXT_FIELD);
    }

    public int getLengthOfFieldInformation() {
        return getInt4Value(LENGTH_OF_FIELD_INFORMATION);
    }

    public int getIdentifierField() {
        return getInt4Value(IDENTIFIER_FIELD);
    }

    public String getTypeOfData() throws UnsupportedEncodingException {
        return getCharValue(TYPE_OF_DATA);
    }

    public String getStatusOfData() throws UnsupportedEncodingException {
        return getCharValue(STATUS_OF_DATA);
    }

    public int getLengthOfData() {
        return getInt4Value(LENGTH_OF_DATA);
    }

    public String getCharData() throws UnsupportedEncodingException {

        String type = getCharValue(TYPE_OF_DATA);
        if (TYPE_CHARACTER.equals(type)) {
            return convertToText(getBytesAt(offsetData, getLengthOfData())).trim();
        } else if (TYPE_MIXED.equals(type)) {
            int numStmts = getInt4Value(MIXED_0);
            if (numStmts > 0) {
                String stmt = getCharValue(MIXED_1).trim();
                if (!"0000".equals(stmt)) { //$NON-NLS-1$
                    return stmt;
                }
            }
        }

        return ""; //$NON-NLS-1$
    }

    /**
     * Creates the PRDI0100 structure.
     */
    private void createStructure() {

        addInt4Field(OFFSET_TO_NEXT_FIELD, 0);
        addInt4Field(LENGTH_OF_FIELD_INFORMATION, 4);
        addInt4Field(IDENTIFIER_FIELD, 8);
        addCharField(TYPE_OF_DATA, 12, 1);
        addCharField(STATUS_OF_DATA, 13, 1);
        addCharField(RESERVED_1, 14, 14);
        addInt4Field(LENGTH_OF_DATA, 28);
        offsetData = 32;
        addInt4Field(MIXED_0, 32);
        addCharField(MIXED_1, 36, 10);
    }
}
