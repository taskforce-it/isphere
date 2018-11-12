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
import org.medfoster.sqljep.function.OracleTimestampFormat;
import org.medfoster.sqljep.*;

public class ToDate extends PostfixCommand {
	static final String PARAM_EXCEPTION = "Format shoud be string";
	private static final String FORMAT_EXCEPTION = "Wrong timestamp";
	
	final public int getNumberOfParameters() {
		return -1;
	}
	
	public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		int num = node.jjtGetNumChildren();
		if (num == 1) {
			Comparable param1 = runtime.stack.pop();
			String res;
			if (param1 == null) {
				res = null;
			}
			else if (param1 instanceof String) {
				runtime.stack.push(Timestamp.valueOf((String)param1));
			} else {
				throw new ParseException(FORMAT_EXCEPTION);
			}
		}
		else if (num == 2) {
			Comparable param2 = runtime.stack.pop();
			Comparable param1 = runtime.stack.pop();
			runtime.stack.push(to_date(param1, param2));
		} else {
			// remove all parameters from stack and push null
			removeParams(runtime.stack, num);
			throw new ParseException("Wrong number of parameters for instr");
		}
	}
	
	public static java.util.Date to_date(Comparable param1) throws ParseException {
		if (param1 == null) {
			return null;
		}
		else if (param1 instanceof String) {
			return Timestamp.valueOf((String)param1);
		} else {
			throw new ParseException(FORMAT_EXCEPTION);
		}
	}

	public static java.util.Date to_date(Comparable param1, Comparable param2) throws ParseException {
		if (param1 == null || param2 == null) {
			return null;
		}
		if (param1 instanceof String && ((String)param1).length() == 0) {
			return null;
		}
		if (param1 instanceof java.util.Date) {
			return (java.util.Date)param1;
		}
		if (!(param1 instanceof String) || !(param2 instanceof String)) {
			throw new ParseException(WRONG_TYPE+"  to_date("+param1.getClass()+","+param2.getClass()+")");
		}
		StringBuilder d = new StringBuilder((String)param1);
		try {
			OracleTimestampFormat format = new OracleTimestampFormat((String)param2);
			return (java.util.Date)format.parseObject((String)param1);
		} catch (java.text.ParseException e) {
			if (BaseJEP.debug) {
				e.printStackTrace();
			}
			throw new ParseException(e.getMessage());
		}
	}
}
