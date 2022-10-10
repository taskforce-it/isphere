/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.base.as400.system;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import com.ibm.as400.access.AS400Timestamp;
import com.ibm.as400.access.DateTimeConverter;

/**
 * This class emulates the IBM {@link DateTimeConverter}. The drawback of the
 * DateTimeConverter is its bad performance, because it calls the QWCCVTDT API
 * under the cover. This class is a pure Java implementation and a lot faster.
 * It uses a slightly modified source code of the JTOPEN {@link AS400Timestamp}
 * API.
 * <p>
 * Timestamp format *DTS ("Standard Time Format").
 * <ul>
 * <li>Example: The timestamp value 0x8000000000000000 represents 2000-01-01
 * 00:00:00.000000, in the time zone context of the IBM i system.
 * <li>Range of years: 1928-2071 <br>
 * (Date range: 1928-07-25 00:00:00.000000 to 2071-05-09 00:00:00.000000)
 * <li>Default separator: not applicable (no separator)
 * <li>Length: 8 bytes
 * <li>Note: The time zone context is the time zone of the IBM i system, rather
 * than GMT. <br>
 * The base date and time for the TOD clock, or the date and time represented by
 * hex value 0x0000000000000000, is August 23, 1928 12:03:06.314752 (in the time
 * zone of the IBM i system).
 * </ul>
 */
public class DTSDateConverter {

    private static final BigInteger DTS_CONVERSION_FACTOR = new BigInteger("946684800000000");
    private static final BigInteger ONE_THOUSAND = new BigInteger("1000");

    public DTSDateConverter() {
    }

    /**
     * Converts a standard time format ( *DTS) date to a Java date. This method
     * emulates the IBM {@link DateTimeConverter}
     * 
     * @param bytes - *DTS byte array
     * @return date
     */
    public Timestamp convert(byte[] as400Value, boolean ignoreDaylightSaving) {

        if (as400Value.length != 8) {
            throw new IllegalArgumentException("DTS date must be an 8-byte array."); //$NON-NLS-1$
        }

        // Determine the "elapsed microseconds" value represented by the *DTS
        // value.
        // Note that *DTS values, in theory, specify microseconds elapsed since
        // August 23, 1928 12:03:06.314752.
        // However, the real reference point is January 1, 2000,
        // 00:00:00.000000,
        // which is represented by *DTS value 0x8000000000000000.

        // In the returned *DTS value, only the first 8 bytes are meaningful.
        // Of those 8 bytes, only bits 0-51 are used to represent
        // "elapsed microseconds".

        // To prevent sign-extension when we right-shift the bits:
        // Copy the first 8 bytes into a 9-byte array, preceded by 0x00.
        byte[] bytes9 = new byte[9];
        System.arraycopy(as400Value, 0, bytes9, 1, 8); // right-justify
        BigInteger bits0to63 = new BigInteger(bytes9); // bits 0-63

        // Convert base of date from August 23, 1928 12:03:06.314752 to January
        // 1, 2000, 00:00:00.000000.
        byte[] dts2000 = { 0, (byte)0x80, 0, 0, 0, 0, 0, 0, 0 }; // 0x8000000000000000
        BigInteger basedOn2000 = bits0to63.subtract(new BigInteger(dts2000));

        // Eliminate the "uniqueness bits" (bits 52-63).
        // Right-shift 12 bits, without sign-extension, leaving bits 0-51.
        BigInteger microsElapsedSince2000 = basedOn2000.shiftRight(12);

        // Convert the above value to
        // "microseconds elapsed since January 1, 1970, 00:00:00". That gets us
        // closer to a value we can use to create a Java timestamp object.
        BigInteger microsElapsedSince1970 = microsElapsedSince2000.add(DTS_CONVERSION_FACTOR);

        // Milliseconds elapsed since January 1, 1970, 00:00:00
        BigInteger[] result = microsElapsedSince1970.divideAndRemainder(ONE_THOUSAND);
        BigInteger millisElapsedSince1970 = result[0];
        BigInteger remainder = result[1];

        // Round up 1 millisecond if necessary
        long millisSince1970 = millisElapsedSince1970.longValue();
        if (remainder.intValue() >= 500) {
            millisSince1970++;
        }

        // Convert milliseconds to java.util.Date
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTimeInMillis(millisSince1970);

        if (ignoreDaylightSaving) {
            int dstOffset = localCalendar.get(Calendar.DST_OFFSET);
            localCalendar.add(Calendar.MILLISECOND, (dstOffset * -1));
        }

        // Fix timezone offset
        int offsetMillisGMT = TimeZone.getDefault().getRawOffset();
        localCalendar.add(Calendar.MILLISECOND, offsetMillisGMT * -1);

        Timestamp timestamp = new Timestamp(localCalendar.getTimeInMillis());

        return timestamp;
    }
}
