/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.exception;

import java.math.BigDecimal;

public abstract class AbstractNumericValueException extends AbstractException {

    private static final long serialVersionUID = -6631798611861235088L;

    private BigDecimal value;

    public AbstractNumericValueException() {
        this(null, null, null, null);
    }

    public AbstractNumericValueException(String text, String localizedText) {
        this(text, localizedText, null, null);
    }

    public AbstractNumericValueException(String text, String localizedText, BigDecimal value) {
        this(text, localizedText, value, null);
    }

    public AbstractNumericValueException(String text, String localizedText, Throwable aCause) {
        this(text, localizedText, null, aCause);
    }

    public AbstractNumericValueException(String text, String localizedText, BigDecimal value, Throwable aCause) {
        super(getText(text, value), getText(localizedText, value), aCause);
    }

    public BigDecimal getValue() {
        return value;
    }

    private static String getText(String text, BigDecimal value) {
        return text + " (" + value.toString() + ")";
    }
}
