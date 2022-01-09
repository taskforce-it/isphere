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
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;

public class OracleTimestampFormat extends AbstractOracleDateTimeFormat {

    private static final long serialVersionUID = 9115281574100099055L;

    public OracleTimestampFormat(String pattern) throws ParseException {
        super(pattern);
    }

    public OracleTimestampFormat(String pattern, Calendar calendar, DateFormatSymbols dateSymb) throws java.text.ParseException {
        super(pattern, calendar, dateSymb);
    }

    @Override
    public java.sql.Timestamp parseObject(String source) {
        return new java.sql.Timestamp(parseInMillis(source, new ParsePosition(0)));
    }

    @Override
    public java.sql.Timestamp parseObject(String source, ParsePosition pos) {
        return new java.sql.Timestamp(parseInMillis(source, pos));
    }
}
