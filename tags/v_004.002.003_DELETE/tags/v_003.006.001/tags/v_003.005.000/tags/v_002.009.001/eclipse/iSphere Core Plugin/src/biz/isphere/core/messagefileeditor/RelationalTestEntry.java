/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefileeditor;

import java.io.Serializable;

public class RelationalTestEntry implements Serializable {

    private static final long serialVersionUID = -2820593094351396999L;

    public static final String LT = "*LT";
    public static final String LE = "*LE";
    public static final String GT = "*GT";
    public static final String GE = "*GE";
    public static final String EQ = "*EQ";
    public static final String NE = "*NE";

    private String operator;
    private String value;

    public RelationalTestEntry() {
        this.operator = MessageDescription.VALUE_NONE;
        this.value = "";
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String toValue) {
        this.value = toValue;
    }

    public String asComparableText() {

        return operator + " " + value; //$NON-NLS-1$
    }

    @Override
    public String toString() {
        return asComparableText();
    }
}
