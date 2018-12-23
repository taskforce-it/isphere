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

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Calendar;

import org.medfoster.sqljep.ASTFunNode;
import org.medfoster.sqljep.JepRuntime;
import org.medfoster.sqljep.annotations.JUnitTest;
import org.medfoster.sqljep.exceptions.InternalErrorException;
import org.medfoster.sqljep.exceptions.ParseException;
import org.medfoster.sqljep.exceptions.WrongNumberOfParametersException;
import org.medfoster.sqljep.exceptions.WrongTypeException;
import org.medfoster.sqljep.function.AbstractOracleDateTimeFormat.DATE;
import org.medfoster.sqljep.function.AbstractOracleDateTimeFormat.DD;
import org.medfoster.sqljep.function.AbstractOracleDateTimeFormat.HH12;
import org.medfoster.sqljep.function.AbstractOracleDateTimeFormat.HH24;
import org.medfoster.sqljep.function.AbstractOracleDateTimeFormat.MI;
import org.medfoster.sqljep.function.AbstractOracleDateTimeFormat.MM;
import org.medfoster.sqljep.function.AbstractOracleDateTimeFormat.NNN;
import org.medfoster.sqljep.function.AbstractOracleDateTimeFormat.SS;
import org.medfoster.sqljep.function.AbstractOracleDateTimeFormat.YYYY;

@JUnitTest
public class Round extends PostfixCommand {

    final public int getNumberOfParameters() {
        return -1;
    }

