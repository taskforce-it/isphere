/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.base.as400.system;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class DTSDate {

    private static final BigInteger MILLISECONDS = new BigInteger("1000");

    private Calendar startingDate;

    public DTSDate() {
        this.startingDate = getStartingDate();
    }

    /**
     * Converts a standard time format ( *DTS) date to a Java date.
     * 
     * @param bytes - *DTS byte array
     * @return date
     */
    public Date getDate(byte[] bytes) {

        // TimeTaken timeTaken = new TimeTaken("getDTSDate()");

        if (bytes.length != 8) {
            throw new IllegalArgumentException("DTS date must be an 8-byte array."); //$NON-NLS-1$
        }

        /*
         * The time field is a binary number which can be interpreted as a time
         * value in units of 1 microsecond. A binary 1 in bit 51 is equal to 1
         * microsecond. Shift 12 bits to the right, to remove the
         * "uniqueness bits" 52-63. See: com.ibm.as400.access.AS400Timestamp
         */
        BigInteger shifted = new BigInteger(shiftRight(bytes, 12));

        /*
         * Divide by 1000 to get milliseconds.
         */
        long millisSince1928 = shifted.divide(MILLISECONDS).longValue();

        /*
         * Round up, if necessary.
         */
        long microSeconds = shifted.mod(MILLISECONDS).longValue();
        if (microSeconds > 500) {
            millisSince1928++;
        }

        /*
         * Get DTS starting date and add milliseconds since then.
         */
        long startingDate1928 = startingDate.getTimeInMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startingDate1928 + millisSince1928);

        Date dtsDate = calendar.getTime();

        // timeTaken.stop();

        return dtsDate;
    }

    /**
     * Returns the starting date for converting a DTS date to date. This
     * procedure is a Java implementation of the QWCCVTDT API. The starting date
     * is: <i>August 23, 1928 12:03:06.314752August 23, 1928 12:03:06.314752</i>
     * as described in {@link com.ibm.as400.access.AS400Timestamp}
     * <p>
     * 
     * @see com.ibm.as400.access.AS400Timestamp
     */
    private Calendar getStartingDate() {

        Calendar c = Calendar.getInstance();

        c.clear();
        c.set(Calendar.YEAR, 1928);
        c.set(Calendar.MONTH, Calendar.AUGUST);
        c.set(Calendar.DAY_OF_MONTH, 23);
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 3);
        c.set(Calendar.SECOND, 6);
        c.set(Calendar.MILLISECOND, 315);

        return c;
    }

    /**
     * Shifts a given byte array a specified number of bits to the right. The
     * number of bits to shift must be a multiple of 4, since we shift nibbles.
     * 
     * @param bytes - byte array that is shifted
     * @param count - number of bits to shift
     * @return array, shifted to the right
     */
    private byte[] shiftRight(byte[] bytes, int count) {

        if (count % 4 != 0) {
            throw new IllegalArgumentException("Number of bits must be a multiple of 4."); //$NON-NLS-1$
        }

        byte[] dtsBytesRtn = Arrays.copyOf(bytes, bytes.length);

        int shift = count;
        while (shift > 0) {
            int shifted;
            if (shift > 4) {
                shifted = 4;
            } else {
                shifted = shift;
            }

            int nextByte;
            int currentByte;
            for (int i = dtsBytesRtn.length - 1; i >= 0; i--) {
                currentByte = dtsBytesRtn[i];
                if (i > 0) {
                    nextByte = (int)dtsBytesRtn[i - 1];
                } else {
                    nextByte = 0;
                }

                dtsBytesRtn[i] = (byte)((currentByte >> 4 & 0x0F) | ((nextByte & 0x0F) << 4));
            }

            shift -= shifted;
        }

        return dtsBytesRtn;
    }

}
