/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.rse.shared.as400fields;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;

public enum AS400TimeFormat {
    HMS (100, "HMS", ":.,&", "HH|mm|ss"),
    ISO (101, "ISO", ".", "HH|mm|ss"),
    USA (102, "USA", ":", "hh|mm a"),
    EUR (103, "EUR", ".", "HH|mm|ss"),
    JIS (104, "JIS", ":", "HH|mm|ss");

    public static final int FORMAT_HMS = HMS.format;
    public static final int FORMAT_ISO = ISO.format;
    public static final int FORMAT_USA = USA.format;
    public static final int FORMAT_EUR = EUR.format;
    public static final int FORMAT_JIS = JIS.format;

    public static final String LITERAL_HMS = HMS.rpgLiteral;
    public static final String LITERAL_ISO = ISO.rpgLiteral;
    public static final String LITERAL_USA = USA.rpgLiteral;
    public static final String LITERAL_EUR = EUR.rpgLiteral;
    public static final String LITERAL_JIS = JIS.rpgLiteral;

    private int format;
    private String rpgLiteral;
    private char baseSeparator;
    private char[] validSeparators;
    private String basePattern;

    private AS400TimeFormat(int format, String label, String separator, String basePattern) {
        this.format = format;
        this.rpgLiteral = label;
        this.baseSeparator = separator.charAt(0);
        this.validSeparators = separator.toCharArray();
        this.basePattern = basePattern;

        Arrays.sort(this.validSeparators);
    }

    public int format() {
        return format;
    }

    public String rpgLiteral() {
        return rpgLiteral;
    }

    public Character separator() {
        return baseSeparator;
    }

    public boolean isValidSeparator(Character separator) {
        return Arrays.binarySearch(validSeparators, separator) >= 0;
    }

    public SimpleDateFormat getFormatter(TimeZone timeZone, Character separator) {

        SimpleDateFormat formatter;

        if (separator != null) {
            formatter = new SimpleDateFormat(basePattern.replaceAll("\\|", separator.toString()));
        } else {
            formatter = new SimpleDateFormat(basePattern.replaceAll("\\|", ""));
        }

        formatter.setTimeZone(timeZone);

        return formatter;
    }
}
