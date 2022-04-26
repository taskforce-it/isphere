/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.exceptions;

import org.medfoster.sqljep.ParseException;

public class UnknownTimeFormatException extends ParseException {

    private static final long serialVersionUID = 3375949441308471372L;

    private String format;

    public UnknownTimeFormatException(String format) {
        this.format = format;
    }

    @Override
    public String getMessage() {
        return String.format("Unknown time format: %s", format);
    }
}
