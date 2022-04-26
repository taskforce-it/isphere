/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

public final class ExceptionHelper {

    public static String getLocalizedMessage(Throwable throwable) {

        String exceptionMessage = throwable.getLocalizedMessage();
        if (StringHelper.isNullOrEmpty(exceptionMessage)) {
            return throwable.getClass().getName();
        } else {
            return exceptionMessage.replaceAll("\\p{C}", "÷");
        }

    }
}
