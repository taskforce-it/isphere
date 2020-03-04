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

import java.util.Calendar;

import org.medfoster.sqljep.annotations.JUnitTest;
import org.medfoster.sqljep.datatypes.Days;

@JUnitTest
public class Day extends AbstractDatePortion<Days> {
    
    public static final String ID = "day";

    public Day() {
        super(Calendar.DAY_OF_MONTH);
    }

    @Override
    protected Days createInstance(int value) {
        return new Days(value);
    }
}
