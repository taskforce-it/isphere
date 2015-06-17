/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api;

public class APIFieldTypeMismatchException extends RuntimeException {

    private static final long serialVersionUID = 1232870384850444139L;

    private String message;

    public APIFieldTypeMismatchException(String name) {
        this.message = "Field " + name + "does not match expected field type."; //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }
}
