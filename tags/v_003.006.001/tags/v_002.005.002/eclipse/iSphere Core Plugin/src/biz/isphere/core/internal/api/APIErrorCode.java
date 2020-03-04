/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api;

import java.io.UnsupportedEncodingException;

import com.ibm.as400.access.AS400;

/**
 * Class to define the error code parameter when calling an IBM i API.
 * 
 * @author Thomas Raddatz
 */
public class APIErrorCode extends APIFormat {

    private static final String BYTES_PROVIDED = "bytesProvides";
    private static final String BYTES_AVAILABLE = "bytesAvailable";
    private static final String EXCEPTION_ID = "exceptionID";
    private static final String RESERVED_1 = "reserved_1";
    private static final String EXCEPTION_DATA = "exceptionData";

    /**
     * Constructs an APIErrorCode object, that does not return error
     * information. The API sends an Escape message instead.
     * 
     * @throws UnsupportedEncodingException
     */
    public APIErrorCode() throws UnsupportedEncodingException {
        this(null, false);
    }

    /**
     * Constructs an APIErrorCode object, that returns error information.
     * 
     * @param system - system used to create the data converter
     * @throws UnsupportedEncodingException
     */
    public APIErrorCode(AS400 system) throws UnsupportedEncodingException {
        this(system, true);
    }

    /**
     * Constructs an APIErrorCode object, that optionally returns error
     * information.
     * 
     * @param system - system used to create the data converter
     * @throws UnsupportedEncodingException
     */
    public APIErrorCode(AS400 system, boolean returnErrorInformation) throws UnsupportedEncodingException {
        super(system, "ERROR_CODE");

        createStructure(returnErrorInformation);

        if (returnErrorInformation) {
            setInt4Value(BYTES_PROVIDED, getLength());
        } else {
            setInt4Value(BYTES_PROVIDED, 0);
        }

        setInt4Value(BYTES_AVAILABLE, 0);
    }

    /**
     * Creates the sub fields of the API format.
     */
    private void createStructure(boolean returnErrorInformation) {

        addInt4Field(BYTES_PROVIDED, 0);
        addInt4Field(BYTES_AVAILABLE, 4);

        if (returnErrorInformation) {
            addInt4Field(EXCEPTION_ID, 8);
            addCharField(RESERVED_1, 15, 1);
            addCharField(EXCEPTION_DATA, 16, 512);
        }
    }
}
