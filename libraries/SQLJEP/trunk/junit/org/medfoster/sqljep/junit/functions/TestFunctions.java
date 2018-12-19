/*******************************************************************************
 * Copyright (c) project_year-2018 project_team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.junit.functions;

import static org.junit.Assert.assertEquals;

import java.util.Date;

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
	}
    
    @Test
    public void testBetween() throws ParseException {
        
        assertEquals(true, parseExpression("5 BETWEEN 1 AND 10"));
        assertEquals(true, parseExpression("15 NOT BETWEEN 1 AND 10"));
    }
    
    @Test
    public void testCeil() throws ParseException {
        
        assertEquals(true, parseExpression("CEIL(10.5) = 11"));
        assertEquals(true, parseExpression("ceil(10.4) = 11"));
        assertEquals(false, parseExpression("CEIL(10.5) = 10"));
        assertEquals(false, parseExpression("ceil(10.4) = 10"));
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
        
        assertEquals(true, parseExpression("DATE('2018-12-05') + Day(10) = '2018-12-15'"));
        assertEquals(true, parseExpression("DATE('2018-12-05') - Day(10) = '2018-11-25'"));
        
        assertEquals(true, parseExpression("DATE('2018-12-05') + Day('10') = '2018-12-15'"));
        assertEquals(true, parseExpression("DATE('2018-12-05') - Day('10') = '2018-11-25'"));
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
        assertEquals(true, parseExpression("TIME('09:00:00', 'HH:MI AM') = '09:00:00'"));
        assertEquals(true, parseExpression("TIME('09:00:00', 'HH:MI PM') = '21:00:00'"));
        assertEquals(true, parseExpression("TIME('12:00:00', 'HH24:MI:SS') < '12:00:01'"));
    }

	@Test
	public void testYear() throws ParseException {

		assertEquals(true,
				parseExpression("year(JODATE) = 2018", getColumnMapping()));
	}
}
