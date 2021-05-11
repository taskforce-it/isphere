/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api.retrieveproductinformation;

import java.io.UnsupportedEncodingException;

import com.ibm.as400.access.AS400;

/**
 * Format RTVM0300 of the Retrieve Message (QMHRTVM) API.
 * 
 * @author Thomas Raddatz
 */
public class PRDR0100 extends PRDR0000 {

    private static final String RESERVED_1 = "Reserved_1"; //$NON-NLS-1$
    private static final String PRODUCT_ID = "Product_ID"; //$NON-NLS-1$
    private static final String RELEASE_LEVEL = "Release_level"; //$NON-NLS-1$
    private static final String PRODUCT_OPTION = "Product_option"; //$NON-NLS-1$
    private static final String LOAD_ID = "Load_ID"; //$NON-NLS-1$
    private static final String LOAD_TYPE = "Load_type"; //$NON-NLS-1$
    private static final String SYMBOLIC_LOAD_STATE = "Symbolic_load_state"; //$NON-NLS-1$
    private static final String LOAD_ERROR_INDICATOR = "Load_error_indicator"; //$NON-NLS-1$
    private static final String LOAD_STATE = "Load_state"; //$NON-NLS-1$
    private static final String SUPPORTED_FLAG = "Supported_flag"; //$NON-NLS-1$
    private static final String REGISTRATION_TYPE = "Registration_type"; //$NON-NLS-1$
    private static final String REGISTRATION_VALUE = "Registration_value"; //$NON-NLS-1$
    private static final String RESERVED_2 = "Reserved_2"; //$NON-NLS-1$
    private static final String OFFSET_TO_ADDITINAL_INFORMATION = "Offset_to_additional_information"; //$NON-NLS-1$
    private static final String PRIMARY_LANGUAGE_LOAD_IDENTIFIER = "Primary_language_load_identifier"; //$NON-NLS-1$
    private static final String MINIMUM_TARGET_RELEASE = "Minimum_target_release"; //$NON-NLS-1$
    private static final String MINIMUM_VRM_OTION = "Minimum_VRM_of_BASE_required_by_option"; //$NON-NLS-1$
    private static final String REQUIREMENTS_MET_OPTION = "Requirements_met_between_base_and_option_value"; //$NON-NLS-1$
    private static final String LEVEL = "Level"; //$NON-NLS-1$

    /**
     * Constructs a PRDR0100 object.
     * 
     * @param system - System that calls the API
     * @param bytes - buffer that contains the retrieved message descriptions
     * @throws UnsupportedEncodingException
     */
    public PRDR0100(AS400 system) throws UnsupportedEncodingException {
        super(system, "PRDR0100");
    }

    /**
     * Returns the version, release, and modification level of the product.
     * 
     * @return release level
     * @throws UnsupportedEncodingException
     */
    public String getReleaseLevel() throws UnsupportedEncodingException {
        return getCharValue(RELEASE_LEVEL);
    }

    /**
     * Creates the PRDR0100 structure.
     */
    protected void createStructure() {

        super.createStructure();

        addInt4Field(RESERVED_1, 8);
        addCharField(PRODUCT_ID, 12, 7);
        addCharField(RELEASE_LEVEL, 19, 6);
        addCharField(PRODUCT_OPTION, 25, 4);
        addCharField(LOAD_ID, 29, 4);
        addCharField(LOAD_TYPE, 33, 10);
        addCharField(SYMBOLIC_LOAD_STATE, 43, 10);
        addCharField(LOAD_ERROR_INDICATOR, 53, 10);
        addCharField(LOAD_STATE, 63, 2);
        addCharField(SUPPORTED_FLAG, 65, 1);
        addCharField(REGISTRATION_TYPE, 66, 2);
        addCharField(REGISTRATION_VALUE, 68, 14);
        addCharField(RESERVED_2, 82, 2);
        addInt4Field(OFFSET_TO_ADDITINAL_INFORMATION, 84);
        addCharField(PRIMARY_LANGUAGE_LOAD_IDENTIFIER, 88, 4);
        addCharField(MINIMUM_TARGET_RELEASE, 92, 6);
        addCharField(MINIMUM_VRM_OTION, 98, 6);
        addCharField(REQUIREMENTS_MET_OPTION, 104, 1);
        addCharField(LEVEL, 105, 3);
    }
}
