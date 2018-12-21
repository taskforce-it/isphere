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
import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.annotations.JUnitTest;

@JUnitTest
public class Abs extends PostfixCommand {

    final public int getNumberOfParameters() {
        return 1;
    }

    public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
        node.childrenAccept(runtime.ev, null);
        Comparable<?> param = runtime.stack.pop();
        runtime.stack.push(abs(param));
    }

    public Comparable<?> abs(Comparable<?> param) throws ParseException {

        if (param == null) {
            return null;
        }

        if (param instanceof String) {
            param = parse((String)param); // parse Integer
        }

        if (param instanceof BigDecimal) {
            return ((BigDecimal)param).abs();
        } else if (param instanceof Double || param instanceof Float) {
            return new Double(Math.abs(((Number)param).doubleValue()));
        } else if (param instanceof Number) { // Long, Integer, Short, Byte
            return new Long(Math.abs(((Number)param).longValue()));
        }

        throw createWrongTypeException(param);
    }
}
