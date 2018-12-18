/*******************************************************************************
 * Copyright (c) project_year-2018 project_team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.junit.functions;

import static org.junit.Assert.assertEquals;

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
		assertEquals(true, parseExpression("2.41 - 10 = -7.59"));
	}
    
    @Test
    public void testBetween() throws ParseException {
        
        assertEquals(true, parseExpression("5 BETWEEN 1 AND 10"));
        assertEquals(true, parseExpression("15 NOT BETWEEN 1 AND 10"));
    }

	@Test
	public void testYear() throws ParseException {

		assertEquals(true,
				parseExpression("year(JODATE) = 2018", getColumnMapping()));
	}
}
