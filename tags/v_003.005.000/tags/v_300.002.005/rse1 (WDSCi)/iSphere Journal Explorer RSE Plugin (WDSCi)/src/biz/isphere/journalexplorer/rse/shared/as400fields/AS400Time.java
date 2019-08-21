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
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.TimeZone;

public class AS400Time {

    public static final int FORMAT_HMS = AS400TimeFormat.HMS.format();
    public static final int FORMAT_ISO = AS400TimeFormat.ISO.format();
    public static final int FORMAT_USA = AS400TimeFormat.USA.format();
    public static final int FORMAT_EUR = AS400TimeFormat.EUR.format();
    public static final int FORMAT_JIS = AS400TimeFormat.JIS.format();

    private static TimeZone defaultTimeZone;

    private static Map<String, AS400TimeFormat> timeFormatsMapExt;
    private static Map<Integer, AS400TimeFormat> timeFormatsMapInt;

    private AS400TimeFormat timeFormat;
    private TimeZone timeZone;
    private Character timeSeparator;

    private static Map<String, AS400TimeFormat> getTimeFormatsMap() {
        initializeTimeFormats();
        return timeFormatsMapExt;
    }

    private static Map<Integer, AS400TimeFormat> getTimeFormatsTable() {
        initializeTimeFormats();
        return timeFormatsMapInt;
    }

    private static void initializeTimeFormats() {
        if (timeFormatsMapExt == null) {
            synchronized (AS400Time.class) {
                if (timeFormatsMapExt == null) {
                    timeFormatsMapExt = new Hashtable<String, AS400TimeFormat>();
                    timeFormatsMapExt.put(AS400TimeFormat.HMS.rpgLiteral(), AS400TimeFormat.HMS);
                    timeFormatsMapExt.put(AS400TimeFormat.ISO.rpgLiteral(), AS400TimeFormat.ISO);
                    timeFormatsMapExt.put(AS400TimeFormat.USA.rpgLiteral(), AS400TimeFormat.USA);
                    timeFormatsMapExt.put(AS400TimeFormat.EUR.rpgLiteral(), AS400TimeFormat.EUR);
                    timeFormatsMapExt.put(AS400TimeFormat.JIS.rpgLiteral(), AS400TimeFormat.JIS);

                    timeFormatsMapInt = new Hashtable<Integer, AS400TimeFormat>();
                    Collection<AS400TimeFormat> timeFormats = timeFormatsMapExt.values();
                    for (AS400TimeFormat dateFormat : timeFormats) {
                        timeFormatsMapInt.put(dateFormat.format(), dateFormat);
                    }
                }
            }
        }
    }

    public AS400Time() {
        this(getDefaultTimeZone());
    }

    public AS400Time(TimeZone timeZone) {
        this(timeZone, getDefaultFormat().format());
    }

    public AS400Time(TimeZone timeZone, int format) {
        this.timeZone = timeZone;
        setTimeFormat(format);
    }

    public AS400Time(TimeZone timeZone, int format, Character separator) {
        this(timeZone, format);
        setSeparator(separator);
    }

    public java.sql.Time parse(String time) {

        try {

            SimpleDateFormat formatter = timeFormat.getFormatter(timeZone, timeSeparator);
            return new java.sql.Time(formatter.parse(time).getTime());

        } catch (Exception e) {
            throw getIllegalTimeFormatException(time);
        }
    }

    private void setTimeFormat(int format) {

        AS400TimeFormat timeFormat = getTimeFormat(format);
        if (timeFormat == null) {
            throw getIllegalTimeFormatException(format);
        }

        this.timeFormat = timeFormat;
        setSeparator(this.timeFormat.separator());
    }

    private void setSeparator(Character separator) {

        if (separator != null && !this.timeFormat.isValidSeparator(separator)) {
            throw new IllegalArgumentException("Invalid separator: " + separator.toString());
        }

        this.timeSeparator = separator;
    }

    private AS400TimeFormat getTimeFormat(int format) {

        return getTimeFormatsTable().get(format);
    }

    private static TimeZone getDefaultTimeZone() {

        if (defaultTimeZone == null) {
            defaultTimeZone = GregorianCalendar.getInstance().getTimeZone();
        }

        return defaultTimeZone;
    }

    private static AS400TimeFormat getDefaultFormat() {
        return AS400TimeFormat.ISO;
    }

    public static int toFormat(String rpgLiteral) {

        if ((rpgLiteral == null) || (rpgLiteral.length() == 0)) {
            throw getIllegalTimeFormatException(rpgLiteral);
        }

        if (rpgLiteral.startsWith("*")) {
            rpgLiteral = rpgLiteral.substring(1);
        }

        AS400TimeFormat timeFormat = getTimeFormatsMap().get(rpgLiteral.trim().toUpperCase());

        if (timeFormat == null) {
            throw getIllegalTimeFormatException(rpgLiteral);
        }

        return timeFormat.format();
    }

    public static int getByteLength(int format) {

        if (format == AS400TimeFormat.FORMAT_ISO || format == AS400TimeFormat.FORMAT_USA || format == AS400TimeFormat.FORMAT_EUR
            || format == AS400TimeFormat.FORMAT_JIS || format == AS400TimeFormat.FORMAT_HMS) {
            return 6;
        }

        throw getIllegalTimeFormatException(format);
    }

    public static int getByteLength(int format, Character separator) {

        if (separator == null) {
            return getByteLength(format);
        }

        if (format == AS400TimeFormat.ISO.format() || format == AS400TimeFormat.USA.format() || format == AS400TimeFormat.EUR.format()
            || format == AS400TimeFormat.JIS.format() || format == AS400TimeFormat.FORMAT_HMS) {
            return 8;
        }

        throw getIllegalTimeFormatException(format);
    }

    private static IllegalArgumentException getIllegalTimeFormatException(int timeFormat) {
        return new IllegalArgumentException("Illegal time format: " + Integer.toString(timeFormat));
    }

    private static IllegalArgumentException getIllegalTimeFormatException(String rpgLiteral) {
        return new IllegalArgumentException("Illegal time format literal: " + rpgLiteral);
    }

}
