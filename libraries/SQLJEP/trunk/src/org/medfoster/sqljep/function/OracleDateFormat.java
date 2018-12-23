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

public final class OracleDateFormat extends AbstractOracleDateTimeFormat {

    private static final long serialVersionUID = -3026029655535820385L;

    public OracleDateFormat(String pattern) throws java.text.ParseException {
        super(pattern);
    }

    public OracleDateFormat(String pattern, Calendar calendar, DateFormatSymbols dateSymb) throws java.text.ParseException {
        super(pattern, calendar, dateSymb);
    }

    @Override
    public java.sql.Date parseObject(String source) {
        return new java.sql.Date(parseInMillis(source, new ParsePosition(0)));
    }

    @Override
    public java.sql.Date parseObject(String source, ParsePosition pos) {
        return new java.sql.Date(parseInMillis(source, pos));
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected java.sql.Date stripMilliSeconds(Comparable time) {
        throw new IllegalAccessError("stripMilliSeconds() is not allowed for " + getClass().getSimpleName());
    }
}
