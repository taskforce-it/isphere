/*****************************************************************************
      SQLJEP - Java SQL Expression Parser 0.2
      November 1 2006
         (c) Copyright 2006, Alexey Gaidukov
      SQLJEP Author: Alexey Gaidukov

      SQLJEP is based on JEP 2.24 (http://www.singularsys.com/jep/)
           (c) Copyright 2002, Nathan Funk
 
      See LICENSE.txt for license information.
*****************************************************************************/

package org.medfoster.sqljep.function;

import java.sql.Timestamp;
import java.util.Calendar;

import static java.util.Calendar.*;
import org.medfoster.sqljep.*;

public class MakeDate extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 2;
	}
	
	public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable param2 = runtime.stack.pop();
		Comparable param1 = runtime.stack.pop();
		runtime.stack.push(makeDate(param1, param2, runtime.calendar));
	}

	public static java.sql.Date makeDate(Comparable param1, Comparable param2, Calendar cal) throws ParseException {
		if (param1 == null && param2 == null) {
			return null;
		}
		if (param1 instanceof String) {
			param1 = parse((String)param1);
		}
		if (param2 instanceof String) {
			param2 = parse((String)param2);
		}
		if (param1 instanceof Number && param2 instanceof Number) {
			int year = ((Number)param1).intValue();
			int day = ((Number)param2).intValue();
			cal.clear();
			cal.set(year, 0, day);
			return new java.sql.Date(cal.getTimeInMillis());
		}
		throw new ParseException(WRONG_TYPE+" makedate("+param1.getClass()+","+param2.getClass()+")");
	}
}

