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
import biz.isphere.core.messagefileeditor.RelationalTestEntry;

import com.ibm.as400.access.AS400;

/**
 * Special reply value entry format of the QMHRTVM API.
 * 
 * @author Thomas Raddatz
 */
public class RelationalTestEntryFormat extends APIFormat {

    private static final String RELATIONAL_OPERATOR = "relationalOperator";
    private static final String RESERVED_1 = "reserved_1";
    private static final String LENGTH_OF_RELATIONAL_VALUE = "lengthOfRelationalValue";
    private static final String RELATIONAL_VALUE = "relationalValue";

    /**
     * Constructs a RelationalTestEntryFormat object.
     * 
     * @param system - System that calls the API
     * @param bytes - buffer that contains the retrieved message descriptions
     * @throws UnsupportedEncodingException
     */
    public RelationalTestEntryFormat(AS400 system, byte[] bytes) throws UnsupportedEncodingException {
        super(system, "RelationalTestEntryFormat");

        createStructure();

        setBytes(bytes);
    }

    /**
     * Sets the offset to a particular reply entry.
     */
    public void setOffset(int offset) {
        super.setOffset(offset);
    }

    /**
     * Returns the operator.
     * 
     * @return operator
     * @throws UnsupportedEncodingException
     */
    public String getOperator() throws UnsupportedEncodingException {
        return getCharValue(RELATIONAL_OPERATOR).trim();
    }

    /**
     * Returns the value.
     * 
     * @return value
     * @throws UnsupportedEncodingException
     */
    public String getValue() throws UnsupportedEncodingException {
        return getCharValue(RELATIONAL_VALUE).substring(0, getLengthOfRelationalValue());
    }

    private int getLengthOfRelationalValue() {
        return getInt4Value(LENGTH_OF_RELATIONAL_VALUE);
    }
    
    /**
     * Factory methods to create a relational test entry.
     * 
     * @return relational test entry
     * @throws UnsupportedEncodingException
     */
    public RelationalTestEntry createRelationalTestEntry() throws UnsupportedEncodingException {

        RelationalTestEntry relationalTestEntry = new RelationalTestEntry();
        relationalTestEntry.setOperator(getOperator());
        relationalTestEntry.setValue(getValue());

        return relationalTestEntry;
    }

    /**
     * Creates the SubstitutionVariable structure.
     */
    private void createStructure() {

        addCharField(RELATIONAL_OPERATOR, 0, 10);
        addCharField(RESERVED_1, 10, 2);
        addInt4Field(LENGTH_OF_RELATIONAL_VALUE, 12);
        addCharField(RELATIONAL_VALUE, 16, 32);
    }
}
