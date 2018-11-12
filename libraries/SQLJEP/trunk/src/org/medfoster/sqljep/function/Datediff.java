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

import java.sql.*;
import org.medfoster.sqljep.*;

public class Datediff extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 2;
	}
	
	/**
	 * Calculates the result of applying the "+" operator to the arguments from
	 * the stack and pushes it back on the stack.
	 */
	public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable param2 = runtime.stack.pop();
		Comparable param1 = runtime.stack.pop();
		runtime.stack.push(datediff(param1, param2)); //push the result on the inStack
	}

		public static Long datediff(Comparable param1, Comparable param2) throws ParseException {
		if (param1 == null && param2 == null) {
			return null;
		}
		try {
			if (param1 instanceof java.util.Date && param2 instanceof java.util.Date) {
				if (param1 instanceof Time || param2 instanceof Time) {
					throw new ParseException();
				}
				java.util.Date d1 = (java.util.Date)param1;
				java.util.Date d2 = (java.util.Date)param2;
				return new Long((d1.getTime()-d2.getTime())/86400000);
			} else {
				throw new ParseException();
			}
		} catch (ParseException e) {
			throw new ParseException(WRONG_TYPE+"  ("+param1.getClass()+"+"+param2.getClass()+")");
		}
	}
}
