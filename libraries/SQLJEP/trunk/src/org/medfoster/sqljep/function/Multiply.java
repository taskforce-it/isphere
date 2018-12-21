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
import org.medfoster.sqljep.exceptions.WrongTypeException;

@JUnitTest
public final class Multiply extends PostfixCommand {

    final public int getNumberOfParameters() {
        return 2;
    }

    public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
        node.childrenAccept(runtime.ev, null);
        Comparable<?> param2 = runtime.stack.pop();
        Comparable<?> param1 = runtime.stack.pop();
        runtime.stack.push(mul(param1, param2));
    }

    public Comparable<?> mul(Comparable<?> param1, Comparable<?> param2) throws ParseException {

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

            if (param1 instanceof Double || param2 instanceof Double || param1 instanceof Float || param2 instanceof Float) {
                return ((Number)param1).doubleValue() * ((Number)param2).doubleValue();
            }

            BigDecimal b1 = getBigDecimal((Number)param1);
            BigDecimal b2 = getBigDecimal((Number)param2);

            return b1.multiply(b2);

        } else {
            throw new WrongTypeException(getFunctionName(), param1, param2);
        }
    }
}
