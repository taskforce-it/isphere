/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

public final class IntHelper {

    public static final String BYTE = "bytes"; //$NON-NLS-1$
    public static final String KILO_BYTE = "kB"; //$NON-NLS-1$
    public static final String MEGA_BYTE = "MB"; //$NON-NLS-1$
    public static final String GIGA_BYTE = "GB"; //$NON-NLS-1$
    public static final String TERA_BYTE = "TB"; //$NON-NLS-1$

    private static final String[] UNITS = new String[] { BYTE, KILO_BYTE, MEGA_BYTE, GIGA_BYTE, TERA_BYTE };

    /**
     * Parses an Integer value from a given hex string text.
     * 
     * @param someText Text representing an integer value.
     * @return Integer on success, else null.
     */
    public static Integer tryParseHex(String someText) {
        try {
            return Integer.parseInt(someText, 16);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Parses an <code>int</code> value from a given hex string text.
     * 
     * @param someText Text representing an <code>int</code> value.
     * @param defaultValue Default value that is returned on invalid input
     *        values.
     * @return <code>int</code> value on success, else the specified default
     *         value.
     */
    public static Integer tryParseHex(String someText, int defaultValue) {
        Integer integer = tryParseHex(someText);
        if (integer == null) {
            return new Integer(defaultValue);
        }
        return integer;
    }

    /**
     * Parses an Integer value from a given text.
     * 
     * @param someText Text representing an integer value.
     * @return Integer on success, else null.
     */
    public static Integer tryParseInt(String someText) {
        try {
            return Integer.parseInt(someText);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Parses an Integer value from a given text.
     * 
     * @param someText Text representing an integer value.
     * @param defaultValue Default value that is returned on invalid input
     *        values.
     * @return Integer on success, else the specified default value.
     */
    public static Integer tryParseInt(String someText, Integer defaultValue) {
        Integer integer = tryParseInt(someText);
        if (integer == null) {
            return defaultValue;
        }
        return integer;
    }

    /**
     * Parses an <code>int</code> value from a given text.
     * 
     * @param someText Text representing an <code>int</code> value.
     * @param defaultValue Default value that is returned on invalid input
     *        values.
     * @return <code>int</code> value on success, else the specified default
     *         value.
     */
    public static Integer tryParseInt(String someText, int defaultValue) {
        Integer integer = tryParseInt(someText);
        if (integer == null) {
            return new Integer(defaultValue);
        }
        return integer;
    }

    /**
     * Aligns a given buffer size to the next lower 16-byte-boundary.
     * 
     * @param value - buffer size
     * @return - value, aligned to 16-byte-boundary
     */
    public static int align16Bytes(int value) {
        return (value / 16) * 16;
    }

    /**
     * Converts a given memory size to string value with a memory unit, e.g. kB,
     * MB, etc.
     * 
     * @param memorySize - memory size in bytes
     * @return string value representing the memory size
     */
    public static String convertStorageSizeToLabel(long memorySize) {

        if (memorySize % 1024 != 0) {
            return Long.toString(memorySize);
        }

        int index = 0;
        long tBufferSize = memorySize;
        while (tBufferSize >= 1024) {
            index++;
            tBufferSize = tBufferSize / 1024;
        }

        if (index >= 0 && index < UNITS.length) {
            return tBufferSize + " " + UNITS[index]; //$NON-NLS-1$
        }

        return Long.toString(memorySize);
    }

    /**
     * Converts a string representation of a given memory site to a numeric
     * value in bytes.
     * 
     * @param label - string representation of a memory size
     * @return memory size in bytes
     */
    public static long convertLabelToStorageSize(String label) {

        long numericValue = -1;

        if (label != null) {

            int i = -1;
            int multipier = 1;

            if (label.endsWith(BYTE)) {
                i = label.indexOf(BYTE);
                multipier = (int)Math.pow(1024, 0);
            } else if (label.endsWith(KILO_BYTE)) {
                i = label.indexOf(KILO_BYTE);
                multipier = (int)Math.pow(1024, 1);
            } else if (label.endsWith(MEGA_BYTE)) {
                i = label.indexOf(MEGA_BYTE);
                multipier = (int)Math.pow(1024, 2);
            } else if (label.endsWith(GIGA_BYTE)) {
                i = label.indexOf(GIGA_BYTE);
                multipier = (int)Math.pow(1024, 3);
            } else if (label.endsWith(TERA_BYTE)) {
                i = label.indexOf(TERA_BYTE);
                multipier = (int)Math.pow(1024, 4);
            }

            String stringValue = label;
            if (i > 0) {
                stringValue = stringValue.substring(0, i).trim();
            }

            numericValue = IntHelper.tryParseInt(stringValue, -1);
            if (numericValue > 0) {
                numericValue = numericValue * multipier;
            }
        }

        if (numericValue > 0) {
            return numericValue;
        }

        return -1;
    }

}
