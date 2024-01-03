/*******************************************************************************
 * Copyright (c) 2012-2023 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class describes a bit sub field of an API format.
 * 
 * @author Thomas Raddatz
 */
public class APIBitFieldDescription extends AbstractAPIFieldDescription {

    private Map<String, SubField> subFields;

    /**
     * Constructs a APIInt2FieldDescription object.
     * 
     * @param name - field name
     * @param offset - offset to the field data
     * @param numBits - length of the field data in bits
     */
    public APIBitFieldDescription(String name, int offset, int numBits) {
        super(name, offset, numBits / 8);

        subFields = new LinkedHashMap<String, SubField>();
    }

    public APIBitFieldDescription addSubField(String name, int offset, int bit) {
        subFields.put(name, new SubField(name, offset, bit));
        return this;
    }

    public boolean getBit(String bitName, byte[] bytes) {

        SubField subField = subFields.get(bitName);

        int offset = subField.getOffset() - getOffset();
        int mask = 7 - subField.getBit(); // Reverse order of bits
        mask = (int)Math.pow(2, mask); // Calculate mask

        int byteValue = bytes[offset] < 0 ? 256 + bytes[offset] : bytes[offset];
        int bit = byteValue & mask;

        if (bit == mask) {
            return true;
        } else {
            return false;
        }
    }

    private class SubField {

        private String name;
        private int offset;
        private int bit;

        private SubField(String name, int offset, int bit) {
            this.name = name;
            this.offset = offset;
            this.bit = bit;
        }

        public int getOffset() {
            return offset;
        }

        public int getBit() {
            return bit;
        }

        @Override
        public String toString() {
            return String.format("%s(%d:%d)", name, offset, bit);
        }
    }
}
