/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.objectselector.model;

import biz.isphere.core.Messages;
import biz.isphere.core.internal.ISeries;

public enum QSYSObjectTypes {
    LIB (ISeries.LIB, Messages.Library),
    MSGF (ISeries.MSGF, Messages.Message_file);

    private String type;
    private String label;

    private QSYSObjectTypes(String type, String label) {
        this.type = type;
        this.label = label;
    }

    public String type() {
        return type;
    }

    public String label() {
        return label;
    }
}
