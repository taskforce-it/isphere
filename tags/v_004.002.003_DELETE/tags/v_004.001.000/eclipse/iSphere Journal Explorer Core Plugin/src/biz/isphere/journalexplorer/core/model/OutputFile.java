/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model;

import biz.isphere.journalexplorer.core.internals.QualifiedMemberName;
import biz.isphere.journalexplorer.core.model.dao.JournalOutputType;

public class OutputFile {

    private QualifiedMemberName qualifiedFileName;

    public OutputFile(String connectionName, String libraryName, String fileName) {
        this(connectionName, libraryName, fileName, "*FIRST");
    }

    public OutputFile(String connectionName, String libraryName, String fileName, String memberName) {
        this.qualifiedFileName = new QualifiedMemberName(connectionName, libraryName, fileName, memberName);
    }

    /**
     * Returns the type of the output file. The type if one of the constants
     * declared in {@link JournalOutputType}.
     * 
     * @return type of the output file
     * @throws Exception
     */
    public JournalOutputType getType() throws Exception {
        MetaTable metaTable = MetaDataCache.getInstance().retrieveMetaData(this);
        return metaTable.getOutfileType();
    }

    public String getConnectionName() {
        return qualifiedFileName.getConnectionName();
    }

    public String getLibraryName() {
        return qualifiedFileName.getLibraryName();
    }

    public String getFileName() {
        return qualifiedFileName.getFileName();
    }

    public String getMemberName() {
        return qualifiedFileName.getMemberName();
    }

    public String getQualifiedName() {
        return qualifiedFileName.getQualifiedName();
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }
}
