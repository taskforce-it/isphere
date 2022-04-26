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

import java.math.BigDecimal;
import org.medfoster.sqljep.*;
 
public final class UMinus extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 1;
	}
	
	public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable param = runtime.stack.pop();
		runtime.stack.push(umin(param));
	}
	
	public static Comparable umin(Comparable param) throws ParseException {
		if (param == null) {
			return null;
		}
		if (param instanceof String) {
			param = parse((String)param);
		}
		if (param instanceof BigDecimal) {		// BigInteger is not supported
			return ((BigDecimal)param).negate();
		}
		if (param instanceof Double || param instanceof Float) {
			return new Double(-((Number)param).doubleValue());
		}
		if (param instanceof Number) {		// Long, Integer, Short, Byte 
			return new Long(-((Number)param).longValue());
		}
		throw new ParseException(WRONG_TYPE+" "+param.getClass());
	}
}

