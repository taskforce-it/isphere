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

public class Microsecond extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 1;
	}
	
	public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable param = runtime.stack.pop();
		runtime.stack.push(microsecond(param));
	}


	public static Long microsecond(Comparable param) throws ParseException {
		if (param == null) {
			return null;
		}
		if (param instanceof Timestamp) {
			Timestamp ts = (Timestamp)param;
			return new Long(ts.getTime());
		}
		throw new ParseException(WRONG_TYPE+" microsecond("+param.getClass()+")");
	}
}

