/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.function;

import static java.util.Calendar.AM_PM;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;

import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

@SuppressWarnings("serial")
public abstract class AbstractOracleDateTimeFormat extends Format {

    private static final String PATTERN_EXCEPTION = "Wrong pattern";
    private static final String BAD_HH12 = "Hour must be between 1 and 12";

    protected static final Hashtable<String, ArrayList<Object>> formatsCache = new Hashtable<String, ArrayList<Object>>();
    protected static final ArrayList<DATE> dateSymbols = new ArrayList<DATE>();
    protected static final ArrayList<DATE> timeSymbols = new ArrayList<DATE>();
    protected ArrayList<Object> format = null;

    protected Calendar calendar;
    protected DateFormatSymbols symb;

    @SuppressWarnings("unchecked")
    public AbstractOracleDateTimeFormat(String pattern) throws java.text.ParseException {

        calendar = Calendar.getInstance();
        symb = new DateFormatSymbols();
        format = formatsCache.get(pattern);

        if (format == null) {
            format = new ArrayList<Object>();
            ArrayList<DATE> symb = (ArrayList<DATE>)dateSymbols.clone();
            symb.addAll(timeSymbols);
            compilePattern(format, symb, pattern);
            formatsCache.put(pattern, format);
        }
    }

    @SuppressWarnings("unchecked")
    public AbstractOracleDateTimeFormat(String pattern, Calendar calendar, DateFormatSymbols dateSymb) throws java.text.ParseException {

        symb = dateSymb;
        format = formatsCache.get(pattern);

        if (format == null) {
            format = new ArrayList<Object>();
            ArrayList<DATE> symb = (ArrayList<DATE>)dateSymbols.clone();
            symb.addAll(timeSymbols);
            compilePattern(format, symb, pattern);
            formatsCache.put(pattern, format);
        }
    }

    protected long parseInMillis(String source, ParsePosition pos) {

        calendar.clear();
        calendar.set(DAY_OF_MONTH, 1);
        calendar.set(MONTH, 0);
        calendar.set(YEAR, 1970);

        final int slen = source.length();
        try {
            if (format != null) {
                for (Object obj : format) {
                    if (obj instanceof DATE) {
                        DATE d = (DATE)obj;
                        if (!(d instanceof NNN) || pos.getIndex() < slen) {
                            d.parse(calendar, symb, source, pos);
                        }
                        if (d instanceof NNN) {
                            eatNumbers(source, pos, 9);
                        }
                    }
                }
                for (int i = pos.getIndex(); i < slen; i++) {
                    final char c = source.charAt(i);
                    if (Character.isLetterOrDigit(c)) {
                        throw new java.text.ParseException(PATTERN_EXCEPTION, 0);
                    }
                }
                pos.setIndex(slen);
            }
        } catch (Exception e) {
            // e.printStackTrace();
            pos.setErrorIndex(pos.getIndex());
            pos.setIndex(0);
            return 0;
        }
        return calendar.getTimeInMillis();
    }

    public boolean hasMilliSeconds() {
        return calendar.isSet(MILLISECOND);
    }

    public String toString() {
        if (format != null) {
            StringBuilder str = new StringBuilder();
            for (Object obj : format) {
                str.append(obj.toString());
            }
            return str.toString();
        } else {
            return "null";
        }
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OracleTimestampFormat other = (OracleTimestampFormat)obj;
        return (format != null) ? format.equals(other.format) : false;
    }

    @SuppressWarnings("rawtypes")
    protected java.util.Date stripMilliSeconds(Comparable time) {

        calendar.clear();
        calendar.setTime((Date)time);
        calendar.set(Calendar.MILLISECOND, 0);

        return new java.sql.Timestamp(calendar.getTimeInMillis());
    }

    private void eatNumbers(String source, ParsePosition pos, int digits) throws ParseException {

        final int len = source.length();
        for (int i = pos.getIndex(); i < len; i++) {
            if (Character.isDigit(source.charAt(i))) {
                pos.setIndex(pos.getIndex() + 1);
            } else {
                throw new java.text.ParseException("", i);
            }
        }
    }

