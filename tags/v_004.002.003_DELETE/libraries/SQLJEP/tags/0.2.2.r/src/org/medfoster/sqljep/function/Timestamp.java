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
import org.medfoster.sqljep.exceptions.WrongNumberOfParametersException;
import org.medfoster.sqljep.exceptions.WrongTypeException;

@JUnitTest
public class Timestamp extends PostfixCommand {

    public static final String ID = "timestamp";

    @Override
    final public int getNumberOfParameters() {
        return -1;
    }

    @Override
    public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {

        node.childrenAccept(runtime.ev, null);
        int num = node.jjtGetNumChildren();

        if (num == 1) {
            Comparable<?> extression = runtime.stack.pop();
            runtime.stack.push(to_timestamp(extression, ParserUtils.getTimestampFormat((String)extression)));
        } else if (num == 2) {
            Comparable<?> pattern = runtime.stack.pop();
            Comparable<?> expression = runtime.stack.pop();
            runtime.stack.push(to_timestamp(expression, pattern));
        } else {
            // remove all parameters from stack and push null
            removeParams(runtime.stack, num);
            throw new WrongNumberOfParametersException(getFunctionName(), num);
        }
    }

    public java.sql.Timestamp to_timestamp(Comparable<?> expression, Comparable<?> pattern) throws ParseException {

        if (expression == null || pattern == null) {
            return null;
        }

        if (expression instanceof java.sql.Timestamp) {
            return (java.sql.Timestamp)expression;
        } else if (expression instanceof String && pattern instanceof String) {
            try {
                OracleTimestampFormat format = new OracleTimestampFormat((String)pattern);
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
