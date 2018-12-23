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
import org.medfoster.sqljep.exceptions.UnexpectedTypeException;
import org.medfoster.sqljep.exceptions.WrongNumberOfParametersException;

@JUnitTest
public class Replace extends PostfixCommand {

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
            runtime.stack.push(replace(param1, param2, ""));
        } else if (num == 3) {
            Comparable<?> param3 = runtime.stack.pop();
            Comparable<?> param2 = runtime.stack.pop();
            Comparable<?> param1 = runtime.stack.pop();
            runtime.stack.push(replace(param1, param2, param3));
        } else {
            // remove all parameters from stack
            removeParams(runtime.stack, num);
            throw new WrongNumberOfParametersException(getFunctionName(), num);
        }
    }

    public String replace(Comparable<?> param1, Comparable<?> param2, Comparable<?> param3) throws ParseException {

        if (param1 == null || param2 == null) {
            return null;
        }

        if (!isSupportedType(param1)) {
            throw new UnexpectedTypeException(getFunctionName(), "source-string", "String|Number");
        }

        if (!isSupportedType(param2)) {
            throw new UnexpectedTypeException(getFunctionName(), "search-string", "String|Number");
        }

        if (!isSupportedType(param3)) {
            throw new UnexpectedTypeException(getFunctionName(), "replace-string", "String|Number");
        }

        String source = (String)param1;
        String search = (String)param2;
        String replace = (String)param3;

        return source.replaceAll(search, replace);
    }

    protected boolean isSupportedType(Object object) {

        if (object instanceof String || object instanceof Number) {
            return true;
        }

        return false;
    }
}
