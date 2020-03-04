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

public final class In extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 2;
	}
	
	public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		final int num = node.jjtGetNumChildren();
		node.jjtGetChild(0).jjtAccept(runtime.ev, null);
		Comparable source = runtime.stack.pop();
		if (source == null) {
			runtime.stack.push(Boolean.FALSE);
		} else {
			Node arg = node.jjtGetChild(1);
			if (arg instanceof ASTArray) {
				arg.jjtAccept(runtime.ev, null);
				for (Comparable d : runtime.stack) {
					if (d != null && ComparativeEQ.compareTo(source, d) == 0) {
						runtime.stack.setSize(0);
						runtime.stack.push(Boolean.TRUE);
						return;
					}
				}
				runtime.stack.setSize(0);
				runtime.stack.push(Boolean.FALSE);
			} else {
				throw new ParseException("Internal error in function IN");
			}
		}
	}
}

