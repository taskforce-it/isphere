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
import java.util.Calendar;

import org.medfoster.sqljep.*;
import org.medfoster.sqljep.annotations.JUnitTest;
import org.medfoster.sqljep.datatypes.Days;
import org.medfoster.sqljep.datatypes.Months;
import org.medfoster.sqljep.datatypes.Years;

@JUnitTest
public final class Add extends PostfixCommand {
	
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
		runtime.stack.push(add(param1, param2));
	}

	public static Comparable add(Comparable param1, Comparable param2) throws ParseException {
		if (param1 == null || param2 == null) {
			return null;
		}
		if (param1 instanceof String) {
			param1 = parse((String)param1);
		}
		if (param2 instanceof String) {
			param2 = parse((String)param2);
		}
		if (param1 instanceof Number && param2 instanceof Number) {
			// BigInteger type is not supported
			Number n1 = (Number)param1;
			Number n2 = (Number)param2;
			if (n1 instanceof BigDecimal || n2 instanceof BigDecimal) {
				BigDecimal b1 = getBigDecimal(n1);
				BigDecimal b2 = getBigDecimal(n2);
				return b1.add(b2);
			}
			if (n1 instanceof Double || n2 instanceof Double || n1 instanceof Float || n2 instanceof Float) {
				return n1.doubleValue() + n2.doubleValue();
			} else {	// Long, Integer, Short, Byte 
				long l1 = n1.longValue();
				long l2 = n2.longValue();
				long r = l1 + l2;
				if (l1 <= r && l2 <= r) {		// overflow check
					return r;
				} else {
					BigDecimal b1 = new BigDecimal(l1);
					BigDecimal b2 = new BigDecimal(l2);
					return b1.add(b2);
				}
			}
		}
		else if (param1 instanceof java.util.Date || param2 instanceof java.util.Date) {
			if (param1 instanceof java.util.Date && param2 instanceof java.util.Date) {
	            throw createWrongTypeException("+", param1, param2);
			}
			java.util.Date d;
			
			if (param2 instanceof java.util.Date) {
			    Comparable param2Old = param2;
			    param2 = param1;
			    param1 = param2Old;
			}
			
			if (param1 instanceof java.util.Date) {
				d = (java.util.Date)param1;
				if (param2 instanceof Days) {
					Days n = (Days)param2;
	                return addDays(d, n.intValue());
				} else if (param2 instanceof Months) {
				    Months n = (Months)param2;
				    return addMonths(d, n.intValue());
                } else if (param2 instanceof Years) {
                    Years n = (Years)param2;
                    return addYears(d, n.intValue());
				} else {
		            throw createWrongTypeException("+", param1, param2);
				}
			}
			throw new ParseException(INTERNAL_ERROR);
		} else {
			throw createWrongTypeException("+", param1, param2);
		}
	}
}

