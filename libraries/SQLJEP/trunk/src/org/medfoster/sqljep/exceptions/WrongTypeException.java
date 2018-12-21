/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.exceptions;

import org.medfoster.sqljep.ParseException;

public class WrongTypeException extends ParseException {

    private static final long serialVersionUID = -147756657638842877L;

    private String function;
    private Comparable<?>[] comparables;

    public WrongTypeException(String function, Comparable<?>... comparables) {
        this.function = function;
        this.comparables = comparables;
    }

    @Override
    public String getMessage() {

        boolean isFirstComparable = true;

        StringBuilder buffer = new StringBuilder();
        buffer.append("Wrong parameter types: ");
        buffer.append(" ");
        buffer.append(function);
        buffer.append("(");
        for (Comparable<?> comparable : comparables) {
            if (isFirstComparable) {
                isFirstComparable = false;
            } else {
                buffer.append(", ");
            }
            buffer.append(comparable.getClass().getSimpleName());
        }
        buffer.append(")");

        return buffer.toString();
    }
}
