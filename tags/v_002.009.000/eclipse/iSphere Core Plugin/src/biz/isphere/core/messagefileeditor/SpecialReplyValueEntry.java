/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefileeditor;

import java.io.Serializable;

public class SpecialReplyValueEntry implements Serializable {

    private static final long serialVersionUID = 1138196896984532140L;
    
    private String fromValue;
    private String toValue;

    public SpecialReplyValueEntry() {
        this.fromValue = "";
        this.toValue = "";
    }

    public String getFromValue() {
        return fromValue;
    }

    public void setFromValue(String fromValue) {
        this.fromValue = fromValue;
    }

    public String getToValue() {
        return toValue;
    }

    public void setToValue(String toValue) {
        this.toValue = toValue;
    }

    public String asComparableText() {

        return fromValue + " -> " + toValue;
    }

    @Override
    public String toString() {
        return asComparableText();
    }
}
