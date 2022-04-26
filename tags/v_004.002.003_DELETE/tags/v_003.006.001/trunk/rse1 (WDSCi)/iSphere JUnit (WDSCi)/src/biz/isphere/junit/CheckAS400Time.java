/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.junit;

import static org.junit.Assert.assertEquals;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;

import biz.isphere.journalexplorer.rse.shared.as400fields.AS400Time;
import biz.isphere.journalexplorer.rse.shared.as400fields.AS400TimeFormat;

/**
 * <b>JUnit 4 Test Case</b>
 * <p>
 * Checks class AS400Date.
 */
public class CheckAS400Time {
    
    StringBuilder buffer = new StringBuilder();
    Calendar calendar = new GregorianCalendar();

    @Test
    public void checkHMSFormat() throws Exception {
        checkTimeFormat("HH:mm:ss", "HMS", ':');
        checkTimeFormat("HH.mm.ss", "HMS", '.');
        checkTimeFormat("HH,mm,ss", "HMS", ',');
        checkTimeFormat("HH&mm&ss", "HMS", '&');
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkHMSFormatIllegalSeparator() throws Exception {
        checkTimeFormat("HH:mm:ss", "HMS", '-');
    }

    @Test
    public void checkISOFormat() throws Exception {
        checkTimeFormat("HH.mm.ss", "ISO", '.');
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkISOFormatIllegalSeparator() throws Exception {
        checkTimeFormat("HH.mm.ss", "ISO", '-');
    }

    @Test
    public void checkUSAFormat() throws Exception {
        checkTimeFormat("hh:mm a", "USA", ':');
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkUSAFormatIllegalSeparator() throws Exception {
        checkTimeFormat("hh:mm a", "USA", '-');
    }

    @Test
    public void checkEURFormat() throws Exception {
        checkTimeFormat("HH.mm.ss", "EUR", '.');
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEURFormatIllegalSeparator() throws Exception {
        checkTimeFormat("HH.mm.ss", "EUR", '-');
    }

    @Test
    public void checkJISFormat() throws Exception {
        checkTimeFormat("HH:mm:ss", "JIS", ':');
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkJISFormatIllegalSeparator() throws Exception {
        checkTimeFormat("HH:mm:ss", "JIS", '-');
    }

    @Test
    public void checkTimeBufferLengths() {
        
        checkTimeBufferLength(AS400TimeFormat.ISO, 6, 8);
        checkTimeBufferLength(AS400TimeFormat.USA, 6, 8);
        checkTimeBufferLength(AS400TimeFormat.EUR, 6, 8);
        checkTimeBufferLength(AS400TimeFormat.JIS, 6, 8);
        checkTimeBufferLength(AS400TimeFormat.HMS, 6, 8);
    }

    private void checkTimeBufferLength(AS400TimeFormat timeFormat, int withoutSeparator, int withSeparator) {

        int actualLength;

        actualLength = AS400Time.getByteLength(timeFormat.format());
        assertEquals("Expected: " + withoutSeparator + ", actual: " + actualLength, actualLength, withoutSeparator);

        actualLength = AS400Time.getByteLength(timeFormat.format(), timeFormat.separator());
        assertEquals("Expected: " + withoutSeparator + ", actual: " + actualLength, actualLength, withSeparator);

    }

    private void checkTimeFormat(String pattern, String rpgLabel, char delimiter) {

        SimpleDateFormat formatter = new SimpleDateFormat(pattern);

        Date[] dates = buildTimes();
        for (Date date : dates) {
            String dateToParse = formatter.format(date);
            checkTime(dateToParse, rpgLabel, delimiter);
        }

    }

    private void checkTime(String timeToParse, String format, char delimiter) {

        int dateFormat = AS400Time.toFormat(format);
        AS400Time as400Time = new AS400Time(Calendar.getInstance().getTimeZone(), dateFormat, delimiter);
        Date date = as400Time.parse(timeToParse);
        Date expected = getTimeExpected(format, timeToParse, delimiter);
        assertEquals("Given: " + timeToParse + "(" + format + "), parsed: " + date.toString() + ", expected: " + expected.toString(), date.getTime(),
            expected.getTime());
    }

    private Time[] buildTimes() {

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        List<Time> times = new ArrayList<Time>();
        for (int hour = 0; hour <= 24; hour++) {
            for (int minute = 0; minute <= 59; minute++) {
                for (int second = 0; second <= 59; second++) {
                    calendar.set(year, month, day, hour, minute, second);
                    times.add(new Time(calendar.getTimeInMillis()));
                }
            }
        }

        return times.toArray(new Time[times.size()]);
    }

    private Date getTimeExpected(String format, String dateString, Character delimiter) {

        int timeFormat = com.ibm.as400.access.AS400Time.toFormat(format);
        com.ibm.as400.access.AS400Time as400Time = new com.ibm.as400.access.AS400Time(Calendar.getInstance().getTimeZone(), timeFormat, delimiter);
        Date date = as400Time.parse(dateString);

        return date;
    }

    private void log(String text) {
        System.out.println(text);
    }
}
