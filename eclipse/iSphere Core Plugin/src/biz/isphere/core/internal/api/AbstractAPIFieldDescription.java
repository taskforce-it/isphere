/*******************************************************************************
 * Copyright (c) 2012-2023 iSphere Project Owners
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

    /*
     * Attributes used, the offset of this field is based-on the value of
     * another field.
     */
    private APIFormat parent;
    private String offsetFieldName;

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
     * Sets the offset of the data of the field.
     * <p>
     * Must only used by subclasses of {@link APIFormat}.
     * 
     * @param offset - offset this data starts in the byte buffer
     */
    protected void setOffset(int offset) {
        if (APIFormat.class.isAssignableFrom(getClass())) {
            this.offset = offset;
            return;
        }
        throw new IllegalAccessError("Method setOffset() must only be used by objects of class APIFormat."); //$NON-NLS-1$
    }

    /**
     * Returns the offset to the data of the field.
     * 
     * @return offset to the field data
     */
    public int getOffset() {
        if (parent != null) {
            offset = parent.getIntValue(offsetFieldName);
        }
        return offset;
    }

    /**
     * Sets the offset of the data of the field.
     * <p>
     * Must only used by subclasses of {@link APIFormat}.
     * 
     * @param length - offset this data starts in the byte buffer
     */
    protected void setLength(int length) {
        if (APIFormat.class.isAssignableFrom(getClass())) {
            this.length = length;
            return;
        }
        throw new IllegalAccessError("Method setLength() must only be used by objects of class APIFormat."); //$NON-NLS-1$
    }

    /**
     * Returns the length of the field data.
     * 
     * @return length of field data
     */
    public int getLength() {
        return length;
    }

    /**
     * Sets the parent field/format and field name that stored the offset of
     * this field/format.
     * 
     * @param parent
     * @param fieldName
     */
    public void setParent(APIFormat parent, String fieldName) {
        this.parent = parent;
        this.offsetFieldName = fieldName;
    }

    /**
     * Returns <code>true</code> when the offset of this field is based-on the
     * value of another field.
     * 
     * @return Returns <code>true</code> when the offset of this field is stored
     *         in another field, else <code>false</code>.
     */
    public boolean isBasedOnField() {
        if (parent == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return name + "(" + offset + ": " + length + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}
