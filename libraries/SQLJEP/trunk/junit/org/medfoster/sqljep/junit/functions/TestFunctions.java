/*******************************************************************************
 * Copyright (c) project_year-2018 project_team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.junit.functions;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;

import org.junit.Test;
import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.junit.AbstractJUnitTestCase;

public class TestFunctions extends AbstractJUnitTestCase {

    @Test
    public void testAbs() throws ParseException {

        assertEquals(true, parseExpression("abs(7.59) = 7.59"));
        assertEquals(true, parseExpression("abs(-7.59) = 7.59"));
    }

    @Test
    public void testAdd() throws ParseException {

        assertEquals(true, parseExpression("7.59 + 2.41 = 10.0"));

        // Test leap year
        assertEquals(true, parseExpression("DATE('2016-02-27') + DAY(5) = '2016-03-03'"));
        assertEquals(true, parseExpression("DATE('2016-02-27') + MONTH(3) = '2016-05-27'"));
        assertEquals(true, parseExpression("DATE('2016-02-29') + YEAR(1) = '2017-02-28'"));

        // Test time
        assertEquals(true, parseExpression("TIME('15.10.30') + HOUR(5) = '20:10:30'"));
    }

    @Test
    public void testBetween() throws ParseException {

        assertEquals(true, parseExpression("5 BETWEEN 1 AND 10"));
        assertEquals(true, parseExpression("15 NOT BETWEEN 1 AND 10"));
    }

    @Test
    public void testCeil() throws ParseException {

        assertEquals(true, parseExpression("CEIL(10.5) = 11"));
        assertEquals(true, parseExpression("ceil('10.4') = 11"));
        assertEquals(true, parseExpression("ceil(10) = 10"));
        
        assertEquals(false, parseExpression("CEIL(10.5) = 10"));
    }

    @Test
    public void testFloor() throws ParseException {

        assertEquals(true, parseExpression("FLOOR(10.5) = 10"));
        assertEquals(true, parseExpression("floor('10.4') = 10"));
        assertEquals(true, parseExpression("floor(10) = 10"));
        
        assertEquals(false, parseExpression("FLOOR(10.5) = 11"));
    }

    @Test
    public void testDay() throws ParseException {

        assertEquals(true, parseExpression("DATE('2018-12-05') + Day(10) = '2018-12-15'"));
        assertEquals(true, parseExpression("DATE('2018-12-05') - Day(10) = '2018-11-25'"));

        assertEquals(true, parseExpression("DATE('2018-12-05') + Day('10') = '2018-12-15'"));
        assertEquals(true, parseExpression("DATE('2018-12-05') - Day('10') = '2018-11-25'"));
    }

    @Test
    public void testHour() throws ParseException {

        assertEquals(true, parseExpression("HOUR(TIME('12:00:00')) = 12"));
        assertEquals(true, parseExpression("HOUR(TIMESTAMP('2018-12-05-15.00.00.123')) = 15"));

        assertEquals(true, parseExpression("HOUR(TIME('12:00:00')) = '12'"));
        assertEquals(true, parseExpression("HOUR(TIMESTAMP('2018-12-05-15.00.00.123')) = '15'"));
    }

    @Test
    public void testMinute() throws ParseException {

        assertEquals(true, parseExpression("MINUTE(TIME('12:25:00')) = 25"));
        assertEquals(true, parseExpression("MINUTE(TIMESTAMP('2018-12-05-15.45.00.123')) = 45"));

        assertEquals(true, parseExpression("MINUTE(TIME('12:25:00')) = '25'"));
        assertEquals(true, parseExpression("MINUTE(TIMESTAMP('2018-12-05-15.45.00.123')) = '45'"));
    }

    @Test
    public void testMonth() throws ParseException {

        assertEquals(true, parseExpression("DATE('2018-12-05') + Month(1) = '2019-01-05'"));
        assertEquals(true, parseExpression("DATE('2018-12-05') - Month(1) = '2018-11-05'"));

        assertEquals(true, parseExpression("DATE('2018-12-05') + Month('1') = '2019-01-05'"));
        assertEquals(true, parseExpression("DATE('2018-12-05') - Month('1') = '2018-11-05'"));
    }

    @Test
    public void testSubstract() throws ParseException {

        assertEquals(true, parseExpression("2.41 - 10 = -7.59"));

        // Test leap year
        assertEquals(true, parseExpression("DATE('2016-03-03') - DAY(5) = '2016-02-27'"));
        assertEquals(true, parseExpression("DATE('2016-03-03') - MONTH(3) = '2015-12-03'"));
        assertEquals(true, parseExpression("DATE('2016-02-29') - YEAR(1) = '2015-02-28'"));
    }

    @Test
    public void testDate() throws ParseException {

        assertEquals(true, parseExpression("DATE('2018-12-05') > '2018-12-04'"));
        assertEquals(true, parseExpression("DATE('2018-12-05') = '2018-12-05'"));
        assertEquals(true, parseExpression("DATE('2018-12-05') < '2018-12-06'"));

        assertEquals(true, parseExpression("DATE('2018-12-05', 'YYYY-MM-DD') > '2018-12-04'"));
        assertEquals(true, parseExpression("DATE('12-05-2018', 'MM/DD/yyyy') = '2018-12-05'"));
        assertEquals(true, parseExpression("DATE('05-12-2018', 'DD.MM.YYYY') < '2018-12-06'"));
    }

    @Test
    public void testMicrosecond() throws ParseException {

        assertEquals(true, parseExpression("MICROSECOND(TIME('12:00:25')) = 0"));
        assertEquals(true, parseExpression("MICROSECOND(TIMESTAMP('2018-12-05-15.00.55.123')) = 123"));

        assertEquals(true, parseExpression("MICROSECOND(TIME('12:00:25')) = '0'"));
        assertEquals(true, parseExpression("MICROSECOND(TIMESTAMP('2018-12-05-15.00.55.123')) = '123'"));
    }

    @Test
    public void testSign() throws ParseException {

        assertEquals(true, parseExpression("SIGN(-10) = -1"));
        assertEquals(true, parseExpression("SIGN(0) = 0"));
        assertEquals(true, parseExpression("SIGN(10) = 1"));

        assertEquals(true, parseExpression("SIGN('-10') = -1"));
        assertEquals(true, parseExpression("SIGN('0') = 0"));
        assertEquals(true, parseExpression("SIGN('10') = 1"));
    }

    @Test
    public void testTime() throws ParseException {

        assertEquals(true, parseExpression("TIME('12:00:00') > '11:59:59'"));
        assertEquals(true, parseExpression("TIME('12:00:00') = '12:00:00'"));
        assertEquals(true, parseExpression("TIME('12:00:00') < '12:00:01'"));

        assertEquals(true, parseExpression("TIME('12.00.00', 'HH24.MI.SS') > '11:59:59'"));
        assertEquals(true, parseExpression("TIME('09:00 am', 'HH12:MI AM') = '09:00:00'"));
        assertEquals(true, parseExpression("TIME('09:00 pm', 'HH12:MI PM') = '21:00:00'"));
        assertEquals(true, parseExpression("TIME('12:00:00', 'HH24:MI:SS') < '12:00:01'"));

        assertEquals(true, parseExpression("TIME('09:00am', 'HH12:MI AM') = '09:00:00'"));
        assertEquals(true, parseExpression("TIME('09:00   pm', 'HH12:MI PM') = '21:00:00'"));
    }

    @Test
    public void testTimestamp() throws ParseException {

        SimpleDateFormat formatter;
        String expected;

        // Test with milliseconds
        formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        expected = formatter.format(getTimestamp(2018, 12, 5, 12, 0, 0, 123));
        assertEquals(true, parseExpression("TIMESTAMP('2018-12-05 12:00:00.123') = '" + expected + "'"));

        // Test without milliseconds
        formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        expected = formatter.format(getTimestamp(2018, 12, 5, 12, 0, 0));
        assertEquals(true, parseExpression("TIMESTAMP('2018-12-05 12:00:00') = '" + expected + "'"));

        // Test with too less milliseconds
        formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SS");
        expected = formatter.format(getTimestamp(2018, 12, 5, 12, 0, 0, 88));
        assertEquals(true, parseExpression("TIMESTAMP('2018-12-05 12:00:00.88') = '" + expected + "'"));

        // Test with too many milliseconds
        formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        expected = formatter.format(getTimestamp(2018, 12, 5, 12, 0, 0, 888));
        assertEquals(true, parseExpression("TIMESTAMP('2018-12-05 12:00:00.8888') = '" + expected + "'"));
    }

    @Test
    public void testYear() throws ParseException {

        assertEquals(true, parseExpression("year(JODATE) = 2018", getColumnMapping()));
        assertEquals(true, parseExpression("year(JODATE) = '2018'", getColumnMapping()));
    }
}
