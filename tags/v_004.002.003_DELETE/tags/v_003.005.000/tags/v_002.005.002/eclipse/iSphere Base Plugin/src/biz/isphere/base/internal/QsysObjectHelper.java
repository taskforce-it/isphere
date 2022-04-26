/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

public final class QsysObjectHelper {

    public static String getAPIObjectType(String objectType) {
        if (objectType.startsWith("*")) { //$NON-NLS-1$
            return objectType.substring(1);
        }
        return objectType;
    }

}
