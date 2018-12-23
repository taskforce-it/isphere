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

import java.util.Calendar;

import org.medfoster.sqljep.ASTFunNode;
import org.medfoster.sqljep.BaseJEP;
import org.medfoster.sqljep.JepRuntime;
import org.medfoster.sqljep.ParserUtils;
import org.medfoster.sqljep.annotations.JUnitTest;
import org.medfoster.sqljep.exceptions.ParseException;
import org.medfoster.sqljep.exceptions.WrongTypeException;

@JUnitTest
public class Time extends PostfixCommand {

    @Override
    final public int getNumberOfParameters() {
        return -1;
    }

    @Override
    public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {

        node.childrenAccept(runtime.ev, null);
        int num = node.jjtGetNumChildren();

        if (num == 1) {
            Comparable<?> param1 = runtime.stack.pop();
            runtime.stack.push(to_time(param1, runtime.calendar));
        } else if (num == 2) {
            Comparable<?> param2 = runtime.stack.pop();
            Comparable<?> param1 = runtime.stack.pop();
            runtime.stack.push(to_time(param1, param2));
        } else {
            // remove all parameters from stack and push null
            removeParams(runtime.stack, num);
            throw new ParseException("Wrong number of parameters for TIME()");
        }
    }

    public java.sql.Time to_time(Comparable<?> expression, Calendar cal) throws ParseException {

        if (expression == null) {
            return null;
        }

        if (expression instanceof java.sql.Timestamp) {

            cal.clear();
            cal.setTime((java.sql.Timestamp)expression);
            cal.set(Calendar.YEAR, 1970);
            cal.set(Calendar.MONTH, 0);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.MILLISECOND, 0);

            return new java.sql.Time(cal.getTimeInMillis());
        } else {
            return to_time(expression, ParserUtils.getTimeFormat((String)expression));
        }
    }

    public java.sql.Time to_time(Comparable<?> expression, Comparable<?> pattern) throws ParseException {

        if (expression == null || pattern == null) {
            return null;
        }

        if (expression instanceof java.sql.Time) {
            return (java.sql.Time)expression;
        } else if (expression instanceof String && pattern instanceof String) {
            try {
                OracleTimeFormat format = new OracleTimeFormat((String)pattern);
                return format.parseObject((String)expression);
            } catch (java.text.ParseException e) {
                if (BaseJEP.debug) {
                    e.printStackTrace();
                }
                throw new ParseException(e.getMessage());
            }
        } else {
            throw new WrongTypeException(getFunctionName(), expression, pattern);
        }
    }
}
