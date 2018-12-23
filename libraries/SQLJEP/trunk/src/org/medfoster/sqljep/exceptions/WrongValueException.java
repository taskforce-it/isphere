/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.exceptions;

import org.medfoster.sqljep.ParseException;

public class WrongValueException extends ParseException {

    private static final long serialVersionUID = -6742576991351297473L;

    private String function;
    private String parameter;
    private String value;

    public WrongValueException(String function, String parameter, int value) {
        this.function = function;
        this.parameter = parameter;
        this.value = Integer.toString(value);
    }

    @Override
    public String getMessage() {
        return String.format("Invalid value '%s' of parameter '%s' of function %s.", value, parameter, function);
    }
}
