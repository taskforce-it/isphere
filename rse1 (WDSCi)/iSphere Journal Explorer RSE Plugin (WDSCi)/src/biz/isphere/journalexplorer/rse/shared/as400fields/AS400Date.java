/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.rse.shared.as400fields;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.TimeZone;

public class AS400Date {

    public static final int FORMAT_MDY = AS400DateFormat.MDY.format();
    public static final int FORMAT_DMY = AS400DateFormat.DMY.format();
    public static final int FORMAT_YMD = AS400DateFormat.YMD.format();
    public static final int FORMAT_JUL = AS400DateFormat.JUL.format();
    public static final int FORMAT_USA = AS400DateFormat.USA.format();
    public static final int FORMAT_EUR = AS400DateFormat.EUR.format();
    public static final int FORMAT_JIS = AS400DateFormat.JIS.format();
    public static final int FORMAT_CYMD = AS400DateFormat.CYMD.format();
    public static final int FORMAT_CMDY = AS400DateFormat.CMDY.format();
    public static final int FORMAT_CDMY = AS400DateFormat.CDMY.format();
    public static final int FORMAT_LONGJUL = AS400DateFormat.LONGJUL.format();

    private static TimeZone defaultTimeZone;

    private static Map<String, AS400DateFormat> dateFormatsMapExt;
    private static Map<Integer, AS400DateFormat> dateFormatsMapInt;

    private AS400DateFormat dateFormat;
    private TimeZone timeZone;
    private Character dateSeparator;

    private static Map<String, AS400DateFormat> getDateFormatsMap() {
        initializeDateFormats();
        return dateFormatsMapExt;
    }

    private static Map<Integer, AS400DateFormat> getDateFormatsTable() {
        initializeDateFormats();
        return dateFormatsMapInt;
    }

    private static void initializeDateFormats() {
        if (dateFormatsMapExt == null) {
            synchronized (AS400Date.class) {
                if (dateFormatsMapExt == null) {
                    dateFormatsMapExt = new Hashtable<String, AS400DateFormat>();
                    dateFormatsMapExt.put(AS400DateFormat.MDY.rpgLiteral(), AS400DateFormat.MDY);
                    dateFormatsMapExt.put(AS400DateFormat.DMY.rpgLiteral(), AS400DateFormat.DMY);
                    dateFormatsMapExt.put(AS400DateFormat.YMD.rpgLiteral(), AS400DateFormat.YMD);
                    dateFormatsMapExt.put(AS400DateFormat.JUL.rpgLiteral(), AS400DateFormat.JUL);
                    dateFormatsMapExt.put(AS400DateFormat.ISO.rpgLiteral(), AS400DateFormat.ISO);
                    dateFormatsMapExt.put(AS400DateFormat.USA.rpgLiteral(), AS400DateFormat.USA);
                    dateFormatsMapExt.put(AS400DateFormat.EUR.rpgLiteral(), AS400DateFormat.EUR);
                    dateFormatsMapExt.put(AS400DateFormat.JIS.rpgLiteral(), AS400DateFormat.JIS);
                    dateFormatsMapExt.put(AS400DateFormat.CYMD.rpgLiteral(), AS400DateFormat.CYMD);
                    dateFormatsMapExt.put(AS400DateFormat.CMDY.rpgLiteral(), AS400DateFormat.CMDY);
                    dateFormatsMapExt.put(AS400DateFormat.CDMY.rpgLiteral(), AS400DateFormat.CDMY);
                    dateFormatsMapExt.put(AS400DateFormat.LONGJUL.rpgLiteral(), AS400DateFormat.LONGJUL);

                    dateFormatsMapInt = new Hashtable<Integer, AS400DateFormat>();
                    Collection<AS400DateFormat> dateFormats = dateFormatsMapExt.values();
                    for (AS400DateFormat dateFormat : dateFormats) {
                        dateFormatsMapInt.put(dateFormat.format(), dateFormat);
                    }
                }
            }
        }
    }

    public AS400Date() {
        this(getDefaultTimeZone());
    }

    public AS400Date(TimeZone timeZone) {
        this(timeZone, getDefaultFormat().format());
    }

    public AS400Date(TimeZone timeZone, int format) {
        this.timeZone = timeZone;
        setDateFormat(format);
    }

    public AS400Date(TimeZone timeZone, int format, Character separator) {
        this(timeZone, format);
        setSeparator(separator);
    }

