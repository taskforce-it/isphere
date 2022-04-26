/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model;

import biz.isphere.journalexplorer.core.internals.QualifiedName;
import biz.isphere.journalexplorer.core.model.dao.JournalOutputType;

public class OutputFile {

    private String connectionName;
    private String libraryName;
    private String fileName;
    private String memberName;

    public OutputFile(String connectionName, String outFileLibrary, String outFileName) {
        this(connectionName, outFileLibrary, outFileName, "*FIRST");
    }

    public OutputFile(String connectionName, String outFileLibrary, String outFileName, String outMemberName) {
        this.connectionName = connectionName;
        this.libraryName = outFileLibrary;
        this.fileName = outFileName;
        this.memberName = outMemberName;
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
        return connectionName;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getQualifiedName() {
        return QualifiedName.getMemberName(libraryName, fileName, memberName);
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }
}
