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

public class DayOfWeek extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 1;
	}
	
	public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable param = runtime.stack.pop();
		runtime.stack.push(dayOfWeek(param, runtime.calendar));
	}

	public static Integer dayOfWeek(Comparable param, Calendar cal) throws ParseException {
		if (param == null) {
			return null;
		}
		if (param instanceof Timestamp || param instanceof java.sql.Date) {
			java.util.Date ts = (java.util.Date)param;
			cal.setTimeInMillis(ts.getTime());
			int dayOfWeek = cal.get(DAY_OF_WEEK)-1;
			if (dayOfWeek == 0) {
				dayOfWeek = 7;
			}
			return new Integer(dayOfWeek);
		}
		throw new ParseException(WRONG_TYPE+" dayofweek("+param.getClass()+")");
	}
}

