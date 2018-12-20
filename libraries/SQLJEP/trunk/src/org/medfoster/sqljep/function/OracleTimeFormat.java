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

import java.text.DateFormatSymbols;
import java.text.ParsePosition;
import java.util.Calendar;

public final class OracleTimeFormat extends AbstractOracleDateTimeFormat {

    private static final long serialVersionUID = -5204467719900951838L;

    public OracleTimeFormat(String pattern) throws java.text.ParseException {
        super(pattern);
    }

    public OracleTimeFormat(String pattern, Calendar calendar, DateFormatSymbols dateSymb) throws java.text.ParseException {
        super(pattern, calendar, dateSymb);
    }

    public java.sql.Time parseObject(String source) {
        return new java.sql.Time(parseInMillis(source, new ParsePosition(0)));
    }

    public Object parseObject(String source, ParsePosition pos) {
        return new java.sql.Time(parseInMillis(source, pos));
    }
}
