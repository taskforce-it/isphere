/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api.retrieveproductinformation;

import java.io.CharConversionException;
import java.io.UnsupportedEncodingException;

import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

public class PRDI0100 extends APIFormat {

    private static final String PRODUCT_ID = "Product_ID"; //$NON-NLS-1$
    private static final String RELEASE_LEVEL = "Release_level"; //$NON-NLS-1$
    private static final String PRODUCT_OPTION = "Product_option"; //$NON-NLS-1$
    private static final String LOAD_ID = "Load_ID"; //$NON-NLS-1$

    /**
     * Constructs a PRDR0100 object.
     * 
     * @param system - System that calls the API
     * @param bytes - buffer that contains the retrieved message descriptions
     * @throws UnsupportedEncodingException
     * @throws CharConversionException
     */
    public PRDI0100(AS400 system) throws UnsupportedEncodingException, CharConversionException {
        super(system, "PRDI0100");

        createStructure();

        setCharValue(PRODUCT_ID, "*OPSYS");
        setCharValue(RELEASE_LEVEL, "*CUR");
        setCharValue(PRODUCT_OPTION, "0000");
        setCharValue(LOAD_ID, "*CODE");
    }

    /**
     * Creates the PRDI0100 structure.
     */
    private void createStructure() {

        addCharField(PRODUCT_ID, 0, 7);
        addCharField(RELEASE_LEVEL, 7, 6);
        addCharField(PRODUCT_OPTION, 13, 4);
        addCharField(LOAD_ID, 17, 10);
    }

}
