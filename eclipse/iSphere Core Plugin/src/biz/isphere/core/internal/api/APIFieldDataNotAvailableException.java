/*******************************************************************************
 * Copyright (c) 2023-2023 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api;

public class APIFieldDataNotAvailableException extends RuntimeException {

    private static final long serialVersionUID = -3071207435207951760L;

    private String message;

    public APIFieldDataNotAvailableException(String name) {
        this.message = "Data of field " + name + " is not available."; //$NON-NLS-1$ //$NON-NLS-2$
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
