package org.medfoster.sqljep.junit;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;
import org.medfoster.sqljep.RowJEP;
import org.medfoster.sqljep.exceptions.ParseException;

public class TestDateTime extends AbstractJUnitTestCase {

    @Test
    public void testTimeNotEqual2() throws org.medfoster.sqljep.exceptions.ParseException, ParseException {

        HashMap<String, Integer> columnMapping = getColumnMapping();
        RowJEP sqljep;
        sqljep = new RowJEP("JOTIME2 <> JOTIME3");
        sqljep.parseExpression(columnMapping);

        Comparable<?>[] row = getRow();
        boolean isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);
    }

    @Test
    public void testTimeNotEqualComparingMSecs() throws org.medfoster.sqljep.exceptions.ParseException, ParseException {

        HashMap<String, Integer> columnMapping = getColumnMapping();
        RowJEP sqljep;
        sqljep = new RowJEP("JOTIME2 != JOTIME3");
        sqljep.parseExpression(columnMapping);

        Comparable<?>[] row = getRow();
        boolean isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);
    }

    @Test
    public void testDateEqual() throws org.medfoster.sqljep.exceptions.ParseException, ParseException {

        HashMap<String, Integer> columnMapping = getColumnMapping();
        RowJEP sqljep;
        sqljep = new RowJEP("JODATE = '22.10.2018'");
        sqljep.parseExpression(columnMapping);

        Comparable<?>[] row = getRow();
        boolean isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);
    }

    @Test
    public void testDateEqualReverse() throws org.medfoster.sqljep.exceptions.ParseException, ParseException {

        HashMap<String, Integer> columnMapping = getColumnMapping();
        RowJEP sqljep;
        sqljep = new RowJEP("'22.10.2018' = JODATE");
        sqljep.parseExpression(columnMapping);

        Comparable<?>[] row = getRow();
        boolean isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);
    }

    @Test
    public void testTimeEqual24EUR() throws org.medfoster.sqljep.exceptions.ParseException, ParseException {

        HashMap<String, Integer> columnMapping = getColumnMapping();
        RowJEP sqljep;
        sqljep = new RowJEP("JOTIME = '15:05:00'");
        sqljep.parseExpression(columnMapping);

        Comparable<?>[] row = getRow();
        boolean isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);
    }

    @Test
    public void testTimeEqual24EURReverse() throws org.medfoster.sqljep.exceptions.ParseException, ParseException {

        HashMap<String, Integer> columnMapping = getColumnMapping();
        RowJEP sqljep;
        sqljep = new RowJEP("'15:05:00' = JOTIME");
        sqljep.parseExpression(columnMapping);

        Comparable<?>[] row = getRow();
        boolean isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);
    }

    @Test
    public void testTimeEqual24ISO() throws org.medfoster.sqljep.exceptions.ParseException, ParseException {

        HashMap<String, Integer> columnMapping = getColumnMapping();
        RowJEP sqljep;
        sqljep = new RowJEP("JOTIME = '15.05.00'");
        sqljep.parseExpression(columnMapping);

        Comparable<?>[] row = getRow();
        boolean isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);
    }

    @Test
    public void testTimeEqual() throws org.medfoster.sqljep.exceptions.ParseException, ParseException {

        HashMap<String, Integer> columnMapping = getColumnMapping();
        RowJEP sqljep;
        sqljep = new RowJEP("JOTIME = '03:05 pm'");
        sqljep.parseExpression(columnMapping);

        Comparable<?>[] row = getRow();
        boolean isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);
    }

    @Test
    public void testDateLower() throws org.medfoster.sqljep.exceptions.ParseException, ParseException {

        HashMap<String, Integer> columnMapping = getColumnMapping();
        RowJEP sqljep;
        sqljep = new RowJEP("JODATE < '23.10.2018'");
        sqljep.parseExpression(columnMapping);

        Comparable<?>[] row = getRow();
        boolean isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);
    }

    @Test
    public void testTimeLower() throws org.medfoster.sqljep.exceptions.ParseException, ParseException {

        HashMap<String, Integer> columnMapping = getColumnMapping();
        RowJEP sqljep;
        sqljep = new RowJEP("JOTIME < '03:06 pm'");
        sqljep.parseExpression(columnMapping);

        Comparable<?>[] row = getRow();
        boolean isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);
    }

    @Test
    public void testDateGreater() throws org.medfoster.sqljep.exceptions.ParseException, ParseException {

        HashMap<String, Integer> columnMapping = getColumnMapping();
        RowJEP sqljep;
        sqljep = new RowJEP("JODATE > '21.10.2018'");
        sqljep.parseExpression(columnMapping);

        Comparable<?>[] row = getRow();
        boolean isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);
    }

    @Test
    public void testDateGreaterReverse() throws org.medfoster.sqljep.exceptions.ParseException, ParseException {

        HashMap<String, Integer> columnMapping = getColumnMapping();
        RowJEP sqljep;
        sqljep = new RowJEP("'21.10.2018' < JODATE");
        sqljep.parseExpression(columnMapping);

        Comparable<?>[] row = getRow();
        boolean isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);
    }

    @Test
    public void testTimeGreater() throws org.medfoster.sqljep.exceptions.ParseException, ParseException {

        HashMap<String, Integer> columnMapping = getColumnMapping();
        RowJEP sqljep;
        sqljep = new RowJEP("JOTIME > '03:04 pm'");
        sqljep.parseExpression(columnMapping);

        Comparable<?>[] row = getRow();
        boolean isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);
    }

    @Test
    public void testTimeGreaterReverse() throws org.medfoster.sqljep.exceptions.ParseException, ParseException {

        HashMap<String, Integer> columnMapping = getColumnMapping();
        RowJEP sqljep;
        sqljep = new RowJEP("'03:04 pm' < JOTIME");
        sqljep.parseExpression(columnMapping);

        Comparable<?>[] row = getRow();
        boolean isSelected = (Boolean)sqljep.getValue(row);

        assertEquals(true, isSelected);
    }

}