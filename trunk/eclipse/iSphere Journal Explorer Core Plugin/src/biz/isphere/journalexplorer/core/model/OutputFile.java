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

    private QualifiedMemberName fileName;

    public OutputFile(String connectionName, String outFileLibrary, String outFileName) {
        this(connectionName, outFileLibrary, outFileName, "*FIRST");
    }

    public OutputFile(String connectionName, String outFileLibrary, String outFileName, String outMemberName) {
        this.fileName = new QualifiedMemberName(connectionName, outFileLibrary, outFileName, outMemberName);
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
        return fileName.getConnectionName();
    }

    public String getLibraryName() {
        return fileName.getLibraryName();
    }

    public String getFileName() {
        return fileName.getFileName();
    }

    public String getMemberName() {
        return fileName.getMemberName();
    }

    public String getQualifiedName() {
        return fileName.getQualifiedName();
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }
}
