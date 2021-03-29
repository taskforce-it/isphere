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

    private QualifiedMemberName qualifiedMemberName;

    public JournaledFile(String connectionName, String libraryName, String fileName, String memberName) {
        super(connectionName, libraryName, fileName, ISeries.FILE);

        this.qualifiedMemberName = new QualifiedMemberName(connectionName, libraryName, fileName, memberName);
    }

    public String getConnectionName() {
        return qualifiedMemberName.getConnectionName();
    }

    public String getFileName() {
        return qualifiedMemberName.getFileName();
    }

    public String getLibraryName() {
        return qualifiedMemberName.getLibraryName();
    }

    public String getMember() {
        return qualifiedMemberName.getMemberName();
    }

    public Journal getJournal() {
        return super.getJournal();
    }

    public String getQualifiedName() {
        return qualifiedMemberName.getQualifiedName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((qualifiedMemberName == null) ? 0 : qualifiedMemberName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        JournaledFile other = (JournaledFile)obj;
        if (qualifiedMemberName == null) {
            if (other.qualifiedMemberName != null) return false;
        } else if (!qualifiedMemberName.equals(other.qualifiedMemberName)) return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getQualifiedName(), getObjectType());
    }
}
