/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Buffer {

    /**
     * Converts a size expression, such as "8kB" to an integer value.
     * 
     * @param expression - Size expression of a number and a size:
     *        <p>
     *        The possible size expressions are:
     *        <ul>
     *        <li>byte: 'b' or 'byte'</li>
     *        <li>kilobyte: 'k', 'kb' or 'kByte'</li>
     *        <li>megabyte: 'mb'</li>
     *        </ul>
     * @return integer value
     */
    public static int size(String sizeExpression) {

        int value = -1;

        Pattern pattern = Pattern.compile("([0-9]+)\\s*([a-z]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sizeExpression);
        if (matcher.find()) {
            value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);
            if ("b".equalsIgnoreCase(unit) || "byte".equalsIgnoreCase(unit)) { //$NON-NLS-1$ //$NON-NLS-2$ 
                value = value * 1;
            } else if ("k".equalsIgnoreCase(unit) || "kb".equalsIgnoreCase(unit) || "kbyte".equalsIgnoreCase(unit)) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                value = value * 1024;
            } else if ("mb".equalsIgnoreCase(unit)) { //$NON-NLS-1$
                value = value * 1024 * 1024;
            } else {
                throw new RuntimeException("Illegal size unit: " + unit);
            }
        }

        return value;
    }

}
