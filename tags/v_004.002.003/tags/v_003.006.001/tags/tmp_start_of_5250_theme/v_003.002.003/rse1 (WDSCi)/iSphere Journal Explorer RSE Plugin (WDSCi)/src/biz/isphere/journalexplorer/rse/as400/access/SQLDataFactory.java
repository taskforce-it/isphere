///////////////////////////////////////////////////////////////////////////////
//
// JTOpen (IBM Toolbox for Java - OSS version)
//
// Filename: SQLDataFactory.java
//
// The source code contained herein is licensed under the IBM Public License
// Version 1.0, which has been approved by the Open Source Initiative.
// Copyright (C) 1997-2003 International Business Machines Corporation and
// others. All rights reserved.
//
// Imported to the iSphere project from Maven-Central jt400 version 8.5.
///////////////////////////////////////////////////////////////////////////////

package biz.isphere.journalexplorer.rse.as400.access;

import java.math.BigDecimal;

/**
 * <p>
 * A factory that generates appropriate SQLData objects given various
 * conditions.
 */
public class SQLDataFactory {
    // @DFA
    /**
     * Compute the smallest possible precision of a BigDecimal. This is the
     * total number of digits, disregarding the trailing 0's. Needed so that we
     * can use as a comparison for truncation. (ie. 9E17 in BigDecimal is
     * represented as BigInt(900000000000000000), but we want to calculate
     * precision taking into account that trailing 0's can be represented in an
     * exponent that is not possible in the BigDecimal object as a negative
     * scale.)
     * 
     * @param value BigDecimal object.
     * @param maxSize max size of precision (16 or 34 for decfloats)
     * @return the precision.
     */
    public static int[] getPrecisionForTruncation(BigDecimal value, int maxSize) // @rnd1
    {
        int precision = 0;

        String toString = value.unscaledValue().toString(); // value.toString();
        // 1.6 returns
        // "123E+4", 1.4
        // returns "1230000"

        // int pointIndex = value.scale();//@rnd1 toString.indexOf('.');

        if (toString.charAt(0) == '-') toString = toString.substring(1);

        int length = toString.length();

        // We need to truncate any ending zeroes. Without this,
        // the precision of 1e5 was getting computed as 5 rather
        // than 1.

        int endIndex = length;

        // @rnd1 if(pointIndex != 0)
        // @rnd1 maxSize++; //allow for extra '.' char
        while ((toString.charAt(--endIndex) == '0') && (endIndex > maxSize))
            ;

        int numberZeros = length - endIndex - 1; // @rnd1

        if (endIndex == maxSize) {
            if (toString.charAt(endIndex) == '0') {// @rnd1
                precision = endIndex;
                numberZeros++; // @rnd1
            }// @rnd1
            else
                precision = endIndex + 1;
        } else
            precision = endIndex + 1;

        // @rnd1 if(pointIndex != -1)
        // @rnd1 precision--;

        int[] retVal = new int[2]; // @rnd1
        retVal[0] = precision; // @rnd1
        retVal[1] = numberZeros; // @rnd1
        return retVal; // @rnd1
    }
}
