/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefileeditor;

public class SpecialReplyValueEntry {

    private String fromValue;
    private String toValue;

    private String comparableText;

    public SpecialReplyValueEntry(String fromValue, String toValue) {
        this.fromValue = fromValue;
        this.toValue = toValue;
        this.comparableText = null;
    }

    public String getFromValue() {
        return fromValue;
    }

    public void setFromValue(String fromValue) {
        this.fromValue = fromValue;
        this.comparableText = null;
    }

    public String getToValue() {
        return toValue;
    }

    public void setToValue(String toValue) {
        this.toValue = toValue;
        this.comparableText = null;
    }

    public String asComparableText() {

        if (comparableText == null) {
            comparableText = fromValue + " -> " + toValue;
        }

        return comparableText;
    }

    @Override
    public String toString() {
        return asComparableText();
    }
}
