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

import static java.util.Calendar.*;
import org.medfoster.sqljep.*;
import org.medfoster.sqljep.annotations.JUnitTest;
import org.medfoster.sqljep.datatypes.Hours;

@JUnitTest
public class Hour extends PostfixCommand {
	final public int getNumberOfParameters() {
		return 1;
	}
	
	public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
		node.childrenAccept(runtime.ev, null);
		Comparable param = runtime.stack.pop();
		runtime.stack.push(hour(param, runtime.calendar));
	}

	public static Hours hour(Comparable param, Calendar cal) throws ParseException {

        try {
        
            if (param == null) {
                return null;
            }
            
            if (param instanceof String) {
                try {
                    return new Hours((Integer)parse((String)param));
                } catch (ParseException e) {
                    // eat exception
                }
            }
            
            if (param instanceof String) {
                OracleDateFormat format = new OracleDateFormat(ParserUtils.getDateFormat((String) param));
                param = (Comparable)format.parseObject((String) param);
            }
            
            if (param instanceof Long) {
                return new Hours(((Long)param).intValue());
            }
    
            if (param instanceof java.sql.Time || param instanceof java.sql.Timestamp) {
                java.util.Date ts = (java.util.Date)param;
                cal.setTime(ts);
                return new Hours(cal.get(HOUR_OF_DAY));
            }
        
        } catch (java.text.ParseException e) {
            if (BaseJEP.debug) {
                e.printStackTrace();
            }
            throw new ParseException(e.getMessage());
        }

        throw createWrongTypeException("hour", param);
	}
}
