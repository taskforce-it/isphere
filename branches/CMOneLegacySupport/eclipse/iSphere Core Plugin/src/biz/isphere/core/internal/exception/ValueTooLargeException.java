/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.exception;

import java.math.BigDecimal;

import biz.isphere.core.Messages;

public class ValueTooLargeException extends AbstractNumericValueException {

    private static final long serialVersionUID = -2037826678119588499L;

    private static final String text = "Value too large";

    private static final String localizedText = Messages.Value_too_large;

    public ValueTooLargeException() {
        super(text, localizedText);
    }

    public ValueTooLargeException(BigDecimal value) {
        super(text, localizedText, value);
    }

    public ValueTooLargeException(Throwable aCause) {
        super(text, localizedText, aCause);
    }

    public ValueTooLargeException(BigDecimal value, Throwable aCause) {
        super(text, localizedText, value, aCause);
    }
}
