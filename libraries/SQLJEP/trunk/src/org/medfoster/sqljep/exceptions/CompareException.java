/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.exceptions;


public class CompareException extends ParseException {

    private static final long serialVersionUID = 1317668761754955133L;

    private String function;
    private Comparable<?> param1;
    private Comparable<?> param2;

    public CompareException(String function, Comparable<?> param1, Comparable<?> param2) {
        this.function = function;
        this.param1 = param1;
        this.param2 = param2;
    }

    @Override
    public String getMessage() {
        return String.format("Cannot compare '%s' with '%s' in function %s.", param1.getClass().getSimpleName(), param2.getClass().getSimpleName(), function);
    }
}
