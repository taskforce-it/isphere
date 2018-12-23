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

public final class ComparativeGE extends PostfixCommand {

    private ComparativeEQ comparativeEQ = new ComparativeEQ();

    @Override
    final public int getNumberOfParameters() {
        return 2;
    }

    @Override
    public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
        node.childrenAccept(runtime.ev, null);
        Comparable<?> param2 = runtime.stack.pop();
        Comparable<?> param1 = runtime.stack.pop();
        if (param1 == null || param2 == null) {
            runtime.stack.push(Boolean.FALSE);
        } else {
            runtime.stack.push(comparativeEQ.compareTo(param1, param2) >= 0);
        }
    }
}
