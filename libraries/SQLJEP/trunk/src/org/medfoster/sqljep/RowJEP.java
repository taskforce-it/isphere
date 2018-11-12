/*****************************************************************************
      SQLJEP - Java SQL Expression Parser 0.2
      November 1 2006
         (c) Copyright 2006, Alexey Gaidukov
      SQLJEP Author: Alexey Gaidukov

      SQLJEP is based on JEP 2.24 (http://www.singularsys.com/jep/)
           (c) Copyright 2002, Nathan Funk
 
      See LICENSE.txt for license information.
*****************************************************************************/

package org.medfoster.sqljep;

import java.util.*;

/**
 * Example of usage
 * <blockquote><pre>
 * 	Comparable[] row = {1,100.00,new java.util.Date()};
 * 	HashMap<String,Integer> columnMapping = new HashMap<String,Integer>();
 * 	columnMapping.put("ID",0);
 * 	columnMapping.put("SUM",1);
 * 	columnMapping.put("SALE_DATE",2);
 * 	RowJEP sqljep = new RowJEP("ID in (1,2,3) and SUM>100 and SALE_DATE>to_date('2006-01-01','yyyy-mm-dd')");
 * 	try {
 * 		sqljep.parseExpression(columnMapping);
 * 		System.out.println(sqljep.getValue(row));
 * 	}
 * 	catch (ParseException e) {
 * 		e.printStackTrace();
 * 	}
 * </blockquote></pre>
 * @author Alexey Gaidukov
 * @see org.medfoster.sqljep.BaseJEP
 */
public class RowJEP extends BaseJEP {
	protected HashMap<String,Integer> columnMapping = null;
	protected Comparable[] row = null;

	public RowJEP(String exp) {
		super(exp);
	}

	public void clear() {
		super.clear();
		this.columnMapping = null;
	}
	
	public int findColumn(String name){
		if (columnMapping != null) {
			Integer v = columnMapping.get(name);
			if (v != null) {
				return v;
			}
		}
		return -1;
	}

	public Comparable getColumnObject(int column) throws ParseException {
		try {
			return row[column];
		} catch (Exception e) {
			throw new ParseException("Column index:"+column, e);
		}
	}
	
	public Map.Entry getVariable(String name) throws ParseException {
		return null;
	}

	public void setRow(Comparable[] row) {
		this.row = row;
	}
	
	public void parseExpression(HashMap<String,Integer> columnMapping) throws ParseException {
		this.columnMapping = columnMapping;
		super.parseExpression();
	}
	
	public Comparable getValue(Comparable[] row) throws ParseException {
		this.row = row;
		return super.getValue();
	}
}
