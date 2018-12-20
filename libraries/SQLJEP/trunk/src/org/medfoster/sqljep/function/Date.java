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

import org.medfoster.sqljep.ASTFunNode;
import org.medfoster.sqljep.BaseJEP;
import org.medfoster.sqljep.JepRuntime;
import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.ParserUtils;
import org.medfoster.sqljep.annotations.JUnitTest;

@JUnitTest
public class Date extends PostfixCommand {

    final public int getNumberOfParameters() {
        return -1;
    }

    public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {

        node.childrenAccept(runtime.ev, null);
        int num = node.jjtGetNumChildren();

        if (num == 1) {
            Comparable<?> param1 = runtime.stack.pop();
            runtime.stack.push(to_date(param1, ParserUtils.getDateFormat((String)param1)));
        } else if (num == 2) {
            Comparable<?> param2 = runtime.stack.pop();
            Comparable<?> param1 = runtime.stack.pop();
            runtime.stack.push(to_date(param1, param2));
        } else {
            // remove all parameters from stack and push null
            removeParams(runtime.stack, num);
            throw new ParseException("Wrong number of parameters for DATE()");
        }
    }

    public java.sql.Date to_date(Comparable<?> expression, Comparable<?> pattern) throws ParseException {

        if (expression == null || pattern == null) {
            return null;
        }

        if (expression instanceof java.util.Date) {
            return new java.sql.Date(((java.util.Date)expression).getTime());
        } else if (expression instanceof String && pattern instanceof String) {
            try {
                OracleDateFormat format = new OracleDateFormat((String)pattern);
                return format.parseObject((String)expression);
            } catch (java.text.ParseException e) {
                if (BaseJEP.debug) {
                    e.printStackTrace();
                }
                throw new ParseException(e.getMessage());
            }
        } else {
            throw createWrongTypeException(expression, pattern);
        }
    }
}