    public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
        node.childrenAccept(runtime.ev, null);
        int num = node.jjtGetNumChildren();
        if (num == 1) {
            Comparable<?> param1 = runtime.stack.pop();
            runtime.stack.push(round(param1, runtime.calendar));
        } else if (num == 2) {
            Comparable<?> param2 = runtime.stack.pop();
            Comparable<?> param1 = runtime.stack.pop();
            runtime.stack.push(round(param1, param2, runtime.calendar));
        } else {
            // remove all parameters from stack and push null
            removeParams(runtime.stack, num);
            throw new WrongNumberOfParametersException(getFunctionName(), num);
        }
    }

    public Comparable<?> round(Comparable<?> param, Calendar cal) throws ParseException {

        if (param == null) {
            return null;
        }

        if (param instanceof String || param instanceof BigDecimal || param instanceof Double || param instanceof Float || param instanceof Number) {
            return round(param, 0, cal);
        }

        throw new WrongTypeException(getFunctionName(), param);
    }

    public Comparable<?> round(Comparable<?> param1, Comparable<?> param2, Calendar cal) throws ParseException {

        if (param1 == null || param2 == null) {
            return null;
        }

        if (param1 instanceof String) {
            param1 = parse((String)param1);
        }

        // if (param1 instanceof java.sql.Time) {
        // throw new WrongTypeException(getFunctionName(), param1, param2);
        // }

        try {

            if (param1 instanceof Number) {

                int scale;

                try {
                    scale = getInteger(param2);
                } catch (ParseException e) {
                    // The expected type is: Integer
                    throw new WrongTypeException(getFunctionName(), param1, param2);
                }

                if (param1 instanceof BigDecimal) {
                    return ((BigDecimal)param1).setScale(scale, BigDecimal.ROUND_HALF_UP);
                }

                if (param1 instanceof Double || param1 instanceof Float) {
                    double d = ((Number)param1).doubleValue();
                    long mult = 1;
                    for (int i = 0; i < scale; i++) {
                        mult *= 10;
                    }
                    d *= mult;
                    return (Math.round(d)) / mult;
                }

                if (param1 instanceof Number) { // Long, Integer, Short, Byte
                    return param1;
                }

                throw new WrongTypeException(getFunctionName(), param1, param2);

            } else if (param1 instanceof java.util.Date) {

                // param1 = java.sql.Date or java.sql.Time or java.sql.Timestamp
                // param2 = a calendar field
                if (param2 instanceof String) {

                    DATE dateFormat = AbstractOracleDateTimeFormat.findFormat((String)param2);

                    java.util.Date d = (java.util.Date)param1;
                    cal.setTimeInMillis(d.getTime());

                    if (dateFormat instanceof YYYY) {

                        if (!isDateOrTimestamp(param1)) {
                            throw new WrongTypeException(getFunctionName(), param1, dateFormat);
                        }

                        int year = cal.get(YEAR);
                        int month = cal.get(MONTH);

                        /*
                         * Year (Rounds up on July 1 to January 1st of the next
                         * year)
                         */
                        if (month >= 6) { // 6 = July
                            year++;
                        }

                        cal.clear();
                        cal.set(year, 0, 1);

                        Comparable<?> date = createObjectInstance(param1, cal);

                        return date;

                    } else if (dateFormat instanceof MM) {

                        if (!isDateOrTimestamp(param1)) {
                            throw new WrongTypeException(getFunctionName(), param1, dateFormat);
                        }

                        int year = cal.get(YEAR);
                        int month = cal.get(MONTH);
                        int day = cal.get(DAY_OF_MONTH);

                        if (day >= 16) {
                            month++;
                        }

                        cal.clear();
                        cal.set(year, month, 1);

                        Comparable<?> date = createObjectInstance(param1, cal);

                        return date;

                    } else if (dateFormat instanceof DD) {

                        if (!isDateOrTimestamp(param1)) {
                            throw new WrongTypeException(getFunctionName(), param1, dateFormat);
                        }

                        if (!isTimeOrTimestamp(param1)) {
                            throw new WrongTypeException(getFunctionName(), param1, dateFormat);
                        }

                        int hour = cal.get(HOUR_OF_DAY);

                        if (hour >= 12) {
                            cal.add(DAY_OF_MONTH, 1);
                        }

                        cal.set(HOUR_OF_DAY, 0);
                        cal.set(MINUTE, 0);
                        cal.set(SECOND, 0);
                        cal.set(MILLISECOND, 0);

                        Comparable<?> date = createObjectInstance(param1, cal);

                        return date;

                    } else if ((dateFormat instanceof HH12) || (dateFormat instanceof HH24)) {

                        if (!isTimeOrTimestamp(param1)) {
                            throw new WrongTypeException(getFunctionName(), param1, dateFormat);
                        }

                        int minute = cal.get(MINUTE);

                        if (minute >= 30) {
                            cal.add(HOUR_OF_DAY, 1);
                        }

                        cal.set(MINUTE, 0);
                        cal.set(SECOND, 0);
                        cal.set(MILLISECOND, 0);

                        Comparable<?> date = createObjectInstance(param1, cal);

                        return date;

                    } else if (dateFormat instanceof MI) {

                        if (!isTimeOrTimestamp(param1)) {
                            throw new WrongTypeException(getFunctionName(), param1, dateFormat);
                        }

                        int second = cal.get(SECOND);

                        if (second >= 30) {
                            cal.add(MINUTE, 1);
                        }

                        cal.set(SECOND, 0);
                        cal.set(MILLISECOND, 0);

                        Comparable<?> date = createObjectInstance(param1, cal);

                        return date;

                    } else if (dateFormat instanceof SS) {

                        if (!isTimestamp(param1)) {
                            throw new WrongTypeException(getFunctionName(), param1, dateFormat);
                        }

                        int mSecs = cal.get(MILLISECOND);

                        if (mSecs >= 500) {
                            cal.add(SECOND, 1);
                        }

                        cal.set(MILLISECOND, 0);

                        Comparable<?> date = createObjectInstance(param1, cal);

                        return date;

                    } else if (dateFormat instanceof NNN) {

                        throw new WrongTypeException(getFunctionName(), param1, dateFormat);

                    }
                }
            }

        } catch (ParseException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalErrorException(getFunctionName(), e);
        }

        throw new WrongTypeException(getFunctionName(), param1, param2);
    }

    private boolean isTimeOrTimestamp(Comparable<?> param1) {

        if (param1 instanceof java.sql.Time) {
            return true;
        }

        if (param1 instanceof java.sql.Timestamp) {
            return true;
        }

        return false;
    }

    private boolean isDateOrTimestamp(Comparable<?> param1) {

        if (param1 instanceof java.sql.Time) {
            return false;
        }

        if (isTimestamp(param1)) {
            return true;
        }

        if (param1 instanceof java.sql.Date) {
            return true;
        }

        return false;
    }

    private boolean isTimestamp(Comparable<?> param1) {

        if (param1 instanceof java.sql.Timestamp) {
            return true;
        }

        return false;
    }

    private Comparable<?> createObjectInstance(Comparable<?> param1, Calendar cal) throws ClassNotFoundException, NoSuchMethodException,
        InstantiationException, IllegalAccessException, InvocationTargetException {

        if (param1 instanceof java.sql.Time) {
            cal.set(DAY_OF_MONTH, 1);
            cal.set(MONTH, 0);
            cal.set(YEAR, 1970);
        }

        Class<?> clazz = Class.forName(param1.getClass().getName());
        Constructor<?> objectConstructor = clazz.getConstructor(long.class);
        Comparable<?> object = (Comparable<?>)objectConstructor.newInstance(new Object[] { cal.getTimeInMillis() });

        return object;
    }
}
