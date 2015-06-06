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
import biz.isphere.core.messagefileeditor.ValidReplyEntry;

import com.ibm.as400.access.AS400;

/**
 * Valid reply entry format of the QMHRTVM API.
 * 
 * @author Thomas Raddatz
 */
public class ValidReplyEntryFormat extends APIFormat {

    private static final String VALID_REPLY_ENTRY = "validReplyEntry";

    /**
     * Constructs a ValidReplyEntryFormat object.
     * 
     * @param system - System that calls the API
     * @param bytes - buffer that contains the retrieved message descriptions
     * @throws UnsupportedEncodingException
     */
    public ValidReplyEntryFormat(AS400 system, byte[] bytes) throws UnsupportedEncodingException {
        super(system, "ValidReplyEntryFormat");

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
     * Returns the valid reply entry value.
     * 
     * @return valid reply entry value
     * @throws UnsupportedEncodingException 
     */
    public String getReplyValue() throws UnsupportedEncodingException {
        return getCharValue(VALID_REPLY_ENTRY).trim();
    }

    /**
     * Factory method to create a valid reply entry.
     *  
     * @return valid reply entry
     * @throws UnsupportedEncodingException
     */
    public ValidReplyEntry createValidReplyEntry() throws UnsupportedEncodingException {
        
        ValidReplyEntry validReplyEntry = new ValidReplyEntry();
        validReplyEntry.setValue(getReplyValue());
        
        return validReplyEntry;
    }

    /**
     * Creates the SubstitutionVariable structure.
     */
    private void createStructure() {

        addCharField(VALID_REPLY_ENTRY, 0, 32);
    }
}
