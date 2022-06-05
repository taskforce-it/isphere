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
import biz.isphere.core.messagefileeditor.SpecialReplyValueEntry;

import com.ibm.as400.access.AS400;

/**
 * Special reply value entry format of the QMHRTVM API.
 * 
 * @author Thomas Raddatz
 */
public class SpecialReplyValueEntryFormat extends APIFormat {

    private static final String FROM_VALUE = "fromValue";
    private static final String TO_VALUE = "toValue";

    /**
     * Constructs a SpecialReplyValueEntryFormat object.
     * 
     * @param system - System that calls the API
     * @param bytes - buffer that contains the retrieved message descriptions
     * @throws UnsupportedEncodingException
     */
    public SpecialReplyValueEntryFormat(AS400 system, byte[] bytes) throws UnsupportedEncodingException {
        super(system, "SpecialReplyValueEntryFormat");

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
     * Returns the from-value.
     * 
     * @return from-value
     * @throws UnsupportedEncodingException
     */
    public String getFromValue() throws UnsupportedEncodingException {
        return getCharValue(FROM_VALUE).trim();
    }

    /**
     * Returns the to-value.
     * 
     * @return to-value
     * @throws UnsupportedEncodingException
     */
    public String getToValue() throws UnsupportedEncodingException {
        return getCharValue(TO_VALUE).trim();
    }

    /**
     * Factory methods to create a special reply value entry.
     * 
     * @return special reply value entry
     * @throws UnsupportedEncodingException
     */
    public SpecialReplyValueEntry createSpecialReplyValueEntry() throws UnsupportedEncodingException {

        SpecialReplyValueEntry specialReplyValueEntry = new SpecialReplyValueEntry();
        specialReplyValueEntry.setFromValue(getFromValue());
        specialReplyValueEntry.setToValue(getToValue());

        return specialReplyValueEntry;
    }

    /**
     * Creates the SubstitutionVariable structure.
     */
    private void createStructure() {

        addCharField(FROM_VALUE, 0, 32);
        addCharField(TO_VALUE, 32, 32);
    }
}
