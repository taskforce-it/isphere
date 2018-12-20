/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.junit;

import static org.junit.Assert.assertEquals;

import java.sql.Time;
import java.text.ParseException;
import java.util.Date;

import org.junit.Test;
import org.medfoster.sqljep.function.OracleDateFormat;
import org.medfoster.sqljep.function.OracleTimeFormat;

public class TestOracleTimestamp extends AbstractJUnitTestCase {

    private static final String DATE_ISO = "YYYY-MM-DD";
    private static final String DATE_USA = "MM/DD/yyyy";
    private static final String DATE_EUR = "DD.MM.YYYY";

    private static final String TIME_ISO = "HH24.MI.SS";
    private static final String TIME_USA_AM = "HH12:MI AM";
    private static final String TIME_USA_PM = "HH12:MI PM";
    private static final String TIME_EUR = "HH24.MI.SS";

    @Test
    public void testTimeFormats() throws ParseException {

        Date actual;
        Date expected;
        OracleTimeFormat format;

        format = new OracleTimeFormat(TIME_ISO);
        actual = (Time)format.parseObject("08:50:00");
        expected = stripMilliSeconds(getTime(8, 50, 0));
        assertEquals(expected, actual);

        format = new OracleTimeFormat(TIME_USA_AM);
        actual = (Time)format.parseObject("08:50am");
        expected = stripMilliSeconds(getTime(8, 50, "am"));
        assertEquals(expected, actual);

        format = new OracleTimeFormat(TIME_USA_AM);
        actual = (Time)format.parseObject("08:50 am");
        expected = stripMilliSeconds(getTime(8, 50, "am"));
        assertEquals(expected, actual);

        format = new OracleTimeFormat(TIME_USA_PM);
        actual = (Time)format.parseObject("08:50pm");
        expected = stripMilliSeconds(getTime(8, 50, "pm"));
        assertEquals(expected, actual);

        format = new OracleTimeFormat(TIME_USA_PM);
        actual = (Time)format.parseObject("08:50 pm");
        expected = stripMilliSeconds(getTime(8, 50, "pm"));
        assertEquals(expected, actual);

        format = new OracleTimeFormat(TIME_EUR);
        actual = (Time)format.parseObject("08.50.30");
        expected = stripMilliSeconds(getTime(8, 50, 30));
        assertEquals(expected, actual);

    }

    @Test
    public void testDateFormats() throws ParseException {

        Date actual;
        Date expected;
        OracleDateFormat format;

        format = new OracleDateFormat(DATE_ISO);
        actual = (Date)format.parseObject("2018-12-04");
        expected = stripMilliSeconds(getDate(2018, 12, 4));
        assertEquals(expected, actual);

        format = new OracleDateFormat(DATE_USA);
        actual = (Date)format.parseObject("12/4/2018");
        expected = stripMilliSeconds(getDate(2018, 12, 4));
        assertEquals(expected, actual);

        format = new OracleDateFormat(DATE_EUR);
        actual = (Date)format.parseObject("4.12.2018");
        expected = stripMilliSeconds(getDate(2018, 12, 4));
        assertEquals(expected, actual);

    }

}
