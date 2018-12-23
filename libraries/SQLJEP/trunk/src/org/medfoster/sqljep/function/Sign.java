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

import org.medfoster.sqljep.ASTFunNode;
import org.medfoster.sqljep.JepRuntime;
import org.medfoster.sqljep.annotations.JUnitTest;
import org.medfoster.sqljep.exceptions.ParseException;
import org.medfoster.sqljep.exceptions.WrongTypeException;

@JUnitTest
public class Sign extends PostfixCommand {

    @Override
    final public int getNumberOfParameters() {
        return 1;
    }

    @Override
    public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
        node.childrenAccept(runtime.ev, null);
        Comparable<?> param = runtime.stack.pop();
        runtime.stack.push(sign(param));
    }

    public Integer sign(Comparable<?> param) throws ParseException {

        if (param == null) {
            return null;
        }

        if (param instanceof String) {
            param = parse((String)param);
        }

        // BigInteger is not supported

        if (param instanceof BigDecimal) {
            return ((BigDecimal)param).signum();
        }

        if (param instanceof Double || param instanceof Float) {
            double n = ((Number)param).doubleValue();
            return new Integer(n > 0 ? 1 : (n < 0 ? -1 : 0));
        }

        if (param instanceof Number) { // Long, Integer, Short, Byte
            long n = ((Number)param).longValue();
            return new Integer(n > 0 ? 1 : (n < 0 ? -1 : 0));
        }

        throw new WrongTypeException(getFunctionName(), param);
    }
}
