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

@JUnitTest
public class Trim extends PostfixCommand {

    private Rtrim rtrim = new Rtrim();
    private Ltrim ltrim = new Ltrim();

    final public int getNumberOfParameters() {
        return -1;
    }

    public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
        node.childrenAccept(runtime.ev, null);
        int num = node.jjtGetNumChildren();
        if (num == 1) {
            Comparable<?> param1 = runtime.stack.pop();
            runtime.stack.push(trim(param1, null));
        } else if (num == 2) {
            Comparable<?> param2 = runtime.stack.pop();
            Comparable<?> param1 = runtime.stack.pop();
            runtime.stack.push(trim(param1, param2));
        } else {
            // remove all parameters from stack and push null
            removeParams(runtime.stack, num);
            throw new WrongNumberOfParametersException(num);
        }
    }

    public String trim(Comparable<?> param1, Comparable<?> param2) throws ParseException {

        if (param1 == null) {
            return null;
        }

        if (param2 == null) {
            return param1.toString().trim();
        }

        param1 = rtrim.rtrim(param1, param2);

        return ltrim.ltrim(param1, param2);
    }
}
