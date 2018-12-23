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
import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.annotations.JUnitTest;
import org.medfoster.sqljep.exceptions.WrongNumberOfParametersException;
import org.medfoster.sqljep.exceptions.WrongTypeException;

@JUnitTest
public class Rpad extends PostfixCommand {

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
            runtime.stack.push(rpad(param1, param2, " "));
        } else if (num == 3) {
            Comparable<?> param3 = runtime.stack.pop();
            Comparable<?> param2 = runtime.stack.pop();
            Comparable<?> param1 = runtime.stack.pop();
            runtime.stack.push(rpad(param1, param2, param3));
        } else {
            // remove all parameters from stack and push null
            removeParams(runtime.stack, num);
            throw new WrongNumberOfParametersException(getFunctionName(), num);
        }
    }

    public String rpad(Comparable<?> param1, Comparable<?> param2, Comparable<?> param3) throws ParseException {

        if (param1 == null || param2 == null || param3 == null) {
            return null;
        }

        String inputStr = param1.toString();
        String chars = param3.toString();

        int length;

        try {
            length = getInteger(param2);
        } catch (ParseException e) {
            // Length in rpad shoud be integer
            throw new WrongTypeException(getFunctionName(), param2);
        }

        if (length <= inputStr.length()) {
            return inputStr;
        }

        length -= inputStr.length();
        int count = length / chars.length();
        int remainder = length % chars.length();
        StringBuilder output = new StringBuilder();
        output.append(inputStr);

        for (int i = 0; i < count; i++) {
            output.append(chars);
        }

        output.append(chars.substring(0, remainder));

        return output.toString();
    }
}
