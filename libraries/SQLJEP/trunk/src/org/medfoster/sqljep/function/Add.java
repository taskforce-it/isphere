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

import org.medfoster.sqljep.annotations.JUnitTest;
import org.medfoster.sqljep.exceptions.ParseException;

@JUnitTest
public final class Add extends AbstractLineCalculation {
    
    public Add() {
        this.sign = new Integer(1);
    }
    
    @Override
    protected BigDecimal performOperation(BigDecimal param1, BigDecimal param2) throws ParseException {
        return param1.add(param2);
    }
}

