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

import java.util.Calendar;
import java.sql.*;

import static java.util.Calendar.*;
import org.medfoster.sqljep.*;

public class LastDay extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 1;
	}
	
	public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable param = runtime.stack.pop();
		runtime.stack.push(lastDay(param, runtime.calendar)); // push the result on the inStack
	}

	public static java.util.Date lastDay(Comparable param, Calendar cal) throws ParseException {
		if (param == null) {
			return null;
		}
		if (param instanceof Timestamp || param instanceof java.sql.Date) {
			java.util.Date d = (java.util.Date)param;
			cal.setTimeInMillis(d.getTime());
			int day = cal.getActualMaximum(DAY_OF_MONTH);
			cal.set(DATE, day);
			return new java.util.Date(cal.getTimeInMillis());
		} else {
			throw new ParseException(WRONG_TYPE+"  last_day("+param.getClass()+")");
		}
	}
}

