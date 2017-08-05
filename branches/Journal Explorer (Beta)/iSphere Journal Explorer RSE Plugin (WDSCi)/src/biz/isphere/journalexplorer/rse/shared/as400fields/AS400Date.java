/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.rse.shared.as400fields;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.TimeZone;

public class AS400Date {

    private static TimeZone defaultTimeZone;

    private static Map<String, AS400DateFormat> dateFormatsMap;
    private static AS400DateFormat[] dateFormatsTable;

    private AS400DateFormat dateFormat;
    private TimeZone timeZone;
    private Character dateDelimiter;

    private static Map<String, AS400DateFormat> getDateFormatsMap() {
        initializeDateFormats();
        return dateFormatsMap;
    }

    private static AS400DateFormat[] getDateFormatsTable() {
        getDateFormatsMap();
        return dateFormatsTable;
    }

    private static void initializeDateFormats() {
        if (dateFormatsMap == null) {
            synchronized (AS400Date.class) {
                if (dateFormatsMap == null) {
                    dateFormatsMap = new Hashtable<String, AS400DateFormat>(12);
                    dateFormatsMap.put(AS400DateFormat.MDY.rpgLiteral(), AS400DateFormat.MDY);
                    dateFormatsMap.put(AS400DateFormat.DMY.rpgLiteral(), AS400DateFormat.DMY);
                    dateFormatsMap.put(AS400DateFormat.YMD.rpgLiteral(), AS400DateFormat.YMD);
                    dateFormatsMap.put(AS400DateFormat.JUL.rpgLiteral(), AS400DateFormat.JUL);
                    dateFormatsMap.put(AS400DateFormat.ISO.rpgLiteral(), AS400DateFormat.ISO);
                    dateFormatsMap.put(AS400DateFormat.USA.rpgLiteral(), AS400DateFormat.USA);
                    dateFormatsMap.put(AS400DateFormat.EUR.rpgLiteral(), AS400DateFormat.EUR);
                    dateFormatsMap.put(AS400DateFormat.JIS.rpgLiteral(), AS400DateFormat.JIS);
                    dateFormatsMap.put(AS400DateFormat.CYMD.rpgLiteral(), AS400DateFormat.CYMD);
                    dateFormatsMap.put(AS400DateFormat.CMDY.rpgLiteral(), AS400DateFormat.CMDY);
                    dateFormatsMap.put(AS400DateFormat.CDMY.rpgLiteral(), AS400DateFormat.CDMY);
                    dateFormatsMap.put(AS400DateFormat.LONGJUL.rpgLiteral(), AS400DateFormat.LONGJUL);

                    dateFormatsTable = new AS400DateFormat[dateFormatsMap.size()];
                    dateFormatsTable[AS400DateFormat.MDY.index()] = AS400DateFormat.MDY;
                    dateFormatsTable[AS400DateFormat.DMY.index()] = AS400DateFormat.DMY;
                    dateFormatsTable[AS400DateFormat.YMD.index()] = AS400DateFormat.YMD;
                    dateFormatsTable[AS400DateFormat.JUL.index()] = AS400DateFormat.JUL;
                    dateFormatsTable[AS400DateFormat.ISO.index()] = AS400DateFormat.ISO;
                    dateFormatsTable[AS400DateFormat.USA.index()] = AS400DateFormat.USA;
                    dateFormatsTable[AS400DateFormat.EUR.index()] = AS400DateFormat.EUR;
                    dateFormatsTable[AS400DateFormat.JIS.index()] = AS400DateFormat.JIS;
                    dateFormatsTable[AS400DateFormat.CYMD.index()] = AS400DateFormat.CYMD;
                    dateFormatsTable[AS400DateFormat.CMDY.index()] = AS400DateFormat.CMDY;
                    dateFormatsTable[AS400DateFormat.CDMY.index()] = AS400DateFormat.CDMY;
                    dateFormatsTable[AS400DateFormat.LONGJUL.index()] = AS400DateFormat.LONGJUL;
                }
            }
        }
    }

    public AS400Date() {
        this(getDefaultTimeZone());
    }

    public AS400Date(TimeZone timeZone) {
        this(timeZone, getDefaultFormat().index());
    }

    public AS400Date(TimeZone timeZone, int format) {
        this.timeZone = timeZone;
        setDateFormat(format);
    }

    public AS400Date(TimeZone timeZone, int format, Character delimiter) {
        this(timeZone, format);
        setDelimiter(delimiter);
    }

    public java.sql.Date parse(String date) {

        try {

            SimpleDateFormat formatter = dateFormat.getFormatter(timeZone, dateDelimiter);

            if (dateFormat.is3DigitYearFormat()) {
                Date startDate = dateFormat.get3DigitYearFormatStartDate(date, timeZone);
                formatter.set2DigitYearStart(startDate);
                return new java.sql.Date(formatter.parse(date.substring(1)).getTime());
            } else if (dateFormat.is2DigitYearFormat()) {
                Date startDate = dateFormat.get2DigitYearFormatStartDate(date, timeZone, dateDelimiter);
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

    private void setDateFormat(int dateFormat) {
        validateDateFormat(dateFormat);
        this.dateFormat = getDateFormatsTable()[dateFormat];
        setDelimiter(this.dateFormat.delimiter());
    }

    private void setDelimiter(Character delimiter) {
        this.dateDelimiter = delimiter;
    }

    private void validateDateFormat(int dateFormat) {
        if (dateFormat < 0 || dateFormat > getDateFormatsTable().length - 1) {
            throw getIllegalDateFormatException(dateFormat);
        }

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

        AS400DateFormat dateFormat = (AS400DateFormat)getDateFormatsMap().get(rpgLiteral.trim().toUpperCase());

        if (dateFormat == null) {
            throw getIllegalDateFormatException(rpgLiteral);
        }

        return dateFormat.index();
    }

    private static IllegalArgumentException getIllegalDateFormatException(int dateFormat) {
        return new IllegalArgumentException("Illegal date format index: " + Integer.toString(dateFormat));
    }

    private static IllegalArgumentException getIllegalDateFormatException(String rpgLiteral) {
        return new IllegalArgumentException("Illegal date format: " + rpgLiteral);
    }

}
