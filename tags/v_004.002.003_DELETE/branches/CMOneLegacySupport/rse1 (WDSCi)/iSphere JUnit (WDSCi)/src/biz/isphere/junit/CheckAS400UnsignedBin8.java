/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.junit;

import static org.junit.Assert.fail;

import java.math.BigInteger;

import junit.framework.Assert;

import org.junit.Test;

import biz.isphere.journalexplorer.rse.as400.access.AS400UnsignedBin8;

import com.ibm.as400.access.AS400DataType;

/**
 * <b>JUnit 4 Test Case</b>
 * <p>
 * Checks class AS400Date.
 */
public class CheckAS400UnsignedBin8 {

    // JTOpen max value
    private static final BigInteger JTOPEN_MAX_VALUE = new BigInteger(new byte[] { 0, -1, -1, -1, -1, -1, -1, -1, -1 });
    private static final BigInteger JTOPEN_MIN_VALUE = BigInteger.ZERO;

    private static final byte[] HIGH_VALUE_BYTES = new byte[] { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
        (byte)0xFF };
    private static final byte[] LOW_VALUE_BYTES = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    @Test
    public void checkObjectToObjectLowValue() {

        Object actual = checkObjectToObjectInternally(getISphereConverter(), JTOPEN_MIN_VALUE, AS400UnsignedBin8.MIN_VALUE);
        Object expected = checkObjectToObjectInternally(getJTOpenConverter(), JTOPEN_MIN_VALUE, AS400UnsignedBin8.MIN_VALUE);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void checkObjectToObjectHighValue() {

        Object actual = checkObjectToObjectInternally(getISphereConverter(), JTOPEN_MAX_VALUE, AS400UnsignedBin8.MAX_VALUE);
        Object expected = checkObjectToObjectInternally(getJTOpenConverter(), JTOPEN_MAX_VALUE, AS400UnsignedBin8.MAX_VALUE);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void checkByteToByteLowValue() {

        byte[] result = checkBytesToBytesInternally(getISphereConverter(), LOW_VALUE_BYTES, LOW_VALUE_BYTES);
        byte[] expected = checkBytesToBytesInternally(getJTOpenConverter(), LOW_VALUE_BYTES, LOW_VALUE_BYTES);

        assertEquals(expected, result);
    }

    @Test
    public void checkByteToByteHighValue() {

        byte[] result = checkBytesToBytesInternally(getISphereConverter(), HIGH_VALUE_BYTES, HIGH_VALUE_BYTES);
        byte[] expected = checkBytesToBytesInternally(getJTOpenConverter(), HIGH_VALUE_BYTES, HIGH_VALUE_BYTES);

        assertEquals(expected, result);
    }

    @Test
    public void checkArrayIndexOutOfBoundException() {

        try {
            com.ibm.as400.access.AS400UnsignedBin8 converter = getJTOpenConverter();
            converter.toObject(new byte[] { 0x00 });
            fail("Expected: ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
            // exception seen ==> OK
        }

        try {
            AS400UnsignedBin8 converter = getISphereConverter();
            converter.toObject(new byte[] { 0x00 });
            fail("Expected: ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
            // exception seen ==> OK
        }

    }

    private Object checkObjectToObjectInternally(AS400DataType converter, Object expected, Object actual) {

        byte[] bytes = converter.toBytes(actual);
        BigInteger result = (BigInteger)converter.toObject(bytes);

        Assert.assertEquals(expected, result);

        return result;
    }

    private byte[] checkBytesToBytesInternally(AS400DataType converter, byte[] expected, byte[] actual) {

        BigInteger bigInteger = (BigInteger)converter.toObject(actual);
        byte[] result = converter.toBytes(bigInteger);

        assertEquals(expected, result);

        return result;
    }

    private void assertEquals(byte[] expected, byte[] actual) {

        Assert.assertEquals(expected.length, actual.length);

        for (int i = 0; i < expected.length; i++) {
            Assert.assertEquals(expected[i], actual[i]);
        }
    }

    private AS400UnsignedBin8 getISphereConverter() {
        return new AS400UnsignedBin8();
    }

    private com.ibm.as400.access.AS400UnsignedBin8 getJTOpenConverter() {
        return new com.ibm.as400.access.AS400UnsignedBin8();
    }
}
