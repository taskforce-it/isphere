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
import java.sql.Time;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.medfoster.sqljep.*;

public final class ComparativeEQ extends PostfixCommand {

	private final static int DATE_ISO = 0;
	private final static int DATE_USA = 1;
	private final static int DATE_EUR = 2;

	private final static int TIME_ISO = 0;
	private final static int TIME_USA_AM = 1;
	private final static int TIME_USA_PM = 2;
	private final static int TIME_EUR = 3;

	private final static DateFormat[] dateFormats;
	private final static Pattern datePattern;

	private final static DateFormat[] timeFormats;
	private final static Pattern timePattern;

	static {

		dateFormats = new DateFormat[3];
		dateFormats[DATE_ISO] = new DateFormat("[0-9]{4}-[0-9]{2}-[0-9]{2}", "YYYY-MM-DD");
		dateFormats[DATE_USA] = new DateFormat("[0-9]{2}/[0-9]{2}/[0-9]{4}", "MM/DD/yyyy");
		dateFormats[DATE_EUR] = new DateFormat("[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}", "DD.MM.YYYY");
		datePattern = Pattern.compile(getPattern(dateFormats));

		timeFormats = new DateFormat[4];
		timeFormats[TIME_ISO] = new DateFormat("[0-9]{2}\\.[0-9]{2}\\.[0-9]{2}", "HH24.MI.SS");
		timeFormats[TIME_USA_AM] = new DateFormat("[0-9]{2}:[0-9]{2}\\s*AM", "HH:MI AM");
		timeFormats[TIME_USA_PM] = new DateFormat("[0-9]{2}:[0-9]{2}\\s*PM", "HH:MI PM");
		timeFormats[TIME_EUR] = new DateFormat("[0-9]{2}:[0-9]{2}:[0-9]{2}", "HH24:MI:SS");
		timePattern = Pattern.compile(getPattern(timeFormats), Pattern.CASE_INSENSITIVE);
	}

	private static String getPattern(DateFormat... dateFormats) {

		StringBuilder buffer = new StringBuilder();

		for (DateFormat dateFormat : dateFormats) {
			if (buffer.length() > 0) {
				buffer.append("|");
			}
			buffer.append("(");
			buffer.append(dateFormat.getPattern());
			buffer.append(")");
		}

		return buffer.toString();
	}

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
		if (s1.getClass() == s2.getClass()) {
			return s1.compareTo(s2);
		} else if (s1 instanceof Number || s2 instanceof Number) {
			return compareNumbers(s1, s2);
		} else if (s1 instanceof Time || s2 instanceof Time) {
			return compareTimes(s1, s2);
		} else if (s1 instanceof Date || s2 instanceof Date) {
			return compareDates(s1, s2);
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

	private static int compareDates(Comparable s1, Comparable s2) throws ParseException {

		if (s2 instanceof Date && s1 instanceof String) {
			s1 = ToDate.to_date((String) s1, getDateFormat((String) s1));
		} else if (s1 instanceof Date && s2 instanceof String) {
			s2 = ToDate.to_date((String) s2, getDateFormat((String) s2));
		}
		if (s1 instanceof Date && s2 instanceof Date) {
			return s1.compareTo(s2);
		}

		throw createParseException(s1, s2);
	}

	private static int compareTimes(Comparable s1, Comparable s2) throws ParseException {

		if (s2 instanceof Time && s1 instanceof String) {
			s1 = ((String)s1).replaceAll(" ", "");
			s1 = new Time(ToDate.to_date((String) s1,
					getTimeFormat((String) s1)).getTime());
		} else if (s1 instanceof Date && s2 instanceof String) {
			s2 = ((String)s2).replaceAll(" ", "");
			s2 = new Time(ToDate.to_date((String) s2,
					getTimeFormat((String) s2)).getTime());
		}
		if (s1 instanceof Time && s2 instanceof Time) {
			return s1.compareTo(s2);
		}

		throw createParseException(s1, s2);
	}

	private static String getDateFormat(String string) throws ParseException {

		Matcher matcher = datePattern.matcher(string);
		if (matcher.matches()) {
			for (int i = 0; i < matcher.groupCount(); i++) {
				if (matcher.group(i + 1) != null) {
					return dateFormats[i].getFormat();
				}
			}
		}

		return getTimeFormat(string);
	}

	private static String getTimeFormat(String string) throws ParseException {

		Matcher matcher = timePattern.matcher(string);
		if (matcher.matches()) {
			for (int i = 0; i < matcher.groupCount(); i++) {
				if (matcher.group(i + 1) != null) {
					return timeFormats[i].getFormat();
				}
			}
		}

		throw new ParseException("Unknown date/time format: " + string);
	}

	private static ParseException createParseException(Comparable s1,
			Comparable s2) {

		String text = String.format("Cannot compare '%s' with '%s'.", s1
				.getClass().getSimpleName(), s2.getClass().getSimpleName());

		ParseException parseException = new ParseException(text);

		return parseException;
	}

	private static class DateFormat {

		private String pattern;
		private String format;

		public DateFormat(String pattern, String format) {
			this.pattern = pattern;
			this.format = format;
		}

		public String getPattern() {
			return pattern;
		}

		public String getFormat() {
			return format;
		}
	}
}
