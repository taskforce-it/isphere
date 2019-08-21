/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement;

public class InvalidXmlVersionNumberFormatException extends Exception {

    private static final long serialVersionUID = 9126453237954378789L;

    public InvalidXmlVersionNumberFormatException() {
        super("Invalid version number format"); //$NON-NLS-1$
    }
}
