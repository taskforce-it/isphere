/*******************************************************************************
 * Copyright (c) project_year-2020 project_team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.junit;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.HashMap;

import org.junit.Test;
import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.RowJEP;

public class TestBigInteger extends AbstractJUnitTestCase {

    @Test
    public void testEqual() throws org.medfoster.sqljep.ParseException, ParseException {

        HashMap<String, Integer> columnMapping = getColumnMapping();
        RowJEP sqljep;
        sqljep = new RowJEP("JOSEQN = " + getBigInteger().toString());
        sqljep.parseExpression(columnMapping);

        Comparable<?>[] row = getRow();
        boolean isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);
    }

    @Test
    public void testLowerEqual() throws org.medfoster.sqljep.ParseException, ParseException {

        HashMap<String, Integer> columnMapping = getColumnMapping();
        RowJEP sqljep;
        sqljep = new RowJEP("JOSEQN <= " + getBigInteger().toString());
        sqljep.parseExpression(columnMapping);

        Comparable<?>[] row = getRow();
        boolean isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);

        columnMapping = getColumnMapping();
        sqljep = new RowJEP("JOSEQN <= " + getBigInteger(1).toString());
        sqljep.parseExpression(columnMapping);

        row = getRow();
        isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);

        columnMapping = getColumnMapping();
        sqljep = new RowJEP("JOSEQN <= " + getBigInteger(-1).toString());
        sqljep.parseExpression(columnMapping);

        row = getRow();
        isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(false, isSelected);
    }

    @Test
    public void testLower() throws org.medfoster.sqljep.ParseException, ParseException {

        HashMap<String, Integer> columnMapping = getColumnMapping();
        RowJEP sqljep;
        sqljep = new RowJEP("JOSEQN < " + getBigInteger().toString());
        sqljep.parseExpression(columnMapping);

        Comparable<?>[] row = getRow();
        boolean isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(false, isSelected);

        columnMapping = getColumnMapping();
        sqljep = new RowJEP("JOSEQN < " + getBigInteger(1).toString());
        sqljep.parseExpression(columnMapping);

        row = getRow();
        isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);

        columnMapping = getColumnMapping();
        sqljep = new RowJEP("JOSEQN < " + getBigInteger(-1).toString());
        sqljep.parseExpression(columnMapping);

        row = getRow();
        isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(false, isSelected);
    }

    @Test
    public void testGreater() throws org.medfoster.sqljep.ParseException, ParseException {

        HashMap<String, Integer> columnMapping = getColumnMapping();
        RowJEP sqljep;
        sqljep = new RowJEP("JOSEQN > " + getBigInteger().toString());
        sqljep.parseExpression(columnMapping);

        Comparable<?>[] row = getRow();
        boolean isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(false, isSelected);

        columnMapping = getColumnMapping();
        sqljep = new RowJEP("JOSEQN > " + getBigInteger(1).toString());
        sqljep.parseExpression(columnMapping);

        row = getRow();
        isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(false, isSelected);

        columnMapping = getColumnMapping();
        sqljep = new RowJEP("JOSEQN > " + getBigInteger(-1).toString());
        sqljep.parseExpression(columnMapping);

        row = getRow();
        isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);
    }

    @Test
    public void testGreaterEqual() throws org.medfoster.sqljep.ParseException, ParseException {

        HashMap<String, Integer> columnMapping = getColumnMapping();
        RowJEP sqljep;
        sqljep = new RowJEP("JOSEQN >= " + getBigInteger().toString());
        sqljep.parseExpression(columnMapping);

        Comparable<?>[] row = getRow();
        boolean isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);

        columnMapping = getColumnMapping();
        sqljep = new RowJEP("JOSEQN >= " + getBigInteger(1).toString());
        sqljep.parseExpression(columnMapping);

        row = getRow();
        isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(false, isSelected);

        columnMapping = getColumnMapping();
        sqljep = new RowJEP("JOSEQN >= " + getBigInteger(-1).toString());
        sqljep.parseExpression(columnMapping);

        row = getRow();
        isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);
    }

}
