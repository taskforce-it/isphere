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

import org.medfoster.sqljep.ASTArray;
import org.medfoster.sqljep.ASTFunNode;
import org.medfoster.sqljep.JepRuntime;
import org.medfoster.sqljep.Node;
import org.medfoster.sqljep.annotations.JUnitTest;
import org.medfoster.sqljep.exceptions.InternalErrorException;
import org.medfoster.sqljep.exceptions.ParseException;

@JUnitTest
public final class In extends PostfixCommand {

    final public int getNumberOfParameters() {
        return 2;
    }

    public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
        node.jjtGetChild(0).jjtAccept(runtime.ev, null);
        Comparable<?> source = runtime.stack.pop();
        if (source == null) {
            runtime.stack.push(Boolean.FALSE);
        } else {
            Node arg = node.jjtGetChild(1);
            if (arg instanceof ASTArray) {
                arg.jjtAccept(runtime.ev, null);
                for (Comparable<?> d : runtime.stack) {
                    if (d != null && ComparativeEQ.compareTo(source, d) == 0) {
                        runtime.stack.setSize(0);
                        runtime.stack.push(Boolean.TRUE);
                        return;
                    }
                }
                runtime.stack.setSize(0);
                runtime.stack.push(Boolean.FALSE);
            } else {
                throw new InternalErrorException(getFunctionName(), "Expected: ASTArray, got: " + arg.getClass().getName());
            }
        }
    }
}
