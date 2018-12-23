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
import org.medfoster.sqljep.exceptions.UnexpectedTypeException;
import org.medfoster.sqljep.exceptions.WrongNumberOfParametersException;
import org.medfoster.sqljep.exceptions.WrongTypeException;
import org.medfoster.sqljep.exceptions.WrongValueException;
import org.medfoster.sqljep.junit.AbstractJUnitTestCase;

public class TestFunctions extends AbstractJUnitTestCase {

    private static final String COMPARE_EXCEPTION = "Cannot compare '%s' with '%s' in function %s.";
    private static final String WRONG_NUMBER_OF_PARAMETERS_EXCEPTION = "Wrong number of parameters %d in function %s.";
    private static final String WRONG_TYPE_EXCEPTION_1 = "Wrong parameter types:  %s(%s)";
    private static final String WRONG_TYPE_EXCEPTION_2 = "Wrong parameter types:  %s(%s, %s)";
    private static final String WRONG_VALUE_EXCEPTION = "Invalid value '%s' of parameter '%s' of function %s.";
    private static final String UNEXPECTED_TYPE_EXCEPTION = "Parameter '%s' of function %s should be an %s.";

    @Test
    public void testAbs() throws ParseException {

        assertEquals(true, parseExpression("abs(7.59) = 7.59"));
        assertEquals(true, parseExpression("abs(-7.59) = 7.59"));
    }

    @Test
    public void testConcat() throws ParseException {

        // String
        assertEquals(true, parseExpression("Concat('Hello', '-World') = 'Hello-World'"));
        assertEquals(true, parseExpression("'a' || 'b' || 'c' = 'abc'"));

        // Numbers
        assertEquals(true, parseExpression("Concat(5.1, '9.8') = '5.19.8'"));
        assertEquals(true, parseExpression("Concat(51, '98') = '5198'"));

        // Date
        assertEquals(true, parseExpression("Concat('Date: ', Date('2018-12-22')) = 'Date: 2018-12-22'"));

        // Time
        assertEquals(true, parseExpression("Concat('Time: ', Time('18.12.22')) = 'Time: 18:12:22'"));

        // Timestamp
        assertEquals(true, parseExpression("Concat('Timestamp: ', Timestamp('2018-12-22-18:12:22.123')) = 'Timestamp: 2018-12-22 18:12:22.123'"));
    }

