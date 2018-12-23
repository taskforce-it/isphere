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
import org.medfoster.sqljep.exceptions.ParseException;

@JUnitTest
public final class Between extends PostfixCommand {

    private ComparativeEQ comparativeEQ = new ComparativeEQ();
    
    final public int getNumberOfParameters() {
        return 3;
    }

    public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {

        node.childrenAccept(runtime.ev, null);
        Comparable<?> limit2 = runtime.stack.pop();
        Comparable<?> limit1 = runtime.stack.pop();
        Comparable<?> source = runtime.stack.pop();

        if (source == null || limit1 == null || limit2 == null) {
            runtime.stack.push(Boolean.FALSE);
        } else {
            runtime.stack.push(between(limit2, limit1, source));
        }
    }

    public boolean between(Comparable<?> limit2, Comparable<?> limit1, Comparable<?> source) throws ParseException {
        return comparativeEQ.compareTo(source, limit1) >= 0 && comparativeEQ.compareTo(source, limit2) <= 0;
    }

}
