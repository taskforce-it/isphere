/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.junit.functions;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;

import junit.framework.Assert;

import org.junit.Test;
import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.exceptions.CompareException;
import org.medfoster.sqljep.exceptions.WrongNumberOfParametersException;
import org.medfoster.sqljep.exceptions.WrongTypeException;
import org.medfoster.sqljep.junit.AbstractJUnitTestCase;

public class TestFunctions extends AbstractJUnitTestCase {

    private static final String COMPARE_EXCEPTION = "Cannot compare '%s' with '%s'.";
    private static final String WRONG_NUMBER_OF_PARAMETERS_EXCEPTION = "Wrong number of parameters: %d";
    private static final String WRONG_PARAMETER_TYPES_EXCEPTION = "Wrong parameter types:  %s(%s, %s)";

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

        try {
            assertEquals(true, parseExpression("TIME('15.10.30') + HOUR(5) = 2"));
        } catch (CompareException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(COMPARE_EXCEPTION, "Time", "Long"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testSubstract() throws ParseException {

        assertEquals(true, parseExpression("2.41 - 10 = -7.59"));

        // Test leap year
        assertEquals(true, parseExpression("DATE('2016-03-03') - DAY(5) = '2016-02-27'"));
        assertEquals(true, parseExpression("DATE('2016-03-03') - MONTH(3) = '2015-12-03'"));
        assertEquals(true, parseExpression("DATE('2016-02-29') - YEAR(1) = '2015-02-28'"));

        // Test time
        assertEquals(true, parseExpression("TIME('15.10.30') - HOUR(5) = '10:10:30'"));

        try {
            assertEquals(true, parseExpression("TIME('15.10.30') + HOUR(5) = 2"));
        } catch (CompareException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(COMPARE_EXCEPTION, "Time", "Long"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testMultiply() throws ParseException {

        assertEquals(true, parseExpression("3.25 * 8 = 26"));
        assertEquals(true, parseExpression("8 * 3.25 = 26"));

        try {
            assertEquals(true, parseExpression("3.25 * DATE('2018-12-21') = 26"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_PARAMETER_TYPES_EXCEPTION, "Multiply", "BigDecimal", "Date"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testDivide() throws ParseException {

        assertEquals(true, parseExpression("26 / 8 = 3.25"));
        assertEquals(true, parseExpression("26 / 3.25 = 8"));

        try {
            assertEquals(true, parseExpression("26 / DATE('2018-12-21') = 3.25"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_PARAMETER_TYPES_EXCEPTION, "Divide", "Long", "Date"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testBetween() throws ParseException {

        assertEquals(true, parseExpression("5 BETWEEN 1 AND 10"));
    }

    @Test
    public void testNotBetween() throws ParseException {

        assertEquals(true, parseExpression("15 NOT BETWEEN 1 AND 10"));
    }

    @Test
    public void testCeil() throws ParseException {

        assertEquals(true, parseExpression("CEIL(10.5) = 11"));
        assertEquals(true, parseExpression("ceil('10.4') = 11"));
        assertEquals(true, parseExpression("ceil(10) = 10"));
        assertEquals(true, parseExpression("Ceil(1.234e2) = 124"));

        assertEquals(false, parseExpression("CEIL(10.5) = 10"));
    }

    @Test
    public void testFloor() throws ParseException {

        assertEquals(true, parseExpression("FLOOR(10.5) = 10"));
        assertEquals(true, parseExpression("floor('10.4') = 10"));
        assertEquals(true, parseExpression("floor(10) = 10"));
        assertEquals(true, parseExpression("Floor(1.234e2) = 123"));

        assertEquals(false, parseExpression("FLOOR(10.5) = 11"));
    }

    @Test
    public void testYear() throws ParseException {

        assertEquals(true, parseExpression("year(JODATE) = 2018", getColumnMapping()));
        assertEquals(true, parseExpression("year(JODATE) = '2018'", getColumnMapping()));
    }

    @Test
    public void testMonth() throws ParseException {

        assertEquals(true, parseExpression("DATE('2018-12-05') + Month(1) = '2019-01-05'"));
        assertEquals(true, parseExpression("DATE('2018-12-05') - Month(1) = '2018-11-05'"));

        assertEquals(true, parseExpression("DATE('2018-12-05') + Month('1') = '2019-01-05'"));
        assertEquals(true, parseExpression("DATE('2018-12-05') - Month('1') = '2018-11-05'"));
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
    public void testSecond() throws ParseException {

        assertEquals(true, parseExpression("SECOND(TIME('12:00:25')) = 25"));
        assertEquals(true, parseExpression("SECOND(TIMESTAMP('2018-12-05-15.00.45.123')) = 45"));

        assertEquals(true, parseExpression("SECOND(TIME('12:00:25')) = '25'"));
        assertEquals(true, parseExpression("SECOND(TIMESTAMP('2018-12-05-15.00.45.123')) = '45'"));
    }

    @Test
    public void testMicrosecond() throws ParseException {

        assertEquals(true, parseExpression("MICROSECOND(TIME('12:00:25')) = 0"));
        assertEquals(true, parseExpression("MICROSECOND(TIMESTAMP('2018-12-05-15.00.55.123')) = 123"));

        assertEquals(true, parseExpression("MICROSECOND(TIME('12:00:25')) = '0'"));
        assertEquals(true, parseExpression("MICROSECOND(TIMESTAMP('2018-12-05-15.00.55.123')) = '123'"));
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
    public void testSign() throws ParseException {

        assertEquals(true, parseExpression("SIGN(-10) = -1"));
        assertEquals(true, parseExpression("SIGN(0) = 0"));
        assertEquals(true, parseExpression("SIGN(10) = 1"));

        assertEquals(true, parseExpression("SIGN('-10') = -1"));
        assertEquals(true, parseExpression("SIGN('0') = 0"));
        assertEquals(true, parseExpression("SIGN('10') = 1"));
    }

    @Test
    public void testTrim() throws ParseException {

        assertEquals(true, parseExpression("Trim('   It works!   ') = 'It works!'"));
        assertEquals(true, parseExpression("Trim('***It works!###', '*') = 'It works!###'"));
        assertEquals(true, parseExpression("Trim('***It works!###', '#') = '***It works!'"));
        assertEquals(true, parseExpression("Trim('123It works!789', '0123456789') = 'It works!'"));

        try {
            assertEquals(true, parseExpression("Trim('***It works!###', '*', '#') = 'It works!'"));
        } catch (WrongNumberOfParametersException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_NUMBER_OF_PARAMETERS_EXCEPTION, 3), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testLTrim() throws ParseException {

        assertEquals(true, parseExpression("LTrim('   It works!   ') = 'It works!   '"));
        assertEquals(true, parseExpression("LTrim('***It works!###', '*') = 'It works!###'"));
        assertEquals(true, parseExpression("LTrim('123It works!789', '0123456789') = 'It works!789'"));

        try {
            assertEquals(true, parseExpression("LTrim('***It works!###', '*', '#') = 'It works!'"));
        } catch (WrongNumberOfParametersException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_NUMBER_OF_PARAMETERS_EXCEPTION, 3), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testRTrim() throws ParseException {

        assertEquals(true, parseExpression("RTrim('   It works!   ') = '   It works!'"));
        assertEquals(true, parseExpression("RTrim('***It works!###', '#') = '***It works!'"));
        assertEquals(true, parseExpression("RTrim('123It works!789', '0123456789') = '123It works!'"));

        try {
            assertEquals(true, parseExpression("RTrim('***It works!###', '*', '#') = 'It works!'"));
        } catch (WrongNumberOfParametersException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_NUMBER_OF_PARAMETERS_EXCEPTION, 3), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }
}
