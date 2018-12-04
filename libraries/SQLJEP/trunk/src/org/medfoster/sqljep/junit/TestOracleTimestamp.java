/*******************************************************************************
 * Copyright (c) project_year-2018 project_team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.junit;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;
import org.medfoster.sqljep.function.OracleTimestampFormat;

public class TestOracleTimestamp extends AbstractJUnitTestCase {

	private static final String DATE_ISO = "YYYY-MM-DD";
	private static final String DATE_USA = "MM/DD/yyyy";
	private static final String DATE_EUR = "DD.MM.YYYY";

	private static final String TIME_ISO = "HH.MI.SS";
	private static final String TIME_USA_AM = "HH:MI AM";
	private static final String TIME_USA_PM = "HH:MI PM";
	private static final String TIME_EUR = "HH.MI.SS";

	@Test
	public void testTimeFormats() throws ParseException {

		Date actual;
		Date expected;
		OracleTimestampFormat format;

		format = new OracleTimestampFormat(TIME_ISO);
		actual = (Date) format.parseObject("08:50:00");
		expected = getTime(8, 50, 0);
		assertEquals(expected, actual);

		format = new OracleTimestampFormat(TIME_USA_AM);
		actual = (Date) format.parseObject("08:50am");
		expected = getTime(8, 50, "am");
		assertEquals(expected, actual);

		format = new OracleTimestampFormat(TIME_USA_AM);
		actual = (Date) format.parseObject("08:50 am");
		expected = getTime(8, 50, "am");
		assertEquals(expected, actual);

		format = new OracleTimestampFormat(TIME_USA_PM);
		actual = (Date) format.parseObject("08:50pm");
		expected = getTime(8, 50, "pm");
		assertEquals(expected, actual);

		format = new OracleTimestampFormat(TIME_USA_PM);
		actual = (Date) format.parseObject("08:50 pm");
		expected = getTime(8, 50, "pm");
		assertEquals(expected, actual);

		format = new OracleTimestampFormat(TIME_EUR);
		actual = (Date) format.parseObject("08.50.30");
		expected = getTime(8, 50, 30);
		assertEquals(expected, actual);

	}

	@Test
	public void testDateFormats() throws ParseException {

		Date actual;
		Date expected;
		OracleTimestampFormat format;

		format = new OracleTimestampFormat(DATE_ISO);
		actual = (Date) format.parseObject("2018-12-04");
		expected = getDate(2018, 12, 4);
		assertEquals(expected, actual);

		format = new OracleTimestampFormat(DATE_USA);
		actual = (Date) format.parseObject("12/4/2018");
		expected = getDate(2018, 12, 4);
		assertEquals(expected, actual);

		format = new OracleTimestampFormat(DATE_EUR);
		actual = (Date) format.parseObject("4.12.2018");
		expected = getDate(2018, 12, 4);
		assertEquals(expected, actual);

	}

}
