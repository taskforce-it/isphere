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
import org.medfoster.sqljep.datatypes.Months;

@JUnitTest
public class Month extends AbstractDatePortion<Months> {

    public Month() {
        super(Calendar.MONTH);
    }

    @Override
    protected Months createInstance(int value) {
        return new Months(value);
    }
}
