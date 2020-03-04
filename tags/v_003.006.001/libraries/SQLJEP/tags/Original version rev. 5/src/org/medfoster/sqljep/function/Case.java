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

public final class Case extends PostfixCommand {
	final public int getNumberOfParameters() {
		return -1;
	}
	
	public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		int num = node.jjtGetNumChildren();
		if (num > 1) {
			boolean elseCase;
			if (num % 2 != 0) {
				elseCase = true;
				num--;
			} else {
				elseCase = false;
			}
			int result = -1;
			for (int i = 0; i < num; i += 2) {
				node.jjtGetChild(i).jjtAccept(runtime.ev, null);
				Comparable cond = runtime.stack.pop();
				if (cond instanceof Boolean) {
					if (((Boolean)cond).booleanValue()) {
						result = i;
						break;
					}
				} else {
					throw new ParseException("In case only boolean is possible as condition. Found: "+(cond != null ? cond.getClass() : "NULL"));
				}
			}
			if (result < 0 && elseCase) {
				result = num-1;
			}
			if (result >= 0) {
				node.jjtGetChild(result+1).jjtAccept(runtime.ev, null);
				Comparable variant = runtime.stack.pop();
				runtime.stack.push(variant);
			} else {
				runtime.stack.push(null);
			}
		} else {
			throw new ParseException("Few arguments for case");
		}
	}
}
