/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model;

import biz.isphere.journalexplorer.core.internals.QualifiedPathName;

public class JsonFile {

    private QualifiedPathName qualifiedPath;

    public JsonFile(String connectionName, String path) {
        this.qualifiedPath = new QualifiedPathName(connectionName, path);
    }

    public String getConnectionName() {
        return qualifiedPath.getConnectionName();
    }

    public String getPath() {
        return qualifiedPath.getPath();
    }

    public String getQualifiedName() {
        return qualifiedPath.getQualifiedName();
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }

}
