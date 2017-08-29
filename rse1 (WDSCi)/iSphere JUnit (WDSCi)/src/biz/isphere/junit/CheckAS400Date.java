/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.junit;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;

import biz.isphere.journalexplorer.rse.shared.as400fields.AS400Date;
import biz.isphere.journalexplorer.rse.shared.as400fields.AS400DateFormat;
import biz.isphere.journalexplorer.rse.shared.as400fields.AS400Time;
import biz.isphere.journalexplorer.rse.shared.as400fields.AS400TimeFormat;

/**
 * <b>JUnit 4 Test Case</b>
 * <p>
 * Checks class AS400Date.
 */
public class CheckAS400Date {

    StringBuilder buffer = new StringBuilder();
    Calendar calendar = new GregorianCalendar();

    @Test
    public void checkMDYFormat() throws Exception {
        check2DigitDateFormat("MM/dd/yy", "MDY", '/');
    }

    @Test
    public void checkDMYFormat() throws Exception {
        check2DigitDateFormat("dd/MM/yy", "MDY", '/');
    }

    @Test
    public void checkYMDFormat() throws Exception {
        check2DigitDateFormat("yy/MM/dd", "YMD", '/');
    }

    @Test
    public void checkCYMDFormat() throws Exception {
        check3DigitDateFormat("CYMD");
    }

    @Test
    public void checkCMDYFormat() throws Exception {
        check3DigitDateFormat("CMDY");
    }

    @Test
    public void checkCDMYFormat() throws Exception {
        check3DigitDateFormat("CDMY");
    }

    @Test
    public void checkISOFormat() throws Exception {
        check4DigitDateFormat("yyyy-MM-dd", "ISO", '-');
    }

    @Test
    public void checkUSAFormat() throws Exception {
        check4DigitDateFormat("MM/dd/yyyy", "USA", '/');
    }

    @Test
    public void checkJISFormat() throws Exception {
        check4DigitDateFormat("yyyy-MM-dd", "JIS", '-');
    }

    @Test
    public void checkEURFormat() throws Exception {
        check4DigitDateFormat("dd.MM.yyyy", "EUR", '.');
    }

    @Test
    public void checkJULFormat() throws Exception {
        // check2DigitDateFormat("JUL");
        check2DigitDateFormat("yy/DDD", "JUL", '/');
    }

    @Test
    public void checkLONGJULFormat() throws Exception {
        check4DigitDateFormat("yyyy/DDD", "LONGJUL", '/');
    }

    @Test
    public void checkDateBufferLengths() {
        
        checkDateBufferLength(AS400DateFormat.MDY, 6, 8);
        checkDateBufferLength(AS400DateFormat.DMY, 6, 8);
        checkDateBufferLength(AS400DateFormat.YMD, 6, 8);
        
        checkDateBufferLength(AS400DateFormat.JUL, 5, 6);
        
        checkDateBufferLength(AS400DateFormat.ISO, 8, 10);
        checkDateBufferLength(AS400DateFormat.USA, 8, 10);
        checkDateBufferLength(AS400DateFormat.EUR, 8, 10);
        checkDateBufferLength(AS400DateFormat.JIS, 8, 10);
        
        checkDateBufferLength(AS400DateFormat.CYMD, 7, 9);
        checkDateBufferLength(AS400DateFormat.CMDY, 7, 9);
        checkDateBufferLength(AS400DateFormat.CDMY, 7, 9);
        
        checkDateBufferLength(AS400DateFormat.LONGJUL, 7, 8);
    }

    private void checkDateBufferLength(AS400DateFormat dateFormat, int withoutSeparator, int withSeparator) {

        int actualLength;

        actualLength = AS400Date.getByteLength(dateFormat.format());
        assertEquals("Expected: " + withoutSeparator + ", actual: " + actualLength, actualLength, withoutSeparator);

        actualLength = AS400Date.getByteLength(dateFormat.format(), dateFormat.separator());
        assertEquals("Expected: " + withoutSeparator + ", actual: " + actualLength, actualLength, withSeparator);

    }

