/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.exception;

import biz.isphere.core.Messages;

public class ErrorLoggedException extends Exception {

    private static final long serialVersionUID = -4497514234146475576L;

    private static final String localizedText = Messages.Error_logged_exception;

    public ErrorLoggedException(String message) {
        super(message);
    }

    public ErrorLoggedException(String message, Throwable aCause) {
        super(message, aCause);
    }

    @Override
    public String getLocalizedMessage() {
        return localizedText;
    }

}
