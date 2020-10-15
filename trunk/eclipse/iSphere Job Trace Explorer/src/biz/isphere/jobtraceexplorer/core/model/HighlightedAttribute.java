/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.model;

public class HighlightedAttribute {

    private transient int index;
    private transient String value;

    public HighlightedAttribute(int index, String value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(index);
        buffer.append("="); //$NON-NLS-1$
        buffer.append(value);

        return buffer.toString();
    }
}
