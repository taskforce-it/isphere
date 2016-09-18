/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefileeditor;

import java.io.Serializable;

public class ValidReplyEntry implements Serializable {

    private static final long serialVersionUID = 4230668370576222173L;
    
    private String value;

    public ValidReplyEntry() {
        this.value = "";
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String asComparableText() {

        return value;
    }

    @Override
    public String toString() {
        return asComparableText();
    }
}