    private void check2DigitDateFormat(String pattern, String rpgLabel, Character delimiter) {

        SimpleDateFormat formatter = new SimpleDateFormat(pattern);

        Date[] dates = build2DigitDates();
        for (Date date : dates) {
            String dateToParse = formatter.format(date);
            checkDate(dateToParse, rpgLabel, delimiter);
        }
    }

    private void check3DigitDateFormat(String format) {

        for (int century = 0; century <= 9; century++) {
            log("Testing " + format + ": c=" + century);
            for (int year = 0; year <= 99; year++) {
                for (int month = 1; month <= 12; month++) {
                    for (int day = 1; day <= 28; day++) {
                        String dateToParse = produceDateToParse(format, century, year, month, day);
                        checkDate(dateToParse, format, null);
                    }
                }

                if (century == 9 && year == 28) {
                    break;
                }
            }
        }
    }

    private void check4DigitDateFormat(String pattern, String rpgLabel, Character delimiter) {

        SimpleDateFormat formatter = new SimpleDateFormat(pattern);

        Date[] dates = build4DigitDates();
        for (Date date : dates) {
            String dateToParse = formatter.format(date);
            checkDate(dateToParse, rpgLabel, delimiter);
        }
    }

    private void checkDate(String dateToParse, String format, Character delimiter) {

        int dateFormat = AS400Date.toFormat(format);
        AS400Date as400Date = new AS400Date(Calendar.getInstance().getTimeZone(), dateFormat, delimiter);
        Date date = as400Date.parse(dateToParse);
        Date expected = getDateExpected(format, dateToParse, delimiter);
        assertEquals("Given: " + dateToParse + "(" + format + "), parsed: " + date.toString() + ", expected: " + expected.toString(), date.getTime(),
            expected.getTime());
    }

    private String produceDateToParse(String format, int century, int year, int month, int day) {

        buffer.replace(0, buffer.length(), "");

        for (int i = 0; i < format.length(); i++) {
            char c = format.charAt(i);
            switch (c) {
            case 'C':
                buffer.append(String.format("%1d", century));
                break;
            case 'Y':
                buffer.append(String.format("%02d", year));
                break;
            case 'M':
                buffer.append(String.format("%02d", month));
                break;
            case 'D':
                buffer.append(String.format("%02d", day));
                break;
            default:
                break;
            }
        }

        return buffer.toString();
    }

    private Date[] build2DigitDates() {

        List<Date> dates = new ArrayList<Date>();
        for (int year = 1940; year <= 2039; year++) {
            for (int month = 1; month <= 12; month++) {
                for (int day = 1; day <= 28; day++) {
                    calendar.set(year, month - 1, day, 0, 0, 0);
                    dates.add(calendar.getTime());
                }
            }
        }

        return dates.toArray(new Date[dates.size()]);
    }

    private Date[] build4DigitDates() {

        List<Date> dates = new ArrayList<Date>();
        for (int year = 1; year <= 9999; year++) {
            if ((year <= 1000 || year >= 9000) || (year >= 1500 && year <= 1599) || (year >= 1000 && year <= 3000)) {
                for (int month = 1; month <= 12; month++) {
                    for (int day = 1; day <= 28; day++) {
                        if (day <= 3 || day >= 26) {
                            calendar.set(year, month - 1, day, 0, 0, 0);
                            dates.add(calendar.getTime());
                        }
                    }
                }
            }
        }

        return dates.toArray(new Date[dates.size()]);
    }

    private Date getDateExpected(String format, String dateString, Character delimiter) {

        int dateFormat = com.ibm.as400.access.AS400Date.toFormat(format);
        com.ibm.as400.access.AS400Date as400Date = new com.ibm.as400.access.AS400Date(Calendar.getInstance().getTimeZone(), dateFormat, delimiter);
        Date date = as400Date.parse(dateString);

        return date;
    }

    private void log(String text) {
        System.out.println(text);
    }
}
