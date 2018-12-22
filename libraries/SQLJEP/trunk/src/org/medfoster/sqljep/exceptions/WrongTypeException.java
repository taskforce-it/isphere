/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.exceptions;

import org.medfoster.sqljep.function.AbstractOracleDateTimeFormat.DATE;

public class WrongTypeException extends ParseException {

    private static final long serialVersionUID = 3810975926896013610L;

    private String function;
    private Object[] comparables;

    public WrongTypeException(String function, Comparable<?>... comparables) {
        this.function = function;
        this.comparables = comparables;
    }

    public WrongTypeException(String function, Comparable<?> comparable, DATE dateTimeFormat) {
        this.function = function;
        this.comparables = new Object[] { comparable, dateTimeFormat };
    }

    @Override
    public String getMessage() {

        boolean isFirstComparable = true;

        StringBuilder buffer = new StringBuilder();
        buffer.append("Wrong parameter types: ");
        buffer.append(" ");
        buffer.append(function);
        buffer.append("(");
        for (Object item : comparables) {
            if (isFirstComparable) {
                isFirstComparable = false;
            } else {
                buffer.append(", ");
            }
            if (item instanceof DATE) {
                buffer.append(item.getClass().getSimpleName());
            } else {
                buffer.append(item.getClass().getSimpleName());
            }
        }
        buffer.append(")");

        return buffer.toString();
    }
}
