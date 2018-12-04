package org.medfoster.sqljep.junit;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;
import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.RowJEP;

public class TestDateTime extends AbstractJUnitTestCase {

	@Test
	public void testDateEqual() throws org.medfoster.sqljep.ParseException,
			ParseException {

		HashMap<String, Integer> columnMapping = getColumnMapping();
		RowJEP sqljep;
		sqljep = new RowJEP("JODATE = '22.10.2018'");
		sqljep.parseExpression(columnMapping);

		Comparable[] row = getRow();
		boolean isSelected = (Boolean) sqljep.getValue(row);

		assertEquals(true, isSelected);
	}

	@Test
	public void testTimeEqual24EUR() throws org.medfoster.sqljep.ParseException,
			ParseException {

		HashMap<String, Integer> columnMapping = getColumnMapping();
		RowJEP sqljep;
  		sqljep = new RowJEP("JOTIME < '21:30:45'");
		sqljep.parseExpression(columnMapping);

		Comparable[] row = getRow();
		boolean isSelected = (Boolean) sqljep.getValue(row);

		assertEquals(true, isSelected);
	}

	@Test
	public void testTimeEqual24ISO() throws org.medfoster.sqljep.ParseException,
			ParseException {

		HashMap<String, Integer> columnMapping = getColumnMapping();
		RowJEP sqljep;
  		sqljep = new RowJEP("JOTIME < '21.30.45'");
		sqljep.parseExpression(columnMapping);

		Comparable[] row = getRow();
		boolean isSelected = (Boolean) sqljep.getValue(row);

		assertEquals(true, isSelected);
	}

	@Test
	public void testTimeEqual() throws org.medfoster.sqljep.ParseException,
			ParseException {

		HashMap<String, Integer> columnMapping = getColumnMapping();
		RowJEP sqljep;
  		sqljep = new RowJEP("JOTIME = '03:05 pm'");
		sqljep.parseExpression(columnMapping);

		Comparable[] row = getRow();
		boolean isSelected = (Boolean) sqljep.getValue(row);

		assertEquals(true, isSelected);
	}

	@Test
	public void testDateLower() throws org.medfoster.sqljep.ParseException,
			ParseException {

		HashMap<String, Integer> columnMapping = getColumnMapping();
		RowJEP sqljep;
		sqljep = new RowJEP("JODATE < '23.10.2018'");
		sqljep.parseExpression(columnMapping);

		Comparable[] row = getRow();
		boolean isSelected = (Boolean) sqljep.getValue(row);

		assertEquals(true, isSelected);
	}

	@Test
	public void testTimeLower() throws org.medfoster.sqljep.ParseException,
			ParseException {

		HashMap<String, Integer> columnMapping = getColumnMapping();
		RowJEP sqljep;
		sqljep = new RowJEP("JOTIME < '03:06 pm'");
		sqljep.parseExpression(columnMapping);

		Comparable[] row = getRow();
		boolean isSelected = (Boolean) sqljep.getValue(row);

		assertEquals(true, isSelected);
	}

	@Test
	public void testDateGreater() throws org.medfoster.sqljep.ParseException,
			ParseException {

		HashMap<String, Integer> columnMapping = getColumnMapping();
		RowJEP sqljep;
		sqljep = new RowJEP("JODATE > '21.10.2018'");
		sqljep.parseExpression(columnMapping);

		Comparable[] row = getRow();
		boolean isSelected = (Boolean) sqljep.getValue(row);

		assertEquals(true, isSelected);
	}

	@Test
	public void testTimeGreater() throws org.medfoster.sqljep.ParseException,
			ParseException {

		HashMap<String, Integer> columnMapping = getColumnMapping();
		RowJEP sqljep;
		sqljep = new RowJEP("JOTIME > '03:04 pm'");
		sqljep.parseExpression(columnMapping);

		Comparable[] row = getRow();
		boolean isSelected = (Boolean) sqljep.getValue(row);

		assertEquals(true, isSelected);
	}

	private Comparable[] getRow() throws ParseException {

		Comparable[] row = new Comparable[columnMappings.size()];

		row[JOCODE] = "R";
		row[JOENTT] = "PT";
		row[JOJOB] = "QZDASOINIT";
		row[JOUSER] = "DONALD";
		row[JONBR] = "123456";
		row[JOLIB] = "QGPL";
		row[JOOBJ] = "ITEMS";
		row[JOMBR] = "ITEMS";
		row[JODATE] = getDate(2018, 10, 22);
		row[JOTIME] = getTime(3, 5, "pm");

		return row;
	}

	private static final int JOCODE = 0;
	private static final int JOENTT = 1;
	private static final int JOJOB = 2;
	private static final int JOUSER = 3;
	private static final int JONBR = 4;
	private static final int JOLIB = 5;
	private static final int JOOBJ = 6;
	private static final int JOMBR = 7;
	private static final int JODATE = 8;
	private static final int JOTIME = 9;

	public static HashMap<String, Integer> getColumnMapping() {
		return columnMappings;
	}

	private static HashMap<String, Integer> columnMappings;
	static {
		columnMappings = new HashMap<String, Integer>();
		columnMappings.put("JOCODE", JOCODE);
		columnMappings.put("JOENTT", JOENTT);
		columnMappings.put("JOJOB", JOJOB);
		columnMappings.put("JOUSER", JOUSER);
		columnMappings.put("JONBR", JONBR);
		columnMappings.put("JOLIB", JOLIB);
		columnMappings.put("JOOBJ", JOOBJ);
		columnMappings.put("JOMBR", JOMBR);
		columnMappings.put("JODATE", JODATE);
		columnMappings.put("JOTIME", JOTIME);
	}

}