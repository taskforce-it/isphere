/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api.retrievememberdescription;

import java.io.UnsupportedEncodingException;

import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

public class MBRD0100 extends APIFormat {

    private static final String BYTES_RETURNED = "bytesReturned"; //$NON-NLS-1$
    private static final String BYTES_AVAILABLE = "bytesAvailable"; //$NON-NLS-1$
    private static final String FILE_NAME = "fileName"; //$NON-NLS-1$
    private static final String LIBRARY_NAME = "libraryName"; //$NON-NLS-1$
    private static final String MEMBER_NAME = "memberName"; //$NON-NLS-1$
    private static final String FILE_ATTRIBUTE = "fileAttribute"; //$NON-NLS-1$
    private static final String SOURCE_TYPE = "sourceType"; //$NON-NLS-1$
    private static final String CREATION_DATE_AND_TIME = "creationDateAndTime"; //$NON-NLS-1$
    private static final String LAST_CHANGE_DATE_AND_TIME = "lastChangeDateAndTIme"; //$NON-NLS-1$
    private static final String MEMBER_DESCRIPTION = "memberDescription"; //$NON-NLS-1$
    private static final String SOURCE_FILE = "sourceFile"; //$NON-NLS-1$

    /**
     * Constructs a RTVM0300 object.
     * 
     * @param system - System that calls the API
     * @param bytes - buffer that contains the retrieved message descriptions
     * @throws UnsupportedEncodingException
     */
    public MBRD0100(AS400 system) throws UnsupportedEncodingException {
        super(system, "MBRD0100");

        createStructure();
    }

    public String getMemberName() throws UnsupportedEncodingException {
        return getCharValue(MEMBER_NAME);
    }

    public String getMemberDescription() throws UnsupportedEncodingException {
        return getCharValue(MEMBER_DESCRIPTION);
    }

    /**
     * Creates the MBRD0100 structure.
     */
    protected void createStructure() {

        addInt4Field(BYTES_RETURNED, 0);
        addInt4Field(BYTES_AVAILABLE, 4);
        addCharField(FILE_NAME, 8, 10);
        addCharField(LIBRARY_NAME, 18, 10);
        addCharField(MEMBER_NAME, 28, 10);
        addCharField(FILE_ATTRIBUTE, 38, 10);
        addCharField(SOURCE_TYPE, 48, 10);
        addCharField(CREATION_DATE_AND_TIME, 58, 13);
        addCharField(LAST_CHANGE_DATE_AND_TIME, 71, 13);
        addCharField(MEMBER_DESCRIPTION, 84, 50);
        addCharField(SOURCE_FILE, 134, 1);
    }

}
