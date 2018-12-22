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
import org.medfoster.sqljep.exceptions.ParseException;
import org.medfoster.sqljep.exceptions.WrongTypeException;

public final class NotLike extends PostfixCommand {

    private Like like = new Like();

    final public int getNumberOfParameters() {
        return 2;
    }

    public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
        node.childrenAccept(runtime.ev, null);
        Comparable<?> param2 = runtime.stack.pop();
        Comparable<?> param1 = runtime.stack.pop();
        runtime.stack.push(not_like(param1, param2));
    }

    public boolean not_like(Comparable<?> param1, Comparable<?> param2) throws ParseException {

        if (validateParameters(param1, param2)) {
            String source = (String)param1;
            String match = (String)param2;
            return !like.like(String.format("/%s/", source), match);
        }

        throw new WrongTypeException(getFunctionName(), param1, param2);
    }

    public boolean validateParameters(Comparable<?> param1, Comparable<?> param2) {
        return like.validateParameters(param1, param2);
    }
}