    public java.sql.Date parse(String date) {

        try {

            SimpleDateFormat formatter = dateFormat.getFormatter(timeZone, dateSeparator);

            if (dateFormat.is3DigitYearFormat()) {
                Date startDate = dateFormat.get3DigitYearFormatStartDate(date, timeZone);
                formatter.set2DigitYearStart(startDate);
                return new java.sql.Date(formatter.parse(date.substring(1)).getTime());
            } else if (dateFormat.is2DigitYearFormat()) {
                Date startDate = dateFormat.get2DigitYearFormatStartDate(date, timeZone, dateSeparator);
                formatter.set2DigitYearStart(startDate);
                return new java.sql.Date(formatter.parse(date).getTime());
            } else {
                // 4-digit year format
                return new java.sql.Date(formatter.parse(date).getTime());
            }

        } catch (Exception e) {
            throw getIllegalDateFormatException(date);
        }
    }

    private void setDateFormat(int format) {

        AS400DateFormat dateFormat = getDateFormat(format);
        if (dateFormat == null) {
            throw getIllegalDateFormatException(format);
        }

        this.dateFormat = dateFormat;
        setSeparator(this.dateFormat.separator());
    }

    private void setSeparator(Character separator) {

        if (separator != null && !this.dateFormat.isValidSeparator(separator)) {
            throw new IllegalArgumentException("Invalid separator: " + separator.toString());
        }

        this.dateSeparator = separator;
    }

    private AS400DateFormat getDateFormat(int format) {

        return getDateFormatsTable().get(format);
    }

    private static TimeZone getDefaultTimeZone() {

        if (defaultTimeZone == null) {
            defaultTimeZone = GregorianCalendar.getInstance().getTimeZone();
        }

        return defaultTimeZone;
    }

    private static AS400DateFormat getDefaultFormat() {
        return AS400DateFormat.ISO;
    }

    public static int toFormat(String rpgLiteral) {

        if ((rpgLiteral == null) || (rpgLiteral.length() == 0)) {
            throw getIllegalDateFormatException(rpgLiteral);
        }

        if (rpgLiteral.startsWith("*")) {
            rpgLiteral = rpgLiteral.substring(1);
        }

        AS400DateFormat dateFormat = getDateFormatsMap().get(rpgLiteral.trim().toUpperCase());

        if (dateFormat == null) {
            throw getIllegalDateFormatException(rpgLiteral);
        }

        return dateFormat.format();
    }

    public static int getByteLength(int format) {

        if (format == AS400DateFormat.FORMAT_MDY || format == AS400DateFormat.FORMAT_DMY || format == AS400DateFormat.FORMAT_YMD) {
            return 6;
        } else if (format == AS400DateFormat.FORMAT_LONGJUL) {
            return 7;
        } else if (format == AS400DateFormat.FORMAT_JUL) {
            return 5;
        } else if (format == AS400DateFormat.FORMAT_ISO || format == AS400DateFormat.FORMAT_USA || format == AS400DateFormat.FORMAT_EUR
            || format == AS400DateFormat.FORMAT_JIS) {
            return 8;
        } else if (format == AS400DateFormat.FORMAT_CYMD || format == AS400DateFormat.FORMAT_CMDY || format == AS400DateFormat.FORMAT_CDMY) {
            return 7;
        }

        throw getIllegalDateFormatException(format);
    }

    public static int getByteLength(int format, Character separator) {

        if (separator == null) {
            return getByteLength(format);
        }

        if (format == AS400DateFormat.JUL.format()) {
            return 6;
        } else if (format == AS400DateFormat.MDY.format() || format == AS400DateFormat.DMY.format() || format == AS400DateFormat.YMD.format()
            || format == AS400DateFormat.LONGJUL.format()) {
            return 8;
        } else if (format == AS400DateFormat.ISO.format() || format == AS400DateFormat.USA.format() || format == AS400DateFormat.EUR.format()
            || format == AS400DateFormat.JIS.format()) {
            return 10;
        } else if (format == AS400DateFormat.CYMD.format() || format == AS400DateFormat.CMDY.format() || format == AS400DateFormat.CDMY.format()) {
            return 9;
        }

        throw getIllegalDateFormatException(format);
    }

    private static IllegalArgumentException getIllegalDateFormatException(int dateFormat) {
        return new IllegalArgumentException("Illegal date format: " + Integer.toString(dateFormat));
    }

    private static IllegalArgumentException getIllegalDateFormatException(String rpgLiteral) {
        return new IllegalArgumentException("Illegal date format literal: " + rpgLiteral);
    }

}
