/*******************************************************************************
 * Copyright (c) project_year-2019 project_team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.junit;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import biz.isphere.base.internal.IBMiHelper;

public class TestIBMiHelper {

    private static final SimpleDateFormat isoDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void testDateToCyymmdd() {

        String dateValue;
        Date date;
        Calendar calendar = Calendar.getInstance();

        dateValue = IBMiHelper.dateToYMD(getDate(1899), null);
        assertTrue(dateValue == null);

        dateValue = IBMiHelper.dateToYMD(getDate(2900), null);
        assertTrue(dateValue == null);

        for (int i = 1940; i <= 2039; i++) {
            dateValue = IBMiHelper.dateToCyymmdd(getDate(i), null);
            date = IBMiHelper.cyymmddToDate(dateValue);
            calendar.setTime(date);
            assertTrue(calendar.get(Calendar.YEAR) == i);
            assertTrue(calendar.get(Calendar.MONTH) == Calendar.JUNE);
            assertTrue(calendar.get(Calendar.DAY_OF_MONTH) == 30);
        }
    }

    @Test
    public void testCyymmddToDate() {

        String cymd;
        Date date;
        Calendar calendar = Calendar.getInstance();

        for (int i = 1900; i <= 2800; i++) {
            calendar.setTime(getDate(i));

            cymd = getCentury(i) + get2DigitString(Integer.toString(i - ((int)(i / 100) * 100))) + "0630";
            date = IBMiHelper.cyymmddToDate(cymd);

            assertTrue("Testing year: " + i, calendar.getTimeInMillis() == date.getTime());
        }
    }

    @Test
    public void testDateToYMD() {

        String dateValue;
        Date date;
        Calendar calendar = Calendar.getInstance();

        dateValue = IBMiHelper.dateToYMD(getIllegal2DigitLowDate(), null);
        assertTrue(dateValue == null);

        dateValue = IBMiHelper.dateToYMD(getIllegal2DigitHighDate(), null);
        assertTrue(dateValue == null);

        for (int i = 1940; i <= 2039; i++) {
            dateValue = IBMiHelper.dateToYMD(getDate(i), null);
            date = IBMiHelper.ymdToDate(dateValue);
            calendar.setTime(date);
            assertTrue(calendar.get(Calendar.YEAR) == i);
            assertTrue(calendar.get(Calendar.MONTH) == Calendar.JUNE);
            assertTrue(calendar.get(Calendar.DAY_OF_MONTH) == 30);
        }
    }

    @Test
    public void testYmdToDate() {

        String ymd;
        Date date;
        Calendar calendar = Calendar.getInstance();

        for (int i = 1940; i <= 2039; i++) {
            calendar.setTime(getDate(i));

            ymd = get2DigitString(Integer.toString(i - ((int)(i / 100) * 100))) + "0630";
            date = IBMiHelper.ymdToDate(ymd);

            assertTrue("Testing year: " + i, calendar.getTimeInMillis() == date.getTime());
        }
    }

    @Test
    public void testDateToDMY() {

        String dateValue;
        Date date;
        Calendar calendar = Calendar.getInstance();

        dateValue = IBMiHelper.dateToDMY(getIllegal2DigitLowDate(), null);
        assertTrue(dateValue == null);

        dateValue = IBMiHelper.dateToDMY(getIllegal2DigitHighDate(), null);
        assertTrue(dateValue == null);

        for (int i = 1940; i <= 2039; i++) {
            dateValue = IBMiHelper.dateToDMY(getDate(i), null);
            date = IBMiHelper.dmyToDate(dateValue);
            calendar.setTime(date);
            assertTrue(calendar.get(Calendar.YEAR) == i);
            assertTrue(calendar.get(Calendar.MONTH) == Calendar.JUNE);
            assertTrue(calendar.get(Calendar.DAY_OF_MONTH) == 30);
        }
    }

    @Test
    public void testDmyToDate() {

        String dmy;
        Date date;
        Calendar calendar = Calendar.getInstance();

        for (int i = 1940; i <= 2039; i++) {
            calendar.setTime(getDate(i));

            dmy = "3006" + get2DigitString(Integer.toString(i - ((int)(i / 100) * 100)));
            date = IBMiHelper.dmyToDate(dmy);

            assertTrue("Testing year: " + i, calendar.getTimeInMillis() == date.getTime());
        }
    }

    @Test
    public void testDateToMdy() {

        String dateValue;
        Date date;
        Calendar calendar = Calendar.getInstance();

        dateValue = IBMiHelper.dateToMDY(getIllegal2DigitLowDate(), null);
        assertTrue(dateValue == null);

        dateValue = IBMiHelper.dateToMDY(getIllegal2DigitHighDate(), null);
        assertTrue(dateValue == null);

        for (int i = 1940; i <= 2039; i++) {
            dateValue = IBMiHelper.dateToMDY(getDate(i), null);
            date = IBMiHelper.mdyToDate(dateValue);
            calendar.setTime(date);
            assertTrue(calendar.get(Calendar.YEAR) == i);
            assertTrue(calendar.get(Calendar.MONTH) == Calendar.JUNE);
            assertTrue(calendar.get(Calendar.DAY_OF_MONTH) == 30);
        }
    }

    @Test
    public void testMdyToDate() {

        String mdy;
        Date date;
        Calendar calendar = Calendar.getInstance();

        for (int i = 1940; i <= 2039; i++) {
            calendar.setTime(getDate(i));

            mdy = "0630" + get2DigitString(Integer.toString(i - ((int)(i / 100) * 100)));
            date = IBMiHelper.mdyToDate(mdy);

            assertTrue("Testing year: " + i, calendar.getTimeInMillis() == date.getTime());
        }
    }

    private String getCentury(int year) {

        if (year >= 2800) {
            return "9";
        } else if (year >= 2700) {
            return "8";
        } else if (year >= 2600) {
            return "7";
        } else if (year >= 2500) {
            return "6";
        } else if (year >= 2400) {
            return "5";
        } else if (year >= 2300) {
            return "4";
        } else if (year >= 2200) {
            return "3";
        } else if (year >= 2100) {
            return "2";
        } else if (year >= 2000) {
            return "1";
        } else if (year >= 1900) {
            return "0";
        }

        return null;
    }

    private String get2DigitString(String value) {

        if (value.length() == 2) {
            return value;
        }

        StringBuilder buffer = new StringBuilder(value);
        while (buffer.length() < 2) {
            buffer.insert(0, "0");
        }

        return buffer.toString();
    }

    private Date getDate(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.JUNE);
        calendar.set(Calendar.DAY_OF_MONTH, 30);
        return calendar.getTime();
    }

    private Date getIllegal2DigitLowDate() {
        return getDate("1939-12-31");
    }

    private Date getIllegal2DigitHighDate() {
        return getDate("2040-01-01");
    }

    private Date getDate(String date) {

        try {
            return isoDateFormatter.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
