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
public class Ceil extends PostfixCommand {

    public static final String ID = "ceil";

    @Override
    final public int getNumberOfParameters() {
        return 1;
    }

    @Override
    public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
        node.childrenAccept(runtime.ev, null);
        Comparable<?> param = runtime.stack.pop();
        runtime.stack.push(ceil(param));
    }

    public Comparable<?> ceil(Comparable<?> param) throws ParseException {

        if (param == null) {
            return null;
        }

        if (param instanceof String) {
            param = parse((String)param);
        }

        // BigInteger is not supported

        if (param instanceof BigDecimal) {
            BigDecimal b = ((BigDecimal)param).setScale(0, BigDecimal.ROUND_CEILING);
            try {
                return b.longValueExact();
            } catch (ArithmeticException e) {
            }
            return b;
        } else if (param instanceof Double || param instanceof Float) {
            // Is that path really execute?
            // Floating point values are converted to BigDecimal
            // in org.medfoster.sqljep.Parser.
            return Math.ceil(((Number)param).doubleValue());
        } else if (param instanceof Number) { // Long, Integer, Short, Byte
            return param;
        }

        throw new WrongTypeException(getFunctionName(), param);
    }
}
