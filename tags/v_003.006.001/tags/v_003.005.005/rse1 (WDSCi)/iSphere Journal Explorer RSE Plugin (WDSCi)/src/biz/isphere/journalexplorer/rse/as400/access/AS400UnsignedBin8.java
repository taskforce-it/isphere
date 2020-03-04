/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.rse.as400.access;

import java.math.BigInteger;
import java.util.Arrays;

import biz.isphere.base.internal.ByteHelper;

import com.ibm.as400.access.AS400DataType;
import com.ibm.as400.access.ExtendedIllegalArgumentException;
import com.ibm.as400.access.InternalErrorException;
import com.ibm.as400.access.Trace;

public class AS400UnsignedBin8 implements AS400DataType {

    private static final long serialVersionUID = 4L;

    private static final int NUM_BYTES = 8;
    public static final BigInteger MIN_VALUE = BigInteger.ZERO;
    public static final BigInteger MAX_VALUE = toBigInteger(ByteHelper.getByteArray("FFFFFFFFFFFFFFFF"), 0);

    public AS400UnsignedBin8() {
        super();
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException localCloneNotSupportedException) {
            Trace.log(2, "Unexpected cloning error", localCloneNotSupportedException);
            throw new InternalErrorException(InternalErrorException.UNKNOWN);
        }
    }

    public int getByteLength() {
        return NUM_BYTES;
    }

    public Object getDefaultValue() {
        return BigInteger.ZERO;
    }

    public int getInstanceType() {
        // RDI data type:
        // return AS400DataType.TYPE_UBIN8;
        return 16;
    }

    /*
     * Abstract method of com.ibm.as400.access.AS400DataType of RDi.
     */
    public Class getJavaType() {
        return BigInteger.class;
    }

    public byte[] toBytes(Object javaValue) {
        byte[] as400Value = new byte[NUM_BYTES]; // initialized to zeros
        toBytes(javaValue, as400Value);
        return as400Value;
    }

    public int toBytes(Object javaValue, byte[] as400Value) {
        return toBytes(javaValue, as400Value, 0);
    }

    public int toBytes(Object javaValue, byte[] as400Value, int offset) {

        BigInteger bigIntValue = (BigInteger)javaValue;
        if (bigIntValue.compareTo(MIN_VALUE) < 0 || bigIntValue.compareTo(MAX_VALUE) > 0) {
            throw new ExtendedIllegalArgumentException("Value is out of range: " + javaValue.toString(),
                ExtendedIllegalArgumentException.RANGE_NOT_VALID);
        }

        // Initialize byte array with zero.
        Arrays.fill(as400Value, offset, offset + NUM_BYTES, (byte)0);

        byte[] val = bigIntValue.toByteArray();

        // Truncate the original array if number of bytes excceds 8.
        if (val.length >= NUM_BYTES + 1) {
            byte[] truncated = new byte[NUM_BYTES];
            int startPos = val.length - NUM_BYTES;
            System.arraycopy(val, startPos, truncated, 0, NUM_BYTES);
            val = truncated;
        }

        // Copy array right-adjusted into return value
        int startPos = (offset + NUM_BYTES) - val.length;
        System.arraycopy(val, 0, as400Value, startPos, val.length);

        // Initialize leading bytes
        if (val.length < NUM_BYTES) {
            int length = NUM_BYTES - val.length;
            Arrays.fill(as400Value, offset, offset + length, (byte)0);
        }

        return NUM_BYTES;
    }

    public Object toObject(byte[] as400Value) {
        return toBigInteger(as400Value, 0);
    }

    public Object toObject(byte[] as400Value, int offset) {
        return toBigInteger(as400Value, offset);
    }

    public static BigInteger toBigInteger(byte[] as400Value, int offset) {
        byte bytes[] = new byte[NUM_BYTES + 1];
        bytes[0] = 0;
        System.arraycopy(as400Value, offset, bytes, 1, NUM_BYTES);
        return new BigInteger(bytes);
    }
}
