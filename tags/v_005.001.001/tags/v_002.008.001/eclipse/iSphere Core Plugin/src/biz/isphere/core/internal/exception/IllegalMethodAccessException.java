/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.exception;

public class IllegalMethodAccessException extends RuntimeException {

    private static final long serialVersionUID = 5227131087541403482L;

    public IllegalMethodAccessException() {
        super();
    }

    public IllegalMethodAccessException(String aMessage) {
        super(aMessage);
    }

    public IllegalMethodAccessException(Throwable aCause) {
        super(aCause);
    }

    public IllegalMethodAccessException(String aMessage, Throwable aCause) {
        super(aMessage, aCause);
    }

}
