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

import java.lang.Math;
import java.math.*;
import org.medfoster.sqljep.*;

public class Floor extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 1;
	}
	
	public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable param = runtime.stack.pop();
		runtime.stack.push(floor(param));		//push the result on the inStack
	}

	public static Comparable floor(Comparable param) throws ParseException {
		if (param == null) {
			return null;
		}
		if (param instanceof String) {
			param = parse((String)param);
		}
		if (param instanceof BigDecimal) {		// BigInteger is not supported
			BigDecimal b = ((BigDecimal)param).setScale(0, BigDecimal.ROUND_FLOOR);
			try {
				return b.longValueExact();
			} catch (ArithmeticException e) {
			}
			return b;
		}
		if (param instanceof Double || param instanceof Float) {
			return Math.floor(((Number)param).doubleValue());
		}
		if (param instanceof Number) {		// Long, Integer, Short, Byte 
			return param;
		}
		throw new ParseException(WRONG_TYPE+" floor("+param.getClass()+")");
	}
}
