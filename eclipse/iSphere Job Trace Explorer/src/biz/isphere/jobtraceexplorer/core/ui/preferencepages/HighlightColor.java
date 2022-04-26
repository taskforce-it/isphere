/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.preferencepages;

public enum HighlightColor {
    ATTRIBUTES ("ATTRIBUTES"), //$NON-NLS-1$
    PROCEDURES ("PROCEDURES"), //$NON-NLS-1$
    HIDDEN_PROCEDURES ("HIDDEN_PROCEDURES"), //$NON-NLS-1$
    ;

    private String keyValue;

    private HighlightColor(String keyValue) {
        this.keyValue = keyValue;
    }

    public String key() {
        return keyValue;
    }
}
