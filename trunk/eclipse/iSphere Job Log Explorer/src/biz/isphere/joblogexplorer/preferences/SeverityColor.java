/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.preferences;

public enum SeverityColor {
    SEVERITY_BL ("SEVERITY_BL"), //$NON-NLS-1$
    SEVERITY_00 ("SEVERITY_00"), //$NON-NLS-1$
    SEVERITY_10 ("SEVERITY_10"), //$NON-NLS-1$
    SEVERITY_20 ("SEVERITY_20"), //$NON-NLS-1$
    SEVERITY_30 ("SEVERITY_30"), //$NON-NLS-1$
    SEVERITY_40 ("SEVERITY_40"); //$NON-NLS-1$
    ;

    private String keyValue;

    private SeverityColor(String keyValue) {
        this.keyValue = keyValue;
    }

    public String key() {
        return keyValue;
    }
}
