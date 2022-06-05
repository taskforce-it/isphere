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
import java.util.Calendar;
import java.util.Date;

import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.annotations.JUnitTest;

@JUnitTest
public final class Add extends AbstractLineCalculation {

    public Add() {
        this.sign = new Integer(1);
    }

    @Override
    protected Comparable<?> add(Calendar calendar, Comparable<?> param1, Comparable<?> param2) throws ParseException {

        if (param1 instanceof java.sql.Date && param2 instanceof java.sql.Time) {
            return composeTimestamp(calendar, (java.sql.Date)param1, (java.sql.Time)param2);
        } else if (param1 instanceof java.sql.Time && param2 instanceof java.sql.Date) {
            return composeTimestamp(calendar, (java.sql.Date)param2, (java.sql.Time)param1);
        } else {
            return super.add(calendar, param1, param2);
        }
    }

    private Date composeTimestamp(Calendar calendar, java.sql.Date date, java.sql.Time time) {

        calendar.clear();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.clear();
        calendar.setTime(time);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        return new java.sql.Timestamp(calendar.getTimeInMillis());
    }

    @Override
    protected BigDecimal performOperation(BigDecimal param1, BigDecimal param2) throws ParseException {
        return param1.add(param2);
    }
}
