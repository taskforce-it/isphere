/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

import java.math.BigDecimal;

public final class BigDecimalHelper {

    public static String getFixLength(byte[] bytes, int length, int fraction) {
        String value = new String(bytes);
        String strDigits = value.substring(0, length - fraction);
        String strFraction = value.substring(length - fraction);
        return strDigits + "." + strFraction; //$NON-NLS-1$ 
    }

    public static String getFixLength(BigDecimal decimal, int length, int fraction) {

        String[] parts = decimal.toString().split("\\."); //$NON-NLS-1$
        String strDigits = ""; //$NON-NLS-1$
        String strFraction = ""; //$NON-NLS-1$
        if (parts.length > 0) {
            if (parts.length > 1) {
                strFraction = parts[1];
            }
            strDigits = parts[0];
        }

        strDigits = StringHelper.getFixLengthLeading(strDigits, length - fraction, "0"); //$NON-NLS-1$
        strFraction = StringHelper.getFixLength(strFraction, fraction, "0"); //$NON-NLS-1$

        String decimalValue = strDigits + "." + strFraction; //$NON-NLS-1$

        return decimalValue;
    }
}
