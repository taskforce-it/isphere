/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.shared;

import biz.isphere.core.internal.ISeries;
import biz.isphere.journalexplorer.core.internals.QualifiedMemberName;

public class JournaledFile extends JournaledObject implements Comparable<JournaledFile> {

    private QualifiedMemberName member;

    public JournaledFile(String connectionName, String libraryName, String fileName, String memberName) {
        super(connectionName, libraryName, fileName, ISeries.FILE);

        this.member = new QualifiedMemberName(connectionName, libraryName, fileName);
    }

    public String getConnectionName() {
        return member.getConnectionName();
    }

    public String getFileName() {
        return member.getFileName();
    }

    public String getLibraryName() {
        return member.getLibraryName();
    }

    public String getMemberName() {
        return member.getMemberName();
    }

    public Journal getJournal() {
        return super.getJournal();
    }

    public String getQualifiedName() {
        return member.getQualifiedName();
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
                result = getFileName().compareTo(other.getFileName());
                if (result != 0) {
                    return result;
                } else {
                    return getMemberName().compareTo(other.getMemberName());
                }
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
