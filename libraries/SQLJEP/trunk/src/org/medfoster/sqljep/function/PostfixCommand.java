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
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;

import org.medfoster.sqljep.ASTFunNode;
import org.medfoster.sqljep.JepRuntime;
import org.medfoster.sqljep.ParseException;

/**
 * Function classes extend this class. It is an implementation of the
 * PostfixMathCommandI interface.
 * <p>
 * It includes a numberOfParameters member, that is checked when parsing the
 * expression. This member should be initialized to an appropriate value for all
 * classes extending this class. If an arbitrary number of parameters should be
 * allowed, initialize this member to -1.
 */
public abstract class PostfixCommand implements PostfixCommandI {
    private static Calendar calendar = Calendar.getInstance();
    protected static final Integer ZERO = new Integer(0);
    public static final String PARAMS_NUMBER = "Wrong number of parameters";
    public static final String WRONG_TYPE = "Wrong type";
    public static final String INTERNAL_ERROR = "Internal error";
    public static final String NOT_IMPLIMENTED_EXCEPTION = "Not implimented";

    /**
     * Return the required number of parameters. Number of parameters a the
     * function requires. Initialize this value to -1 if any number of
     * parameters should be allowed.
     */
    abstract public int getNumberOfParameters();

    /**
     * Throws an exception because this method should never be called under
     * normal circumstances. Each function should use it's own run() method for
     * evaluating the function. This includes popping off the parameters from
     * the stack, and pushing the result back on the stack.
     */
    abstract public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException;

    protected final void removeParams(Stack<Comparable> s, int num) {
        int i = 1;
        s.pop();
        while (i < num) {
            s.pop();
            i++;
        }
        s.push(null);
    }

    public static Comparable parse(String param) throws ParseException {
        try {
            return Integer.valueOf((String)param);
        } catch (NumberFormatException e) {
            try {
                BigDecimal d = new BigDecimal((String)param);
                if (d.scale() < 0) {
                    d = d.setScale(0);
                }
                return d;
            } catch (NumberFormatException ex) {
                throw new ParseException("Not a number");
            }
        }
    }

    public static BigDecimal getBigDecimal(Number param) {
        // BigInteger is not supported
        if (param instanceof BigDecimal) {
            return (BigDecimal)param;
        }
        if (param instanceof Double || param instanceof Float) {
            return new BigDecimal(param.doubleValue());
        }
        return new BigDecimal(param.longValue());
    }

    public static int getInteger(Comparable param) throws ParseException {
        if (param instanceof Number) {
            return ((Number)param).intValue();
        } else if (param instanceof String) {
            try {
                BigDecimal d = new BigDecimal((String)param);
                return d.intValueExact();
            } catch (Exception e) {
                throw new ParseException("Cant parse integer: '" + (String)param + "'");
            }
        }
        throw new ParseException("Not Integer: " + (param != null ? param.getClass() : "null"));
    }

    public static double getDouble(Comparable param) throws ParseException {
        if (param instanceof Number) {
            Number n = (Number)param;
            return n.doubleValue();
        } else if (param instanceof String) {
            try {
                return Double.valueOf((String)param);
            } catch (NumberFormatException e) {
                throw new ParseException("Cant parse double: '" + (String)param + "'");
            }
        }
        throw new ParseException("Not Double: " + (param != null ? param.getClass() : "null"));
    }

    protected ParseException createErrorException(String function, Comparable...comparables) {
        return createErrorException(function, null, comparables);
    }

    protected ParseException createErrorException(String function, Throwable e, Comparable...comparables) {
        return createExceptionInternally(INTERNAL_ERROR, function, comparables, e);
    }

    protected ParseException createWrongTypeException(Comparable...comparables) {
        return createWrongTypeException(getClass().getSimpleName(), null, comparables);
    }

    private ParseException createWrongTypeException(String function, Comparable...comparables) {
        return createWrongTypeException(function, null, comparables);
    }

    private ParseException createWrongTypeException(String function, Throwable e, Comparable...comparables) {
        return createExceptionInternally(WRONG_TYPE, function, comparables, e);
    }
    
    protected static Date addDays(Date time, int days) {
        
        calendar.clear();
        calendar.setTime((Date)time);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        
        return calendar.getTime();
    }
    
    protected static Date addMonths(Date time, int months) {
        
        calendar.clear();
        calendar.setTime((Date)time);
        calendar.add(Calendar.MONTH, months);
        
        return calendar.getTime();
    }
    
    protected static Date addYears(Date time, int years) {
        
        calendar.clear();
        calendar.setTime((Date)time);
        calendar.add(Calendar.YEAR, years);
        
        return calendar.getTime();
    }
    
    protected static Date addHours(Date time, int hours) {
        
        calendar.clear();
        calendar.setTime((Date)time);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        
        return calendar.getTime();
    }

    private static ParseException createExceptionInternally(String type, String function, Comparable[] comparables, Throwable e) {

        boolean isFirstComparable = true;

        StringBuilder buffer = new StringBuilder();
        buffer.append(type);
        buffer.append("  ");
        buffer.append(function);
        buffer.append("(");
        for (Comparable comparable : comparables) {
            if (isFirstComparable) {
                isFirstComparable = false;
            } else {
                buffer.append(",");
            }
            buffer.append(comparable.getClass());
        }
        buffer.append(")");

        if (e != null) {
            buffer.append(": ");
            String localizedMessage = e.getLocalizedMessage();
            if (localizedMessage == null) {
                localizedMessage = e.getClass().getSimpleName();
            }
            buffer.append(localizedMessage);
        }

        return new ParseException(buffer.toString());
    }
}
