/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.api.retrievenetworkattributes;

import java.io.UnsupportedEncodingException;

import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

public class QWCRNETA_Attribute extends APIFormat {

    private static final String NETWORK_ATTRIBUTE = "networkAttribute"; //$NON-NLS-1$
    private static final String TYPE_OF_DATA = "typeOfData"; //$NON-NLS-1$
    private static final String INFORMATION_STATUS = "informationStatus"; //$NON-NLS-1$
    private static final String LENGTH_OF_DATA = "lengthOfData"; //$NON-NLS-1$

    private static final String TYPE_CHARACTER = "C"; //$NON-NLS-1$
    private static final String TYPE_BINARY = "B"; //$NON-NLS-1$

    private int offsetData;

    public QWCRNETA_Attribute(AS400 system) {
        super(system, "QWCRNETA_Attribute"); //$NON-NLS-1$

        createStructure();
    }

    public String getCharData() throws UnsupportedEncodingException {

        String type = getCharValue(TYPE_OF_DATA);
        if (TYPE_CHARACTER.equals(type)) {
            return convertToText(getBytesAt(offsetData, getLengthOfData())).trim();
        }

        return ""; //$NON-NLS-1$
    }

    public String getKey() throws UnsupportedEncodingException {
        return getCharValue(NETWORK_ATTRIBUTE);
    }

    public int getLengthOfData() {
        return getInt4Value(LENGTH_OF_DATA);
    }

    /**
     * Creates the structure.
     */
    private void createStructure() {

        addCharField(NETWORK_ATTRIBUTE, 0, 10);
        addCharField(TYPE_OF_DATA, 10, 1);
        addCharField(INFORMATION_STATUS, 11, 1);
        addInt4Field(LENGTH_OF_DATA, 12);

        offsetData = getLength();
    }

}
