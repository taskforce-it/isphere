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
import java.util.Stack;

import org.medfoster.sqljep.ASTFunNode;
import org.medfoster.sqljep.JepRuntime;
import org.medfoster.sqljep.exceptions.ParseException;

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

    protected static final Integer ZERO = new Integer(0);

    public static final String PARAMS_NUMBER = "Wrong number of parameters";
    public static final String WRONG_TYPE = "Wrong type";
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

    protected final void removeParams(Stack<Comparable<?>> s, int num) {
        int i = 1;
        s.pop();
        while (i < num) {
            s.pop();
            i++;
        }
        s.push(null);
    }

    /**
     * Parses a String and returns an Integer or BigDecimal.
     * 
     * @param expression - Expression that is parsed
     * @return Integer or BigDecimal
     * @throws ParseException
     */
    public static Comparable<?> parse(String expression) throws ParseException {

        try {
            return Integer.valueOf((String)expression);
        } catch (NumberFormatException e) {
            try {
                BigDecimal d = new BigDecimal((String)expression);
                if (d.scale() < 0) {
                    d = d.setScale(0);
                }
                return d;
            } catch (Throwable ex) {
                throw new ParseException(String.format("Expression '%s' cannot be parsed to an Integer or BigDecimal", expression));
            }
        }
    }

    /**
     * Converts a Number to a BigDecimal.
     * 
     * @param param - Number that is converted to a BigDecimal.
     * @return BigDecimal
     */
    public static BigDecimal getBigDecimal(Number param) {

        if (param instanceof BigDecimal) {
            return (BigDecimal)param;
        }

        if (param instanceof Double || param instanceof Float) {
            return new BigDecimal(param.doubleValue());
        }

        return new BigDecimal(param.longValue());
    }

    /**
     * Converts a Number or String to an Integer.
     * 
     * @param param - Comparable that is converted to a Number or BigDecimal.
     * @return Integer
     */
    public static int getInteger(Comparable<?> param) throws ParseException {

        if (param instanceof Number) {
            return ((Number)param).intValue();
        } else if (param instanceof String) {
            String expression = (String)param;
            try {
                BigDecimal d = new BigDecimal(expression);
                return d.intValueExact();
            } catch (Throwable e) {
                throw new ParseException(String.format("Expression '%s' cannot be parsed to an Integer", expression));
            }
        }

        throw new ParseException("Parameter is not a Number or String: " + (param != null ? param.getClass().getName() : "null"));
    }

    protected String getFunctionName() {
        return getClass().getSimpleName();
    }
}
