/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/
package biz.isphere.core.spooledfiles;

import biz.isphere.base.internal.StringHelper;

public class SpooledFileSubSystemAttributes {

    public final static String VENDOR_ID = "biz.isphere"; //$NON-NLS-1$

    private final static String DECORATION_STYLE = "biz.isphere.spooledfiles.internal.decoration.text"; //$NON-NLS-1$

    private static SpooledFileTextDecoration DECORATION_STYLE_DEFAULT = SpooledFileTextDecoration.getDefaultDecorationStyle();

    private ISpooledFileSubSystem spooledFileSubSystem;

    public SpooledFileSubSystemAttributes(ISpooledFileSubSystem spooledFileSubSystem) {

        this.spooledFileSubSystem = spooledFileSubSystem;
    }

    public void restoreToDefault() {

        setDecorationTextStyle(DECORATION_STYLE_DEFAULT);
    }

    public SpooledFileTextDecoration getDecorationTextStyle() {

        String decorationStyleKey = getVendorAttribute(DECORATION_STYLE);
        if (StringHelper.isNullOrEmpty(decorationStyleKey)) {
            return DECORATION_STYLE_DEFAULT;
        } else {
            if (SpooledFileTextDecoration.isValidKey(decorationStyleKey)) {
                return SpooledFileTextDecoration.getDecorationStyleByKey(decorationStyleKey);
            } else {
                return DECORATION_STYLE_DEFAULT;
            }
        }
    }

    public void setDecorationTextStyle(SpooledFileTextDecoration decorationStyle) {

        setVendorAttribute(DECORATION_STYLE, decorationStyle.getKey());
    }

    private String getVendorAttribute(String key) {
        return spooledFileSubSystem.getVendorAttribute(key);
    }

    private void setVendorAttribute(String key, String value) {
        spooledFileSubSystem.setVendorAttribute(key, value);
    }
}
