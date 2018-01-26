/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.internals;

public class QualifiedName {

    public static String getName(String connectionName, String libraryName, String objectName) {

        StringBuilder buffer = new StringBuilder();

        buffer.append(connectionName.trim());
        buffer.append(": "); //$NON-NLS-1$
        buffer.append(getName(libraryName, objectName));

        return buffer.toString();
    }

    public static String getName(String libraryName, String objectName) {

        StringBuilder buffer = new StringBuilder();

        buffer.append(libraryName.trim());
        buffer.append("/"); //$NON-NLS-1$
        buffer.append(objectName.trim());

        return buffer.toString();
    }

    public static String getMemberName(String connectionName, String libraryName, String objectName, String memberName) {

        StringBuilder buffer = new StringBuilder();

        buffer.append(connectionName.trim());
        buffer.append(": "); //$NON-NLS-1$
        buffer.append(getMemberName(libraryName, objectName, memberName));

        return buffer.toString();
    }

    public static String getMemberName(String libraryName, String objectName, String memberName) {

        StringBuilder buffer = new StringBuilder();

        buffer.append(getName(libraryName, objectName));
        buffer.append(" ("); //$NON-NLS-1$
        buffer.append(memberName);
        buffer.append(")"); //$NON-NLS-1$

        return buffer.toString();
    }
}
