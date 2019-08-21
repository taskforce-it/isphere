/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api;

/**
 * This class describes the sub field of an API format.
 * 
 * @author Thomas Raddatz
 */
public abstract class AbstractAPIFieldDescription {

    private String name;
    private int offset;
    private int length;

    /**
     * Constructs a AbstractAPIFieldDescription object.
     * 
     * @param name - field name
     * @param offset - offset to the field data
     * @param length - length of the field data
     */
    protected AbstractAPIFieldDescription(String name, int offset, int length) {
        this.name = name;
        this.offset = offset;
        this.length = length;
    }

    /**
     * Returns the name of the field description.
     * 
     * @return field name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the offset to the data of the field.
     * 
     * @return offset to the field data
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Returns the length of the field data.
     * 
     * @return length of field data
     */
    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return name + "(" + offset + ": " + length + ")";
    }
}
