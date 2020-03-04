/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api.retrievemessagedescription;

import java.io.UnsupportedEncodingException;

import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

/**
 * Format of the substitution variables of the QMHRTVM API.
 * 
 * @author Thomas Raddatz
 */
public class SubstitutionVariable extends APIFormat {

    private static final String LENGTH_OF_REPLACEMENT_DATA = "lengthOfReplacementData";
    private static final String FIELD_SIZE_OR_DECIMAL_POSITIONS = "fieldSizeOrDecimalPositions";
    private static final String SUBSTITUTION_VARIABLE_TYPE = "substitutionVariableType";

    /**
     * Constructs a SubstitutionVariable object.
     * 
     * @param system - System that calls the API
     * @param bytes - buffer that contains the retrieved message descriptions
     * @throws UnsupportedEncodingException
     */
    public SubstitutionVariable(AS400 system, byte[] bytes) throws UnsupportedEncodingException {
        super(system, "SubstitutionVariable");

        createStructure();

        setBytes(bytes);
    }

    /**
     * Sets the offset to a particular substitution variable.
     */
    public void setOffset(int offset) {
        super.setOffset(offset);
    }

    /**
     * Returns the length of the replacement data for the substitution variable.
     * <p>
     * The number of characters or digits that are needed in the message
     * replacement data for this substitution variable. The value returned is
     * dependent on the substitution variable type and length:
     * <ul>
     * <li>-1 is returned if the length is *VARY.</li>
     * <li>The total number of decimal digits (including the fractional portion)
     * is returned if the substitution variable type is *DEC.</li>
     * <li>In all other cases, the value returned is the size in bytes of the
     * substitution variable.</li>
     * </ul>
     * 
     * @return length of the substitution variable
     */
    public int getLengthOfReplacementData() {
        return getInt4Value(LENGTH_OF_REPLACEMENT_DATA);
    }

    /**
     * This value is used in one of two ways, depending on the substitution
     * variable type.
     * <ul>
     * <li>If the substitution variable type is *QTDCHAR, *CHAR, *CCHAR, *HEX,
     * or *SPP and *VARY is specified as the length of replacement data for the
     * substitution variable, the size of the length portion of the replacement
     * data is returned. This value is 2 if the length portion is 2 bytes long,
     * or 4 if the length portion is 4 bytes long.</li>
     * <li>When the substitution variable type is *DEC, the number of decimal
     * positions in the substitution variable is returned.</li>
     * <li>In all other cases, 0 is returned.</li>
     * </ul>
     * 
     * @return number of decimal positions or size of the length portion of the
     *         replacement data
     */
    public int getDecimalPositions() {
        return getInt4Value(FIELD_SIZE_OR_DECIMAL_POSITIONS);
    }

    /**
     * Returns the data type of the substitution variable.
     * 
     * @return data type
     * @throws UnsupportedEncodingException
     */
    public String getType() throws UnsupportedEncodingException {
        return getCharValue(SUBSTITUTION_VARIABLE_TYPE);
    }

    /**
     * Creates the SubstitutionVariable structure.
     */
    private void createStructure() {

        addInt4Field(LENGTH_OF_REPLACEMENT_DATA, 0);
        addInt4Field(FIELD_SIZE_OR_DECIMAL_POSITIONS, 4);
        addCharField(SUBSTITUTION_VARIABLE_TYPE, 8, 10);
    }
}