    private void compilePattern(ArrayList<Object> format, ArrayList<DATE> symbols, String pattern) throws java.text.ParseException {
        if (pattern == null) {
            throw new java.text.ParseException(PATTERN_EXCEPTION, 0);
        }
        StringBuilder fill = new StringBuilder();
        final int plen = pattern.length();
        for (int i = 0; i < plen;) {
            boolean f = false;
            for (DATE d : symbols) {
                String symb = d.toString();
                if (i + symb.length() <= plen && pattern.regionMatches(true, i, symb, 0, symb.length())) {
                    if (fill.length() > 0) {
                        format.add(fill.toString());
                        fill.setLength(0);
                    }
                    format.add(d);
                    i += symb.length();
                    f = true;
                    break;
                }
            }
            if (!f) {
                char c = pattern.charAt(i);
                if (!Character.isLetterOrDigit(c)) {
                    fill.append(c);
                    i++;
                } else {
                    throw new java.text.ParseException(PATTERN_EXCEPTION, i);
                }
            }
        }
        if (fill.length() > 0) {
            format.add(fill.toString());
        }
    }

    public StringBuffer format(Object obj, StringBuffer str, FieldPosition fieldPosition) {

        if (obj instanceof java.util.Date) {

            // java.sql.Date or java.sql.Time or java.sql.Timestamp
            java.util.Date date = (java.util.Date)obj;
            calendar.setTime(date);

            if (format != null) {
                for (Object f : format) {
                    if (f instanceof String) {
                        str.append((String)f);
                    } else {
                        DATE d = (DATE)f;
                        try {
                            d.toString(str, calendar, symb);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return str;
    }

    public static final DATE findFormat(String format) {

        if (format == null) {
            throw new IllegalArgumentException("Parameter 'format' must not be null");
        }

        for (DATE symbol : dateSymbols) {
            if (symbol.toString().equalsIgnoreCase(format)) {
                return symbol;
            }
        }

        for (DATE symbol : timeSymbols) {
            if (symbol.toString().equalsIgnoreCase(format)) {
                return symbol;
            }
        }

        return null;
    }

    /**
     * Format pattern: year
     */
    public static final class YYYY extends DATE {
        public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
            int year = cal.get(YEAR);
            String y = Integer.toString(Math.abs(year));
            for (int i = y.length(); i < 4; i++) {
                str.append('0');
            }
            str.append(y);
            return str;
        }

        public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
            int year = getNumber(source, pos, 4);
            cal.set(YEAR, year);
        }

        public String toString() {
            return "YYYY";
        }
    };

    static {
        dateSymbols.add(new YYYY());
    }

    /**
     * Format pattern: month
     */
    public static final class MM extends DATE {
        public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
            int month = cal.get(MONTH) + 1;
            String m = Integer.toString(month);
            if (m.length() == 1) {
                str.append('0');
            }
            str.append(m);
            return str;
        }

        public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
            int month = getNumber(source, pos, 2) - 1;
            cal.set(MONTH, month);
        }

        public String toString() {
            return "MM";
        }
    };

    static {
        dateSymbols.add(new MM());
    }

    /**
     * Format pattern: day
     */
    public static final class DD extends DATE {

        public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
            int day = cal.get(DAY_OF_MONTH);
            String d = Integer.toString(day);
            if (d.length() == 1) {
                str.append('0');
            }
            str.append(d);
            return str;
        }

        public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
            int day = getNumber(source, pos, 2);
            cal.set(DAY_OF_MONTH, day);
        }

