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

public class JournaledFile extends JournaledObject implements Comparable<JournaledFile> {

    private String memberName;
    private String qualifiedName;

    public JournaledFile(String connectionName, String libraryName, String fileName, String memberName) {
        super(connectionName, new QSYSObjectPathName(libraryName, fileName, getObjectType(ISeries.FILE)));

        this.memberName = memberName;
        this.qualifiedName = null;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getQualifiedName() {

        if (qualifiedName == null) {

            StringBuilder buffer = new StringBuilder();

            buffer.append(super.getQualifiedName());
            buffer.append(" (");
            buffer.append(memberName);
            buffer.append(")");

            qualifiedName = buffer.toString();
        }

        return qualifiedName;
    }

    public int compareTo(JournaledFile other) {

        if (other == null) {
            return -1;
        }

        int result = compareToChecked(getConnectionName(), other.getConnectionName());
        if (result != 0) {
            return result;
        } else {
            result = compareToChecked(getLibraryName(), other.getLibraryName());
            if (result != 0) {
                return result;
            } else {
                return getObjectName().compareTo(other.getObjectName());
            }
        }
    }

    private int compareToChecked(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return 0;
        } else {
            if (o1 == null && o2 != null) {
                return -1;
            } else if (o1 != null && o2 == null) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
