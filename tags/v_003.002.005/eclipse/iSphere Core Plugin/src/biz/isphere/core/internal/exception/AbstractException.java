/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.exception;

public abstract class AbstractException extends Exception {

    private static final long serialVersionUID = 3635630665923510988L;

    private String text;
    private String localizedText;

    public AbstractException() {
        this(null, null, null);
    }

    public AbstractException(String text, String localizedText) {
        this(text, localizedText, null);
    }

    public AbstractException(String text, String localizedText, Throwable aCause) {
        super(text, aCause);
        this.text = text;
        this.localizedText = localizedText;
    }

    @Override
    public String getMessage() {
        return text;
    }

    @Override
    public String getLocalizedMessage() {
        return localizedText;
    }

    @Override
    public String toString() {
        return text;
    }

}
