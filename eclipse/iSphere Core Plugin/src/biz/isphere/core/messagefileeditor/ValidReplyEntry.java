/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefileeditor;

public class ValidReplyEntry {

    private String value;
    
    private String comparableText;

    public ValidReplyEntry(String value) {
        this.value = value;
        this.comparableText = null;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        this.comparableText = null;
    }

    public String asComparableText() {
        
        if (comparableText == null) {
            comparableText = value;
        }
        
        return comparableText;
    }

    @Override
    public String toString() {
        return asComparableText();
    }
}
