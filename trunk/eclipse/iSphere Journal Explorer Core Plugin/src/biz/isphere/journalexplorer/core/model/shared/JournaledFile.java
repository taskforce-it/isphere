/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.shared;

import com.ibm.as400.access.ObjectDescription;

public class JournaledFile extends JournaledObject {

    private String memberName;

    public JournaledFile(String connectionName, ObjectDescription objectDescription, String memberName) {
        super(connectionName, objectDescription);

        this.memberName = memberName;
    }

    public String getMemberName() {
        return memberName;
    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(super.toString());
        buffer.append(" ("); //$NON-NLS-1$
        buffer.append(memberName);
        buffer.append(")"); //$NON-NLS-1$

        return buffer.toString();
    }
}
