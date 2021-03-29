/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.shared;

import biz.isphere.core.internal.ISeries;
import biz.isphere.journalexplorer.core.handlers.ISelectedFile;
import biz.isphere.journalexplorer.core.internals.QualifiedMemberName;

public class JournaledFile extends JournaledObject implements ISelectedFile {

    private QualifiedMemberName member;

    public JournaledFile(String connectionName, String libraryName, String fileName, String memberName) {
        super(connectionName, libraryName, fileName, ISeries.FILE);

        this.member = new QualifiedMemberName(connectionName, libraryName, fileName, memberName);
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

    public String getMember() {
        return member.getMemberName();
    }

    public Journal getJournal() {
        return super.getJournal();
    }

    public String getQualifiedName() {
        return member.getQualifiedName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((member == null) ? 0 : member.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        JournaledFile other = (JournaledFile)obj;
        if (member == null) {
            if (other.member != null) return false;
        } else if (!member.equals(other.member)) return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getQualifiedName(), getObjectType());
    }
}
