/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.internals;

import java.io.File;

import biz.isphere.base.internal.StringHelper;

public class QualifiedPathName extends AbstractComparable<QualifiedPathName> implements IQualifiedName {

    private String connectionName;
    private String file;

    public QualifiedPathName(String connectionName, File file) {
        init(connectionName, file.getPath());
    }

    public QualifiedPathName(String connectionName, String path) {
        init(connectionName, path);
    }

    public QualifiedPathName(String qualifiedPath) {

        if (StringHelper.isNullOrEmpty(qualifiedPath)) {
            throw new IllegalArgumentException("Invalid qualified path: " + qualifiedPath); //$NON-NLS-1$
        }

        String[] parts = qualifiedPath.split(CONNECTION_DELIMITER);
        if (parts.length == 1) {
            init(null, parts[0]);
        } else {
            init(parts[0], parts[1]);
        }
    }

    private void init(String connectionName, String file) {
        this.connectionName = connectionName == null ? null : connectionName.trim();
        this.file = file.trim();
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getPath() {
        return file;
    }

    public String getQualifiedName() {
        return connectionName + CONNECTION_DELIMITER + file;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((connectionName == null) ? 0 : connectionName.hashCode());
        result = prime * result + ((file == null) ? 0 : file.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        QualifiedPathName other = (QualifiedPathName)obj;
        if (connectionName == null) {
            if (other.connectionName != null) return false;
        } else if (!connectionName.equals(other.connectionName)) return false;
        if (file == null) {
            if (other.file != null) return false;
        } else if (!file.equals(other.file)) return false;
        return true;
    }

    public static QualifiedPathName parse(String string) {

        if (StringHelper.isNullOrEmpty(string)) {
            return null;
        }

        return new QualifiedPathName(string);
    }

    public static String getPath(String connectionName, String fileName) {
        return new QualifiedPathName(connectionName, fileName).getQualifiedName();
    }

    public int compareTo(QualifiedPathName other) {

        if (other == null) {
            return 1;
        } else {
            int result;
            result = compareToChecked(connectionName, other.connectionName);
            if (result != 0) {
                return result;
            } else {
                return compareToChecked(file, other.file);
            }
        }
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }
}
