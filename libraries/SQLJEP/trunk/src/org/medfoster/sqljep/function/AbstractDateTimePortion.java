/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.function;

import java.util.Calendar;

import org.medfoster.sqljep.ASTFunNode;
import org.medfoster.sqljep.BaseJEP;
import org.medfoster.sqljep.JepRuntime;
import org.medfoster.sqljep.exceptions.ParseException;
import org.medfoster.sqljep.exceptions.WrongTypeException;

public abstract class AbstractDateTimePortion<M extends Comparable<?>> extends PostfixCommand {

    private int calendarField;

    public AbstractDateTimePortion(int calendarField) {
        this.calendarField = calendarField;
    }

    final public int getNumberOfParameters() {
        return 1;
    }

    public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
        node.childrenAccept(runtime.ev, null);
        Comparable<?> param = runtime.stack.pop();
        runtime.stack.push(evaluate(param, runtime.calendar));
    }

    protected abstract M createInstance(int value);

    protected abstract boolean isSupportedType(Object object);

    protected abstract Comparable<?> parseObject(Comparable<?> param) throws java.text.ParseException, ParseException;

    private M evaluate(Comparable<?> param, Calendar cal) throws ParseException {

        try {

            if (param == null) {
                return null;
            }

            if (param instanceof String) {
                try {
                    return createInstance((Integer)parse((String)param));
                } catch (ParseException e) {
                    // eat exception
                }
            }

            if (param instanceof String) {
                param = parseObject(param);
            }

            if (param instanceof Long) {
                return createInstance(((Long)param).intValue());
            }

            if (isSupportedType(param)) {
                // java.sql.Date or java.sql.Time or java.sql.Timestamp
                java.util.Date ts = (java.util.Date)param;
                cal.setTimeInMillis(ts.getTime());
                return createInstance(cal.get(calendarField));
            }

        } catch (java.text.ParseException e) {
            if (BaseJEP.debug) {
                e.printStackTrace();
            }
            throw new ParseException(e.getMessage());
        }

        throw new WrongTypeException(getFunctionName(), param);
    }
}
