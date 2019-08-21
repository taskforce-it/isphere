/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api.retrieveproductinformation;

import java.io.UnsupportedEncodingException;

import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

/**
 * Format PRD00000 of the Retrieve Product Information (QSZRTVPR) API.
 * 
 * @author Thomas Raddatz
 */
public abstract class PRDR0000 extends APIFormat {

    protected PRDR0000(AS400 system, String formatName) throws UnsupportedEncodingException {
        super(system, formatName);
        
        createStructure();
    }

    private static final String BYTES_RETURNED = "BytesReturned"; //$NON-NLS-1$
    private static final String BYTES_AVAILABLE = "BytesAvailable"; //$NON-NLS-1$

    /**
     * Returns the number of bytes returned for the message description.
     * 
     * @return number of bytes returned
     */
    public int getBytesReturned() {
        return getInt4Value(BYTES_RETURNED);
    }

    /**
     * Returns the number of bytes available for the message description.
     * 
     * @return number of bytes available
     */
    public int getBytesAvailable() {
        return getInt4Value(BYTES_AVAILABLE);
    }

    /**
     * Creates the PRDR0100 structure.
     */
    protected void createStructure() {

        addInt4Field(BYTES_RETURNED, 0);
        addInt4Field(BYTES_AVAILABLE, 4);
    }
}
