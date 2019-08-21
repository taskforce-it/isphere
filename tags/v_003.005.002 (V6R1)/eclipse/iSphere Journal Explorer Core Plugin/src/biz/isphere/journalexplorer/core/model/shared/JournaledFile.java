/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.shared;

import biz.isphere.core.internal.ISeries;

import com.ibm.as400.access.QSYSObjectPathName;

public class JournaledFile extends JournaledObject {

    private String memberName;

    public JournaledFile(String connectionName, String libraryName, String fileName, String memberName) {
        super(connectionName, new QSYSObjectPathName(libraryName, fileName, getObjectType(ISeries.FILE)));

        this.memberName = memberName;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getQualifiedName() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(super.getQualifiedName());
        buffer.append(" (");
        buffer.append(memberName);
        buffer.append(")");

        return buffer.toString();
    }
}
