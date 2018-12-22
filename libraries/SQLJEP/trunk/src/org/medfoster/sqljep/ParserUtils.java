/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.medfoster.sqljep.exceptions.ParseException;

public final class ParserUtils {

    private final static int DATE_ISO = 0;
    private final static int DATE_USA = 1;
    private final static int DATE_EUR = 2;

    private final static int TIME_ISO = 0;
    private final static int TIME_USA_AM = 1;
    private final static int TIME_USA_PM = 2;
    private final static int TIME_EUR = 3;

    private final static int TIMESTAMP_ISO = 0;
    private final static int TIMESTAMP_IBM = 1;

    private final static DateFormat[] dateFormats;
    private final static Pattern datePattern;

    private final static DateFormat[] timeFormats;
    private final static Pattern timePattern;

    private final static DateFormat[] timestampFormats;
    private final static Pattern timestampPattern;

    static {

        dateFormats = new DateFormat[3];
        dateFormats[DATE_ISO] = new DateFormat("[0-9]{4}-[0-9]{2}-[0-9]{2}", "YYYY-MM-DD");
        dateFormats[DATE_USA] = new DateFormat("[0-9]{2}/[0-9]{2}/[0-9]{4}", "MM/DD/yyyy");
        dateFormats[DATE_EUR] = new DateFormat("[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}", "DD.MM.YYYY");
        datePattern = Pattern.compile(getPattern(dateFormats));

        timeFormats = new DateFormat[4];
        timeFormats[TIME_ISO] = new DateFormat("[0-9]{2}\\.[0-9]{2}\\.[0-9]{2}", "HH24.MI.SS");
        timeFormats[TIME_USA_AM] = new DateFormat("[0-9]{2}:[0-9]{2}\\s*AM", "HH12:MI AM");
        timeFormats[TIME_USA_PM] = new DateFormat("[0-9]{2}:[0-9]{2}\\s*PM", "HH12:MI PM");
        timeFormats[TIME_EUR] = new DateFormat("[0-9]{2}:[0-9]{2}:[0-9]{2}", "HH24:MI:SS");
        timePattern = Pattern.compile(getPattern(timeFormats), Pattern.CASE_INSENSITIVE);

        timestampFormats = new DateFormat[2];
        timestampFormats[TIMESTAMP_ISO] = new DateFormat("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}(?:.[0-9]{0,12})?",
            "YYYY-MM-DD HH24:MI:SS.NNN");
        timestampFormats[TIMESTAMP_IBM] = new DateFormat("[0-9]{4}-[0-9]{2}-[0-9]{2}-[0-9]{2}.[0-9]{2}.[0-9]{2}(?:.[0-9]{0,12})?",
            "YYYY-MM-DD-HH24.MI.SS.NNN");
        timestampPattern = Pattern.compile(getPattern(timestampFormats), Pattern.CASE_INSENSITIVE);
    }

    public static String getDateFormat(String string) throws ParseException {

        String format = getDateFormatChecked(string);
        if (format != null) {
            return format;
        }

        throw new ParseException("Unknown date format: " + string);
    }

    public static String getDateFormatChecked(String string) throws ParseException {

        Matcher matcher = datePattern.matcher(string);
        if (matcher.matches()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                if (matcher.group(i + 1) != null) {
                    return dateFormats[i].getFormat();
                }
            }
        }

        return null;
    }

    public static String getTimeFormat(String string) throws ParseException {

        String format = getTimeFormatChecked(string);
        if (format != null) {
            return format;
        }

        throw new ParseException("Unknown time format: " + string);
    }

    public static String getTimeFormatChecked(String string) throws ParseException {

        Matcher matcher = timePattern.matcher(string);
        if (matcher.matches()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                if (matcher.group(i + 1) != null) {
                    return timeFormats[i].getFormat();
                }
            }
        }

        return null;
    }

    public static String getTimestampFormat(String string) throws ParseException {

        String format = getTimestampFormatChecked(string);
        if (format != null) {
            return format;
        }

        throw new ParseException("Unknown timestamp format: " + string);
    }

    public static String getTimestampFormatChecked(String string) throws ParseException {

        Matcher matcher = timestampPattern.matcher(string);
        if (matcher.matches()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                if (matcher.group(i + 1) != null) {
                    return timestampFormats[i].getFormat();
                }
            }
        }

        return null;
    }

    private static String getPattern(DateFormat... dateFormats) {

        StringBuilder buffer = new StringBuilder();

        for (DateFormat dateFormat : dateFormats) {
            if (buffer.length() > 0) {
                buffer.append("|");
            }
            buffer.append("(");
            buffer.append(dateFormat.getPattern());
            buffer.append(")");
        }

        return buffer.toString();
    }

    private static class DateFormat {

        private String pattern;
        private String format;

        public DateFormat(String pattern, String format) {
            this.pattern = pattern;
            this.format = format;
        }

        public String getPattern() {
            return pattern;
        }

        public String getFormat() {
            return format;
        }
    }
}
