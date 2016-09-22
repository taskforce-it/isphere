/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefileeditor;

import java.io.Serializable;

public class RangeOfReplyValues implements Serializable {

    private static final long serialVersionUID = 1744438963458077051L;
    
    private String lowerValue;
    private String upperValue;

    public RangeOfReplyValues() {
        this.lowerValue = MessageDescription.VALUE_NONE;
        this.upperValue = "";
    }

    public String getLowerValue() {
        return lowerValue;
    }

    public void setLowerValue(String lowerValue) {
        this.lowerValue = lowerValue;
    }

    public String getUpperValue() {
        return upperValue;
    }

    public void setUpperValue(String upperValue) {
        this.upperValue = upperValue;
    }

    public String asComparableText() {

        return lowerValue + " : " + upperValue; //$NON-NLS-1$
    }

    @Override
    public String toString() {
        return asComparableText();
    }
}
