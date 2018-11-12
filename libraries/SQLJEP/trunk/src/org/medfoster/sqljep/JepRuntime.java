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

import java.util.Calendar;
import java.text.DateFormatSymbols;

final public class JepRuntime {
	public static final ThreadLocal<Calendar> threadCalendar = new ThreadLocal<Calendar>();
	public static final ThreadLocal<DateFormatSymbols> threadDateFormatSymbols = new ThreadLocal<DateFormatSymbols>();
	public java.util.Stack<Comparable> stack = new java.util.Stack<Comparable>();
	public Calendar calendar;
	public DateFormatSymbols dateSymbols;
	public final ParserVisitor ev;
	
	public JepRuntime(ParserVisitor visitor) {
		ev = visitor;
	}
}
