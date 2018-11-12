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

public class Initcap extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 1;
	}
	
	public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable param = runtime.stack.pop();
		runtime.stack.push(initcap(param));		//push the result on the inStack
	}

	public static String initcap(Comparable param) throws ParseException {
		if (param == null) {
			return null;
		}
		String inputStr = param.toString();
		StringBuilder output = new StringBuilder(inputStr);
		boolean needUpper = true;
		final int len = inputStr.length();
		for (int i = 0; i < len; i++) {
			char c = inputStr.charAt(i);
			if (needUpper && Character.isLetter(c)) {
				if (Character.isLowerCase(c)) {
					output.setCharAt(i, Character.toUpperCase(c));
				}
				needUpper = false;
			}
			else if (!needUpper) {
				if (!Character.isLetterOrDigit(c)) {
					needUpper = true;
				}
				else if (Character.isUpperCase(c)) {
					output.setCharAt(i, Character.toLowerCase(c));
				}
			}
		}
		return output.toString();
	}
}