        public String toString() {
            return "DD";
        }
    };

    static {
        dateSymbols.add(new DD());
    }

    /**
     * Format pattern: 24-hour
     */
    public static final class HH24 extends TIME {
        public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
            int hour = cal.get(HOUR_OF_DAY);
            String h = Integer.toString(hour);
            if (h.length() == 1) {
                str.append('0');
            }
            str.append(h);
            return str;
        }

        public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
            int hour = getNumber(source, pos, 2);
            cal.set(HOUR_OF_DAY, hour);
        }

        public String toString() {
            return "HH24";
        }
    };

    static {
        timeSymbols.add(new HH24());
    }

    /**
     * Format pattern: 12-hour
     */
    public static final class HH12 extends TIME {
        public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
            int hour = cal.get(HOUR);
            String h = Integer.toString(hour);
            if (h.length() == 1) {
                str.append('0');
            }
            str.append(h);
            return str;
        }

        public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
            int hour = getNumber(source, pos, 2);
            if (hour > 0 && hour < 13) {
                cal.set(HOUR, hour);
            } else {
                throw new java.text.ParseException(BAD_HH12, 0);
            }
        }

        public String toString() {
            return "HH12";
        }
    };

    static {
        timeSymbols.add(new HH12());
    }

    /**
     * Format pattern: AM of 12-hour clock
     */
    public static final class AM extends TIME {
        public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
            String[] ampm = symb.getAmPmStrings();
            int am = cal.get(AM_PM);
            str.append(ampm[am]);
            return str;
        }

        public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
            skipSpaces(source, pos);
            String[] ampm = symb.getAmPmStrings();
            int idx = pos.getIndex();
            for (int i = 0; i < ampm.length; i++) {
                int len = ampm[i].length();
                if (source.regionMatches(true, idx, ampm[i], 0, len)) {
                    pos.setIndex(idx + len);
                    cal.set(AM_PM, i);
                    return;
                }
            }
            throw new java.text.ParseException("", 0);
        }

        public String toString() {
            return "AM";
        }
    };

    static {
        timeSymbols.add(new AM());
    }

    /**
     * Format pattern: PM of 12-hour clock
     */
    public static final class PM extends TIME {
        public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
            String[] ampm = symb.getAmPmStrings();
            int am = cal.get(AM_PM);
            str.append(ampm[am]);
            return str;
        }

        public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
            skipSpaces(source, pos);
            String[] ampm = symb.getAmPmStrings();
            int idx = pos.getIndex();
            for (int i = 0; i < ampm.length; i++) {
                int len = ampm[i].length();
                if (source.regionMatches(true, idx, ampm[i], 0, len)) {
                    pos.setIndex(idx + len);
                    cal.set(AM_PM, i);
                    return;
                }
            }
            throw new java.text.ParseException("", 0);
        }

        public String toString() {
            return "PM";
        }
    };

    static {
        timeSymbols.add(new PM());
    }

    /**
     * Format pattern: minute
     */
    public static final class MI extends TIME {
        public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
            int minute = cal.get(MINUTE);
            String m = Integer.toString(minute);
            if (m.length() == 1) {
                str.append('0');
            }
            str.append(m);
            return str;
        }

        public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
            int minute = getNumber(source, pos, 2);
            cal.set(MINUTE, minute);
        }

        public String toString() {
            return "MI";
        }
    };

    static {
        timeSymbols.add(new MI());
    }

    /**
     * IBM date format: seconds
     */
    public static final class SS extends TIME {
        public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
            int second = cal.get(SECOND);
            String s = Integer.toString(second);
            if (s.length() == 1) {
                str.append('0');
            }
            str.append(s);
            return str;
        }

        public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
            int second = getNumber(source, pos, 2);
            cal.set(SECOND, second);
        }

        public String toString() {
            return "SS";
        }
    };

    static {
        timeSymbols.add(new SS());
    }

    /**
     * IBM date format: milliseconds
     */
    public static final class NNN extends TIME {

        public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException {
            int mSecs = cal.get(MILLISECOND);
            str.append(mSecs);
            return str;
        }

        public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException {
            int nnn = getNumber(source, pos, 3);
            cal.set(MILLISECOND, nnn);
        }

        public String toString() {
            return "NNN";
        }
    };

    static {
        timeSymbols.add(new NNN());
    }

    public static abstract class DATE {

        abstract public StringBuffer toString(StringBuffer str, Calendar cal, DateFormatSymbols symb) throws java.text.ParseException;

        abstract public void parse(Calendar cal, DateFormatSymbols symb, String source, ParsePosition pos) throws java.text.ParseException;

        public boolean equals(Comparable<?> obj) {

            if (obj == null) {
                return false;
            }

            return (obj.getClass() == this.getClass());
        }

        public int getNumber(String source, ParsePosition pos, int digits) throws java.text.ParseException, NumberFormatException {

            final int len = source.length();
            String d = "";
            int i;

            for (i = pos.getIndex(); i < len; i++) {
                final char c = source.charAt(i);
                if (Character.isDigit(c)) {
                    break;
                } else if (Character.isLetter(c)) {
                    pos.setIndex(i);
                    throw new java.text.ParseException("", i);
                }
            }

            for (; i < len; i++) {
                final char c = source.charAt(i);
                if (Character.isDigit(c)) {
                    d += c;
                    if (d.length() == digits) {
                        pos.setIndex(i + 1);
                        return Integer.valueOf(d);
                    }
                } else if (Character.isLetter(c)) {
                    pos.setIndex(i);
                    throw new java.text.ParseException("", i);
                } else {
                    break;
                }
            }

            pos.setIndex(i);

            return Integer.valueOf(d);
        }
    };

    public static abstract class TIME extends DATE {

        protected void skipSpaces(String source, ParsePosition pos) {

            int i = pos.getIndex();

            while (i < source.length() && " ".equals(source.substring(i, i + 1))) {
                i++;
            }

            pos.setIndex(i);
        }
    };
}
