/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class IBMiHelper {

    private static final SimpleDateFormat cyymmddFormatter = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat hhmmssFormatter = new SimpleDateFormat("HHmmss");
    private static final SimpleDateFormat yymmddFormatter = new SimpleDateFormat("yyMMdd");

    public static String quote(String text) {

        StringBuilder quotedText = new StringBuilder();

        quotedText.append("'");
        quotedText.append(text.replaceAll("'", "''"));
        quotedText.append("'");

        return quotedText.toString();
    }

    public static String dateToCyymmdd(Date date, String defaultValue) {

        String cyymmdd;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            if (calendar.get(Calendar.YEAR) >= 2000) {
                cyymmdd = "1" + yymmddFormatter.format(calendar.getTime());
            } else {
                cyymmdd = "0" + yymmddFormatter.format(calendar.getTime());
            }
        } catch (Exception e) {
            return defaultValue;
        }

        return cyymmdd;
    }

    public static Date cyymmddToDate(String cyymmdd) {

        if ("1".equals(cyymmdd.substring(0, 1))) {
            cyymmdd = "20" + cyymmdd.substring(1);
        } else {
            cyymmdd = "19" + cyymmdd.substring(1);
        }

        Date date;
        try {
            date = cyymmddFormatter.parse(cyymmdd);
        } catch (ParseException e) {
            return null;
        }

        return date;
    }

    public static String timeToHhmmss(Date time, String defaultValue) {

        String hhmmss;
        try {
            hhmmss = hhmmssFormatter.format(time);
        } catch (Exception e) {
            return defaultValue;
        }

        return hhmmss;
    }

    public static Date hhmmssToTime(String hhmmss) {

        Date time;
        try {
            time = hhmmssFormatter.parse(hhmmss);
        } catch (ParseException e) {
            return null;
        }

        return time;
    }

}
