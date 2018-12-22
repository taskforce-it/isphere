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

public final class NotBetween extends PostfixCommand {

    private Between between = new Between();

    final public int getNumberOfParameters() {
        return 3;
    }

    public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
        node.childrenAccept(runtime.ev, null);
        Comparable<?> limit2 = runtime.stack.pop();
        Comparable<?> limit1 = runtime.stack.pop();
        Comparable<?> source = runtime.stack.pop();
        runtime.stack.push(!between.between(limit2, limit1, source));
    }
}
