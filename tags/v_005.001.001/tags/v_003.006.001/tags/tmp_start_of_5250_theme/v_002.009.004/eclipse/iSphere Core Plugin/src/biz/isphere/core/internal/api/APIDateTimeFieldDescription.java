/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api;

/**
 * This class describes a date and time sub field of an API format.
 * 
 * @author Thomas Raddatz
 */
public class APIDateTimeFieldDescription extends AbstractAPIFieldDescription {

    public static final String DTS = "*DTS"; //$NON-NLS-1$

    private String format;

    /**
     * Constructs a APIDateTimeFieldDescription object.
     * 
     * @param name - field name
     * @param offset - offset to the field data
     * @param length - length of the field data
     */
    public APIDateTimeFieldDescription(String name, int offset, int length, String format) {
        super(name, offset, length);
        this.format = format;
    }

    /**
     * Returns the format of the date and time data.
     * 
     * @return format of the data
     */
    public String getFormat() {
        return format;
    }
}
