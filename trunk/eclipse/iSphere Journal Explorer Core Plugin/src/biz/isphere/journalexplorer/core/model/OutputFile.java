/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
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
    private String outFileLibrary;
    private String outFileName;
    private String outMemberName;

    public OutputFile(String connectionName, String outFileLibrary, String outFileName) {
        this(connectionName, outFileLibrary, outFileName, "*FIRST");
    }

    public OutputFile(String connectionName, String outFileLibrary, String outFileName, String outMemberName) {
        this.connectionName = connectionName;
        this.outFileLibrary = outFileLibrary;
        this.outFileName = outFileName;
        this.outMemberName = outMemberName;
    }

    /**
     * Returns the type of the output file. The type if one of the constants
     * declared in {@link JournalOutputType}.
     * 
     * @return type of the output file
     * @throws Exception
     */
    public JournalOutputType getType() throws Exception {

        MetaTable metaTable = MetaDataCache.INSTANCE.retrieveMetaData(this);
        metaTable.setJournalOutputFile(true);

        return metaTable.getOutfileType();
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnetionName(String connetionName) {
        this.connectionName = connetionName;
    }

    public String getOutFileLibrary() {
        return outFileLibrary;
    }

    public void setOutFileLibrary(String outFileLibrary) {
        this.outFileLibrary = outFileLibrary;
    }

    public String getOutFileName() {
        return outFileName;
    }

    public void setOutFileName(String outFileName) {
        this.outFileName = outFileName;
    }

    public String getOutMemberName() {
        return outMemberName;
    }

    public void setOutMemberName(String outMemberName) {
        this.outMemberName = outMemberName;
    }

    public String getQualifiedName() {
        return QualifiedName.getMemberName(connectionName, outFileLibrary, outFileName, outMemberName);
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }
}
