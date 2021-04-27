/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy;

import biz.isphere.core.Messages;

public enum Columns {
    FROM_LIBRARY ("fromLibrary", 120, Messages.Library),
    FROM_FILE ("fromFile", 120, Messages.File),
    FROM_MEMBER ("fromMember", 120, Messages.Member),
    TO_MEMBER ("toMember", 120, Messages.To_member_colhdg),
    ERROR_MESSAGE ("errorMessage", 400, Messages.Result);

    public String name;
    public int width;
    public String label;

    private Columns(String name, int width, String label) {
        this.name = name;
        this.width = width;
        this.label = label;
    }
}
