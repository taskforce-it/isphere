/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.exceptions;


public class WrongNumberOfParametersException extends ParseException {

    private static final long serialVersionUID = 4788921664877612893L;

    private int count;

    public WrongNumberOfParametersException(int count) {
        this.count = count;
    }

    @Override
    public String getMessage() {
        return String.format("Wrong number of parameters: %d", count);
    }
}
