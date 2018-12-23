/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.exceptions;

public class UnexpectedTypeException extends ParseException {

    private static final long serialVersionUID = -799012200779920147L;

    private String function;
    private String parameter;
    private String expected;

    public UnexpectedTypeException(String function, String parameter, String expected) {
        this.function = function;
        this.parameter = parameter;
        this.expected = expected;
    }

    @Override
    public String getMessage() {
        return String.format("Parameter '%s' of function %s should be an %s.", parameter, function, expected);
    }
}
