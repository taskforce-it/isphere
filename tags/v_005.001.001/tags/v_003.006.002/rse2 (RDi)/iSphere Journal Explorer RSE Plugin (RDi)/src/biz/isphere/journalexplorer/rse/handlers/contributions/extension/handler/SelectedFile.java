/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.rse.handlers.contributions.extension.handler;

import biz.isphere.journalexplorer.rse.handlers.contributions.extension.point.ISelectedFile;

public class SelectedFile implements ISelectedFile {

    private String connectionName;
    private String libraryName;
    private String fileName;
    private String memberName;

    public SelectedFile(String connectionName, String outFileLibrary, String outFileName) {
        this(connectionName, outFileLibrary, outFileName, "*FIRST");
    }

    public SelectedFile(String connectionName, String libraryName, String fileName, String memberName) {
        this.connectionName = connectionName;
        this.libraryName = libraryName;
        this.fileName = fileName;
        this.memberName = memberName;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnetionName(String connetionName) {
        this.connectionName = connetionName;
    }

    public String getLibrary() {
        return libraryName;
    }

    public void setLibrary(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getName() {
        return fileName;
    }

    public void setName(String fileName) {
        this.fileName = fileName;
    }

    public String getMember() {
        return memberName;
    }

    public void setMember(String memberName) {
        this.memberName = memberName;
    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(getLibrary());
        buffer.append("/"); //$NON-NLS-1$
        buffer.append(getName());
        buffer.append("("); //$NON-NLS-1$
        buffer.append(getMember());
        buffer.append(")"); //$NON-NLS-1$

        return buffer.toString();
    }
}
