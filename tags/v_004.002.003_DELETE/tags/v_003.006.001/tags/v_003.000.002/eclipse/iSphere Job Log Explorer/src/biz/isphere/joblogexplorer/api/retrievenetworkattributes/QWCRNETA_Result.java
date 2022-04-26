/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.api.retrievenetworkattributes;

import java.util.HashSet;
import java.util.Set;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

public class QWCRNETA_Result extends APIFormat {

    private static final String NUM_ATTRS_RETURNED = "numberOfAttributesReturned"; //$NON-NLS-1$
    private static final String OFFSET_ATTRS_TABLE = "offsetAttributesTable"; //$NON-NLS-1$

    private Set<String> keys;

    public QWCRNETA_Result(AS400 system) {
        super(system, "QWCRNETA_Result"); //$NON-NLS-1$

        keys = new HashSet<String>();

        createStructure();
    }

    public void addKey(String key) {
        keys.add(key);
    }

    public String[] getKeys() {
        return keys.toArray(new String[keys.size()]);
    }

    public QWCRNETA_Attribute getAttribute(String key) {

        try {

            QWCRNETA_Attribute attribute = new QWCRNETA_Attribute(getSystem());
            attribute.setBytes(getBytes());

            int numAttrs = getInt4Value(NUM_ATTRS_RETURNED);
            int[] offsAttrs = getInt4Array(OFFSET_ATTRS_TABLE, numAttrs);
            for (int i = 0; i < offsAttrs.length; i++) {
                attribute.setOffset(offsAttrs[i]);
                if (key.equals(attribute.getKey().trim())) {
                    return attribute;
                }
            }

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Failed to retrieve network attribute ***", e); //$NON-NLS-1$
        }

        return null;
    }

    /**
     * Creates the structure.
     */
    private void createStructure() {

        addInt4Field(NUM_ATTRS_RETURNED, 0);
        addInt4Field(OFFSET_ATTRS_TABLE, 4);
    }

}
