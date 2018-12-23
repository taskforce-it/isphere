/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.exceptions;

import org.medfoster.sqljep.ParseException;

public class UnknownDateFormatException extends ParseException {

    private static final long serialVersionUID = -2546236102672479452L;

    private String format;

    public UnknownDateFormatException(String format) {
        this.format = format;
    }

    @Override
    public String getMessage() {
        return String.format("Unknown date format: %s", format);
    }
}
