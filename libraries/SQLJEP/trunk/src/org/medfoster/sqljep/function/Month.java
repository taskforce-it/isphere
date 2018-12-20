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

import org.medfoster.sqljep.ASTFunNode;
import org.medfoster.sqljep.BaseJEP;
import org.medfoster.sqljep.JepRuntime;
import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.ParserUtils;
import org.medfoster.sqljep.annotations.JUnitTest;
import org.medfoster.sqljep.datatypes.Months;

@JUnitTest
public class Month extends PostfixCommand {
    final public int getNumberOfParameters() {
        return 1;
    }

    public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
        node.childrenAccept(runtime.ev, null);
        Comparable<?> param = runtime.stack.pop();
        runtime.stack.push(month(param, runtime.calendar));
    }

    public Months month(Comparable<?> param, Calendar cal) throws ParseException {

        try {

            if (param == null) {
                return null;
            }

            if (param instanceof String) {
                try {
                    return new Months((Integer)parse((String)param));
                } catch (ParseException e) {
                    // eat exception
                }
            }

            if (param instanceof String) {
                OracleDateFormat format = new OracleDateFormat(ParserUtils.getDateFormat((String)param));
                param = format.parseObject((String)param);
            }

            if (param instanceof Long) {
                return new Months(((Long)param).intValue());
            }

            if (param instanceof java.util.Date) {
                java.util.Date ts = (java.util.Date)param;
                cal.setTimeInMillis(ts.getTime());
                return new Months(cal.get(Calendar.MONTH));
            }

        } catch (java.text.ParseException e) {
            if (BaseJEP.debug) {
                e.printStackTrace();
            }
            throw new ParseException(e.getMessage());
        }

        throw createWrongTypeException(param);
    }
}
