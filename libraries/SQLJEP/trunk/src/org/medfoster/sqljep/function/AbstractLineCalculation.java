/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.function;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.medfoster.sqljep.ASTFunNode;
import org.medfoster.sqljep.JepRuntime;
import org.medfoster.sqljep.datatypes.Days;
import org.medfoster.sqljep.datatypes.Hours;
import org.medfoster.sqljep.datatypes.Microseconds;
import org.medfoster.sqljep.datatypes.Minutes;
import org.medfoster.sqljep.datatypes.Months;
import org.medfoster.sqljep.datatypes.Seconds;
import org.medfoster.sqljep.datatypes.Years;
import org.medfoster.sqljep.exceptions.ParseException;
import org.medfoster.sqljep.exceptions.WrongTypeException;

public abstract class AbstractLineCalculation extends PostfixCommand {

    private Calendar calendar = Calendar.getInstance();

    protected Integer sign = null;

    public final int getNumberOfParameters() {
        return 2;
    }

    /**
     * Calculates the result of applying the "+" operator to the arguments from
     * the stack and pushes it back on the stack.
     */
    public final void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
        node.childrenAccept(runtime.ev, null);
        Comparable<?> param2 = runtime.stack.pop();
        Comparable<?> param1 = runtime.stack.pop();
        runtime.stack.push(add(param1, param2));
    }

    public final Comparable<?> add(Comparable<?> param1, Comparable<?> param2) throws ParseException {

        if (param1 == null || param2 == null) {
            return null;
        }

        if (param1 instanceof String) {
            param1 = parse((String)param1);
        }

        if (param2 instanceof String) {
            param2 = parse((String)param2);
        }

        if (param1 instanceof Number && param2 instanceof Number) {
            // BigInteger type is not supported
            Number n1 = (Number)param1;
            Number n2 = (Number)param2;
            if (n1 instanceof BigDecimal || n2 instanceof BigDecimal) {
                BigDecimal b1 = getBigDecimal(n1);
                BigDecimal b2 = getBigDecimal(n2);
                return performOperation(b1, b2);
            }
            if (n1 instanceof Double || n2 instanceof Double || n1 instanceof Float || n2 instanceof Float) {
                return n1.doubleValue() + n2.doubleValue();
            } else { // Long, Integer, Short, Byte
                long l1 = n1.longValue();
                long l2 = n2.longValue();
                BigDecimal b1 = new BigDecimal(l1);
                BigDecimal b2 = new BigDecimal(l2);
                return performOperation(b1, b2);
            }
        } else if (param1 instanceof java.util.Date || param2 instanceof java.util.Date) {
            if (param1 instanceof java.util.Date && param2 instanceof java.util.Date) {
                throw new WrongTypeException(getFunctionName(), param1, param2);
            }

            if (param2 instanceof java.util.Date) {
                Comparable<?> param2Old = param2;
                param2 = param1;
                param1 = param2Old;
            }

            if (param1 instanceof java.sql.Timestamp) {

                if (param2 instanceof Years || param2 instanceof Months || param2 instanceof Days || param2 instanceof Hours
                    || param2 instanceof Minutes || param2 instanceof Seconds || param2 instanceof Microseconds) {
                    return new java.sql.Timestamp(performOperation(param1, param2).getTime());
                }

            } else if (param1 instanceof java.sql.Time) {

                if (param2 instanceof Hours || param2 instanceof Minutes || param2 instanceof Seconds) {
                    java.util.Date date = performOperation(param1, param2);
                    return new java.sql.Time(clearDatePortion(date).getTime());
                }

            }
            if (param1 instanceof java.sql.Date) {

                if (param2 instanceof Years || param2 instanceof Months || param2 instanceof Days || param2 instanceof Hours
                    || param2 instanceof Minutes || param2 instanceof Seconds) {
                    return new java.sql.Date(performOperation(param1, param2).getTime());
                }

            }
            if (param1 instanceof java.util.Date) {

                if (param2 instanceof Years || param2 instanceof Months || param2 instanceof Days || param2 instanceof Hours
                    || param2 instanceof Minutes || param2 instanceof Seconds) {
                    return new java.util.Date(performOperation(param1, param2).getTime());
                }

            }

            throw new WrongTypeException(getFunctionName(), param1, param2);
        } else {
            throw new WrongTypeException(getFunctionName(), param1, param2);
        }
    }

    protected abstract BigDecimal performOperation(BigDecimal param1, BigDecimal param2) throws ParseException;

    protected java.util.Date performOperation(Comparable<?> param1, Comparable<?> param2) throws ParseException {

        java.util.Date d = (java.util.Date)param1;
        
        if (param2 instanceof Days) {
            Days n = (Days)param2;
            return addDays(d, getOperand(n));
        } else if (param2 instanceof Months) {
            Months n = (Months)param2;
            return addMonths(d, getOperand(n));
        } else if (param2 instanceof Years) {
            Years n = (Years)param2;
            return addYears(d, getOperand(n));
        } else if (param2 instanceof Hours) {
            Hours n = (Hours)param2;
            return addHours(d, getOperand(n));
        } else if (param2 instanceof Minutes) {
            Minutes n = (Minutes)param2;
            return addMinutes(d, getOperand(n));
        } else if (param2 instanceof Seconds) {
            Seconds n = (Seconds)param2;
            return addSeconds(d, getOperand(n));
        } else if (param2 instanceof Microseconds) {
            Microseconds n = (Microseconds)param2;
            return addMicroseconds(d, getOperand(n));
        } else {
            throw new WrongTypeException(getFunctionName(), param1, param2);
        }
    }

    protected Date addDays(Date time, int days) {

        calendar.clear();
        calendar.setTime((Date)time);
        calendar.add(Calendar.DAY_OF_MONTH, days);

        return calendar.getTime();
    }

    protected Date addMonths(Date time, int months) {

        calendar.clear();
        calendar.setTime((Date)time);
        calendar.add(Calendar.MONTH, months);

        return calendar.getTime();
    }

    protected Date addYears(Date time, int years) {

        calendar.clear();
        calendar.setTime((Date)time);
        calendar.add(Calendar.YEAR, years);

        return calendar.getTime();
    }

    protected Date addHours(Date time, int hours) {

        calendar.clear();
        calendar.setTime((Date)time);
        calendar.add(Calendar.HOUR_OF_DAY, hours);

        return calendar.getTime();
    }

    protected Date addMinutes(Date time, int minutes) {

        calendar.clear();
        calendar.setTime((Date)time);
        calendar.add(Calendar.MINUTE, minutes);

        return calendar.getTime();
    }

    protected Date addSeconds(Date time, int seconds) {

        calendar.clear();
        calendar.setTime((Date)time);
        calendar.add(Calendar.SECOND, seconds);

        return calendar.getTime();
    }

    protected Date addMicroseconds(Date time, int microseconds) {

        calendar.clear();
        calendar.setTime((Date)time);
        calendar.add(Calendar.MILLISECOND, microseconds);

        return calendar.getTime();
    }

    private java.util.Date clearDatePortion(Date date) {

        calendar.clear();
        calendar.setTime(date);
        calendar.set(DAY_OF_MONTH, 1);
        calendar.set(MONTH, 0);
        calendar.set(YEAR, 1970);
        return calendar.getTime();
    }

    private int getOperand(Number number) {
        return number.intValue() * sign;
    }
}
