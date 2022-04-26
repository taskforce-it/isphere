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

import org.medfoster.sqljep.*;

public final class LogicalNOT extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 1;
	}
	
	public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable param = runtime.stack.pop();
		runtime.stack.push(not(param));		//push the result on the inStack
	}

	public static Boolean not(Comparable param) throws ParseException {
		if (param == null) {
			return null;
		}
		if (param instanceof Boolean) {
			return ((Boolean)param).booleanValue() ? Boolean.TRUE : Boolean.FALSE;
		}
		throw new ParseException(WRONG_TYPE+" not "+param.getClass());
	}
}