    @Test
    public void testAdd() throws ParseException {

        assertEquals(true, parseExpression("7.59 + 2.41 = 10.0"));

        // Test leap year
        assertEquals(true, parseExpression("DATE('2016-02-27') + DAY(5) = '2016-03-03'"));
        assertEquals(true, parseExpression("DATE('2016-02-27') + MONTH(3) = '2016-05-27'"));
        assertEquals(true, parseExpression("DATE('2016-02-29') + YEAR(1) = '2017-02-28'"));

        // Test time
        assertEquals(true, parseExpression("TIME('15.10.30') + HOUR(15) = '06:10:30'"));
        assertEquals(true, parseExpression("TIME('15.10.30') + MINUTE(65) = '16:15:30'"));
        assertEquals(true, parseExpression("TIME('15.10.30') + SECOND(65) = '15:11:35'"));

        // Test time
        assertEquals(true, parseExpression("TIMESTAMP('2018-12-21-15.10.30.123') + HOUR(5) = '2018-12-21-20.10.30.123'"));
        assertEquals(true, parseExpression("TIMESTAMP('2018-12-21-15.10.30.123') + MINUTE(65) = '2018-12-21-16.15.30.123'"));
        assertEquals(true, parseExpression("TIMESTAMP('2018-12-21-15.10.30.123') + SECOND(65) = '2018-12-21-15.11.35.123'"));
        assertEquals(true, parseExpression("TIMESTAMP('2018-12-21-15.10.30.123') + MICROSECOND(965) = '2018-12-21-15.10.31.088'"));

        try {
            assertEquals(true, parseExpression("TIME('15.10.30') + MICROSECOND(65) = '15:11:35.65'"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Add", "Time", "Microseconds"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("TIME('15.10.30') + HOUR(5) = 2"));
        } catch (CompareException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(COMPARE_EXCEPTION, "Time", "Long", "ComparativeEQ"), message);
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
            assertEquals(String.format(COMPARE_EXCEPTION, "Time", "Long", "ComparativeEQ"), message);
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
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Multiply", "BigDecimal", "Date"), message);
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
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Divide", "Long", "Date"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testRound1Parameter() throws ParseException {

        assertEquals(true, parseExpression("Round('26') = 26"));
        assertEquals(true, parseExpression("Round('26.4') = 26"));
        assertEquals(true, parseExpression("Round('26.5') = 27"));

        assertEquals(true, parseExpression("Round(26) = 26"));
        assertEquals(true, parseExpression("Round(26.4) = 26"));
        assertEquals(true, parseExpression("Round(26.5) = 27"));

        try {
            assertEquals(true, parseExpression("Round(Date('2018-12-21')) = 0"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_1, "Round", "Date"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("Round(Time('09.15.00')) = 0"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_1, "Round", "Time"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("Round(Date('2018-12-21')) = 0"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_1, "Round", "Date"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("Round(Timestamp('2018-12-21-09.15.00.123')) = 0"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_1, "Round", "Timestamp"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testRound2Parameters() throws ParseException {

        assertEquals(true, parseExpression("Round('26', 1) = 26"));
        assertEquals(true, parseExpression("Round('26', 0) = 26"));
        assertEquals(true, parseExpression("Round('26', -1) = 26"));

        assertEquals(true, parseExpression("Round(26, 1) = 26"));
        assertEquals(true, parseExpression("Round(26, 0) = 26"));
        assertEquals(true, parseExpression("Round(26, -1) = 26"));

        assertEquals(true, parseExpression("Round(125.432, 1) = 125.4"));
        assertEquals(true, parseExpression("Round(124.567, 1) = 124.6"));

        assertEquals(true, parseExpression("Round(125.432, 0) = 125"));
        assertEquals(true, parseExpression("Round(124.567, 0) = 125"));

        assertEquals(true, parseExpression("Round(125.432, -1) = 130"));
        assertEquals(true, parseExpression("Round(124.567, -1) = 120"));

        try {
            assertEquals(true, parseExpression("Round(Time('09.15.00'), 0) = 0"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Round", "Time", "Long"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testRoundDate() throws ParseException {

        // Year: Rounds up on July 1 to January 1st of the next year
        assertEquals(true, parseExpression("Round(Date('2018-07-01'), 'YYYY') = '2019-01-01'"));
        assertEquals(true, parseExpression("Round(Date('2018-06-30'), 'YYYY') = '2018-01-01'"));

        // Month: Rounds up on the 16th day of the month
        assertEquals(true, parseExpression("Round(Date('2016-02-29'), 'MM') = '2016-03-01'"));
        assertEquals(true, parseExpression("Round(Date('2016-02-16'), 'MM') = '2016-03-01'"));
        assertEquals(true, parseExpression("Round(Date('2016-02-15'), 'MM') = '2016-02-01'"));

        // Day: Rounds up on the 12th hour of the day
        try {
            assertEquals(true, parseExpression("Round(Date('2016-02-29'), 'DD') = '2016-03-01'"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Round", "Date", "DD"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        /*
         * Hour, minute, second and microsecond are not allowed for dates.
         */

        try {
            assertEquals(true, parseExpression("Round(Date('2016-02-29'), 'HH12') = '2016-03-01'"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Round", "Date", "HH12"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("Round(Date('2016-02-29'), 'HH24') = '2016-03-01'"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Round", "Date", "HH24"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("Round(Date('2016-02-29'), 'MI') = '2016-03-01'"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Round", "Date", "MI"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("Round(Date('2016-02-29'), 'SS') = '2016-03-01'"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Round", "Date", "SS"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("Round(Date('2016-02-29'), 'NNN') = '2016-03-01'"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Round", "Date", "NNN"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testRoundTime() throws ParseException {

        // Hour: Rounds up at 30 minutes
        assertEquals(true, parseExpression("Round(Time('23:30:00'), 'HH24') = '00:00:00'"));
        assertEquals(true, parseExpression("Round(Time('23:29:00'), 'HH24') = '23:00:00'"));

        assertEquals(true, parseExpression("Round(Time('23:30:00'), 'HH12') = '00:00:00'"));
        assertEquals(true, parseExpression("Round(Time('23:29:00'), 'HH12') = '23:00:00'"));

        // Minute: Rounds up at 30 seconds
        assertEquals(true, parseExpression("Round(Time('23:59:30'), 'MI') = '00:00:00'"));
        assertEquals(true, parseExpression("Round(Time('23:59:29'), 'MI') = '23:59:00'"));

        /*
         * year, month, day, second and microsecond are not allowed for times.
         */

        try {
            assertEquals(true, parseExpression("Round(Time('23:59:30'), 'YYYY') = '00:00:00'"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Round", "Time", "YYYY"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("Round(Time('23:59:30'), 'MM') = '00:00:00'"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Round", "Time", "MM"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("Round(Time('23:59:30'), 'DD') = '00:00:00'"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Round", "Time", "DD"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("Round(Time('23:59:30'), 'SS') = '00:00:00'"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Round", "Time", "SS"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("Round(Time('23:59:30'), 'NNN') = '00:00:00'"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Round", "Time", "NNN"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testRoundTimestamp() throws ParseException {

        // Year: Rounds up on July 1 to January 1st of the next year
        assertEquals(true, parseExpression("Round(Timestamp('2018-07-01-23.30.00.123'), 'YYYY') = '2019-01-01-00.00.00.000'"));
        assertEquals(true, parseExpression("Round(Timestamp('2018-06-30-23.29.00.123'), 'YYYY') = '2018-01-01-00.00.00.000'"));

        // Month: Rounds up on the 16th day of the month
        assertEquals(true, parseExpression("Round(Timestamp('2016-02-29-23.30.00.123'), 'MM') = '2016-03-01-00.00.00.000'"));
        assertEquals(true, parseExpression("Round(Timestamp('2016-02-16-23.30.00.123'), 'MM') = '2016-03-01-00.00.00.000'"));
        assertEquals(true, parseExpression("Round(Timestamp('2016-02-15-23.30.00.123'), 'MM') = '2016-02-01-00.00.00.000'"));

        // Day: Rounds up on the 12th hour of the day
        assertEquals(true, parseExpression("Round(Timestamp('2018-12-21-12.00.00.000'), 'DD') = '2018-12-22-00.00.00.000'"));
        assertEquals(true, parseExpression("Round(Timestamp('2018-12-21-11.59.59.999'), 'DD') = '2018-12-21-00.00.00.000'"));

        // Hour: Rounds up at 30 minutes
        assertEquals(true, parseExpression("Round(Timestamp('2018-12-21-23.30.00.123'), 'HH24') = '2018-12-22-00.00.00.000'"));
        assertEquals(true, parseExpression("Round(Timestamp('2018-12-21-23.29.00.123'), 'HH24') = '2018-12-21-23.00.00.000'"));

        // Minute: Rounds up at 30 seconds
        assertEquals(true, parseExpression("Round(Timestamp('2018-12-21-23.59.30.123'), 'MI') = '2018-12-22-00.00.00.000'"));
        assertEquals(true, parseExpression("Round(Timestamp('2018-12-21-23.59.29.123'), 'MI') = '2018-12-21-23.59.00.000'"));

        // Second: Rounds up at 500000 microseconds
        assertEquals(true, parseExpression("Round(Timestamp('2018-12-21-23.59.30.500'), 'SS') = '2018-12-21-23.59.31.000'"));
        assertEquals(true, parseExpression("Round(Timestamp('2018-12-21-23.59.29.499'), 'SS') = '2018-12-21-23.59.29.000'"));

        /*
         * microsecond is not allowed for times.
         */

        try {
            assertEquals(true, parseExpression("Round(Timestamp('2018-12-21-23.59.30.500'), 'NNN') = '2018-12-21-23.59.31.000'"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Round", "Timestamp", "NNN"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testBetween() throws ParseException {

        // Test String (Test values from https://www.w3schools.com)
        assertEquals(false, parseExpression("'Carnarvon Tigers' BETWEEN 'Chais' AND 'Mishi Kobe Niku'"));
        assertEquals(true, parseExpression("'Chais' BETWEEN 'Chais' AND 'Mozzarella di Giovanni'"));
        assertEquals(true, parseExpression("'Chang' BETWEEN 'Chais' AND 'Mozzarella di Giovanni'"));
        assertEquals(true, parseExpression("'Maxilaku' BETWEEN 'Chais' AND 'Mozzarella di Giovanni'"));
        assertEquals(true, parseExpression("'Mozzarella di Giovanni' BETWEEN 'Chais' AND 'Mozzarella di Giovanni'"));
        assertEquals(false, parseExpression("'Carnarvon Tigers' BETWEEN 'Chais' AND 'Mozzarella di Giovanni'"));

        // Test Date
        assertEquals(true, parseExpression("DATE('2018-12-21') BETWEEN DATE('2018-12-20') AND DATE('2018-12-22')"));

        // Test Time
        assertEquals(true, parseExpression("TIME('15.10.30') BETWEEN TIME('15.10.29') AND TIME('15.10.31')"));

        // Test Timestamp
        assertEquals(true,
            parseExpression("TIMESTAMP('2018-12-21-15.10.30') BETWEEN TIMESTAMP('2018-12-21-15.10.29') AND TIMESTAMP('2018-12-21-15.10.31')"));
        assertEquals(
            true,
            parseExpression("TIMESTAMP('2018-12-21-15.10.30.123') BETWEEN TIMESTAMP('2018-12-21-15.10.30.122') AND TIMESTAMP('2018-12-21-15.10.30.124')"));

        // Test Date and Timestamp
        assertEquals(true, parseExpression("DATE('2018-12-21') BETWEEN TIMESTAMP('2018-12-20-23.59.59') AND TIMESTAMP('2018-12-22-00.00.00')"));

        // Test Numbers: begin and end values are included
        assertEquals(true, parseExpression("5 BETWEEN 5 AND 5"));
        assertEquals(true, parseExpression("5 BETWEEN 5 AND 6"));
        assertEquals(true, parseExpression("5 BETWEEN 4 AND 5"));

        // Test numbers
        assertEquals(true, parseExpression("5 BETWEEN 1 AND 10"));
        assertEquals(true, parseExpression("5.2 BETWEEN 1.1 AND 9.9"));
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

        assertEquals(true, parseExpression("DATE(TIMESTAMP('2018-12-05-15.30.00.123')) = '2018-12-05'"));
        assertEquals(true, parseExpression("DATE(TIMESTAMP('2018-12-05-15.30.00.123')) = '2018-12-05'"));

        assertEquals(true, parseExpression("DATE('2018-12-05') > '2018-12-04'"));
        assertEquals(true, parseExpression("DATE('2018-12-05') = '2018-12-05'"));
        assertEquals(true, parseExpression("DATE('2018-12-05') < '2018-12-06'"));

        assertEquals(true, parseExpression("DATE('2018-12-05', 'YYYY-MM-DD') > '2018-12-04'"));
        assertEquals(true, parseExpression("DATE('12-05-2018', 'MM/DD/yyyy') = '2018-12-05'"));
        assertEquals(true, parseExpression("DATE('05-12-2018', 'DD.MM.YYYY') < '2018-12-06'"));
    }

    @Test
    public void testTime() throws ParseException {

        assertEquals(true, parseExpression("TIME(TIMESTAMP('2018-12-05-15.30.00.123')) = '15.30.00'"));
        assertEquals(true, parseExpression("TIME(TIMESTAMP('2018-12-05-15.30.00.123')) = '15.30.00'"));

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

        // Test with milliseconds ISO
        formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        expected = formatter.format(getTimestamp(2018, 12, 5, 12, 0, 0, 123));
        assertEquals(true, parseExpression("TIMESTAMP('2018-12-05 12:00:00.123') = '" + expected + "'"));

        // Test without milliseconds ISO
        formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        expected = formatter.format(getTimestamp(2018, 12, 5, 12, 0, 0));
        assertEquals(true, parseExpression("TIMESTAMP('2018-12-05 12:00:00') = '" + expected + "'"));

        // Test with milliseconds IBM SQL
        formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSS");
        expected = formatter.format(getTimestamp(2018, 12, 5, 12, 0, 0, 123));
        assertEquals(true, parseExpression("TIMESTAMP('2018-12-05-12.00.00.123') = '" + expected + "'"));

        // Test without milliseconds IBM SQL
        formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
        expected = formatter.format(getTimestamp(2018, 12, 5, 12, 0, 0));
        assertEquals(true, parseExpression("TIMESTAMP('2018-12-05-12.00.00') = '" + expected + "'"));

        // Test with too less milliseconds
        formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SS");
        expected = formatter.format(getTimestamp(2018, 12, 5, 12, 0, 0, 88));
        assertEquals(true, parseExpression("TIMESTAMP('2018-12-05 12:00:00.88') = '" + expected + "'"));

        // Test with too many milliseconds
        formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        expected = formatter.format(getTimestamp(2018, 12, 5, 12, 0, 0, 888));
        assertEquals(true, parseExpression("TIMESTAMP('2018-12-05 12:00:00.888999') = '" + expected + "'"));

        // Test with pattern parameter

        // Test ISO
        formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        expected = formatter.format(getTimestamp(2018, 12, 5, 12, 0, 0, 123));
        assertEquals(true, parseExpression("TIMESTAMP('2018-12-05 12:00:00.123', 'YYYY-MM-DD HH24:MI:SS.NNN') = '" + expected + "'"));

        // Test IBM SQL
        formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSS");
        expected = formatter.format(getTimestamp(2018, 12, 5, 12, 0, 0, 123));
        assertEquals(true, parseExpression("TIMESTAMP('2018-12-05-12.00.00.123', 'YYYY-MM-DD-HH24.MI.SS.NNN') = '" + expected + "'"));

        try {
            // Too many parameters
            assertEquals(true, parseExpression("TIMESTAMP('2018-12-05-12.00.00.123', 'YYYY-MM-DD-HH24.MI.SS.NNN', 'TOO_MANY') = '" + expected + "'"));
        } catch (WrongNumberOfParametersException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_NUMBER_OF_PARAMETERS_EXCEPTION, 3, "Timestamp"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
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
            assertEquals(String.format(WRONG_NUMBER_OF_PARAMETERS_EXCEPTION, 3, "Trim"), message);
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
            assertEquals(String.format(WRONG_NUMBER_OF_PARAMETERS_EXCEPTION, 3, "Ltrim"), message);
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
            assertEquals(String.format(WRONG_NUMBER_OF_PARAMETERS_EXCEPTION, 3, "Rtrim"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testIn() throws ParseException {

        assertEquals(true, parseExpression("'PT' In('PT', 'PX', 'UB', 'UP', 'UR', 'DL', 'DR')"));

        assertEquals(true, parseExpression("'2.2' In(1, 2.2, 3, 4.5, 5, 6)"));
        assertEquals(true, parseExpression("4.5 In(1, 2.2, 3, 4.5, 5, 6)"));

        assertEquals(true, parseExpression("Date('2018-12-20') In(Date('2018-12-19'), '2018-12-20', '2018-12-21')"));

        assertEquals(true, parseExpression("Time('18:12:20') In(Time('18:12:19'), '18:12:20', '18:12:21')"));

        assertEquals(true,
            parseExpression("Timestamp('2018-12-20-18.12.20') In(Timestamp('2018-12-20-18.12.19'), '2018-12-20-18.12.20', '2018-12-20-18.12.21')"));
    }

    @Test
    public void testLength() throws ParseException {

        assertEquals(true, parseExpression("Length('Hello World') = 11"));

        try {
            assertEquals(true, parseExpression("Length(Date('2018-12-21')) = 11"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_1, "Length", "Date"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testLike() throws ParseException {

        assertEquals(true, parseExpression("'Westend New York' Like('%New%')"));
        assertEquals(true, parseExpression("'Westend New York' Like('__stend New York')"));
        assertEquals(true, parseExpression("'Eastend New York' Like('__stend New York')"));

        // Forwards to Regexp_Like
        assertEquals(true, parseExpression("'Westend New York' Like('/.*New.*/')"));

        try {
            assertEquals(true, parseExpression("'1' Like(1)"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Like", "String", "Long"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testNotLike() throws ParseException {

        assertEquals(true, parseExpression("'Westend New York' Not Like('%Old%')"));
        assertEquals(true, parseExpression("'Westend New York' Not Like('__stend Old York')"));
        assertEquals(true, parseExpression("'Eastend New York' Not Like('__stend Old York')"));

        // Forwards to Not Regexp_Like
        assertEquals(true, parseExpression("'Westend New York' Not Like('/.*Old.*/')"));

        try {
            assertEquals(true, parseExpression("'1' Not Like(2)"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "NotLike", "String", "Long"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testRegexp_Like() throws ParseException {

        assertEquals(true, parseExpression("'Westend New York' Regexp_Like('.*New.*')"));
        assertEquals(true, parseExpression("'Westend New York' Regexp_Like('.{2}stend New York')"));
        assertEquals(true, parseExpression("'Eastend New York' Regexp_Like('.{2}stend New York')"));

        try {
            assertEquals(true, parseExpression("'1' Regexp_Like(1)"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Regexp_Like", "String", "Long"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testNotRegexp_Like() throws ParseException {

        assertEquals(true, parseExpression("'Westend New York' Not Like('.*Old.*')"));
        assertEquals(true, parseExpression("'Westend New York' Not Like('.{2}stend Old York')"));
        assertEquals(true, parseExpression("'Eastend New York' Not Like('.{2}stend Old York')"));

        try {
            assertEquals(true, parseExpression("'1' Not Regexp_Like(2)"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "NotRegexp_Like", "String", "Long"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testModulus() throws ParseException {

        assertEquals(true, parseExpression("'11' Mod 3 = 2"));
        assertEquals(true, parseExpression("11 Mod '3' = 2"));
        assertEquals(true, parseExpression("11 Mod 3 = '2'"));

        assertEquals(true, parseExpression("11 Mod 3 = 2"));
        assertEquals(true, parseExpression("10 Mod 3 = 1"));
        assertEquals(true, parseExpression("9 Mod 3 = 0"));

        try {
            assertEquals(true, parseExpression("Date('2018-12-21') Mod 3 = 2"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Modulus", "Date", "Long"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("11 Mod Date('2018-12-21') = 2"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_2, "Modulus", "Long", "Date"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testUMinus() throws ParseException {

        assertEquals(true, parseExpression("-5 = 5 * -1"));

        try {
            assertEquals(true, parseExpression("-Date('2018-12-21') = 5 * -1"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_1, "UMinus", "Date"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testLpad() throws ParseException {

        assertEquals(true, parseExpression("Lpad('Hello', 10) = '     Hello'"));
        assertEquals(true, parseExpression("Lpad('Hello', 10, '-+') = '-+-+-Hello'"));

        /*
         * IBM SQL Reference: The LPAD function treats leading or trailing
         * blanks in expression as significant. Padding will only occur if the
         * actual length of expression is less than length, and pad is not an
         * empty string
         */
        assertEquals(true, parseExpression("Lpad('The quick brown fox', 10) = 'The quick brown fox'"));
        assertEquals(true, parseExpression("Lpad('  Hello', 10, '-+') = '-+-  Hello'"));

        try {
            // Too many parameters
            assertEquals(true, parseExpression("Lpad('Hello', 10, '-+', 4) = '-+-+-Hello'"));
        } catch (WrongNumberOfParametersException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_NUMBER_OF_PARAMETERS_EXCEPTION, 4, "Lpad"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("Lpad('Hello', Date('2018-12-21')) = '     Hello'"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_1, "Lpad", "Date"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testRpad() throws ParseException {

        assertEquals(true, parseExpression("Rpad('Hello', 10) = 'Hello     '"));
        assertEquals(true, parseExpression("Rpad('Hello', 10, '-+') = 'Hello-+-+-'"));

        /*
         * IBM SQL Reference: The RPAD function treats leading or trailing
         * blanks in expression as significant. Padding will only occur if the
         * actual length of expression is less than length, and pad is not an
         * empty string.
         */
        assertEquals(true, parseExpression("Rpad('The quick brown fox', 10) = 'The quick brown fox'"));
        assertEquals(true, parseExpression("Rpad('Hello  ', 10, '-+') = 'Hello  -+-'"));

        try {
            // Too many parameters
            assertEquals(true, parseExpression("Rpad('Hello', 10, '-+', 4) = 'Hello-+-+-'"));
        } catch (WrongNumberOfParametersException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_NUMBER_OF_PARAMETERS_EXCEPTION, 4, "Rpad"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("Rpad('Hello', Date('2018-12-21')) = 'Hello     '"));
        } catch (WrongTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_TYPE_EXCEPTION_1, "Rpad", "Date"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testSubstring() throws ParseException {

        assertEquals(true, parseExpression("Substring('Hello World', 1, 5) = 'Hello'"));
        assertEquals(true, parseExpression("Substring('Hello World', 7) = 'World'"));

        assertEquals(true, parseExpression("Substr('Hello World', 1, 5) = 'Hello'"));
        assertEquals(true, parseExpression("Substr('Hello World', 7) = 'World'"));

        assertEquals(true, parseExpression("Substring('Hello World', 7, 999) = 'World'"));
        assertEquals(true, parseExpression("Substring('Hello World', 999) = ''"));

        try {
            assertEquals(true, parseExpression("Substring('Hello World', -1) = 'Hello'"));
        } catch (WrongValueException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_VALUE_EXCEPTION, "-1", "start", "Substring"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("Substring('Hello World', 1, -1) = 'Hello'"));
        } catch (WrongValueException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_VALUE_EXCEPTION, "-1", "length", "Substring"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("Substring('Hello World', 'a') = 'Hello'"));
        } catch (UnexpectedTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(UNEXPECTED_TYPE_EXCEPTION, "start", "Substring", "Integer"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("Substring('Hello World', 1, 'a') = 'Hello'"));
        } catch (UnexpectedTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(UNEXPECTED_TYPE_EXCEPTION, "length", "Substring", "Integer"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("Substring('Hello World', 1, 1, 'TOO_MANY') = 'Hello'"));
        } catch (WrongNumberOfParametersException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_NUMBER_OF_PARAMETERS_EXCEPTION, 4, "Substring"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testLower() throws ParseException {

        assertEquals(true, parseExpression("Lower('Hello World') = 'hello world'"));
        assertEquals(true, parseExpression("Lower(2.235E+02) = '223.5'"));

        try {
            assertEquals(true, parseExpression("Lower(Date('2018-12-21')) = '2018-12-21'"));
        } catch (UnexpectedTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(UNEXPECTED_TYPE_EXCEPTION, "expression", "Lower", "String|Number"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testUpper() throws ParseException {

        assertEquals(true, parseExpression("Upper('Hello World') = 'HELLO WORLD'"));
        assertEquals(true, parseExpression("Upper(2.235E+02) = '223.5'"));

        try {
            assertEquals(true, parseExpression("Upper(Date('2018-12-21')) = '2018-12-21'"));
        } catch (UnexpectedTypeException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(UNEXPECTED_TYPE_EXCEPTION, "expression", "Upper", "String|Number"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }

    @Test
    public void testReplace() throws ParseException {

        assertEquals(true, parseExpression("Replace('ZZZ-ABC-ZZZ-ABC-ZZZ', 'ABC', 'WWW') = 'ZZZ-WWW-ZZZ-WWW-ZZZ'"));
        assertEquals(true, parseExpression("Replace('ZZZ-ABC-ZZZ-ABC-ZZZ', 'ABC') = 'ZZZ--ZZZ--ZZZ'"));

        try {
            assertEquals(true, parseExpression("Replace('ZZZ-ABC-ZZZ-ABC-ZZZ') = 'ZZZ--ZZZ--ZZZ'"));
        } catch (WrongNumberOfParametersException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_NUMBER_OF_PARAMETERS_EXCEPTION, 1, "Replace"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }

        try {
            assertEquals(true, parseExpression("Replace('ZZZ-ABC-ZZZ-ABC-ZZZ', 'ABC', 'WWW', 'TOO_MANY') = 'ZZZ-WWW-ZZZ-WWW-ZZZ'"));
        } catch (WrongNumberOfParametersException e) {
            String message = e.getLocalizedMessage();
            assertEquals(String.format(WRONG_NUMBER_OF_PARAMETERS_EXCEPTION, 4, "Replace"), message);
        } catch (Throwable e) {
            Assert.fail("Wrong exception: " + e.getClass());
        }
    }
}
