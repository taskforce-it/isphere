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

public final class ComparativeEQ extends PostfixCommand {

	final public int getNumberOfParameters() {
		return 2;
	}
	
	public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable param2 = runtime.stack.pop();
		Comparable param1 = runtime.stack.pop();
		if (param1 == null || param2 == null) {
			runtime.stack.push(Boolean.FALSE);
		} else {
			runtime.stack.push(compareTo(param1, param2) == 0);
		}
	}

	public static int compareTo(Comparable s1, Comparable s2) throws ParseException {

		if (s1 instanceof Number || s2 instanceof Number) {
			return compareNumbers(s1, s2);
		} else if (s1 instanceof java.util.Date || s2 instanceof java.util.Date) {
			return compareDatesAndTimes(s1, s2);
		} else if (s1.getClass() == s2.getClass()) {
			return s1.compareTo(s2);
		}

		throw createParseException(s1, s2);
	}

	private static int compareNumbers(Comparable s1, Comparable s2) throws ParseException {

		if (s2 instanceof Number && s1 instanceof String) {
			s1 = parse((String)s1);
		} 
		else if (s1 instanceof Number && s2 instanceof String) {
			s2 = parse((String)s2);
		}
		if (s1 instanceof Number && s2 instanceof Number) {
			Number n1 = (Number) s1;
			Number n2 = (Number) s2;
			if (n1 instanceof BigDecimal || n2 instanceof BigDecimal) {		// BigInteger
				BigDecimal d1 = getBigDecimal(n1);
				BigDecimal d2 = getBigDecimal(n2);
				return d1.compareTo(d2);
			}
			else if (n1 instanceof Double || n2 instanceof Double || n1 instanceof Float || n2 instanceof Float) {
				return Double.compare(n1.doubleValue(), n2.doubleValue());
			} else { // Long, Integer, Short, Byte
				long thisVal = n1.longValue();
				long anotherVal = n2.longValue();
				return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
			}
		}

		throw createParseException(s1, s2);
	}

	private static int compareDatesAndTimes(Comparable s1, Comparable s2) throws ParseException {

		try {
			
			int reverseOperation = 1;
			
			if (s2 instanceof java.util.Date && s1 instanceof String) {
				Comparable s1Old = s1; 
				s1 = s2;
				s2 = s1Old;
				reverseOperation = -1;
			}
	
		    
            if (s1 instanceof java.sql.Timestamp && s2 instanceof String) {
                OracleTimestampFormat format = new OracleTimestampFormat(ParserUtils.getTimestampFormat((String) s2));
                s2 = (Comparable)format.parseObject((String) s2);
                if (!format.hasMilliSeconds()){
                    s1 = format.stripMilliSeconds(s1);
                }
            } else if (s1 instanceof java.sql.Time && s2 instanceof String) {
                OracleTimeFormat format = new OracleTimeFormat(ParserUtils.getTimeFormat((String) s2));
                s2 = (Comparable)format.parseObject((String) s2);
                if (!format.hasMilliSeconds()){
                    s1 = format.stripMilliSeconds(s1);
                }
            } else if (s1 instanceof java.sql.Date && s2 instanceof String) {
                OracleDateFormat format = new OracleDateFormat(ParserUtils.getDateFormat((String) s2));
                s2 = (Comparable)format.parseObject((String) s2);
            } else if (s1 instanceof java.util.Date && s2 instanceof String) {
                throw new IllegalArgumentException("Did not expect java.util.Date");
//				OracleDateFormat format = new OracleDateFormat(ParserUtils.getDateFormat((String) s2));
//				s2 = (Comparable)format.parseObject((String) s2);
			}
			
			if (s1 instanceof java.util.Date && s2 instanceof java.util.Date) {
				return s1.compareTo(s2) * reverseOperation;
			}
		
		} catch (java.text.ParseException e) {
			if (BaseJEP.debug) {
				e.printStackTrace();
			}
			throw new ParseException(e.getMessage());
		}

		throw createParseException(s1, s2);
	}

	private static ParseException createParseException(Comparable s1,
			Comparable s2) {

		String text = String.format("Cannot compare '%s' with '%s'.", s1
				.getClass().getSimpleName(), s2.getClass().getSimpleName());

		ParseException parseException = new ParseException(text);

		return parseException;
	}
}
