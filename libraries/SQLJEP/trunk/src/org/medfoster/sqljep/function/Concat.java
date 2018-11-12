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

public class Concat extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 2;
	}
	
	/**
	 * Calculates the result of applying the "||" operator to the arguments from
	 * the stack and pushes it back on the stack.
	 */
	public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable param2 = runtime.stack.pop();
		Comparable param1 = runtime.stack.pop();
		runtime.stack.push(concat(param1, param2)); //push the result on the inStack
	}

	public static String concat(Comparable param1, Comparable param2) throws ParseException {
		if (param1 == null) {
			return (param2 != null) ? param2.toString() : null;
		}
		if (param2 == null) {
			return (param1 != null) ? param1.toString() : null;
		}
		return param1.toString().concat(param2.toString());
	}
}

