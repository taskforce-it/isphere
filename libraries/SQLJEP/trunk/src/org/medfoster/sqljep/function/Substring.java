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
import org.medfoster.sqljep.JepRuntime;
import org.medfoster.sqljep.annotations.JUnitTest;
import org.medfoster.sqljep.exceptions.InternalErrorException;
import org.medfoster.sqljep.exceptions.ParseException;
import org.medfoster.sqljep.exceptions.UnexpectedTypeException;
import org.medfoster.sqljep.exceptions.WrongNumberOfParametersException;
import org.medfoster.sqljep.exceptions.WrongValueException;

@JUnitTest
public class Substring extends PostfixCommand {

    @Override
    final public int getNumberOfParameters() {
        return -1;
    }

    @Override
    public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
        node.childrenAccept(runtime.ev, null);
        int num = node.jjtGetNumChildren();
        if (num == 2) {
            Comparable<?> param2 = runtime.stack.pop();
            Comparable<?> param1 = runtime.stack.pop();
            runtime.stack.push(substring(param1, param2, null));
        } else if (num == 3) {
            Comparable<?> param3 = runtime.stack.pop();
            Comparable<?> param2 = runtime.stack.pop();
            Comparable<?> param1 = runtime.stack.pop();
            runtime.stack.push(substring(param1, param2, param3));
        } else {
            // remove all parameters from stack and push null
            removeParams(runtime.stack, num);
            throw new WrongNumberOfParametersException(getFunctionName(), num);
        }
    }

    public String substring(Comparable<?> param1, Comparable<?> param2, Comparable<?> param3) throws ParseException {

        if (param1 == null) {
            return null;
        }

        String expression = param1.toString();
        if (expression.length() == 0) {
            return expression;
        }

        int start;

        try {
            start = getInteger(param2);
        } catch (ParseException e) {
            throw new UnexpectedTypeException(getFunctionName(), "start", "Integer");
        }

        if (start < 0) {
            throw new WrongValueException(getFunctionName(), "start", start);
        }

        if (start > expression.length()) {
            return "";
        }

        start--;

        int length;
        if (param3 == null) {
            length = expression.length() - start;
        } else {
            try {
                length = getInteger(param3);
                if (start + length > expression.length()) {
                    length = expression.length() - start;
                }
            } catch (ParseException e) {
                throw new UnexpectedTypeException(getFunctionName(), "length", "Integer");
            }
        }

        if (length < 0) {
            throw new WrongValueException(getFunctionName(), "length", length);
        }

        try {
            return expression.substring(start, start + length);
        } catch (StringIndexOutOfBoundsException e) {
            throw new InternalErrorException(getFunctionName(), e);
        }
    }
}
