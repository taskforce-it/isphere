/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.stringlisteditor;

public class ValidationEvent {

    public static final int ACTIVATE = 1;
    public static final int ADD = 2;
    public static final int CHANGE = 3;

    private int type;
    public String value;

    public ValidationEvent(int type) {
        this(type, null);
    }

    public ValidationEvent(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }
}
