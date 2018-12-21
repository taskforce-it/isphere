/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.exceptions;

import org.medfoster.sqljep.ParseException;

public class InternalErrorException extends ParseException {

    private static final long serialVersionUID = -6957634153643388017L;

    public InternalErrorException(String function, Throwable cause) {
        super(String.format("Internal error in function %s(): %s", function, cause.getLocalizedMessage()), cause);
    }
}
